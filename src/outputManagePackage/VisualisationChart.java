package src.outputManagePackage;

import src.dataManagePackage.Database;
import src.dataManagePackage.Taxpayer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.text.DecimalFormat;

public class VisualisationChart {

    private Database database = Database.getInstance();
    private ChartFrame receiptPieChartFrame;
    private DefaultPieDataset receiptPieChartDataset;

    public DefaultPieDataset getReceiptPieChartDataset() {
        return receiptPieChartDataset;
    }

    public DefaultCategoryDataset getTaxAnalysisBarChartDataset() {
        return taxAnalysisBarChartDataset;
    }

    private DefaultCategoryDataset taxAnalysisBarChartDataset;
    private JFreeChart receiptPieJFreeChart;
    private PiePlot piePlot;

    private static VisualisationChart firstInstance = null;

    private VisualisationChart(){}

    public static VisualisationChart getInstance() {
        if(firstInstance == null) {
            firstInstance = new VisualisationChart();
        }

        return firstInstance;
    }
    public void createTaxpayerReceiptsPieJFreeChart(int taxpayerIndex){
        receiptPieChartDataset = new DefaultPieDataset();
        Taxpayer taxpayer = database.getTaxpayerFromArrayList(taxpayerIndex);

        receiptPieChartDataset.setValue("Basic", taxpayer.getSpecificReceiptsTotalAmount("Basic"));
        receiptPieChartDataset.setValue("Entertainment", taxpayer.getSpecificReceiptsTotalAmount("Entertainment"));
        receiptPieChartDataset.setValue("Travel", taxpayer.getSpecificReceiptsTotalAmount("Travel"));
        receiptPieChartDataset.setValue("Health", taxpayer.getSpecificReceiptsTotalAmount("Health"));
        receiptPieChartDataset.setValue("Other", taxpayer.getSpecificReceiptsTotalAmount("Other"));

        receiptPieJFreeChart = ChartFactory.createPieChart("Receipt Pie Chart", receiptPieChartDataset);
        piePlot = (PiePlot)receiptPieJFreeChart.getPlot();
        PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{0}: {1}$ ({2})", new DecimalFormat("0.00"), new DecimalFormat("0.00%"));
        piePlot.setLabelGenerator(generator);

        receiptPieChartFrame = new ChartFrame(database.getTaxpayerNameAfmValuesPairList(taxpayerIndex), receiptPieJFreeChart);
        receiptPieChartFrame.pack();
        receiptPieChartFrame.setResizable(false);
        receiptPieChartFrame.setLocationRelativeTo(null);
        receiptPieChartFrame.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        receiptPieChartFrame.setVisible(true);
    }

    public void createTaxpayerTaxAnalysisBarJFreeChart(int taxpayerIndex){

        taxAnalysisBarChartDataset = new DefaultCategoryDataset();
        Taxpayer taxpayer = database.getTaxpayerFromArrayList(taxpayerIndex);

        String taxVariationType = taxpayer.getTaxInxrease()!=0? "Tax Increase" : "Tax Decrease";
        double taxVariationAmount = taxpayer.getTaxInxrease()!=0? taxpayer.getTaxInxrease() : taxpayer.getTaxDecrease()*(-1);

        taxAnalysisBarChartDataset.setValue(taxpayer.getBasicTax(), "Tax", "Basic Tax");
        taxAnalysisBarChartDataset.setValue(taxVariationAmount, "Tax", taxVariationType);
        taxAnalysisBarChartDataset.setValue(taxpayer.getTotalTax(), "Tax", "Total Tax");

        JFreeChart taxAnalysisJFreeChart = ChartFactory.createBarChart("Tax Analysis Bar Chart", "",  "Tax Analysis in $", taxAnalysisBarChartDataset, PlotOrientation.VERTICAL, true, true, false);

        ChartFrame receiptPieChartFrame = new ChartFrame(database.getTaxpayerNameAfmValuesPairList(taxpayerIndex), taxAnalysisJFreeChart);
        receiptPieChartFrame.pack();
        receiptPieChartFrame.setResizable(false);
        receiptPieChartFrame.setLocationRelativeTo(null);
        receiptPieChartFrame.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        receiptPieChartFrame.setVisible(true);
    }
}
