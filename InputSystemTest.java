package src.tests;

import src.dataManagePackage.Database;
import src.dataManagePackage.Receipt;
import src.dataManagePackage.Taxpayer;
import src.inputManagePackage.InputSystem;
import src.managerTags.ParsingTags;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static src.constants.ApplicationConstants.*;
import static junit.framework.TestCase.assertEquals;


public class InputSystemTest {

    private  Database database = Database.getInstance();
    private InputSystem inputSystem = InputSystem.getInstance();
    private ParsingTags parsingTags = new ParsingTags();


    @Test
    public void addTaxpayersDataFromFilesIntoDatabase() throws IOException {

        testFiles("files/InputFiles/130456094_INFO.xml", parsingTags.getTagsForUpdatedFile("TagsInputFileXML"));
        testFiles("files/InputFiles/130456093_INFO.txt", parsingTags.getTagsForUpdatedFile("TagsInputFileTXT"));

    }

    private void testFiles(String path,  ArrayList<ArrayList<String[]>> tagsForFile) throws IOException {

        Taxpayer taxpayer;
        if(path.endsWith(INPUT_FILE_FORMAT_TXT)) taxpayer = taxpayer(TAXPAYER_INDEX_TXT);
        else taxpayer = taxpayer(TAXPAYER_INDEX_XML);

        String[] taxpayerInfo = {taxpayer.getName(), taxpayer.getAFM(), taxpayer.getFamilyStatus(),
                String.valueOf(taxpayer.getIncome())};
        ArrayList<ArrayList<String>> allReceipts = getReceipts(taxpayer.getReceiptsArrayList());

        FileReader input = null;
        input = new FileReader(path);
        BufferedReader bufRead = new BufferedReader(input);
        String myLine = null;
        int lineCounterTaxPayer = 0;
        boolean flagReceipts = false;

        while((myLine = bufRead.readLine()) != null){

            if(myLine.isBlank() || myLine.equals("Receipts:") ||myLine.equals("<Receipts>")){
                flagReceipts = true;
                continue;
            }
            if(myLine.equals("</Receipts>")) break;

            if ( flagReceipts ) {
                for (int i =0; i< allReceipts.size(); i++){
                    if(myLine.isBlank())  myLine = bufRead.readLine();
                    for (int j =0; j< 9; j++) {
                        if(path.endsWith(INPUT_FILE_FORMAT_TXT)){
                            assertEquals(myLine.substring(tagsForFile.get(RECEIPTS_TAGS).get(j)[0].length(),
                                    myLine.length() - tagsForFile.get(RECEIPTS_TAGS).get(j)[1].length()),
                                    allReceipts.get(i).get(j));
                        }
                        else{
                            assertEquals(myLine.substring(tagsForFile.get(RECEIPTS_TAGS).get(j)[0].length(),
                                    myLine.length() - tagsForFile.get(RECEIPTS_TAGS).get(j)[1].length()),
                                    allReceipts.get(i).get(j));
                        }

                        myLine = bufRead.readLine();
                    }
                }
            }
            else {
                if(path.endsWith(INPUT_FILE_FORMAT_TXT)) {
                    assertEquals(myLine.substring(tagsForFile.get(TAXPAYER_TAGS).get(lineCounterTaxPayer)[0].length(),
                            myLine.length() - tagsForFile.get(TAXPAYER_TAGS).get(lineCounterTaxPayer)[1].length()),
                            taxpayerInfo[lineCounterTaxPayer]);
                }else{
                    assertEquals(myLine.substring(tagsForFile.get(TAXPAYER_TAGS).get(lineCounterTaxPayer)[0].length(),
                            myLine.length() - tagsForFile.get(TAXPAYER_TAGS).get(lineCounterTaxPayer)[1].length()),
                            taxpayerInfo[lineCounterTaxPayer]);
                }
                lineCounterTaxPayer++;
            }

        }
    }


    private Taxpayer taxpayer(int index){
        initializeTaxPayers();
        return database.getTaxpayerFromArrayList(index);
    }

    private void initializeTaxPayers() {
        List<String> files = new ArrayList<>();
        files.add("130456093_INFO.txt");
        files.add("130456094_INFO.xml");
        try {
            inputSystem.addTaxpayersDataFromFilesIntoDatabase("files/InputFiles", files);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<ArrayList<String>> getReceipts(ArrayList<Receipt>  receipt){

        ArrayList<ArrayList<String>> allReceipts = new ArrayList<>();
        for(int i =0; i<receipt.size(); i++){
            ArrayList<String> currentReceipt = new ArrayList<>();
            currentReceipt.add(receipt.get(i).getId());
            currentReceipt.add(receipt.get(i).getDate());
            currentReceipt.add(receipt.get(i).getKind());
            currentReceipt.add(String.valueOf(receipt.get(i).getAmount()));
            currentReceipt.add(receipt.get(i).getCompany().getName());
            currentReceipt.add(receipt.get(i).getCompany().getCountry());
            currentReceipt.add(receipt.get(i).getCompany().getCity());
            currentReceipt.add(receipt.get(i).getCompany().getStreet());
            currentReceipt.add(receipt.get(i).getCompany().getNumber());
            allReceipts.add(currentReceipt);
        }
        return allReceipts;
    }
}
