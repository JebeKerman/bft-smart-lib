package bftsmart.serialization.proto;

import bftsmart.reconfiguration.views.View;
import bftsmart.statemanagement.standard.StandardSMMessageWire;
import com.google.protobuf.ByteString;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

class StandardSMMessageMapper
        implements MessageMapper<StandardSMMessageWire<?>, ProtoMessages.StandardSMMMessage> {
    private static final StandardSMMessageMapper instance = new StandardSMMessageMapper();

    static StandardSMMessageMapper getInstance() {
        return instance;
    }

    @Override
    public StandardSMMessageWire<?> fromProto(int senderId, ProtoMessages.StandardSMMMessage msg) {
        View view = null;
        if (msg.getParent().hasView()) {
            view = MapperUtil.viewFromProto(msg.getParent().getView());
        }

        Serializable state = null;
        byte[] stateBytes = msg.getParent().getState().toByteArray();
        try (ByteArrayInputStream is = new ByteArrayInputStream(stateBytes);
                ObjectInputStream ois = new ObjectInputStream(is); ) {
            state = (Serializable) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        StandardSMMessageWire<?> result =
                new StandardSMMessageWire<>(
                        senderId,
                        msg.getParent().getCid(),
                        msg.getParent().getType(),
                        msg.getReplica(),
                        state,
                        view,
                        msg.getParent().getRegency(),
                        msg.getParent().getLeader(),
                        msg.getParent().getTriggerSmLocally());
        return result;
    }

    @Override
    public ProtoMessages.StandardSMMMessage toProto(StandardSMMessageWire<?> msg) {

        byte[] state = null;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os); ) {
            oos.writeObject(msg.getState());
            state = os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProtoMessages.SMMessage.Builder parent =
                ProtoMessages.SMMessage.newBuilder()
                        .setCid(msg.getCID())
                        .setType(msg.getType())
                        .setRegency(msg.getRegency())
                        .setLeader(msg.getLeader())
                        .setTriggerSmLocally(msg.TRIGGER_SM_LOCALLY)
                        .setState(ByteString.copyFrom(state));

        if (msg.getView() != null) {
            ProtoMessages.View view = MapperUtil.viewToProto(msg.getView());
            parent = parent.setView(view);
        } else {
            parent = parent.clearView();
        }

        ProtoMessages.StandardSMMMessage.Builder protoMessage =
                ProtoMessages.StandardSMMMessage.newBuilder()
                        .setParent(parent)
                        .setReplica(msg.getReplica());

        return protoMessage.build();
    }

    private StandardSMMessageMapper() {}
}
