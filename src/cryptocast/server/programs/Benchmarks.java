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
    public void prepare() throws Exception;
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
        @Parameter(names = { "-t" }, description = "Degree of the polynomial")
        private int t = 100;
        @Parameter(names = { "-b" }, description = "The bit size of the modulus")
        private int b = 160;
        
        IntegersModuloPrime field;
        ImmutableList<BigInteger> xs;
        
        public void prepare() throws Exception {
            BigInteger p = makePrime(b);
            field =  new IntegersModuloPrime(p);
            System.out.printf("Lagrange options: t=%d b=%d\n", t, b);
            Random rnd = new Random();
            ImmutableList.Builder<BigInteger> builder = ImmutableList.builder();
            for (int i = 0; i < t; i++) {
                builder.add(field.randomElement(rnd));
            }
            xs = builder.build();
        }
        
        public void run() {
            LagrangeInterpolation.computeCoefficients(field, xs);
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
        
        public void prepare() throws Exception {
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
        
        public void run() {
            byte[] cipher = server.encrypt(plain);
            log.debug("128-bit key was encrypted to {} bytes of Naor-Pinkas ciphertext", cipher.length);
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
        
        public void prepare() throws Exception {
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
        
        public void run() {
            try {
                byte[] plain = client.decrypt(cipher);
            } catch (Exception e) {
                log.error("Error while decrypting", e);
            }
        }
    }

    static Map<String, Benchmark> commands = ImmutableMap.of(
              "lagrange", new LagrangeBenchmark(),
              "encrypt", new EncryptBenchmark(),
              "decrypt", new DecryptBenchmark()
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
        benchmark.prepare();
        log.info("Preparation took {} ms, starting tests!", System.currentTimeMillis() - start);
        runBenchmark(benchmark, opts.n, opts.f);
    }
    
    private static void runBenchmark(Runnable run, int rep, int freq) {
        Result res = measure(run, rep, freq);
        if (rep % freq != 0) { 
            res.print();
        }
    }
    
    private static Result measure(Runnable run, int rep, int freq) {
        Result res = new Result(freq);
        for (int i = 0; i < rep; ++i) {
            long start = System.currentTimeMillis();
            run.run();
            res.report((System.currentTimeMillis() - start) / 1000.0);
        }
        return res;
    }
    
    private static BigInteger makePrime(int b) {
        return BigInteger.valueOf(2).pow(b).nextProbablePrime();
    }
}