import Common.IPAddress;
import Common.InvalidIPException;
import Common.MessageReceiver;
import Common.MessageWriter;
import Common.Messages.CommandMessage;
import Common.Messages.IntroductionMessage;
import Common.Messages.Message;
import Common.Messages.MessageType;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    private static final Scanner scanner = new Scanner(System.in);
    private final String name;
    private final boolean debug;
    private Socket connection;
    private MessageReceiver receiver;

    public Client(boolean debug) {
        this.debug = debug;
        System.out.print("Name? ");
        this.name = scanner.nextLine();
    }

    public static void main(final String[] args) {
        boolean debug = false;
        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("debug")) {
                debug = true;
            }
        }
        Client client = new Client(debug);
        while (!client.tryConnect()) {
            System.out.println("Could not connect!");
            System.out.println("Try again? (y/n): ");
            var input = scanner.nextLine();
            if (input.equalsIgnoreCase("n")) {
                break;
            }
        }
    }

    public boolean tryConnect() {
        System.out.print("IP? ");

        var input = scanner.nextLine();

        if (input.equalsIgnoreCase("default") || input.equalsIgnoreCase("d")) {
            input = "127.0.0.1:5001";
        }

        IPAddress connectedAddress;
        try {
            connectedAddress = new IPAddress(input);
        } catch (InvalidIPException e) {
            return false;
        }

        try {
            this.connection = new Socket(connectedAddress.getAddress(), connectedAddress.getPort());
            this.receiver = new MessageReceiver(this.connection.getInputStream());
            listenForInput();

            System.out.println("Connected to: " + connection);
            MessageWriter messageWriter = new MessageWriter(connection.getOutputStream());

            messageWriter.sendMessage(new IntroductionMessage(this.name));

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (debug) System.out.println("Sending: " + line);
                if (line.startsWith("/")) {
                    messageWriter.sendMessage(new CommandMessage(line));
                } else {
                    messageWriter.sendMessage(new Message(this.name, line));
                }
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public void listenForInput() {
        new Thread(() -> {
            while (connection.isConnected()) {
                String input = receiver.getLine();

                if (input == null) {
                    continue;
                }

                char[] chars = input.toCharArray();
                MessageType type = MessageType.getMessageTypeFromChar(chars[0]);

                String message = input.substring(1);


                switch (type) {
                    case DISCONNECT -> disconnect();
                    case MESSAGE, TEXT -> System.out.println(message);
                    case MULTI_MESSAGE -> {
                        var messages = message.split("\0");
                        Arrays.stream(messages).toList().forEach(System.out::println);
                    }
                    case PRIVATE_MESSAGE -> {
                        final String sender = message.substring(0, message.indexOf('\0'));
                        final String pmMessage = message.substring(message.indexOf('\0') + 1);
                        System.out.println(sender + " -> you: " + pmMessage);
                    }
                    default -> {
                        if (debug) System.out.println("Data received: " + message + ", type: " + type.name());
                    }
                }
            }
        }).start();
    }

    private void disconnect() {
        System.exit(0);
    }
}