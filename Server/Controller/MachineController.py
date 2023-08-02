from ..DatabaseConnection import MachineStatus


def machineOnController(machineID, status):
    try:
        cnx = MachineStatus.databaseConnect()
    except Exception as e:
        print("Failed to connect to the database:", e)
        return False

    status = MachineStatus.getMachineStatus(cnx, machineID)
    print(f"现在机器{machineID}的状态是{status}")

    MachineStatus.machineOn_Off(cnx, machineID, status)
    print(f"调整机器状态为{status}")

    status = MachineStatus.getMachineStatus(cnx, machineID)
    print(f"现在机器{machineID}的状态是{status}")
    return True
