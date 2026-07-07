package bftsmart.serialization.proto;

import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.consensus.messages.MessageFactory;
import com.google.protobuf.ByteString;

class ConsensusMessageMapper
        implements MessageMapper<ConsensusMessage, ProtoMessages.ConsensusMessage> {
    private static final ConsensusMessageMapper instance = new ConsensusMessageMapper();

    static ConsensusMessageMapper getInstance() {
        return instance;
    }

    @Override
    public ConsensusMessage fromProto(int senderId, ProtoMessages.ConsensusMessage msg) {
        byte[] value = null;
        if (msg.getValue().toByteArray().length > 0) {
            value = msg.getValue().toByteArray();
        }
        int type = convertPaxosTypeToInternRepresentation(msg.getType());
        ConsensusMessage message =
                new ConsensusMessage(type, msg.getNumber(), msg.getEpoch(), senderId, value);
        return message;
    }

    @Override
    public ProtoMessages.ConsensusMessage toProto(ConsensusMessage msg) {
        ProtoMessages.ConsensusMessage.Builder protoMessage =
                ProtoMessages.ConsensusMessage.newBuilder()
                        .setNumber(msg.getNumber())
                        .setEpoch(msg.getEpoch())
                        .setType(convertPaxosTypeToProtoRepresentation(msg.getType()));
        if (msg.getValue() != null) {
            protoMessage.setValue(ByteString.copyFrom(msg.getValue()));
        }
        return protoMessage.build();
    }

    private static int convertPaxosTypeToInternRepresentation(ProtoMessages.PaxosType type) {
        switch (type) {
            case PROPOSE:
                return MessageFactory.PROPOSE;
            case WRITE:
                return MessageFactory.WRITE;
            case ACCEPT:
                return MessageFactory.ACCEPT;
            case REQ_DECISION:
                return MessageFactory.REQ_DECISION;
            case FWD_DECISION:
                return MessageFactory.FWD_DECISION;
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    private static ProtoMessages.PaxosType convertPaxosTypeToProtoRepresentation(int type) {
        switch (type) {
            case MessageFactory.PROPOSE:
                return ProtoMessages.PaxosType.PROPOSE;
            case MessageFactory.WRITE:
                return ProtoMessages.PaxosType.WRITE;
            case MessageFactory.ACCEPT:
                return ProtoMessages.PaxosType.ACCEPT;
            case MessageFactory.REQ_DECISION:
                return ProtoMessages.PaxosType.REQ_DECISION;
            case MessageFactory.FWD_DECISION:
                return ProtoMessages.PaxosType.FWD_DECISION;
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    private ConsensusMessageMapper() {}
}
