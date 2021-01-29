package src.dataManagePackage;

import src.inputManagePackage.InputSystem;
import src.outputManagePackage.UpdateInputFile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static src.constants.ApplicationConstants.*;


public class Database {
	private String taxpayersInfoFilesPath;
	private ArrayList<Taxpayer> taxpayersArrayList = new ArrayList<Taxpayer>();

	private static Database firstInstance = null;

	private Database(){ }

	public static Database getInstance() {
		if(firstInstance == null) {
			firstInstance = new Database();
		}
		return firstInstance;
	}

	public void setTaxpayersInfoFilesPath(String taxpayersInfoFilesPath){
		this.taxpayersInfoFilesPath = taxpayersInfoFilesPath;
	}
	
	public String getTaxpayersInfoFilesPath(){
		return this.taxpayersInfoFilesPath;
	}
	
	public void proccessTaxpayersDataFromFilesIntoDatabase(String afmInfoFilesFolderPath, List<String> taxpayersAfmInfoFiles) {
		InputSystem inputSystem = InputSystem.getInstance();
		try {
			inputSystem.addTaxpayersDataFromFilesIntoDatabase(afmInfoFilesFolderPath, taxpayersAfmInfoFiles);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addTaxpayerToList(Taxpayer taxpayer){
		taxpayersArrayList.add(taxpayer);
	}
	
	public int getTaxpayersArrayListSize(){
		return taxpayersArrayList.size();
	}
	
	public Taxpayer getTaxpayerFromArrayList(int index){
		return taxpayersArrayList.get(index);
	}
	
	public void removeTaxpayerFromArrayList(int index){
		taxpayersArrayList.remove(index);
	}
	
	public String getTaxpayerNameAfmValuesPairList(int index){
		Taxpayer taxpayer = taxpayersArrayList.get(index);
		return taxpayer.getName() + " | " + taxpayer.getAFM();
	}
	
	public String[] getTaxpayersNameAfmValuesPairList(){
		String[] taxpayersNameAfmValuesPairList = new String[taxpayersArrayList.size()];
		
		int c = 0;
		for (Taxpayer taxpayer : taxpayersArrayList){
			taxpayersNameAfmValuesPairList[c++] = taxpayer.getName() + " | " + taxpayer.getAFM();
		}
		
		return taxpayersNameAfmValuesPairList;
	}
	
	public void updateTaxpayerInputFile(int index){

		File taxpayersInfoFilesPathFileObject = new File(taxpayersInfoFilesPath);
		FilenameFilter fileNameFilter = new FilenameFilter(){
            public boolean accept(File dir, String name) {
               return (name.toLowerCase().endsWith("_info.txt") || name.toLowerCase().endsWith("_info.xml"));
            }
         };
		for (File file : taxpayersInfoFilesPathFileObject.listFiles(fileNameFilter)){
			if (!file.getName().contains(taxpayersArrayList.get(index).getAFM())) continue;
			
			if (file.getName().toLowerCase().endsWith(INPUT_FILE_FORMAT_TXT)){
				UpdateInputFile updateInputFile = new UpdateInputFile(INPUT_FILE_TYPE_TXT);
				updateInputFile.saveUpdatedTaxpayerInputFile(file.getAbsolutePath(), index);
			}
			if (file.getName().toLowerCase().endsWith(INPUT_FILE_FORMAT_XML)){
				UpdateInputFile updateInputFile = new UpdateInputFile(INPUT_FILE_TYPE_XML);
				updateInputFile.saveUpdatedTaxpayerInputFile(file.getAbsolutePath(), index);
			}
			break;
		}
	}

}