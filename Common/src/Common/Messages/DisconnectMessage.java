package Common.Messages;

public class DisconnectMessage implements IMessage {
    @Override
    public final String serialise() {
        return MessageType.DISCONNECT.toString();
    }
}
