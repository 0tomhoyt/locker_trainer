# 使用小写字母和下划线来命名变量和函数。例如，my_variable 和 my_function。
# 使用大写字母开头的单词来命名类。例如，MyClass。
# 使用大写字母和下划线来命名常量。例如，MY_CONSTANT。
# 所有成功都返回1，失败返回字符串

##pip install flask
from Controller import MachineController

from flask import Flask, request
from waitress import serve

app = Flask(__name__)


@app.before_request
def log_request_info():
    print('Headers: %s', request.headers)
    print('Body: %s', request.get_data())


@app.post("/machineStart")
def machineStart():
    data = request.get_json()  # 获取数据
    machine_id = data.get('machineID')  # 获取machineid
    result = MachineController.machineOnController(machine_id, 1)
    if result == 1:
        return {"success": True, "code": 200}
    else:
        return {"success": False, "code": 500}


@app.post("/machineStop")
def machineStop():
    data = request.get_json()  # 获取数据
    machine_id = data.get('machineID')  # 获取machineid
    result = MachineController.machineOnController(machine_id, 0)
    if result == 1:
        return {"success": True, "code": 200}
    else:
        return {"success": False, "code": 500}


if __name__ == "__main__":
    serve(app, host="localhost", port=5000)

