package bftsmart.serialization.proto;

import bftsmart.communication.SystemMessage;
import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.consensus.messages.MessageFactory;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.serialization.messages.TOMMessagePlain;
import bftsmart.tom.core.messages.TOMMessageType;
import com.google.protobuf.ByteString;

public class ProtoMessageMapper {
    static SystemMessage toInternal(ProtoMessages.SystemMessage msg) {
        int senderId = msg.getSenderId();

        switch (msg.getPayloadCase()) {
            case TOM_MSG:
                return toPlainTOMMessage(senderId, msg.getTomMsg());
            case VM_MSG:
                return VMMessageMapper.getInstance().fromProto(senderId, msg.getVmMsg());
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
            builder.setTomMsg(fromPlainTOMMessage((TOMMessagePlain) msg));
        } else if (msg instanceof VMMessage) {
            builder.setVmMsg(VMMessageMapper.getInstance().toProto((VMMessage) msg));
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

    private static TOMMessagePlain toPlainTOMMessage(int senderId, ProtoMessages.TOMMessage msg) {
        TOMMessagePlain plain = new TOMMessagePlain(senderId);
        plain.setViewID(msg.getViewId());
        plain.setType(TOMMessageType.getMessageType(msg.getTypeValue()));
        plain.setSession(msg.getSession());
        plain.setSequence(msg.getSequence());
        plain.setOperationId(msg.getOperationId());
        if (msg.getContent().toByteArray().length > 0) {
            plain.setContent(msg.getContent().toByteArray());
        }
        plain.setReplyServer(msg.getReplyServer());
        return plain;
    }

    private static ProtoMessages.TOMMessage fromPlainTOMMessage(TOMMessagePlain msg) {
        ProtoMessages.TOMMessage.Builder tomMessage =
                ProtoMessages.TOMMessage.newBuilder()
                        .setViewId(msg.getViewID())
                        .setTypeValue(msg.getType().ordinal())
                        .setSession(msg.getSession())
                        .setSequence(msg.getSequence())
                        .setOperationId(msg.getOperationId())
                        .setReplyServer(msg.getReplyServer());
        if (msg.getContent() != null) {
            tomMessage.setContent(ByteString.copyFrom(msg.getContent()));
        }
        return tomMessage.build();
    }
}
