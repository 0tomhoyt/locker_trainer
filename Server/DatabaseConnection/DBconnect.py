import mysql.connector  # python访问mysql数据库


def databaseConnect():
    cnx = mysql.connector.connect(user='hoyt',
                                  password='hjqCYS1301',
                                  host='47.113.207.215',
                                  database='zy_database')
    return cnx


def databaseDisconnect(cnx):
    cnx.close()
    return True


def execute_query(cnx, query):
    cursor = cnx.cursor()
    try:
        cursor.execute(query)
        return cursor
    except Exception as exp:
        return str(exp)