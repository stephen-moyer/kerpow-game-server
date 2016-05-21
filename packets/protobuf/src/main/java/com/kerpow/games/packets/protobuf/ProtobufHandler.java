package com.kerpow.games.packets.protobuf;

import com.google.protobuf.*;
import com.kerpow.games.packets.MessageHandler;

public final class ProtobufHandler {

    private static final ProtobufEncoder ENCODER = new ProtobufEncoder();

    public static MessageHandler create(int opcode, GeneratedMessage message) {
        return new MessageHandler(message.getClass(), opcode, new ProtobufDecoder(message.getParserForType()), ENCODER);
    }

    private static final class ProtobufEncoder<T extends GeneratedMessage> implements MessageHandler.Encoder<T> {

        @Override
        public byte[] encode(T message) {
            return message.toByteArray();
        }
    }

    private static final class ProtobufDecoder<T extends GeneratedMessage> implements MessageHandler.Decoder<T> {

        private final Parser<T> parser;

        public ProtobufDecoder(Parser<T> parser) {
            this.parser = parser;
        }

        @Override
        public T decode(byte[] data) {
            try {
                return parser.parseFrom(data);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
                //TODO throw?
                return null;
            }
        }
    }
}
