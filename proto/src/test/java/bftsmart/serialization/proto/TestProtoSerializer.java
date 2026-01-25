package bftsmart.serialization.proto;

import static org.junit.jupiter.api.Assertions.*;

import bftsmart.communication.SystemMessage;
import bftsmart.serialization.MessageSerializer;
import bftsmart.serialization.messages.TOMMessagePlain;
import bftsmart.tom.core.messages.TOMMessageType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

public class TestProtoSerializer {
    private final MessageSerializer serializer = ProtoSerializer.getInstance();

    @Provide
    Arbitrary<TOMMessagePlain> messages() {
        return Combinators.combine(
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        Arbitraries.bytes()
                                .array(byte[].class)
                                .ofMinSize(1)
                                .ofMaxSize(2 ^ 14)
                                .injectNull(0.05),
                        Arbitraries.integers(),
                        Arbitraries.of(TOMMessageType.values()))
                .as(TOMMessagePlain::new);
    }

    @org.junit.jupiter.api.Test
    public void serializeSimpleMessage() throws IOException, ClassNotFoundException {
        TOMMessagePlain expected = new TOMMessagePlain(1);
        expected.setSession(2);
        expected.setSequence(3);
        expected.setOperationId(4);
        expected.setContent(new byte[] {5, 6, 7});
        expected.setViewID(8);
        expected.setType(TOMMessageType.ASK_STATUS);
        expected.setReplyServer(42);

        byte[] bytes = toBytes(expected);
        assertTrue(bytes.length > 0);

        TOMMessagePlain actual = fromBytes(bytes, TOMMessagePlain.class);

        assertMsgEquals(expected, actual);
    }

    @Property
    public void randomizedSerialization(@ForAll("messages") TOMMessagePlain expected)
            throws IOException, ClassNotFoundException {
        byte[] bytes = toBytes(expected);
        assertTrue(bytes.length > 0);

        TOMMessagePlain actual = fromBytes(bytes, TOMMessagePlain.class);

        assertMsgEquals(expected, actual);
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

    private void assertMsgEquals(TOMMessagePlain expected, TOMMessagePlain actual) {
        assertEquals(expected.getSender(), actual.getSender());
        assertEquals(expected.getViewID(), actual.getViewID());
        assertEquals(expected.getSession(), actual.getSession());
        assertEquals(expected.getSequence(), actual.getSequence());
        assertEquals(expected.getOperationId(), actual.getOperationId());
        assertTrue(Arrays.equals(expected.getContent(), actual.getContent()));
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getReplyServer(), actual.getReplyServer());
    }
}
