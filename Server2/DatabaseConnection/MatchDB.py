import time
import datetime
from DatabaseConnection.DBconnect import execute_query


# 创建比赛ID，以秒为单位，可以追溯到创建时间
def createMatch(cnx, MatchDuration, SameTime=0):
    MatchID = int(time.time())
    query = f'''INSERT INTO matches (MatchID, MatchDuration, SameTime) 
                VALUES ({MatchID}, {MatchDuration}, {SameTime})'''
    # 执行查询
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return MatchID


# 获取所有比赛
def getAllMatch(cnx):
    query = f"SELECT * FROM matches"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return result.fetchall()


# 获取一个指定ID的比赛
def getOneMatch(cnx, MatchID):
    query = f"SELECT * FROM matches WHERE MatchID = {MatchID}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return result.fetchone()


# 获取所有活跃的比赛
def getActiveMatches(cnx):
    query = "SELECT * FROM matches WHERE IsOn = 1"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return result.fetchall()


# 将比赛设置为开启状态
def setMatchOn(cnx, MatchID):
    query = f"UPDATE matches SET IsOn = 1 WHERE MatchID = {MatchID}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return 1


# 将比赛设置为关闭状态
def setMatchOff(cnx, MatchID):
    query = f"UPDATE matches SET IsOn = 0 WHERE MatchID = {MatchID}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return 1


# 删除指定ID的比赛
def deleteMatch(cnx, MatchID):
    query = f"DELETE FROM matches WHERE MatchID = {MatchID}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return 1


# 更新比赛的持续时间
def updateMatchDuration(cnx, MatchID, MatchDuration):
    query = f"UPDATE matches SET MatchDuration = {MatchDuration} WHERE MatchID = {MatchID}"
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return 1


# 获取比赛的创建时间
def getMatchCreationTime(MatchID):
    # 将比赛ID转换为Unix时间戳
    timestamp = MatchID
    # 将Unix时间戳转换为datetime对象
    dt_object = datetime.datetime.fromtimestamp(timestamp)
    return dt_object
