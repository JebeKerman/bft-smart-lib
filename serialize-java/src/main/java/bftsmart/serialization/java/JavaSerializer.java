package bftsmart.serialization.java;

import bftsmart.communication.SystemMessage;
import bftsmart.serialization.MessageSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class JavaSerializer implements MessageSerializer {

    private static final JavaSerializer instance = new JavaSerializer();

    public static JavaSerializer getInstance() {
        return instance;
    }

    @Override
    public void serialize(SystemMessage msg, OutputStream out) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(msg);
            out.flush();
        }
    }

    @Override
    public SystemMessage deserialize(InputStream in) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(in)) {
            SystemMessage msg = (SystemMessage) ois.readObject();
            return msg;
        }
    }

    private JavaSerializer() {}
}
