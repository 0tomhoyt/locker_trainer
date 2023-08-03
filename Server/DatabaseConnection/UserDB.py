def login(cnx, UserName, Password):
    cursor = cnx.cursor()
    query = f"SELECT Password, AuthToken FROM users WHERE UserName = '{UserName}'"
    try:
        cursor.execute(query)
        result = cursor.fetchone()
        if result is None:
            return False
        stored_password, auth_token = result
        # 比较输入的密码的哈希值与数据库中存储的哈希值
        if Password == stored_password:
            return auth_token
        else:
            return False
    except Exception as exp:
        return str(exp)


def createUser(cnx, UserName, Password, Role, AuthToken, HeadUrl, EnrolledDate):
    cursor = cnx.cursor()
    query = f'''INSERT INTO users (UserName, Password, Role, AuthToken, HeadUrl, EnrolledDate) 
                VALUES ('{UserName}', '{Password}', {Role}, '{AuthToken}', '{HeadUrl}', '{EnrolledDate}')'''
    try:
        cursor.execute(query)
        cnx.commit()
    except Exception as exp:
        return str(exp)
    return 1

def getUser(cnx, UserID):
    cursor = cnx.cursor()
    query = f'SELECT * FROM users WHERE UserID = {UserID}'
    try:
        cursor.execute(query)
        result = cursor.fetchone()
        return result
    except Exception as exp:
        return str(exp)

def updateUserRole(cnx, UserID, Role):
    cursor = cnx.cursor()
    query = f'UPDATE users SET Role = {Role} WHERE UserID = {UserID}'
    try:
        cursor.execute(query)
        cnx.commit()
    except Exception as exp:
        return str(exp)
    return 1


def deleteUser(cnx, UserID):
    cursor = cnx.cursor()
    query = f'DELETE FROM users WHERE UserID = {UserID}'
    try:
        cursor.execute(query)
        cnx.commit()
    except Exception as exp:
        return str(exp)
    return 1
