package bftsmart.serialization.proto;

import bftsmart.messages.bench.MessageProvider;
import bftsmart.serialization.MessageSerializer;
import bftsmart.serialization.messages.TOMMessagePlain;
import java.io.ByteArrayOutputStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class ProtoBenchmark_TOMMessage {

    private MessageSerializer serializer;
    private TOMMessagePlain message;
    private ByteArrayOutputStream os;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        serializer = ProtoSerializer.getInstance();

        message = MessageProvider.getTOMMessage();

        os = new ByteArrayOutputStream();
    }

    @Benchmark
    public void serialize() throws Exception {
        os.reset();
        serializer.serialize(message, os);
    }
}
