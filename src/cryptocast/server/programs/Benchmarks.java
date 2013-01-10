package cryptocast.server.programs;

import java.math.BigInteger;
import java.util.Map;
import java.util.Random;

import javax.crypto.KeyGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.*;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import cryptocast.crypto.*;
import cryptocast.crypto.naorpinkas.*;
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
        public int f = 10;
    }
    
    @Parameters(commandDescription = "Calculate the lagrange coefficients")
    private static class LagrangeBenchmark implements Benchmark {
        private static final Logger log = LoggerFactory
                .getLogger(Benchmarks.LagrangeBenchmark.class);
        
        @Parameter(names = { "-t" }, description = "Degree of the polynomial")
        private int t = 100;
        @Parameter(names = { "-b" }, description = "The bit size of the modulus")
        private int b = 160;
        @Parameter(names = { "-x" }, description = "The bit size of the modulus")
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
    
    @Parameters(commandDescription = "Encrypt a 128-bit key using Naor-Pinkas")
    private static class EncryptBenchmark implements Benchmark {
        private static final Logger log = LoggerFactory
                .getLogger(Benchmarks.EncryptBenchmark.class);
        
        @Parameter(names = { "-t" }, description = "Number of revocable users")
        private int t = 100;
        
        NaorPinkasServer server;
        byte[] plain;
        
        public void beforeAll() throws Exception {
            SchnorrGroup schnorr = SchnorrGroup.getP1024Q160();
            log.info("Naor-Pinkas options: t={} qbits={} pbits={}", 
                        t, schnorr.getQ().bitLength(), schnorr.getP().bitLength());
            log.info("Generating server instance and setting up dummy keys");
            server = NaorPinkasServer.generate(t, schnorr);
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(128);
            plain = gen.generateKey().getEncoded();
            // do an initial encryption to set up the dummy keys
            server.encrypt(plain);
        }
        
        public void before() {}
        
        public void run() {
            byte[] cipher = server.encrypt(plain);
            log.debug("128-bit key was encrypted to {} bytes of Naor-Pinkas ciphertext", cipher.length);
        }
    }
    
    @Parameters(commandDescription = "Encrypt a 128-bit key using Naor-Pinkas multiple times, with revocations in between")
    private static class MultiEncryptBenchmark implements Benchmark {
        private static final Logger log = LoggerFactory
                .getLogger(Benchmarks.MultiEncryptBenchmark.class);
        
        @Parameter(names = { "-t" }, description = "Number of revocable users")
        private int t = 100;
        
        SchnorrGroup schnorr;
        NaorPinkasServer server;
        byte[] plain;
        
        public void beforeAll() throws Exception {
            schnorr = SchnorrGroup.getP1024Q160();
            log.info("Naor-Pinkas options: t={} qbits={} pbits={}", 
                        t, schnorr.getQ().bitLength(), schnorr.getP().bitLength());
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(128);
            plain = gen.generateKey().getEncoded();
        }
        
        public void before() {
            server = NaorPinkasServer.generate(t, schnorr);
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
    private static class DecryptBenchmark implements Benchmark {
        private static final Logger log = LoggerFactory
                .getLogger(Benchmarks.DecryptBenchmark.class);
        
        @Parameter(names = { "-t" }, description = "Number of revocable users")
        private int t = 100;
        
        NaorPinkasClient client;
        byte[] cipher;
        
        public void beforeAll() throws Exception {
            SchnorrGroup schnorr = SchnorrGroup.getP1024Q160();
            log.info("Naor-Pinkas options: t={} qbits={} pbits={}", 
                    t, schnorr.getQ().bitLength(), schnorr.getP().bitLength());
            log.info("Generating server and client and doing the initial encryption");
            NaorPinkasServer server = NaorPinkasServer.generate(t, schnorr);
            client = new NaorPinkasClient(server.getPersonalKey(server.getIdentity(0)).get());
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(128);
            byte[] plain = gen.generateKey().getEncoded();
            cipher = server.encrypt(plain);
        }
        
        public void before() {}
        
        public void run() {
            try {
                client.decrypt(cipher);
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
        @Parameter(names = { "-n" }, description = "The number of evaluation points (will be rounded to the next power of 2)")
        private int n = 1024;
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
    
    static Map<String, Benchmark> commands = ImmutableMap.of(
              "lagrange", new LagrangeBenchmark(),
              "encrypt", new EncryptBenchmark(),
              "multi-encrypt", new MultiEncryptBenchmark(),
              "decrypt", new DecryptBenchmark(),
              "multi-eval", new MultiEvalBenchmark()
              );

    private static void printCommands() {
        System.err.println("Available benchmarks: " + Joiner.on(", ").join(commands.keySet()));
        return;
    }
    
    /**
     * @param args command line arguments
     */
    public static void main(String[] argv) throws Exception {
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