package bftsmart.messages.test;

import bftsmart.tom.leaderchange.LCMessageWire;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ArbitrarySupplier;
import net.jqwik.api.Combinators;

public final class LCMessageArbitrary implements ArbitrarySupplier<LCMessageWire> {

    @Override
    public Arbitrary<LCMessageWire> get() {
        return Combinators.combine(
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        Arbitraries.bytes().array(byte[].class).ofMinSize(1).ofMaxSize(2 ^ 10),
                        Arbitraries.of(true, false))
                .as(LCMessageWire::new);
    }
}
