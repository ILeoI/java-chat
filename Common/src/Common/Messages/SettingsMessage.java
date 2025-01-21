package Common.Messages;

import Common.Settings;

public class SettingsMessage implements IMessage {

    private final Settings settings;

    public SettingsMessage(final Settings settings) {
        this.settings = settings;
    }

    @Override
    public String serialise() {
        String retMsg = String.valueOf(MessageType.SETTINGS) +
                (settings.selfEcho() ? '1' : '0');
        return retMsg;
    }

}
