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
    public void serialize(SystemMessage msg, OutputStream out) throws IOException {
        ProtoMessageMapper.fromInternal(msg).writeDelimitedTo(out);
    }

    @Override
    public SystemMessage deserialize(InputStream in) throws IOException, ClassNotFoundException {
        ProtoMessages.SystemMessage result = ProtoMessages.SystemMessage.parseDelimitedFrom(in);
        SystemMessage internal = ProtoMessageMapper.toInternal(result);
        return internal;
    }

    private ProtoSerializer() {}
}
