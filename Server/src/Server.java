import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public final class Server {
    private static final int DEFAULT_PORT = 5001;
    public final String MOTD = """
            Welcome to the Server!
            Rules:
                - Rule 1
                - Rule 2
                
            Have Fun
            """;
    private final String serverName;
    private final int serverPort;
    private final ServerSocket serverSocket;

    public Server(final String name, final int port) throws IOException {
        this.serverName = name;
        this.serverPort = port;

        this.serverSocket = new ServerSocket(port);
    }

    public static void main(final String[] args) {
        try {
            Server s = new Server("MyServer", DEFAULT_PORT);
            s.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getServerName() {
        return serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void run() {
        try {

            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("Accepted connection from: " + socket);
                new ClientHandler(socket, this).start();
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
