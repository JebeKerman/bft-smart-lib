package bftsmart.serialization;

import java.util.concurrent.atomic.AtomicLong;

public final class ByteCountStats {
    private final AtomicLong byteCount;
    private final AtomicLong messageCount;

    public ByteCountStats() {
        byteCount = new AtomicLong(0);
        messageCount = new AtomicLong(0);
    }

    public void addMessage(long bytes) {
        messageCount.incrementAndGet();
        byteCount.addAndGet(bytes);   
    }

    public long getByteCount() {
      return byteCount.get();
    }

    public long getMessageCount() {
      return messageCount.get();
    }
}