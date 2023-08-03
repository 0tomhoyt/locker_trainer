import hashlib


##对密码进行哈希处理
def hash_password(password):
    # Create a new SHA256 hash object
    sha_signature = hashlib.sha256(password.encode()).hexdigest()
    return sha_signature
