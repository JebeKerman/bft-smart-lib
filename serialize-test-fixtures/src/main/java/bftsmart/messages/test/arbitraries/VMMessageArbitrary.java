package bftsmart.messages.test.arbitraries;

import bftsmart.reconfiguration.ReconfigureReply;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.reconfiguration.views.View;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

public final class VMMessageArbitrary implements ArbitraryMessageSupplier<VMMessage> {

    @Override
    public Arbitrary<VMMessage> getArbitraries() {
        return Combinators.combine(Arbitraries.integers(), replies()).as(VMMessage::new);
    }

    @Override
    public Arbitrary<VMMessage> getFixtures() {
        View view =
                new View(
                        1,
                        new int[] {1, 2, 3},
                        1,
                        new InetSocketAddress[] {
                            new InetSocketAddress("localhost", 8080),
                            new InetSocketAddress("localhost", 8081),
                            new InetSocketAddress("localhost", 8082)
                        });
        ReconfigureReply reply = new ReconfigureReply(view, new String[] {"node1", "node2"}, 1, 2);
        VMMessage message = new VMMessage(1, reply);
        return Arbitraries.of(message);
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
