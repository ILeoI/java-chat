package Common;

public class IPAddress {

    private final String address;
    private final int port;

    public IPAddress(final String fullAddress) throws InvalidIPException {
        if (validIP(fullAddress)) {
            final String[] parts = fullAddress.split(":");
            this.address = parts[0];
            this.port = Integer.parseInt(parts[1]);
        } else {
            throw new InvalidIPException("Invalid IP");
        }
    }

    public IPAddress(final String address, final int port) throws InvalidIPException {
        final String fullAddress = address + ':' + port;
        if (validIP(fullAddress)) {
            this.port = port;
            this.address = address;
        } else {
            throw new InvalidIPException("Invalid IP");
        }
    }

    public static boolean validIP(String ip) throws InvalidIPException {
        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }

            String[] parts = ip.split(":");

            String[] ipv4 = parts[0].split("\\.");
            if (ipv4.length != 4) {
                return false;
            }

            for (String s : ipv4) {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255)) {
                    return false;
                }
            }
            if (ip.endsWith(".")) {
                return false;
            }

            int port = Integer.parseInt(parts[1]);
            return port <= 65535 && port >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public final String getAddress() {
        return address;
    }

    public final int getPort() {
        return port;
    }
}
