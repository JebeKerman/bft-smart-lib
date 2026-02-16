package bftsmart.messages.test.arbitraries;

import bftsmart.reconfiguration.ReconfigureReply;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.reconfiguration.views.View;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ArbitrarySupplier;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

public final class VMMessageArbitrary implements ArbitrarySupplier<VMMessage> {

    @Override
    public Arbitrary<VMMessage> get() {
        return Combinators.combine(Arbitraries.integers(), replies()).as(VMMessage::new);
    }

    @Provide
    Arbitrary<ReconfigureReply> replies() {
        return Combinators.combine(
                        views(),
                        Arbitraries.strings()
                                .alpha()
                                .ofMinLength(0)
                                .ofMaxLength(20)
                                .array(String[].class)
                                .ofMinSize(0)
                                .ofMaxSize(10),
                        Arbitraries.integers(),
                        Arbitraries.integers())
                .as(ReconfigureReply::new);
    }

    @Provide
    Arbitrary<View> views() {
        return Arbitraries.integers()
                .between(0, 10) // shared size
                .flatMap(
                        size ->
                                Combinators.combine(
                                                Arbitraries.integers(), // id
                                                Arbitraries.integers()
                                                        .array(int[].class)
                                                        .uniqueElements()
                                                        .ofSize(size),
                                                Arbitraries.integers().between(0, size), // f
                                                socketAddresses()
                                                        .array(InetSocketAddress[].class)
                                                        .ofSize(size))
                                        .as(View::new));
    }

    @Provide
    Arbitrary<InetSocketAddress> socketAddresses() {
        return Arbitraries.integers()
                .between(1, 254)
                .flatMap(
                        i ->
                                Arbitraries.integers()
                                        .between(1024, 65535)
                                        .map(
                                                port -> {
                                                    try {
                                                        return new InetSocketAddress(
                                                                InetAddress.getByAddress(
                                                                        new byte[] {127, 0, 0, 1}),
                                                                port);
                                                    } catch (UnknownHostException e) {
                                                    }
                                                    return null;
                                                }));
    }
}
