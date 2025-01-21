package Common;

public class Settings {

    private boolean selfEcho = false;

    public Settings() {

    }

    public Settings(boolean selfEcho) {
        this.selfEcho = selfEcho;
    }
    public static Settings DEFAULT_SETTINGS = new Settings();

    public boolean selfEcho() {
        return selfEcho;
    }

    public void setSelfEcho(boolean selfEcho) {
        this.selfEcho = selfEcho;
    }

    public static String getHelpMessage() {
        return """
                Usage: /settings <setting> [args]
                Possible Settings:
                    - selfEcho <on/off>
                """;
    }
}

