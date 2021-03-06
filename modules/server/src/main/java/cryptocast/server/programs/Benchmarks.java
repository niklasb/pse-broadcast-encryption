package cryptocast.server.programs;

import java.math.BigInteger;
import java.util.Map;
import java.util.Random;

import javax.crypto.KeyGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

import com.beust.jcommander.*;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import cryptocast.crypto.*;
import cryptocast.crypto.naorpinkas.*;
import cryptocast.server.LogbackUtils;
import cryptocast.server.OptParse;

class Result {
    private double max = Double.MIN_VALUE, min = Double.MAX_VALUE;
    private double total;
    private int n;
    private int freq;

    public Result(int freq) {
        this.freq = freq;
    }
    public double getMax() { return max; }
    public double getMin() { return min; }
    public double getAvg() { return total / n; }

    void report(double t) {
        max = Math.max(max, t);
        min = Math.min(min, t);
        total += t;
        n++;
        if (n % freq == 0) {
            print();
        }
    }

    public void print() {
        System.out.printf("%5d  min %.3f  max %.3f  avg %.3f\n",
                n, getMin(), getMax(), getAvg());
    }
}

interface Benchmark extends Runnable {
    public void beforeAll() throws Exception;
    public void before() throws Exception;
}

/**
 * The main method to start the server.
 */
public final class Benchmarks {
    private static final Logger log = LoggerFactory.getLogger(Benchmarks.class);

    private static class OptsCommon extends OptParse.WithHelp {
        @Parameter(names = { "-n" }, description = "Number of repetitions")
        public int n = 100;
        @Parameter(names = { "-f" }, description = "Report frequency")
        public int f = 1;
    }

    private static class WithECOrSchnorr {
        private static final Logger log = LoggerFactory
                .getLogger(Benchmarks.WithECOrSchnorr.class);

        @Parameter(names = { "-g" }, description = "The group (ec or schnorr)")
        protected String g = "schnorr";

        protected NPServerFactory serverFactory;
        protected NPServerInterface server;

        protected SchnorrGroup schnorrGroup;
        protected EllipticCurveGroup<BigInteger, EllipticCurveOverFp.Point, EllipticCurveOverFp>
              ecGroup;

        protected void beforeAll() throws Exception {
            if (isEc()) {
                serverFactory = new ECNPServerFactory();
                ecGroup = EllipticCurveGroup.getSecp160R1();
                log.info("Using an elliptic curve over GF(p) with p 160 bits");
            } else if (isSchnorr()) {
                serverFactory = new SchnorrNPServerFactory();
                schnorrGroup = SchnorrGroup.getP1024Q160();
                log.info("Using a subgroup of GF(p) of size q with p 1024 and q 160 bits");
            } else {
                throw new Exception("Invalid group: `" + g + "'");
            }
        }

        protected void createServer(int t) {
            server = serverFactory.construct(t);
        }

        protected boolean isEc() {
            return g.equals("ec");
        }

        protected boolean isSchnorr() {
            return g.equals("schnorr");
        }
    }

    @Parameters(commandDescription = "Calculate the lagrange coefficients")
    private static class LagrangeBenchmark implements Benchmark {
        private static final Logger log = LoggerFactory
                .getLogger(Benchmarks.LagrangeBenchmark.class);

        @Parameter(names = { "-t" }, description = "Degree of the polynomial")
        private int t = 100;
        @Parameter(names = { "-b" }, description = "The bit size of the modulus")
        private int b = 160;
        @Parameter(names = { "-x" }, description = "The number of threads")
        private int numThreads = 2;

        IntegersModuloPrime field;
        ImmutableList<BigInteger> xs;

        public void beforeAll() throws Exception {
            BigInteger p = makePrime(b);
            field =  new IntegersModuloPrime(p);
            log.info("Lagrange options: t={} pbits={} numThreads={}", t, p.bitLength(), numThreads);
            Random rnd = new Random();
            ImmutableList.Builder<BigInteger> builder = ImmutableList.builder();
            for (int i = 0; i < t; i++) {
                builder.add(field.randomElement(rnd));
            }
            xs = builder.build();
        }

        public void before() {}

        public void run() {
            LagrangeInterpolation.computeCoefficients(field, xs, numThreads);
        }
    }

    @Parameters(commandDescription = "Calculate the product of powers in a cyclic group")
    private static class MultiExpBenchmark extends WithECOrSchnorr implements Benchmark {
        private static final Logger log = LoggerFactory
                .getLogger(Benchmarks.MultiExpBenchmark.class);

        @Parameter(names = { "-t" }, description = "The number of powers")
        private int t = 100;

        @Parameter(names = { "-k" }, description = "The chunk size to use for Shamir's trick")
        private int k = 5;

        ImmutableList<BigInteger> basesSchnorr;
        ImmutableList<EllipticCurveOverFp.Point> basesEc;
        ImmutableList<BigInteger> exps;

        public void beforeAll() throws Exception {
            super.beforeAll();
            Random rnd = new Random();
            IntegersModuloPrime modQ;
            if (isEc()) {
                ImmutableList.Builder<EllipticCurveOverFp.Point> builder =
                             ImmutableList.builder();
                for (int i = 0; i < t; i++) {
                    builder.add(ecGroup.getGenerator());
                }
                basesEc = builder.build();
                modQ = ecGroup.getFieldModOrder();
            } else { // schnorr
                ImmutableList.Builder<BigInteger> builder = ImmutableList.builder();
                for (int i = 0; i < t; i++) {
                    builder.add(schnorrGroup.getFieldModP().randomElement(rnd));
                }
                basesSchnorr = builder.build();
                modQ = schnorrGroup.getFieldModOrder();
            }
            ImmutableList.Builder<BigInteger> cbuilder = ImmutableList.builder();
            for (int i = 0; i < t; i++) {
                cbuilder.add(modQ.randomElement(rnd));
            }
            exps = cbuilder.build();
        }

        public void before() {}

        public void run() {
            if (g.equals("ec")) {
                ecGroup.multiexpShamir(basesEc, exps, k);
            } else {
                schnorrGroup.multiexpShamir(basesSchnorr, exps, k);
            }
        }
    }

    @Parameters(commandDescription = "Encrypt a 128-bit key using Naor-Pinkas")
    private static class EncryptBenchmark extends WithECOrSchnorr implements Benchmark {
        private static final Logger log = LoggerFactory
                .getLogger(Benchmarks.EncryptBenchmark.class);

        @Parameter(names = { "-t" }, description = "Number of revocable users")
        private int t = 100;

        byte[] plain;

        public void beforeAll() throws Exception {
            super.beforeAll();
            long start;
            log.info("Naor-Pinkas options: t={}", t);
            log.info("Generating server instance");
            start = System.currentTimeMillis();
            createServer(t);
            log.info("Took {} ms", System.currentTimeMillis() - start);
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(128);
            plain = gen.generateKey().getEncoded();
            log.info("Doing an initial encryption");
            start = System.currentTimeMillis();
            server.encrypt(plain);
            log.info("Took {} ms", System.currentTimeMillis() - start);
        }

        public void before() {}

        public void run() {
            byte[] cipher = server.encrypt(plain);
            log.debug("128-bit key was encrypted to {} bytes of ciphertext", cipher.length);
        }
    }

    @Parameters(commandDescription = "Encrypt a 128-bit key using Naor-Pinkas multiple times, with revocations in between")
    private static class MultiEncryptBenchmark extends WithECOrSchnorr implements Benchmark {
        private static final Logger log = LoggerFactory
                .getLogger(Benchmarks.MultiEncryptBenchmark.class);

        @Parameter(names = { "-t" }, description = "Number of revocable users")
        private int t = 100;

        byte[] plain;

        public void beforeAll() throws Exception {
            super.beforeAll();
            log.info("Naor-Pinkas options: t={}", t);
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(128);
            plain = gen.generateKey().getEncoded();
        }

        public void before() {
            log.info("Generating server...");
            long t1 = System.currentTimeMillis();
            createServer(t);
            log.info("Done! Took {} ms", System.currentTimeMillis() - t1);
        }

        public void run() {
            try {
                server.encrypt(plain);
                for (int i = 1; i <= 100; ++i) {
                    server.revoke(server.getIdentity(i));
                }
                server.encrypt(plain);
                for (int i = 30; i <= 80; ++i) {
                    server.unrevoke(server.getIdentity(i));
                }
                server.encrypt(plain);
            } catch (Exception e) {
                log.error("Error while encrypting", e);
            }
        }
    }

    @Parameters(commandDescription = "Decrypt a 128-bit key using Naor-Pinkas")
    private static class DecryptBenchmark extends WithECOrSchnorr implements Benchmark {
        private static final Logger log = LoggerFactory
                .getLogger(Benchmarks.DecryptBenchmark.class);

        @Parameter(names = { "-t" }, description = "Number of revocable users")
        private int t = 100;

        ECNPClient ecClient;
        SchnorrNPClient schnorrClient;
        byte[] cipher;

        public void beforeAll() throws Exception {
            super.beforeAll();
            log.info("Naor-Pinkas options: t={}", t);
            log.info("Generating server and client and doing the initial encryption");
            createServer(t);
            if (isSchnorr()) {
                schnorrClient = new SchnorrNPClient(
                        ((SchnorrNPServer) server).getPersonalKey(server.getIdentity(0)).get());
            } else {
                ecClient = new ECNPClient(
                        ((ECNPServer) server).getPersonalKey(server.getIdentity(0)).get());
            }
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(128);
            byte[] plain = gen.generateKey().getEncoded();
            cipher = server.encrypt(plain);
        }

        public void before() {}

        public void run() {
            try {
                if (isSchnorr()) {
                    schnorrClient.decrypt(cipher);
                } else {
                    ecClient.decrypt(cipher);
                }
            } catch (Exception e) {
                log.error("Error while decrypting", e);
            }
        }
    }

    @Parameters(commandDescription = "Evaluate a polynomial")
    private static class MultiEvalBenchmark implements Benchmark {
        private static final Logger log = LoggerFactory
                .getLogger(Benchmarks.MultiEvalBenchmark.class);

        @Parameter(names = { "-t" }, description = "Degree of the polynomial")
        private int t = 100;
        @Parameter(names = { "-b" }, description = "The bit size of the modulus")
        private int b = 160;
        @Parameter(names = { "-n" }, description = "The number of evaluation points")
        private int n = 1000;
        @Parameter(names = { "-x" }, description = "The number of threads")
        private int numThreads = 4;
        @Parameter(names = { "-c" }, description = "The number of points to evaluate at once")
        private int chunkSize = 1024;

        IntegersModuloPrime field;
        ImmutableList<BigInteger> xs;
        Polynomial<BigInteger> poly;

        public void beforeAll() throws Exception {
            BigInteger p = makePrime(b);
            field =  new IntegersModuloPrime(p);
            log.info("Multi-eval options: t={} b={} n={} numThreads={}, chunkSize={}",
                    t, p.bitLength(), n, numThreads, chunkSize);
            Random rnd = new Random();
            ImmutableList.Builder<BigInteger> builder = ImmutableList.builder();
            for (int i = 0; i < n; i++) {
                builder.add(field.randomElement(rnd));
            }
            xs = builder.build();
            poly = Polynomial.createRandomPolynomial(rnd, field, t);
        }

        public void before() {}

        public void run() {
            PolynomialMultiEvaluation eval = new PolynomialMultiEvaluation(xs, numThreads, chunkSize);
            eval.evaluate(poly);
        }
    }

    @Parameters(commandDescription = "Generate client keys")
    private static class KeygenBenchmark extends WithECOrSchnorr implements Benchmark {
        private static final Logger log = LoggerFactory
                .getLogger(Benchmarks.KeygenBenchmark.class);

        @Parameter(names = { "-t" }, description = "Degree of the polynomial")
        private int t = 100;
        @Parameter(names = { "-n" }, description = "The number of users")
        private int n = 1000;

        NPServerInterface server;

        public void beforeAll() throws Exception {
            super.beforeAll();
        }

        public void before() throws Exception {
            long start;
            log.info("Naor-Pinkas options: t={}", t);
            log.info("Generating server instance");
            start = System.currentTimeMillis();
            createServer(t);
            log.info("Took {} ms", System.currentTimeMillis() - start);
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(128);
            log.info("Doing an initial encryption");
            start = System.currentTimeMillis();
            server.encrypt(new byte[] { 0x1 });
            log.info("Took {} ms", System.currentTimeMillis() - start);
        }

        public void run() {
            for (int i = 0; i < n; ++i) {
                server.getPersonalKey(server.getIdentity(i));
            }
        }
    }

    private static Map<String, Benchmark> commands = ImmutableMap.<String, Benchmark>builder()
            .put("lagrange", new LagrangeBenchmark())
            .put("encrypt", new EncryptBenchmark())
            .put("multi-encrypt", new MultiEncryptBenchmark())
            .put("decrypt", new DecryptBenchmark())
            .put("multi-eval", new MultiEvalBenchmark())
            .put("keygen", new KeygenBenchmark())
            .put("multi-exp", new MultiExpBenchmark())
            .build();

    private static void printCommands() {
        System.err.println("Available benchmarks: " + Joiner.on(", ").join(commands.keySet()));
        return;
    }

    /**
     * @param args command line arguments
     */
    public static void main(String[] argv) throws Exception {
        LogbackUtils.removeAllAppenders();
        LogbackUtils.addStderrLogger(Level.DEBUG);

        OptsCommon opts = new OptsCommon();
        JCommander jc = new JCommander(opts);
        for (Map.Entry<String, Benchmark> entry : commands.entrySet()) {
            jc.addCommand(entry.getKey(), entry.getValue());
        }
        try {
            jc.parse(argv);
        } catch (MissingCommandException e) {
            printCommands();
            return;
        }
        Benchmark benchmark = commands.get(jc.getParsedCommand());
        if (benchmark == null) {
            printCommands();
            return;
        }
        log.info("Running with n={}  f={}", opts.n, opts.f);
        long start = System.currentTimeMillis();
        benchmark.beforeAll();
        log.info("beforeAll took {} ms, starting tests!", System.currentTimeMillis() - start);
        runBenchmark(benchmark, opts.n, opts.f);
    }

    private static void runBenchmark(Benchmark benchmark, int rep, int freq) throws Exception {
        Result res = measure(benchmark, rep, freq);
        if (rep % freq != 0) {
            res.print();
        }
    }

    private static Result measure(Benchmark benchmark, int rep, int freq) throws Exception {
        Result res = new Result(freq);
        for (int i = 0; i < rep; ++i) {
            benchmark.before();
            long start = System.currentTimeMillis();
            benchmark.run();
            res.report((System.currentTimeMillis() - start) / 1000.0);
        }
        return res;
    }

    private static BigInteger makePrime(int b) {
        return BigInteger.valueOf(2).pow(b).nextProbablePrime();
    }
}
