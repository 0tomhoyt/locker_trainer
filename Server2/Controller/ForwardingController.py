


##这个函数需要检查本机是不是为主服务器，如果是主服务器则返回1，如果是分服务器则需要进行转发，
# 分服务器接收到的主服务器消息需要转发给客户端，分服务器收到的客户端消息需要转发给主服务器

# 主服务器：1.接收来自自己客户端的消息 2.接收来自分服务器的转发，分服务器转发的消息会带上fromServer，serverIp
# 分服务器：1.尝试连接主服务器，如果不能连接则启用
def serverController(event, data):
    return 1
