package Common.Messages;

public class IntroductionMessage implements IMessage {

    private final String name;

    public IntroductionMessage(final String name) {
        this.name = name;
    }

    @Override
    public String serialise() {
        return MessageType.INTRODUCTION + this.name;
    }
}
