import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class InvoiceECSPosBuilder {
    private String title;

    private String invoicePeriod;
    private String invoiceNumber;
    private String datetime;
    private String randomCode;
    private String totalAmount;
    private String sellerTaxId;
    private String buyer;
    private String barcodeData;
    private String qrcode1;
    private String qrcode2;

    public InvoiceECSPosBuilder setTitle(String title) {
        this.title = title;
        return this;
    }



    public InvoiceECSPosBuilder setInvoicePeriod(String invoicePeriod) {
        this.invoicePeriod = invoicePeriod;
        return this;
    }

    public InvoiceECSPosBuilder setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        return this;
    }

    public InvoiceECSPosBuilder setDatetime(String datetime) {
        this.datetime = datetime;
        return this;
    }

    public InvoiceECSPosBuilder setRandomCode(String randomCode) {
        this.randomCode = randomCode;
        return this;
    }

    public InvoiceECSPosBuilder setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public InvoiceECSPosBuilder setSellerTaxId(String sellerTaxId) {
        this.sellerTaxId = sellerTaxId;
        return this;
    }

    public InvoiceECSPosBuilder setBuyer(String buyer) {
        this.buyer = buyer;
        return this;
    }

    public InvoiceECSPosBuilder setBarcodeData(String barcodeData) {
        this.barcodeData = barcodeData;
        return this;
    }

    public InvoiceECSPosBuilder setQRCode1(String qrcode1) {
        this.qrcode1 = qrcode1;
        return this;
    }

    public InvoiceECSPosBuilder setQRCode2(String qrcode2) {
        this.qrcode2 = qrcode2;
        return this;
    }

    public byte[] build() {
        ECSPosBuilder builder = new ECSPosBuilder();

        int qrcodeSize = 180;
        BufferedImage bufferedImage1 = generateQRCodeImage(qrcode1, qrcodeSize, qrcodeSize);
        BufferedImage bufferedImage2 = generateQRCodeImage(qrcode2, qrcodeSize, qrcodeSize);

        int margin = 30;
        BufferedImage qrcodeImages = new BufferedImage(qrcodeSize * 2 + margin, qrcodeSize, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = (Graphics2D) qrcodeImages.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, qrcodeImages.getWidth(), qrcodeImages.getHeight());

        graphics.drawImage(bufferedImage1, 0, 0, null);
        graphics.drawImage(bufferedImage2, qrcodeSize + margin, 0, null);

        builder
                .setCharset(StandardCharsets.UTF_8)

                .setAlignment(Alignment.CENTER)
                .setBold(true)
                .setFontSize(0x22)
                .printLine(title)
                .setBold(false)
                .feedPaper(225)
                .setFontSize(0x11)
                .printLine("電子發票證明聯")
                .printLine(invoicePeriod)
                .printLine(invoiceNumber)
                .feedPaper(20)
                .setFontSize(0)
                .setAlignment(Alignment.LEFT)
                .printLine(datetime)
                .setAlignment(Alignment.LEFT)
                .printText("隨機碼：" + randomCode)
                .printText("     ")
                .printText("總計：" + totalAmount)
                .printLine()
                .printText("賣方：" + sellerTaxId)
                .printText("   ")
                .printText("買方：" + buyer)
                .printLine()
                .setAlignment(Alignment.CENTER)
                .printBarcode(barcodeData, BarCodeType.CODE39, 700, 60, 0)
                .printLine()
                .setAlignment(Alignment.LEFT)
                .printImage(qrcodeImages)
                .printLine()
                .cutPaper();

        return builder.build();
    }

    private BufferedImage generateQRCodeImage(String text, int width, int height) {
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
}