package bftsmart.serialization.java;

import bftsmart.messages.test.AbstractMessageSerializerTest;
import bftsmart.messages.test.arbitraries.ArbitraryMessageSupplier;
import bftsmart.messages.test.arbitraries.VMMessageArbitrary;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.serialization.MessageSerializer;

public class TestJavaSerializer_VMMessage extends AbstractMessageSerializerTest<VMMessage> {
    @Override
    protected MessageSerializer serializer() {
        return JavaSerializer.getInstance();
    }

    @Override
    protected Class<VMMessage> messageType() {
        return VMMessage.class;
    }

    @Override
    protected ArbitraryMessageSupplier<VMMessage> arbitrarySupplier() {
        return new VMMessageArbitrary();
    }
}
