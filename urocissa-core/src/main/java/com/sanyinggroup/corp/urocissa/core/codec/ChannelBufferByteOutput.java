
package com.sanyinggroup.corp.urocissa.core.codec;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteOutput;

import java.io.IOException;

/**
 * 出参
 * @author lixiao create at 2017年7月10日 下午2:41:23 
 * @since 1.0.0
 */
public class ChannelBufferByteOutput implements ByteOutput {

    private final ByteBuf buffer;

    public ChannelBufferByteOutput(ByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    public void close() throws IOException {
        // Nothing to do
    }

    @Override
    public void flush() throws IOException {
        // nothing to do
    }

    @Override
    public void write(int b) throws IOException {
        buffer.writeByte(b);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        buffer.writeBytes(bytes);
    }

    @Override
    public void write(byte[] bytes, int srcIndex, int length) throws IOException {
        buffer.writeBytes(bytes, srcIndex, length);
    }

    /**
     * Return the {@link ByteBuf} which contains the written content
     *
     */
    ByteBuf getBuffer() {
        return buffer;
    }
}
