package bftsmart.messages.bench;

import bftsmart.communication.SystemMessage;
import bftsmart.serialization.MessageSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public abstract class AbstractMessageBenchmark<T extends SystemMessage> {

    protected MessageSerializer serializer;
    protected ByteArrayOutputStream os;

    protected T message;
    protected byte[] serializedMessage;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        serializer = createSerializer();
        message = createMessage();

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
    public T deserialize() throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(serializedMessage);
        return messageType().cast(serializer.deserialize(is));
    }

    protected abstract MessageSerializer createSerializer();

    protected abstract T createMessage();

    protected abstract Class<T> messageType();
}
