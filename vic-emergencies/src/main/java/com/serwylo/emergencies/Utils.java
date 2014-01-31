package com.serwylo.emergencies;

import android.app.*;

public class Utils {

	public static void refreshTheme( Activity activity ) {
		int theme = PrefHelper.get().theme().equals( PrefHelper.PREF_THEME_DARK )
			? R.style.IncidentThemeDark
			: R.style.IncidentThemeLight;
		activity.setTheme( theme );
	}

}
