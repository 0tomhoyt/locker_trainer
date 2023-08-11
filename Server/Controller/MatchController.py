import json
from DatabaseConnection import MachineDB,DBconnect

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
#
# 首先我需要

def start_match_controller(authtoken,trainings):

    return 0
