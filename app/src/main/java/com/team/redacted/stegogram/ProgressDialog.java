package com.team.redacted.stegogram;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Shawn on 10/16/2015.
 */
public class ProgressDialog extends DialogFragment {
    Activity a;
    Uri image_uri;
    String password, message, recipients;
    int type;
    View view;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.dialog_progress, null);

        builder.setView(view);
        return builder.create();
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.d("Debug", "Calling performRequest");
        Utilities.performRequest(this, a, recipients, image_uri, password, message, type);
    }
    public void setArgs(final Activity a, final String recs, final Uri image_uri, String password, String message, final int type){
        this.a = a;
        this.image_uri = image_uri;
        this.password = password;
        this.message = message;
        this.type = type;
        this.recipients = recs;
    }
}
