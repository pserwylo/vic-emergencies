package com.serwylo.emergencies;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class AlternateInfoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alternate_info);
		setTitle( "Emergency Information" );

		getSupportActionBar().setDisplayHomeAsUpEnabled( true );
    }

}
