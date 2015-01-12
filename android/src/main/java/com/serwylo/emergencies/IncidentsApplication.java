package com.serwylo.emergencies;

import android.app.*;

public class IncidentsApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		PrefHelper.setup( getApplicationContext() );
	}

}
