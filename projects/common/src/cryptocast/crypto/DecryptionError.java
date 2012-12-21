package cryptocast.crypto;

public class DecryptionError extends Exception {
    private static final long serialVersionUID = -979360017405928569L;
    public DecryptionError(String msg) {
        super(msg);
    }
}
