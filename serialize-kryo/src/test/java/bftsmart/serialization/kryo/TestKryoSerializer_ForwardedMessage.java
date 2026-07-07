package bftsmart.serialization.kryo;

import bftsmart.messages.test.AbstractMessageSerializerTest;
import bftsmart.messages.test.arbitraries.ArbitraryMessageSupplier;
import bftsmart.messages.test.arbitraries.ForwardedMessageArbitrary;
import bftsmart.serialization.MessageSerializer;
import bftsmart.tom.core.messages.ForwardedMessage;

public class TestKryoSerializer_ForwardedMessage
        extends AbstractMessageSerializerTest<ForwardedMessage> {
    @Override
    protected MessageSerializer serializer() {
        return KryoSerializer.getInstance();
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
