import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PrinterTest {
    private String printerName;
    private PrintService printService;

    public PrinterTest(String printerName) {
        this.printerName = printerName;
        initialize();
    }

    public void initialize() {
        PrintServiceAttributeSet psas = new HashPrintServiceAttributeSet();
        psas.add(new PrinterName(printerName, null));
        printService = findPrintService(psas);
        if (printService == null) {
            System.out.println("Printer not found.");
        } else {
            System.out.println("Printer initialized successfully.");
        }
    }

    public void printCommand() throws IOException {
        if (printService == null) {
            System.out.println("Printer not initialized.");
            return;
        }

        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        DocPrintJob job = printService.createPrintJob();


        InvoiceECSPosBuilder invoiceBuilder = new InvoiceECSPosBuilder()
                .setTitle("點溡科技有限公司")
                .setInvoicePeriod("112年01-02月")
                .setInvoiceNumber("AA-60866758")
                .setDatetime("2023-06-22 11:45:14")
                .setRandomCode("3756")
                .setTotalAmount("5,000")
                .setSellerTaxId("22555003")
                .setBuyer("John Doe")
                .setBarcodeData("999999999999")
                .setQRCode1("113:06:25:XY3Z:1000:1050:00000000:12345678:1234")
                .setQRCode2("AB12345678:20240625:XY3Z:1000:50:1050:商品A:2:300:600:商品B:1:400:400");

        byte[] bytes = invoiceBuilder.build();


        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc doc = new SimpleDoc(new ByteArrayInputStream(bytes), flavor, null);

        try {
            job.print(doc, pras);
            System.out.println("Print job sent.");
        } catch (PrintException e) {
            System.out.println("Print job failed.");
            e.printStackTrace();
        }
    }

    public BufferedImage generateQRCodeImage(String text, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            hints.put(EncodeHintType.MARGIN, 0); // 设置边距为0，去除白边

            bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            return ImageIO.read(new ByteArrayInputStream(pngData));
        } catch (WriterException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static PrintService findPrintService(PrintServiceAttributeSet printServiceAttributeSet) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, printServiceAttributeSet);
        if (printServices.length > 0) {
            return printServices[0];
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        PrinterTest printer = new PrinterTest("XP-58");
        printer.printCommand();
    }
}
