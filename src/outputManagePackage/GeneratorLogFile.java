package src.outputManagePackage;

import src.dataManagePackage.Database;
import src.dataManagePackage.Taxpayer;
import src.managerTags.ParsingTags;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import static src.constants.ApplicationConstants.*;

public class GeneratorLogFile {

    private Database database = Database.getInstance();
    private final String typeOfFile;
    private ArrayList<String[]> tags;

    public GeneratorLogFile(String typeOfFile) {
        this.typeOfFile = typeOfFile;
        ParsingTags parsingTags = new ParsingTags();
        if (typeOfFile.endsWith(INPUT_FILE_TYPE_TXT)){
            tags = parsingTags.getTagsForLogFile("TagsLogFileTXT");
        }
        else if (typeOfFile.endsWith(INPUT_FILE_TYPE_XML)){
            tags = parsingTags.getTagsForLogFile("TagsLogFileXML");
        }

    }

    public void saveTaxpayerInfoToLogFile(String folderSavePath, int taxpayerIndex){

        PrintWriter outputStream = null;
        try
        {
            if(typeOfFile.equals(INPUT_FILE_TYPE_XML)) {
                outputStream = new PrintWriter(new FileOutputStream(folderSavePath + "//" + getTaxpayer(taxpayerIndex).getAFM() + "_LOG.xml"));
            }
            else if(typeOfFile.equals(INPUT_FILE_TYPE_TXT)){
                outputStream = new PrintWriter(new FileOutputStream(folderSavePath+"//"+getTaxpayer(taxpayerIndex).getAFM()+"_LOG.txt"));
            }
        }
        catch(FileNotFoundException e)
        {
            System.out.println("Problem opening: "+folderSavePath+"//"+getTaxpayer(taxpayerIndex).getAFM()+"_LOG.xml");
        }

        WriteToLogFile(getTaxpayer(taxpayerIndex), outputStream);

        outputStream.close();

        JOptionPane.showMessageDialog(null, "Η αποθήκευση ολοκληρώθηκε", "Μήνυμα", JOptionPane.INFORMATION_MESSAGE);
    }

    private Taxpayer getTaxpayer(int taxpayerIndex) {
        return database.getTaxpayerFromArrayList(taxpayerIndex);
    }

    private void WriteToLogFile(Taxpayer taxpayer, PrintWriter outputStream) {
        for(int i = 0; i < getTaxPayerInfo(taxpayer).length; i++){

            if ( i == CHECK_IF_INCREASE){
                if (taxpayer.getTaxInxrease() == 0) {
                    i++;    //overpass "Tax Increase: " go to "Tax Decrease: "
                    outputStream.println(tags.get(i)[0] + getTaxPayerInfo(taxpayer)[i] + tags.get(i)[1]);
                } else {
                    outputStream.println(tags.get(i)[0] + getTaxPayerInfo(taxpayer)[i] + tags.get(i)[1]);
                    i++;    //overpass "Tax Decrease: "
                }
                continue;
            }
            outputStream.println(tags.get(i)[0] + getTaxPayerInfo(taxpayer)[i] + tags.get(i)[1]);

        }
    }

    public String[] getTaxPayerInfo(Taxpayer taxpayer) {
        return new String[]{taxpayer.getName(), taxpayer.getAFM(), String.valueOf(taxpayer.getIncome()),
                String.valueOf(taxpayer.getBasicTax()), String.valueOf(taxpayer.getTaxInxrease()),
                String.valueOf(taxpayer.getTaxDecrease()), String.valueOf(taxpayer.getTotalTax()),
                String.valueOf(taxpayer.getTotalReceiptsAmount()),
                String.valueOf(taxpayer.getSpecificReceiptsTotalAmount("Entertainment")),
                String.valueOf(taxpayer.getSpecificReceiptsTotalAmount("Basic")),
                String.valueOf(taxpayer.getSpecificReceiptsTotalAmount("Travel")),
                String.valueOf(taxpayer.getSpecificReceiptsTotalAmount("Health")),
                String.valueOf(taxpayer.getSpecificReceiptsTotalAmount("Other"))
        };
    }

}
