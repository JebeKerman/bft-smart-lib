package bftsmart.serialization.proto;

import bftsmart.messages.test.AbstractMessageSerializerTest;
import bftsmart.messages.test.arbitraries.ArbitraryMessageSupplier;
import bftsmart.messages.test.arbitraries.TOMMessageArbitary;
import bftsmart.serialization.MessageSerializer;
import bftsmart.serialization.messages.TOMMessagePlain;

public class TestProtoSerializer_TOMMessage extends AbstractMessageSerializerTest<TOMMessagePlain> {
    @Override
    protected MessageSerializer serializer() {
        return ProtoSerializer.getInstance();
    }

    @Override
    protected Class<TOMMessagePlain> messageType() {
        return TOMMessagePlain.class;
    }

    @Override
    protected ArbitraryMessageSupplier<TOMMessagePlain> arbitrarySupplier() {
        return new TOMMessageArbitary();
    }
}
