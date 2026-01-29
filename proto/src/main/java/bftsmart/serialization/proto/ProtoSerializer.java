package bftsmart.serialization.proto;

import bftsmart.communication.SystemMessage;
import bftsmart.serialization.MessageSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProtoSerializer implements MessageSerializer {

    private static final ProtoSerializer instance = new ProtoSerializer();

    public static ProtoSerializer getInstance() {
        return instance;
    }

    @Override
    public <T extends SystemMessage> void serialize(T msg, OutputStream out) throws IOException {
        ProtoMessageMapper.fromInternal(msg).writeDelimitedTo(out);
    }

    @Override
    public <T extends SystemMessage> T deserialize(InputStream in, Class<T> clazz)
            throws IOException, ClassNotFoundException {
        ProtoMessages.SystemMessage result = ProtoMessages.SystemMessage.parseDelimitedFrom(in);
        SystemMessage internal = ProtoMessageMapper.toInternal(result);
        if (clazz.isInstance(internal)) {
            return clazz.cast(internal);
        } else {
            throw new ClassCastException(
                    "Expected " + clazz.getName() + " but got " + internal.getClass().getName());
        }
    }

    private ProtoSerializer() {}
}
