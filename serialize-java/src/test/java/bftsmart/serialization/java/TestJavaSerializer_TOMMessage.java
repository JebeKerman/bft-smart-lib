package bftsmart.serialization.java;

import bftsmart.messages.test.AbstractMessageSerializerTest;
import bftsmart.messages.test.arbitraries.ArbitraryMessageSupplier;
import bftsmart.messages.test.arbitraries.TOMMessageArbitrary;
import bftsmart.serialization.MessageSerializer;
import bftsmart.serialization.messages.TOMMessageWire;

public class TestJavaSerializer_TOMMessage extends AbstractMessageSerializerTest<TOMMessageWire> {
    @Override
    protected MessageSerializer serializer() {
        return JavaSerializer.getInstance();
    }

    @Override
    protected Class<TOMMessageWire> messageType() {
        return TOMMessageWire.class;
    }

    @Override
    protected ArbitraryMessageSupplier<TOMMessageWire> arbitrarySupplier() {
        return new TOMMessageArbitrary();
    }
}
