# ECSPosBuilder 使用手冊

`ECSPosBuilder` 類別用於構建ESC/POS打印機命令流，支持多種格式設置和內容打印。此類提供多種方法，方便用戶自定義打印內容和格式。

## 方法簡介

- `setTraditionalChineseMode()`：設置傳統中文模式。
- `setSimplifiedChineseMode()`：設置簡體中文模式。
- `setKoreanMode()`：設置韓文模式。
- `setJapaneseMode()`：設置日文模式。
- `setCharset(Charset charset)`：設置字符集。
- `printLine(String text)`：打印一行文本。
- `printText(String text)`：打印文本。
- `printLine()`：打印換行。
- `cutPaper()`：切紙。
- `setFontSize(double size)`：設置字體大小，支持1, 1.5, 2, 3, 4, 5, 6, 7, 8倍。
- `setAlignment(Alignment alignment)`：設置對齊方式（左、中、右）。
- `setBold(boolean bold)`：設置加粗文本。
- `setUnderline(int weight)`：設置下劃線（0-2）。
- `setLineSpacing(int spacing)`：設置行間距。
- `setCharacterSet(int set)`：設置字符集編碼。
- `printBarcode(String content, BarCodeType type, int width, int height, int hriPosition)`：打印條形碼。
- `setInverted(boolean inverted)`：設置反白/反黑模式。
- `setLineHeight(int height)`：設置行高度。
- `feedPaper(int dots)`：進紙。
- `moveTo(int position)`：移動到指定位置。
- `setAbsolutePosition(int position)`：設置絕對位置。
- `printImage(BufferedImage image)`：打印圖像。
- `printQRCode(String content, int size)`：打印QR碼。
- `cashDrawerOut(int m, int t1, int t2)`：打開錢箱。
- `cashDrawerOut()`：默認打開錢箱。

## 使用範例

```java
// 創建ECSPosBuilder實例
ECSPosBuilder builder = new ECSPosBuilder();

// 設置打印模式為傳統中文
builder.setTraditionalChineseMode()
       .setFontSize(2) // 設置字體大小為2倍
       .setAlignment(Alignment.CENTER) // 設置文字居中
       .setBold(true) // 設置加粗
       .printLine("歡迎光臨") // 打印一行文字
       .setBold(false) // 取消加粗
       .printLine("這是一行示例文字") // 打印另一行文字
       .setUnderline(1) // 設置下劃線
       .printText("下劃線示例")
       .printLine() // 換行
       .setUnderline(0) // 取消下劃線
       .setInverted(true) // 設置反白/反黑打印
       .printLine("反白/反黑示例") // 打印反白/反黑文字
       .setInverted(false) // 取消反白/反黑打印
       .printBarcode("123456789", BarCodeType.CODE128, 2, 50, 2) // 打印條形碼
       .cutPaper(); // 切紙

// 獲取最終的指令字節數組
byte[] commands = builder.build();

// 清空指令流以便重複使用
builder.clear();
