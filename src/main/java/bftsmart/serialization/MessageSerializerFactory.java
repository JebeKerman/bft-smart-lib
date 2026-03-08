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

    private static MessageSerializer serializer;
    static {
        String type = System.getProperty(PROPERTY, DEFAULT_SERIALIZER);
        Serializer serializerType = Serializer.fromName(type);
        serializer = serializerType.serializer;
        if (serializerType == Serializer.Kryo) {
            KryoSerializer.getInstance().register(StandardSMMessage.class);
            KryoSerializer.getInstance().register(CSTSMMessage.class);
            KryoSerializer.getInstance().register(TOMMessage.class);
            KryoSerializer.getInstance().register(LCMessage.class);
            KryoSerializer.getInstance().register(DefaultApplicationState.class);
            KryoSerializer.getInstance().register(TreeMap.class);
        }
    }

    public static MessageSerializer getSerializer() {
        return serializer;
    }

    private MessageSerializerFactory() { }

    private static enum Serializer {
        Java(JavaSerializer.getInstance()),
        Proto(ProtoSerializer.getInstance()),
        Kryo(KryoSerializer.getInstance());

        final MessageSerializer serializer;

        private Serializer(final MessageSerializer serializer) {
            this.serializer = serializer;
        } 

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
