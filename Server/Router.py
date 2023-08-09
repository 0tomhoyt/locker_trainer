import json

from Controller import MachineController, UserController


# 分服务器提供的route
def client_server_event_router(event, data):
    return json.dumps({"message": "当前分服务器未连接主服务器，无法处理"})


# 主服务器提供的route
def main_server_event_router(event, data):
    # 如果本机是主服务器，根据不同的事件进行不同的处理
    if event == 'machineStart':
        if 'machineId' not in data:
            print(f'machineStart: 收到的data包不正确{data}')
            return json.dumps({"message": f'machineStart: 收到的data包不正确{data}'})
        return MachineController.machineOnController(data['machineId'], 1)
    elif event == 'machineStop':
        if 'machineId' not in data:
            print(f'machineStop: 收到的data包不正确{data}')
            return json.dumps({"message": f'machineStart: 收到的data包不正确{data}'})
        return MachineController.machineOnController(data['machineId'], 0)
    elif event == 'workerLogin':
        if "username" not in data or "password" not in data or "machineId" not in data or "workstationId" not in data:
            print(f'workerLogin: 收到的data包不正确{data}')
            return json.dumps({"replyMessage": True, "message": f'machineStart: 收到的data包不正确{data}'})
        return UserController.WorkerLogin(data['username'], data['password'], data['machineId'], data['workstationId'])
    elif event == 'workerLogout':
        if "machineId" not in data or "workstationId" not in data:
            print(f'workerLogout: 收到的data包不正确{data}')
            return json.dumps({"replyMessage": True, "message": f'machineStart: 收到的data包不正确{data}'})
        return UserController.workerLogout(data['machineId'], data['workstationId'])
    elif event == 'adminLogin':
        if "username" not in data or "password" not in data or "machineId" not in data:
            print(f'admin login: 收到的data包不正确{data}')
            return json.dumps({"replyMessage": True, "message": f'machineStart: 收到的data包不正确{data}'})
        return UserController.adminLogin(data['username'], data['password'], data['machineId'])

    else:
        print(f'unknown event: {event},data:{data}')
        return json.dumps({ "message": f'unknown event: {event},data:{data}'})
