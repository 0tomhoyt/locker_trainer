package socketClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import controllers.MainController;

public class SocketClient2 {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MainController controller;

    public SocketClient2(MainController controller, String host, int port) throws IOException {
        this.controller = controller;
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void listen() throws IOException {
        String response;
        while ((response = in.readLine()) != null) {
            if (response.equals("1")) {
                controller.setJoinMatchButtonsVisible(1, true);
            }
        }
    }

    // 其他你可能需要的方法，例如发送消息
    public void send(String message) {
        out.println(message);
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
