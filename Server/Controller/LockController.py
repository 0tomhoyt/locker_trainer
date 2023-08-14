from DatabaseConnection import DBconnect, LockDB, UserDB
import json


def getLocksController(authToken, workstationId):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}"})
    isAdmin = UserDB.checkAdminToken(cnx, authToken)
    if isAdmin != 1:
        return json.dumps({"message": "权限不足", "code": 403})
    Locks = LockDB.getAllLocksForWorkstation(cnx, workstationId)
    lock_list = []
    for lock in Locks:
        if lock[4] == 0:
            continue
        lock_json = {
            "lockId":lock[0],
            "lockName": lock[3],
            "lockSerialNumber": lock[4],
            "difficulty": lock[5]
        }
        lock_list.append(lock_json)

    return json.dumps({"Locks": lock_list, "message": "查询成功", "code": 200})


def updateLockInfo(authToken, lockId, lockName, lockSerialNumber, difficulty):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}"})
    isAdmin = UserDB.checkAdminToken(cnx, authToken)
    if isAdmin != 1:
        return json.dumps({"message": "权限不足", "code": 403})
    result = LockDB.updateLockInfo(cnx, lockId, lockName, lockSerialNumber, difficulty)
    if type(result) == str:
        print(f"更新锁内容数据库连接错误，{result}")
        return json.dumps({"message": f"更新锁内容数据库连接错误，{result}"})

    return json.dumps({ "message": "查询成功", "code": 200})
