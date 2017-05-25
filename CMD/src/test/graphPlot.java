package test;

/**
 * Created by quang on May 9 2017
 */

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.data.xy.XYDataset;
import org.jfree.*;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;

public class graphPlot extends ApplicationFrame implements ActionListener{

    public static double onePacketFlowRate = 0.0;
    public static double packetIATRate = 0.0;

    private TimeSeries onePacketFlowSeries;
    private TimeSeries packetIATSeries;
    private Timer sixSecTimer = new Timer(6000, this);

    public graphPlot(final String title) {
        super(title);

        this.onePacketFlowSeries = new TimeSeries("1-packet Flow Rate");
        this.packetIATSeries = new TimeSeries("Packet IAT < 0.2ms Rate");

        final TimeSeriesCollection onePacketFlowDataset = new TimeSeriesCollection(this.onePacketFlowSeries);
        final TimeSeriesCollection packetIATDataset = new TimeSeriesCollection(this.packetIATSeries);

        final JFreeChart onePacketFlowChart = createChart(onePacketFlowDataset, "1-packet Flow Rate","Percentage", 100.0);
        final JFreeChart packetIATChart = createChart(packetIATDataset, "Packet IAT < 0.2ms Rate", "Percentage", 100.0);

        sixSecTimer.setInitialDelay(1000);

        onePacketFlowChart.setBackgroundPaint(Color.WHITE);
        packetIATChart.setBackgroundPaint(Color.WHITE);

        //Create JPanel to show graph on screen
        final JPanel mainPanel = new JPanel(new GridLayout(1, 3));

        //Create ChartPanel for chart area
        final ChartPanel onePacketFlowPanel = new ChartPanel(onePacketFlowChart);
        final ChartPanel packetIATPanel = new ChartPanel(packetIATChart);

        //Add chartPanel to mainPanel
        mainPanel.add(onePacketFlowPanel);
        mainPanel.add(packetIATPanel);

        //Set size
        onePacketFlowPanel.setPreferredSize(new Dimension(500, 400));
        packetIATPanel.setPreferredSize(new Dimension(500, 400));

        setContentPane(mainPanel);

        sixSecTimer.start();


    }


    /*
    Create a sample chart
    @param dataset the dataset
    @return A JFreeChart sample chart
     */

    private JFreeChart createChart(final XYDataset dataset, String title, String valueAxisLabel, double upper) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
                title,
                "Seconds",
                valueAxisLabel,
                dataset,
                true,
                true,
                false
        );

        final XYPlot plot = result.getXYPlot();

        //Customize chart
        plot.setBackgroundPaint(new Color(0xffffe0));
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.lightGray);

        ValueAxis xaxis = plot.getDomainAxis();
        xaxis.setAutoRange(true);

        //Domain axis would show data of 60 seconds for a time
        xaxis.setFixedAutoRange(60000.0);
        xaxis.setVerticalTickLabels(true);

        ValueAxis yaxis = plot.getRangeAxis();
        yaxis.setRange(0.0, upper);

        return result;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Second currentSecond = new Second();
        this.onePacketFlowSeries.add(currentSecond, this.onePacketFlowRate*100);
        this.packetIATSeries.add(currentSecond, this.packetIATRate*100);

        System.out.println("------------------------");
        System.out.println("Current time = " + currentSecond.toString() + ", 1-packet Flow Rate = " + this.onePacketFlowRate*100);
        System.out.println("Current time = " + currentSecond.toString() + ", Packet IAT < 0.2ms Rate = " + this.packetIATRate*100);
        System.out.println("------------------------");
    }
}
