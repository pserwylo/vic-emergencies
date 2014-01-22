package com.serwylo.emergencies.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.webkit.WebView;

import com.serwylo.emergencies.data.Incident;

public final class AdviceDialog {

	private AdviceDialog() {}

	public static Dialog showAdviceIfAvailable( Context context, Incident incident ) {
		String url = incident.getDetailsUrl();
		Dialog dialog = null;
		if ( url != null ) {
			WebView adviceView = new WebView( context );
			adviceView.loadUrl( url );
			dialog = new AlertDialog.Builder( context )
				.setTitle( "Advice from " + incident.getAgencyShort() )
				.setView( adviceView )
				.setCancelable( true )
				.setPositiveButton( "Close", null )
				.create();
			dialog.show();
		}
		return dialog;
	}

}
