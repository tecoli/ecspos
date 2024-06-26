import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class PrinterDiscovery {
    public static void main(String[] args) {
        // 獲取所有的印表機服務
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        System.out.println("Found " + printServices.length + " print services");

        // 遍歷每一個印表機服務
        for (PrintService printService : printServices) {
            System.out.println("Printer Name: " + printService.getName());

            System.out.println();
        }
    }
}