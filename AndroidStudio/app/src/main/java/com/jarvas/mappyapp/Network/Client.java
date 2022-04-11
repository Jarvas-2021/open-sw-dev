package com.jarvas.mappyapp.Network;

import androidx.annotation.NonNull;

import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.utils.ContextStorage;
import com.jarvas.mappyapp.utils.StringResource;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Client extends Thread {

    private String IP = StringResource.getStringResource(ContextStorage.getCtx(), R.string.AI_Server_IP);
    private int portNum = Integer.parseInt(StringResource.getStringResource(ContextStorage.getCtx(), R.string.AI_Server_Port));
    static public String client_msg;
    private String send_msg;

    public Client(@NonNull String name, String msg) {
        super(name);
        this.send_msg = msg;
    }

    public void run() {
        try (Socket client = new Socket()) {
            InetSocketAddress ipep = new InetSocketAddress(IP, portNum);

            client.connect(ipep);

            try(OutputStream sender = client.getOutputStream(); InputStream receiver = client.getInputStream();) {

                byte[] data = send_msg.getBytes();

                ByteBuffer b = ByteBuffer.allocate(4);

                b.order(ByteOrder.LITTLE_ENDIAN);
                b.putInt(data.length);

                sender.write(b.array(), 0, 4);
                sender.write(data);

                data = new byte[4];

                receiver.read(data, 0, 4);

                ByteBuffer c = ByteBuffer.wrap(data);
                c.order(ByteOrder.LITTLE_ENDIAN);

                int length = c.getInt();

                data = new byte[length];

                receiver.read(data, 0, length);

                this.client_msg = new String(data, "UTF-8");

                client.close();
            }
        }catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
