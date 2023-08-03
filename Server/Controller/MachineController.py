from ..DatabaseConnection import MachineDB,DBconnect


def machineOnController(machineID, status):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("Failed to connect to the database:", e)
        return False

    status = MachineDB.getMachineStatus(cnx, machineID)
    print(f"现在机器{machineID}的状态是{status}")

    MachineDB.machineOn_Off(cnx, machineID, status)
    print(f"调整机器状态为{status}")

    status = MachineDB.getMachineStatus(cnx, machineID)
    print(f"现在机器{machineID}的状态是{status}")

    DBconnect.databaseDisconnect(cnx)
    return True
