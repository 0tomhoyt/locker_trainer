import json
from DatabaseConnection import TrainingDB, DBconnect, UserDB, Lock_unlockDB, LockDB


def workerGetSelfTrainingHistory(authToken):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}"})
    try:
        userid = UserDB.getUserIdFromAuthToken(cnx, authToken)
        if userid is None:
            return json.dumps({"message": "找不到对应的用户ID", "code": 500})
    except Exception as e:
        return json.dumps({"message": f"{e}", "code": 500})

    training_records = TrainingDB.getTrainingByUserID(cnx, userid[0])
    # 如果没有训练记录，则返回错误
    if not training_records:
        return json.dumps({"message": "没有找到对应的训练记录", "code": 500})
    # 将训练记录整理成字典格式，并添加到列表中
    training_list = []
    for record in training_records:
        unlocks = Lock_unlockDB.getAllunlock(cnx, record[0])
        unlock_list = []
        for unlock in unlocks:
            unlock_list.append({
                "lockId": unlock[4],
                "lockSerialNumber": unlock[5],
                "lockName": unlock[6],
                "difficulty": unlock[7]
            })
        training_dict = {
            "TrainingID": record[0],
            "IsMatch": record[1],
            "MatchID": record[2],
            "UserID": record[3],
            "WorkstationID": record[4],
            "Score": record[5],
            "UnlockedNum": record[6],
            "TotalTime": record[7],
            "TrainingType": record[8],
            "Difficulty": record[9],
            "IsOn": record[10],
            "unlockList": unlock_list
        }
        training_list.append(training_dict)

    return json.dumps({"message": "获取训练列表成功", "code": 200, "trainingList": training_list})


def getMatchTrainings(authToken, matchID):
    return 0


def addTrainingRecord(authToken, workstationId, difficulty, totalTime):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}", "code": 500})

    try:
        user = UserDB.getUserIdFromAuthToken(cnx, authToken)
        if user is None:
            return json.dumps({"message": "找不到对应的用户ID", "code": 500})
    except Exception as e:
        return json.dumps({"message": f"{e}", "code": 500})
    userId = user[0]
    # 设置默认值
    IsMatch = 0
    MatchID = -1
    Score = 0
    UnlockedNum = 0
    TrainingType = 1
    IsOn = 1

    result = TrainingDB.createTraining(cnx, IsMatch, MatchID, userId, workstationId, Score, UnlockedNum, totalTime,
                                       TrainingType, difficulty, IsOn)
    if isinstance(result, str):  # 如果返回错误信息
        return json.dumps({"message": result, "code": 500})
    else:
        return json.dumps({"message": "训练记录创建成功", "code": 200, "TrainingID": result})


def updateTrainingController(authToken, trainingID, score, unlockedNum, IsOn):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}", "code": 500})
    try:
        user = UserDB.getUserIdFromAuthToken(cnx, authToken)
        var = TrainingDB.getOneTraining(cnx, trainingID)
        if user is None:
            return json.dumps({"message": "找不到对应的用户ID", "code": 500})
        elif var is None:
            return json.dumps({"message": "找不到对应的trainingID", "code": 500})
    except Exception as e:
        return json.dumps({"message": f"{e}", "code": 500})

    # 更新训练记录的得分
    update_result_score = TrainingDB.updateTrainingScore(cnx, trainingID, score)
    if type(update_result_score) == str:
        return json.dumps({"message": f"更新得分失败:{update_result_score}", "code": 500})

    # 更新训练记录的解锁数量
    update_result_unlockedNum = TrainingDB.updateTrainingUnlockedNum(cnx, trainingID, unlockedNum)
    if type(update_result_unlockedNum) == str:
        return json.dumps({"message": f"更新解锁数量失败:{update_result_unlockedNum}", "code": 500})

    # 更新训练记录的状态
    if IsOn == 1:
        update_result_IsOn = TrainingDB.setTrainingOn(cnx, trainingID)
    else:
        update_result_IsOn = TrainingDB.setTrainingOff(cnx, trainingID)
    if type(update_result_IsOn) == str:
        return json.dumps({"message": f"更新状态失败:{update_result_IsOn}", "code": 500})

    return json.dumps({"message": "更新训练成功", "code": 200})


def stopTrainingController(authToken, trainingID, score, unlockedNum, unlocks):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}", "code": 500})
    try:
        user = UserDB.getUserIdFromAuthToken(cnx, authToken)
        var = TrainingDB.getOneTraining(cnx, trainingID)
        if user is None:
            return json.dumps({"message": "找不到对应的用户ID", "code": 500})
        elif var is None:
            return json.dumps({"message": "找不到对应的trainingID", "code": 500})
    except Exception as e:
        return json.dumps({"message": f"{e}", "code": 500})

    # 更新训练记录的得分
    update_result_score = TrainingDB.updateTrainingScore(cnx, trainingID, score)
    if type(update_result_score) == str:
        return json.dumps({"message": f"更新得分失败:{update_result_score}", "code": 500})
    # 更新训练记录的解锁数量
    update_result_unlockedNum = TrainingDB.updateTrainingUnlockedNum(cnx, trainingID, unlockedNum)
    if type(update_result_unlockedNum) == str:
        return json.dumps({"message": f"更新解锁数量失败:{update_result_unlockedNum}", "code": 500})
    # 更新结束训练
    update_result_IsOn = TrainingDB.setTrainingOff(cnx, trainingID)
    if type(update_result_IsOn) == str:
        return json.dumps({"message": f"更新状态失败:{update_result_IsOn}", "code": 500})

    for unlock in unlocks:
        Lock = LockDB.getLock(cnx, unlock["lockId"])
        if Lock[4] == 0:
            continue
        Lock_unlockDB.createLockUnlock(cnx, unlock["duration"], trainingID, unlock["lockId"], Lock[4], Lock[3], Lock[5])

    return json.dumps({"message": "结束训练成功", "code": 200})
