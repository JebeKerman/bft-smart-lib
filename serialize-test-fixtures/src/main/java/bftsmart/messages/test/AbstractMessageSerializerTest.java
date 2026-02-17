package bftsmart.messages.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bftsmart.communication.SystemMessage;
import bftsmart.messages.test.arbitraries.ArbitraryMessageSupplier;
import bftsmart.serialization.MessageSerializer;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.ShrinkingMode;

public abstract class AbstractMessageSerializerTest<T extends SystemMessage> {

    protected abstract MessageSerializer serializer();

    protected abstract Class<T> messageType();

    protected abstract ArbitraryMessageSupplier<T> arbitrarySupplier();

    @Property(tries = 50, shrinking = ShrinkingMode.OFF)
    void testSerializeArbitrary(@ForAll("arbitraries") T expected) throws Exception {
        MessageSerializer serializer = serializer();

        byte[] bytes = TestHelper.toBytes(serializer, expected);
        assertTrue(bytes.length > 0);

        T actual = TestHelper.fromBytes(serializer, bytes, messageType());

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields(ignoredFields())
                .isEqualTo(expected);
    }

    @Property
    void testSerializeFixtures(@ForAll("fixtures") T expected) throws Exception {
        MessageSerializer serializer = serializer();

        byte[] bytes = TestHelper.toBytes(serializer, expected);
        assertTrue(bytes.length > 0);

        T actual = TestHelper.fromBytes(serializer, bytes, messageType());

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields(ignoredFields())
                .isEqualTo(expected);
    }

    @Provide
    Arbitrary<T> arbitraries() {
        return arbitrarySupplier().getArbitraries();
    }

    @Provide
    Arbitrary<T> fixtures() {
        return arbitrarySupplier().getFixtures();
    }

    protected String[] ignoredFields() {
        return new String[0];
    }
}
