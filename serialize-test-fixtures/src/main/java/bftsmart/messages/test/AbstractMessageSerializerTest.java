package bftsmart.messages.test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import bftsmart.communication.SystemMessage;
import bftsmart.serialization.MessageSerializer;
import java.util.function.Supplier;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.ShrinkingMode;

public abstract class AbstractMessageSerializerTest<T extends SystemMessage> {

    protected abstract MessageSerializer serializer();

    protected abstract Class<T> messageType();

    protected abstract Supplier<? extends Arbitrary<T>> arbitrarySupplier();

    protected abstract Supplier<? extends Arbitrary<T>> fixturesSupplier();

    @Property(tries = 50, shrinking = ShrinkingMode.OFF)
    void testSerializeArbitrary(@ForAll("messages") T expected) throws Exception {
        MessageSerializer serializer = serializer();

        byte[] bytes = TestHelper.toBytes(serializer, expected);
        assertTrue(bytes.length > 0);

        T actual = TestHelper.fromBytes(serializer, bytes, messageType());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Property
    void testSerializeFixtures(@ForAll("fixtures") T expected) throws Exception {
        MessageSerializer serializer = serializer();

        byte[] bytes = TestHelper.toBytes(serializer, expected);
        assertTrue(bytes.length > 0);

        T actual = TestHelper.fromBytes(serializer, bytes, messageType());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Provide
    Arbitrary<T> messages() {
        return arbitrarySupplier().get();
    }

    @Provide
    Arbitrary<T> fixtures() {
        return fixturesSupplier().get();
    }
}
