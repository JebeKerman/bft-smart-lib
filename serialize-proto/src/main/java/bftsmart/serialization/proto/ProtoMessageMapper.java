package bftsmart.serialization.proto;

import bftsmart.communication.SystemMessage;
import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.consensus.messages.MessageFactory;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.serialization.messages.TOMMessagePlain;
import bftsmart.tom.leaderchange.LCMessageWire;

class ProtoMessageMapper {
    static SystemMessage toInternal(ProtoMessages.SystemMessage msg) {
        int senderId = msg.getSenderId();

        switch (msg.getPayloadCase()) {
            case TOM_MSG:
                return TOMMessageMapper.getInstance().fromProto(senderId, msg.getTomMsg());
            case VM_MSG:
                return VMMessageMapper.getInstance().fromProto(senderId, msg.getVmMsg());
            case LC_MSG:
                return LCMessageMapper.getInstance().fromProto(senderId, msg.getLcMsg());
            case CONSENSUS_MSG:
                return toConsensusMessage(senderId, msg.getConsensusMsg());
            case PAYLOAD_NOT_SET:
                break;
            default:
                break;
        }
        return null;
    }

    static ProtoMessages.SystemMessage fromInternal(SystemMessage msg) {
        ProtoMessages.SystemMessage.Builder builder =
                ProtoMessages.SystemMessage.newBuilder().setSenderId(msg.getSender());
        if (msg instanceof TOMMessagePlain) {
            builder.setTomMsg(TOMMessageMapper.getInstance().toProto((TOMMessagePlain) msg));
        } else if (msg instanceof VMMessage) {
            builder.setVmMsg(VMMessageMapper.getInstance().toProto((VMMessage) msg));
        } else if (msg instanceof LCMessageWire) {
            builder.setLcMsg(LCMessageMapper.getInstance().toProto((LCMessageWire) msg));
        } else {
            return null;
        }
        return builder.build();
    }

    private static ConsensusMessage toConsensusMessage(
            int senderId, ProtoMessages.ConsensusMessage msg) {
        MessageFactory factory = new MessageFactory(senderId);
        switch (msg.getType()) {
            case PROPOSE:
                return factory.createPropose(
                        msg.getNumber(), msg.getEpoch(), msg.getValue().toByteArray());
            case WRITE:
                return factory.createWrite(
                        msg.getNumber(), msg.getEpoch(), msg.getValue().toByteArray());
            case ACCEPT:
                return factory.createAccept(
                        msg.getNumber(), msg.getEpoch(), msg.getValue().toByteArray());
            case REQ_DECISION:
                return factory.createRequestDecision(
                        msg.getNumber(), msg.getEpoch(), msg.getValue().toByteArray());
            case FWD_DECISION:
                return factory.createForwardDecision(
                        msg.getNumber(), msg.getEpoch(), msg.getValue().toByteArray());
            default:
                break;
        }
        return null;
    }
}
