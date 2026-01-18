package bftsmart.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import bftsmart.communication.SystemMessage;

public interface MessageSerializer {
  void serialize(SystemMessage msg, OutputStream out) throws IOException;

  SystemMessage deserialize(InputStream in) throws IOException;
}
