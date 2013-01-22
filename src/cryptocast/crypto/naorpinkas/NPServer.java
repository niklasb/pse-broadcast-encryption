package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import static com.google.common.base.Preconditions.*;

/**
 * A server in the Naor-Pinkas broadcast encryption scheme. It knows
 * the entire polynomial and therefore all the private keys of its users. 
 */
public abstract class NPServer<T, G extends CyclicGroupOfPrimeOrder<T>>
          extends Observable
          implements NPServerInterface, Serializable {
    private static final Logger log = LoggerFactory.getLogger(NPServer.class);
    
    private static final long serialVersionUID = -6864326409385317975L;

    private NPServerContext<T, G> context;
    private T gp0;  // $g^P(0)$
    private Map<NPIdentity, NPKey<T, G>> keyByIdentity =
                                Maps.newHashMap();
    private Set<NPIdentity> revokedUsers = Sets.newHashSet();
    
    private G group;
    private int t;
    
    private static SecureRandom rnd = new SecureRandom();

    protected NPServer(NPServerContext<T, G> context) {
        this.context = context;
        this.group = context.getGroup();
        this.t = context.getT();
        this.gp0 = group.getPowerOfG(
                          context.getPoly().evaluate(BigInteger.ZERO));
    }

    @Override
    public int getT() {
        return t;
    }
    
    public NPServerContext<T, G> getContext() { return context; }

    protected abstract Optional<byte[]> encryptSecretWithItem(byte[] secret, T key);
    
    @Override
    public byte[] encrypt(byte[] secret) {
        return packMessage(encryptMessage(secret));
    }
    
    protected NPMessage<T, G> encryptMessage(byte[] secret) {
        long t0 = System.currentTimeMillis();
        BigInteger r = group.getFieldModOrder().randomElement(rnd);
        T grp0 = group.pow(gp0, r), // g^{r P(0)} is the secret value that can be computed by
                                    // authorized users
          gr = group.getPowerOfG(r);
        Optional<byte[]> mEncryptedSecret = encryptSecretWithItem(secret, grp0);
        checkArgument(mEncryptedSecret.isPresent(), "Secret is too large to encrypt");
        byte[] encryptedSecret = mEncryptedSecret.get();
        
        ImmutableList.Builder<NPShare<T, G>> shares = ImmutableList.builder();
        int i = 0;
        int dummies = t - revokedUsers.size();
        while (i < dummies) {
            NPShare<T, G> share = getDummyKey(i).getShare(r, gr);
            shares.add(share);
            ++i;
        }
        for (NPIdentity id : revokedUsers) {
            NPShare<T, G> share = getPersonalKey(id).get().getShare(r, gr);
            shares.add(share);
        }
        ImmutableList.Builder<BigInteger> lagrangeCoeffs = ImmutableList.builder();
        for (NPShare<T, G> share : shares.build()) {
            BigInteger c = context.getLagrange().getCoefficients().get(share.getI());
            assert c != null;
            lagrangeCoeffs.add(c);
        }
        
        log.debug("Encryption took ~{} ms", System.currentTimeMillis() - t0);
        return new NPMessage<T, G>(
                t, r, encryptedSecret, group, 
                lagrangeCoeffs.build(), shares.build());
    }
    
    protected abstract void writeShare(ByteArrayDataOutput out, NPShare<T, G> share);
    
    private byte[] packMessage(NPMessage<T, G> msg) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(msg.getT());
        putBytes(out, msg.getR().toByteArray());
        putBytes(out, msg.getEncryptedSecret());
        for (BigInteger c : msg.getLagrangeCoeffs()) {
            putBytes(out, c.toByteArray());
        }
        for (NPShare<T, G> share : msg.getShares()) {
            writeShare(out, share);
        }
        return out.toByteArray();
    }
    
    protected void putBytes(ByteArrayDataOutput out, byte[] x) {
        out.writeInt(x.length);
        out.write(x);
    }
    
    /**
     * @param i An index.
     * @return The identity with the given index.
     */
    @Override
    public NPIdentity getIdentity(int i) {
        return getUserKey(i).getIdentity();
    }

    private NPKey<T, G> getUserKey(int i) {
        // first t users are dummies
        NPKey<T, G> key = context.getKeyGen().get(t + i);
        keyByIdentity.put(key.getIdentity(), key);
        return key;
    }

    private NPKey<T, G> getDummyKey(int i) {
        return context.getKeyGen().get(i);
    }

    /**
     * Revokes multiple users.
     * 
     * @param id The identities of the users
     * @return true, if the set of revoked users changed or false otherwise
     */
    @Override
    public boolean revoke(Set<NPIdentity> ids) throws NoMoreRevocationsPossibleError {
        Set<NPIdentity> notYetRevoked = Sets.difference(ids, revokedUsers);
        if (notYetRevoked.size() == 0) { return false; }
        if (getRemainingRevocations() < notYetRevoked.size()) {
            throw new NoMoreRevocationsPossibleError();
        }
        for (NPIdentity id : notYetRevoked) {
            revokeUnconditional(id);
        }
        log.trace("Set of revoked users changed, informing observers");
        setChanged();
        notifyObservers();
        return true;
    }
    
    /**
     * Revokes a user.
     * 
     * @param id The identity of the user
     * @return true, if the set of revoked users changed or false otherwise
     */
    @Override
    public boolean revoke(NPIdentity id) throws NoMoreRevocationsPossibleError {
        return revoke(ImmutableSet.of(id));
    }
    
    // must only be called if we know revocation will be successful
    // and that the user is not already revoked!
    private void revokeUnconditional(NPIdentity id) {
        assert getRemainingRevocations() > 0 && !revokedUsers.contains(id);
        // TODO abstract this away
        // remove highest dummy key and add new identity
        context.getLagrange().removeX(
                getDummyKey(getRemainingRevocations() - 1).getIdentity().getI());
        context.getLagrange().addX(id.getI());
        revokedUsers.add(id);
    }
    
    private int getRemainingRevocations() {
        return t - revokedUsers.size();
    }
    
    /**
     * Authorizes a user.
     * 
     * @param id The identity of the user
     * @return true, if the set of revoked users changed or false otherwise
     */
    @Override
    public boolean unrevoke(NPIdentity id) {
        if (!revokedUsers.contains(id)) { return false; }
        // TODO abstract this away
        // remove old identity and add new dummy
        context.getLagrange().removeX(id.getI());
        context.getLagrange().addX(
                    getDummyKey(t - revokedUsers.size()).getIdentity().getI());
        revokedUsers.remove(id);
        log.trace("Set of revoked users changed, informing observers");
        setChanged();
        notifyObservers();
        return true;
    }
    
    /**
     * @param id The identity of the user
     * @return Whether the user is revoked.
     */
    @Override
    public boolean isRevoked(NPIdentity id) {
        return revokedUsers.contains(id);
    }
    
    /**
     * @param id The identity to look up
     * @return The private key of the user or absent if no
     * such user exists
     */
    public Optional<NPKey<T, G>> getPersonalKey(NPIdentity id) {
        return Optional.fromNullable(keyByIdentity.get(id));
    }
}
