import socket, json
import threading

import Router
from DatabaseConnection import ServerDB, DBconnect


class SubServerClient:
    def __init__(self, host, port, main_server_host, main_server_port):
        self.host = host
        self.port = port
        self.main_server_host = main_server_host
        self.main_server_port = main_server_port
        self.server_socket = None
        self.main_server_socket = None

    def handle_client_connection(self, client_conn, addr):
        print(f"Connected to {addr}")
        while True:
            message_from_client = client_conn.recv(1024).decode('utf-8')
            # 检查
            try:
                json_data = json.loads(message_from_client)
            except json.JSONDecodeError:
                print(f'接收到来自主服务器的消息不是json格式 : {message_from_client}')
                return
            if 'event' not in json_data or 'data' not in json_data:
                print(f'接收到来自客户端的消息格式不正确: {json_data}')
                return
            # 主要逻辑如果主服务器连接失败
            if self.check_main_server_connection() != 1:
                # 读取event和data
                event = json_data['event']
                data = json_data['data']
                Router.client_server_event_router(event, data)
            else:
                self.main_server_socket.sendall(message_from_client.encode('utf-8'))

    def handle_main_server_connection(self):
        while True:
            message_from_server = self.main_server_socket.recv(1024).decode('utf-8')
            try:
                json_data = json.loads(message_from_server)
            except json.JSONDecodeError:
                print(f'接收到来自主服务器的消息不是json格式 : {message_from_server}')
                return
            if 'replyMessage' not in json_data:
                print(f'接收到来自主服务器的消息不是回复消息: {message_from_server}')
                return
            # 直接转发
            self.server_socket.sendall(message_from_server.encode('utf-8'))

    # 检查主服务器连接是否有效
    def check_main_server_connection(self):
        try:
            if self.main_server_socket is None or not self.main_server_socket.is_valid():
                return 0
        except Exception as e:
            print(f"检查主服务器连接时出错: {e}")
        return 1

    def connect_to_main_server(self):
        try:
            self.main_server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.main_server_socket.connect((self.main_server_host, self.main_server_port))
            print(f"Connected to main server at {self.main_server_host}:{self.main_server_port}")
            return 1
        except Exception as e:
            print(f"连接主服务器失败: {e}")

    def start(self):
        # Start server to handle client messages
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket.bind((self.host, self.port))
        self.server_socket.listen(5)
        print(f"Server started at {self.host}:{self.port}")

        self.connect_to_main_server()

        threading.Thread(target=self.handle_main_server_connection, daemon=True).start()

        while True:
            client_conn, addr = self.server_socket.accept()
            threading.Thread(target=self.handle_client_connection, args=(client_conn, addr), daemon=True).start()

    def close(self):
        self.server_socket.close()
        self.main_server_socket.close()


class MainServerClient:
    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.server_socket = None

    def handle_connection(self, client_conn, addr):
        print(f"Connected to {addr}")
        while True:
            message_from_client = client_conn.recv(1024).decode('utf-8')
            # 检查
            try:
                json_data = json.loads(message_from_client)
            except json.JSONDecodeError:
                print(f'接收到来自客户端的消息不是json格式 : {message_from_client}')
                return
            if 'event' not in json_data or 'data' not in json_data:
                print(f'接收到来自客户端的消息格式不正确: {json_data}')
                return
            # 处理消息，回复客户端
            event = json_data['event']
            data = json_data['data']
            reply_message = Router.main_server_event_router(event, data)
            client_conn.sendall(reply_message.encode('utf-8'))

    def start(self):
        # Start server to handle client messages
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket.bind((self.host, self.port))
        self.server_socket.listen(5)
        print(f"Server started at {self.host}:{self.port}")

        while True:
            client_conn, addr = self.server_socket.accept()
            threading.Thread(target=self.handle_connection, args=(client_conn, addr), daemon=True).start()

    def close(self):
        self.server_socket.close()


def main():
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return 1

    self = ServerDB.get_self_server(cnx)
    main_server = ServerDB.get_main_server(cnx)
    self_ip, self_port = self[2].split(":")
    main_server_ip, main_server_port = main_server[2].split(":")
    print(self_ip,self_port,main_server_port,main_server_ip)
    if self == main_server:
        print("主服务器启动")
        main_server_client = MainServerClient(self_ip,int(self_port))
        main_server_client.start()
    else:
        print("分服务器启动")
        sub_server_client = SubServerClient(self_ip, int(self_port), main_server_ip, int(main_server_port))
        sub_server_client.start()


if __name__ == "__main__":
    main()
