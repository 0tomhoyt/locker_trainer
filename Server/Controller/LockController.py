from DatabaseConnection import DBconnect, LockDB, UserDB,Lock_unlockDB
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


def getUnlockInfoBySerialNum(authToken, serialNumber):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}"})
    isAdmin = UserDB.checkAdminToken(cnx, authToken)
    if isAdmin != 1:
        return json.dumps({"message": "权限不足", "code": 403})
    unlocks = Lock_unlockDB.getUnlockBySerialNumber(cnx,serialNumber)
    unlocklist = []
    for unlock in unlocks:
        unlock_json = {
            "unlockId":unlock[0],
            "unlockTime":unlock[1],
            "unlockDuration":unlock[2],
            "trainingId":unlock[3],
            "lockId":unlock[4],
            "lockSerialNum":unlock[5],
            "lockName":unlock[6],
            "difficulty":unlock[7],
        }
        unlocklist.append(unlock_json)

    return json.dumps({"unlocks": unlocklist, "message": "查询成功", "code": 200})
