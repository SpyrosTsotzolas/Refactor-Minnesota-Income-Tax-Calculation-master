package src.outputManagePackage;

import src.dataManagePackage.Database;
import src.dataManagePackage.Receipt;
import src.managerTags.ParsingTags;

import static src.constants.ApplicationConstants.*;

import java.io.*;
import java.util.ArrayList;

public class UpdateInputFile {

    private final Database database = Database.getInstance();
    private final String typeOfFile;
    private ArrayList<ArrayList<String[]>> infoFromInputTemplateFile;

    public UpdateInputFile(String typeOfFile){
        this.typeOfFile = typeOfFile;

        try {
            this.infoFromInputTemplateFile = getInfoFromTemplateFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ArrayList<String[]>> getInfoFromTemplateFile() throws IOException {

        ParsingTags parsingTags = new ParsingTags();

        ArrayList<ArrayList<String[]>> infoOfFile = new ArrayList<>();
        ArrayList<String[]> taxpayerInfo = new ArrayList<>();
        ArrayList<String[]> receiptsInfo = new ArrayList<>();
        if(typeOfFile.equals(INPUT_FILE_TYPE_TXT)){
//            infoOfFile = parsingTags.getTagsForUpdatedFile("TagsInputFileTXT");
            initialiseTemplateFileValuesTXT(taxpayerInfo, receiptsInfo);
        }
        else if(typeOfFile.equals(INPUT_FILE_TYPE_XML)){
//            infoOfFile = parsingTags.getTagsForUpdatedFile("TagsInputFileXML");
            initialiseTemplateFileValuesXML(taxpayerInfo, receiptsInfo);
        }

        infoOfFile.add(taxpayerInfo);
        infoOfFile.add(receiptsInfo);

        return infoOfFile;

    }

    private void initialiseTemplateFileValuesTXT(ArrayList<String[]> taxpayerInfo, ArrayList<String[]> receiptsInfo) throws IOException {
        BufferedReader bufRead = getBufferedReader("files/filesFormat/TagsInputFileTXT");
        String myLine;
        boolean flagReceipts = false;
        while((myLine = bufRead.readLine())!=null){
            if(myLine.equals("Receipts:")){ receiptsInfo.add(new String[]{myLine, ""}); flagReceipts=true; continue;}

            if(flagReceipts) receiptsInfo.add(new String[]{myLine + " ", ""});

            else taxpayerInfo.add(new String[]{myLine + " ", ""});
        }
    }

    private void initialiseTemplateFileValuesXML(ArrayList<String[]> taxpayerInfo, ArrayList<String[]> receiptsInfo) throws IOException {
        BufferedReader bufRead = getBufferedReader("files/filesFormat/TagsInputFileXML");
        String myLine;
        boolean flagReceipts = false;
        while((myLine = bufRead.readLine())!=null){
            if(myLine.equals("<Receipts>")){receiptsInfo.add(new String[]{myLine, ""}); flagReceipts=true; continue;}

            if(flagReceipts){
                String[] receiptsArray = new String[2];
                receiptsArray[0] = myLine.split("\\s+")[0] + " ";
                receiptsArray[1] = " " + myLine.split("\\s+")[1];

                receiptsInfo.add(receiptsArray);
            }
            else{
                String[] taxPayerArray = new String[2];
                taxPayerArray[0] = myLine.split("\\s+")[0]+ " ";
                taxPayerArray[1] = " " + myLine.split("\\s+")[1];
                taxpayerInfo.add(taxPayerArray);
            }
        }
        receiptsInfo.add(new String[] {"</Receipts>",""});
    }

    private BufferedReader getBufferedReader(String path) throws FileNotFoundException {
        FileReader input = null;
        input = new FileReader(path);
        BufferedReader bufRead = new BufferedReader(input);
        return bufRead;
    }

    public void saveUpdatedTaxpayerInputFile(String filePath, int taxpayerIndex){

        PrintWriter outputWriter = getPrintWriter(filePath);
        for(int i = 0; i < getTaxPayerInfo(taxpayerIndex).length; i++){
            outputWriter.println(infoFromInputTemplateFile.get(0).get(i)[0] + getTaxPayerInfo(taxpayerIndex)[i]
                    + infoFromInputTemplateFile.get(0).get(i)[1]);
        }

        if (database.getTaxpayerFromArrayList(taxpayerIndex).getReceiptsArrayList().size() > 0){

            outputWriter.println();
//            if(filePath.endsWith(INPUT_FILE_FORMAT_TXT)) {
//
//                outputWriter.println("Receipts:");
//
//            }else {
//                outputWriter.println("<Receipts>");
//            }
            outputWriter.println(infoFromInputTemplateFile.get(1).get(0)[0]);
            outputWriter.println();

            for (int i = 0; i < getReceipts(taxpayerIndex).size(); i++){
                if(i>0)
                    outputWriter.println();
                for (int j =1; j< END_INFO_RECEIPTS; j++) {
                    outputWriter.println(infoFromInputTemplateFile.get(1).get(j)[0] + getReceipts(taxpayerIndex).get(i).get(j-1)
                            + infoFromInputTemplateFile.get(1).get(j)[1]);
                }

            }
//            if(filePath.endsWith(INPUT_FILE_FORMAT_XML)){
//                outputWriter.println();
//                outputWriter.println("</Receipts>");
//            }
            if(infoFromInputTemplateFile.get(1).size() == LAST_LINE_OF_XML_FILE){
                outputWriter.println();
                outputWriter.println("</Receipts>");
            }
        }

        outputWriter.close();
    }

    public String[] getTaxPayerInfo(int taxpayerIndex) {
        return new String[]{database.getTaxpayerFromArrayList(taxpayerIndex).getName(),
                database.getTaxpayerFromArrayList(taxpayerIndex).getAFM(),
                database.getTaxpayerFromArrayList(taxpayerIndex).getFamilyStatus(),
                String.valueOf(database.getTaxpayerFromArrayList(taxpayerIndex).getIncome())};
    }

    private PrintWriter getPrintWriter(String filePath) {
        PrintWriter outputStream = null;
        try
        {
            outputStream = new PrintWriter(new FileOutputStream(filePath));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("Problem opening: "+filePath);
        }
        return outputStream;
    }

    public ArrayList<ArrayList<String>> getReceipts(int taxpayerIndex){

        ArrayList<Receipt> receipt = database.getTaxpayerFromArrayList(taxpayerIndex).getReceiptsArrayList();
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