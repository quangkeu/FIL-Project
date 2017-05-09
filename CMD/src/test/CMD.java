package test;

import org.jfree.ui.RefineryUtilities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * Created by Nam on 20/04/2017.
 */
public class CMD {
    public static final String command = "sudo tshark -i eth0 -f tcp -T fields -e frame.time_relative -e ip.src -e ip.dst -e tcp.srcport -e tcp.dstport -e frame.cap_len";

    private List<Item> listFlow;//luu cac goi tin dau tien cua cac flow
    private Map<Item,List<Double>> listIAT;//luu danh sach cac paket Inter-Arrival Time cua tung flow
    private Map<Item,List<Integer>> listPktSize;
    private static long start = System.currentTimeMillis();
    public CMD(){
        listFlow = new ArrayList<Item>();
        listIAT= new HashMap<Item,List<Double>>();
        listPktSize= new HashMap<Item,List<Integer>>();
    }

    private int executeCommand(String command) {
        int count1 = 0;
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            Socket clientSocket = new Socket("localhost", 5000);
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

            String line = "";
            StringTokenizer readData;
            while ((line = reader.readLine()) != null) {
                count1++;
                long current = System.currentTimeMillis();
                if(current - start < 10000){
                    List<Double> listIATofFlow = new ArrayList<Double>();
                    List<Integer> pktSize = new ArrayList<Integer>();
                    String[] a = line.trim().split("\\t");
                    Item item = createItem(a);
                    if (getItem(item) == null) {
                        listFlow.add(item);
                        listIAT.put(item, listIATofFlow);
                        listPktSize.put(item, pktSize);
                    } else {
                        listIATofFlow = getIATofFlow(item);
                        double sumIATime = sumIAT(item);
                        double timeStampOfFirstPkt = (Double) getItem(item).getFieldValue(Flow.TIME_STAMP.toString());
                        int count = (Integer) getItem(item).getFieldValue(Flow.COUNT.toString());
                        getItem(item).setAttribute(Flow.COUNT.toString(), count + 1);
                        double IATime = (Double) item.getFieldValue(Flow.TIME_STAMP.toString()) - sumIATime - timeStampOfFirstPkt;

                        int PktSize = (Integer)item.getFieldValue(Flow.PACKET_SIZE.toString());
                        listIATofFlow.add(IATime);
                        pktSize.add(PktSize);
                        listIAT.put(item, listIATofFlow);
                        listPktSize.put(item,pktSize);
                    }
//                    System.out.println("TIME STAMP:"+getItem(item).getFieldValue(Flow.TIME_STAMP.toString())
//                            +"\tIP_SRC :"+getItem(item).getFieldValue(Flow.IP_SRC.toString())
//                            +"\tIP_DST: "+getItem(item).getFieldValue(Flow.IP_DST.toString())
//                            +"\tPORT_SRC: "+getItem(item).getFieldValue(Flow.PORT_SRC.toString())
//                            +"\tPORT_DST: "+getItem(item).getFieldValue(Flow.PORT_DST.toString())
//                            +" \tCount:"+getItem(item).getFieldValue(Flow.COUNT.toString()));
                    System.out.println(line);
                }else {
                    long t = System.currentTimeMillis();
                    int ONE_PKT_ON_FLOW = 0;
                    int TWO_PKT_ON_FLOW = 0;
                    int THREE_PKT_ON_FLOW = 0;

                    int numberFlow = listFlow.size();
                    for(Map.Entry<Item,List<Double>> entry : listIAT.entrySet()){
                        if(entry.getValue().size() == 0){
                            ONE_PKT_ON_FLOW++;
                        }
                        else if(entry.getValue().size() == 1){
                            TWO_PKT_ON_FLOW++;
                        }
                        else if(entry.getValue().size() == 2){
                            THREE_PKT_ON_FLOW++;
                        }
                    }
                    double ONE_PKT_FLOW = ONE_PKT_ON_FLOW*1.0/numberFlow;
                    double TWO_PKT_FLOW = TWO_PKT_ON_FLOW*1.0/numberFlow;
                    double THREE_PKT_FLOW = THREE_PKT_ON_FLOW*1.0/numberFlow;

                    int FLOW_IAT_02 = 0;
                    int FLOW_IAT_24 = 0;
                    int FLOW_IAT_46 = 0;
                    for(int i = 0; i < numberFlow - 1;i++){
                        double flow_iat = (Double)listFlow.get(i+1).getFieldValue(Flow.TIME_STAMP.toString())
                                - (Double) listFlow.get(i).getFieldValue(Flow.TIME_STAMP.toString());
                        if(flow_iat < 0.0002){
                            FLOW_IAT_02++;
                        }else if(flow_iat < 0.0004){
                            FLOW_IAT_24++;
                        }else  if(flow_iat < 0.0004) {
                            FLOW_IAT_46++;
                        }
                    }
                    double Flow_IAT_02 = FLOW_IAT_02 * 1.0 / numberFlow;
                    double Flow_IAT_24 = FLOW_IAT_24 * 1.0 / numberFlow;
                    double Flow_IAT_46 = FLOW_IAT_46 * 1.0 / numberFlow;

                    int TOTAL_BYTE_COUNT = 0;
                    for(Map.Entry<Item,List<Integer>> e : listPktSize.entrySet()){
                        TOTAL_BYTE_COUNT += sum_byte(e.getValue());
                    }

                    Item result = new Item();

                    result.setAttribute(Parameter.NUMBER_FLOW.toString(),numberFlow);
                    result.setAttribute(Parameter.ONE_PKT_ON_FLOW.toString(),ONE_PKT_FLOW);
                    result.setAttribute(Parameter.TWO_PKT_ON_FLOW.toString(),TWO_PKT_FLOW);
                    result.setAttribute(Parameter.THREE_PKT_ON_FLOW.toString(),THREE_PKT_FLOW);
                    result.setAttribute(Parameter.FLOW_IAT_02.toString(), Flow_IAT_02);
                    result.setAttribute(Parameter.FLOW_IAT_24.toString(), Flow_IAT_24);
                    result.setAttribute(Parameter.FLOW_IAT_46.toString(), Flow_IAT_46);
                    result.setAttribute(Parameter.BYTE_COUNT.toString(), TOTAL_BYTE_COUNT);

                    listFlow = new ArrayList<Item>();
                    listIAT= new HashMap<Item,List<Double>>();
                    listPktSize= new HashMap<Item,List<Integer>>();
                    double z = System.currentTimeMillis() - t;

                    out.writeObject(result);
                    start = current;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage().toString());
        }
        return 0;
    }

    public boolean flowCompare(Item i,Item item)
    {
        if (i.getFieldValue(Flow.IP_SRC.toString()).equals(item.getFieldValue(Flow.IP_SRC.toString()))
                && i.getFieldValue(Flow.IP_DST.toString()).equals(item.getFieldValue(Flow.IP_DST.toString()))
                && i.getFieldValue(Flow.PORT_SRC.toString()).equals(item.getFieldValue(Flow.PORT_SRC.toString()))
                && i.getFieldValue(Flow.PORT_DST.toString()).equals(item.getFieldValue(Flow.PORT_DST.toString())))
            return true;

        return false;
    }

    public Item getItem(Item item){
        for(Item i : listFlow){
            if(flowCompare(i,item)){
                return i;
            }
        }
        return null;
    }

    public Item createItem(String a[]){
        Item item = new Item();

        item.setAttribute(Flow.TIME_STAMP.toString(), Double.parseDouble(a[0]));
        item.setAttribute(Flow.IP_SRC.toString(),a[1]);
        item.setAttribute(Flow.IP_DST.toString(),a[2]);
        item.setAttribute(Flow.PORT_SRC.toString(),a[3]);
        item.setAttribute(Flow.PORT_DST.toString(),a[4]);
        item.setAttribute(Flow.COUNT.toString(),1);
        item.setAttribute(Flow.PACKET_SIZE.toString(),Integer.parseInt(a[5]));
        return item;
    }

    public List<Double> getIATofFlow(Item item){
        for (Map.Entry<Item,List<Double>> entry : listIAT.entrySet()){
            if(flowCompare(entry.getKey(),item)){
                return entry.getValue();
            }
        }
        return null;
    }

    public double sumIAT(Item item){
        List<Double> l = getIATofFlow(item);
        double sum = 0;
        for(double iat : l){
            sum += iat;
        }
        return sum;
    }
    public int sum_byte(List<Integer> l){
        int sum = 0;
        for(int iat : l){
            sum += iat;
        }
        return sum;
    }

    public List<Item> getListFlow() {
        return listFlow;
    }


    public static void main(String[] args) throws InterruptedException {
        CMD cmd = new CMD();
        cmd.executeCommand(command);


    }

}
