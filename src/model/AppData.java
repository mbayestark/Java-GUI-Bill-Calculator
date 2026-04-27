package model;
import java.util.List;
import java.util.ArrayList;

public class AppData {
    private List<Appliance> appliances;
    private List<TariffTier> tariffs;
    private double flatRate;
    private String currency;
    private boolean useTieredPricing;

    public static AppData getInstance(){
        if (instance==null){
            instance=new AppData();
        }
        return instance;
    }

    public double getTotalKwh(){
        double totalKwh=0;
        for (Appliance appliance : appliances ){
            totalKwh+=appliance.getMonthlyKwh();
        }
        return totalKwh;
    }

    public double getTotalCost(){
        double total=0;
        if (!useTieredPricing){
            total= getTotalKwh()*flatRate;
        }

        return total;
    }
}
