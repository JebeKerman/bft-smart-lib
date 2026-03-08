package bftsmart.serialization.java;

import bftsmart.messages.test.AbstractMessageSerializerTest;
import bftsmart.messages.test.arbitraries.ArbitraryMessageSupplier;
import bftsmart.messages.test.arbitraries.StandardSMMessageArbitrary;
import bftsmart.serialization.MessageSerializer;
import bftsmart.statemanagement.standard.StandardSMMessageWire;

public class TestJavaSerializer_StandardSMMessage
        extends AbstractMessageSerializerTest<StandardSMMessageWire<Integer>> {
    @Override
    protected MessageSerializer serializer() {
        return JavaSerializer.getInstance();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<StandardSMMessageWire<Integer>> messageType() {
        return (Class<StandardSMMessageWire<Integer>>) (Class<?>) StandardSMMessageWire.class;
    }

    @Override
    protected ArbitraryMessageSupplier<StandardSMMessageWire<Integer>> arbitrarySupplier() {
        return new StandardSMMessageArbitrary();
    }

    @Override
    protected String[] ignoredFields() {
        return new String[] {"TRIGGER_SM_LOCALLY"};
    }
}
