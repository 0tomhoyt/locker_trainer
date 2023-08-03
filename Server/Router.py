from Controller import MachineController
def SocketConnectionRouter(event, data):
    # 根据不同的事件进行不同的处理
    if event == 'machineStart':
        if 'machineId' not in data:
            print('Received data does not contain machineID.')
            return 'Received data does not contain machineID.'
        return MachineController.machineOnController(data['machineId'], 1)
    elif event == 'machineStop':
        if 'machineId' not in data:
            print('Received data does not contain machineID.')
            return 'Received data does not contain machineID.'
        return MachineController.machineOnController(data['machineId'], 0)
    else:
        print(f"Unknown event: {event}")
        return 1