package cryptocast.server;

/**
 * The main method to start the server
 */
public class Main {
    /**
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new Shell(System.in, System.out, System.err).run(args);
    }
}
