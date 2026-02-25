package bftsmart.serialization.proto;

import bftsmart.communication.SystemMessage;
import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.serialization.messages.TOMMessageWire;
import bftsmart.statemanagement.durability.CSTSMMessageWire;
import bftsmart.statemanagement.standard.StandardSMMessageWire;
import bftsmart.tom.core.messages.ForwardedMessage;
import bftsmart.tom.leaderchange.LCMessageWire;
import java.io.Serializable;

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
                return ConsensusMessageMapper.getInstance()
                        .fromProto(senderId, msg.getConsensusMsg());
            case FORWARDED_MESSAGE:
                return ForwardedMessageMapper.getInstance()
                        .fromProto(senderId, msg.getForwardedMessage());
            case CSTSM_MESSAGE:
                return CSTSMessageMessageMapper.getInstance()
                        .fromProto(senderId, msg.getCstsmMessage());
            case STANDARD_SM_MESSAGE:
                return StandardSMMessageMapper.getInstance()
                        .fromProto(senderId, msg.getStandardSmMessage());
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
        if (msg instanceof TOMMessageWire) {
            builder.setTomMsg(TOMMessageMapper.getInstance().toProto((TOMMessageWire) msg));
        } else if (msg instanceof VMMessage) {
            builder.setVmMsg(VMMessageMapper.getInstance().toProto((VMMessage) msg));
        } else if (msg instanceof LCMessageWire) {
            builder.setLcMsg(LCMessageMapper.getInstance().toProto((LCMessageWire) msg));
        } else if (msg instanceof ConsensusMessage) {
            builder.setConsensusMsg(
                    ConsensusMessageMapper.getInstance().toProto((ConsensusMessage) msg));
        } else if (msg instanceof ForwardedMessage) {
            builder.setForwardedMessage(
                    ForwardedMessageMapper.getInstance().toProto((ForwardedMessage) msg));
        } else if (msg instanceof CSTSMMessageWire) {
            builder.setCstsmMessage(
                    CSTSMessageMessageMapper.getInstance()
                            .toProto((CSTSMMessageWire<? extends Serializable>) msg));
        } else if (msg instanceof StandardSMMessageWire) {
            builder.setStandardSmMessage(
                    StandardSMMessageMapper.getInstance()
                            .toProto((StandardSMMessageWire<? extends Serializable>) msg));
        }
        return builder.build();
    }
}
