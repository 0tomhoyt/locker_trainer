def getAllMachineStatuses(cnx):
    cursor = cnx.cursor()
    query = 'SELECT * FROM machineinfo'
    try:
        cursor.execute(query)
        results = cursor.fetchall()
        return results
    except Exception as exp:
        return str(exp)


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


def addMachine(cnx, status=0):
    cursor = cnx.cursor()
    query = f'INSERT INTO machineinfo (IsStarted) VALUES ({status})'
    try:
        cursor.execute(query)
        cnx.commit()
    except Exception as exp:
        return str(exp)
    return 1


def deleteMachine(cnx, machine_ID):
    cursor = cnx.cursor()
    query = f'DELETE FROM machineinfo WHERE MachineID = {machine_ID}'
    try:
        cursor.execute(query)
        cnx.commit()
    except Exception as exp:
        return str(exp)
    return 1


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
