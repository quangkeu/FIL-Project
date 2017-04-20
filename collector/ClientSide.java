import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/* 
 * Send file from client to server via socket
 */

public class ClientSide {
	
	private static int serverPort = 8899;
	private static String serverIP = "192.168.102.104";
	private static String filePath = "C:\\Users\\quang\\Desktop\\a.txt";
	public static Date date = new Date();
	
	public static void main(String[] args) throws IOException, InterruptedException {
		while (true) {
			try {
				//Connect to specific IP:PORT
				Socket clientSocket = new Socket(serverIP, serverPort);
				System.out.print("[+]Connect to " + serverIP + ":" + serverPort);
				
				//Open input stream and convert to buffered input stream
				File file = new File(filePath);
				FileInputStream transferFile = new FileInputStream(file);
				DataInputStream dataIn = new DataInputStream(transferFile);
			
				//Read 
				byte[] byteArray = new byte[(int)file.length()];
				dataIn.read(byteArray, 0, byteArray.length);
				
				//Send from byte array to buffer output stream
				DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
				dataOut.write(byteArray, 0, byteArray.length);
				dataOut.flush();
				clientSocket.close();
				System.out.println("\n[+]Sending file succeed");
				System.out.println((new Date()).toString());
				Thread.sleep(6000);
				System.out.println((new Date()).toString());
				System.out.println("\nStart again");
;
				
			} catch (UnknownHostException e) {
				System.err.println("[-]Don't know about host " + serverIP + "" +". Exit");
				System.exit(1);
			} catch (IOException e) {
				System.err.println("[-]Couldn't get I/O for the connection to " + serverIP);
				System.exit(1);
			}
		}
	}

}
