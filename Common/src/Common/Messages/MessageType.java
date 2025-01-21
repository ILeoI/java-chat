package Common.Messages;

public enum MessageType {
    INVALID('0'),
    INTRODUCTION('I'),
    SETTINGS('S'),
    COMMAND('C'),
    DISCONNECT('D'),
    TEXT('T'),
    MESSAGE('M'),
    PRIVATE_MESSAGE('P'),
    MULTI_MESSAGE('U');

    private final char represent;

    MessageType(char c) {
        this.represent = c;
    }

    public static MessageType getMessageTypeFromChar(char c) {
        for (MessageType type : MessageType.values()) {
            if (type.represent == c) {
                return type;
            }
        }
        return INVALID;
    }

    @Override
    public String toString() {
        return String.valueOf(represent);
    }
}
