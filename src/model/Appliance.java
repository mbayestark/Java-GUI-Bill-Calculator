package model;

public class Appliance {
     private String name;
     private String category;
     private double watts;
     private double hoursPerDay;
     private double daysPerMonth;
     private int quantity;


     public double getMonthlyKwh(){
         return daysPerMonth*hoursPerDay*watts;
     }

     public double getMonthlyCost(double rate){
         return getMonthlyKwh()*quantity;
     }
}
