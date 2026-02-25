package bftsmart.serialization.proto;

import bftsmart.reconfiguration.views.View;
import java.net.InetSocketAddress;
import java.util.Map;

class MapperUtil {
    public static View viewFromProto(ProtoMessages.View msg) {
        Map<Integer, ProtoMessages.SocketAddress> addressesMap = msg.getAddressesMap();
        int[] processes = new int[addressesMap.size()];
        InetSocketAddress[] addresses = new InetSocketAddress[addressesMap.size()];

        int index = 0;
        for (Map.Entry<Integer, ProtoMessages.SocketAddress> el : addressesMap.entrySet()) {
            processes[index] = el.getKey();
            addresses[index] =
                    new InetSocketAddress(el.getValue().getHost(), el.getValue().getPort());
            index++;
        }
        return new View(msg.getId(), processes, msg.getF(), addresses);
    }

    public static ProtoMessages.View viewToProto(View msg) {
        if (msg == null) {
            return null;
        }
        ProtoMessages.View.Builder viewBuilder = ProtoMessages.View.newBuilder();
        viewBuilder.setId(msg.getId());
        viewBuilder.setF(msg.getF());
        for (int process : msg.getProcesses()) {
            viewBuilder.addProcesses(process);
            InetSocketAddress address = msg.getAddress(process);

            viewBuilder.putAddresses(
                    process,
                    ProtoMessages.SocketAddress.newBuilder()
                            .setHost(address.getHostName())
                            .setPort(address.getPort())
                            .build());
        }
        return viewBuilder.build();
    }

    private MapperUtil() {}
}
