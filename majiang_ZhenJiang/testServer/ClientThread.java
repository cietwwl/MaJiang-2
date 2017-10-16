import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

// �̳�Thread�߳���
public class ClientThread extends Thread {
	// �ͻ��б�
	private ArrayList<Socket> clients = new ArrayList<Socket>();

	// ��ӿͻ�
	public void addClient(Socket socket) {
		clients.add(socket);
	}
	// ɾ���ͻ�
	public void removeClient(Socket socket) {
		clients.remove(socket);
	}
	// ��ͻ���������
	public void sendMessage(Socket socket, String data) throws IOException {
		// ����ҷ�������
		OutputStream os = socket.getOutputStream();
		os.write(data.getBytes("UTF-8"));
		System.out.println(data.getBytes("UTF-8"));
	}

	@Override
	public void run() {
		while (true) {
			try {
				for (Socket socket : clients) {
					// ��ȡ�ͻ��˷���������
					InputStream is = socket.getInputStream();
					int len = is.available() + 1;
					byte[] buff = new byte[len];
					int flag = is.read(buff);

					// read()����-1��˵���ͻ��˵�socket�ѶϿ�
					if (flag == -1) {
						System.out.println("�пͻ��Ͽ�����~");
						this.removeClient(socket);
						break;
					}

					// ������յ�������
					String read = new String(buff);
					System.out.println("�յ����ݣ�" + read);

					// ����ҷ�������
					String data = "��ϲ�㣬���ӳɹ���~~";
					sendMessage(socket, data);
				}
				sleep(10);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}