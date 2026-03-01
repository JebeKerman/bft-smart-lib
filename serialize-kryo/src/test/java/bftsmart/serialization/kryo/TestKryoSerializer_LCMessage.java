package bftsmart.serialization.kryo;

import bftsmart.messages.test.AbstractMessageSerializerTest;
import bftsmart.messages.test.arbitraries.ArbitraryMessageSupplier;
import bftsmart.messages.test.arbitraries.LCMessageArbitrary;
import bftsmart.serialization.MessageSerializer;
import bftsmart.tom.leaderchange.LCMessageWire;

public class TestKryoSerializer_LCMessage extends AbstractMessageSerializerTest<LCMessageWire> {

    @Override
    protected MessageSerializer serializer() {
        return KryoSerializer.getInstance();
    }

    @Override
    protected Class<LCMessageWire> messageType() {
        return LCMessageWire.class;
    }

    @Override
    protected ArbitraryMessageSupplier<LCMessageWire> arbitrarySupplier() {
        return new LCMessageArbitrary();
    }

    @Override
    protected String[] ignoredFields() {
        return new String[] {"TRIGGER_LC_LOCALLY"};
    }
}
