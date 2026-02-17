package bftsmart.serialization.proto;

import bftsmart.serialization.messages.TOMMessageWire;
import bftsmart.tom.core.messages.ForwardedMessage;
import com.google.protobuf.ByteString;

class ForwardedMessageMapper
        implements MessageMapper<ForwardedMessage, ProtoMessages.ForwardedMessage> {
    private static final ForwardedMessageMapper instance = new ForwardedMessageMapper();

    static ForwardedMessageMapper getInstance() {
        return instance;
    }

    @Override
    public ForwardedMessage fromProto(int senderId, ProtoMessages.ForwardedMessage msg) {
        byte[] signature = null;
        if (msg.getSignature().toByteArray().length > 0) {
            signature = msg.getSignature().toByteArray();
        }
        TOMMessageWire request =
                TOMMessageMapper.getInstance().fromProto(msg.getRequestSender(), msg.getRequest());
        request.signed = signature != null;
        request.serializedMessageSignature = signature;
        ForwardedMessage message = new ForwardedMessage(senderId, request);

        return message;
    }

    @Override
    public ProtoMessages.ForwardedMessage toProto(ForwardedMessage msg) {
        ProtoMessages.TOMMessage protoRequest =
                TOMMessageMapper.getInstance().toProto(msg.getRequest());
        ProtoMessages.ForwardedMessage.Builder message =
                ProtoMessages.ForwardedMessage.newBuilder()
                        .setRequestSender(msg.getRequest().getSender())
                        .setRequest(protoRequest);
        if (msg.getRequest().signed) {
            message.setSignature(ByteString.copyFrom(msg.getRequest().serializedMessageSignature));
        }
        return message.build();
    }

    private ForwardedMessageMapper() {}
}
