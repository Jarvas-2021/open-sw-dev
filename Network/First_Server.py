import glob
import base64
import numpy as np
from time import sleep
from tqdm import tqdm
import socket, threading

def binder(client_socket, addr):

    print('Connected by', addr)
    try:

        while True:

            data = client_socket.recv(4)

            length = int.from_bytes(data, "little")

            data = client_socket.recv(length)

            msg = data.decode()

            print('Received from', addr, msg)

            msg = "echo : " + msg

            data = msg.encode()

            length = len(data)

            client_socket.sendall(length.to_bytes(4, byteorder='little'))

            client_socket.sendall(data)
    except:
        print("except : " , addr)
    finally:
        client_socket.close()

server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server_socket.bind(('', port))
server_socket.listen()

try:
    while True:
        client_socket, addr = server_socket.accept()
        th = threading.Thread(target=binder, args = (client_socket,addr))
        th.start();
except:
    print("server")
finally:
    server_socket.close()