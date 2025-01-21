package Common.Messages;

public class CommandMessage implements IMessage {
    private final String command;

    public CommandMessage(final String command) {
        this.command = command;
    }

    @Override
    public final String serialise() {
        return MessageType.COMMAND + command;
    }
}
