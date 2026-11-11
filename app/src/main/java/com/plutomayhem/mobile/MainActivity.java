package com.plutomayhem.mobile;
import android.app.*; import android.os.*; import android.content.*; import android.hardware.usb.*; import android.graphics.Color; import android.view.*; import android.widget.*;
public class MainActivity extends Activity {
 UsbManager usb; TextView status; final int VID=0x0456, PID=0xb673;
 public void onCreate(Bundle b){super.onCreate(b);usb=(UsbManager)getSystemService(USB_SERVICE);
 LinearLayout layout=new LinearLayout(this);layout.setOrientation(1);layout.setPadding(30,40,30,20);layout.setBackgroundColor(Color.rgb(12,17,28));
 TextView title=new TextView(this);title.setText("PLUTO MAYHEM MOBILE");title.setTextColor(0xff64e7d1);title.setTextSize(25);layout.addView(title);
 status=new TextView(this);status.setTextColor(Color.WHITE);status.setTextSize(17);status.setText("USB OTG diagnostic prototype\nNo IQ streaming yet.");layout.addView(status);
 Button scan=new Button(this);scan.setText("SCAN USB DEVICES");layout.addView(scan);scan.setOnClickListener(v->scan());
 setContentView(layout);scan();}
 void scan(){StringBuilder s=new StringBuilder("USB devices:\n");boolean found=false;
 for(UsbDevice d:usb.getDeviceList().values()){s.append(String.format("%04X:%04X %s\n",d.getVendorId(),d.getProductId(),d.getDeviceName()));
 if(d.getVendorId()==VID && d.getProductId()==PID){found=true;s.append("ADALM-Pluto detected\n");}}
 if(!found)s.append("Pluto not detected. Check OTG cable and power.");status.setText(s.toString());}
}