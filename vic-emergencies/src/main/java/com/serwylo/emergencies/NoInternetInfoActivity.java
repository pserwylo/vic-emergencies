package com.serwylo.emergencies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class NoInternetInfoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alternate_info);
		setTitle( "Emergency Information" );
		getSupportActionBar().setDisplayHomeAsUpEnabled( true );
    }

	protected void onCheckInternet() {
		finish();

		Intent intent = new Intent( this, IncidentsActivity.class );
		startActivity( intent );
	}

}
