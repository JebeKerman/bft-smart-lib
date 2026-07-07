package bftsmart.messages.test.arbitraries;

import bftsmart.serialization.messages.TOMMessageWire;
import bftsmart.tom.core.messages.ForwardedMessage;
import bftsmart.tom.core.messages.TOMMessageType;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;

public final class ForwardedMessageArbitrary implements ArbitraryMessageSupplier<ForwardedMessage> {

    @Override
    public Arbitrary<ForwardedMessage> getArbitraries() {
        Arbitrary<TOMMessageWire> tomMessages =
                Combinators.combine(
                                Arbitraries.integers(),
                                Arbitraries.integers(),
                                Arbitraries.integers(),
                                Arbitraries.integers(),
                                Arbitraries.bytes()
                                        .array(byte[].class)
                                        .ofMinSize(1)
                                        .ofMaxSize(1024)
                                        .injectNull(0.05),
                                Arbitraries.integers(),
                                Arbitraries.of(TOMMessageType.values()))
                        .as(TOMMessageWire::new);

        return Combinators.combine(Arbitraries.integers(), tomMessages).as(ForwardedMessage::new);
    }

    @Override
    public Arbitrary<ForwardedMessage> getFixtures() {
        TOMMessageWire tomMessage = new TOMMessageWire(1);
        tomMessage.setSession(2);
        tomMessage.setSequence(3);
        tomMessage.setOperationId(4);
        tomMessage.setContent(new byte[] {5, 6, 7});
        tomMessage.setViewID(8);
        tomMessage.setType(TOMMessageType.ASK_STATUS);
        tomMessage.setReplyServer(42);

        ForwardedMessage message = new ForwardedMessage(1, tomMessage);
        return Arbitraries.of(message);
    }
}
