package bftsmart.serialization;

import bftsmart.communication.SystemMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface MessageSerializer {
    <T extends SystemMessage> void serialize(T msg, OutputStream out) throws IOException;

    <T extends SystemMessage> T deserialize(InputStream in, Class<T> clazz)
            throws IOException, ClassNotFoundException;
}
