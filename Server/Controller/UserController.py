import json
from DatabaseConnection import UserDB, DBconnect, WorkStationDB
from datetime import datetime


def WorkerLogin(username, password, machineId, workstationId):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}"})

    userResult = UserDB.login(cnx, username, password)
    if userResult is None:
        return json.dumps({"loginSuccess": False, "code": 500, "message": "不存在用户"})
    if type(userResult) == str:
        print(f"用户登陆数据库连接错误，{userResult}")
        return json.dumps({"message": f"用户登陆数据库连接错误，{userResult}"})
    userId = userResult[0]
    AuthToken = userResult[4]
    userName = userResult[1]
    headerUrl = userResult[5]
    enrollDate = userResult[6]
    worklength = (datetime.today().date() - enrollDate).days

    workstationResult = WorkStationDB.findWorkstation(cnx, workstationId, machineId)
    if workstationResult is None:
        return json.dumps({"loginSuccess": False, "code": 500, "message": "不存在workstation"})
    if type(workstationResult) == str:
        print(f"workstation数据库连接错误，{userResult}")
        return json.dumps({"message": f"workstation数据库连接错误，{userResult}"})

    if type(WorkStationDB.workstationLogin(cnx, workstationId, userId)) == str:
        print(f"workstation用户登陆状态更新错误，{userResult}")
        return json.dumps({"message": f"workstation数据库连接错误，{userResult}"})
    DBconnect.databaseDisconnect(cnx)

    return json.dumps({
        "loginSuccess": True,
        "code": 200,
        "authToken": AuthToken,
        "machineId": machineId,
        "workstationId": workstationId,
        "userName": userName,
        "headerURL": headerUrl,
        "worklength": worklength,
        "message": "登录成功"
    })


def workerLogout(machineId, workstationId):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}"})

    workstationResult = WorkStationDB.findWorkstation(cnx, workstationId, machineId)
    if workstationResult is None:
        return json.dumps({"logoutSuccess": False, "code": 500, "message": "不存在workstation"})
    if type(workstationResult) == str:
        print(f"workstation数据库连接错误")
        return json.dumps({"message": f"workstation数据库连接错误"})

    logoutResult = WorkStationDB.workstationLogout(cnx, workstationId)
    if type(logoutResult) == str:
        print(f"workstation用户登陆状态更新错误")
        return json.dumps({"message": f"workstation数据库连接错误"})

    DBconnect.databaseDisconnect(cnx)

    return json.dumps({
        "logoutSuccess": True,
        "code": 200,
        "machineId": machineId,
        "workstationId": workstationId
    })



def adminLogin(userName, Password, machineId):
    return json.dumps({
        "a": 1
    })
