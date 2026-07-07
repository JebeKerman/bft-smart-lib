package bftsmart.serialization.proto;

import bftsmart.reconfiguration.views.View;
import bftsmart.statemanagement.durability.CSTRequestF1;
import bftsmart.statemanagement.durability.CSTSMMessageWire;
import com.google.protobuf.ByteString;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

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

        Serializable state = null;
        byte[] stateBytes = msg.getParent().getState().toByteArray();
        try (ByteArrayInputStream is = new ByteArrayInputStream(stateBytes);
                ObjectInputStream ois = new ObjectInputStream(is); ) {
            state = (Serializable) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        CSTSMMessageWire<?> result =
                new CSTSMMessageWire<>(
                        senderId,
                        msg.getParent().getCid(),
                        msg.getParent().getType(),
                        config,
                        state,
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

        byte[] state = null;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os); ) {
            oos.writeObject(msg.getState());
            state = os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
