import json
from DatabaseConnection import UserDB, DBconnect, WorkStationDB
from datetime import datetime
from Util import Util


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
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}"})

    userResult = UserDB.login(cnx, userName, Password)
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
    role = userResult[3]
    if role == 1:
        return json.dumps({
            "loginSuccess": True,
            "code": 200,
            "authToken": AuthToken,
            "machineId": machineId,
            "userName": userName,
            "headerURL": headerUrl,
            "worklength": worklength,
            "message": "登录成功"
        })
    else:
        return json.dumps({
            "loginSucess": False,
            "code": 500,
            "message": "没有管理员权限"
        })


def getAllWorkersController(authToken):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}"})
    isAdmin = UserDB.checkAdminToken(cnx, authToken)
    print(isAdmin)
    if isAdmin != 1:
        return json.dumps({"message": "权限不足", "code": 403})

    result = UserDB.getAllWorker(cnx)

    workers_list = []
    for worker in result:
        worker_json = {
            "UserID": worker[0],
            "UserName": worker[1],
            "Password": worker[2],
            "Role": worker[3],
            "AuthToken": worker[4],
            "HeadUrl": worker[5],
            "EnrolledDate": worker[6].strftime('%Y-%m-%d') if worker[6] else None
        }
        workers_list.append(worker_json)
        print(workers_list)

    return json.dumps({"workers": workers_list, "message": "查询成功", "code": 200})


def addUser( userName, password, role):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}"})


    new_token = Util.generate_auth_token(30)
    UserDB.createUser(cnx, userName, password, role, new_token, '', datetime.today())

    return json.dumps({ "message": "添加成功", "code": 200})
