package broadcastenc.crypto;

public abstract class SecretSharingScheme<T, U extends Share<T>> {
    public SecretSharingScheme(T secret, int t, int initialN) { }
    public abstract U getAtomicShare(int i);
    public abstract U combine(U a, U b);
}
