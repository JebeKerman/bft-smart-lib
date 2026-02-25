package bftsmart.messages.test.arbitraries;

import bftsmart.reconfiguration.views.View;
import bftsmart.statemanagement.durability.CSTRequestF1;
import bftsmart.statemanagement.standard.StandardSMMessageWire;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

public final class StandardSMMessageArbitrary
        implements ArbitraryMessageSupplier<StandardSMMessageWire<Integer>> {

    @Override
    public Arbitrary<StandardSMMessageWire<Integer>> getArbitraries() {
        return Combinators.combine(
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        Arbitraries.integers(),
                        VMMessageArbitrary.getViews(),
                        Arbitraries.integers(),
                        Arbitraries.integers())
                .as(
                        (t1, t2, t3, t4, t5, t6, t7, t8) ->
                                new StandardSMMessageWire<>(t1, t2, t3, t4, t5, t6, t7, t8, false));
    }

    @Override
    public Arbitrary<StandardSMMessageWire<Integer>> getFixtures() {
        InetSocketAddress[] addr;
        try {
            addr =
                    new InetSocketAddress[] {
                        new InetSocketAddress(
                                InetAddress.getByAddress(new byte[] {127, 0, 0, 1}), 1011),
                        new InetSocketAddress(
                                InetAddress.getByAddress(new byte[] {127, 0, 0, 1}), 1012),
                        new InetSocketAddress(
                                InetAddress.getByAddress(new byte[] {127, 0, 0, 1}), 1013),
                        new InetSocketAddress(
                                InetAddress.getByAddress(new byte[] {127, 0, 0, 1}), 1014),
                    };
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        View view = new View(10, new int[] {11, 12, 13, 14}, 15, addr);
        return Arbitraries.of(new StandardSMMessageWire<>(1, 2, 3, 4, 5, view, 6, 7, false));
    }

    @Provide
    static Arbitrary<CSTRequestF1> getConfigs() {
        return Arbitraries.integers().map(CSTRequestF1::new);
    }
}
