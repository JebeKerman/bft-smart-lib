package bftsmart.analysis;

import bftsmart.communication.SystemMessage;
import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.messages.bench.MessageProvider;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.serialization.MessageSerializer;
import bftsmart.serialization.java.JavaSerializer;
import bftsmart.serialization.messages.TOMMessageWire;
import bftsmart.serialization.proto.ProtoSerializer;
import bftsmart.statemanagement.durability.CSTSMMessageWire;
import bftsmart.statemanagement.standard.StandardSMMessageWire;
import bftsmart.tom.leaderchange.LCMessageWire;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class ConsensusMessageBenchmark {

    @Param public SerializerType serializerType;
    @Param public MessageType messageType;

    private MessageSerializer serializer;
    private ByteArrayOutputStream os;

    private SystemMessage message;
    private Class<? extends SystemMessage> messageClass;
    private byte[] serializedMessage;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        serializer = serializerType.serializer;

        message = messageType.message;
        messageClass = messageType.messageClass;

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
    @SuppressWarnings("unused")
    public void deserialize() throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(serializedMessage);
        SystemMessage plain = messageClass.cast(serializer.deserialize(is));
    }

    public enum SerializerType {
        Java(JavaSerializer.getInstance()),
        Proto(ProtoSerializer.getInstance());

        private MessageSerializer serializer;

        private SerializerType(MessageSerializer serializer) {
            this.serializer = serializer;
        }
    }

    public enum MessageType {
        CSTSMMessageMinimal(MessageProvider.getCSTSMMessageMinimal(), CSTSMMessageWire.class),
        ConsensusMessage(MessageProvider.getConsensusMessage(), ConsensusMessage.class),
        LCMessage(MessageProvider.getLCMessage(), LCMessageWire.class),
        StandardSMMessage(
                MessageProvider.getStandardSMMessageMinimal(), StandardSMMessageWire.class),
        TOMMessage(MessageProvider.getTOMMessage(), TOMMessageWire.class),
        VMMessage(MessageProvider.getVMMessage(), VMMessage.class);

        private SystemMessage message;
        private Class<? extends SystemMessage> messageClass;

        private MessageType(SystemMessage message, Class<? extends SystemMessage> messageClass) {
            this.message = message;
            this.messageClass = messageClass;
        }
    }
}
