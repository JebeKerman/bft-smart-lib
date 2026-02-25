package bftsmart.serialization.messages;

import bftsmart.messages.bench.AbstractMessageBenchmark;
import bftsmart.messages.bench.MessageProvider;
import bftsmart.serialization.MessageSerializer;
import bftsmart.serialization.java.JavaSerializer;
import bftsmart.statemanagement.durability.CSTSMMessageWire;
import java.io.Serializable;

public class JavaBenchmark_CSTSMMessage
        extends AbstractMessageBenchmark<CSTSMMessageWire<? extends Serializable>> {

    @Override
    protected MessageSerializer createSerializer() {
        return JavaSerializer.getInstance();
    }

    @Override
    protected CSTSMMessageWire<? extends Serializable> createMessage() {
        return MessageProvider.getCSTSMMessageMinimal();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<CSTSMMessageWire<? extends Serializable>> messageType() {
        return (Class<CSTSMMessageWire<? extends Serializable>>) (Class<?>) CSTSMMessageWire.class;
    }
}
