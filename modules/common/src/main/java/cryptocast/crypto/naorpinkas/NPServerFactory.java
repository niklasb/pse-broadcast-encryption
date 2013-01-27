package cryptocast.crypto.naorpinkas;

/** A strategy to create NP servers. */
public interface NPServerFactory {
    /**
     * @param t The parameter $t$ of the NP scheme
     * @return A newly generated NP server instance
     */
    NPServerInterface construct(int t);
}
