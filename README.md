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
