import json, time
from DatabaseConnection import MachineDB, DBconnect, UserDB, ServerDB, WorkStationDB, TrainingDB


# 输入
# authToken:字符串类型，用于验证管理员身份
# trainings:列表，包含这次比赛包含的所有training信息
# 		单个training结构：
# 			userID:这个training对应的user
# 			difficulty:难度系数
# 			time:总时长
# 			workstaionID：工作站ID  服务器会检查userID和工作站ID是否匹配，如果不匹配则以userID为准
# 返回：
# message: 字符串，表示状态。
# code: 整数，表示HTTP状态代码，如200表示成功。
#
# 给每个客户端发送:
# match:1
# trainingID:int
# authToken:string
# userID: int
# difficulty:难度系数
# time:总时长
# workstationID
#
# 首先我需要

def start_match_controller(authToken, trainings, main_server_client):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}"})
    isAdmin = UserDB.checkAdminToken(cnx, authToken)
    if isAdmin != 1:
        return json.dumps({"message": "权限不足", "code": 403})
    # try:
    client_connections = main_server_client.client_connections
    servers = ServerDB.get_all_servers(cnx)
    WorkStationDB.getWorkstationStatus(cnx, )
    for training in trainings:
        userID = training["userID"]
        try:
            authToken = UserDB.getAuthTokenFromUserId(cnx, userID)
            difficulty = training["difficulty"]
            time = training["time"]
            workstationID = training["workstationID"]
            workstation = WorkStationDB.getWorkstationStatus(cnx, workstationID)
            serverID = workstation[1]
            match_id = int(time.time() * 100)  # 乘以100来保留时间戳小数点后两位
            training_id = TrainingDB.createTraining(cnx, 1, match_id, userID, workstationID, 0, 0, time, 1,
                                                    difficulty,
                                                    0)
            message = json.dumps({
                "match": match_id,
                "trainingID": training_id,
                "userID": userID,
                "difficulty": difficulty,
                "time": time,
                "workstationID": workstationID
            })
            for server in servers:
                if server[0] == serverID:
                    if server[4] == 1:
                        dbip, dbport = server[3].split(":")
                    else:
                        dbip, dbport = server[2].split(":")
                    for client_connection in client_connections:
                        client_ip, client_port = client_connection["clientAddress"][0], \
                            client_connection["clientAddress"][1]
                        if client_ip == dbip and dbport == client_port:
                            client_connection["connection"].sendall(message)
        except Exception as e:
            print(f"training 初始化失败")
    # except Exception as e:
    #     print(f"match 初始化失败")
    #     return json.dumps({
    #         "message": "match启动失败",
    #         "code": 665
    #     })

    return json.dumps({
        "message": "match启动成功",
        "code": 200
    })
