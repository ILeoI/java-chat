package Common.Messages;

public class Message implements IMessage {

    private final String sender;
    private final String message;

    public Message(final String sender, final String message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public final String serialise() {
        return MessageType.MESSAGE + sender + ": " + message;
    }

    public static Message getMessage(String message) {
        String name = message.substring(0, message.indexOf(":"));
        String contents = message.substring(message.indexOf(":"));

        return new Message(name, contents);
    }
}
