public class Main {
    public static void main(String[] args) {
        model.AppData data = model.AppData.getInstance();
        System.out.println("Total kWh: " + data.getTotalKwh());
        System.out.println("Total Cost: " + data.getTotalCost());
    }
}
