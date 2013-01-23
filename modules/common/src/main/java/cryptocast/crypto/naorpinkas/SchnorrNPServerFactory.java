package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.SchnorrGroup;

public class SchnorrNPServerFactory implements NPServerFactory {
    public SchnorrNPServer construct(int t, SchnorrGroup g) {
        return new SchnorrNPServer(NPServerContext.generate(t, g));
    }
    
    @Override
    public SchnorrNPServer construct(int t) {
        return construct(t, SchnorrGroup.getP1024Q160());
    }
}
