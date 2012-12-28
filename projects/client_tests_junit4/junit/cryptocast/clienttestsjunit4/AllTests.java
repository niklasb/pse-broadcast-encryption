package cryptocast.clienttestsjunit4;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@RunWith(Suite.class)
@Suite.SuiteClasses({
    cryptocast.comm.test.TestStreamUtils.class,
    cryptocast.comm.test.TestStatisticalInputStream.class,
    cryptocast.comm.test.TestDecoratingMessageOutChannel.class,
    cryptocast.comm.test.TestStreamMessageInChannel.class,
    cryptocast.comm.test.TestMessageBuffer.class,
    cryptocast.comm.test.TestStreamMessageChannels.class,
    cryptocast.comm.test.TestMultiOutputStream.class,
    cryptocast.crypto.test.TestPolynomial.class,
    cryptocast.crypto.test.TestBroadcastEncryptionServer.class,
    cryptocast.crypto.test.TestLagrangeInterpolation.class,
    cryptocast.crypto.test.TestModularExponentiationGroup.class,
    cryptocast.crypto.test.TestDynamicCipherStreams.class,
    cryptocast.crypto.naorpinkas.test.TestNaorPinkasShare.class,
    cryptocast.crypto.naorpinkas.test.TestNaorPinkasCommunication.class,
    cryptocast.crypto.naorpinkas.test.TestNaorPinkasShareCombinator.class,
    cryptocast.crypto.naorpinkas.test.TestNaorPinkasServer.class,
    cryptocast.crypto.naorpinkas.test.TestNaorPinkasMessage.class,
    cryptocast.crypto.naorpinkas.test.TestBroadcastEncryptionCommunication.class,
    cryptocast.util.test.TestOptimisticGenerator.class,
    cryptocast.util.test.TestGenerator.class,
})
public class AllTests {}