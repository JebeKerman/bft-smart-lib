package bftsmart.serialization.proto;

import bftsmart.communication.SystemMessage;
import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.consensus.messages.MessageFactory;

public class ProtoMessageMapper {
  static SystemMessage toInternal(ProtoMessages.SystemMessage msg) {
    int senderId = msg.getSenderId();

    switch (msg.getPayloadCase()) {
      case CONSENSUS_MSG:
        return toConsensusMessage(senderId, msg.getConsensusMsg());
      case TOM_MSG:
        break;
      case PAYLOAD_NOT_SET:
        break;
      default:
        break;
    }
    return null;
  }

  static ProtoMessages.SystemMessage fromInternal(SystemMessage msg) {
    return null;
  }

  private static ConsensusMessage toConsensusMessage(int senderId, ProtoMessages.ConsensusMessage msg) {
    MessageFactory factory = new MessageFactory(senderId);
    switch (msg.getType()) {
      case PROPOSE:
        return factory.createPropose(msg.getNumber(), msg.getEpoch(), msg.getValue().toByteArray());
      case WRITE:
        return factory.createWrite(msg.getNumber(), msg.getEpoch(), msg.getValue().toByteArray());
        case ACCEPT:
        return factory.createAccept(msg.getNumber(), msg.getEpoch(), msg.getValue().toByteArray());
      case REQ_DECISION:
        return factory.createRequestDecision(msg.getNumber(), msg.getEpoch(), msg.getValue().toByteArray());
      case FWD_DECISION:
        return factory.createForwardDecision(msg.getNumber(), msg.getEpoch(), msg.getValue().toByteArray());
      default:
        break;
    }
    return null;
  }
}
