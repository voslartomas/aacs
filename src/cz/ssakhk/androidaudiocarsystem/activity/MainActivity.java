package cz.ssakhk.androidaudiocarsystem.activity;

import java.net.URISyntaxException;

import cz.ssakhk.androidaudiocarsystem.R;
import cz.ssakhk.androidaudiocarsystem.helper.TrackPoint;
import cz.ssakhk.androidaudiocarsystem.helper.TrackPointItem;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author 
 *
 */
public class MainActivity extends Activity implements SensorEventListener, LocationListener {
	
	/**
	 * Index preferenci.
	 */
	public static final String PREFS_NAME = "AACSPrefs";
	
	/**
	 * Manager a listener pro geolokaci.
	 */
	private LocationManager lm = null;
	private LocationListener ls = null;
	
	/**
	 * Sensor a jeho manager pro urceni smeru.
	 */
	private SensorManager sm;
	private Sensor s;
	
	/**
	 * Jednotky pro zobrazeni rychlosti.
	 */
	private int units = 0;
	
	/**
	 * Horizontalni smer.
	 */
	private float azimuth = 0;
	
	/**
	 * Nastaveni aplikace, SharedPreferences.
	 */
	private SharedPreferences settings;
	
	/**
	 * Ukazatel prave zvoleneho tlacitka pro kontextove menu.
	 */
	private View lastButtonSelected;
	
	/**
	 * 
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		
		settings = getSharedPreferences(PREFS_NAME, 0);
		
		registerForContextMenu(findViewById(R.id.button1));
		registerForContextMenu(findViewById(R.id.button2));
		registerForContextMenu(findViewById(R.id.button3));
		registerForContextMenu(findViewById(R.id.button4));
		registerForContextMenu(findViewById(R.id.button5));
		
		this.setButtonsAction();
		
		units = settings.getInt("units", 0);
		
		// Acquire a reference to the system Location Manager
		lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		// Register the listener with the Location Manager to receive location updates
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		
		sm = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
	    s = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	    sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    lastButtonSelected = v;
	    inflater.inflate(R.menu.button_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	        case R.id.delete:
	        	deleteButtonAssertion(lastButtonSelected);
	            return true;
	        case R.id.new_pick:
	            
	        	int id = deleteButtonAssertion(lastButtonSelected);
	        	buttonAction(id);
	        	
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	private int deleteButtonAssertion(View buttonId){
		int selected = 0;
		
		if(buttonId == findViewById(R.id.button1))
			selected = 1;
		else if (buttonId == findViewById(R.id.button2))
			selected = 2;
		else if (buttonId == findViewById(R.id.button3)) 
			selected = 3;
		else if (buttonId == findViewById(R.id.button4)) 
			selected = 4;
		else if (buttonId == findViewById(R.id.button5)) 
			selected = 5;
		
		settings.edit().remove("button" + selected).commit();
		settings.edit().remove("buttonTitle" + selected).commit();
		
		Context context = getApplicationContext();
		CharSequence text = "Tlačítko " + String.valueOf(selected) + " resetováno.";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
		setButtonsAction();
		return selected;
	}
	
	/**
	 * 
	 */
	private void setButtonsAction() {
		
		Button btn1 = (Button) this.findViewById(R.id.button1);
		btn1.setText(getButtonsTitle(1));
		btn1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				buttonAction(1);
			}
		});
		
		Button btn2 = (Button) this.findViewById(R.id.button2);
		btn2.setText(getButtonsTitle(2));
		btn2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				buttonAction(2);
			}
		});
		
		Button btn3 = (Button) this.findViewById(R.id.button3);
		btn3.setText(getButtonsTitle(3));
		btn3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				buttonAction(3);
			}
		});
		
		Button btn4 = (Button) this.findViewById(R.id.button4);
		btn4.setText(getButtonsTitle(4));
		btn4.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				buttonAction(4);
			}
		});
		
		Button btn5 = (Button) this.findViewById(R.id.button5);
		btn5.setText(getButtonsTitle(5));
		btn5.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				buttonAction(5);
			}
		});
	}
	
	private String getButtonsTitle(int buttonId){
		
		if(settings.contains("buttonTitle" + buttonId)){
			return settings.getString("buttonTitle" + buttonId, null);
		}else{
			return (String) getResources().getText(R.string.button_default_title);
		}
	}
	
	/**
	 * 
	 * @param buttonId
	 */
	private void buttonAction(int buttonId){
		
		if(!settings.contains("button" + buttonId))
			getApplicationPicker(buttonId);
		else
			startActivity(findIntentByURI(settings.getString("button" + buttonId, null)));
	}
	
	/**
	 * 
	 * @param uri
	 * @return Intent|NULL
	 */
	private Intent findIntentByURI(String uri){
		
		try {
			return Intent.getIntent(uri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param buttonId
	 */
	private void getApplicationPicker(int buttonId){
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);            
        Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
        pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
        startActivityForResult(pickIntent, buttonId);
	}
	
	/**
	 * 
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode > 0) {
			 // vsechno v poradku
		     if(resultCode == RESULT_OK){
		    	 
		    	 ApplicationInfo app = null;
		    	 try {
					app = getPackageManager().getApplicationInfo(data.getComponent().getPackageName(), 0);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	 
		    	 SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		         SharedPreferences.Editor editor = settings.edit();
		         editor.putString("button" + String.valueOf(requestCode), data.toURI());
		         editor.putString("buttonTitle" + String.valueOf(requestCode), app.loadLabel(getPackageManager()).toString());
		         editor.commit();
		         
		         setButtonsAction();
		     }
			if (resultCode == RESULT_CANCELED) {
				// osetrit pokud se nic nevrati
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_main, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_settings:
	            
	        	Intent s = new Intent(this, PreferencesActivity.class);
	            startActivity(s);
	            
	            return true;
	            
	       case R.id.quit:
	    	   finish();
	    	   return true;
		default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		azimuth = event.values[0];
		updateCompass();
	}
	
	private void updateCompass(){
		ImageView iv = (ImageView) this.findViewById(R.id.imageView1);
		Matrix mat = new Matrix();
		Bitmap bMap = BitmapFactory.decodeResource(getResources(),R.drawable.compass);
		mat.postRotate(360 - azimuth);
		Bitmap bMapRotate = Bitmap.createBitmap(bMap, 0, 0,bMap.getWidth(),bMap.getHeight(), mat, true);
		iv.setImageBitmap(bMapRotate);
	}
	
	private void updateLocation(Location location) {
		
		TrackPointItem tpi = new TrackPointItem(location.getLongitude(), location.getLatitude(), location.getTime());
		
		TrackPoint tp = new TrackPoint();
		tp.addPoint(tpi);
		
		float speedFinal = 0;
		
		if(location.getSpeed() == 0.0){
			speedFinal = tp.getSpeed();
		}else{
			speedFinal = location.getSpeed();
		}
		
		TextView speed = (TextView) this.findViewById(R.id.title_speed);
		speed.setText(tp.calculateInto(speedFinal, units));
	}

	public void onResume(Bundle savedInstanceState){
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ls);
		sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void onPause(Bundle savedInstanceState){
		lm.removeUpdates(ls);
		sm.unregisterListener(this);
		
	}

	@Override
	public void onLocationChanged(Location location) {
		 // pokud je nova hodnota lokace, zavolame metodu pro aktualizaci pozice 
	     updateLocation(location);
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
