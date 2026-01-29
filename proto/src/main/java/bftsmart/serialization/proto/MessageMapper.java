package bftsmart.serialization.proto;

import bftsmart.communication.SystemMessage;

interface MessageMapper<TInternal extends SystemMessage, TProto> {
    TInternal fromProto(int senderId, TProto protoMsg);

    TProto toProto(TInternal internalMsg);
}
