import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) throws IOException {
		
		// ����ServerSocket�������˿ںţ�12345
		ServerSocket ss = new ServerSocket(12345);
		// �������ڹ���ͻ��˵��շ����ݵ����߳���
		ClientThread clientThread = new ClientThread();
		clientThread.start();
		
		System.out.println("������������");

		// �����˿ںţ�12345
		// �ȴ��ͻ����� accept()
		while (true) {
			// ��ʼ���տͻ��˵�����
			Socket socket = ss.accept();
			System.out.println("���¿ͻ�����~");
			clientThread.addClient(socket);
		}
	}
}