import mysql.connector  # python访问mysql数据库


def databaseConnect():
    cnx = mysql.connector.connect(user='root',
                                  password='hjqCYS1301',
                                  host='localhost',
                                  database='my_test_database_test')
    return cnx


def getMachineStatus(cnx, machine_ID):
    cursor = cnx.cursor()
    query = f'select * from machineinfo where MachineID = {machine_ID}'
    try:
        cursor.execute(query)
        if cursor.rowcount == 1:
            return cursor[0][1]
        else:
            return 500
    except Exception as exp:
        return str(exp)


def machineOn_Off(cnx, machine_ID, status):
    if status != 0 and status != 1:
        status = 0

    cursor = cnx.cursor()
    query = f'update machineinfo SET IsStarted = {status} where MachineID = {machine_ID}'

    try:
        cursor.execute(query)
        cursor.close()
    except Exception as exp:
        return str(exp)
    return 1
