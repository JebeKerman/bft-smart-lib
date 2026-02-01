package bftsmart.serialization.proto;

import bftsmart.serialization.messages.TOMMessagePlain;
import bftsmart.tom.core.messages.TOMMessageType;
import com.google.protobuf.ByteString;

class TOMMessageMapper implements MessageMapper<TOMMessagePlain, ProtoMessages.TOMMessage> {
    private static final TOMMessageMapper instance = new TOMMessageMapper();

    static TOMMessageMapper getInstance() {
        return instance;
    }

    @Override
    public TOMMessagePlain fromProto(int senderId, ProtoMessages.TOMMessage msg) {
        TOMMessageType type = TOMMessageType.getMessageType(msg.getTypeValue());

        byte[] content = null;
        if (msg.getContent().toByteArray().length > 0) {
            content = msg.getContent().toByteArray();
        }

        TOMMessagePlain plain =
                new TOMMessagePlain(
                        senderId,
                        msg.getSession(),
                        msg.getSequence(),
                        msg.getOperationId(),
                        content,
                        msg.getViewId(),
                        type);
        plain.setReplyServer(msg.getReplyServer());

        return plain;
    }

    @Override
    public ProtoMessages.TOMMessage toProto(TOMMessagePlain msg) {
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

    private TOMMessageMapper() {}
}
