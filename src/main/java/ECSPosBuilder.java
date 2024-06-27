import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ECSPosBuilder {

    private ByteArrayOutputStream commandStream;
    private boolean paperStatus = true;
    private boolean drawerStatus = false;
    private boolean boldMode = false;
    private boolean underlineMode = false;
    private boolean doubleHeightMode = false;
    private boolean doubleWidthMode = false;
    private boolean inverseMode = false;
    private int characterSet = 0;
    private int lineSpacing = 30;
    private Alignment alignment = Alignment.LEFT;
    private Charset charset = StandardCharsets.UTF_8;

    public ECSPosBuilder() {
        commandStream = new ByteArrayOutputStream();
    }

    public ECSPosBuilder initialize() {
        sendCommand(new byte[]{0x1B, 0x40});
        resetPrintModes();
        return this;
    }

    public ECSPosBuilder setTraditionalChineseMode() {
        sendCommand(new byte[]{0x1F, 0x1B, 0x1F, 0x46, 0x4F, 0x4E, 0x54, 0x01});
        return this;
    }

    public ECSPosBuilder setSimplifiedChineseMode() {
        sendCommand(new byte[]{0x1F, 0x1B, 0x1F, 0x46, 0x4F, 0x4E, 0x54, 0x00});
        return this;
    }

    public ECSPosBuilder setKoreanMode() {
        sendCommand(new byte[]{0x1F, 0x1B, 0x1F, 0x46, 0x4F, 0x4E, 0x54, 0x02});
        return this;
    }

    public ECSPosBuilder setJapaneseMode() {
        sendCommand(new byte[]{0x1F, 0x1B, 0x1F, 0x46, 0x4F, 0x4E, 0x54, 0x03});
        return this;
    }

    public ECSPosBuilder setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public ECSPosBuilder printLine(String text) {
        printText(text);
        printLine();
        return this;
    }

    public ECSPosBuilder printText(String text) {
        try {
            sendCommand(text.getBytes("GBK"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public ECSPosBuilder printLine() {
        sendCommand(new byte[]{0x0A});
        return this;
    }

    public ECSPosBuilder cutPaper() {
        sendCommand(new byte[]{0x1D, 0x56, 0x41, 0x00});
        return this;
    }

    public ECSPosBuilder setFontSize(int size) {
        sendCommand(new byte[]{29, 33, (byte) (size)});
        return this;
    }

    public ECSPosBuilder setAlignment(Alignment alignment) {
        this.alignment = alignment;
        byte value;
        switch (alignment) {
            case LEFT:
                value = 0;
                break;
            case CENTER:
                value = 1;
                break;
            case RIGHT:
                value = 2;
                break;
            default:
                throw new IllegalArgumentException("Invalid alignment: " + alignment);
        }
        sendCommand(new byte[]{27, 97, value});
        return this;
    }

    public ECSPosBuilder setBold(boolean bold) {
        this.boldMode = bold;
        sendCommand(new byte[]{0x1B, 0x45, (byte) (bold ? 1 : 0)});
        return this;
    }

    public ECSPosBuilder setUnderline(int weight) {
        this.underlineMode = (weight > 0);
        sendCommand(new byte[]{0x1B, 0x2D, (byte) weight});
        return this;
    }

    public ECSPosBuilder setLineSpacing(int spacing) {
        this.lineSpacing = spacing;
        sendCommand(new byte[]{0x1B, 0x33, (byte) spacing});
        return this;
    }

    public ECSPosBuilder setCharacterSet(int set) {
        if (set >= 0 && set <= 15) {
            this.characterSet = set;
            sendCommand(new byte[]{0x1B, 0x52, (byte) set});
        }
        return this;
    }

    public ECSPosBuilder printBarcode(String content, BarCodeType type, int width, int height, int hriPosition) {
        sendCommand(new byte[]{0x1D, 0x68, (byte) height});
        sendCommand(new byte[]{0x1D, 0x77, (byte) width});
        sendCommand(new byte[]{0x1D, 0x6B, (byte) type.ordinal()});
        sendCommand(content.getBytes(charset));
        sendCommand(new byte[]{0});
        return this;
    }
    public ECSPosBuilder setInverted(boolean inverted) {
        sendCommand(new byte[]{29, 66, (byte) (inverted ? 1 : 0)});
        return this;
    }

    public ECSPosBuilder setLineHeight(int height) {
        sendCommand(new byte[]{0x1B, 0x33, (byte) height});
        return this;
    }

    public ECSPosBuilder feedPaper(int dots) {
        sendCommand(new byte[]{0x1B, 0x4A, (byte) dots});
        return this;
    }

    public ECSPosBuilder moveTo(int position) {
        sendCommand(new byte[]{0x1B, 0x24, (byte) (position & 0xFF), (byte) ((position >> 8) & 0xFF)});
        return this;
    }

    public ECSPosBuilder setAbsolutePosition(int position) {
        sendCommand(new byte[]{0x1B, 0x24, (byte) (position % 256), (byte) (position / 256)});
        return this;
    }

    public ECSPosBuilder printImage(BufferedImage image) {
        try {
            int width = (image.getWidth() + 7) / 8;
            int height = image.getHeight();
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            buf.write(new byte[]{0x1D, 0x76, 0x30});
            buf.write(0);
            buf.write(width & 0xFF);
            buf.write(width >> 8 & 0xFF);
            buf.write(height & 0xFF);
            buf.write(height >> 8 & 0xFF);

            byte[] buffer = new byte[width * height];
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int rgb = image.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    if (red + green + blue == 0) {
                        int gx = x / 8;
                        int dx = x % 8;
                        buffer[gx + y * width] |= (128 >> dx);
                    }
                }
            }
            buf.write(buffer);
            sendCommand(buf.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public ECSPosBuilder printQRCode(String content, int size) {
        // 添加QR碼打印邏輯
        return this;
    }

    private void resetPrintModes() {
        boldMode = false;
        underlineMode = false;
        doubleHeightMode = false;
        doubleWidthMode = false;
        inverseMode = false;
        alignment = Alignment.LEFT;
        lineSpacing = 30;
        characterSet = 0;
    }

    private void sendCommand(byte[] command) {
        try {
            commandStream.write(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] build() {
        return commandStream.toByteArray();
    }

    public void clear() {
        commandStream.reset();
    }

    public boolean checkPrinterStatus() {
        return paperStatus && !drawerStatus;
    }


    public ECSPosBuilder cashDrawerOut(int m, int t1, int t2) {
        sendCommand(new byte[]{27, 112, (byte) m, (byte) t1, (byte) t2});
        return this;
    }

    public ECSPosBuilder cashDrawerOut() {
        return cashDrawerOut(0, 10, 0);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
