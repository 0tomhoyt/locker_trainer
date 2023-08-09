import json
from DatabaseConnection import TrainingDB, DBconnect, UserDB


def workerGetSelfTrainingHistory(authToken):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}"})
    try:
        userid = UserDB.getUserIdFromAuthToken(cnx, authToken)[0]
        if userid is None:
            return json.dumps({"message": "找不到对应的用户ID", "code": 500})
    except Exception as e:
        return json.dumps({"message": "找不到对应的用户ID", "code": 500})

    training_records = TrainingDB.getTrainingByUserID(cnx, userid)

    # 如果没有训练记录，则返回错误
    if not training_records:
        return json.dumps({"message": "没有找到对应的训练记录", "code": 500})

    # 将训练记录整理成字典格式，并添加到列表中
    training_list = []
    for record in training_records:
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
        }
        training_list.append(training_dict)

    return json.dumps({"message": "获取训练列表成功", "code": 200, "trainingList": training_list})


def addTrainingRecord(authToken, workstationId, difficulty, totalTime):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}", "code": 500})

    try:
        userId = UserDB.getUserIdFromAuthToken(cnx, authToken)[0]
        if userId is None:
            return json.dumps({"message": "找不到对应的用户ID", "code": 500})
    except Exception as e:
        return json.dumps({"message": "找不到对应的用户ID", "code": 500})

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
