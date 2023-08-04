from DatabaseConnection.DBconnect import execute_query


def add_server(cnx, is_main_server, server_ip_address, client_ip_address, is_self):
    # 插入一个新的服务器到数据库
    # 确保只有一行数据的 isMainServer 和 isSelf 是 1
    if is_main_server:
        reset_all_main_server(cnx)
    if is_self:
        reset_all_self(cnx)

    query = f'INSERT INTO server (isMainServer, serverIpAddress, clientIpAddress, isSelf) VALUES ({is_main_server}, "{server_ip_address}", "{client_ip_address}", {is_self})'
    result = execute_query(cnx, query)
    return 1 if type(result) != str else result


def reset_all_main_server(cnx):
    # 将所有服务器的 isMainServer 设置为 0
    query = f'UPDATE server SET isMainServer = 0'
    execute_query(cnx, query)


def reset_all_self(cnx):
    # 将所有服务器的 isSelf 设置为 0
    query = f'UPDATE server SET isSelf = 0'
    execute_query(cnx, query)


def change_main_server(cnx, server_id, is_main_server):
    # 更改服务器是否为主服务器
    # 确保只有一行数据的 isMainServer 是 1
    if is_main_server:
        reset_all_main_server(cnx)

    query = f'UPDATE server SET isMainServer = {is_main_server} WHERE serverId = {server_id}'
    result = execute_query(cnx, query)
    return 1 if type(result) != str else result


def change_self(cnx, server_id, is_self):
    # 更改服务器是否是本机服务器
    # 确保只有一行数据的 isSelf 是 1
    if is_self:
        reset_all_self(cnx)

    query = f'UPDATE server SET isSelf = {is_self} WHERE serverId = {server_id}'
    result = execute_query(cnx, query)
    return 1 if type(result) != str else result


def get_all_servers(cnx):
    # 获取所有服务器的信息
    query = 'SELECT * FROM server'
    result = execute_query(cnx, query)
    return result.fetchall() if type(result) != str else result


def get_server(cnx, server_id):
    # 获取指定服务器的信息
    query = f'SELECT * FROM server WHERE serverId = {server_id}'
    result = execute_query(cnx, query)
    return result.fetchone() if type(result) != str else result


def delete_server(cnx, server_id):
    # 删除指定服务器
    query = f'DELETE FROM server WHERE serverId = {server_id}'
    result = execute_query(cnx, query)
    return 1 if type(result) != str else result


def change_server_ip_address(cnx, server_id, new_ip_address):
    # 修改指定服务器的IP地址
    query = f'UPDATE server SET serverIpAddress = "{new_ip_address}" WHERE serverId = {server_id}'
    result = execute_query(cnx, query)
    return 1 if type(result) != str else result


def change_client_ip_address(cnx, server_id, new_ip_address):
    # 修改指定服务器的客户端IP地址
    query = f'UPDATE server SET clientIpAddress = "{new_ip_address}" WHERE serverId = {server_id}'
    result = execute_query(cnx, query)
    return 1 if type(result) != str else result
