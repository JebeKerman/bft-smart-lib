package bftsmart.serialization.proto;

import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.messages.test.AbstractMessageSerializerTest;
import bftsmart.messages.test.arbitraries.ArbitraryMessageSupplier;
import bftsmart.messages.test.arbitraries.ConsensusMessageArbitrary;
import bftsmart.serialization.MessageSerializer;

public class TestProtoSerializer_ConsensusMessage
        extends AbstractMessageSerializerTest<ConsensusMessage> {
    @Override
    protected MessageSerializer serializer() {
        return ProtoSerializer.getInstance();
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
