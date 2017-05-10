package test;

import org.jfree.ui.RefineryUtilities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by odldev on 4/24/17.
 */
public class Server {

    static graphPlot plot;

    public final static int DEFAULT_PORT = 5000;
    private static void communicate(Socket connSocket){
        try {
            ObjectInputStream in = new ObjectInputStream(connSocket.getInputStream());
            Item item;
            try{
                while((item = (Item) in.readObject()) != null) {
                    plot.rate = Double.parseDouble((String) item.getFieldValue(Parameter.ONE_PKT_ON_FLOW.toString()));

                    System.out.println("-------------------------------------");
                    System.out.println(" Number Flow Received: " + item.getFieldValue(Parameter.NUMBER_FLOW.toString()));
                    System.out.println(" % Flow IAT 0-0,2 ms Received: " + item.getFieldValue(Parameter.FLOW_IAT_02.toString()));
                    System.out.println(" % Flow IAT 0,2-0,4 ms Received: " + item.getFieldValue(Parameter.FLOW_IAT_24.toString()));
                    System.out.println(" % Flow IAT 0,4-0,6 ms Received: " + item.getFieldValue(Parameter.FLOW_IAT_46.toString()));
                    System.out.println("% flow has 1 pkt Received: " +  item.getFieldValue(Parameter.ONE_PKT_ON_FLOW.toString()));
                    System.out.println("% flow has 2 pkt Received: " +  item.getFieldValue(Parameter.TWO_PKT_ON_FLOW.toString()));
                    System.out.println("% flow has 3 pkt Received: " +  item.getFieldValue(Parameter.THREE_PKT_ON_FLOW.toString()));
                    System.out.println(" Total byte Received: " + item.getFieldValue(Parameter.BYTE_COUNT.toString()));
                    System.out.println("-------------------------------------");
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Invalid data from client!");
            } catch(IOException e){
                System.out.println("Client stopped sending data!");
            }
        } catch(IOException e){
            System.out.println("Cannot communicate to client!");
        }
    }

    public static void main(String[] args) {

        //Graph plotting
        System.out.println("[+] Graph Plotting");
        plot = new graphPlot("1-packet Flow Rate");
        plot.pack();
        RefineryUtilities.centerFrameOnScreen(plot);
        plot.setVisible(true);

        try {
            ServerSocket servSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("[+] Starting server socket at " + DEFAULT_PORT);
            while (true){
                try{
                    Socket connSocket = servSocket.accept();
                    communicate(connSocket);
                } catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }


    }
}
