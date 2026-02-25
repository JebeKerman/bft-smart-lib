package bftsmart.messages.bench;

import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.consensus.messages.MessageFactory;
import bftsmart.reconfiguration.ReconfigureReply;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.reconfiguration.views.View;
import bftsmart.serialization.messages.TOMMessageWire;
import bftsmart.statemanagement.durability.CSTRequestF1;
import bftsmart.statemanagement.durability.CSTSMMessageWire;
import bftsmart.statemanagement.standard.StandardSMMessageWire;
import bftsmart.tom.core.messages.TOMMessageType;
import bftsmart.tom.leaderchange.LCMessageWire;
import java.io.Serializable;
import java.net.InetSocketAddress;

public class MessageProvider {

    public static ConsensusMessage getConsensusMessage() {
        return new ConsensusMessage(MessageFactory.PROPOSE, 0, 0, 0, new byte[] {0});
    }

    public static CSTSMMessageWire<? extends Serializable> getCSTSMMessageMinimal() {
        return new CSTSMMessageWire<>(
                1,
                2,
                3,
                new CSTRequestF1(4),
                5,
                new View(6, new int[0], 8, new InetSocketAddress[0]),
                9,
                10,
                false);
    }

    public static LCMessageWire getLCMessage() {
        return new LCMessageWire(1, 2, 3, new byte[] {1, 2, 3}, true);
    }

    public static StandardSMMessageWire<? extends Serializable> getStandardSMMessageMinimal() {
        return new StandardSMMessageWire<>(
                1, 2, 3, 4, 5, new View(6, new int[0], 8, new InetSocketAddress[0]), 9, 10, false);
    }

    public static TOMMessageWire getTOMMessage() {
        TOMMessageWire message = new TOMMessageWire(1);
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
