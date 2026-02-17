package bftsmart.serialization.proto;

import bftsmart.messages.test.AbstractMessageSerializerTest;
import bftsmart.messages.test.arbitraries.ArbitraryMessageSupplier;
import bftsmart.messages.test.arbitraries.TOMMessageArbitary;
import bftsmart.serialization.MessageSerializer;
import bftsmart.serialization.messages.TOMMessageWire;

public class TestProtoSerializer_TOMMessage extends AbstractMessageSerializerTest<TOMMessageWire> {
    @Override
    protected MessageSerializer serializer() {
        return ProtoSerializer.getInstance();
    }

    @Override
    protected Class<TOMMessageWire> messageType() {
        return TOMMessageWire.class;
    }

    @Override
    protected ArbitraryMessageSupplier<TOMMessageWire> arbitrarySupplier() {
        return new TOMMessageArbitary();
    }
}
