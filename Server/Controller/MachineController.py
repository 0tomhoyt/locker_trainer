import json
from DatabaseConnection import MachineDB,DBconnect


def machineOnController(machineID, status):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return f"连接数据库失败:{e}"
    machineResult = MachineDB.getMachineStatus(cnx,machineID)
    if machineResult is None:
        return json.dumps({
            "machinestatus": False,
            "code": 500,
        })
    dbresult = MachineDB.machineOn_Off(cnx, machineID, status)
    if dbresult == 1:
        print(f"调整机器状态为{status}")
        DBconnect.databaseDisconnect(cnx)
        return json.dumps({
            "machinestatus": True,
            "code": 200,
        })
    else:
        DBconnect.databaseDisconnect(cnx)
        print(f"机器开关数据库连接错误，{dbresult}")
        return f"机器开关数据库连接错误，{dbresult}"


