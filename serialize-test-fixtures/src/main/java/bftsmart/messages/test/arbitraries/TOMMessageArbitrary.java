package bftsmart.messages.test.arbitraries;

import bftsmart.serialization.messages.TOMMessageWire;
import bftsmart.tom.core.messages.TOMMessageType;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;

public final class TOMMessageArbitrary implements ArbitraryMessageSupplier<TOMMessageWire> {

    @Override
    public Arbitrary<TOMMessageWire> getArbitraries() {
        return Combinators.combine(
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
    }

    @Override
    public Arbitrary<TOMMessageWire> getFixtures() {
        TOMMessageWire message = new TOMMessageWire(1);
        message.setSession(2);
        message.setSequence(3);
        message.setOperationId(4);
        message.setContent(new byte[] {5, 6, 7});
        message.setViewID(8);
        message.setType(TOMMessageType.ASK_STATUS);
        message.setReplyServer(42);

        return Arbitraries.of(message);
    }
}
