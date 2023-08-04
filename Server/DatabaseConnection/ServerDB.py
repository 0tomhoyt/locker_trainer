from DatabaseConnection.DBconnect import execute_query


def add_server(cnx, is_main_server, server_ip_address, client_ip_address):
    # 插入一个新的服务器到数据库
    query = f'INSERT INTO server (isMainServer, serverIpAddress, clientIpAddress) VALUES ({is_main_server}, "{server_ip_address}", "{client_ip_address}")'
    result = execute_query(cnx, query)
    return 1 if type(result) != str else result


def get_all_servers(cnx):
    # 返回所有服务器的信息
    query = 'SELECT * FROM server'
    result = execute_query(cnx, query)
    return result.fetchall() if type(result) != str else result


def get_server(cnx, server_id):
    # 返回特定服务器的信息
    query = f'SELECT * FROM server WHERE serverId = {server_id}'
    result = execute_query(cnx, query)
    return result.fetchone() if type(result) != str else result


def delete_server(cnx, server_id):
    # 从数据库中删除一个服务器
    query = f'DELETE FROM server WHERE serverId = {server_id}'
    result = execute_query(cnx, query)
    return 1 if type(result) != str else result


def change_main_server(cnx, server_id, is_main_server):
    # 更改服务器是否为主服务器
    query = f'UPDATE server SET isMainServer = {is_main_server} WHERE serverId = {server_id}'
    result = execute_query(cnx, query)
    return 1 if type(result) != str else result


def change_server_ip_address(cnx, server_id, new_ip_address):
    # 更改服务器的 IP 地址
    query = f'UPDATE server SET serverIpAddress = "{new_ip_address}" WHERE serverId = {server_id}'
    result = execute_query(cnx, query)
    return 1 if type(result) != str else result


def change_client_ip_address(cnx, server_id, new_ip_address):
    # 更改客户端的 IP 地址
    query = f'UPDATE server SET clientIpAddress = "{new_ip_address}" WHERE serverId = {server_id}'
    result = execute_query(cnx, query)
    return 1 if type(result) != str else result
