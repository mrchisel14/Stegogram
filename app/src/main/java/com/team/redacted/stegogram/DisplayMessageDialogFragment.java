package com.team.redacted.stegogram;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Shawn on 10/12/2015.
 */
public class DisplayMessageDialogFragment extends DialogFragment {
    String decoded_message = null;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_decoded_message, null);
        Button close_button = (Button)v.findViewById(R.id.close_button);
        TextView message = (TextView)v.findViewById(R.id.decoded_message);
        if(decoded_message != null)
            message.setText(decoded_message);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        builder.setView(v);
        return builder.create();
    }

}
