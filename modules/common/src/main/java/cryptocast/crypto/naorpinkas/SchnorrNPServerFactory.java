package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.SchnorrGroup;

/** Creates instances of NP servers that use the variant based on a Schnorr
 * group. */
public class SchnorrNPServerFactory implements NPServerFactory {
    /**
     * @param t The parameter $t$ of the NP scheme.
     * @param g A group instance.
     * @return An NP server instance.
     */
    public SchnorrNPServer construct(int t, SchnorrGroup g) {
        return new SchnorrNPServer(NPServerContext.generate(t, g));
    }

    @Override
    public SchnorrNPServer construct(int t) {
        return construct(t, SchnorrGroup.getP1024Q160());
    }
}
