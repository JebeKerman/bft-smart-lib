package bftsmart.serialization.proto;

import bftsmart.messages.bench.MessageProvider;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.serialization.MessageSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class ProtoBenchmark_VMMessage {

    private MessageSerializer serializer;
    private ByteArrayOutputStream os;

    private VMMessage message;
    private byte[] serializedMessage;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        serializer = ProtoSerializer.getInstance();

        message = MessageProvider.getVMMessage();

        os = new ByteArrayOutputStream();
        serializer.serialize(message, os);
        serializedMessage = os.toByteArray();
        os.reset();
    }

    @Benchmark
    public void serialize() throws Exception {
        os.reset();
        serializer.serialize(message, os);
    }

    @Benchmark
    public void deserialize() throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(serializedMessage);
        serializer.deserialize(is, VMMessage.class);
    }
}
