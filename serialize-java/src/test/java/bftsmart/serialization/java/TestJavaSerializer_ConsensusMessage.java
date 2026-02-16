package bftsmart.serialization.java;

import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.messages.test.AbstractMessageSerializerTest;
import bftsmart.messages.test.arbitraries.ConsensusMessageArbitrary;
import bftsmart.messages.test.arbitraries.ConsensusMessageFixtures;
import bftsmart.serialization.MessageSerializer;
import java.util.function.Supplier;
import net.jqwik.api.Arbitrary;

public class TestJavaSerializer_ConsensusMessage
        extends AbstractMessageSerializerTest<ConsensusMessage> {
    @Override
    protected MessageSerializer serializer() {
        return JavaSerializer.getInstance();
    }

    @Override
    protected Class<ConsensusMessage> messageType() {
        return ConsensusMessage.class;
    }

    @Override
    protected Supplier<? extends Arbitrary<ConsensusMessage>> arbitrarySupplier() {
        return new ConsensusMessageArbitrary();
    }

    @Override
    protected Supplier<? extends Arbitrary<ConsensusMessage>> fixturesSupplier() {
        return new ConsensusMessageFixtures();
    }
}
