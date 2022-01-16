import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Arraylist;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Client {
    public static void main(String... args) {

        String[] text_list = {"부천에서 인천대입구역으로 가자.", "1시간 뒤에 다이소에 들렸다가 광화문으로 갈거야.", "1시간 뒤에 다이소에 들렸다가 광화문으로 갈거야.", "서울대학교에서 안양으로 바로 가려고", "여기서 가장 가까운 주유소가 어디야?", "지금 서울역에 가서 KTX를 타고 싶어", "내일부터 청양으로 출근해야 돼", "평창올림픽 보러 갈거야", "버스타고 용산으로 갈게", "인천대학교 7호관으로 안내해줘"};


        try (Socket client = new Socket()) {
            InetSocketAddress ipep = new InetSocketAddress("###.###.###.###", #####);

            client.connect(ipep);

            try(OutputStream sender = client.getOutputStream(); InputStream receiver = client.getInputStream();) {
                for (int i = 0; i < 10; i++) {
                    String msg = text_list[i];

                    byte[] data = msg.getBytes();

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

                    msg = new String(data, "UTF-8");


                    System.out.println(msg);
                }
            }
        }catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
