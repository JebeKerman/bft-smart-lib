package bftsmart.messages.test.arbitraries;

import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.consensus.messages.MessageFactory;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;

public final class ConsensusMessageArbitrary implements ArbitraryMessageSupplier<ConsensusMessage> {

    @Override
    public Arbitrary<ConsensusMessage> getArbitraries() {
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

    @Override
    public Arbitrary<ConsensusMessage> getFixtures() {
        return Arbitraries.of(
                new ConsensusMessage(MessageFactory.PROPOSE, 0, 0, 0, new byte[] {1}),
                new ConsensusMessage(MessageFactory.WRITE, 1, 1, 1, new byte[] {2}),
                new ConsensusMessage(MessageFactory.ACCEPT, 2, 2, 2, new byte[] {3}));
    }
}
