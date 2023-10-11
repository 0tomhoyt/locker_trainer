from DatabaseConnection.DBconnect import execute_query


def create_fingerprint(cnx, user_id, fingerprint_string):
    cursor = cnx.cursor()
    query = f"INSERT INTO finger_print (`user_id`,`fingerprintstring`) VALUES ({user_id}, '{fingerprint_string}')"
    try:
        cursor.execute(query)
        cnx.commit()
        last_inserted_id = cursor.lastrowid  # 获取最后插入的ID
        return last_inserted_id  # 操作成功，返回最后插入的ID
    except Exception as e:
        cnx.rollback()
        return str(e)  # 捕获错误，返回错误信息


def get_fingerprint_by_id(cnx, fingerprint_id):
    query = f"SELECT * FROM finger_print WHERE id = {fingerprint_id}"
    result = execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 返回查询结果
        return result.fetchone()


def get_fingerprint(cnx, user_id):
    # 查询数据，LockID为整数类型
    query = f"SELECT * FROM finger_print WHERE user_id = {user_id}"
    result = execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 返回查询结果
        return result.fetchall()


def get_all_fringerprint(cnx):
    # 查询数据，LockID为整数类型
    query = f"SELECT * FROM finger_print"
    result = execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 返回查询结果
        return result.fetchall()


def delete_fingerprint(cnx, fingerprint_id):
    # 删除数据，LockID为整数类型
    query = f"DELETE FROM finger_print WHERE idfinger_print = {fingerprint_id}"
    result = execute_query(cnx, query)
    if type(result) == str:  # 捕获错误，返回错误信息
        return result
    else:  # 操作成功，返回1
        cnx.commit()
        return 1
