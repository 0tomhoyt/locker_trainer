import json

from Controller import LockController,MachineController, UserController, TrainingController, MatchController

# 1.workUIcontroller 显示每把锁的信息
# 2.workerUIcontroller可以更改每把锁的信息
# 3.服务器存储每把锁的信息，可以被event调用
# 4.training 获取单把锁的开启用时（MCU）
# 5.training结束传递给服务器单把锁的开启信息
# 6.服务器存储单把锁的开启信息，forien key是 trainingID
# 7.管理员查看工人信息，点进去之后有训练历史
# 8.训练历史点进去之后有时间filter，还要有单把锁的开锁信息

# 分服务器提供的route
def client_server_event_router(event, data):
    return json.dumps({"message": "当前分服务器未连接主服务器，无法处理", "code": 500})


# 主服务器提供的route
def main_server_event_router(event, data, main_server_client):
    # 如果本机是主服务器，根据不同的事件进行不同的处理

    # 输入machineId :1
    # 返回"machinestatus": True,
    #	"code": 200,
    if event == 'machineStart':
        if 'machineId' not in data:
            print(f'machineStart: 收到的data包不正确{data}')
            return json.dumps({"message": f'machineStart: 收到的data包不正确{data}', "code": 500})
        return MachineController.machineOnController(data['machineId'], 1)
    elif event == 'machineStop':
        if 'machineId' not in data:
            print(f'machineStop: 收到的data包不正确{data}')
            return json.dumps({"message": f'machineStart: 收到的data包不正确{data}', "code": 500})
        return MachineController.machineOnController(data['machineId'], 0)
    # 输入username:hyt
    #  password:hjqCYS1301
    #  machineId:1
    #  workstationId:1
    # 返回
    # "loginSuccess": True,
    # "code": 200,
    # "authToken": AuthToken,
    # "machineId": machineId,
    # "workstationId": workstationId,
    # "userName": userName,
    # "headerURL": headerUrl,
    # "worklength": worklength,
    # "message": "登录成功"
    elif event == 'workerLogin':
        if "username" not in data or "password" not in data or "machineId" not in data or "workstationId" not in data:
            print(f'workerLogin: 收到的data包不正确{data}')
            return json.dumps({"replyMessage": True, "message": f'machineStart: 收到的data包不正确{data}', "code": 500})
        return UserController.WorkerLogin(data['username'], data['password'], data['machineId'], data['workstationId'])
    elif event == 'workerLogout':
        if "machineId" not in data or "workstationId" not in data:
            print(f'workerLogout: 收到的data包不正确{data}')
            return json.dumps({"replyMessage": True, "message": f'machineStart: 收到的data包不正确{data}', "code": 500})
        return UserController.workerLogout(data['machineId'], data['workstationId'])
    # 输入username:hyt
    #  password:hjqCYS1301
    #  machineId:1
    # 输出"loginSuccess": True,
    #             "code": 200,
    #             "authToken": AuthToken,
    #             "machineId": machineId,
    #             "userName": userName,
    #             "headerURL": headerUrl,
    #             "worklength": worklength,
    #             "message": "登录成功"
    #         })
    #     或者
    #         return json.dumps({
    #             "loginSucess": False,
    #             "code": 500,
    #             "message": "没有管理员权限"
    elif event == 'adminLogin':
        if "username" not in data or "password" not in data or "machineId" not in data:
            print(f'admin login: 收到的data包不正确{data}')
            return json.dumps({"replyMessage": True,
                               "message": f'machineStart: 收到的data包不正确{data}', "code": 500})
        return UserController.adminLogin(data['username'], data['password'], data['machineId'])

    # 根据提供的authToken获取对应用户的训练历史记录。
    #
    # 输入:
    # authToken (str): 用于验证用户并获取其ID的身份验证令牌。
    #
    # 输出:
    # JSON字符串，包含以下字段:
    # - "message": 字符串，描述操作结果。
    # - "code": 整数，响应状态码。200表示成功，500表示错误。
    # - "trainingList": 列表 (仅在code为200时存在)，包含用户的训练历史记录，每个记录是一个字典。
    # - 	TrainingID (bigint): 训练的ID。
    # - 	IsMatch (tinyint): 是否是比赛。
    # - 	MatchID (int): 比赛的ID（如果有）。
    # - 	UserID (int): 用户的ID。
    # - 	WorkstationID (int): 工作站的ID。
    # - 	Score (int): 分数。
    # - 	UnlockedNum (int): 解锁数量。
    # - 	TotalTime (int): 总时间。
    # - 	TrainingType (int): 训练类型。
    # - 	Difficulty (int): 难度。
    # - 	IsOn (tinyint): 是否开启。
    # -     unlockList: []
    # -          unlockjson{
    #             lockId:int,
    #             lockSerialNumber:int,
    #             lockName:string,
    #             difficulty:int}

    # 示例输出:
    # {
    #     "message": "获取训练列表成功",
    #     "code": 200,
    #     "trainingList": [训练记录1, 训练记录2, ...]
    # }
    elif event == 'workerGetSelfTrainingHistory':
        if "authToken" not in data:
            print(f'workerGetSelfTrainingHistory: 收到的data包不正确{data}')
            return json.dumps(
                {"replyMessage": True, "message": f'workerGetSelfTrainingHistory: 收到的data包不正确{data}',
                 "code": 500})
        return TrainingController.workerGetSelfTrainingHistory(data['authToken'])

    # 根据提供的authToken获取对应用户的训练历史记录。
    #
    # 输入:
    # authToken (str): 用于验证用户并获取其ID的身份验证令牌。
    # matchID
    #
    # 输出:
    # JSON字符串，包含以下字段:
    # - "message": 字符串，描述操作结果。
    # - "code": 整数，响应状态码。200表示成功，500表示错误。
    # - "trainingList": 列表 (仅在code为200时存在)，包含用户的训练历史记录，每个记录是一个字典。
    # - 	TrainingID (bigint): 训练的ID。
    # - 	IsMatch (tinyint): 是否是比赛。
    # - 	MatchID (int): 比赛的ID（如果有）。
    # - 	UserID (int): 用户的ID。
    # - 	WorkstationID (int): 工作站的ID。
    # - 	Score (int): 分数。
    # - 	UnlockedNum (int): 解锁数量。
    # - 	TotalTime (int): 总时间。
    # - 	TrainingType (int): 训练类型。
    # - 	Difficulty (int): 难度。
    # - 	IsOn (tinyint): 是否开启。

    # 示例输出:
    # {
    #     "message": "获取训练列表成功",
    #     "code": 200,
    #     "trainingList": [训练记录1, 训练记录2, ...]
    # }
    elif event == 'getMatchTrainings':
        if "authToken" not in data or 'matchID' not in data:
            print(f'getMatchTrainings: 收到的data包不正确{data}')
            return json.dumps(
                {"replyMessage": True, "message": f'workerGetSelfTrainingHistory: 收到的data包不正确{data}',
                 "code": 500})
        return TrainingController.workerGetSelfTrainingHistory(data['authToken'])



    # 输入:
    # authToken:123456
    # workstationID:1
    # difficulty:5
    # totalTime:15 具体表示秒还是分钟你们客户端决定，我这里提供一个存储功能
    # 输出:
    # - "message": 字符串，描述操作结果。
    # - "code": 整数，响应状态码。200表示成功，500表示错误。
    # - "TrainingID": 12316512
    elif event == 'startNewTraining':
        if "authToken" not in data or "workstationID" not in data \
                or "difficulty" not in data or "totalTime" not in data:
            print(f'startNewTraining: 收到的data包不正确{data}')
            return json.dumps(
                {"replyMessage": True, "message": f'startNewTraining: 收到的data包不正确{data}', "code": 500})
        return TrainingController.addTrainingRecord(data["authToken"], data["workstationID"], data["difficulty"],
                                                    data["totalTime"])
    # 输入
    # authToken: 字符串类型，用于验证用户并从中提取用户ID。
    # trainingID: 整数类型，标识要更新的训练记录的ID。
    # score: 整数类型，表示要更新的训练记录的得分。
    # unlockedNum: 整数类型，表示要更新的训练记录的解锁数量。
    # IsOn: 整数类型，用于表示训练记录的状态（例如，1表示开启，0表示关闭）。
    # 输出
    # message: 字符串类型，描述了操作的结果，例如"更新训练成功"或具体的错误消息。
    # code: 整数类型，代表响应的状态代码。常见代码有200表示成功，500表示服务器内部错误。
    elif event == 'updateTraining':
        if "authToken" not in data or "trainingID" not in data \
                or "score" not in data or "unlockedNum" not in data or "isOn" not in data:
            print(f'updateTraining: 收到的data包不正确{data}')
            return json.dumps(
                {"replyMessage": True, "message": f'updateTraining: 收到的data包不正确{data}', "code": 500})
        return TrainingController.updateTrainingController(data["authToken"], data["trainingID"], data["score"],
                                                           data["unlockedNum"], data["isOn"])

    # 输入
    # authToken: 字符串类型，用于验证用户并从中提取用户ID。
    # trainingID: 整数类型，标识要更新的训练记录的ID。
    # score: 整数类型，表示要更新的训练记录的得分。
    # unlockedNum: 整数类型，表示要更新的训练记录的解锁数量。
    # unlocks: list of json
    # 		unlockjson:{
    # 		duration:int 时间
    # 		lockId:int
    # 		}
    # 输出
    # message: 字符串类型，描述了操作的结果，例如"结束训练成功"或具体的错误消息。
    # code: 整数类型，代表响应的状态代码。常见代码有200表示成功，500表示服务器内部错误。
    elif event == 'stopTraining':
        if "authToken" not in data or "trainingID" not in data or "score" not in data \
                or "unlockedNum" not in data or "unlocks" not in data:
            print(f'stopTraining: 收到的data包不正确{data}')
            return json.dumps(
                {"replyMessage": True, "message": f'stopTraining: 收到的data包不正确{data}', "code": 500})
        return TrainingController.stopTrainingController(data["authToken"], data["trainingID"], data["score"],
                                                         data["unlockedNum"], data["unlocks"])

    # 输入
    # authToken: 字符串类型，用于验证管理员身份。
    # 输出
    # workers: 包含所有工人信息的JSON对象列表。
    #         "UserID": worker[0],
    #         "UserName": worker[1],
    #         "Password": worker[2],
    #         "Role": worker[3],
    #         "AuthToken": worker[4],
    #         "HeadUrl": worker[5],
    #         "EnrolledDate": worker[6].strftime('%Y-%m-%d') if worker[6] else None
    # message: 字符串，表示查询状态。
    # code: 整数，表示HTTP状态代码，如200表示成功。
    elif event == "getWorkerStatus":
        if "authToken" not in data:
            print(f'getWorkerStatus: 收到的data包不正确{data}')
            return json.dumps(
                {"replyMessage": True, "message": f'getWorkerStatus: 收到的data包不正确{data}', "code": 500})
        return UserController.getAllWorkersController(data['authToken'])

    # data输入
    # authToken: 字符串类型，用于验证管理员身份。
    # 输出
    # workstations: 包含所有workstaions信息的json对象列表 ， list包含json对象
    # 			{"workstationID":int
    # 			"machineID":int
    # 			"isLoggedIn":int      1表示登陆
    # 			"userID":int		登陆的userid，未登录为-1
    # message: 字符串，表示查询状态。
    # code: 整数，表示HTTP状态代码，如200表示成功。
    elif event == "getWorkStationStatus":
        if "authToken" not in data:
            print(f'getWorkerStatus: 收到的data包不正确{data}')
            return json.dumps(
                {"replyMessage": True, "message": f'getWorkerStatus: 收到的data包不正确{data}', "code": 500})
        return MachineController.getWorkStationsStatusController(data['authToken'])

    # data输入
    # authToken: 字符串类型，用于验证管理员身份。
    # 输出
    # machines: list of json
    # 			[{"serverId":int
    # 			"isMainServer":int
    # 			"serverIpAddress":string
    # 			"clientIpAddress":string
    # 			"workstations":[{     ##list of json
    # 			"workstationId":int
    # 			"workerId":int 	未登录 = -1
    # 			"username":string
    # 			"headUrl":string}}]
    # 			]
    # message: 字符串，表示查询状态。
    # code: 整数，表示HTTP状态代码，如200表示成功。
    elif event == "getConnectedMachine":
        if "authToken" not in data:
            print(f'getConnectedMachine: 收到的data包不正确{data}')
            return json.dumps(
                {"replyMessage": True, "message": f'getConnectedMachine: 收到的data包不正确{data}', "code": 500})
        return MachineController.getConnectedMachine(data['authToken'], main_server_client)


    # data输入
    # authToken: 字符串类型，用于验证管理员身份。
    # userName string
    # password string
    # role     1表示管理员，2表示普通用户
    # 输出
    # message: 字符串，表示查询状态。
    # code: 整数，表示HTTP状态代码，如200表示成功。
    elif event == "addUser":
        if "authToken" not in data or 'userName' not in data or 'password' not in data or 'role' not in data:
            print(f'getWorkerStatus: 收到的data包不正确{data}')
            return json.dumps(
                {"replyMessage": True, "message": f'addUser: 收到的data包不正确{data}', "code": 500})
        return UserController.addUser(data['authToken'], data['userName'], data['password'], data['role'])

    #获取每把锁的名称，序列号，难度
    # data输入
    # authToken: 字符串类型，用于验证管理员身份。
    # workstationId:int
    # 输出
    # message: 字符串，表示查询状态。
    # code: 整数，表示HTTP状态代码，如200表示成功。
    # Locks:[] 注意只返回serialnumber不为0的
    # {"lockId":int , “lockName”：string， ”lockSerialNumber“：int，”difficulty“：int}
    elif event == "getLocks":
        if "authToken" not in data or "workstationId" not in data:
            print(f'getLocks: 收到的data包不正确{data}')
            return json.dumps(
                {"replyMessage": True, "message": f'getLocks: 收到的data包不正确{data}', "code": 500})
        return LockController.getLocksController(data['authToken'],data['workstationId'])

    #获取每把锁的名称，序列号，难度
    # data输入
    # authToken: 字符串类型，用于验证管理员身份。
    # lockId:int
    # “lockName”：string，
    # ”lockSerialNumber“：int，
    # ”difficulty“：int
    # 输出
    # message: 字符串，表示查询状态。
    # code: 整数，表示HTTP状态代码，如200表示成功。
    elif event == "updateLockInfo":
        if "authToken" not in data or "lockId" not in data or "lockName" not in data or \
                "lockSerialNumber" not in data\
                or "difficulty" not in data:
            print(f'updateLockInfo: 收到的data包不正确{data}')
            return json.dumps(
                {"replyMessage": True, "message": f'updateLockInfo: 收到的data包不正确{data}', "code": 500})
        return LockController.updateLockInfo(data['authToken'],data['lockId'],data['lockName'],data['lockSerialNumber'],data['difficulty'])



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
    # userID: int
    # difficulty:难度系数
    # time:总时长
    # workstationID

    elif event == "startMatch":
        if "authToken" not in data or "trainings" not in data:
            print(f'getWorkerStatus: 收到的data包不正确{data}')
            return json.dumps(
                {"replyMessage": True, "message": f'startMatch: 收到的data包不正确{data}', "code": 500})
        return MatchController.start_match_controller(data['authToken'], data['trainings'],main_server_client)



    else:
        print(f'unknown event: {event},data:{data}')
        return json.dumps({"message": f'unknown event: {event},data:{data}', "code": 500})
