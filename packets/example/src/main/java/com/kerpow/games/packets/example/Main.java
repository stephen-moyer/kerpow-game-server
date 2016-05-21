package com.kerpow.games.packets.example;

import com.kerpow.games.packets.MessageHandler;
import com.kerpow.games.packets.PacketProcessor;
import com.kerpow.games.packets.exceptions.MessageHandlerException;
import com.kerpow.games.packets.exceptions.MissingHandlerException;
import com.kerpow.games.packets.protobuf.ProtobufHandler;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(ProtobufHandler.create(0, Example.ExamplePacket.getDefaultInstance()));
        handlers.add(new MessageHandler<>(ExamplePacket2.class, 1, new ExamplePacket2Decoder(), new ExamplePacket2Encoder()));
        PacketProcessor processor = new PacketProcessor(handlers);
        processor.addListener(Example.ExamplePacket.class, new PacketProcessor.PacketListener<Object, Example.ExamplePacket>() {
            @Override
            public void received(Object o, Example.ExamplePacket examplePacket) {
                System.out.println(examplePacket.getEx1());
                System.out.println(examplePacket.getEx2());
            }
        });
        processor.addListener(ExamplePacket2.class, new PacketProcessor.PacketListener<Object, ExamplePacket2>() {
            @Override
            public void received(Object o, ExamplePacket2 examplePacket) {
                System.out.println(examplePacket.ex1);
                System.out.println(examplePacket.ex2);
            }
        });
        Example.ExamplePacket.Builder bldr = Example.ExamplePacket.newBuilder();
        bldr.setEx1("Test1");
        bldr.setEx2(13);
        try {
            long time = System.currentTimeMillis();
            processor.processPacket(new Object(), processor.encodeMessage(bldr.build()));
            System.out.println("Protobuf time: " + (System.currentTimeMillis() - time));
        } catch (MissingHandlerException | MessageHandlerException e) {
            e.printStackTrace();
        }
        try {
            ExamplePacket2 packet2 = new ExamplePacket2();
            packet2.ex1 = 10;
            packet2.ex2 = 14;
            long time = System.currentTimeMillis();
            processor.processPacket(new Object(), processor.encodeMessage(packet2));
            System.out.println("Raw array time: " + (System.currentTimeMillis() - time));
        } catch (MissingHandlerException | MessageHandlerException e) {
            e.printStackTrace();
        }
    }

    private static class ExamplePacket2 {

        public byte ex1;
        public byte ex2;

    }

    private static class ExamplePacket2Encoder implements MessageHandler.Encoder<ExamplePacket2> {

        @Override
        public byte[] encode(ExamplePacket2 examplePacket2) {
            return new byte[]{
                examplePacket2.ex1,
                examplePacket2.ex2
            };
        }
    }

    private static class ExamplePacket2Decoder implements MessageHandler.Decoder<ExamplePacket2> {

        @Override
        public ExamplePacket2 decode(byte[] bytes) {
            ExamplePacket2 packet2 = new ExamplePacket2();
            packet2.ex1 = bytes[0];
            packet2.ex2 = bytes[1];
            return packet2;
        }
    }
}
