package src.tests;

import src.dataManagePackage.Database;
import src.dataManagePackage.Taxpayer;
import org.junit.Test;
import src.managerTags.ParsingTags;
import src.outputManagePackage.GeneratorLogFile;
import static src.constants.ApplicationConstants.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class GeneratorLogFileTest {

    private Database database = Database.getInstance();
    private ParsingTags parsingTags = new ParsingTags();

    @Test
    public void saveTaxpayerInfoToLogFile() throws IOException {

        initializeTaxPayers();
        CheckInfoEquality(database.getTaxpayerFromArrayList(TAXPAYER_INDEX_TXT), INPUT_FILE_TYPE_TXT, OUTPUT_FILE_TYPE_TXT);
        CheckInfoEquality(database.getTaxpayerFromArrayList(TAXPAYER_INDEX_TXT), INPUT_FILE_TYPE_TXT, OUTPUT_FILE_TYPE_XML);
        CheckInfoEquality(database.getTaxpayerFromArrayList(TAXPAYER_INDEX_XML), INPUT_FILE_TYPE_XML, OUTPUT_FILE_TYPE_TXT);
        CheckInfoEquality(database.getTaxpayerFromArrayList(TAXPAYER_INDEX_XML), INPUT_FILE_TYPE_XML, OUTPUT_FILE_TYPE_XML);

    }

    private void initializeTaxPayers() {
        List<String> files = new ArrayList<>();
        files.add("130456093_INFO.txt");
        files.add("130456094_INFO.xml");
        database.proccessTaxpayersDataFromFilesIntoDatabase("files/InputFiles", files);
    }

    private void CheckInfoEquality(Taxpayer taxpayer, String typeOfInputFile, String typeOfLOGFile) throws IOException {

        GeneratorLogFile generatorLogFile = new GeneratorLogFile(typeOfLOGFile);

        BufferedReader bufRead = new BufferedReader(getFileReader(typeOfInputFile, typeOfLOGFile, generatorLogFile));
        String myLine = null;

        int lineOfFileItem=0;

        while((myLine = bufRead.readLine())!=null){
            assertEquals(myLine,getLinesOfFile(taxpayer, generatorLogFile.getTaxPayerInfo(taxpayer),
                    parsingTags.getTagsForLogFile("TagsLogFile"+typeOfLOGFile)).get(lineOfFileItem));
            lineOfFileItem++;
        }
    }

    private ArrayList<String> getLinesOfFile(Taxpayer taxpayer, String[] taxpayerInfo, ArrayList<String[]> infoFromTemplateFile) {
        ArrayList<String> linesOfFile = new ArrayList<>();

        for(int i = 0; i < taxpayerInfo.length; i++) {

            if ( i == CHECK_IF_INCREASE){
                if (taxpayer.getTaxInxrease() == 0) {
                    i++;    //overpass "Tax Increase: " go to "Tax Decrease: "
                    linesOfFile.add(infoFromTemplateFile.get(i)[0].concat(taxpayerInfo[i].concat(infoFromTemplateFile.get(i)[1])));
                } else {
                    linesOfFile.add(infoFromTemplateFile.get(i)[0].concat(taxpayerInfo[i].concat(infoFromTemplateFile.get(i)[1])));
                    i++;    //overpass "Tax Decrease: "
                }
                continue;
            }
            linesOfFile.add(infoFromTemplateFile.get(i)[0].concat(taxpayerInfo[i].concat(infoFromTemplateFile.get(i)[1])));
        }
        return linesOfFile;
    }

    private FileReader getFileReader(String typeOfFile, String typeOfLOGFile, GeneratorLogFile generatorLogFile) throws FileNotFoundException {
        FileReader input = null;
        if(typeOfFile.equals(INPUT_FILE_TYPE_TXT)) {

            input = readFromFile(typeOfLOGFile, generatorLogFile, TAXPAYER_INDEX_TXT);

        }else{
            input = readFromFile(typeOfLOGFile, generatorLogFile, TAXPAYER_INDEX_XML);
        }
        return input;
    }

    private FileReader readFromFile(String typeOfLOGFile, GeneratorLogFile generatorLogFile, int index) throws FileNotFoundException {

        FileReader input;
        generatorLogFile.saveTaxpayerInfoToLogFile("files/filesFormat/", index);

        if (typeOfLOGFile.equals(OUTPUT_FILE_TYPE_TXT))
            input = new FileReader("files/filesFormat/" + database.getTaxpayerFromArrayList(index).getAFM() + "_LOG.txt");
        else
            input = new FileReader("files/filesFormat/" + database.getTaxpayerFromArrayList(index).getAFM() + "_LOG.xml");
        return input;
    }
}