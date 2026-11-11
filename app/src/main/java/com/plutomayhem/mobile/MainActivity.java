package com.plutomayhem.mobile;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.*;
import android.view.*;
import android.widget.Toast;
import java.util.*;

public class MainActivity extends Activity {
 MayhemView view;
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);view=new MayhemView();setContentView(view);}
 @Override public void onBackPressed(){if(view.screen==0)super.onBackPressed();else{view.screen=0;view.invalidate();}}
 class MayhemView extends View {
  Canvas c; Paint p=new Paint(3); Random rng=new Random(); int screen=0,selection=0; boolean demo=true;
  final int BLACK=0xff111523, BORDER=0xffa6b7d6, CYAN=0xff54d7d7, PANEL=0xff626d99, WHITE=0xfff5f6ff;
  final String[] menu={"ExtAppErr","Receive","Transmit","Transceiver","Recon","Capture","Replay","Looking Glass","Utilities","Games","Settings","Hardware"};
  final String[] icons={"▣","↓","↑","↕","⌁","●","▶","⌕","⚒","▤","⚙","▣"};
  float sx,sy; long t0=System.currentTimeMillis(); float[][] waterfall=new float[120][120];
  MayhemView(){super(MainActivity.this);setLayerType(View.LAYER_TYPE_SOFTWARE,null);setFocusable(true);}
  void fill(int color,float l,float t,float r,float b){p.setColor(color);p.setStyle(Paint.Style.FILL);c.drawRect(l,t,r,b,p);}
  void stroke(int color,float l,float t,float r,float b){p.setColor(color);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1);c.drawRect(l,t,r,b,p);p.setStyle(Paint.Style.FILL);}
  void txt(String s,float x,float y,float size,int color,boolean bold){p.setColor(color);p.setTypeface(bold?Typeface.create("monospace",Typeface.BOLD):Typeface.MONOSPACE);p.setTextSize(size);c.drawText(s,x,y,p);}
  void center(String s,float x,float y,float size,int color){p.setTypeface(Typeface.create("monospace",Typeface.BOLD));p.setTextSize(size);txt(s,x-p.measureText(s)/2,y,size,color,true);}
  @Override protected void onDraw(Canvas canvas){super.onDraw(canvas);c=canvas;c.drawColor(0xff171c2b);
    sx=getWidth()/360f;sy=getHeight()/570f;c.save();c.scale(sx,sy);
    fill(0xff34405f,10,48,350,548);stroke(BORDER,10,48,350,548);
    fill(BLACK,24,65,336,445);stroke(0xff98aac7,24,65,336,445);
    
    header();
    if(screen==0)home();else if(screen==1)waterfall();else if(screen==2)utility();else if(screen==3)games();else if(screen==4)hardware();else if(screen==5)settings();else if(screen==6)files();else placeholder();
    sideButtons();txt("◉ desktop",26,438,10,WHITE,true);txt(String.format(Locale.US,"%tT",new Date()),265,438,10,WHITE,false);
    c.restore();if(screen==1)postInvalidateDelayed(90);
  }
  void header(){fill(0xff101525,24,65,336,86);txt("HACKRF MAYHEM",28,76,12,WHITE,true);txt("PORTAPACK",28,83,6,WHITE,true);
   txt("◉ ☾ ♜ ↑ ≋ ♫ ▣",149,79,13,0xff76e4b2,true);fill(0xff2ebf37,259,67,17+259,84);txt("OK",260,79,8,BLACK,true);}
  void home(){for(int i=0;i<12;i++){int row=i/2,col=i%2;float l=26+col*154,t=89+row*56;fill(i==selection?0xffd4d9e5:PANEL,l,t,l+153,t+55);stroke(0xff202943,l,t,l+153,t+55);
   center(icons[i],l+76,t+22,19,i==selection?0xff30a8a6:0xff4be3b9);
   center(menu[i],l+76,t+44,menu[i].length()>11?9:12,i==selection?BLACK:WHITE);}}
  void sideButtons(){
    // Five-key directional pad, matching the hardware controls below the Mayhem display.
    String[] keys={"▲","◀","OK","▶","▼"};
    int[] xs={167,116,167,218,167},ys={451,480,480,480,509};
    for(int i=0;i<5;i++){float x=xs[i],y=ys[i];fill(0xff6b79a5,x,y,x+45,y+28);stroke(0xffa6b7d6,x,y,x+45,y+28);center(keys[i],x+22,y+19,13,WHITE);}
  }
  void waterfall(){txt("RX DEMO  433.920 MHz",30,104,12,CYAN,true);txt("Sample:12k5  Format:C16",30,120,9,WHITE,false);
    fill(0xff1b2442,30,127,273,145);txt("1k       |       4k",38,140,10,WHITE,false);
    for(int y=0;y<120;y++)for(int x=0;x<120;x++){float v=waterfall[y][x];int col=Color.rgb((int)(12+v*65),(int)(32+v*115),(int)(65+v*170));fill(col,30+x*2,148+y*2,32+x*2,150+y*2);}
    for(int y=119;y>0;y--)System.arraycopy(waterfall[y-1],0,waterfall[y],0,120);
    for(int x=0;x<120;x++){float signal=(float)Math.exp(-Math.pow((x-62)/6f,2))*0.8f;waterfall[0][x]=Math.min(1,rng.nextFloat()*.22f+signal);}
    txt("DEMO — NO SDR DATA",42,409,13,0xffffff72,true);txt("Offline visual preview",35,430,11,WHITE,false);
    txt("◀ Back        ▶ Menu",34,466,11,CYAN,false);}
  void utility(){txt("UTILITIES",38,113,20,CYAN,true);String[] a={"RF wavelength calculator","Frequency converter","Stopwatch","Notes","Files / document picker","Device information"};for(int i=0;i<a.length;i++){fill(PANEL,31,131+i*49,272,173+i*49);txt(a[i],38,158+i*49,12,WHITE,false);}}
  void games(){txt("GAMES",40,116,21,CYAN,true);txt("NUMBER GUESS",39,153,14,WHITE,true);txt("Tap to roll a number 1–9",39,190,11,WHITE,false);txt("Your roll: "+(1+rng.nextInt(9)),45,250,22,CYAN,true);}
  void hardware(){txt("HARDWARE",39,115,20,CYAN,true);txt("Pluto SDR: optional",39,160,13,WHITE,false);txt("USB connection not required",39,185,10,CYAN,false);txt("Offline UI active",39,210,11,WHITE,false);}
  void settings(){txt("SETTINGS",39,115,20,CYAN,true);txt("Theme: PortaPack classic",39,160,12,WHITE,false);txt("Offline mode: enabled",39,188,12,WHITE,false);}
  void files(){txt("FILES",39,115,20,CYAN,true);txt("Tap to open Android files",39,160,11,WHITE,false);}
  void placeholder(){txt("SDR REQUIRED",39,115,20,CYAN,true);txt("Connect Pluto for RF features.",39,160,11,WHITE,false);txt("Offline menus still work.",39,185,11,WHITE,false);}
  @Override public boolean onTouchEvent(android.view.MotionEvent e){if(e.getAction()!=1)return true;float x=e.getX()/sx,y=e.getY()/sy;
    if(y>=451&&y<=537){
      int key=-1;
      if(y<480&&x>=167&&x<=212)key=0;
      else if(y>=509&&x>=167&&x<=212)key=4;
      else if(y>=480&&y<509){if(x>=116&&x<161)key=1;else if(x>=167&&x<212)key=2;else if(x>=218&&x<263)key=3;}
      if(key>=0){
        if(screen==0){if(key==0)selection=(selection+10)%12;else if(key==4)selection=(selection+2)%12;else if(key==1)selection=(selection+11)%12;else if(key==3)selection=(selection+1)%12;else activate(selection);}
        else if(key==1)screen=0;
        else if(key==2&&screen==6)openFiles();
        else if(key==2&&screen==1)demo=!demo;
        else if(key==3&&screen==7)screen=0;
        invalidate();return true;
      }
    }
    if(screen==0&&x>25&&x<336&&y>=89&&y<425){int row=(int)((y-89)/56),col=x<180?0:1;selection=Math.min(11,row*2+col);activate(selection);}
    else if(screen==2&&y>131&&y<425){int index=(int)((y-131)/49);if(index==4)openFiles();else if(index>=0&&index<6)Toast.makeText(MainActivity.this,"Utility: "+index+" — available in v0.2",Toast.LENGTH_SHORT).show();}
    else if(screen==6)openFiles();else if(screen==1&&y>445)screen=0;else if(screen==3)invalidate();
    invalidate();return true;}
  void openFiles(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("*/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,42);}
  void activate(int n){if(n==8)screen=2;else if(n==9)screen=3;else if(n==10)screen=5;else if(n==11)screen=4;else if(n==7||n==1)screen=1;else if(n==0)screen=6;else screen=7;invalidate();}
 }
}