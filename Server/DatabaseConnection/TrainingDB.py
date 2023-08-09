import time
import datetime

from DatabaseConnection.DBconnect import execute_query


# 创建一个训练记录
def createTraining(cnx, IsMatch, MatchID, UserID, WorkstationID, Score, UnlockedNum, TotalTime, TrainingType,
                   Difficulty, IsOn):
    TrainingID = int(time.time() * 100)  # 乘以10来保留时间戳小数点后一位
    query = f'''INSERT INTO trainings (TrainingID, IsMatch, MatchID, UserID, WorkstationID, Score, UnlockedNum, TotalTime, TrainingType, Difficulty, IsOn) 
                VALUES ({TrainingID}, {IsMatch}, {MatchID}, {UserID}, {WorkstationID}, {Score}, {UnlockedNum}, {TotalTime}, {TrainingType}, {Difficulty}, {IsOn})'''
    result = execute_query(cnx, query)
    print(result)
    if type(result) == str:
        return result
    else:
        cnx.commit()
        return TrainingID


# 将训练记录设置为开启状态
def setTrainingOn(cnx, TrainingID):
    query = f"UPDATE trainings SET IsOn = 1 WHERE TrainingID = {TrainingID}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return 1


# 将训练记录设置为关闭状态
def setTrainingOff(cnx, TrainingID):
    query = f"UPDATE trainings SET IsOn = 0 WHERE TrainingID = {TrainingID}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return 1


# 更新训练记录的难度等级
def updateTrainingDifficulty(cnx, TrainingID, Difficulty):
    query = f"UPDATE trainings SET Difficulty = {Difficulty} WHERE TrainingID = {TrainingID}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return 1


# 更新训练记录的得分
def updateTrainingScore(cnx, TrainingID, Score):
    query = f"UPDATE trainings SET Score = {Score} WHERE TrainingID = {TrainingID}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return 1


# 更新训练记录的解锁数量
def updateTrainingUnlockedNum(cnx, TrainingID, UnlockedNum):
    query = f"UPDATE trainings SET UnlockedNum = {UnlockedNum} WHERE TrainingID = {TrainingID}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return 1


# 更新训练记录的总时间
def updateTrainingTotalTime(cnx, TrainingID, TotalTime):
    query = f"UPDATE trainings SET TotalTime = {TotalTime} WHERE TrainingID = {TrainingID}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return 1


# 更新训练记录的用户ID
def updateTrainingUserID(cnx, TrainingID, UserID):
    query = f"UPDATE trainings SET UserID = {UserID} WHERE TrainingID = {TrainingID}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return 1


# 检索所有训练记录
def getAllTraining(cnx, TrainingID):
    query = f"SELECT * FROM trainings"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return result.fetchall()


# 检索指定ID的训练记录
def getOneTraining(cnx, TrainingID):
    query = f"SELECT * FROM trainings WHERE TrainingID = {TrainingID}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return result.fetchone()


# 根据用户ID检索训练记录
def getTrainingByUserID(cnx, UserID):
    query = f"SELECT * FROM trainings WHERE UserID = {UserID}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return result.fetchall()


# 根据比赛ID检索训练记录
def getTrainingByMatchID(cnx, MatchID):
    query = f"SELECT * FROM trainings WHERE MatchID = {MatchID}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return result.fetchall()


# 根据工作站ID检索训练记录
def getTrainingByWorkstationID(cnx, WorkstationID):
    query = f"SELECT * FROM trainings WHERE WorkstationID = {WorkstationID}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return result.fetchall()


# 根据难度等级检索训练记录
def getTrainingByDifficulty(cnx, Difficulty):
    query = f"SELECT * FROM trainings WHERE Difficulty = {Difficulty}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return result.fetchall()


# 检索处于开启状态的训练记录
def getTrainingOn(cnx):
    query = f"SELECT * FROM trainings WHERE IsOn = 1"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return result.fetchall()


# 删除指定ID的训练记录
def deleteTraining(cnx, TrainingID):
    query = f"DELETE FROM trainings WHERE TrainingID = {TrainingID}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return 1


# 获取训练记录的创建时间
def getTrainingCreationTime(TrainingID):
    # 除以10将时间戳转换回正常的Unix时间戳
    timestamp = TrainingID / 10
    # 将Unix时间戳转换为datetime对象
    dt_object = datetime.datetime.fromtimestamp(timestamp)
    return dt_object
