package bftsmart.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bftsmart.communication.SystemMessage;

public final class ByteCountMessageSerializer implements MessageSerializer {
    private final MessageSerializer delegate;

    private final Map<Class<? extends SystemMessage>, ByteCountStats> stats =
            new ConcurrentHashMap<>();

    public ByteCountMessageSerializer(MessageSerializer delegate) {
        this.delegate = delegate;
    }

    public Map<Class<? extends SystemMessage>, ByteCountStats> getStats() {
        return stats;
    }

    @Override
    public void serialize(SystemMessage msg, OutputStream out) throws IOException {        
        ByteCountOutputStream countingOut = new ByteCountOutputStream(out);

        delegate.serialize(msg, countingOut);

        long bytes = countingOut.getByteCount();

        ByteCountStats counter = stats.computeIfAbsent(msg.getClass(), c -> new ByteCountStats());
        counter.addMessage(bytes);
    }

    @Override
    public SystemMessage deserialize(InputStream in) throws IOException, ClassNotFoundException {
        return delegate.deserialize(in);
    }
}