import time
from DatabaseConnection import DBconnect


def createLockUnlock(cnx, unlockDuration, trainingId, lockId, lockSerialNum, lockName, difficulty):
    unlockTime = time.strftime('%Y-%m-%d')  # Get the current date in the required format
    query = f'''INSERT INTO `locks_unlock` (unlockTime, unlockDuration, trainingId, lockId, lockSerialNum, lockName, difficulty) 
                VALUES ('{unlockTime}', {unlockDuration}, {trainingId}, {lockId}, {lockSerialNum}, '{lockName}', {difficulty})'''
    result = DBconnect.execute_query(cnx, query)
    if type(result) == str:
        return result
    else:
        cnx.commit()
        return 1


