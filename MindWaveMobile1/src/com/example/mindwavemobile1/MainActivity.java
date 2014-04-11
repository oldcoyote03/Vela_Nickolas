/* 
 * HelloEEG
 * From guide pdf */

package com.example.mindwavemobile1;

import android.os.Bundle;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.widget.Button;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import java.util.ArrayList;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.neurosky.thinkgear.TGDevice;
import android.bluetooth.BluetoothAdapter;
//import android.util.Log;

public class MainActivity extends Activity {

	Button clearButton, mwButton, plotButton;
	TGDevice tgDevice;
	BluetoothAdapter btAdapter;

	private XYPlot plot;
	private SimpleXYSeries attnSeries;
	private SimpleXYSeries mdtnSeries;
	private boolean plotting = false;
	private Thread plot_t;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Configure and draw the plot canvas
        plot = (XYPlot) findViewById(R.id.mwmPlot);
        attnSeries = new SimpleXYSeries(
        		new ArrayList<Number>(),
        		SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
        		"Attention");
        mdtnSeries = new SimpleXYSeries(
        		new ArrayList<Number>(),
        		SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
        		"Meditation");
        plot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        plot.setDomainBoundaries(0, 0, BoundaryMode.AUTO);
    
        LineAndPointFormatter attnSeriesFormat = new LineAndPointFormatter();
        attnSeriesFormat.setPointLabelFormatter(new PointLabelFormatter());
        attnSeriesFormat.configure(getApplicationContext(), 
        		R.layout.line_point_formatter_with_plf1);
        plot.addSeries(attnSeries, attnSeriesFormat);
        
        LineAndPointFormatter mdtnSeriesFormat = new LineAndPointFormatter();
        mdtnSeriesFormat.setPointLabelFormatter(new PointLabelFormatter());  
        mdtnSeriesFormat.configure(getApplicationContext(), 
        		R.layout.line_point_formatter_with_plf2);
        plot.addSeries(mdtnSeries, mdtnSeriesFormat);

        plot.setDomainStepValue(2);
        plot.setTicksPerDomainLabel(1);
        plot.setRangeStepValue(11);
		plot.setTicksPerRangeLabel(2);

		/* Bluetooth connection starts here */
    	btAdapter =  BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null) {
			Toast.makeText(getApplicationContext(), "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		} else if (btAdapter != null) {
			tgDevice = new TGDevice(btAdapter, handler);
		}

    	mwButton = (Button) findViewById(R.id.mw_button);
    	mwButton.setOnClickListener(new OnClickListener() {
    		
    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			if (btAdapter.isEnabled()) {
    				mwConnect(v);
    			}
    		} // onClick	
    	});

    	plotButton = (Button) findViewById(R.id.plot_button);
    	plotButton.setEnabled(false);
    	plotButton.setOnClickListener(new OnClickListener() {
    		
    		@Override
    		public void onClick(View v) {
    			try {
					plotListener(v);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
       		}
    	});

    	clearButton = (Button) findViewById(R.id.clear_button);
    	clearButton.setEnabled(false);
    	clearButton.setOnClickListener(new OnClickListener() {
    		
    		@Override
    		public void onClick(View v) {
    			attnSeries.setModel(new ArrayList<Number>(), 
    	        		SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
    	        mdtnSeries.setModel(new ArrayList<Number>(), 
    	        		SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
    	    	if ( !plotting ) { 
    	    		plot.redraw();
    	    		clearButton.setEnabled(false); }
    		}
    	});
    } // onCreate
    
    private void mwConnect(View view) {
    	if (tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED) {
    		tgDevice.connect(true);
    		mwButton.setText("Disconnect");
    	} else {
        	tgDevice.close();
        	mwButton.setText("Connect");
        	plotButton.setEnabled(false);
        	if (plotting) { 
        		try {
					plotListener(view);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
    	} // else
    } // wmConnect

    private void plotListener(View view) throws InterruptedException {
    	if ( !plotting ) {
    		plotButton.setText("Stop");
    		clearButton.setEnabled(true);
    		plotting = true;
            plot_t = new Thread(new MWPlot());
    		plot_t.start();
    	} else {
    		plotButton.setText("Plot");
    		plotting = false;
    		plot_t.join();
    	}
    } // plotListener

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
    			case TGDevice.MSG_STATE_CHANGE:
    				switch (msg.arg1){
    					case TGDevice.STATE_IDLE:
    						break;
    					case TGDevice.STATE_CONNECTING:
    						Toast.makeText(getApplicationContext(), "Connecting..",
    								Toast.LENGTH_LONG).show();
    						break;
    					case TGDevice.STATE_CONNECTED:
    						Toast.makeText(getApplicationContext(), "Connected",
    								Toast.LENGTH_LONG).show();
    						tgDevice.start();
    			        	plotButton.setEnabled(true);
    						break;
    					case TGDevice.STATE_DISCONNECTED:
    						Toast.makeText(getApplicationContext(), "Disconnected",
    								Toast.LENGTH_LONG).show();
    						break;
    					case TGDevice.STATE_NOT_FOUND:
    						Toast.makeText(getApplicationContext(), "Not Found",
    								Toast.LENGTH_LONG).show();
    						break;
    					case TGDevice.STATE_NOT_PAIRED:
    						Toast.makeText(getApplicationContext(), "Not Paired",
    								Toast.LENGTH_LONG).show();
    					default:
    					break;
    				} // switch msg.arg1
    			break;
    			case TGDevice.MSG_ATTENTION:
    	    		if(plotting) {
    	    			attnSeries.addLast(null, msg.arg1);
    	    		}
    			break;
    			case TGDevice.MSG_MEDITATION:
    				if(plotting) {
    					mdtnSeries.addLast(null, msg.arg1);
    				}
    			break;

    		} // switch msg.what
    	} // handlMessage
    }; // Handler

	private class MWPlot implements Runnable {

		@Override
		public void run() {
			while ( plotting ) {
				try {
					Thread.sleep(1000);
					plot.redraw();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} // run
	} // MWPlot

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

} // MainActivity

/*

    			case TGDevice.MSG_EEG_POWER:
    				TGEegPower ep = (TGEegPower)msg.obj;
    				tv.append("Delta: " + ep.delta);
    				tv.append("Delta: " + ep.theta);
    				tv.append("Delta: " + ep.lowAlpha);
    				tv.append("Delta: " + ep.highAlpha);
    				tv.append("Delta: " + ep.lowBeta);
    				tv.append("Delta: " + ep.highBeta);
    				tv.append("Delta: " + ep.lowGamma);
    				tv.append("Delta: " + ep.midGamma);
    			break;

*/
