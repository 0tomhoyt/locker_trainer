import requests


def main():
    response_start = requests.post("http://localhost:5000/machineStart", json={"machineID": 1})

    print("Response Status Code for /machineStart: ", response_start.status_code)
    print("Response Content for /machineStart: ", response_start.json())

    # 访问 /machineStop 端口
    response_stop = requests.post("http://localhost:5000/machineStop", json={"machineID": 1})

    print("Response Status Code for /machineStop: ", response_stop.status_code)
    print("Response Content for /machineStop: ", response_stop.json())
    return 0


if __name__ == "__main__":
    main()
