package broadcastenc.crypto;

class PersonalKey { }

public abstract class BroadcastSchemeClient<T, U extends Share<T>> {
    public BroadcastSchemeClient(PersonalKey key) { }
    protected abstract U getPrivateShare();
}
