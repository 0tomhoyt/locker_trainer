import socket, json


# import unittest
# from Controller import MachineController, UserController
# from unittest.mock import patch
# from Router import SocketConnectionRouter


def start_client(event, data):
    host = 'localhost'  # 服务器的主机名或IP地址
    port = 5001  # 服务器的端口号

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect((host, port))  # 连接到服务器
        data = {'event': event,
                "data": data
                }
        message = json.dumps(data)
        s.sendall(message.encode('utf-8'))  # 发送消息，需要转为 bytes 对象

        # 接收服务器的回应
        data = s.recv(1024)
        print(json.loads(data.decode('utf-8')))


if __name__ == "__main__":
    # print("测试1：机器开关，正确")
    # event = 'machineStart'
    # data = {'machineId': "1"}
    # start_client(event, data)
    # event = 'machineStop'
    # data = {'machineId': "1"}
    # start_client(event, data)
    #
    # print("测试2：机器开关，不存在的机器Id")
    # event = 'machineStart'
    # data = {'machineId': "555"}
    # start_client(event, data)
    #
    # print("测试3：机器开关，不正确的data格式")
    # event = 'machineStart'
    # data = {'machineID': "555"}
    # start_client(event, data)
    #
    # print("测试4：工人登陆，正确")
    # event = 'workerLogin'
    # data = {'username': "hyt",
    #         "password": "hjqCYS1301",
    #         "machineId": 1,
    #         "workstationId": 1}
    # start_client(event, data)
    #
    # print("测试5：工人登陆，错误的password")
    # event = 'workerLogin'
    # data = {'username': "hyt",
    #         "password": "hjqCYS1302",
    #         "machineId": 1,
    #         "workstationId": 1}
    # start_client(event, data)
    #
    # print("测试6：工人登陆，错误的machineId")
    # event = 'workerLogin'
    # data = {'username': "hyt",
    #         "password": "hjqCYS1301",
    #         "machineId": 2,
    #         "workstationId": 1}
    # start_client(event, data)
    #
    # print("测试7：工人登陆，错误的workstationId")
    # event = 'workerLogin'
    # data = {'username': "hyt",
    #         "password": "hjqCYS1301",
    #         "machineId": 1,
    #         "workstationId": 2}
    # start_client(event, data)
    #
    # print("测试8：工人登陆，错误的data格式")
    # event = 'workerLogin'
    # data = {'username': "hyt",
    #         "password": "hjqCYS1301"}
    # start_client(event, data)
    #
    # print("测试9：工人登出，正确")
    # event = 'workerLogout'
    # data = {"machineId": 1,
    #         "workstationId": 1}
    # start_client(event, data)
    #
    # print("测试10：工人登出，错误的机器")
    # event = 'workerLogout'
    # data = {"machineId": 2,
    #         "workstationId": 1}
    # start_client(event, data)
    # print("测试11：查看训练历史")
    # event = 'workerGetSelfTrainingHistory'
    # data = {"authToken": 123456}
    # start_client(event, data)
    #
    # print("测试12：查看训练历史,输入错误的token")
    # event = 'workerGetSelfTrainingHistory'
    # data = {"authToken": 22222}
    # start_client(event, data)

    # print("测试13：创建新训练")
    # event = 'startNewTraining'
    # data = {"authToken": 123456,"workstationID":1,"difficulty":5,"totalTime":15}
    # start_client(event, data)
    #
    # print("测试14：创建新训练,错误测试")
    # event = 'startNewTraining'
    # data = {"authToken": 123456,"workstationID":2,"difficulty":5,"totalTime":15}
    # start_client(event, data)
    # print("测试15：创建新训练,错误测试")
    # event = 'startNewTraining'
    # data = {"authToken": 2222,"workstationID":1,"difficulty":5,"totalTime":15}
    # start_client(event, data)
    # print("测试16：更新训练,正确测试")
    # event = 'updateTraining'
    # data = {"authToken": 123456,"trainingID":22,"score":5,"unlockedNum":15,"isOn":1}
    # start_client(event, data)
    # print("测试17：更新训练,错误测试")
    # event = 'updateTraining'
    # data = {"authToken": 22222,"trainingID":22,"score":5,"unlockedNum":15,"isOn":1}
    # start_client(event, data)
    # print("测试18：更新训练,错误测试")
    # event = 'updateTraining'
    # data = {"authToken": 123456,"trainingID":254654,"score":5,"unlockedNum":15,"isOn":1}
    # start_client(event, data)
    # print("测试19：结束训练,正确测试")
    # event = 'stopTraining'
    # data = {"authToken": 123456,"trainingID":22}
    # start_client(event, data)
    # print("测试19：结束训练,错误测试")
    # event = 'stopTraining'
    # data = {"authToken": 123456,"trainingID":28}
    # start_client(event, data)
    print("测试20：获取所有工人信息,正确测试")
    event = 'getWorkerStatus'
    data = {"authToken": "888666"} # 确保这是有效的管理员令牌
    start_client(event, data)

    print("测试21：获取所有工人信息,错误测试")
    event = 'getWorkerStatus'
    data = {"authToken": "123456"} # 使用无效的管理员令牌
    start_client(event, data)
    print("测试22：获取所有工人信息,错误测试")
    event = 'getWorkerStatus'
    data = {"authToken": "555555"} # 使用无效的管理员令牌
    start_client(event, data)


# unittest.main()
