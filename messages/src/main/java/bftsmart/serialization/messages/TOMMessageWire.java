package bftsmart.serialization.messages;

import bftsmart.communication.SystemMessage;
import bftsmart.tom.core.messages.TOMMessageType;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TOMMessageWire extends SystemMessage {
    protected int viewID; // current sender view
    protected TOMMessageType type; // request type: application or reconfiguration request

    protected int session; // Sequence number defined by the client
    // Sequence number defined by the client.
    // There is a sequence number for ordered and anothre for unordered messages
    protected int sequence;
    protected int operationId; // Sequence number defined by the client

    protected byte[] content = null; // Content of the message

    protected int replyServer = -1;

    // the fields bellow are not serialized!!!
    public transient long timestamp = 0; // timestamp to be used by the application

    public transient long seed = 0; // seed for the nonces
    public transient int numOfNonces = 0; // number of nonces

    public transient int destination = -1; // message destination
    public transient boolean signed = false; // is this message signed?

    public transient long receptionTime; // the reception time of this message (nanoseconds)
    public transient long
            receptionTimestamp; // the reception timestamp of this message (miliseconds)

    public transient boolean timeout = false; // this message was timed out?

    public transient boolean recvFromClient =
            false; // Did the client already sent this message to me, or did it arrived in the
    // batch?
    public transient boolean isValid = false; // Was this request already validated by the replica?

    // the bytes received from the client and its MAC and signature
    public transient byte[] serializedMessage = null;
    public transient byte[] serializedMessageSignature = null;
    public transient byte[] serializedMessageMAC = null;

    // for benchmarking purposes
    public transient long consensusStartTime = 0; // time the consensus is created
    public transient long proposeReceivedTime = 0; // time the propose is received
    public transient long writeSentTime = 0; // time the replica' write message is sent
    public transient long acceptSentTime = 0; // time the replica' accept message is sent
    public transient long decisionTime = 0; // time the decision is established
    public transient long deliveryTime = 0; // time the request is delivered
    public transient long executedTime = 0; // time the request is executed

    public TOMMessageWire() {
        super();
    }

    public TOMMessageWire(int sender) {
        super(sender);
    }

    public TOMMessageWire(
            int sender,
            int session,
            int sequence,
            int operationId,
            byte[] content,
            int view,
            TOMMessageType type) {
        super(sender);

        this.session = session;
        this.sequence = sequence;
        this.operationId = operationId;
        this.viewID = view;
        this.content = content;
        this.type = type;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(viewID);
        out.writeByte(type.ordinal());
        out.writeInt(session);
        out.writeInt(sequence);
        out.writeInt(operationId);
        out.writeInt(replyServer);

        if (content == null) {
            out.writeInt(-1);
        } else {
            out.writeInt(content.length);
            out.write(content);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        viewID = in.readInt();
        type = TOMMessageType.getMessageType(in.readByte());
        session = in.readInt();
        sequence = in.readInt();
        operationId = in.readInt();
        replyServer = in.readInt();

        int toRead = in.readInt();
        if (toRead != -1) {
            content = new byte[toRead];
            in.readFully(content);
        }
    }

    public int getViewID() {
        return viewID;
    }

    public void setViewID(int viewID) {
        this.viewID = viewID;
    }

    public TOMMessageType getType() {
        return type;
    }

    public void setType(TOMMessageType type) {
        this.type = type;
    }

    public int getSession() {
        return session;
    }

    public void setSession(int session) {
        this.session = session;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getOperationId() {
        return operationId;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getReplyServer() {
        return replyServer;
    }

    public void setReplyServer(int replyServer) {
        this.replyServer = replyServer;
    }
}
