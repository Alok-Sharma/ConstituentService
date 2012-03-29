package com.alok.georeporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.alok.georeporter.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ReporterHome extends Activity {
	Uri imageUri;
	final int TAKE_PICTURE=1;
	final int GET_LOCATION=2;
	TextView maptext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button mapbutton=(Button)findViewById(R.id.button2);
		Button camerabutton=(Button)findViewById(R.id.button1);
		Button servicelist=(Button)findViewById(R.id.button3);
		maptext=(TextView)findViewById(R.id.textView2);

		mapbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i=new Intent(ReporterHome.this, ReportMap.class);
				startActivityForResult(i, GET_LOCATION);
			}
		});

		camerabutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
				intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photo));
				imageUri = Uri.fromFile(photo);
				startActivityForResult(intent, TAKE_PICTURE);
			}
		});
		
		servicelist.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doInBack bckgrnd=new doInBack();
				bckgrnd.execute("https://open311.sfgov.org/dev/v2/services.xml?jurisdiction_id=sfgov.org");
			}
		});
	}
	
	/*
	 * This will do the fetching in background. UI will remain responsive. 
	 * URI must be given as parameter.
	 */
	private class doInBack extends AsyncTask<String, Void , String>{
		@Override
		protected String doInBackground(String... params) {
			getHttpResponse(params[0]);
			return null;
		}
	}

	/*
	 * Given the URI as an argument, this function performs a GET on the URI and dumps the output
	 * to the log.
	 */
	public String getHttpResponse(String uri) {
		Log.d("!!!!!!!", "get request here");
		BufferedReader reader=null;
		StringBuffer sb=new StringBuffer("");
		String inputline="";
		try{
			DefaultHttpClient client=new DefaultHttpClient();
			HttpGet get_request=new HttpGet();
			get_request.setURI(new URI(uri));
			HttpResponse response=client.execute(get_request);
			reader=new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			while((inputline=reader.readLine())!=null){
				sb.append(inputline+"\n");
			}
			reader.close();
			String outputpage=sb.toString();
			System.out.println(outputpage);
			
		}catch(Exception e){
			Log.e("!!!!!!!", e.getMessage());
		}
		return null;
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case TAKE_PICTURE:
			if (resultCode == Activity.RESULT_OK) {
				Uri capturedImage = imageUri;
				getContentResolver().notifyChange(capturedImage, null);
				ImageView imageView = (ImageView) findViewById(R.id.ImageView);
				ContentResolver cr = getContentResolver();
				Bitmap bitmap;
				try {
					bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, capturedImage);
					imageView.setImageBitmap(bitmap);
					Toast.makeText(this, capturedImage.toString(),Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
					Log.e("Camera", e.toString());
				}
			}
			break;
		case GET_LOCATION:
			int lat=data.getExtras().getInt("ret_lat");
			int lon=data.getExtras().getInt("ret_long");
			maptext.setText("Latitude: "+lat+" Longitude: "+lon);
			break;
		default:
			Log.d("!!!!!!!!!", "error");
		}
	}

}


