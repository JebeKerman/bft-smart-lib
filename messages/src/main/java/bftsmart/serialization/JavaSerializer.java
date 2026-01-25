package bftsmart.serialization;

import bftsmart.communication.SystemMessage;
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
    public <T extends SystemMessage> void serialize(T msg, OutputStream out) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
            msg.writeExternal(oos);
        }
    }

    @Override
    public <T extends SystemMessage> T deserialize(InputStream in, Class<T> clazz)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(in)) {
            T msg = clazz.getDeclaredConstructor().newInstance();
            msg.readExternal(ois);
            return msg;
        } catch (ReflectiveOperationException e) {
            throw new IOException("Failed to instantiate " + clazz.getName(), e);
        }
    }

    private JavaSerializer() {}
}
