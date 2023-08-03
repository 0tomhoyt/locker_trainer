# 导入所需的模块
import socketserver
import json

import Router


# 定义一个请求处理器，它从BaseRequestHandler类继承
class ThreadedTCPRequestHandler(socketserver.BaseRequestHandler):
    # 覆盖“handle”方法，以处理每个连接的具体逻辑
    def handle(self):
        # 使用self.request.recv，从客户端读取数据。规定收发的数据都要是json
        data = self.request.recv(1024).decode('utf-8')
        # 打印从客户端收到的数据和客户端的地址
        print(f"接收到来自{self.client_address[0]}的数据：{data}")

        # 检查接收到的data是不是json类型
        try:
            json_data = json.loads(data)
        except json.JSONDecodeError:
            print(f'接收到的消息不是json格式 : {data}')
            return

        # 检查接收到的json是否包含event和data两个部分
        if 'event' not in json_data or 'data' not in json_data:
            print(f'接收到的socket消息json不包含event和data: {data}')
            return

        # 读取event和data
        event = json_data['event']
        data = json_data['data']

        response = Router.SocketConnectionRouter(event, data)
        message = json.dumps(response).encode('utf-8')
        # 将响应数据发送回客户端
        self.request.sendall(message)


# 定义一个服务器类，它从socketserver.ThreadingMixIn（多线程混入）和socketserver.TCPServer继承
# ThreadingMixIn类提供了管理工作线程池的功能，TCPServer类提供了处理TCP连接的功能
class ThreadedTCPServer(socketserver.ThreadingMixIn, socketserver.TCPServer):
    pass


# 开启服务器的函数
def start_server():
    # 设置服务器地址和端口
    HOST, PORT = "localhost", 12345
    # 创建服务器实例
    server = ThreadedTCPServer((HOST, PORT), ThreadedTCPRequestHandler)
    # 使服务器开始监听并处理连接
    # serve_forever将一直运行，直到服务器被关闭或者主程序被结束
    server.serve_forever()





if __name__ == "__main__":
    start_server()
