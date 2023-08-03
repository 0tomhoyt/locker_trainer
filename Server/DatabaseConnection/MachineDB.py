from DatabaseConnection.LockDB import execute_query


# 添加机器
def addMachine(cnx, status=0):
    # 插入数据，status为整数类型，取值只能为0或1
    query = f'INSERT INTO machines (IsStarted) VALUES ({status})'
    result = execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 操作成功，返回1
        return 1


# 删除机器
def deleteMachine(cnx, machine_ID):
    # 删除数据，machine_ID为整数类型
    query = f'DELETE FROM machines WHERE MachineID = {machine_ID}'
    result = execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 操作成功，返回1
        return 1


# 获取所有机器状态
def getAllMachineStatuses(cnx):
    # 查询数据
    query = 'SELECT * FROM machines'
    result = execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 返回查询结果
        return result.fetchall()


# 获取某一机器状态
def getMachineStatus(cnx, machine_ID):
    # 查询数据，machine_ID为整数类型
    query = f'SELECT * FROM machines WHERE MachineID = {machine_ID}'
    result = execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 返回查询结果
        return result.fetchone()


# 改变机器状态
def machineOn_Off(cnx, machine_ID, status):
    # status为整数类型，取值只能为0或1，machine_ID为整数类型
    if status != 0 and status != 1:
        status = 0
    query = f'UPDATE machines SET IsStarted = {status} WHERE MachineID = {machine_ID}'
    result = execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 操作成功，返回1
        return 1
