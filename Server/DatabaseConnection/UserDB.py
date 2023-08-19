from DatabaseConnection.DBconnect import execute_query



# 创建新用户
def createUser(cnx, UserName, Password, Role, AuthToken, HeadUrl, EnrolledDate):
    # 插入数据
    # UserName, Password, AuthToken, HeadUrl 为字符串类型，Role 为整数类型，EnrolledDate 为日期类型
    query = f'''INSERT INTO users (UserName, Password, Role, AuthToken, HeadUrl, EnrolledDate) 
                VALUES ('{UserName}', '{Password}', {Role}, '{AuthToken}', '{HeadUrl}', '{EnrolledDate}')'''
    result = execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 操作成功，返回1
        cnx.commit()
        return 1


# 用户登录
def login(cnx, UserName, Password):
    # 查询数据，UserName 为字符串类型，Password 为字符串类型
    query = f"SELECT * FROM users WHERE UserName = '{UserName}' and Password = '{Password}'"
    cursor = cnx.cursor()
    try:
        cursor.execute(query)
        result = cursor.fetchone()
    except Exception as exp:
        return str(exp)

    return result


def checkAdminToken(cnx, authToken):
    query = f'SELECT * FROM users WHERE AuthToken = "{authToken}" AND Role = 1'
    result = execute_query(cnx, query)
    result = result.fetchone()
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    elif result is None:
        return 0
    else:
        return 1


# 获取用户信息
def getUser(cnx, UserID):
    # 查询数据，UserID 为整数类型
    query = f'SELECT * FROM users WHERE UserID = {UserID}'
    result = execute_query(cnx, query)
    result = result.fetchone()
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 返回查询结果
        return result


# 根据AuthToken获取UserID
def getUserIdFromAuthToken(cnx, AuthToken):
    # 查询数据，AuthToken 为字符串类型
    query = f"SELECT UserID FROM users WHERE AuthToken = '{AuthToken}'"
    result = execute_query(cnx, query)
    result = result.fetchone()
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 返回查询结果
        return result


# 根据UserID获取AuthToken
def getAuthTokenFromUserId(cnx, UserID):
    # 查询数据，UserID 为整数类型
    query = f"SELECT AuthToken FROM users WHERE UserID = {UserID}"
    result = execute_query(cnx, query)
    result = result.fetchone()
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 返回查询结果
        return result


# 获取用户头像URL
def getUserHeadUrl(cnx, UserID):
    # 查询数据，UserID 为整数类型
    query = f"SELECT HeadUrl FROM users WHERE UserID = {UserID}"
    result = execute_query(cnx, query)
    result = result.fetchone()
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 返回查询结果
        return result


# 获取用户注册日期
def getUserEnrolledDate(cnx, UserID):
    # 查询数据，UserID 为整数类型
    query = f"SELECT EnrolledDate FROM users WHERE UserID = {UserID}"
    result = execute_query(cnx, query)
    result = result.fetchone()
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 返回查询结果
        return result


# 更新用户角色
def updateUserRole(cnx, UserID, Role):
    # 更新数据，UserID 和 Role 为整数类型
    query = f'UPDATE users SET Role = {Role} WHERE UserID = {UserID}'
    result = execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 操作成功，返回1
        cnx.commit()
        return 1


# 更新用户头像URL
def updateUserHeadUrl(cnx, UserID, HeadUrl):
    # 更新数据，UserID 为整数类型，HeadUrl 为字符串类型
    query = f"UPDATE users SET HeadUrl = '{HeadUrl}' WHERE UserID = {UserID}"
    result = execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 操作成功，返回1
        cnx.commit()
        return 1


# 更新用户注册日期
def updateUserEnrolledDate(cnx, UserID, EnrolledDate):
    # 更新数据，UserID 为整数类型，EnrolledDate 为日期类型
    query = f"UPDATE users SET EnrolledDate = '{EnrolledDate}' WHERE UserID = {UserID}"
    result = execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 操作成功，返回1
        cnx.commit()
        return 1


# 删除用户
def deleteUser(cnx, UserID):
    # 删除数据，UserID 为整数类型
    query = f'DELETE FROM users WHERE UserID = {UserID}'
    result = execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 操作成功，返回1
        cnx.commit()
        return 1

def getAllWorker(cnx):
    query = "SELECT * FROM users"
    result = execute_query(cnx, query)
    result = result.fetchall()
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 返回查询结果
        return result

# if __name__ == "__main__":
#     try:
#         cnx = DBconnect.databaseConnect()
#     except Exception as e:
#         print("连接数据库失败：", e)
#     print(checkAdminToken(cnx,"123456"))
#     print(checkAdminToken(cnx,"888666"))
