package bftsmart.messages.test;

import bftsmart.serialization.messages.TOMMessagePlain;
import bftsmart.tom.core.messages.TOMMessageType;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ArbitrarySupplier;
import net.jqwik.api.Combinators;

public final class TOMMessageArbitary implements ArbitrarySupplier<TOMMessagePlain> {

    @Override
    public Arbitrary<TOMMessagePlain> get() {
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
}
