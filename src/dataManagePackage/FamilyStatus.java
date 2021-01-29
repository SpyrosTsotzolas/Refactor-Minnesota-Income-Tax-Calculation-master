package src.dataManagePackage;

import java.util.ArrayList;

public class FamilyStatus {

    private static ArrayList<Double> rates;
    private static ArrayList<Double> incomes;
    private static ArrayList<Double> values;

    public static void setRates(ArrayList<Double> rates) {
        FamilyStatus.rates = rates;
    }

    public static void setIncomes(ArrayList<Double> incomes) {
        FamilyStatus.incomes = incomes;
    }

    public static void setValues(ArrayList<Double> values) {
        FamilyStatus.values = values;
    }

    public static void setValuesOfStatusList(ArrayList<ArrayList<Double>> valuesOfStatusList) {
        FamilyStatus.valuesOfStatusList = valuesOfStatusList;
    }

    public static ArrayList<ArrayList<Double>> valuesOfStatusList;
    public static final FamilyStatus SINGLE = new FamilyStatus();
    public static final FamilyStatus HEAD_OF_HOUSEHOLD = new FamilyStatus();
    public static final FamilyStatus MARRIED_FILLING_JOINTLY = new FamilyStatus();
    public static final FamilyStatus MARRIED_FILLING_SEPARATELY = new FamilyStatus();

    public ArrayList<Double> getRates() {return rates;}
    public ArrayList<Double> getIncomes() {return incomes;}
    public ArrayList<Double> getValues() {return values;}

    public FamilyStatus(){ }

    public static FamilyStatus initializeFamilyInfo(String familyStatus, ArrayList<ArrayList<Double>> valuesOfStatusList ){

        switch (familyStatus.toLowerCase()) {
            case "single":
                setValuesOfStatusList(valuesOfStatusList);
                setRates(valuesOfStatusList.get(0));
                setIncomes(valuesOfStatusList.get(1));
                setValues(valuesOfStatusList.get(2));
                return SINGLE;
            case "head of household":
                setValuesOfStatusList(valuesOfStatusList);
                setRates(valuesOfStatusList.get(0));
                setIncomes(valuesOfStatusList.get(1));
                setValues(valuesOfStatusList.get(2));
                return HEAD_OF_HOUSEHOLD;
            case "married filing separately":
                setValuesOfStatusList(valuesOfStatusList);
                setRates(valuesOfStatusList.get(0));
                setIncomes(valuesOfStatusList.get(1));
                setValues(valuesOfStatusList.get(2));
                return MARRIED_FILLING_SEPARATELY;
            case "married filing jointly":
                setValuesOfStatusList(valuesOfStatusList);
                setRates(valuesOfStatusList.get(0));
                setIncomes(valuesOfStatusList.get(1));
                setValues(valuesOfStatusList.get(2));
                return MARRIED_FILLING_JOINTLY;
        }

        return null;

    }
}
