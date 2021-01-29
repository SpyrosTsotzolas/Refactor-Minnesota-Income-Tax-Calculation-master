package src.inputManagePackage;
import src.dataManagePackage.Database;
import src.dataManagePackage.FamilyStatus;
import src.dataManagePackage.Receipt;
import src.dataManagePackage.Taxpayer;
import src.managerTags.ParsingTags;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static src.constants.ApplicationConstants.*;

public class InputSystem {

	private Database database = Database.getInstance();
	private ArrayList<ArrayList<String[]>> tags;
	private static InputSystem firstInstance = null;

	private InputSystem(){ }

	public static InputSystem getInstance() {
		if(firstInstance == null) {
			firstInstance = new InputSystem();
		}
		return firstInstance;
	}

	public void addTaxpayersDataFromFilesIntoDatabase(String afmInfoFilesFolderPath, List<String> taxpayersAfmInfoFiles) throws IOException {
		for (String afmInfoFile : taxpayersAfmInfoFiles)
		{
			ParsingTags parsingTags = new ParsingTags();
			if (afmInfoFile.endsWith(INPUT_FILE_FORMAT_TXT)){
				tags = parsingTags.getTagsForUpdatedFile("TagsInputFileTXT");
			}
			else if (afmInfoFile.endsWith(INPUT_FILE_FORMAT_XML)){
				tags = parsingTags.getTagsForUpdatedFile("TagsInputFileXML");
			}
			loadTaxpayerDataFromFileIntoDatabase(afmInfoFilesFolderPath, afmInfoFile);
		}
	}

	public void loadTaxpayerDataFromFileIntoDatabase(String afmInfoFileFolderPath, String afmInfoFile) {

		Scanner inputStream = openFile(afmInfoFileFolderPath, afmInfoFile);
		Taxpayer newTaxpayer = initializeTaxpayer(inputStream);
		String fileLine;

		while (inputStream.hasNextLine()) {
			fileLine = inputStream.nextLine();
			if(fileLine.isBlank() || fileLine.equals("Receipts:") || fileLine.equals("<Receipts>"))	continue;
			if(fileLine.equals("</Receipts>")) break;
			Receipt newReceipt = initializeReceipt( inputStream, fileLine);
			newTaxpayer.addReceiptToList(newReceipt);
		}
		database.addTaxpayerToList(newTaxpayer);
	}

	private Scanner openFile(String afmInfoFileFolderPath, String afmInfoFile) {

		Scanner inputStream = null;
		try
		{
			inputStream = new Scanner(new FileInputStream(afmInfoFileFolderPath+"/"+afmInfoFile));
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Problem opening " + afmInfoFile + " file.");
			System.exit(0);
		}
		return inputStream;
	}

	private Taxpayer initializeTaxpayer(Scanner inputStream) {

		ArrayList<String> fileLines = getLinesFromFile(inputStream, END_OF_TAXPAYER_INFO, TAXPAYER, null);
		ArrayList<String> newTaxpayerInfo = getDataFromFile(fileLines, TAXPAYER);
		ArrayList<ArrayList<Double>> valuesOfStatusList = new ArrayList<>();

		try {
			valuesOfStatusList = getValuesOfStatus(newTaxpayerInfo.get(STATUS));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Taxpayer newTaxpayer = new Taxpayer(newTaxpayerInfo.get(TAXPAYER_NAME), newTaxpayerInfo.get(AFM),
				FamilyStatus.initializeFamilyInfo(newTaxpayerInfo.get(STATUS), valuesOfStatusList) , newTaxpayerInfo.get(INCOME));
		return newTaxpayer;

	}

	private Receipt initializeReceipt(Scanner inputStream, String fileLine) {

		ArrayList<String> fileLines = getLinesFromFile(inputStream, END_OF_RECEIPT_INFO, RECEIPT, fileLine);
		ArrayList<String> newReceiptInfo = getDataFromFile(fileLines,RECEIPT);
		Receipt newReceipt = new Receipt(newReceiptInfo.get(KIND), newReceiptInfo.get(ID), newReceiptInfo.get(DATE) ,
				newReceiptInfo.get(AMOUNT), newReceiptInfo.get(RECEIPT_NAME),newReceiptInfo.get(COUNTRY) ,
				newReceiptInfo.get(CITY), newReceiptInfo.get(STREET), newReceiptInfo.get(NUMBER));
		return newReceipt;

	}

	private ArrayList<String> getLinesFromFile(Scanner inputStream, int numOfInfo, String type, String fileLine) {

		ArrayList<String> infoArrayList = new ArrayList<String>();
		int i = 0;
		while (i < numOfInfo){
			if(type.equals(TAXPAYER)){
				infoArrayList.add(inputStream.nextLine());
			}
			else if(type.equals(RECEIPT)){
				if(i == 0)
					infoArrayList.add(fileLine);
				else
					infoArrayList.add(inputStream.nextLine());
			}
			i++;
		}
		return infoArrayList;
	}

	private ArrayList<String> getDataFromFile(ArrayList<String> fileLines, String type){

		ArrayList<String> allInfo = new ArrayList<>();
		if(type.equals(TAXPAYER)){
			for(int i = 0; i < fileLines.size(); i++){
				allInfo.add(getParameterValueFromFileLine(fileLines.get(i), tags.get(0).get(i)[0], tags.get(0).get(i)[1]));
			}
		}
		else if(type.equals(RECEIPT)){
			for(int i = 0; i < fileLines.size(); i++){
				allInfo.add(getParameterValueFromFileLine(fileLines.get(i), tags.get(1).get(i)[0], tags.get(1).get(i)[1]));
			}
		}
		return allInfo;
	}

	private ArrayList<ArrayList<Double>> getValuesOfStatus(String familyStatus) throws IOException {

		FileReader input = null;
		input = new FileReader("files/valuesForCalcTax");

		BufferedReader bufRead = new BufferedReader(input);
		String myLine = null;
		int lineCounter = 0;
		boolean flagStatus = false; // found right status

		ArrayList<ArrayList<Double>> valuesOfStatusList = new ArrayList<ArrayList<Double>>();

		while (lineCounter < 3){
			myLine = bufRead.readLine();

			if(myLine.equals(familyStatus.toLowerCase())){
				flagStatus = true;
				myLine = bufRead.readLine();
			}

			if(flagStatus){
				String[] values = myLine.split(" ");
				ArrayList<Double> doubleValues = new ArrayList<Double>();

				for(int i =0; i<values.length; i++)
					doubleValues.add(Double.parseDouble(values[i]));

				valuesOfStatusList.add(doubleValues);
				lineCounter++;
			}

		}

		return valuesOfStatusList;
	}

	private String getParameterValueFromFileLine(String fileLine, String parameterStartField, String parameterEndField){
		return fileLine.substring(parameterStartField.length(), fileLine.length()-parameterEndField.length());
	}
}