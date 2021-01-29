package src.dataManagePackage;

import java.math.BigDecimal;
import java.util.ArrayList;
import static src.constants.ApplicationConstants.*;

public class Taxpayer {
	private String name;
	private String afm;
	private FamilyStatus familyStatus;
	private double income;
	private double basicTax;
	private double taxIncrease;
	private double taxDecrease;
	private double totalTax;
	private ArrayList<Receipt> receipts;

	public String getFamilyStatus(){
		if (FamilyStatus.SINGLE.equals(getFamilyStatusObject())) {
			return "Single";
		} else if (FamilyStatus.MARRIED_FILLING_JOINTLY.equals(getFamilyStatusObject())) {
			return "Married Filing Jointly";
		} else if (FamilyStatus.HEAD_OF_HOUSEHOLD.equals(getFamilyStatusObject())) {
			return "Head Of Household";
		} else if (FamilyStatus.MARRIED_FILLING_SEPARATELY.equals(getFamilyStatusObject())) {
			return "Married Filling Separately";
		}
		return null;
	}

	public FamilyStatus getFamilyStatusObject(){
		return familyStatus;
	}

	public Taxpayer(String name, String afm, FamilyStatus familyStatus, String income){

		this.name = name;
		this.afm = afm;
		this.familyStatus = familyStatus;
		this.income = Double.parseDouble(income);
		setBasicTaxBasedOnFamilyStatus();
		taxIncrease = 0;
		taxDecrease = 0;
		receipts = new ArrayList<Receipt>();
	}

	private void setBasicTaxBasedOnFamilyStatus(){

		ArrayList<ArrayList<Double>> taxpayerAmounts = new ArrayList<>();
		taxpayerAmounts.add(getFamilyStatusObject().getRates());
		taxpayerAmounts.add(getFamilyStatusObject().getValues());
		taxpayerAmounts.add(getFamilyStatusObject().getIncomes());
		basicTax = calculateTax(income, taxpayerAmounts);
		totalTax = basicTax;
	}

	public double calculateTax(double totalIncome,ArrayList<ArrayList<Double>> taxpayerAmounts){

		double tax = 0;
		if (totalIncome < taxpayerAmounts.get(INCOMES).get(0)){
			tax = taxpayerAmounts.get(VALUES).get(0) + (taxpayerAmounts.get(RATES).get(0) /100) * totalIncome;
		}
		else if (totalIncome < taxpayerAmounts.get(INCOMES).get(1)){
			tax = taxpayerAmounts.get(VALUES).get(1) + (taxpayerAmounts.get(RATES).get(1) /100 *
					(totalIncome- taxpayerAmounts.get(INCOMES).get(0)));
		}
		else if (totalIncome < taxpayerAmounts.get(INCOMES).get(2)){
			tax = taxpayerAmounts.get(VALUES).get(2) + (taxpayerAmounts.get(RATES).get(2) /100 *
					(totalIncome- taxpayerAmounts.get(INCOMES).get(1)));
		}
		else if (totalIncome < taxpayerAmounts.get(INCOMES).get(3)){
			tax = taxpayerAmounts.get(VALUES).get(3) + (taxpayerAmounts.get(RATES).get(3) /100 *
					(totalIncome- taxpayerAmounts.get(INCOMES).get(2)));
		}
		else{
			tax = taxpayerAmounts.get(VALUES).get(4) + (taxpayerAmounts.get(RATES).get(4) /100 *
					(totalIncome- taxpayerAmounts.get(INCOMES).get(3)));
		}
		return tax;
	}

	public String toString(){
		return "Name: "+name
				+"\nAFM: "+afm
				+"\nStatus: "+getFamilyStatus()
				+"\nIncome: "+String.format("%.2f", income)
				+"\nBasicTax: "+String.format("%.2f", basicTax)
				+"\nTaxIncrease: "+String.format("%.2f", taxIncrease)
				+"\nTaxDecrease: "+String.format("%.2f", taxDecrease);
	}

	public Receipt getReceipt(int receiptID){
		return receipts.get(receiptID);
	}

	public ArrayList<Receipt> getReceiptsArrayList(){
		return receipts;
	}

	public String[] getReceiptsList(){
		String[] receiptsList = new String[receipts.size()];

		int c = 0;
		for (Receipt receipt : receipts){
			receiptsList[c++] = receipt.getId() + " | " + receipt.getDate() + " | " + receipt.getAmount();
		}

		return receiptsList;
	}

	public double getSpecificReceiptsTotalAmount(String typeOfReceipt){

		double totalAmount = 0;
		for (Receipt receipt : receipts){
			if (receipt.getKind().equals(typeOfReceipt)){
				totalAmount += receipt.getAmount();
			}
		}
		return (new BigDecimal(totalAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

	}

	public double getTotalReceiptsAmount(){

		double totalReceiptsAmount = 0;
		for (Receipt receipt : receipts){
			totalReceiptsAmount += receipt.getAmount();
		}
		return (new BigDecimal(totalReceiptsAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}

	public String getName(){
		return name;
	}

	public String getAFM(){
		return afm;
	}

	public double getIncome(){
		return (new BigDecimal(income).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}

	public double getBasicTax(){
		return (new BigDecimal(basicTax).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}

	public double getTaxInxrease(){
		return (new BigDecimal(taxIncrease).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}

	public double getTaxDecrease(){
		return (new BigDecimal(taxDecrease).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}

	public double getTotalTax(){
		return (new BigDecimal(totalTax).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}

	public void addReceiptToList(Receipt receipt){
		receipts.add(receipt);
		calculateTaxpayerTaxIncreaseOrDecreaseBasedOnReceipts();
	}

	public void removeReceiptFromList(int index){
		receipts.remove(index);
		calculateTaxpayerTaxIncreaseOrDecreaseBasedOnReceipts();
	}

	public void calculateTaxpayerTaxIncreaseOrDecreaseBasedOnReceipts(){
		double totalReceiptsAmount = 0;
		for (Receipt receipt : receipts){
			totalReceiptsAmount += receipt.getAmount();
		}
		taxIncrease = 0;
		taxDecrease = 0;
		if ((totalReceiptsAmount/ income) < 0.2){
			taxIncrease = basicTax * 0.08;
		}
		else if ((totalReceiptsAmount/ income) < 0.4){
			taxIncrease = basicTax * 0.04;
		}
		else if ((totalReceiptsAmount/ income) < 0.6){
			taxDecrease = basicTax * 0.15;
		}
		else{
			taxDecrease = basicTax * 0.30;
		}
		totalTax = basicTax + taxIncrease - taxDecrease;
	}

}
