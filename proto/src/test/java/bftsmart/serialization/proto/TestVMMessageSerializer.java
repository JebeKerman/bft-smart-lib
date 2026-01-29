package bftsmart.serialization.proto;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import bftsmart.communication.SystemMessage;
import bftsmart.reconfiguration.ReconfigureReply;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.reconfiguration.views.View;
import bftsmart.serialization.MessageSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.ShrinkingMode;

public class TestVMMessageSerializer {
    private final MessageSerializer serializer = ProtoSerializer.getInstance();

    @Provide
    Arbitrary<VMMessage> messages() {
        return Combinators.combine(Arbitraries.integers(), replies()).as(VMMessage::new);
    }

    @Provide
    Arbitrary<ReconfigureReply> replies() {
        return Combinators.combine(
                        views(),
                        Arbitraries.strings()
                                .alpha()
                                .ofMinLength(0)
                                .ofMaxLength(20)
                                .array(String[].class)
                                .ofMinSize(0)
                                .ofMaxSize(10),
                        Arbitraries.integers(),
                        Arbitraries.integers())
                .as(ReconfigureReply::new);
    }

    @Provide
    Arbitrary<View> views() {
        return Arbitraries.integers()
                .between(0, 10) // shared size
                .flatMap(
                        size ->
                                Combinators.combine(
                                                Arbitraries.integers(), // id
                                                Arbitraries.integers()
                                                        .array(int[].class)
                                                        .uniqueElements()
                                                        .ofSize(size),
                                                Arbitraries.integers().between(0, size), // f
                                                socketAddresses()
                                                        .array(InetSocketAddress[].class)
                                                        .ofSize(size))
                                        .as(View::new));
    }

    @Provide
    Arbitrary<InetSocketAddress> socketAddresses() {
        return Arbitraries.integers()
                .between(1, 254)
                .flatMap(
                        i ->
                                Arbitraries.integers()
                                        .between(1024, 65535)
                                        .map(
                                                port -> {
                                                    try {
                                                        return new InetSocketAddress(
                                                                InetAddress.getByAddress(
                                                                        new byte[] {127, 0, 0, 1}),
                                                                port);
                                                    } catch (UnknownHostException e) {
                                                    }
                                                    return null;
                                                }));
    }

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

        byte[] bytes = toBytes(expected);
        assertTrue(bytes.length > 0);

        VMMessage actual = fromBytes(bytes, VMMessage.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Property(tries = 50, shrinking = ShrinkingMode.OFF)
    public void testSerializeRandomVMMessage(@ForAll("messages") VMMessage expected)
            throws IOException, ClassNotFoundException {
        byte[] bytes = toBytes(expected);
        assertTrue(bytes.length > 0);

        VMMessage actual = fromBytes(bytes, VMMessage.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    private byte[] toBytes(SystemMessage msg) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(msg, outputStream);
        return outputStream.toByteArray();
    }

    private <T extends SystemMessage> T fromBytes(byte[] bytes, Class<T> clazz)
            throws IOException, ClassNotFoundException {
        return serializer.deserialize(new ByteArrayInputStream(bytes), clazz);
    }
}
