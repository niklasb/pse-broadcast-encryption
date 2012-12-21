package cryptocast.crypto;

public class InsufficientInformationError extends DecryptionError {
    private static final long serialVersionUID = 297331299012388587L;
    public InsufficientInformationError(String msg) {
        super(msg);
    }
}
