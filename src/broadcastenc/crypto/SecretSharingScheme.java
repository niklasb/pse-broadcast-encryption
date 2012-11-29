package broadcastenc.crypto;

public abstract class SecretSharingScheme<T, U> {
    public SecretSharingScheme(T secret, int t, int initialN) { }
    public abstract Share<T, U> getAtomicShare(int i);
}
