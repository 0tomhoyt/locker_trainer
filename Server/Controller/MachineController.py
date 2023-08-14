import json
from DatabaseConnection import MachineDB, DBconnect, UserDB, WorkStationDB, ServerDB


def machineOnController(machineID, status):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}"})
    machineResult = MachineDB.getMachineStatus(cnx, machineID)
    if machineResult is None:
        return json.dumps({
            "machinestatus": False,
            "code": 500,
        })
    dbresult = MachineDB.machineOn_Off(cnx, machineID, status)
    if dbresult == 1:
        print(f"调整机器状态为{status}")
        DBconnect.databaseDisconnect(cnx)
        return json.dumps({
            "machinestatus": True,
            "code": 200,
        })
    else:
        DBconnect.databaseDisconnect(cnx)
        print(f"机器开关数据库连接错误，{dbresult}")
        return json.dumps({"message": f"机器开关数据库连接错误，{dbresult}"})


def getWorkStationsStatusController(authToken):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}"})
    isAdmin = UserDB.checkAdminToken(cnx, authToken)
    if isAdmin != 1:
        return json.dumps({"message": "权限不足", "code": 403})

    result = WorkStationDB.getAllWorkstationStatuses(cnx)
    workstation_list = []
    for workstation in result:
        workstation_json = {
            "workstationID": workstation[0],
            "machineID": workstation[1],
            "isLoggedIn": workstation[2],
            "userID": workstation[3]
        }
        workstation_list.append(workstation_json)

    return json.dumps({"workstations": workstation_list, "message": "查询成功", "code": 200})


def getConnectedMachine(authToken, main_server_client):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}"})
    isAdmin = UserDB.checkAdminToken(cnx, authToken)
    if isAdmin != 1:
        return json.dumps({"message": "权限不足", "code": 403})

    servers = ServerDB.get_all_servers(cnx)
    client_connections = main_server_client.client_connections
    server_list = []
    for client_conn in client_connections:
        client_ip, client_port = client_conn["clientAddress"][0], client_conn["clientAddress"][1]
        for server in servers:
            dbip, dbport = server[2].split(":")
            localip, localport = server[3].split(":")
            print(localip,localport,client_ip,client_port)
            if (client_ip == dbip and client_port == dbport) or (
                    server[4] == 1
                    # and client_ip == localip and client_port == localport
            ):
                workstations = WorkStationDB.findWorkstationByMachineID(cnx, server[0])
                workstation_list = []
                for workstation in workstations:
                    user = UserDB.getUser(cnx, workstation[3])
                    if user is None:
                        worstation_json = {
                            "workstationId": workstation[0],
                            "workerId": -1,
                            "username": "",
                            "headUrl": ""
                        }
                    else:
                        worstation_json = {
                            "workstationId": workstation[0],
                            "workerId": workstation[3],
                            "username": user[1],
                            "headUrl": user[5]
                        }
                    workstation_list.append(worstation_json)

                server_json = {
                    "serverId": server[0],
                    "isMainServer": server[1],
                    "serverIpAddress": dbip,
                    "clientIpAddress": dbport,
                    "workstations": workstation_list
                }
                server_list.append(server_json)
    #       ('127.0.0.1', 57957)
    # 		('127.0.0.1', 57961)
    return json.dumps({"machines": server_list, "message": "查询成功", "code": 200})
