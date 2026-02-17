package bftsmart.serialization.java;

import bftsmart.messages.test.AbstractMessageSerializerTest;
import bftsmart.messages.test.arbitraries.ArbitraryMessageSupplier;
import bftsmart.messages.test.arbitraries.ForwardedMessageArbitrary;
import bftsmart.serialization.MessageSerializer;
import bftsmart.tom.core.messages.ForwardedMessage;

public class TestJavaSerializer_ForwardedMessage
        extends AbstractMessageSerializerTest<ForwardedMessage> {
    @Override
    protected MessageSerializer serializer() {
        return JavaSerializer.getInstance();
    }

    @Override
    protected Class<ForwardedMessage> messageType() {
        return ForwardedMessage.class;
    }

    @Override
    protected ArbitraryMessageSupplier<ForwardedMessage> arbitrarySupplier() {
        return new ForwardedMessageArbitrary();
    }
}
