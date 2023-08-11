import json
from DatabaseConnection import MachineDB,DBconnect,UserDB,WorkStationDB


def machineOnController(machineID, status):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message":f"连接数据库失败:{e}"})
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
        return json.dumps({"message":f"机器开关数据库连接错误，{dbresult}"})

def getWorkStationsStatusController(authToken):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}"})
    isAdmin = UserDB.checkAdminToken(cnx, authToken)
    if isAdmin != 1:
        return json.dumps({"message": "权限不足", "code": 403})

    result = WorkStationDB.getAllWorkstationStatuses(cnx)
    workstation_list = []
    for workstation in result:
        workstation_json = {
            "workstationID":workstation[0],
            "machineID":workstation[1],
            "isLoggedIn":workstation[2],
            "userID":workstation[3]
        }
        workstation_list.append(workstation_json)

    return json.dumps({"workstations": workstation_list, "message": "查询成功", "code": 200})


