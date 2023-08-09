import json

from Controller import MachineController, UserController, TrainingController


# 分服务器提供的route
def client_server_event_router(event, data):
    return json.dumps({"message": "当前分服务器未连接主服务器，无法处理"})


# 主服务器提供的route
def main_server_event_router(event, data):
    # 如果本机是主服务器，根据不同的事件进行不同的处理

    # 输入machineId :1
    # 返回"machinestatus": True,
    #	"code": 200,
    if event == 'machineStart':
        if 'machineId' not in data:
            print(f'machineStart: 收到的data包不正确{data}')
            return json.dumps({"message": f'machineStart: 收到的data包不正确{data}'})
        return MachineController.machineOnController(data['machineId'], 1)
    elif event == 'machineStop':
        if 'machineId' not in data:
            print(f'machineStop: 收到的data包不正确{data}')
            return json.dumps({"message": f'machineStart: 收到的data包不正确{data}'})
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
            return json.dumps({"replyMessage": True, "message": f'machineStart: 收到的data包不正确{data}'})
        return UserController.WorkerLogin(data['username'], data['password'], data['machineId'], data['workstationId'])
    elif event == 'workerLogout':
        if "machineId" not in data or "workstationId" not in data:
            print(f'workerLogout: 收到的data包不正确{data}')
            return json.dumps({"replyMessage": True, "message": f'machineStart: 收到的data包不正确{data}'})
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
            return json.dumps({"replyMessage": True, "message": f'machineStart: 收到的data包不正确{data}'})
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
                {"replyMessage": True, "message": f'workerGetSelfTrainingHistory: 收到的data包不正确{data}'})
        return TrainingController.workerGetSelfTrainingHistory(data['authToken'])
    # 输入:
    # authToken:123456
    # workstationID:1
    # difficulty:5
    # totalTime:15 具体表示秒还是分钟你们客户端决定，我这里提供一个存储功能
    # 输出:
    # - "message": 字符串，描述操作结果。
    # - "code": 整数，响应状态码。200表示成功，500表示错误。
    elif event == 'startNewTraining':
        if "authToken" not in data or "workstationID" not in data or "difficulty" not in data or "totalTime" not in data:
            print(f'startNewTraining: 收到的data包不正确{data}')
            return json.dumps(
                {"replyMessage": True, "message": f'startNewTraining: 收到的data包不正确{data}'})
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
        if "authToken" not in data or "trainingID" not in data or "score" not in data or "unlockedNum" not in data or "isOn" not in data:
            print(f'startNewTraining: 收到的data包不正确{data}')
            return json.dumps(
                {"replyMessage": True, "message": f'startNewTraining: 收到的data包不正确{data}'})
        return TrainingController.updateTrainingController(data["authToken"], data["trainingID"], data["score"],
                                                           data["unlockedNum"], data["isOn"])

    # 输入
    # authToken: 字符串类型，用于验证用户并从中提取用户ID。
    # trainingID: 整数类型，标识要更新的训练记录的ID。
    # 输出
    # message: 字符串类型，描述了操作的结果，例如"结束训练成功"或具体的错误消息。
    # code: 整数类型，代表响应的状态代码。常见代码有200表示成功，500表示服务器内部错误。
    elif event == 'stopTraining':
        if "authToken" not in data or "trainingID" not in data:
            print(f'startNewTraining: 收到的data包不正确{data}')
            return json.dumps(
                {"replyMessage": True, "message": f'startNewTraining: 收到的data包不正确{data}'})
        return TrainingController.stopTrainingController(data["authToken"], data["trainingID"])

    else:
        print(f'unknown event: {event},data:{data}')
        return json.dumps({"message": f'unknown event: {event},data:{data}'})
