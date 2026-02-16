package bftsmart.serialization.proto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.consensus.messages.MessageFactory;
import bftsmart.messages.test.TestHelper;
import bftsmart.messages.test.arbitraries.ConsensusMessageArbitrary;
import bftsmart.serialization.MessageSerializer;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.ShrinkingMode;

public class TestProtoSerializer_ConsensusMessage {
    private final MessageSerializer serializer = ProtoSerializer.getInstance();

    @org.junit.jupiter.api.Test
    public void testSerializeSimpleConsensusMessage() throws IOException, ClassNotFoundException {
        ConsensusMessage expected = new ConsensusMessage(MessageFactory.PROPOSE, 1, 1, 1, new byte[] {1, 2, 3});

        byte[] bytes = TestHelper.toBytes(serializer, expected);
        assertTrue(bytes.length > 0);

        ConsensusMessage actual = TestHelper.fromBytes(serializer, bytes, ConsensusMessage.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Property(tries = 50, shrinking = ShrinkingMode.OFF)
    public void testSerializeArbitrary_ConsensusMessage(
            @ForAll(supplier = ConsensusMessageArbitrary.class) ConsensusMessage expected)
            throws IOException, ClassNotFoundException {
        byte[] bytes = TestHelper.toBytes(serializer, expected);
        assertTrue(bytes.length > 0);

        ConsensusMessage actual = TestHelper.fromBytes(serializer, bytes, ConsensusMessage.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
