package com.serwylo.emergencies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.*;
import android.support.v7.app.ActionBarActivity;
import android.view.*;

public class NoInternetInfoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_internet);
		setTitle( "Emergency Information" );
		getSupportActionBar().setDisplayHomeAsUpEnabled( true );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.no_internet_info_menu, menu);
		MenuItemCompat.setShowAsAction( menu.findItem( R.id.menu_check_internet ), MenuItemCompat.SHOW_AS_ACTION_ALWAYS );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_check_internet:
                checkInternet();
                return true;
        }
        return false;
    }
	protected void checkInternet() {
		finish();

		Intent intent = new Intent( this, IncidentsActivity.class );
		startActivity( intent );
	}

}
