package bftsmart.serialization;

import bftsmart.communication.SystemMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface MessageSerializer {
    void serialize(SystemMessage msg, OutputStream out) throws IOException;

    SystemMessage deserialize(InputStream in) throws IOException, ClassNotFoundException;
}
