package bftsmart.messages.test.arbitraries;

import bftsmart.communication.SystemMessage;
import net.jqwik.api.Arbitrary;

public interface ArbitraryMessageSupplier<T extends SystemMessage> {
    Arbitrary<T> getArbitraries();

    Arbitrary<T> getFixtures();
}
