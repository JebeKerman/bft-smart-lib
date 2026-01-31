package bftsmart.messages.bench;

import bftsmart.reconfiguration.ReconfigureReply;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.reconfiguration.views.View;
import bftsmart.serialization.messages.TOMMessagePlain;
import bftsmart.tom.core.messages.TOMMessageType;
import java.net.InetSocketAddress;

public class MessageProvider {
    public static TOMMessagePlain getTOMMessage() {
        TOMMessagePlain message = new TOMMessagePlain(1);
        message.setSession(2);
        message.setSequence(3);
        message.setOperationId(4);
        message.setContent(new byte[] {5, 6, 7});
        message.setViewID(8);
        message.setType(TOMMessageType.ASK_STATUS);
        message.setReplyServer(42);
        return message;
    }

    public static VMMessage getVMMessage() {
        InetSocketAddress[] addresses =
                new InetSocketAddress[] {
                    new InetSocketAddress("127.0.0.1", 1000),
                    new InetSocketAddress("127.0.0.1", 1001),
                    new InetSocketAddress("127.0.0.1", 1002),
                    new InetSocketAddress("127.0.0.1", 1003),
                };
        View view = new View(4, new int[] {5, 6, 7, 8}, 42, addresses);
        String[] joinSet = new String[] {"123", "hello world!"};
        ReconfigureReply reply = new ReconfigureReply(view, joinSet, 2, 3);
        return new VMMessage(1, reply);
    }
}
