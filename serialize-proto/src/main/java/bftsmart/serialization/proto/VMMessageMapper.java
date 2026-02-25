package bftsmart.serialization.proto;

import bftsmart.reconfiguration.ReconfigureReply;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.reconfiguration.views.View;

class VMMessageMapper implements MessageMapper<VMMessage, ProtoMessages.VMMessage> {
    private static final VMMessageMapper instance = new VMMessageMapper();

    static VMMessageMapper getInstance() {
        return instance;
    }

    @Override
    public VMMessage fromProto(int senderId, ProtoMessages.VMMessage protoMsg) {
        ProtoMessages.ReconfigureReply protoReply = protoMsg.getReply();

        String[] joinSet = null;
        if (protoReply.getJoinSetCount() > 0) {
            joinSet = protoReply.getJoinSetList().toArray(new String[0]);
        }

        View view = MapperUtil.viewFromProto(protoReply.getView());

        ReconfigureReply reply =
                new ReconfigureReply(
                        view,
                        joinSet,
                        protoReply.getLastExecConsId(),
                        protoMsg.getReply().getExecLeader());

        return new VMMessage(senderId, reply);
    }

    @Override
    public ProtoMessages.VMMessage toProto(VMMessage internalMsg) {
        ProtoMessages.View view = MapperUtil.viewToProto(internalMsg.getReply().getView());

        ProtoMessages.ReconfigureReply.Builder replyBuilder =
                ProtoMessages.ReconfigureReply.newBuilder().setView(view);

        for (String join : internalMsg.getReply().getJoinSet()) {
            replyBuilder.addJoinSet(join);
        }
        replyBuilder.setLastExecConsId(internalMsg.getReply().getLastExecConsId());
        replyBuilder.setExecLeader(internalMsg.getReply().getExecLeader());

        return ProtoMessages.VMMessage.newBuilder().setReply(replyBuilder).build();
    }

    private VMMessageMapper() {}
}
