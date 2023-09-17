import time, json
from DatabaseConnection import UserDB, DBconnect,  FingerPrintDB
from ctypes import *
import base64
import os

# from ctypes import c_ubyte

IMG_WIDTH = 320
IMG_HEIGHT = 480
# 创建一个字节数组，模拟 C++ 中的 g_image
g_image = (c_ubyte * (IMG_WIDTH * IMG_HEIGHT))()
g_feature1 = (c_ubyte * 1024)()
g_feature2 = (c_ubyte * 1024)()
finger_str = "AwFUFY0A//7A/oAegAYAAgACAAIAAgACAAIAAAAAAAAAAgACAAIAAgAAAAAAAAAAAAAAAAAAAABQFFi+EBxqnlEeWb4ZsE9+VzQH3mI2HX5kvsg+CsCjnhULqxc9jIG/Fanp/zwqBL9nsQdfHbZqHz/CS3daJYX6Vifa+is1FZopOBE3QJbC1Cm6DXQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=="
finger_str5 = 'AwFcFIoA//78HvAC4ADAAMAAwADAAMAAwADAAMAAwADAAMAA4ADgAAAAAAAAAAAAAAAAAAAAAABtEtc+YBcBfhkckDZzHZjeJZ4onjMfqZ4jpVBeKK6PXkAyjx5HuSveM0KjXliNAF8xj+nfYCrD/zusKX9YQmW1VrnXe1i+iptRvE2YT7oQeQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=='
finger_str3 = 'AwFaEZYA//78fuAewA6ABgACAAIAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAxCgE+Ow6W/ioWVF4fmCreEJ0p/m6mxd4mLtC+N7hC3g+6zd5TFdhfXb1eH1YlRT1Sp9mdJsFm1jTDBhYnQ0zwMMHqTgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=='
finger_str4 = 'Rk2KACAyMAAAAAF644444AHgAfwB/AEAAABjNkBvADB2XUDjGTJNVXBcARtvXUCWAT9uXUCfAUBzXUC3AVDnXUBYAWZiXUDoAXFeXUDJAX9sXUBcAYRzXUDOAYZwXUCDAY1nXUCFAa56XUCzAbt2XYBoADj7XYBDAD0KXYBWAECCXYAqAEIQXYB8AERxXYDFAGNkXYB+AGXxXYCIAGltXYCQAG/tXYDDAG9pXYCuAHTuXYDCAH1pXYDNAJPlXYDJAK5mXYDbANdiXYAlAQkXXYDLAQldXYA5ATMLXYBUATVzXYBkAUByXYBVAUH0XYBzAUFzXYCNAUNvXYCoAURtXYBFAUeAXYA5AUoSXYBlAUvuXYDXAVDkXYA4AVkUXYAgAWArXYBHAWR6XYBNAXRyXYA8AXYRXYBLAX97XYC0AYF0XYDBAYFxXYBAAYIGXYChAZFsXYBLAZiFXYC1Aat0XQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=='


def get_fp():
    # 引用 windows 库
    lib_import2 = windll.LoadLibrary(os.getcwd() + '\\lib\\win_64\\fpcore.dll')
    # 比对方法
    OpenDevice = lib_import2.OpenDevice
    OpenDevice.restype = c_int32
    CloseDevice = lib_import2.CloseDevice
    GetDeviceImage = lib_import2.GetDeviceImage
    GetDeviceImage.argtypes = [POINTER(c_ubyte)]
    GetDeviceImage.restype = c_bool
    OpcDetectFinger = lib_import2.OpcDetectFinger
    OpcDetectFinger.argtypes = [POINTER(c_ubyte)]
    OpcDetectFinger.restypes = c_int32
    CreateTemplate = lib_import2.CreateTemplate
    CreateTemplate.argtypes = [POINTER(c_ubyte), POINTER(c_ubyte)]
    CreateTemplate.restype = c_bool

    for i in range(30):
        lib_import2.CloseDevice()
        ret = lib_import2.OpenDevice()
        if ret == 0:
            print('open device OK')
            if lib_import2.GetDeviceImage(g_image) == 1:
                if OpcDetectFinger(g_image) == 0:
                    print('Capture OK')
                    if CreateTemplate(g_image, g_feature1):
                        finger_str2 = base64.b64encode(bytes(g_feature1)).decode('utf-8')
                        return finger_str2
        elif ret == 1:
            print('No found device')
        elif ret == 2:
            print('Open Device Fail')
        time.sleep(0.1)
    return 0


def match_fp(finger_str1, finger_str2):
    lib_import2 = windll.LoadLibrary(os.getcwd() + '\\lib\\win_64\\fpcore.dll')
    MatchTemplate = lib_import2.MatchTemplate
    MatchTemplate.argtypes = [c_char_p, c_char_p, ]
    MatchTemplate.restype = c_int32
    # for i in range(20):
    result = lib_import2.MatchTemplate(base64.b64decode(finger_str1), base64.b64decode(finger_str2))
    return result


def worker_add_fingerprint(authtoken):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}", "code": 500})

    try:
        userid = UserDB.getUserIdFromAuthToken(cnx, authtoken)
        if userid is None:
            return json.dumps({"message": "找不到对应的用户ID", "code": 500})
    except Exception as e:
        return json.dumps({"message": f"{e}", "code": 500})

    exist_fingerprint = FingerPrintDB.get_fingerprint(cnx, userid)
    result = 0
    for i in range(3):
        try:
            result = get_fp()
        except Exception as e:
            print(e)
        if result != 0:
            continue
    if result == 0:
        return json.dumps({"message": f"获取指纹失败", "code": 500})
    if len(exist_fingerprint) > 2:
        FingerPrintDB.delete_fingerprint(cnx, exist_fingerprint[0][0])
        FingerPrintDB.create_fingerprint(cnx, userid, result)
        return json.dumps({"message": f"已删除一个指纹并添加新指纹", "code": 200})
    else:
        FingerPrintDB.create_fingerprint(cnx, userid, result)
        return json.dumps({"message": f"已添加新指纹", "code": 200})


def WorkerLoginFingerprint(username, machineId, workstationId):
    try:
        cnx = DBconnect.databaseConnect()
    except Exception as e:
        print("连接数据库失败：", e)
        return json.dumps({"message": f"连接数据库失败:{e}", "code": 500})
    user = UserDB.getUserByUsername(cnx, username)
    if user is None:
        return json.dumps({"message": f"不存在用户", "code": 500})
    user_id = user[0]
    AuthToken = user[4]
    compare_result = 0
    try:
        # 硬件获取指纹
        result = 0
        for i in range(3):
            try:
                result = get_fp()
            except Exception as e:
                print(e)
            if result != 0:
                continue
        if result == 0:
            return json.dumps({"message": f"获取指纹失败", "code": 500})
        # 数据库获取指纹
        fingerprints = FingerPrintDB.get_fingerprint(cnx, user_id)
        for fingerprint in fingerprints:
            ##compare
            similarity = match_fp(fingerprint, result)
            if similarity > 50:
                compare_result = 1
    except Exception as e:
        return json.dumps(({"message": f"获取指纹数据失败:{e}", "code": 500}))

    if compare_result == 0:
        return json.dumps({
            "loginSuccess": False,
            "code": 403,
            "message": "匹配失败"
        })
    else:
        return json.dumps({
            "loginSuccess": True,
            "code": 200,
            "authToken": AuthToken,
            "machineId": machineId,
            "workstationId": workstationId,
            "userName": username,
            "headerURL": user[5],
            "worklength": user[6],
            "message": "登录成功"
        })
