package com.plutomayhem.mobile;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.*;
import android.hardware.usb.*;
import android.os.Bundle;
import android.widget.*;
import java.util.Locale;

public class UsbDiagnosticsActivity extends Activity {
    private static final String PERMISSION = "com.plutomayhem.mobile.USB_PERMISSION";
    private UsbManager manager;
    private TextView status;
    private UsbDeviceConnection connection;
    private final BroadcastReceiver permissionReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            if (PERMISSION.equals(intent.getAction())) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null && intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) open(device);
                else status.setText("USB permission denied");
            }
        }
    };
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        manager = (UsbManager) getSystemService(USB_SERVICE);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 40, 24, 24);
        status = new TextView(this);
        status.setTextSize(16);
        Button scan = new Button(this);
        scan.setText("SCAN USB / CONNECT");
        scan.setOnClickListener(v -> scan());
        layout.addView(scan);
        layout.addView(status);
        setContentView(layout);
        registerReceiver(permissionReceiver, new IntentFilter(PERMISSION));
        scan();
    }
    private void scan() {
        StringBuilder text = new StringBuilder("Direct USB OTG diagnostic (not RX/TX yet)\n");
        UsbDevice candidate = null;
        for (UsbDevice d : manager.getDeviceList().values()) {
            text.append(String.format(Locale.US,"VID %04X PID %04X interfaces %d\n",d.getVendorId(),d.getProductId(),d.getInterfaceCount()));
            if (d.getVendorId() == 0x0456 || d.getVendorId() == 0x1d50) candidate = d;
        }
        status.setText(text.toString());
        if (candidate == null) { status.append("No candidate Analog Devices USB device found."); return; }
        if (manager.hasPermission(candidate)) { open(candidate); return; }
        Intent intent = new Intent(PERMISSION).setPackage(getPackageName());
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        manager.requestPermission(candidate, pi);
    }
    private void open(UsbDevice device) {
        if (connection != null) connection.close();
        connection = manager.openDevice(device);
        if (connection == null) { status.append("\nCould not open USB device"); return; }
        status.append("\nUSB device opened, fd=" + connection.getFileDescriptor());
        for (int i=0;i<device.getInterfaceCount();i++) {
            UsbInterface iface=device.getInterface(i);
            status.append("\nInterface "+i+" class="+iface.getInterfaceClass()+" endpoints="+iface.getEndpointCount());
            for (int j=0;j<iface.getEndpointCount();j++) {
                UsbEndpoint ep=iface.getEndpoint(j);
                status.append(String.format(Locale.US,"\n  EP 0x%02X type=%d",ep.getAddress(),ep.getType()));
            }
        }
        status.append("\nRX/TX disabled until USB IIO transport is implemented and verified.");
    }
    @Override protected void onDestroy() {
        if (connection != null) connection.close();
        unregisterReceiver(permissionReceiver);
        super.onDestroy();
    }
}
