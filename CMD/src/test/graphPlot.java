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
import java.util.Date;
import javax.swing.*;


/**
 * Created by quang on 5/9/2017.
 */
public class graphPlot extends ApplicationFrame implements ActionListener{

    public static double rate = 0.0;

    private TimeSeries series;
    private Timer timer = new Timer(6000, this);

    public graphPlot(final String title) {
        super(title);
        this.series = new TimeSeries("1-packet Flow Rate");

        final TimeSeriesCollection dataset = new TimeSeriesCollection(this.series);
        final JFreeChart chart = createChart(dataset);

        timer.setInitialDelay(1000);

        chart.setBackgroundPaint(Color.GRAY);

        //Create JPanel to show graph on screen
        final JPanel mainPanel = new JPanel(new BorderLayout());

        //Create ChartPanel for chart area
        final ChartPanel chartPanel = new ChartPanel(chart);

        //Add chartPanel to mainPanel
        mainPanel.add(chartPanel);

        //Set size
        chartPanel.setPreferredSize(new Dimension(800, 600));

        setContentPane(mainPanel);

        timer.start();

    }

    /*
    Create a sample chart
    @param dataset the dataset
    @return A JFreeChart sample chart
     */

    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
                "1-packet Flow Rate",
                "Seconds",
                "Percentage",
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
        yaxis.setRange(0.0, 100.0);

        return result;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Second currentSecond = new Second();
        this.series.add(currentSecond, this.rate*100);

        System.out.println("Current time = " + currentSecond.toString() + ", 1-packet Flow Rate = " + this.rate*100);
    }
}
