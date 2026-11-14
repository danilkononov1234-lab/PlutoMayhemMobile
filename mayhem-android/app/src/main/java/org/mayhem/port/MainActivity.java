package org.mayhem.port;
import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
public class MainActivity extends Activity {
 @Override public void onCreate(Bundle state) { super.onCreate(state); TextView view = new TextView(this); view.setBackgroundColor(Color.rgb(15,20,24)); view.setTextColor(Color.WHITE); view.setGravity(Gravity.CENTER); view.setTextSize(19); view.setPadding(32,32,32,32); view.setText("Mayhem Android\n\nDevelopment scaffold only.\nOriginal Mayhem UI is not yet integrated.\nNo SDR required."); setContentView(view); }
}
