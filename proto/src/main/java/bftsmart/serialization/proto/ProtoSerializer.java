package bftsmart.serialization.proto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import bftsmart.communication.SystemMessage;
import bftsmart.serialization.MessageSerializer;

public class ProtoSerializer implements MessageSerializer {

  @Override
  public void serialize(SystemMessage msg, OutputStream out) throws IOException {
    ProtoMessageMapper.fromInternal(msg).writeDelimitedTo(out);
  }

  @Override
  public SystemMessage deserialize(InputStream in) throws IOException {
    ProtoMessages.SystemMessage msg = ProtoMessages.SystemMessage.parseDelimitedFrom(in);
    return ProtoMessageMapper.toInternal(msg);
  }
}