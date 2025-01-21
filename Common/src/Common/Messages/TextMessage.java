package Common.Messages;

public class TextMessage implements IMessage {

    private final String message;

    public TextMessage(final String message) {
        this.message = message;
    }

    @Override
    public final String serialise() {
        return MessageType.MESSAGE + message;
    }
}
