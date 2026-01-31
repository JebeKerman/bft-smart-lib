package bftsmart.serialization.proto;

import bftsmart.messages.bench.MessageProvider;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.serialization.MessageSerializer;
import java.io.ByteArrayOutputStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class ProtoBenchmark_VMMessage {

    private MessageSerializer serializer;
    private VMMessage message;
    private ByteArrayOutputStream os;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        serializer = ProtoSerializer.getInstance();

        message = MessageProvider.getVMMessage();

        os = new ByteArrayOutputStream();
    }

    @Benchmark
    public void serialize() throws Exception {
        os.reset();
        serializer.serialize(message, os);
    }
}
