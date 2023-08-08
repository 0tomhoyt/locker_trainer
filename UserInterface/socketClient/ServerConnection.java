package socketClient;

public class ServerConnection implements Runnable {
    private String serverAddress;
    private int serverPort;
    private boolean connected;

    public ServerConnection() {
        this.serverAddress = "localhost";
        this.serverPort = 12345;
        this.connected = false;
    }

    @Override
    public void run() {
        while (true) {
            if (!connected) {
                // 尝试连接服务器
                System.out.println("Connecting to server...");
                SocketClient socketClient = new SocketClient(serverAddress, serverPort);
                // 调用checkMainServerConnection，来控制connected
                // ...

                if(connected){
                    System.out.println("Connected to server.");
                }
                else {
                    System.out.println("Didn't connect to server");
                }
            } else {
                try {
                    // 与服务器断开连接时，尝试重新连接
                    Thread.sleep(5000); // 等待一段时间后重新checkConnection
                    // 调用checkMainServerConnection，来控制connected
                    // ...

                } catch (InterruptedException e) {
                    System.err.println("Thread interrupted: " + e.getMessage());
                }
            }
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public static void main(String[] args) {
        ServerConnection serverConnection = new ServerConnection();
        Thread connectionThread = new Thread(serverConnection);
        connectionThread.start();
    }
}

