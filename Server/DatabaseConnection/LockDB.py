import json
from DatabaseConnection import DBconnect


# 创建锁
def createLock(cnx, LockID, LockStatus, WorkstationID):
    # 插入数据，LockID, LockStatus, WorkstationID都为整数类型
    query = f"INSERT INTO locks (`LockID`,`LockStatus`,`WorkstationID`) VALUES " \
            f"({LockID}, {LockStatus}, {WorkstationID})"
    result = DBconnect.execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 操作成功，返回1
        cnx.commit()
        return 1


# 获取锁信息
def getLock(cnx, LockID):
    # 查询数据，LockID为整数类型
    query = f"SELECT * FROM locks WHERE LockID = {LockID}"
    result = DBconnect.execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 返回查询结果
        return result.fetchone()


# 删除锁
def deleteLock(cnx, LockID):
    # 删除数据，LockID为整数类型
    query = f"DELETE FROM locks WHERE LockID = {LockID}"
    result = DBconnect.execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 操作成功，返回1
        cnx.commit()
        return 1


# 更新锁状态
def updateLockStatus(cnx, LockID, LockStatus):
    # 更新数据，LockID和LockStatus为整数类型
    query = f"UPDATE locks SET LockStatus = {LockStatus} WHERE LockID = {LockID}"
    result = DBconnect.execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 操作成功，返回1
        cnx.commit()
        return 1


def updateLockInfo(cnx, LockID, LockName, LockSerialNum, Difficulty):
    # 更新数据，LockID和LockStatus为整数类型
    query = f"UPDATE locks SET LockName = {LockName},LockSerialNum={LockSerialNum},Difficulty={Difficulty} WHERE LockID = {LockID}"
    result = DBconnect.execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 操作成功，返回1
        cnx.commit()
        return 1


# 获取指定工作站的所有锁
def getAllLocksForWorkstation(cnx, WorkstationID):
    # 查询数据，WorkstationID为整数类型
    query = f"SELECT * FROM locks WHERE WorkstationID = {WorkstationID}"
    result = DBconnect.execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 返回查询结果
        return result.fetchall()

def main():
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}"})
    for i in range(60):
        print(createLock(cnx,i,-1,1))
        createLock(cnx,i+60,-1,2)
    return 0

if __name__ == "__main__":
    main()
