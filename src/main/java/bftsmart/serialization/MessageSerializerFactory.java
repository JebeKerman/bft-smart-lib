package bftsmart.serialization;

import bftsmart.serialization.java.JavaSerializer;
import bftsmart.serialization.proto.ProtoSerializer;
import bftsmart.serialization.kryo.KryoSerializer;

public class MessageSerializerFactory {
    private static final String PROPERTY = "serialization.framework";
    private static final String DEFAULT_SERIALIZER = "java";

    private static MessageSerializer serializer;
    static {
        String type = System.getProperty(PROPERTY, DEFAULT_SERIALIZER);
        serializer = Serializer.fromName(type).serializer;
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
