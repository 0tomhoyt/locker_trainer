import socket, json


def start_client():
    host = 'localhost'  # 服务器的主机名或IP地址
    port = 12345  # 服务器的端口号

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect((host, port))  # 连接到服务器
        # data = {'event': 'machineStart',
        #         "data":
        #             {
        #                 "machineId": "123",
        #             }
        #         }
        # message = json.dumps(data)
        # s.sendall(message.encode('utf-8'))  # 发送消息，需要转为 bytes 对象
        #
        # # 接收服务器的回应
        # data = s.recv(1024)
        # print('Received', repr(data))

        data = {'event': 'workerLogin',
                "data":
                    {
                        "username": "hyt",
                        "password": "hjqCYS1301",
                        "machineId": 1,
                        "workstationId": 1
                    }
                }
        message = json.dumps(data)
        s.sendall(message.encode('utf-8'))  # 发送消息，需要转为 bytes 对象

        # 接收服务器的回应
        data = s.recv(1024)
        print('Received', json.loads(data.decode('utf-8')))


if __name__ == "__main__":
    start_client()
