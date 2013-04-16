package eu.trentorise.smartcampus.puschservice.test;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import eu.trentorise.smartcampus.puschservice.PushServiceActivity;

public class MainActivity extends PushServiceActivity {

	Button button;

	@SuppressWarnings("static-access")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		String serverurl="http://192.168.41.190:8081/communicator/";
		String senderid="557126495282";
		String appName= getApplicationContext()
				.getPackageName();
		
		super.onCreate(savedInstanceState);
		super.init(serverurl,senderid,appName);
		
	//	setContentView(R.layout.activity_main);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
