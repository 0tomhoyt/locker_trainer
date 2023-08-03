from Controller import MachineController, UserController


def SocketConnectionRouter(event, data):
    # 根据不同的事件进行不同的处理
    if event == 'machineStart':
        if 'machineId' not in data:
            print('machineStart: 收到的data包不正确')
            return 'machineStart: 收到的data包不正确'
        return MachineController.machineOnController(data['machineId'], 1)
    elif event == 'machineStop':
        if 'machineId' not in data:
            print('machineStop: 收到的data包不正确')
            return 'machineStop: 收到的data包不正确'
        return MachineController.machineOnController(data['machineId'], 0)
    elif event == 'workerLogin':
        if "username" not in data or "password" not in data or "machineId" not in data or "workstationId" not in data:
            print('workerLogin: 收到的data包不正确')
            return 'workerLogin: 收到的data包不正确'
        return UserController.WorkerLogin(data['username'], data['password'], data['machineId'], data['workstationId'])

    else:
        print(f"Unknown event: {event}")
        return 1
