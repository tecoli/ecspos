# ECSPosBuilder 使用手冊

`ECSPosBuilder` 類別用於構建 ESC/POS 打印機命令流，支持多種格式設置和內容打印。此類提供多種方法，方便用戶自定義打印內容和格式。

## 方法簡介

- **`setTraditionalChineseMode()`**：設置傳統中文模式，用於打印繁體中文文本。
- **`setSimplifiedChineseMode()`**：設置簡體中文模式，用於打印簡體中文文本。
- **`setKoreanMode()`**：設置韓文模式。
- **`setJapaneseMode()`**：設置日文模式。
- **`setCharset(Charset charset)`**：設置字符集，默認為 UTF-8。
- **`printLine(String text)`**：打印一行文本，並自動換行。
- **`printText(String text)`**：打印文本，但不換行。
- **`printLine()`**：打印換行（不帶文本）。
- **`cutPaper()`**：切紙命令。
- **`setFontSize(double size)`**：設置字體大小，支持 1, 1.5, 2, 3, 4, 5, 6, 7, 8 倍。
- **`setAlignment(Alignment alignment)`**：設置對齊方式（左、中、右）。
- **`setBold(boolean bold)`**：設置加粗文本（`true` 加粗，`false` 正常）。
- **`setUnderline(int weight)`**：設置下劃線，0 表示無下劃線，1 表示細線，2 表示粗線。
- **`setLineSpacing(int spacing)`**：設置行間距，間距單位為點（dot），通常介於 0 到 255 之間。
- **`setCharacterSet(int set)`**：設置字符集編碼（0 到 15）。
- **`printBarcode(String content, BarCodeType type, int width, int height, int hriPosition)`**：打印條形碼，`type` 為條形碼類型，`width` 和 `height` 分別為條形碼寬度和高度，`hriPosition` 設置條形碼下方文字位置。
- **`setInverted(boolean inverted)`**：設置反白/反黑模式（`true` 為反白，`false` 為正常）。
- **`setLineHeight(int height)`**：設置行高度，指定行距的高度（0 到 255）。
- **`feedPaper(int dots)`**：進紙命令，進紙距離以點為單位。
- **`moveTo(int position)`**：水平移動到指定位置。
- **`setAbsolutePosition(int position)`**：設置絕對位置，單位為點。
- **`printImage(BufferedImage image)`**：打印圖像。
- **`printQRCode(String content, int size)`**：打印 QR 碼，`size` 設置大小。
- **`cashDrawerOut(int m, int t1, int t2)`**：打開錢箱，`m`、`t1`、`t2` 為控制參數。
- **`cashDrawerOut()`**：使用默認參數打開錢箱。

## 詳細說明

### 設置字體大小

- `setFontSize(double size)`：設置字體大小，支持以下倍數：
    - `1`：正常大小
    - `1.5`：1.5 倍
    - `2`：2 倍
    - `3`：3 倍
    - `4`：4 倍
    - `5`：5 倍
    - `6`：6 倍
    - `7`：7 倍
    - `8`：8 倍

### 設置對齊方式

- `setAlignment(Alignment alignment)`：設置文字對齊方式：
    - `Alignment.LEFT`：左對齊
    - `Alignment.CENTER`：居中對齊
    - `Alignment.RIGHT`：右對齊

### 設置行間距

- `setLineSpacing(int spacing)`：
    - 單位為點（dot），通常範圍為 `0` 到 `255`。
    - 可用於調整打印內容的行間距，以滿足不同的排版需求。

### 設置行高度

- `setLineHeight(int height)`：
    - 設置行高度，指定行距的高度（`0` 到 `255`）。
    - 例如：
        - `setLineHeight(24)`：設置行高為 24 點。
        - 可以與 `setLineSpacing` 結合使用，以達到更好的排版效果。

### 設置反白/反黑模式

- `setInverted(boolean inverted)`：
    - `true`：啟用反白模式（背景黑，文字白）。
    - `false`：正常模式。

### 打印條形碼

- `printBarcode(String content, BarCodeType type, int width, int height, int hriPosition)`：
    - `content`：條形碼內容。
    - `type`：條形碼類型（如 `CODE128`, `EAN13` 等）。
    - `width`：條形碼寬度（建議範圍 `2` 到 `6`）。
    - `height`：條形碼高度。
    - `hriPosition`：條形碼下方文字位置（`0` 無顯示，`1` 顯示在上方，`2` 顯示在下方，`3` 上下均顯示）。

### 打印 QR 碼

- `printQRCode(String content, int size)`：
    - `content`：QR 碼內容。
    - `size`：QR 碼大小（通常為 `1` 到 `8`，數字越大尺寸越大）。

## 使用範例

```java
// 創建 ECSPosBuilder 實例
ECSPosBuilder builder = new ECSPosBuilder();

// 設置打印模式為傳統中文
builder.setTraditionalChineseMode()
       .setFontSize(2) // 設置字體大小為 2 倍
       .setAlignment(Alignment.CENTER) // 設置文字居中
       .setBold(true) // 設置加粗
       .printLine("歡迎光臨") // 打印一行文字
       .setBold(false) // 取消加粗
       .printLine("這是一行示例文字") // 打印另一行文字
       .setUnderline(1) // 設置下劃線
       .printText("下劃線示例")
       .printLine() // 換行
       .setUnderline(0) // 取消下劃線
       .setLineHeight(30) // 設置行高度為 30 點
       .setLineSpacing(20) // 設置行間距為 20 點
       .setInverted(true) // 設置反白/反黑打印
       .printLine("反白/反黑示例") // 打印反白/反黑文字
       .setInverted(false) // 取消反白/反黑打印
       .printBarcode("123456789", BarCodeType.CODE128, 2, 50, 2) // 打印條形碼
       .cutPaper(); // 切紙

// 獲取最終的指令字節數組
byte[] commands = builder.build();

// 清空指令流以便重複使用
builder.clear();
```

# ECSPosBuilder User Manual

The `ECSPosBuilder` class is used to build ESC/POS printer command streams, supporting various formatting and content printing options. This class provides multiple methods for users to customize the printing content and format.

## Method Overview

- **`setTraditionalChineseMode()`**: Sets Traditional Chinese mode for printing.
- **`setSimplifiedChineseMode()`**: Sets Simplified Chinese mode for printing.
- **`setKoreanMode()`**: Sets Korean mode.
- **`setJapaneseMode()`**: Sets Japanese mode.
- **`setCharset(Charset charset)`**: Sets the character set, default is UTF-8.
- **`printLine(String text)`**: Prints a line of text with a newline.
- **`printText(String text)`**: Prints text without a newline.
- **`printLine()`**: Prints a newline.
- **`cutPaper()`**: Command to cut the paper.
- **`setFontSize(double size)`**: Sets font size, supporting 1, 1.5, 2, 3, 4, 5, 6, 7, 8 times.
- **`setAlignment(Alignment alignment)`**: Sets text alignment (left, center, right).
- **`setBold(boolean bold)`**: Sets bold text (`true` for bold, `false` for normal).
- **`setUnderline(int weight)`**: Sets underline (0 for none, 1 for thin, 2 for thick).
- **`setLineSpacing(int spacing)`**: Sets line spacing in dots (0 to 255).
- **`setCharacterSet(int set)`**: Sets character set encoding (0 to 15).
- **`printBarcode(String content, BarCodeType type, int width, int height, int hriPosition)`**: Prints a barcode with specified parameters.
- **`setInverted(boolean inverted)`**: Sets inverted mode (white on black).
- **`setLineHeight(int height)`**: Sets line height in dots (0 to 255).
- **`feedPaper(int dots)`**: Feeds paper by specified dot amount.
- **`moveTo(int position)`**: Moves horizontally to a specified position.
- **`setAbsolutePosition(int position)`**: Sets the absolute position in dots.
- **`printImage(BufferedImage image)`**: Prints an image.
- **`printQRCode(String content, int size)`**: Prints a QR code with specified content and size.
- **`cashDrawerOut(int m, int t1, int t2)`**: Opens the cash drawer with specified parameters.
- **`cashDrawerOut()`**: Opens the cash drawer with default parameters.

## Detailed Description

### Setting Font Size

- `setFontSize(double size)`: Sets the font size with the following multipliers:
    - `1`: Normal size
    - `1.5`: 1.5 times
    - `2`: 2 times
    - `3`: 3 times
    - `4`: 4 times
    - `5`: 5 times
    - `6`: 6 times
    - `7`: 7 times
    - `8`: 8 times

### Setting Text Alignment

- `setAlignment(Alignment alignment)`: Sets the text alignment:
    - `Alignment.LEFT`: Left alignment
    - `Alignment.CENTER`: Center alignment
    - `Alignment.RIGHT`: Right alignment

### Setting Line Spacing

- `setLineSpacing(int spacing)`:
    - The unit is in dots, typically ranging from `0` to `255`.
    - Adjusts the spacing between lines for different formatting needs.

### Setting Line Height

- `setLineHeight(int height)`:
    - Specifies the line height in dots (`0` to `255`).
    - Examples:
        - `setLineHeight(24)`: Sets line height to 24 dots.
    - Can be combined with `setLineSpacing` for better layout control.

### Setting Inverted Mode

- `setInverted(boolean inverted)`:
    - `true`: Enables inverted mode (white text on a black background).
    - `false`: Normal mode.

### Printing Barcodes

- `printBarcode(String content, BarCodeType type, int width, int height, int hriPosition)`:
    - `content`: The content of the barcode.
    - `type`: Barcode type (e.g., `CODE128`, `EAN13`).
    - `width`: Barcode width (suggested range `2` to `6`).
    - `height`: Barcode height.
    - `hriPosition`: Position of human-readable information (HRI) text (`0` for none, `1` above, `2` below, `3` both).

### Printing QR Codes

- `printQRCode(String content, int size)`:
    - `content`: The content of the QR code.
    - `size`: Size of the QR code (typically `1` to `8`, larger numbers for larger sizes).

## Usage Example

```java
// Create an ECSPosBuilder instance
ECSPosBuilder builder = new ECSPosBuilder();

// Set to Traditional Chinese printing mode
builder.setTraditionalChineseMode()
       .setFontSize(2) // Set font size to 2x
       .setAlignment(Alignment.CENTER) // Center align text
       .setBold(true) // Set bold text
       .printLine("Welcome") // Print a line of text
       .setBold(false) // Disable bold
       .printLine("This is a sample line") // Print another line of text
       .setUnderline(1) // Set underline
       .printText("Underline example")
       .printLine() // New line
       .setUnderline(0) // Disable underline
       .setLineHeight(30) // Set line height to 30 dots
       .setLineSpacing(20) // Set line spacing to 20 dots
       .setInverted(true) // Enable inverted mode
       .printLine("Inverted text example") // Print inverted text
       .setInverted(false) // Disable inverted mode
       .printBarcode("123456789", BarCodeType.CODE128, 2, 50, 2) // Print barcode
       .cutPaper(); // Cut paper

// Get the final command byte array
byte[] commands = builder.build();

// Clear the command stream for reuse
builder.clear();
```