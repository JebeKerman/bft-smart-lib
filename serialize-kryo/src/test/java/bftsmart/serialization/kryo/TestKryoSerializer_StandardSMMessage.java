package bftsmart.serialization.kryo;

import bftsmart.messages.test.AbstractMessageSerializerTest;
import bftsmart.messages.test.arbitraries.ArbitraryMessageSupplier;
import bftsmart.messages.test.arbitraries.StandardSMMessageArbitrary;
import bftsmart.serialization.MessageSerializer;
import bftsmart.statemanagement.standard.StandardSMMessageWire;

public class TestKryoSerializer_StandardSMMessage
        extends AbstractMessageSerializerTest<StandardSMMessageWire<?>> {
    @Override
    protected MessageSerializer serializer() {
        return KryoSerializer.getInstance();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<StandardSMMessageWire<?>> messageType() {
        return (Class<StandardSMMessageWire<?>>) (Class<?>) StandardSMMessageWire.class;
    }

    @Override
    protected ArbitraryMessageSupplier<StandardSMMessageWire<?>> arbitrarySupplier() {
        return new StandardSMMessageArbitrary();
    }

    @Override
    protected String[] ignoredFields() {
        return new String[] {"TRIGGER_SM_LOCALLY"};
    }
}
