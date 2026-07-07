package bftsmart.serialization.java;

import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.messages.test.AbstractMessageSerializerTest;
import bftsmart.messages.test.arbitraries.ArbitraryMessageSupplier;
import bftsmart.messages.test.arbitraries.ConsensusMessageArbitrary;
import bftsmart.serialization.MessageSerializer;

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
    protected ArbitraryMessageSupplier<ConsensusMessage> arbitrarySupplier() {
        return new ConsensusMessageArbitrary();
    }
}
