package broadcastenc.crypto;

class PersonalKey { }

public abstract class BroadcastSchemeClient<T, U> {
  public BroadcastSchemeClient(PersonalKey key) { }
  protected abstract Share<T, U> getPrivateShare();
}
