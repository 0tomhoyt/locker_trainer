package socketClient;

import java.net.*;
import java.io.*;


public class SocketClient {
    private final String serverName;
    private final int port;
    private Socket client;

    public SocketClient(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
    }

    public void connect() throws IOException {
        System.out.println("Connecting to " + serverName + " on port " + port);
        client = new Socket(serverName, port);
        System.out.println("Just connected to " + client.getRemoteSocketAddress());
    }

    public void send(String message) throws IOException {
        OutputStream os = client.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        pw.write(message);
        pw.flush();

        client.shutdownOutput();
    }

    public String receive() throws IOException {
        InputStream is = client.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String info = br.readLine();

        br.close();
        is.close();
        return info;
    }

    public void close() throws IOException {
        client.close();
    }

    public static void main(String[] args) throws IOException {
        SocketClient client = new SocketClient("localhost", 12345);
        client.connect();

        String jsonString = "{ \"event\": \"workerLogin\", \"data\": { \"username\": \"hyt\", \"password\": \"hjqCYS1301\", \"machineId\": 1, \"workstationId\": 1 } }";
        client.send(jsonString);

        System.out.println("Server says " + client.receive());
        client.close();
    }
}