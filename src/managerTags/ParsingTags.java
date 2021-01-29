package src.managerTags;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ParsingTags {

    public ArrayList<ArrayList<String[]>> getTagsForUpdatedFile(String fileName) {

        ArrayList<ArrayList<String[]>> infoOfFile = new ArrayList<>();
        ArrayList<String[]> taxpayerInfo = new ArrayList<>();
        ArrayList<String[]> receiptsInfo = new ArrayList<>();
        BufferedReader bufRead = getBufferedReader(fileName);
        String myLine = null;
        boolean flagReceipts = false; //first line of receipts info

        if(fileName.equals("TagsInputFileTXT")){
            while(true){
                try {
                    if (!((myLine = bufRead.readLine())!=null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(myLine.equals("Receipts:")){flagReceipts=true; continue;}

                if(flagReceipts) getTxtTags(receiptsInfo, myLine);

                else {
                    getTxtTags(taxpayerInfo, myLine);
                }
            }
        }
        else{
            while(true){
                try {
                    if (!((myLine = bufRead.readLine())!=null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(myLine.equals("<Receipts>")){flagReceipts=true; continue;}

                if(flagReceipts) {
                    String[] receiptsArray = getXmlTags(myLine);
                    receiptsInfo.add(receiptsArray);
                }
                else {
                    String[] taxpayerArray = getXmlTags(myLine);
                    taxpayerInfo.add(taxpayerArray);
                }
            }
        }
        infoOfFile.add(taxpayerInfo);
        infoOfFile.add(receiptsInfo);
        return infoOfFile;
    }

    public  ArrayList<String[]> getTagsForLogFile(String fileName) {

        ArrayList<String[]> taxpayerInfo = new ArrayList<>();
        BufferedReader bufRead = getBufferedReader(fileName);
        String myLine = null;

        if(fileName.equals("TagsLogFileTXT")){
            while(true){
                try {
                    if (!((myLine = bufRead.readLine())!=null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                getTxtTags(taxpayerInfo, myLine);
            }
        }
        else if(fileName.equals("TagsLogFileXML")){
            while(true){
                try {
                    if (!((myLine = bufRead.readLine())!=null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] taxPayerArray = getXmlTags(myLine);
                taxpayerInfo.add(taxPayerArray);
            }
        }
        return taxpayerInfo;
    }

    private BufferedReader getBufferedReader(String fileName) {
        FileReader input = null;
        try {
            input = new FileReader("files/filesFormat/" + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new BufferedReader(input);
    }

    private void getTxtTags(ArrayList<String[]> txtTags, String myLine) {
        txtTags.add(new String[]{myLine + " ", ""});
    }

    private String[] getXmlTags(String myLine) {
        String[] xmlTags = new String[2];
        xmlTags[0] = myLine.split("\\s+")[0] + " ";
        xmlTags[1] = " " + myLine.split("\\s+")[1];
        return xmlTags;
    }

}