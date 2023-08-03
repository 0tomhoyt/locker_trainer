from DatabaseConnection.DBconnect import execute_query


# 创建新工作站
def addWorkstation(cnx, machine_ID, status=0):
    # 插入数据
    query = f'INSERT INTO workstations (MachineID, IsLoggedIn) VALUES ({machine_ID}, {status})'
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return 1


# 获取所有工作站的状态
def getAllWorkstationStatuses(cnx):
    # 查询数据
    query = 'SELECT * FROM workstations'
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return result.fetchall()


# 获取指定工作站的状态
def getWorkstationStatus(cnx, workstation_ID):
    # 查询数据
    query = f'SELECT * FROM workstations WHERE WorkStationID = {workstation_ID}'
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return result.fetchone()


# 更新工作站的登录状态
def updateWorkstationLogin(cnx, workstation_ID, status):
    if status != 0 and status != 1:
        status = 0

    # 更新数据
    query = f'UPDATE workstations SET IsLoggedIn = {status} WHERE WorkStationID = {workstation_ID}'
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return 1


# 将用户分配给指定工作站
def assignUserToWorkstation(cnx, workstation_ID, user_ID):
    # 更新数据
    query = f'UPDATE workstations SET UserID = {user_ID} WHERE WorkStationID = {workstation_ID}'
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return 1


# 删除指定工作站
def deleteWorkstation(cnx, workstation_ID):
    # 删除数据
    query = f'DELETE FROM workstations WHERE WorkStationID = {workstation_ID}'
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return 1
