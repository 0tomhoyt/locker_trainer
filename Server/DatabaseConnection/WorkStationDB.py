from DatabaseConnection.DBconnect import execute_query


# 创建新工作站
# 输入：cnx (数据库连接对象)，machine_ID (int)，status (int，默认为0)
# 输出：如果执行成功返回1，否则返回错误信息字符串
def addWorkstation(cnx, machine_ID, status=0):
    query = f'INSERT INTO workstations (MachineID, IsLoggedIn) VALUES ({machine_ID}, {status})'
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        cnx.commit()
        return 1


# 获取所有工作站的状态
# 输入：cnx (数据库连接对象)
# 输出：如果执行成功返回所有工作站状态的列表，否则返回错误信息字符串
def getAllWorkstationStatuses(cnx):
    query = 'SELECT * FROM workstations'
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return result.fetchall()


# 获取指定工作站的状态
# 输入：cnx (数据库连接对象)，workstation_ID (int)
# 输出：如果执行成功返回单个工作站状态的元组，否则返回错误信息字符串
def getWorkstationStatus(cnx, workstation_ID):
    query = f'SELECT * FROM workstations WHERE WorkStationID = {workstation_ID}'
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        return result.fetchone()


# 更新工作站的登录状态
# 输入：cnx (数据库连接对象)，workstation_ID (int)，status (int)
# 输出：如果执行成功返回1，否则返回错误信息字符串
def updateWorkstationLogin(cnx, workstation_ID, status):
    if status != 0 and status != 1:
        status = 0

    query = f'UPDATE workstations SET IsLoggedIn = {status} WHERE WorkStationID = {workstation_ID}'
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        cnx.commit()
        return 1


# 将用户分配给指定工作站
# 输入：cnx (数据库连接对象)，workstation_ID (int)，user_ID (int)
# 输出：如果执行成功返回1，否则返回错误信息字符串
def assignUserToWorkstation(cnx, workstation_ID, user_ID):
    query = f'UPDATE workstations SET UserID = {user_ID} WHERE WorkStationID = {workstation_ID}'
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        cnx.commit()
        return 1


# 删除指定工作站
# 输入：cnx (数据库连接对象)，workstation_ID (int)
# 输出：如果执行成功返回1，否则返回错误信息字符串
def deleteWorkstation(cnx, workstation_ID):
    query = f'DELETE FROM workstations WHERE WorkStationID = {workstation_ID}'
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        cnx.commit()
        return 1


# 通过工作站ID和机器ID查找特定工作站
# 输入：cnx (数据库连接对象)，workstation_ID (int)，machine_ID (int)
# 输出：如果执行成功返回特定工作站的状态元组，否则返回错误信息字符串
def findWorkstation(cnx, workstation_ID, machine_ID):
    # 在workstations表中使用WHERE子句来匹配WorkStationID和MachineID
    query = f'SELECT * FROM workstations WHERE WorkStationID = {workstation_ID} AND MachineID = {machine_ID}'
    # 使用execute_query函数执行查询并获取结果
    result = execute_query(cnx, query)
    # 如果结果是字符串类型（即发生错误），则返回错误信息
    if type(result) == str:
        return result
    else:
        # 否则，返回查询结果的第一行，这应该是一个包含工作站状态信息的元组
        return result.fetchone()

def findWorkstationByMachineID(cnx,  machine_ID):
    # 在workstations表中使用WHERE子句来匹配WorkStationID和MachineID
    query = f'SELECT * FROM workstations WHERE  MachineID = {machine_ID}'
    # 使用execute_query函数执行查询并获取结果
    result = execute_query(cnx, query)
    # 如果结果是字符串类型（即发生错误），则返回错误信息
    if type(result) == str:
        return result
    else:
        # 否则，返回查询结果的第一行，这应该是一个包含工作站状态信息的元组
        return result.fetchall()

# 工作站登录函数
# 输入：cnx (数据库连接对象)，workstation_ID (int)，user_ID (int)
# 输出：如果执行成功返回1，否则返回错误信息字符串
def workstationLogin(cnx, workstation_ID, user_ID):
    query = f'UPDATE workstations SET UserID = {user_ID}, IsLoggedIn = 1 WHERE WorkStationID = {workstation_ID}'
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        cnx.commit()
        return 1


# 工作站登出函数
# 输入：cnx (数据库连接对象)，workstation_ID (int)
# 输出：如果执行成功返回1，否则返回错误信息字符串
def workstationLogout(cnx, workstation_ID):
    query = f'UPDATE workstations SET UserID = -1, IsLoggedIn = 0 WHERE WorkStationID = {workstation_ID}'
    result = execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        cnx.commit()
        return 1
