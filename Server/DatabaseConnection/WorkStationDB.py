
def getAllWorkstationStatuses(cnx):
    cursor = cnx.cursor()
    query = 'SELECT * FROM workstations'
    try:
        cursor.execute(query)
        results = cursor.fetchall()
        return results
    except Exception as exp:
        return str(exp)


def getWorkstationStatus(cnx, workstation_ID):
    cursor = cnx.cursor()
    query = f'SELECT * FROM workstations WHERE WorkStationID = {workstation_ID}'
    try:
        cursor.execute(query)
        if cursor.rowcount == 1:
            return cursor[0]
        else:
            return 500
    except Exception as exp:
        return str(exp)


def updateWorkstationLogin(cnx, workstation_ID, status):
    if status != 0 and status != 1:
        status = 0

    cursor = cnx.cursor()
    query = f'UPDATE workstations SET IsLoggedIn = {status} WHERE WorkStationID = {workstation_ID}'

    try:
        cursor.execute(query)
        cnx.commit()
    except Exception as exp:
        return str(exp)
    return 1


def assignUserToWorkstation(cnx, workstation_ID, user_ID):
    cursor = cnx.cursor()
    query = f'UPDATE workstations SET UserID = {user_ID} WHERE WorkStationID = {workstation_ID}'

    try:
        cursor.execute(query)
        cnx.commit()
    except Exception as exp:
        return str(exp)
    return 1


def addWorkstation(cnx, machine_ID, status=0):
    cursor = cnx.cursor()
    query = f'INSERT INTO workstations (MachineID, IsLoggedIn) VALUES ({machine_ID}, {status})'

    try:
        cursor.execute(query)
        cnx.commit()
    except Exception as exp:
        return str(exp)
    return 1


def deleteWorkstation(cnx, workstation_ID):
    cursor = cnx.cursor()
    query = f'DELETE FROM workstations WHERE WorkStationID = {workstation_ID}'

    try:
        cursor.execute(query)
        cnx.commit()
    except Exception as exp:
        return str(exp)
    return 1

