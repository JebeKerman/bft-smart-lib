package bftsmart.analysis;

import bftsmart.communication.SystemMessage;
import bftsmart.messages.bench.MessageProvider;
import bftsmart.serialization.MessageSerializer;
import bftsmart.serialization.java.JavaSerializer;
import bftsmart.serialization.proto.ProtoSerializer;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.core.util.Separators.Spacing;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class MemoryAnalysis {

    private static SystemMessage[] messages =
            new SystemMessage[] {
                MessageProvider.getTOMMessage(),
                MessageProvider.getVMMessage(),
                MessageProvider.getLCMessage(),
                MessageProvider.getConsensusMessage(),
            };
    private static MessageSerializer[] serializers =
            new MessageSerializer[] {
                JavaSerializer.getInstance(), ProtoSerializer.getInstance(),
            };

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: WriteJsonMain <output-file>");
            System.exit(1);
        }
        File output = new File(args[0]);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode results = mapper.createArrayNode();
        root.set("results", results);

        for (SystemMessage message : messages) {
            ObjectNode result = mapper.createObjectNode();
            result.put("message", message.getClass().getSimpleName());

            ArrayNode msgSizes = mapper.createArrayNode();

            for (MessageSerializer serializer : serializers) {
                byte[] array;
                try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                    serializer.serialize(message, os);
                    array = os.toByteArray();
                }
                ;
                ObjectNode size = mapper.createObjectNode();
                size.put("serializer", serializer.getClass().getSimpleName());
                size.put("size", array.length);
                msgSizes.add(size);
            }

            result.set("sizes", msgSizes);
            results.add(result);
        }

        DefaultPrettyPrinter pp =
                new DefaultPrettyPrinter()
                        .withObjectIndenter(new DefaultIndenter("  ", "\n"))
                        .withArrayIndenter(new DefaultIndenter("  ", "\n"))
                        .withSeparators(
                                Separators.createDefaultInstance()
                                        .withObjectFieldValueSpacing(Spacing.AFTER));
        mapper.writer(pp).writeValue(output, root);
    }
}
