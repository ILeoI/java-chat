package Common.Messages;

public final class PrivateMessage implements IMessage {

    private final String sender;
    private final String message;

    public static final int MAX_LENGTH = 128;

    public PrivateMessage(final String sender, final String message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public String serialise() {
        return MessageType.PRIVATE_MESSAGE + sender + "\0" + message;
    }
}
