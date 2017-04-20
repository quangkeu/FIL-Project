import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSide extends Thread {
	private static final String serverIP = "192.168.102.104";
	private static final int serverPort = 8899;
	private static final int fileSize = 16*1024;
	private static ServerSocket serverSocket;

	public ServerSide() {
		try {
			serverSocket = new ServerSocket(serverPort);
			//serverSocket.bind(new InetSocketAddress(serverIP, serverPort));
			System.out.println("[+]Server start at " + serverIP + ":" + serverPort);
		} catch (IOException e) {
			
		}
	}
	
	public void run () {
		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				System.out.println("[+]Accept new client from " + clientSocket.getRemoteSocketAddress());
				saveFile(clientSocket);
			} catch (IOException e) {
				
			}
		}
	}
	
	public void saveFile(Socket clientSocket) throws IOException {
		BufferedInputStream bufIn = new BufferedInputStream(clientSocket.getInputStream());
		File file = new File("outFile.txt");
		FileOutputStream outFile = new FileOutputStream(file);
		
		byte[] byteArray = new byte[fileSize];
		
		//Read Buffer Input Stream into byte array
		bufIn.read(byteArray, 0, byteArray.length);
		
		//Write byte array to file
		outFile.write(byteArray);
		
		//outFile.flush();
		clientSocket.close();
		
	}
	
	public static void main(String[] args) throws IOException{
		ServerSide server = new ServerSide();
		server.start();
	}

}
