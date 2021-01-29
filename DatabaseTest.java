package src.tests;
import src.dataManagePackage.Database;
import src.dataManagePackage.Taxpayer;
import org.junit.Test;
import src.outputManagePackage.UpdateInputFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static src.constants.ApplicationConstants.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class DatabaseTest {

    private  Database database = Database.getInstance();

    @Test
    public void addTaxpayerToList() {

        Taxpayer taxpayer = taxpayer();
        int size = database.getTaxpayersArrayListSize();
        database.addTaxpayerToList(taxpayer);
        assertEquals(size + 1, database.getTaxpayersArrayListSize());

    }

    @Test
    public void getTaxpayerFromArrayList() {

        Taxpayer taxpayer = taxpayer();
        database.addTaxpayerToList(taxpayer);
        assertEquals(taxpayer, database.getTaxpayerFromArrayList(database.getTaxpayersArrayListSize() - 1));

    }

    @Test
    public void removeTaxpayerFromArrayList() {

        taxpayer();
        int size = database.getTaxpayersArrayListSize();
        database.removeTaxpayerFromArrayList(0);
        assertEquals(size - 1, database.getTaxpayersArrayListSize());

    }

    @Test
    public void getTaxpayerNameAfmValuesPairList() {

        String nameAfm  = "Nikos Zisis | 130456094";
        taxpayer();
        assertEquals(nameAfm, database.getTaxpayerNameAfmValuesPairList(TAXPAYER_INDEX_XML));

    }

    @Test
    public void getTaxpayersNameAfmValuesPairList() {

        taxpayer();
        String[] taxpayersNameAfmValuesPairList = {"Apostolos Zarras | 130456093", "Nikos Zisis | 130456094"};
        assertArrayEquals(taxpayersNameAfmValuesPairList,
                database.getTaxpayersNameAfmValuesPairList());
    }

    @Test
    public void updateTaxpayerInputFile() throws IOException {

        initializeTaxPayers();
        CheckInfoEquality(database.getTaxpayerFromArrayList(TAXPAYER_INDEX_TXT), INPUT_FILE_TYPE_TXT, TAXPAYER_INDEX_TXT);
        CheckInfoEquality(database.getTaxpayerFromArrayList(TAXPAYER_INDEX_XML), INPUT_FILE_TYPE_XML, TAXPAYER_INDEX_XML);

    }

    private void CheckInfoEquality(Taxpayer taxpayer, String typeOfInputFile, int taxpayerIndex) throws IOException {

        UpdateInputFile updateInputFile = new UpdateInputFile(typeOfInputFile);
        database.setTaxpayersInfoFilesPath("files/InputFiles/");
        taxpayer.removeReceiptFromList(0);
        database.updateTaxpayerInputFile(taxpayerIndex);

        String[] taxpayerInfo = updateInputFile.getTaxPayerInfo(taxpayerIndex);
        ArrayList<ArrayList<String>> taxpayerReceipts = updateInputFile.getReceipts(taxpayerIndex);
        ArrayList<ArrayList<String[]>> infoFromTemplateFile = updateInputFile.getInfoFromTemplateFile();
        BufferedReader bufRead = getBufferedReader("files/InputFiles/" + taxpayer.getAFM() + "_INFO." + typeOfInputFile.toLowerCase());

        String myLine;
        int templateIndex = 0;
        int taxpayerValue = 0;

        while(!(myLine = bufRead.readLine()).isBlank()){

            assertEquals(myLine,
                    infoFromTemplateFile.get(0).get(templateIndex)[0].concat(taxpayerInfo[taxpayerValue].
                            concat(infoFromTemplateFile.get(0).get(templateIndex)[1])));
            templateIndex++;
            taxpayerValue++;
        }

        bufRead.readLine(); //read "Receipts: " or "<Receipts>"
        bufRead.readLine(); // read blank line

        int receiptIndex = 0;

        for(int i =0 ; i < taxpayer.getReceiptsArrayList().size() - 1;i++){

            templateIndex = 1;
            int currentReceiptValue = 0;
            while(!(myLine = bufRead.readLine()).isBlank()){

                assertEquals(myLine,
                        infoFromTemplateFile.get(1).get(templateIndex)[0].
                                concat(taxpayerReceipts.get(receiptIndex).get(currentReceiptValue).
                                        concat(infoFromTemplateFile.get(1).get(templateIndex)[1])));
                templateIndex++;
                currentReceiptValue++;
            }
            receiptIndex++;
        }
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

    private BufferedReader getBufferedReader(String path) throws FileNotFoundException {
        FileReader input = null;
        input = new FileReader(path);
        BufferedReader bufRead = new BufferedReader(input);
        return bufRead;
    }

}
