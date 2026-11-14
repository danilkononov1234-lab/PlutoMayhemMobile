package org.mayhem.port;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

/** Offline touchscreen shell; NOT the upstream C++ firmware UI. */
public class MainActivity extends Activity {
    private MayhemView display;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(5894 | 1024 | 512);
        display = new MayhemView();
        setContentView(display);
    }
    @Override public void onBackPressed() {
        if (display.page != 0) { display.page = 0; display.invalidate(); }
        else super.onBackPressed();
    }
    private class MayhemView extends View {
        final Paint p = new Paint(3);
        final String[] tabs = {"RX", "TX", "GAMES", "UTIL", "SET"};
        final String[][] items = {
            {"Audio", "Spectrum", "Scanner", "Capture", "Looking Glass", "Replay"},
            {"Transmit (disabled)", "Replay TX (disabled)", "Signal Generator (disabled)"},
            {"Games", "Offline tools"},
            {"Calculator", "File browser (planned)", "Clock", "About"},
            {"Display", "Storage", "Radio", "About"}
        };
        int page = 0, tab = 0, selected = -1;
        MayhemView() { super(MainActivity.this); setLayerType(View.LAYER_TYPE_SOFTWARE, null); }
        void rect(Canvas c, int color, float l, float t, float r, float b) {
            p.setColor(color); p.setStyle(Paint.Style.FILL); c.drawRect(l,t,r,b,p);
        }
        void text(Canvas c, String s, float x, float y, int size, int color) {
            p.setColor(color); p.setStyle(Paint.Style.FILL); p.setTextSize(size); p.setTypeface(android.graphics.Typeface.MONOSPACE); c.drawText(s,x,y,p);
        }
        @Override protected void onDraw(Canvas original) {
            Canvas c = original;
            c.save();
            float sx = getWidth()/320f, sy = getHeight()/480f;
            c.scale(sx,sy);
            rect(c,0xff10151c,0,0,320,480);
            rect(c,0xff273747,0,0,320,42);
            text(c,"MAYHEM  /  ANDROID",12,27,18,0xfff1f6fa);
            rect(c,0xff293139,0,42,320,76);
            text(c,"OFFLINE  •  NO SDR",12,65,15,0xffffce74);
            for(int i=0;i<5;i++) {
                float x=i*64;
                rect(c,i==tab?0xff386481:0xff1d2731,x,80,x+63,120);
                text(c,tabs[i],x+9,105,14,0xffe6f0f7);
            }
            text(c,page==0?"HOME / "+tabs[tab]:items[tab][selected],12,153,17,0xff9bd9ff);
            if(page==0) {
                for(int i=0;i<items[tab].length;i++) {
                    int y=168+i*39;
                    rect(c,i%2==0?0xff222c37:0xff19222b,10,y,310,y+35);
                    text(c,items[tab][i],18,y+24,15,0xffe5edf4);
                    text(c,">",290,y+24,15,0xff92bdd3);
                }
            } else {
                rect(c,0xff202d38,12,176,308,375);
                text(c,"OFFLINE PREVIEW",25,213,19,0xff9bd9ff);
                text(c,"Original Mayhem C++ UI",25,250,14,0xfff1f6fa);
                text(c,"integration in progress.",25,274,14,0xfff1f6fa);
                text(c,"SDR operations unavailable.",25,309,13,0xffffce74);
                text(c,"Tap BACK below.",25,349,14,0xffb5cbd9);
            }
            rect(c,0xff273747,0,427,320,480);
            text(c,page==0?"TOUCH MENU  |  NO HARDWARE":"< BACK",15,458,16,0xffe3edf6);
            c.restore();
        }
        @Override public boolean onTouchEvent(MotionEvent e) {
            if(e.getAction()!=MotionEvent.ACTION_UP) return true;
            float x=e.getX()*320f/getWidth(), y=e.getY()*480f/getHeight();
            if(y>427){page=0;invalidate();return true;}
            if(y>=80 && y<=120) {tab=Math.min(4,(int)x/64);page=0;invalidate();return true;}
            if(page==0 && y>=168) {
                int i=(int)(y-168)/39;
                if(i>=0 && i<items[tab].length && y<168+i*39+35) {selected=i;page=1;}
            }
            invalidate();return true;
        }
    }
}
