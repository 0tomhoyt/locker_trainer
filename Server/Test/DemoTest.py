import json, urllib
from urllib import request


def main():
    data = json.dumps({"machineID": 1}).encode(encoding='utf-8')
    req = urllib.request.Request(url='0.0.0.0:5000' + '/machineStart', data=data)
    req = urllib.request.Request(url='0.0.0.0:5000' + '/machineStop', data=data)
    return 0


if __name__ == "__main__":
    main()
