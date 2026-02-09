package bftsmart.serialization.proto;

import bftsmart.tom.leaderchange.LCMessageWire;
import com.google.protobuf.ByteString;

class LCMessageMapper implements MessageMapper<LCMessageWire, ProtoMessages.LCMessage> {
    private static final LCMessageMapper instance = new LCMessageMapper();

    static LCMessageMapper getInstance() {
        return instance;
    }

    @Override
    public LCMessageWire fromProto(int senderId, ProtoMessages.LCMessage msg) {
        byte[] payload = null;
        if (msg.getPayload().toByteArray().length > 0) {
            payload = msg.getPayload().toByteArray();
        }
        LCMessageWire wire =
                new LCMessageWire(senderId, msg.getType(), msg.getTs(), payload, false);
        return wire;
    }

    @Override
    public ProtoMessages.LCMessage toProto(LCMessageWire msg) {
        ProtoMessages.LCMessage.Builder protoMessage =
                ProtoMessages.LCMessage.newBuilder()
                        .setType(msg.getType())
                        .setTs(msg.getReg())
                        .setTriggerLcLocally(false);
        if (msg.getPayload() != null) {
            protoMessage.setPayload(ByteString.copyFrom(msg.getPayload()));
        }
        return protoMessage.build();
    }

    private LCMessageMapper() {}
}
