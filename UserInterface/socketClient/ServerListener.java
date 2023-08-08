package socketClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerListener implements Runnable {
    private String serverAddress;
    private int serverPort;

    public ServerListener(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        try {
            // 创建Socket连接
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to server: " + serverAddress + ":" + serverPort);

            // 创建输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 持续监听服务器发送的数据
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Received from server: " + message);
                // 在这里处理服务器发送的数据
            }

            // 当服务器关闭连接后，读取流会返回null，这时候可以在这里处理断开连接的逻辑

            // 关闭连接和流
            reader.close();
            socket.close();
            System.out.println("Connection closed.");

        } catch (IOException e) {
            System.err.println("Error while listening to server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 12345;

        ServerListener serverListener = new ServerListener(serverAddress, serverPort);
        Thread listenerThread = new Thread(serverListener);
        listenerThread.start();
    }
}
