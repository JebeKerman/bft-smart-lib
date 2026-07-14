package bftsmart.serialization.kryo;

import bftsmart.communication.SystemMessage;
import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.reconfiguration.ReconfigureReply;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.reconfiguration.views.View;
import bftsmart.serialization.MessageSerializer;
import bftsmart.serialization.messages.TOMMessageWire;
import bftsmart.statemanagement.durability.CSTRequestF1;
import bftsmart.statemanagement.durability.CSTSMMessageWire;
import bftsmart.statemanagement.standard.StandardSMMessageWire;
import bftsmart.tom.core.messages.ForwardedMessage;
import bftsmart.tom.core.messages.TOMMessageType;
import bftsmart.tom.leaderchange.LCMessageWire;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class KryoSerializer implements MessageSerializer {

    private final Kryo kryo;

    public static KryoSerializer getInstance() {
        return new KryoSerializer();
    }

    @Override
    public synchronized void serialize(SystemMessage msg, OutputStream out) throws IOException {
        Output output = new Output(out);
        kryo.writeClassAndObject(output, msg);
        output.flush();
    }

    @Override
    public synchronized SystemMessage deserialize(InputStream in)
            throws IOException, ClassNotFoundException {
        return (SystemMessage) kryo.readClassAndObject(new Input(in));
    }

    public void register(Class<?> type) {
        this.kryo.register(type);
    }

    private KryoSerializer() {
        kryo = new Kryo();

        kryo.register(int[].class);
        kryo.register(byte[].class);
        kryo.register(String[].class);
        kryo.register(HashMap.class);
        kryo.register(
                InetSocketAddress.class,
                new Serializer<InetSocketAddress>() {
                    @Override
                    public void write(Kryo kryo, Output output, InetSocketAddress address) {
                        output.writeString(address.getHostString());
                        output.writeInt(address.getPort());
                    }

                    @Override
                    public InetSocketAddress read(
                            Kryo kryo, Input input, Class<? extends InetSocketAddress> type) {
                        String host = input.readString();
                        int port = input.readInt();
                        return new InetSocketAddress(host, port);
                    }
                });

        kryo.register(SystemMessage.class);

        kryo.register(ConsensusMessage.class);
        kryo.register(CSTSMMessageWire.class);
        kryo.register(ForwardedMessage.class);
        kryo.register(LCMessageWire.class);
        kryo.register(StandardSMMessageWire.class);
        kryo.register(TOMMessageWire.class);
        kryo.register(VMMessage.class);

        kryo.register(TOMMessageType.class);
        kryo.register(ReconfigureReply.class);
        kryo.register(View.class);
        kryo.register(CSTRequestF1.class);
    }
}
