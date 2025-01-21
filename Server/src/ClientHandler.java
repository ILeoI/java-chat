import Common.MessageReceiver;
import Common.MessageWriter;
import Common.Messages.*;
import Common.Settings;

import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientHandler extends Thread {

    private final static ConcurrentLinkedQueue<ClientHandler> CLIENT_HANDLERS = new ConcurrentLinkedQueue<>();

    private final MessageReceiver reader;
    private final MessageWriter writer;
    private final Socket socket;
    private final Server server;
    private final UUID clientID;
    private Settings clientSettings = Settings.DEFAULT_SETTINGS;
    private String name;
    private boolean isConsole = false;

    private ClientHandler replyHandler;

    public ClientHandler(final Socket socket, final Server server) throws IOException {
        this.socket = socket;
        this.server = server;

        this.reader = new MessageReceiver(socket.getInputStream());
        this.writer = new MessageWriter(socket.getOutputStream());

        CLIENT_HANDLERS.add(this);
        this.clientID = UUID.randomUUID();
    }

    public ClientHandler(final Socket socket, final Server server, final boolean isServer) throws IOException {
        this(socket, server);

        this.isConsole = isServer;
    }

    @Override
    public void run() {
        try {
            writer.sendMessage(new MultiLineTextMessage(server.MOTD));

            while (socket.isConnected()) {
                String input = reader.getLine();

                if (input == null) {
                    throw new IOException();
                }

                char[] chars = input.toCharArray();
                MessageType type = MessageType.getMessageTypeFromChar(chars[0]);

                String message = input.substring(1);

                switch (type) {
                    case INTRODUCTION -> {
                        this.name = message;
                        broadcast("SERVER: " + this.name + " has joined the server!");
                    }
                    case COMMAND -> {
                        String[] args = message.split(" ");
                        switch (args[0]) {
                            case "/disconnect" -> {
                                writer.sendMessage(new DisconnectMessage());
                                throw new IOException();
                            }
                            case "/party" -> broadcast("SERVER: " + name + " is having a party!", true);
                            case "/settings" -> {
                                if (args.length == 1) {
                                    writer.sendMessage(new MultiLineTextMessage(Settings.getHelpMessage()));
                                    break;
                                }

                                if (args[1].equalsIgnoreCase("selfEcho")) {
                                    if (args.length != 3) {
                                        writer.sendMessage(new TextMessage("ERROR: Invalid Command Usage: /settings selfEcho on/off"));
                                        break;
                                    }
                                    if (args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("off")) {
                                        clientSettings.setSelfEcho(args[2].equalsIgnoreCase("on"));
                                        writer.sendMessage(new TextMessage("SETTINGS: Set selfEcho to " + args[2]));
                                    } else {
                                        writer.sendMessage(new TextMessage("ERROR: Invalid Command Usage: /settings selfEcho on/off"));
                                    }
                                }
                            }
                            case "/msg" -> {
                                if (args.length != 3) {
                                    writer.sendMessage(new TextMessage("Usage: /msg <recipient> <message>"));
                                    break;
                                }

                                final String recipient = args[1];
                                final String pmMessage = args[2];
                                final ClientHandler recipientClient = getUser(recipient);

                                if (recipientClient == null) {
                                    writer.sendMessage(new TextMessage(String.format("User '%s' not found!", recipient)));
                                    break;
                                }

                                if (pmMessage.length() > PrivateMessage.MAX_LENGTH) {
                                    writer.sendMessage(new TextMessage(String.format("The message is too long (%d). Max length is 128 characters.", pmMessage.length())));
                                    break;
                                }

                                this.writer.sendMessage(new TextMessage("you -> " + recipient + ": " + pmMessage));
                                this.replyHandler = recipientClient;
                                recipientClient.writer.sendMessage(new PrivateMessage(recipient, pmMessage));
                                recipientClient.replyHandler = this;
                            }
                            case "/reply" -> {
                                if (args.length != 2) {
                                    writer.sendMessage(new TextMessage("Usage: /reply <message>"));
                                    break;
                                }

                                if (this.replyHandler == null) {
                                    writer.sendMessage(new TextMessage("ERROR: No one to reply to"));
                                    break;
                                }

                                final String pmMessage = args[1];

                                this.replyHandler.writer.sendMessage(
                                        new PrivateMessage(this.replyHandler.name, pmMessage));
                                this.writer.sendMessage(
                                        new TextMessage("you -> " + this.replyHandler.name + ": " + pmMessage));

                            }
                            default -> {
                            }
                        }
                    }
                    case MESSAGE -> broadcast(message);
                    case SETTINGS -> {
                        StringReader reader = new StringReader(message);
                        boolean selfEcho = ((char) reader.read()) == '1';

                        writer.sendMessage(selfEcho);

                        this.clientSettings = new Settings(selfEcho);
                    }
                    default -> {
                    }
                }
            }
        } catch (IOException e) {
            disconnect();
            broadcast("SERVER: " + name + " has disconnected");
        }
    }

    void message(final String message) {
        writer.sendMessage(new TextMessage(message));
    }

    void broadcast(final String message) {
        broadcast(message, false);
    }

    void broadcast(final String message, final boolean overrideNoEcho) {
        for (ClientHandler client : CLIENT_HANDLERS) {
            if (this.clientID.equals(client.clientID) && !(clientSettings.selfEcho() || overrideNoEcho)) {
                continue;
            }
            client.message(message);
        }
        System.out.println(message);
    }

    private void disconnect() {
        try {
            socket.close();
            CLIENT_HANDLERS.remove(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ClientHandler getUser(String name) {
        for (var client : CLIENT_HANDLERS) {
            if (client.name.equals(name)) {
                return client;
            }
        }

        return null;
    }
}
