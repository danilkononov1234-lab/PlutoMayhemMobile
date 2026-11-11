package com.plutomayhem.mobile;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.usb.*;
import android.view.*;
import android.widget.*;
import android.text.InputType;
import java.util.*;
import java.text.*;

public class MainActivity extends Activity {
 final int BG=0xff101424, PANEL=0xff303956, EDGE=0xff6b7ba1, CYAN=0xff5de8d6, WHITE=0xfff4f6ff;
 LinearLayout root,body; TextView clock; Handler handler=new Handler(); long start=0,elapsed=0; boolean running=false;
 String notes=""; int score=0; Random random=new Random(); int target=0;
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);
 notes=getPreferences(0).getString("notes","");home();}
 TextView text(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
 LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
 void frame(String title){root=column();root.setBackgroundColor(BG);root.setPadding(14,10,14,8);setContentView(root);
 LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);root.addView(top);
 TextView back=text("◀",23,CYAN);back.setPadding(4,8,16,8);top.addView(back);back.setOnClickListener(v->home());
 TextView heading=text("MAYHEM  "+title,19,WHITE);heading.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);top.addView(heading,new LinearLayout.LayoutParams(0,-2,1));
 clock=text("",12,CYAN);top.addView(clock);tick();
 View line=new View(this);line.setBackgroundColor(CYAN);root.addView(line,new LinearLayout.LayoutParams(-1,2));
 ScrollView scroll=new ScrollView(this);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));body=column();body.setPadding(2,12,2,10);scroll.addView(body);
 TextView foot=text("PORTAPACK  •  PLUTO MOBILE   |   OFFLINE",11,EDGE);foot.setGravity(Gravity.CENTER);root.addView(foot);}
 void tick(){if(clock==null)return;clock.setText(new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date()));}
 void label(String s){TextView t=text(s,17,WHITE);t.setPadding(12,14,8,14);body.addView(t);}
 void tile(LinearLayout parent,String icon,String name,Runnable action){TextView b=text(icon+"\n"+name,17,WHITE);b.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);b.setGravity(Gravity.CENTER);
 b.setBackgroundColor(PANEL);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,118,1);p.setMargins(3,3,3,3);parent.addView(b,p);b.setOnClickListener(v->action.run());}
 void row(String i,String a,Runnable ra,String j,String b,Runnable rb){LinearLayout r=new LinearLayout(this);body.addView(r);tile(r,i,a,ra);tile(r,j,b,rb);}
 void button(String name,Runnable r){Button b=new Button(this);b.setText(name);body.addView(b);b.setOnClickListener(v->r.run());}
 EditText input(String hint,String value){EditText e=new EditText(this);e.setHint(hint);e.setText(value);e.setTextColor(WHITE);e.setHintTextColor(EDGE);body.addView(e);return e;}
 void home(){frame("HOME");label("HACKRF MAYHEM  •  ANDROID EDITION");row("↓","Receive",()->sdr("Receive"),"↕","Transceiver",()->sdr("Transceiver"));
 row("↑","Transmit",()->sdr("Transmit"),"⌁","Recon",()->sdr("Recon"));
 row("●","Capture",()->sdr("Capture"),"▶","Replay",()->sdr("Replay"));
 row("⚒","Utilities",()->utilities(),"▣","Games",()->games());
 row("⚙","Settings",()->settings(),"▤","Hardware",()->hardware());
 row("▦","Files",()->files(),"☷","About",()->about());}
 void sdr(String name){frame(name.toUpperCase(Locale.US));label(name+" requires a connected SDR and an implemented IQ transport.");label("Offline menu remains fully available.");button("RETURN HOME",()->home());}
 void utilities(){frame("UTILITIES");row("λ","RF Calculator",()->calculator(),"◷","Stopwatch",()->stopwatch());
 row("✎","Notes",()->notes(),"▤","Device Info",()->deviceInfo());row("▦","Files",()->files(),"◈","Unit Converter",()->converter());}
 void calculator(){frame("RF CALCULATOR");label("Frequency in MHz");EditText e=input("Example: 433.92","433.92");TextView result=text("",18,CYAN);body.addView(result);
 button("CALCULATE",()->{try{double mhz=Double.parseDouble(e.getText().toString().replace(',','.'));if(mhz<=0)throw new Exception();
 double meters=299.792458/mhz;result.setText(String.format(Locale.US,"Wavelength: %.5f m\nQuarter wave: %.2f cm\nHalf wave: %.2f cm",meters,meters*25,meters*50));
 }catch(Exception x){result.setText("Enter a positive frequency.");}});}
 void converter(){frame("CONVERTER");label("Frequency in MHz");EditText e=input("MHz","100");TextView out=text("",18,CYAN);body.addView(out);
 button("CONVERT",()->{try{double v=Double.parseDouble(e.getText().toString().replace(',','.'));out.setText(String.format(Locale.US,"%.0f Hz\n%.3f kHz\n%.6f GHz",v*1e6,v*1e3,v/1e3));}catch(Exception ex){out.setText("Invalid number");}});}
 void notes(){frame("NOTES");EditText e=input("Your offline notes",notes);e.setMinLines(8);e.setGravity(Gravity.TOP);button("SAVE NOTES",()->{notes=e.getText().toString();getPreferences(0).edit().putString("notes",notes).apply();Toast.makeText(this,"Saved on this phone",Toast.LENGTH_SHORT).show();});}
 void stopwatch(){frame("STOPWATCH");TextView display=text("00:00.0",36,CYAN);display.setGravity(Gravity.CENTER);body.addView(display);
 Runnable update=new Runnable(){public void run(){if(!display.isAttachedToWindow())return;long ms=elapsed+(running?SystemClock.elapsedRealtime()-start:0);
 display.setText(String.format(Locale.US,"%02d:%02d.%d",ms/60000,(ms/1000)%60,(ms/100)%10));handler.postDelayed(this,100);}};
 handler.post(update);button("START / PAUSE",()->{if(running){elapsed+=SystemClock.elapsedRealtime()-start;running=false;}else{start=SystemClock.elapsedRealtime();running=true;}});
 button("RESET",()->{running=false;elapsed=0;display.setText("00:00.0");});}
 void deviceInfo(){frame("DEVICE INFO");label("Manufacturer: "+Build.MANUFACTURER);label("Model: "+Build.MODEL);label("Android: "+Build.VERSION.RELEASE);label("SDK: "+Build.VERSION.SDK_INT);label("Board: "+Build.BOARD);}
 void files(){frame("FILES");label("Browse local files using Android's document picker.");button("OPEN FILE PICKER",()->{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("*/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,42);});}
 @Override protected void onActivityResult(int req,int res,Intent data){super.onActivityResult(req,res,data);if(req==42&&res==RESULT_OK&&data!=null){frame("FILES");label("Selected: "+data.getData());button("OPEN ANOTHER",()->files());}}
 void games(){frame("GAMES");label("GUESS THE NUMBER • 1 TO 9");target=1+random.nextInt(9);score=0;TextView info=text("Score: 0",20,CYAN);body.addView(info);
 for(int i=1;i<=9;i+=3){LinearLayout r=new LinearLayout(this);body.addView(r);for(int j=i;j<i+3;j++){final int n=j;tile(r,"★",""+n,()->{if(n==target){score++;info.setText("Correct! Score: "+score);target=1+random.nextInt(9);}else{info.setText("Try again • Score: "+score);}});}}}
 void settings(){frame("SETTINGS");label("Appearance: Mayhem dark theme");label("Offline mode: always enabled");label("Notes stored locally on device");button("OPEN ANDROID APP SETTINGS",()->{Intent i=new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);i.setData(android.net.Uri.parse("package:"+getPackageName()));startActivity(i);});}
 void hardware(){frame("HARDWARE");label("Pluto SDR • USB OTG diagnostics");button("SCAN USB",()->{UsbManager manager=(UsbManager)getSystemService(USB_SERVICE);StringBuilder sb=new StringBuilder();
 for(UsbDevice d:manager.getDeviceList().values())sb.append(String.format(Locale.US,"VID %04X / PID %04X\n%s\n",d.getVendorId(),d.getProductId(),d.getDeviceName()));
 label(sb.length()==0?"No USB devices detected. Check OTG, power and cable.":sb.toString());});
 label("USB detection only. IIO streaming is not implemented.");}
 void about(){frame("ABOUT");label("Pluto Mayhem Mobile 0.2");label("Offline-first Android UI inspired by PortaPack Mayhem.");label("Not affiliated with the Mayhem firmware project.");label("RF features require future hardware integration.");}
 @Override public void onBackPressed(){home();}
}
