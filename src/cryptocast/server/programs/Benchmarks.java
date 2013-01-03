package cryptocast.server.programs;

import java.math.BigInteger;
import java.util.Date;
import java.util.Random;

import com.beust.jcommander.*;
import com.google.common.collect.ImmutableList;

import cryptocast.crypto.IntegersModuloPrime;
import cryptocast.crypto.LagrangeInterpolation;
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
    public void prepare();
}

/**
 * The main method to start the server.
 */
public final class Benchmarks {
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
        
        BigInteger p;
        IntegersModuloPrime field;
        ImmutableList<BigInteger> xs;
        
        public void prepare() {
            p = makePrime(b);
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

    /**
     * @param args command line arguments
     */
    public static void main(String[] argv) throws Exception {
        OptsCommon opts = new OptsCommon();
        JCommander jc = new JCommander(opts);
        LagrangeBenchmark lagrange = new LagrangeBenchmark();
        jc.addCommand("lagrange", lagrange);
        jc.parse(argv);
        
        System.out.printf("Running with n=%d  f=%d\n", opts.n, opts.f);
        Benchmark benchmark = null;
        if (jc.getParsedCommand() == "lagrange") {
            benchmark = lagrange;
        } else {
            System.err.println("Available benchmarks: lagrange");
            return;
        }
        benchmark.prepare();
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