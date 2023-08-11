package socketClient;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;


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
        pw.write(message+"\n");
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
//        SocketClient client = new SocketClient("localhost", 5001);
//        client.connect();
//
//        String jsonString = "{ \"event\": \"workerLogin\", \"data\": { \"username\": \"hyt\", \"password\": \"123\", \"machineId\": 1, \"workstationId\": 1 } }";
//        client.send(jsonString);
//
//        System.out.println("Server says " + client.receive());
//        client.close();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = (Future<String>) executorService.submit(() -> {
            try {
                // 接收消息
                SocketClient client = new SocketClient("localhost", 5001);
                client.connect();

                String jsonString = "{ \"event\": \"workerLogin\", \"data\": { \"username\": \"hyt\", \"password\": \"123\", \"machineId\": 1, \"workstationId\": 1 } }";
                client.send(jsonString);

                System.out.println("Server says " + client.receive());
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            String receivedMessage = future.get(5, TimeUnit.SECONDS); // 设置超时时间为5秒
            // 处理接收到的消息
            System.out.println("Received message: " + receivedMessage);
        } catch (TimeoutException e) {
            // 超时处理
            System.out.println("Socket receive timeout: " + e.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            // 其他异常处理
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        System.out.println("hello world!");
    }
}