package bftsmart.serialization.messages;

import bftsmart.serialization.MessageSerializer;
import bftsmart.serialization.java.JavaSerializer;
import bftsmart.tom.core.messages.TOMMessageType;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Thread)
@BenchmarkMode(org.openjdk.jmh.annotations.Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(2)
public class TOMMessageSerializationBenchmark {

    private MessageSerializer serializer;
    private TOMMessagePlain message;
    private ByteArrayOutputStream os;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        serializer = JavaSerializer.getInstance();

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
    public void jacksonSerialize() throws Exception {
        os.reset();
        serializer.serialize(message, os);
    }
}
