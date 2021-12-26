package agh.ics.oop;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DataStatistics {
    private final ArrayList<Integer> animalDataCount = new ArrayList<>();
    private final ArrayList<Integer> bushesDataCount = new ArrayList<>();
    private final ArrayList<Double> energyDataValue = new ArrayList<>();
    private final ArrayList<Double> lifeSpanDataValue = new ArrayList<>();
    private final ArrayList<Double> averageChildrenDataCount = new ArrayList<>();

    private long sumOfAnimals = 0;
    private long sumOfBushes = 0;
    private double sumOfEnergy = 0;
    private double sumOfLifeSpan = 0;
    private double sumOfChildren = 0;

    boolean borders;
    public DataStatistics(boolean borders){
        this.borders = borders;
    }

    public void addStatistics(int animalCount, int bushCount, double energyValue, double lifeSpanValue, double childrenCount){
        this.animalDataCount.add(animalCount);
        this.bushesDataCount.add(bushCount);
        this.energyDataValue.add(energyValue);
        this.lifeSpanDataValue.add(lifeSpanValue);
        this.averageChildrenDataCount.add(childrenCount);

        this.sumOfAnimals += animalCount;
        this.sumOfBushes += bushCount;
        this.sumOfEnergy += energyValue;
        this.sumOfLifeSpan += lifeSpanValue;
        this.sumOfChildren += childrenCount;
    }

    public void writeToFile() throws IOException {
        FileWriter csvWriter;
        if (this.borders){
            csvWriter = new FileWriter("mapBorders.csv");
        } else {
            csvWriter = new FileWriter("mapBorderless.csv");
        }
        csvWriter.append("Generation");
        csvWriter.append(",");
        csvWriter.append("Animal count");
        csvWriter.append(",");
        csvWriter.append("Bush count");
        csvWriter.append(",");
        csvWriter.append("Average energy");
        csvWriter.append(",");
        csvWriter.append("Average life span for dead animals");
        csvWriter.append(",");
        csvWriter.append("Average number of children per animal");
        csvWriter.append("\n");
        for (int i = 0; i < this.animalDataCount.size(); i++){
            csvWriter.append(Long.toString(i+1));
            csvWriter.append(",");
            csvWriter.append(this.animalDataCount.get(i).toString());
            csvWriter.append(",");
            csvWriter.append(this.bushesDataCount.get(i).toString());
            csvWriter.append(",");
            csvWriter.append(this.energyDataValue.get(i).toString());
            csvWriter.append(",");
            csvWriter.append(this.lifeSpanDataValue.get(i).toString());
            csvWriter.append(",");
            csvWriter.append(this.averageChildrenDataCount.get(i).toString());
            csvWriter.append("\n");
        }

        int genNumber = this.animalDataCount.size();
        csvWriter.append("All");
        csvWriter.append(",");
        double animals = this.sumOfAnimals/(double) genNumber;
        String anString = animals + "";
        csvWriter.append(anString);
        csvWriter.append(",");
        double bushes = this.sumOfBushes/(double) genNumber;
        String bushString = bushes + "";
        csvWriter.append(bushString);
        csvWriter.append(",");
        double energy = this.sumOfEnergy/(double) genNumber;
        String enString = energy + "";
        csvWriter.append(enString);
        csvWriter.append(",");
        double lifeSpan = this.sumOfLifeSpan/(double) genNumber;
        String lifeString = lifeSpan + "";
        csvWriter.append(lifeString);
        csvWriter.append(",");
        double children = this.sumOfChildren/(double) genNumber;
        String childString = children + "";
        csvWriter.append(childString);
        csvWriter.append("\n");

        csvWriter.flush();
        csvWriter.close();
    }

    public int getAnimalData() { return this.animalDataCount.get(this.animalDataCount.size()-1); }

    public int getBushData() { return this.bushesDataCount.get(this.bushesDataCount.size()-1); }

    public double getEnergyData() { return this.energyDataValue.get(this.energyDataValue.size()-1); }

    public double getLifeSpanData() { return this.lifeSpanDataValue.get(this.lifeSpanDataValue.size()-1); }

    public double getChildrenData() { return this.averageChildrenDataCount.get(this.averageChildrenDataCount.size()-1); }


}
