package bftsmart.messages.test.arbitraries;

import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.consensus.messages.MessageFactory;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ArbitrarySupplier;

public final class ConsensusMessageFixtures implements ArbitrarySupplier<ConsensusMessage> {

    @Override
    public Arbitrary<ConsensusMessage> get() {
        return Arbitraries.of(
                new ConsensusMessage(MessageFactory.PROPOSE, 0, 0, 0, new byte[] {1}),
                new ConsensusMessage(MessageFactory.WRITE, 1, 1, 1, new byte[] {2}),
                new ConsensusMessage(MessageFactory.ACCEPT, 2, 2, 2, new byte[] {3}));
    }
}
