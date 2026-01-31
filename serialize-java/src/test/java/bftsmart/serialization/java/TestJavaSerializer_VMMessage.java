package bftsmart.serialization.java;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import bftsmart.messages.test.TestHelper;
import bftsmart.messages.test.VMMessageArbitrary;
import bftsmart.reconfiguration.ReconfigureReply;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.reconfiguration.views.View;
import bftsmart.serialization.MessageSerializer;
import java.io.IOException;
import java.net.InetSocketAddress;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.ShrinkingMode;

public class TestJavaSerializer_VMMessage {
    private final MessageSerializer serializer = JavaSerializer.getInstance();

    @org.junit.jupiter.api.Test
    public void testSerializeSimpleVMMessage() throws IOException, ClassNotFoundException {
        View view =
                new View(
                        1,
                        new int[] {1, 2, 3},
                        1,
                        new InetSocketAddress[] {
                            new InetSocketAddress("localhost", 8080),
                            new InetSocketAddress("localhost", 8081),
                            new InetSocketAddress("localhost", 8082)
                        });
        ReconfigureReply reply = new ReconfigureReply(view, new String[] {"node1", "node2"}, 1, 2);
        VMMessage expected = new VMMessage(1, reply);

        byte[] bytes = TestHelper.toBytes(serializer, expected);
        assertTrue(bytes.length > 0);

        VMMessage actual = TestHelper.fromBytes(serializer, bytes, VMMessage.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Property(tries = 50, shrinking = ShrinkingMode.OFF)
    public void testSerializeArbitrary_VMMessage(
            @ForAll(supplier = VMMessageArbitrary.class) VMMessage expected)
            throws IOException, ClassNotFoundException {
        byte[] bytes = TestHelper.toBytes(serializer, expected);
        assertTrue(bytes.length > 0);

        VMMessage actual = TestHelper.fromBytes(serializer, bytes, VMMessage.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
