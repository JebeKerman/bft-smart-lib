package bftsmart.serialization.messages;

import bftsmart.messages.bench.AbstractMessageBenchmark;
import bftsmart.messages.bench.MessageProvider;
import bftsmart.serialization.MessageSerializer;
import bftsmart.serialization.java.JavaSerializer;
import bftsmart.statemanagement.standard.StandardSMMessageWire;
import java.io.Serializable;

public class JavaBenchmark_StandardSMMessage
        extends AbstractMessageBenchmark<StandardSMMessageWire<? extends Serializable>> {

    @Override
    protected MessageSerializer createSerializer() {
        return JavaSerializer.getInstance();
    }

    @Override
    protected StandardSMMessageWire<? extends Serializable> createMessage() {
        return MessageProvider.getStandardSMMessageMinimal();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<StandardSMMessageWire<? extends Serializable>> messageType() {
        return (Class<StandardSMMessageWire<? extends Serializable>>)
                (Class<?>) StandardSMMessageWire.class;
    }
}
