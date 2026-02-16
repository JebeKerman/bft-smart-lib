package bftsmart.serialization.proto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import bftsmart.messages.test.TestHelper;
import bftsmart.messages.test.arbitraries.TOMMessageArbitary;
import bftsmart.serialization.MessageSerializer;
import bftsmart.serialization.messages.TOMMessagePlain;
import bftsmart.tom.core.messages.TOMMessageType;
import java.io.IOException;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

public class TestProtoSerializer_TOMMessage {
    private final MessageSerializer serializer = ProtoSerializer.getInstance();

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

        byte[] bytes = TestHelper.toBytes(serializer, expected);
        assertTrue(bytes.length > 0);

        TOMMessagePlain actual = TestHelper.fromBytes(serializer, bytes, TOMMessagePlain.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Property
    public void testSerializeArbitrary_TOMMessage(
            @ForAll(supplier = TOMMessageArbitary.class) TOMMessagePlain expected)
            throws IOException, ClassNotFoundException {
        byte[] bytes = TestHelper.toBytes(serializer, expected);
        assertTrue(bytes.length > 0);

        TOMMessagePlain actual = TestHelper.fromBytes(serializer, bytes, TOMMessagePlain.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
