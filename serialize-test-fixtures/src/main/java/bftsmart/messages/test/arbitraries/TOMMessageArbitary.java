package bftsmart.messages.test.arbitraries;

import bftsmart.serialization.messages.TOMMessagePlain;
import bftsmart.tom.core.messages.TOMMessageType;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;

public final class TOMMessageArbitary implements ArbitraryMessageSupplier<TOMMessagePlain> {

    @Override
    public Arbitrary<TOMMessagePlain> getArbitraries() {
        return Combinators.combine(
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        Arbitraries.bytes()
                                .array(byte[].class)
                                .ofMinSize(1)
                                .ofMaxSize(2 ^ 14)
                                .injectNull(0.05),
                        Arbitraries.integers(),
                        Arbitraries.of(TOMMessageType.values()))
                .as(TOMMessagePlain::new);
    }

    @Override
    public Arbitrary<TOMMessagePlain> getFixtures() {
        TOMMessagePlain message = new TOMMessagePlain(1);
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
