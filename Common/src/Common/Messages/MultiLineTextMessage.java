package Common.Messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiLineTextMessage implements IMessage {

    private final ArrayList<String> messages = new ArrayList<>();

    public MultiLineTextMessage(String message) {
        var list = message.split("\n");
        messages.addAll(Arrays.asList(list));
    }

    public MultiLineTextMessage(String[] message) {
        messages.addAll(Arrays.asList(message));
    }

    public MultiLineTextMessage(List<String> list) {
        messages.addAll(list);
    }

    @Override
    public String serialise() {
        StringBuilder sb = new StringBuilder();
        sb.append(MessageType.MULTI_MESSAGE);

        messages.forEach((e) -> sb.append(e).append("\0"));

        return sb.toString();
    }
}
