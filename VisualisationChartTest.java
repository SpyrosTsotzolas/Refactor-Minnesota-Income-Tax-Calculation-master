package src.tests;
import src.dataManagePackage.Database;
import src.dataManagePackage.Taxpayer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.junit.Test;
import src.outputManagePackage.VisualisationChart;
import static src.constants.ApplicationConstants.*;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class VisualisationChartTest {

    private Database database = Database.getInstance();
    private VisualisationChart visualisationChart = VisualisationChart.getInstance();

    @Test
    public void createTaxpayerReceiptsPieJFreeChart() {
        Taxpayer taxpayer = initializeTaxpayer();
        visualisationChart.createTaxpayerReceiptsPieJFreeChart(TAXPAYER_INDEX_TXT);
        DefaultPieDataset receiptPieChartDataset = visualisationChart.getReceiptPieChartDataset();

        assertEquals(taxpayer.getSpecificReceiptsTotalAmount("Basic"), receiptPieChartDataset.getValue("Basic"));
        assertEquals(taxpayer.getSpecificReceiptsTotalAmount("Entertainment"), receiptPieChartDataset.getValue("Entertainment"));
        assertEquals(taxpayer.getSpecificReceiptsTotalAmount("Travel"), receiptPieChartDataset.getValue("Travel"));
        assertEquals(taxpayer.getSpecificReceiptsTotalAmount("Health"), receiptPieChartDataset.getValue("Health"));
        assertEquals(taxpayer.getSpecificReceiptsTotalAmount("Other"), receiptPieChartDataset.getValue("Other"));

    }

    @Test
    public void createTaxpayerTaxAnalysisBarJFreeChart() {
        Taxpayer taxpayer = initializeTaxpayer();
        String taxVariationType = taxpayer.getTaxInxrease()!=0? "Tax Increase" : "Tax Decrease";
        double taxVariationAmount = taxpayer.getTaxInxrease()!=0? taxpayer.getTaxInxrease() : taxpayer.getTaxDecrease()*(-1);

        visualisationChart.createTaxpayerTaxAnalysisBarJFreeChart(TAXPAYER_INDEX_TXT);
        DefaultCategoryDataset taxAnalysisBarChartDataset = visualisationChart.getTaxAnalysisBarChartDataset();

        assertEquals(taxpayer.getBasicTax(), taxAnalysisBarChartDataset.getValue("Tax", "Basic Tax"));
        assertEquals(taxVariationAmount, taxAnalysisBarChartDataset.getValue("Tax", taxVariationType));
        assertEquals(taxpayer.getTotalTax(), taxAnalysisBarChartDataset.getValue("Tax", "Total Tax"));


    }

    private Taxpayer initializeTaxpayer() {
        List<String> files = new ArrayList<>();
        files.add("130456094_INFO.xml");
        files.add("130456093_INFO.txt");
        database.proccessTaxpayersDataFromFilesIntoDatabase("files/InputFiles", files);

        return database.getTaxpayerFromArrayList(TAXPAYER_INDEX_TXT);
    }

}