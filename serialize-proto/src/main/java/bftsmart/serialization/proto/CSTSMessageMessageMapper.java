package bftsmart.serialization.proto;

import bftsmart.reconfiguration.views.View;
import bftsmart.statemanagement.durability.CSTRequestF1;
import bftsmart.statemanagement.durability.CSTSMMessageWire;
import com.google.protobuf.ByteString;

class CSTSMessageMessageMapper
        implements MessageMapper<CSTSMMessageWire<?>, ProtoMessages.CSTSMMessage> {
    private static final CSTSMessageMessageMapper instance = new CSTSMessageMessageMapper();

    static CSTSMessageMessageMapper getInstance() {
        return instance;
    }

    @Override
    public CSTSMMessageWire<?> fromProto(int senderId, ProtoMessages.CSTSMMessage msg) {
        CSTRequestF1 config = new CSTRequestF1(msg.getCstConfig().getCid());
        View view = MapperUtil.viewFromProto(msg.getParent().getView());

        byte[] stateBytes = msg.getParent().getState().toByteArray();

        CSTSMMessageWire<?> result =
                new CSTSMMessageWire<>(
                        senderId,
                        msg.getParent().getCid(),
                        msg.getParent().getType(),
                        config,
                        stateBytes,
                        view,
                        msg.getParent().getRegency(),
                        msg.getParent().getLeader(),
                        msg.getParent().getTriggerSmLocally());
        return result;
    }

    @Override
    public ProtoMessages.CSTSMMessage toProto(CSTSMMessageWire<?> msg) {
        ProtoMessages.CSTRequestF1 config =
                ProtoMessages.CSTRequestF1.newBuilder().setCid(msg.getCstConfig().getCID()).build();

        ProtoMessages.View view = MapperUtil.viewToProto(msg.getView());

        byte[] state = msg.getSerializedState();

        ProtoMessages.SMMessage parent =
                ProtoMessages.SMMessage.newBuilder()
                        .setView(view)
                        .setCid(msg.getCID())
                        .setType(msg.getType())
                        .setRegency(msg.getRegency())
                        .setLeader(msg.getLeader())
                        .setTriggerSmLocally(msg.TRIGGER_SM_LOCALLY)
                        .setState(ByteString.copyFrom(state))
                        .build();

        ProtoMessages.CSTSMMessage.Builder protoMessage =
                ProtoMessages.CSTSMMessage.newBuilder().setParent(parent).setCstConfig(config);

        return protoMessage.build();
    }

    private CSTSMessageMessageMapper() {}
}
