package bftsmart.messages.test.arbitraries;

import bftsmart.reconfiguration.views.View;
import bftsmart.statemanagement.durability.CSTRequestF1;
import bftsmart.statemanagement.durability.CSTSMMessageWire;
import java.net.InetSocketAddress;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

public final class CSTSMMessageArbitrary
        implements ArbitraryMessageSupplier<CSTSMMessageWire<Integer>> {

    @Override
    public Arbitrary<CSTSMMessageWire<Integer>> getArbitraries() {
        return Combinators.combine(
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        getConfigs(),
                        Arbitraries.integers(),
                        VMMessageArbitrary.getViews(),
                        Arbitraries.integers(),
                        Arbitraries.integers())
                .as((t1, t2, t3, t4, t5, t6, t7, t8) -> new CSTSMMessageWire<>(t1, t2, t3, t4, t5, t6, t7, t8, false));
    }

    @Override
    public Arbitrary<CSTSMMessageWire<Integer>> getFixtures() {
        CSTRequestF1 config = new CSTRequestF1(4);
        View view = new View(10, new int[] {11, 12, 13, 14}, 15, new InetSocketAddress[] {});
        return Arbitraries.of(new CSTSMMessageWire<>(1, 2, 3, config, 5, view, 6, 7, true));
    }

    @Provide
    static Arbitrary<CSTRequestF1> getConfigs() {
        return Arbitraries.integers().map(CSTRequestF1::new);
    }
}
