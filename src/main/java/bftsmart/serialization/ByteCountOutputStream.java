package bftsmart.serialization;

import java.io.IOException;
import java.io.OutputStream;

public class ByteCountOutputStream extends OutputStream {
    private long bytesWritten;
    private final OutputStream delegate;

    public ByteCountOutputStream(OutputStream delegate) {
        this.delegate = delegate;
    }

    public long getByteCount() {
        return bytesWritten;
    }

    @Override
    public void write(int b) throws IOException {
        delegate.write(b);
        bytesWritten++;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        delegate.write(b, off, len);
        bytesWritten += len;
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
