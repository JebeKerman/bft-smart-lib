package bftsmart.serialization.proto;

import bftsmart.serialization.MessageSerializer;
import bftsmart.serialization.messages.TOMMessagePlain;
import bftsmart.tom.core.messages.TOMMessageType;
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

        message = new TOMMessagePlain(1);
        message.setSession(2);
        message.setSequence(3);
        message.setOperationId(4);
        message.setContent(new byte[] {5, 6, 7});
        message.setViewID(8);
        message.setType(TOMMessageType.ASK_STATUS);
        message.setReplyServer(42);

        os = new ByteArrayOutputStream();
    }

    @Benchmark
    public void protoSerializeTOMMessage() throws Exception {
        os.reset();
        serializer.serialize(message, os);
    }
}
