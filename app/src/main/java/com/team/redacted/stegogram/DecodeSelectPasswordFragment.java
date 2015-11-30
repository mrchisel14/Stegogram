package com.team.redacted.stegogram;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Shawn on 11/28/2015.
 */
public class DecodeSelectPasswordFragment extends DialogFragment {
    EditText password_box;
    Bitmap png;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_decode, null);
        Button close_button = (Button)v.findViewById(R.id.decode_close_button);
        Button decode_button = (Button)v.findViewById(R.id.decode_button);
        CheckBox view_password = (CheckBox)v.findViewById(R.id.decode_view_password_receiver);
        password_box = (EditText)v.findViewById(R.id.decode_password);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                getActivity().finish();
            }
        });
        decode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decodeMessage();
            }
        });
        view_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    password_box.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else
                    password_box.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });
        builder.setView(v);
        return builder.create();
    }
    void decodeMessage(){
        String password = password_box.getText().toString();
        if(png == null){
            Toast.makeText(getActivity(), "Failed opening image", Toast.LENGTH_SHORT);
            dismiss();
        }
        if (password == null || password.matches("")) {
            Toast.makeText(getActivity(), "No password specified", Toast.LENGTH_SHORT);
        }
        Utilities.createStegogramRequest(getActivity(), png, password, null, Utilities.DECODE_IMAGE);
        dismiss();
    }
}
