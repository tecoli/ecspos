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

        ECSPOSPrinter ECSPOSPrinter = new ECSPOSPrinter();
     //   xPrinter.sendCommand(new byte[] {0x1F, 0x1B, 0x1F, 0x46, 0x4F, 0x4E, 0x54, 0x01});
        ECSPOSPrinter.initializeForTraditionalChinese();

        ECSPOSPrinter.setFontSize(2, 2);
        ECSPOSPrinter.setAlignment(Alignment.CENTER);
        ECSPOSPrinter.setBold(true);
        ECSPOSPrinter.printLine("點溡科技有限公司");
        ECSPOSPrinter.setBold(false);
        ECSPOSPrinter.feedPaper(225);


        ECSPOSPrinter.printLine("電子發票證明聯");

        ECSPOSPrinter.printLine("112年01-02月");
        ECSPOSPrinter.printLine("AA-60866758");
        ECSPOSPrinter.feedPaper(20);
        ECSPOSPrinter.setFontSize(1, 1);
        ECSPOSPrinter.setAlignment(Alignment.LEFT);
        ECSPOSPrinter.printLine("2023-06-22 11:45:14");
       // xPrinter.feedPaper(10);
        ECSPOSPrinter.setAlignment(Alignment.LEFT);

        ECSPOSPrinter.printText("隨機碼：3756");
        ECSPOSPrinter.printText("     ");
        ECSPOSPrinter.printText("總計：5,000");
        ECSPOSPrinter.printLine();
      //  xPrinter.feedPaper(10);
        ECSPOSPrinter.printText("賣方：22555003");
        ECSPOSPrinter.printText("   ");
        ECSPOSPrinter.printText("買方：22555003");
        ECSPOSPrinter.printLine();
       // xPrinter.feedPaper(1);
        ECSPOSPrinter.setAlignment(Alignment.CENTER);
        ECSPOSPrinter.printBarcode("999999999999", BarCodeType.CODE39, 700, 60, 0);

        int qrcodeSize = 180;

        BufferedImage bufferedImage1 = generateQRCodeImage("113:06:25:XY3Z:1000:1050:00000000:12345678:1234",
                qrcodeSize,
                qrcodeSize);
        BufferedImage bufferedImage2 = generateQRCodeImage("AB12345678:20240625:XY3Z:1000:50:1050:商品A:2:300:600:商品B:1:400:400",
                qrcodeSize,
                qrcodeSize);

        int margin = 30;
        BufferedImage qrcodeImages = new BufferedImage(
                qrcodeSize * 2 + margin,
                qrcodeSize,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = (Graphics2D)qrcodeImages.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, qrcodeImages.getWidth(), qrcodeImages.getHeight());

        graphics.drawImage(bufferedImage1, 0, 0, null);
        graphics.drawImage(bufferedImage2, qrcodeSize + margin, 0, null);

        ECSPOSPrinter.printLine();
        ECSPOSPrinter.setAlignment(Alignment.LEFT);
        ECSPOSPrinter.printImage(qrcodeImages);
        ImageIO.write(qrcodeImages, "jpg", new FileOutputStream("./test.jpg"));
        ECSPOSPrinter.printLine();
        ECSPOSPrinter.cutPaper();
        byte[] bytes = ECSPOSPrinter.toBytes();

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
        BitMatrix bitMatrix = null;
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            hints.put(EncodeHintType.MARGIN, 0); // 设置边距为0，去除白边

            bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            return ImageIO.read(new ByteArrayInputStream(pngData));
        } catch (WriterException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    public void printText(String text) {
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);

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