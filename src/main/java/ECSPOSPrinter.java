import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ECSPOSPrinter {

    private ByteArrayOutputStream commandStream;

    // 打印機狀態
    private boolean paperStatus = true;
    private boolean drawerStatus = false;

    // 打印模式
    private boolean boldMode = false;
    private boolean underlineMode = false;
    private boolean doubleHeightMode = false;
    private boolean doubleWidthMode = false;
    private boolean inverseMode = false;

    // 字符集
    private int characterSet = 0;

    // 行間距
    private int lineSpacing = 30; // 默認值約3.75mm

    // 對齊方式
    private int alignment = 0; // 0:左對齊, 1:居中, 2:右對齊

    public ECSPOSPrinter() {
        commandStream = new ByteArrayOutputStream();
    }

    private Charset charset = StandardCharsets.UTF_8; // 默認使用UTF-8

    // 初始化打印機
    public void initialize() {
        sendCommand(new byte[]{0x1B, 0x40});
        resetPrintModes();
    }

    public void initializeForTraditionalChinese() {
        initialize();
        setCharset(Charset.forName("Big5"));
        //setTraditionalChineseMode();
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    // 打印並換行
    public void printLine(String text) {
        printText(text);
        printLine();
    }

    public void setTraditionalChineseMode() {
        // 選擇中文字符集
        sendCommand(new byte[]{0x1C, 0x2E}); // FS &
        // 選擇雙字節編碼
        sendCommand(new byte[]{0x1C, 0x43, 0x1}); // FS C 1

    }

    // 打印文本
    public void printText(String text) {

        sendCommand(text.getBytes(charset));

    }

    // 換行
    public void printLine() {
        sendCommand(new byte[]{0x0A});
    }

    // 切紙
    public void cutPaper() {
        sendCommand(new byte[]{0x1D, 0x56, 0x41, 0x00});
    }

    // 設置字體大小
    public void setFontSize(int width, int height) {
        int size = (width - 1) | ((height - 1) << 4);
        sendCommand(new byte[]{0x1D, 0x21, (byte) size});
    }

    // 設置對齊方式
    public void setAlignment(Alignment alignment) {

        this.alignment = alignment.ordinal();
        sendCommand(new byte[]{0x1B, 0x61, (byte) alignment.ordinal()});

    }

    // 設置加粗
    public void setBold(boolean bold) {
        this.boldMode = bold;
        sendCommand(new byte[]{0x1B, 0x45, (byte) (bold ? 1 : 0)});
    }

    // 設置下劃線
    public void setUnderline(int weight) {
        this.underlineMode = (weight > 0);
        sendCommand(new byte[]{0x1B, 0x2D, (byte) weight});
    }

    // 設置行間距
    public void setLineSpacing(int spacing) {
        this.lineSpacing = spacing;
        sendCommand(new byte[]{0x1B, 0x33, (byte) spacing});
    }

    // 設置字符集
    public void setCharacterSet(int set) {
        if (set >= 0 && set <= 15) {
            this.characterSet = set;
            sendCommand(new byte[]{0x1B, 0x52, (byte) set});
        }
    }

    // 打印條碼
    public void printBarcode(String content, BarCodeType type, int width, int height, int hriPosition) {
        // 設置條碼高度
        sendCommand(new byte[]{0x1D, 0x68, (byte) height});
        // 設置條碼寬度
        sendCommand(new byte[]{0x1D, 0x77, (byte) width});
        // 設置HRI字符的打印位置
        //    sendCommand(new byte[]{0x1D, 0x48, (byte) hriPosition});
        // 打印條碼
        sendCommand(new byte[]{0x1D, 0x6B, (byte) type.ordinal()});
        sendCommand(content.getBytes(charset));
        sendCommand(new byte[]{0});
    }

    public void setLineHeight(int height) {
        sendCommand(new byte[]{0x1B, 0x33, (byte) height});
    }

    public void feedPaper(int dots) {
        sendCommand(new byte[]{0x1B, 0x4A, (byte) dots});
    }

    public void moveTo(int position) {
        sendCommand(new byte[]{0x1B, 0x24, (byte)(position & 0xFF), (byte)((position >> 8) & 0xFF)});
    }
    public void setAbsolutePosition(int position) {
        sendCommand(new byte[]{0x1B, 0x24, (byte)(position % 256), (byte)(position / 256)});
    }

    public void printImage(BufferedImage image) {
        try {


            int width = (image.getWidth()+7) / 8;
            int height = image.getHeight();


            ByteArrayOutputStream buf = new ByteArrayOutputStream();

            buf.write(new byte[]{0x1D, 0x76, 0x30});
            buf.write(0);
            buf.write(width & 0xFF);
            buf.write(width >> 8 & 0xFF);
            buf.write(height & 0xFF);
            buf.write(height >> 8 & 0xFF);

            byte[] buffer = new byte[width*height];


            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int rgb = image.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    if (red + green + blue == 0) {
                        int gx = x / 8;
                        int dx = x % 8;
                        buffer[gx + y * width] |=  (128 >> dx);
                    }
                }
            }

            buf.write(buffer);
            sendCommand(buf.toByteArray());


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private byte[] bufferedImageToByteArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int bytesPerLine = (width + 7) / 8;
        byte[] data = new byte[height * bytesPerLine];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (image.getRGB(x, y) != -1) { // -1 是白色
                    int byteIndex = y * bytesPerLine + x / 8;
                    int bitIndex = 7 - (x % 8);
                    data[byteIndex] |= (1 << bitIndex);
                }
            }
        }
        return data;
    }

    // 打印QR碼
    public void printQRCode(String content, int size) {
//        // 設置QR碼大小
//        sendCommand(new byte[]{0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x43, (byte) size});
//        // 設置錯誤糾正級別
//        sendCommand(new byte[]{0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x45, 0x31});
//        // 存儲QR碼數據
//        byte[] qrData = content.getBytes();
//        int dataLength = qrData.length + 3;
//        sendCommand(new byte[]{0x1D, 0x28, 0x6B, (byte) (dataLength & 0xFF), (byte) ((dataLength >> 8) & 0xFF), 0x31, 0x50, 0x30});
//        sendCommand(qrData);
//        // 打印QR碼
//        sendCommand(new byte[]{0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30});
    }

    // 重置打印模式
    private void resetPrintModes() {
        boldMode = false;
        underlineMode = false;
        doubleHeightMode = false;
        doubleWidthMode = false;
        inverseMode = false;
        alignment = 0;
        lineSpacing = 30;
        characterSet = 0;
    }

    // 發送命令到打印機
    public void sendCommand(byte[] command) {
        try {
            commandStream.write(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 獲取所有命令的字節數組
    public byte[] toBytes() {
        return commandStream.toByteArray();
    }

    // 清除所有已添加的命令
    public void clear() {
        commandStream.reset();
    }

    // 檢查打印機狀態
    public boolean checkPrinterStatus() {
        // 這裡應該實現實際檢查打印機狀態的邏輯
        return paperStatus && !drawerStatus;
    }

    // 輔助方法：將byte數組轉換為十六進制字符串
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}