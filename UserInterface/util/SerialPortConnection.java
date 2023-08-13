//package util;
//
//public class SerialPortConnection {
//    private Queue<String> portOutQueue;
//    private Queue<Byte> portInQueue;
//    // 其他成员变量，例如线程和定时器
//
//    private Thread threadComSend;
//    private Thread threadComReceive;
//    private Thread threadDataHandle;
//    private Timer timerDevStatusCmd;
//    private SerialPort serialPortDevPort;
//
//    public SerialPortConnection() {
//        portOutQueue = new LinkedList<>();
//        portInQueue = new LinkedList<>();
//        deviceCtrlInit();
//        devPrintInit();
//    }
//// 资源清理
////    @Override
////    protected void finalize() throws Throwable {
////        // 清理资源，例如停止线程和关闭串行端口
////        // ...
////        super.finalize();
////    }
//
//
//
//    private void deviceCtrlInit() {
//        // 初始化线程
//        threadComSend = new Thread(this::startSerialPortSend);
//        threadComReceive = new Thread(this::startSerialPortRcv);
//        threadDataHandle = new Thread(this::handleRcvData);
//
//        // 初始化定时器
//        timerDevStatusCmd = new Timer(65); // 设置间隔
//        timerDevStatusCmd.addActionListener(e -> {
//            // 定时器逻辑
//        });
//
//        // 初始化串行端口
//        serialPortDevPort = new SerialPort(PORT_NAME);
//        serialPortDevPort.setBaudRate(BAUD_RATE);
//        // 其他串行端口设置
//    }
//
//
//    private void devPrintInit() {
//        // 初始化代码
//    }
//
//    // 其他方法
//}
//
