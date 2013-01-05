package cryptocast.clienttestsjunit4;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@RunWith(Suite.class)
@Suite.SuiteClasses({
    cryptocast.comm.TestStreamUtils.class,
    cryptocast.comm.TestSimpleHttpStreamServer.class,
    cryptocast.comm.TestStatisticalInputStream.class,
    cryptocast.comm.TestMultiOutputStream.class,
    cryptocast.comm.TestDecoratingMessageOutChannel.class,
    cryptocast.comm.TestStreamMessageInChannel.class,
    cryptocast.comm.TestMessageBuffer.class,
    cryptocast.comm.TestStreamMessageChannels.class,
    cryptocast.client.filechooser.TestFileChooserState.class,
    cryptocast.crypto.naorpinkas.TestNaorPinkasShare.class,
    cryptocast.crypto.naorpinkas.TestNaorPinkasCommunication.class,
    cryptocast.crypto.naorpinkas.TestNaorPinkasShareCombinator.class,
    cryptocast.crypto.naorpinkas.TestNaorPinkasServer.class,
    cryptocast.crypto.naorpinkas.TestNaorPinkasMessage.class,
    cryptocast.crypto.naorpinkas.TestBroadcastEncryptionCommunication.class,
    cryptocast.crypto.TestPolynomial.class,
    cryptocast.crypto.TestBroadcastEncryptionServer.class,
    cryptocast.crypto.TestLagrangeInterpolation.class,
    cryptocast.crypto.TestSubproductTree.class,
    cryptocast.crypto.TestModularExponentiationGroup.class,
    cryptocast.crypto.TestDynamicCipherStreams.class,
    cryptocast.util.TestSerializationUtils.class,
    cryptocast.util.TestOptimisticGenerator.class,
    cryptocast.util.TestGenerator.class,
})
public class AllTests {}