package bftsmart.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import bftsmart.communication.SystemMessage;

public final class ByteCountMessageSerializer implements MessageSerializer {
    private final MessageSerializer delegate;

    private final Map<Class<? extends SystemMessage>, AtomicLong> stats =
            new ConcurrentHashMap<>();

    public ByteCountMessageSerializer(MessageSerializer delegate) {
        this.delegate = delegate;
    }

    public Map<Class<? extends SystemMessage>, AtomicLong> getStats() {
        return stats;
    }

    @Override
    public void serialize(SystemMessage msg, OutputStream out) throws IOException {        
        ByteCountOutputStream countingOut = new ByteCountOutputStream(out);

        delegate.serialize(msg, countingOut);

        long bytes = countingOut.getByteCount();

        AtomicLong counter = stats.computeIfAbsent(msg.getClass(), c -> new AtomicLong(0));
        counter.addAndGet(bytes);
    }

    @Override
    public SystemMessage deserialize(InputStream in) throws IOException, ClassNotFoundException {
        return delegate.deserialize(in);
    }
}