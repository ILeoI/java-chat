package Common;

import Common.Messages.IMessage;
import Common.Messages.MessageType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class MessageWriter {

    private final BufferedWriter writer;

    public MessageWriter(final OutputStream outputStream) {
        this.writer = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    public <T> void sendMessage(final T f) {
        try {
            switch (f) {
                case String s -> writer.write(MessageType.TEXT + s);
                case IMessage message -> writer.write(message.serialise());
                case null, default -> {
                    assert f != null;
                    writer.write("Unsupported Messages.Message: " + f.getClass().getName());
                }
            }

            //System.out.println("Sent message: " + f);

            writer.newLine();
            writer.flush();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
