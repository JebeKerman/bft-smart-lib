package bftsmart.messages.test.arbitraries;

import bftsmart.tom.leaderchange.LCMessageWire;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;

public final class LCMessageArbitrary implements ArbitraryMessageSupplier<LCMessageWire> {

    @Override
    public Arbitrary<LCMessageWire> getArbitraries() {
        return Combinators.combine(
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        Arbitraries.bytes().array(byte[].class).ofMinSize(1).ofMaxSize(16384),
                        Arbitraries.of(true, false))
                .as(LCMessageWire::new);
    }

    @Override
    public Arbitrary<LCMessageWire> getFixtures() {
        return Arbitraries.of(new LCMessageWire(1, 2, 3, new byte[] {1, 2, 3}, true));
    }
}
