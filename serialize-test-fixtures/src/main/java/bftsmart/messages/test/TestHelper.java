package bftsmart.messages.test;

import bftsmart.communication.SystemMessage;
import bftsmart.serialization.MessageSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestHelper {
    public static byte[] toBytes(MessageSerializer serializer, SystemMessage msg)
            throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(msg, outputStream);
        return outputStream.toByteArray();
    }

    public static <T extends SystemMessage> T fromBytes(
            MessageSerializer serializer, byte[] bytes, Class<T> clazz)
            throws IOException, ClassNotFoundException {
        SystemMessage msg = serializer.deserialize(new ByteArrayInputStream(bytes));
        if (clazz.isInstance(msg)) {
            return clazz.cast(msg);
        } else {
            return null;
        }
    }

    private TestHelper() {}
}
