package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.SchnorrGroup;

public class SchnorrNaorPinkasServerFactory implements NaorPinkasServerFactory {
    public NaorPinkasServerInterface construct(int t, SchnorrGroup g) {
        return new SchnorrNaorPinkasServer(NaorPinkasServerContext.generate(t, g));
    }
    
    @Override
    public NaorPinkasServerInterface construct(int t) {
        return construct(t, SchnorrGroup.getP1024Q160());
    }
}
