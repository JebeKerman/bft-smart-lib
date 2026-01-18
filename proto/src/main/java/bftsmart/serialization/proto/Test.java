package bftsmart.serialization.proto;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import com.google.protobuf.ByteString;
import bftsmart.serialization.proto.ProtoMessages.ConsensusMessage;
import bftsmart.serialization.proto.ProtoMessages.PaxosType;
import bftsmart.serialization.proto.ProtoMessages.SystemMessage;
import bftsmart.serialization.proto.ProtoMessages.TOMMessage;
import bftsmart.serialization.proto.ProtoMessages.TOMMessageType;

public class Test {
  public static void main(String[] args) {
    System.out.println("Starting simple proto test");


    FileOutputStream output;
    try {
      output = new FileOutputStream("test.bin");
      SystemMessage msg = createTOMMessage();
      msg.writeDelimitedTo(output);
      msg = createConsenusMsg();
      msg.writeDelimitedTo(output);
      output.close();


      SystemMessage receivedMsg;
      try (FileInputStream input = new FileInputStream("test.bin")) {
        while ((receivedMsg = SystemMessage.parseDelimitedFrom(input)) != null) {
          switch (receivedMsg.getPayloadCase()) {
            case CONSENSUS_MSG:
              ConsensusMessage consensusMessage = receivedMsg.getConsensusMsg();
              System.out
                  .println("Received ConsensusMessage from sender: " + receivedMsg.getSenderId());
              System.out.println(consensusMessage);
              break;
            case TOM_MSG:
              TOMMessage tomlMessage = receivedMsg.getTomMsg();
              System.out.println("Received TOMMessage from sender: " + receivedMsg.getSenderId());
              System.out.println(tomlMessage);
              break;
            case PAYLOAD_NOT_SET:
              System.err.println("Not good!");
            default:
              break;
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static SystemMessage createConsenusMsg() {
    ConsensusMessage.Builder msg = ConsensusMessage.newBuilder().setNumber(2).setEpoch(3)
        .setType(PaxosType.PROPOSE).setValue(ByteString.copyFrom(new byte[] {1, 2, 3, 4}));

    return SystemMessage.newBuilder().setSenderId(1).setConsensusMsg(msg).build();
  }

  private static SystemMessage createTOMMessage() {
    TOMMessage.Builder message =
        TOMMessage.newBuilder().setViewId(2).setType(TOMMessageType.ORDERED_REQUEST).setSession(3)
            .setSequence(4).setOperationId(5).setReplyServer(6).setContent("Hello world!");

    return SystemMessage.newBuilder().setSenderId(1).setTomMsg(message).build();
  }
}
