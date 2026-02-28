package bftsmart.serialization.proto;

import bftsmart.messages.test.AbstractMessageSerializerTest;
import bftsmart.messages.test.arbitraries.ArbitraryMessageSupplier;
import bftsmart.messages.test.arbitraries.CSTSMMessageArbitrary;
import bftsmart.serialization.MessageSerializer;
import bftsmart.statemanagement.durability.CSTSMMessageWire;

public class TestProtoSerializer_CSTSMMessage
        extends AbstractMessageSerializerTest<CSTSMMessageWire<?>> {
    @Override
    protected MessageSerializer serializer() {
        return ProtoSerializer.getInstance();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<CSTSMMessageWire<?>> messageType() {
        return (Class<CSTSMMessageWire<?>>) (Class<?>) CSTSMMessageWire.class;
    }

    @Override
    protected ArbitraryMessageSupplier<CSTSMMessageWire<?>> arbitrarySupplier() {
        return new CSTSMMessageArbitrary();
    }

    @Override
    protected String[] ignoredFields() {
        return new String[] {"TRIGGER_SM_LOCALLY"};
    }
}
