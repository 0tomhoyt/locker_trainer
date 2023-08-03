from Controller import MachineController, UserController


def SocketConnectionRouter(event, data):
    # 根据不同的事件进行不同的处理
    if event == 'machineStart':
        if 'machineId' not in data:
            print(f'machineStart: 收到的data包不正确{data}')
            return f'machineStart: 收到的data包不正确{data}'
        return MachineController.machineOnController(data['machineId'], 1)
    elif event == 'machineStop':
        if 'machineId' not in data:
            print(f'machineStop: 收到的data包不正确{data}')
            return f'machineStop: 收到的data包不正确{data}'
        return MachineController.machineOnController(data['machineId'], 0)
    elif event == 'workerLogin':
        if "username" not in data or "password" not in data or "machineId" not in data or "workstationId" not in data:
            print(f'workerLogin: 收到的data包不正确{data}')
            return f'workerLogin: 收到的data包不正确{data}'
        return UserController.WorkerLogin(data['username'], data['password'], data['machineId'], data['workstationId'])
    elif event == 'workerLogout':
        if "machineId" not in data or "workstationId" not in data:
            print(f'workerLogout: 收到的data包不正确{data}')
            return f'workerLogout: 收到的data包不正确{data}'
        return UserController.workerLogout(data['machineId'], data['workstationId'])
    elif event == 'adminLogin':
        if "username" not in data or "password" not in data or "machineId" not in data:
            print(f'admin login: 收到的data包不正确{data}')
            return f'admin login: 收到的data包不正确{data}'
        return UserController.adminLogin(data['username'], data['password'], data['machineId'])

    else:
        print(f"Unknown event: {event}")
        return f"Unknown event: {event}"
