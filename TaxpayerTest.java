package src.tests;

import src.dataManagePackage.Database;
import src.dataManagePackage.Receipt;
import src.dataManagePackage.Taxpayer;
import org.junit.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class TaxpayerTest {

    private final int TAXPAYER_INDEX_TXT = 0;
    private final int TAXPAYER_INDEX_XML = 1;
    private Database database = Database.getInstance();

    @Test
    public void calculateTaxForMarriedFilingJointlyTaxpayerFamilyStatus() {
        Taxpayer taxpayer = taxpayer();

        double tax;
        double totalIncome = 1000.0;

        if (totalIncome < 36080){
            tax = (5.35/100) * totalIncome;
        }
        else if (totalIncome < 90000){
            tax = 1930.28 + ((7.05/100) * (totalIncome-36080));
        }
        else if (totalIncome < 143350){
            tax = 5731.64 + ((7.05/100) * (totalIncome-90000));
        }
        else if (totalIncome < 254240){
            tax = 9492.82 + ((7.85/100) * (totalIncome-143350));
        }
        else{
            tax = 18197.69 + ((9.85/100) * (totalIncome-254240));
        }

        ArrayList<ArrayList<Double>> taxpayerAmounts = new ArrayList<>();
        taxpayerAmounts.add(taxpayer.getFamilyStatusObject().getRates());
        taxpayerAmounts.add(taxpayer.getFamilyStatusObject().getValues());
        taxpayerAmounts.add(taxpayer.getFamilyStatusObject().getIncomes());

        assertEquals(tax, taxpayer.calculateTax(totalIncome,taxpayerAmounts));
    }

    @Test
    public void getReceiptsList() {

        String[] receiptsList = new String[taxpayer().getReceiptsArrayList().size()];
        int c = 0;
        for (Receipt receipt : taxpayer().getReceiptsArrayList()){
            receiptsList[c++] = receipt.getId() + " | " + receipt.getDate() + " | " + receipt.getAmount();
        }

        assertArrayEquals(receiptsList,
                taxpayer().getReceiptsList());

    }

    @Test
    public void getSpecificReceiptsTotalAmount() {

        double totalAmount = 0;
        for (Receipt receipt : taxpayer().getReceiptsArrayList()){
            if (receipt.getKind().equals(taxpayer().getReceipt(0).getKind())){
                totalAmount += receipt.getAmount();
            }
        }
        assertEquals(totalAmount, taxpayer().getSpecificReceiptsTotalAmount(taxpayer().getReceipt(0).getKind()));

    }

    @Test
    public void getTotalReceiptsAmount() {

        double totalReceiptsAmount = 0;
        for (Receipt receipt : taxpayer().getReceiptsArrayList()){
            totalReceiptsAmount += receipt.getAmount();
        }
        assertEquals(totalReceiptsAmount, taxpayer().getTotalReceiptsAmount());
    }

    @Test
    public void addReceiptToList() {

        int size = taxpayer().getReceiptsArrayList().size();
        taxpayer().addReceiptToList(taxpayer().getReceipt(0)); // add receipt by coping the first receipt
        assertEquals(size +1, taxpayer().getReceiptsArrayList().size());

    }

    @Test
    public void removeReceiptFromList() {

        taxpayer().addReceiptToList(taxpayer().getReceipt(0)); // add receipt by coping the first receipt
        int size = taxpayer().getReceiptsArrayList().size();
        taxpayer().removeReceiptFromList(0);

        assertEquals(size - 1, taxpayer().getReceiptsArrayList().size());
    }

    @Test
    public void calculateTaxpayerTaxIncreaseOrDecreaseBasedOnReceipts() {

        double taxIncrease = taxpayer().getTaxInxrease();
        double taxDecrease = taxpayer().getTaxDecrease();

        if ((taxpayer().getTotalReceiptsAmount() / taxpayer().getIncome()) < 0.2){
            taxIncrease = taxpayer().getBasicTax() * 0.08;
        }
        else if ((taxpayer().getTotalReceiptsAmount() / taxpayer().getIncome()) < 0.4){
            taxIncrease = taxpayer().getBasicTax() * 0.04;
        }
        else if ((taxpayer().getTotalReceiptsAmount() / taxpayer().getIncome()) < 0.6){
            taxDecrease = taxpayer().getBasicTax() * 0.15;
        }
        else{
            taxDecrease = taxpayer().getBasicTax() * 0.30;
        }
        double tax = taxpayer().getBasicTax() + taxIncrease - taxDecrease;
        taxpayer().calculateTaxpayerTaxIncreaseOrDecreaseBasedOnReceipts();

        assertEquals(new BigDecimal(tax).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(),
                taxpayer().getTotalTax());
    }

    private Taxpayer taxpayer(){

        initializeTaxPayers();
        return database.getTaxpayerFromArrayList(TAXPAYER_INDEX_TXT);

    }

    private void initializeTaxPayers() {
        List<String> files = new ArrayList<>();
        files.add("130456093_INFO.txt");
        files.add("130456094_INFO.xml");
        database.proccessTaxpayersDataFromFilesIntoDatabase("files/InputFiles", files);
    }

}
