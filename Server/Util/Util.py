import hashlib,string,random
from DatabaseConnection import ServerDB, DBconnect


##对密码进行哈希处理
def hash_password(password):
    # Create a new SHA256 hash object
    sha_signature = hashlib.sha256(password.encode()).hexdigest()
    return sha_signature


def checkIfSelfIsMainServer():
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return f"连接数据库失败:{e}"
    server_info = ServerDB.get_self_server(cnx)
    if server_info is None:
        print("未设置本机服务器")
        return "未设置本机服务器"
    if type(server_info) == str:
        return server_info  # return error message if any
    else:
        is_main = server_info[1]  # we expect 'isMainServer' is at the second position in the tuple
        return 1 if is_main == 1 else 0

def generate_auth_token(length=30):
    characters = string.ascii_letters + string.digits  # 包括大小写字母和数字
    token = ''.join(random.choice(characters) for i in range(length))
    return token


if __name__ == "__main__":
    print(checkIfSelfIsMainServer())
