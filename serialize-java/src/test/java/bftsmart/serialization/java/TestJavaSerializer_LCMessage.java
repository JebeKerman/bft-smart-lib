package bftsmart.serialization.java;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import bftsmart.messages.test.LCMessageArbitrary;
import bftsmart.messages.test.TestHelper;
import bftsmart.serialization.MessageSerializer;
import bftsmart.tom.leaderchange.LCMessageWire;
import java.io.IOException;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

public class TestJavaSerializer_LCMessage {
    private final MessageSerializer serializer = JavaSerializer.getInstance();

    @org.junit.jupiter.api.Test
    public void testSerializeSimpleLCMessage() throws IOException, ClassNotFoundException {
        LCMessageWire expected = new LCMessageWire(1, 2, 3, new byte[] {1, 2, 3}, true);

        byte[] bytes = TestHelper.toBytes(serializer, expected);
        assertTrue(bytes.length > 0);

        LCMessageWire actual = TestHelper.fromBytes(serializer, bytes, LCMessageWire.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("TRIGGER_LC_LOCALLY")
                .isEqualTo(expected);
    }

    @Property
    public void testSerializeArbitrary_LCMessage(
            @ForAll(supplier = LCMessageArbitrary.class) LCMessageWire expected)
            throws IOException, ClassNotFoundException {

        byte[] bytes = TestHelper.toBytes(serializer, expected);
        assertTrue(bytes.length > 0);

        LCMessageWire actual = TestHelper.fromBytes(serializer, bytes, LCMessageWire.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("TRIGGER_LC_LOCALLY")
                .isEqualTo(expected);
    }
}
