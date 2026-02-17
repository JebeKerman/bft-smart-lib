package bftsmart.serialization.proto;

import bftsmart.communication.SystemMessage;
import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.serialization.messages.TOMMessageWire;
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
                return ConsensusMessageMapper.getInstance()
                        .fromProto(senderId, msg.getConsensusMsg());
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
        }
        return builder.build();
    }
}
