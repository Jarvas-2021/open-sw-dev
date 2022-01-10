import glob
import base64
import numpy as np
from time import sleep
from tqdm import tqdm
import socket, threading

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

            data = msg.encode()

            length = len(data)

            client_socket.sendall(length.to_bytes(4, byteorder='little'))

            client_socket.sendall(data)
            
            
            #################################################
            
            if data != "":
            
                socket_ = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

                socket_.connect((Server_IP, Sever_port))

                socket_.sendall(length.to_bytes(4, byteorder="little"))

                socket_.sendall(data)

                data = socket_.recv(4)

                length = int.from_bytes(data, "little")

                data = socket_.recv(length)

                msg = data.decode()

                print(msg)

                socket_.close()
            
    except:
        print("except : " , addr)
    finally:
        client_socket.close()

server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server_socket.bind(('', port))
server_socket.listen()

Server_IP = 
Sever_port = 

try:
    while True:
        client_socket, addr = server_socket.accept()
        th = threading.Thread(target=binder, args = (client_socket,addr))
        th.start();
except:
    print("server")
finally:
    server_socket.close()