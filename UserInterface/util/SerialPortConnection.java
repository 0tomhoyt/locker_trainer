package util;

import com.fazecast.jSerialComm.SerialPort;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SerialPortConnection {
    private final LinkedBlockingQueue<byte[]> sendBuffer;
    private final LinkedBlockingQueue<byte[]> receiveBuffer;
    private final SerialPort serialPort;
    private Thread sendThread;
    private Thread receiveThread;

    public SerialPortConnection(String portName, int baudRate) {
        sendBuffer = new LinkedBlockingQueue<>();
        receiveBuffer = new LinkedBlockingQueue<>();
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(baudRate);
        initSendThread();
        initReceiveThread();
    }

    public void start() {
        if (serialPort.openPort()) {
            sendThread.start();
            receiveThread.start();
        } else {
            System.err.println("Failed to open the port.");
        }
    }

    public void stop() {
        serialPort.closePort();
        sendThread.interrupt();
        receiveThread.interrupt();
    }

    public void sendData(byte[] data) {
        sendBuffer.offer(data);
    }

    public byte[] receiveData(long timeout, TimeUnit unit) throws InterruptedException {
        return receiveBuffer.poll(timeout, unit);
    }

    public byte[] receiveSendData() throws InterruptedException {
        return sendBuffer.take();
    }
    public int getReceiveBufferSize() {
        return receiveBuffer.size();
    }

    public int getSendBufferSize(){
        return sendBuffer.size();
    }

    public LinkedBlockingQueue<byte[]> getReceiveBuffer() {
        return receiveBuffer;
    }

    public LinkedBlockingQueue<byte[]> getSendBuffer() {
        return sendBuffer;
    }

    private void initSendThread() {
        sendThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    byte[] dataToSend = sendBuffer.take();
                    serialPort.writeBytes(dataToSend, dataToSend.length);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private void initReceiveThread() {
        receiveThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (serialPort.bytesAvailable() > 0) {
                    byte[] readBuffer = new byte[serialPort.bytesAvailable()];
                    int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
                    if (numRead > 0) {
                        receiveBuffer.offer(readBuffer);
                    }
                }
            }
        });
    }

    private byte[]  stringToByte(String hexString){
        hexString = hexString.replace(" ","");
        byte[] hexData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexData.length; i++) {
            int index = i * 2;
            int value = Integer.parseInt(hexString.substring(index, index + 2), 16);
            hexData[i] = (byte) value;
        }
        return hexData;
    }

    public void sendByHexString(String hexString){
        byte[] hexData = this.stringToByte(hexString);
        this.sendData(hexData);
//        StringBuilder sting = new StringBuilder();
//        for (byte b : hexData) {
//            sting.append(String.format("%02X ", b));
//        }
//        System.out.println(sting.toString().trim());
    }

    public static void main(String[] args) throws InterruptedException {
        SerialPortConnection commManager = new SerialPortConnection("COM3", 9600);
        commManager.start();
        String hexString = "AA 01 00 01 00 02 55 00 00";

        byte[] hexData = commManager.stringToByte(hexString);
        // 发送数据
        commManager.sendData(hexData);
        System.out.println("发送数据");
        System.out.println(new String(hexData));

        // 接收数据
        byte[] receivedData = commManager.receiveData(3,TimeUnit.SECONDS);
        System.out.println("接收数据");
        System.out.println(new String(receivedData));

        commManager.stop();
    }

}
