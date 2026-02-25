package bftsmart.serialization.messages;

import bftsmart.messages.bench.AbstractMessageBenchmark;
import bftsmart.messages.bench.MessageProvider;
import bftsmart.serialization.MessageSerializer;
import bftsmart.serialization.java.JavaSerializer;
import bftsmart.tom.leaderchange.LCMessageWire;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class JavaBenchmark_LCMessage extends AbstractMessageBenchmark<LCMessageWire> {
    @Override
    protected MessageSerializer createSerializer() {
        return JavaSerializer.getInstance();
    }

    @Override
    protected LCMessageWire createMessage() {
        return MessageProvider.getLCMessage();
    }

    @Override
    protected Class<LCMessageWire> messageType() {
        return LCMessageWire.class;
    }
}
