package com.alok.georeporter;

import com.alok.georeporter.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ReportMap extends MapActivity {

	MapView map;
	Button return_button;
	ImageView reticle;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);
        map=(MapView)findViewById(R.id.map);
        map.setBuiltInZoomControls(true);
        return_button=(Button)findViewById(R.id.return_button);
        reticle=(ImageView)findViewById(R.id.reticle_view);
        
        return_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GeoPoint p=map.getMapCenter();
				Intent data=new Intent();
				data.putExtra("ret_lat", p.getLatitudeE6());
				data.putExtra("ret_long", p.getLongitudeE6());
				setResult(Activity.RESULT_OK,data);
				finish();
			}
        });
//        Toast tst=Toast.makeText(this, "", Toast.LENGTH_LONG);
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}