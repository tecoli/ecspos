import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ECSPosBuilder {

    private ByteArrayOutputStream commandStream;


    private Charset charset = StandardCharsets.UTF_8;

    public ECSPosBuilder() {
        commandStream = new ByteArrayOutputStream();
    }



    public ECSPosBuilder setTraditionalChineseMode() {
        command(new byte[]{0x1F, 0x1B, 0x1F, 0x46, 0x4F, 0x4E, 0x54, 0x01});
        return this;
    }

    public ECSPosBuilder setSimplifiedChineseMode() {
        command(new byte[]{0x1F, 0x1B, 0x1F, 0x46, 0x4F, 0x4E, 0x54, 0x00});
        return this;
    }

    public ECSPosBuilder setKoreanMode() {
        command(new byte[]{0x1F, 0x1B, 0x1F, 0x46, 0x4F, 0x4E, 0x54, 0x02});
        return this;
    }

    public ECSPosBuilder setJapaneseMode() {
        command(new byte[]{0x1F, 0x1B, 0x1F, 0x46, 0x4F, 0x4E, 0x54, 0x03});
        return this;
    }

    public ECSPosBuilder setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public ECSPosBuilder newLine(String text) {
        printText(text);
        newLine();
        return this;
    }

    public ECSPosBuilder printText(String text) {
        try {
            command(text.getBytes("GBK"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public ECSPosBuilder newLine() {
        command(new byte[]{0x0A});
        return this;
    }

    public ECSPosBuilder cutPaper() {
        command(new byte[]{0x1D, 0x56, 0x41, 0x00});
        return this;
    }

    public ECSPosBuilder setFontSize(double size) {
        int n = 0;
        if(size == 1) {
            n = 0;
        } else if(size == 2) {
            n = 0x11;
        } else if(size == 3) {
            n = 0x22;
        } else if(size == 4) {
            n = 0x33;
        } else if(size == 5) {
            n = 0x44;
        } else if(size == 6) {
            n = 0x55;
        } else if(size == 7) {
            n = 0x66;
        } else if(size == 8) {
            n = 0x77;
        } else if(size == 1.5) {
            n = 0x01;
        } else {
            throw new IllegalArgumentException("font size only 1,1.5,2,3,4,5,6,7,8");
        }
        System.out.println(size + ":" + n);
        command(new byte[]{29, 33, (byte) (n)});
        return this;
    }

    public ECSPosBuilder setAlignment(Alignment alignment) {

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
        command(new byte[]{27, 97, value});
        return this;
    }

    public ECSPosBuilder setBold(boolean bold) {

        command(new byte[]{0x1B, 0x45, (byte) (bold ? 1 : 0)});
        return this;
    }

    public ECSPosBuilder setUnderline(int weight) {

        command(new byte[]{0x1B, 0x2D, (byte) weight});
        return this;
    }

    public ECSPosBuilder setLineSpacing(int spacing) {

        command(new byte[]{0x1B, 0x33, (byte) spacing});
        return this;
    }

    public ECSPosBuilder setCharacterSet(int set) {
        if (set >= 0 && set <= 15) {

            command(new byte[]{0x1B, 0x52, (byte) set});
        }
        return this;
    }

    public ECSPosBuilder printBarcode(String content, BarCodeType type, int width, int height, int hriPosition) {
        command(new byte[]{0x1D, 0x68, (byte) height});
        command(new byte[]{0x1D, 0x77, (byte) width});
        command(new byte[]{0x1D, 0x6B, (byte) type.ordinal()});
        command(content.getBytes(charset));
        command(new byte[]{0});
        return this;
    }

    public ECSPosBuilder setInverted(boolean inverted) {
        command(new byte[]{29, 66, (byte) (inverted ? 1 : 0)});
        return this;
    }

    public ECSPosBuilder setLineHeight(int height) {
        command(new byte[]{0x1B, 0x33, (byte) height});
        return this;
    }

    public ECSPosBuilder feedPaper(int dots) {
        command(new byte[]{0x1B, 0x4A, (byte) dots});
        return this;
    }

    public ECSPosBuilder moveTo(int position) {
        command(new byte[]{0x1B, 0x24, (byte) (position & 0xFF), (byte) ((position >> 8) & 0xFF)});
        return this;
    }

    public ECSPosBuilder setAbsolutePosition(int position) {
        command(new byte[]{0x1B, 0x24, (byte) (position % 256), (byte) (position / 256)});
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
            command(buf.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public ECSPosBuilder printQRCode(String content, int size) {
        // 添加QR碼打印邏輯
        return this;
    }

    /**
     * 設置加粗<br>
     * 15、 ESC E n 選擇/取消加粗模式
     *
     * @param n 0≦n≦255<br>
     *          n的最低位為0時,取消加粗模式<br>
     *          n的最低位為1時,選擇加粗模式<br>
     */
    public ECSPosBuilder setBold(int n) {
        commandStream.write(27);
        commandStream.write(69);
        commandStream.write(n);
        return this;
    }

    /**
     * 設置反白/反黑 打印模式
     * 33、 GS B n 選擇/取消黑白反顯打印模式
     *
     * @param n 0≦n≦255<br>
     *          n的最低位為0時,取消反顯打印<br>
     *          n的最低位為1時,選擇反顯打印
     */
    public ECSPosBuilder setInverted(int n) {
        commandStream.write(29);
        commandStream.write(66);
        commandStream.write(n);
        return this;
    }



    private ECSPosBuilder command(byte[] command) {
        try {
            commandStream.write(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ECSPosBuilder.this;
    }


    public byte[] build() {
        command(new byte[]{0x1B, 0x40});

        return commandStream.toByteArray();
    }

    public void clear() {
        commandStream.reset();
    }




    public ECSPosBuilder cashDrawerOut(int m, int t1, int t2) {
        command(new byte[]{27, 112, (byte) m, (byte) t1, (byte) t2});
        return this;
    }

    public ECSPosBuilder cashDrawerOut() {
        return cashDrawerOut(0, 10, 0);
    }
    public static int charsWidth(String text) {
        int width = 0;
        int itemLength = text.length();
        //判斷全形半形長度
        for (int i = 0; i < itemLength; i++) {
            char c = text.charAt(i);
            // 檢查字符是否為半形。如果字符位於 ASCII 可打印字符範圍內（即字符碼介於 33 到 126 之間），則認為是半形字符，其寬度計為 1。
            // 若不在此範圍內，則預設為全形字符，其寬度計為 2。
            width += (c >= 32 && c <= 126) ? 1 : 2;
        }
        return width;
    }

}
