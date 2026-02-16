package bftsmart.messages.test.arbitraries;

import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.consensus.messages.MessageFactory;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ArbitrarySupplier;
import net.jqwik.api.Combinators;

public final class ConsensusMessageArbitrary implements ArbitrarySupplier<ConsensusMessage> {

    @Override
    public Arbitrary<ConsensusMessage> get() {
        return Combinators.combine(
                        Arbitraries.of(
                                MessageFactory.PROPOSE,
                                MessageFactory.WRITE,
                                MessageFactory.ACCEPT),
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        Arbitraries.bytes().array(byte[].class).ofMinSize(1).ofMaxSize(16384))
                .as(ConsensusMessage::new);
    }
}
