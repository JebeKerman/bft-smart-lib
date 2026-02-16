package bftsmart.serialization.java;

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
import org.junit.jupiter.api.Test;

public class TestJavaSerializer_TOMMessage {
    private final MessageSerializer serializer = JavaSerializer.getInstance();

    @Test
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
    public void randomizedSerialization(
            @ForAll(supplier = TOMMessageArbitary.class) TOMMessagePlain expected)
            throws IOException, ClassNotFoundException {
        byte[] bytes = TestHelper.toBytes(serializer, expected);
        assertTrue(bytes.length > 0);

        TOMMessagePlain actual = TestHelper.fromBytes(serializer, bytes, TOMMessagePlain.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
