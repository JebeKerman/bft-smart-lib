package bftsmart.serialization;

import bftsmart.serialization.proto.ProtoSerializer;

public class MessageSerializerFactory {
    private static MessageSerializer serializer = ProtoSerializer.getInstance();

    public static MessageSerializer getSerializer() {
        return serializer;
    }

    public static void useJavaSerializer() {
        useSerializer(JavaSerializer.getInstance());
    }

    public static void useProtoSerializer() {
        useSerializer(ProtoSerializer.getInstance());
    }

    public static void useSerializer(MessageSerializer serializer) {
        MessageSerializerFactory.serializer = serializer;
    }
}
