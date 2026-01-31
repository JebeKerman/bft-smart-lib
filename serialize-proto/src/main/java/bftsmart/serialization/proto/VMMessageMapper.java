package bftsmart.serialization.proto;

import bftsmart.reconfiguration.ReconfigureReply;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.reconfiguration.views.View;
import java.net.InetSocketAddress;
import java.util.Map;

public class VMMessageMapper implements MessageMapper<VMMessage, ProtoMessages.VMMessage> {
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

        Map<Integer, ProtoMessages.SocketAddress> addressesMap =
                protoReply.getView().getAddressesMap();
        int[] processes = new int[addressesMap.size()];
        InetSocketAddress[] addresses = new InetSocketAddress[addressesMap.size()];

        int index = 0;
        for (Map.Entry<Integer, ProtoMessages.SocketAddress> el : addressesMap.entrySet()) {
            processes[index] = el.getKey();
            addresses[index] =
                    new InetSocketAddress(el.getValue().getHost(), el.getValue().getPort());
            index++;
        }

        View view =
                new View(
                        protoReply.getView().getId(),
                        processes,
                        protoReply.getView().getF(),
                        addresses);

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
        ProtoMessages.View.Builder viewBuilder = ProtoMessages.View.newBuilder();
        viewBuilder.setId(internalMsg.getReply().getView().getId());
        viewBuilder.setF(internalMsg.getReply().getView().getF());
        for (int process : internalMsg.getReply().getView().getProcesses()) {
            viewBuilder.addProcesses(process);
            InetSocketAddress address = internalMsg.getReply().getView().getAddress(process);

            viewBuilder.putAddresses(
                    process,
                    ProtoMessages.SocketAddress.newBuilder()
                            .setHost(address.getHostName())
                            .setPort(address.getPort())
                            .build());
        }

        ProtoMessages.ReconfigureReply.Builder replyBuilder =
                ProtoMessages.ReconfigureReply.newBuilder();
        replyBuilder.setView(viewBuilder);
        for (String join : internalMsg.getReply().getJoinSet()) {
            replyBuilder.addJoinSet(join);
        }
        replyBuilder.setLastExecConsId(internalMsg.getReply().getLastExecConsId());
        replyBuilder.setExecLeader(internalMsg.getReply().getExecLeader());

        return ProtoMessages.VMMessage.newBuilder().setReply(replyBuilder).build();
    }

    private VMMessageMapper() {}
}
