package bftsmart.serialization.messages;

import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.messages.bench.AbstractMessageBenchmark;
import bftsmart.messages.bench.MessageProvider;
import bftsmart.serialization.MessageSerializer;
import bftsmart.serialization.java.JavaSerializer;

public class JavaBenchmark_ConsensusMessage extends AbstractMessageBenchmark<ConsensusMessage> {

    @Override
    protected MessageSerializer createSerializer() {
        return JavaSerializer.getInstance();
    }

    @Override
    protected ConsensusMessage createMessage() {
        return MessageProvider.getConsensusMessage();
    }

    @Override
    protected Class<ConsensusMessage> messageType() {
        return ConsensusMessage.class;
    }
}
