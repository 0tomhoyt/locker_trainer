import time
from DatabaseConnection import DBconnect


def createLockUnlock(cnx, unlockDuration, trainingId, lockId, lockSerialNum, lockName, difficulty):
    unlockTime = time.strftime('%Y-%m-%d')  # Get the current date in the required format
    query = f'''INSERT INTO `locks_unlock` (unlockTime, unlockDuration, trainingId, lockId, lockSerialNum, lockName, difficulty) 
                VALUES ('{unlockTime}', {unlockDuration}, {trainingId}, {lockId}, {lockSerialNum}, '{lockName}', {difficulty})'''
    result = DBconnect.execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        cnx.commit()
        return 1


def getAllunlock(cnx, trainingId):
    query = f"SELECT * FROM locks_unlock WHERE trainingId = {trainingId}"
    result = DBconnect.execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 返回查询结果
        return result.fetchall()

def getUnlockBySerialNumber(cnx, serialNumber):
    query = f"SELECT * FROM locks_unlock WHERE lockSerialNum = {serialNumber}"
    result = DBconnect.execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 返回查询结果
        return result.fetchall()