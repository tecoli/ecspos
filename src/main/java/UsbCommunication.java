import javax.usb.*;
import javax.usb.util.UsbUtil;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class UsbCommunication {

    public static void main(String[] args) {
        try {
            // 获取USB服务
            UsbServices services = UsbHostManager.getUsbServices();
            // 获取根USB集线器
            UsbHub rootHub = services.getRootUsbHub();
            // 列出并与设备通信
            listAndCommunicate(rootHub);
        } catch (UsbException e) {
            e.printStackTrace();
        }
    }

    public static void listAndCommunicate(UsbHub hub) {
        List<UsbDevice> devices = hub.getAttachedUsbDevices();
        for (UsbDevice device : devices) {
            printDeviceInfo(device);
            if (device.isUsbHub()) {
                listAndCommunicate((UsbHub) device); // 递归列出集线器中的设备
            } else {
                // 根据实际的Vendor ID和Product ID来确定目标设备
                UsbDeviceDescriptor descriptor = device.getUsbDeviceDescriptor();
                if (descriptor.idVendor() == (short) 0x1234 && descriptor.idProduct() == (short) 0x5678) {
                    communicateWithDevice(device);
                }
            }
        }
    }

    public static void printDeviceInfo(UsbDevice device) {
        UsbDeviceDescriptor descriptor = device.getUsbDeviceDescriptor();
        System.out.println("Vendor ID : " + String.format("%04x", descriptor.idVendor()));
        System.out.println("Product ID: " + String.format("%04x", descriptor.idProduct()));
        try {
            System.out.println("Manufacturer: " + device.getString(descriptor.iManufacturer()));
            System.out.println("Product: " + device.getString(descriptor.iProduct()));
            System.out.println("Serial Number: " + device.getString(descriptor.iSerialNumber()));
        } catch (UnsupportedEncodingException | UsbException e) {
            e.printStackTrace();
        }
    }

    public static void communicateWithDevice(UsbDevice device) {
        try {
            // 获取活动配置
            UsbConfiguration configuration = device.getActiveUsbConfiguration();
            // 获取接口
            UsbInterface iface = configuration.getUsbInterface((byte) 0);
            // 声明接口
            iface.claim(usbInterface -> true);

            // 获取端点并打开管道
            UsbEndpoint endpoint = iface.getUsbEndpoint((byte) 0x01); // 替换为实际的端点地址
            UsbPipe pipe = endpoint.getUsbPipe();
            pipe.open();

            // 发送数据
            byte[] data = new byte[]{0x01, 0x02, 0x03}; // 示例数据
            int sent = pipe.syncSubmit(data);
            System.out.println("发送了 " + sent + " 字节");

            // 接收数据
            byte[] buffer = new byte[8];
            int received = pipe.syncSubmit(buffer);
            System.out.println("接收了 " + received + " 字节");
            for (byte b : buffer) {
                System.out.printf("%02x ", b);
            }

            // 关闭管道和接口
            pipe.close();
            iface.release();
        } catch (UsbException e) {
            e.printStackTrace();
        }
    }
}
