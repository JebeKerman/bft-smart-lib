package bftsmart.serialization;

import java.util.TreeMap;
import bftsmart.serialization.java.JavaSerializer;
import bftsmart.serialization.proto.ProtoSerializer;
import bftsmart.statemanagement.durability.CSTSMMessage;
import bftsmart.statemanagement.standard.StandardSMMessage;
import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.leaderchange.LCMessage;
import bftsmart.tom.server.defaultservices.DefaultApplicationState;
import bftsmart.serialization.kryo.KryoSerializer;

public class MessageSerializerFactory {
    private static final String PROPERTY = "serialization.framework";
    private static final String DEFAULT_SERIALIZER = "java";

    private static final String MEASURE_PROPERTY = "serialization.measure.bytes";

    private static ByteCountMessageSerializer byteCountSerializer = null;

    public static MessageSerializer getSerializer() {
        String type = System.getProperty(PROPERTY, DEFAULT_SERIALIZER);
        Serializer serializerType = Serializer.fromName(type);
        MessageSerializer serializer = null;
        switch (serializerType) {
            case Kryo:
                KryoSerializer instance = KryoSerializer.getInstance();
                instance.register(StandardSMMessage.class);
                instance.register(CSTSMMessage.class);
                instance.register(TOMMessage.class);
                instance.register(LCMessage.class);
                instance.register(DefaultApplicationState.class);
                instance.register(TreeMap.class);
                serializer = instance;
                break;
            case Proto:
                serializer = ProtoSerializer.getInstance();
                break;
            case Java:
                serializer = JavaSerializer.getInstance();
                break;
        }
        if (Boolean.getBoolean(MEASURE_PROPERTY)) {
            byteCountSerializer = new ByteCountMessageSerializer(serializer);
            serializer = byteCountSerializer;
        }
        return serializer;
    }

    public static ByteCountMessageSerializer getByteCountSerializer() {
        return byteCountSerializer;
    }

    private MessageSerializerFactory() { }

    private static enum Serializer {
        Java,
        Proto,
        Kryo;

        private static Serializer fromName(String name) {
            switch (name) {
                case "java":
                    return Serializer.Java;
                case "proto":
                    return Serializer.Proto;
                case "kryo":
                    return Serializer.Kryo;
                default:
                    return null;
            }
        }
    }
}
