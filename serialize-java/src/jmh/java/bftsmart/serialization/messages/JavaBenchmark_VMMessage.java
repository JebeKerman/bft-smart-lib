package bftsmart.serialization.messages;

import bftsmart.messages.bench.AbstractMessageBenchmark;
import bftsmart.messages.bench.MessageProvider;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.serialization.MessageSerializer;
import bftsmart.serialization.java.JavaSerializer;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class JavaBenchmark_VMMessage extends AbstractMessageBenchmark<VMMessage> {
    @Override
    protected MessageSerializer createSerializer() {
        return JavaSerializer.getInstance();
    }

    @Override
    protected VMMessage createMessage() {
        return MessageProvider.getVMMessage();
    }

    @Override
    protected Class<VMMessage> messageType() {
        return VMMessage.class;
    }
}
