package com.serwylo.emergencies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

	public boolean isOnline( Context context ) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return ( netInfo != null && netInfo.isConnectedOrConnecting() );
	}

	public static void refreshTheme( Activity activity ) {
		int theme = PrefHelper.get().theme().equals( PrefHelper.PREF_THEME_DARK )
			? R.style.IncidentThemeDark
			: R.style.IncidentThemeLight;
		activity.setTheme( theme );
	}

}
