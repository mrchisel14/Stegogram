package com.team.redacted.stegogram;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;

public class ReceiverActivity extends Activity {
    int PICK_IMAGE = 1;
    String path_name, password;
    EditText imageSelect, etpassword;
    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        setContentView(R.layout.activity_receiver);
        final Button selectButton = (Button)findViewById(R.id.sel);
        Button decodeButton = (Button)findViewById(R.id.decode);
        CheckBox view_password = (CheckBox)findViewById(R.id.view_password_receiver);
        imageSelect = (EditText)findViewById(R.id.pathname);
        etpassword = (EditText)findViewById(R.id.pwenter);
        if(Intent.ACTION_VIEW.equals(action) || intent.ACTION_GET_CONTENT.equals(action)){
            if(type.startsWith("image/")){
                RelativeLayout layout = (RelativeLayout)findViewById(R.id.receiver_body);
                layout.setVisibility(View.INVISIBLE);
                handleDecode(intent);
            }
        }
        else{


            selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectButtonOnClick();
                }
            });

            decodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decodeButtonOnClick();
                }
            });
            view_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        etpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else
                        etpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            });
        }

    }

    void selectButtonOnClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    void decodeButtonOnClick() {
        password = etpassword.getText().toString();

        if (path_name == null || path_name.matches("")) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT);
            return;
        }

        if (password ==  null || password.matches("")) {
            Toast.makeText(this, "No password specified", Toast.LENGTH_SHORT);
        }
        Utilities.createStegogramRequest(this, imageUri, password, null, Utilities.DECODE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Debug: ", "In On Activity Result");
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Log.d("Debug: ", "In Pick Image");
            if(resultCode == Activity.RESULT_OK) {
                Log.d("Debug: ", "Result OKay");
                path_name = Utilities.getRealPathFromURI(this, data.getData());
                imageUri = Uri.fromFile(new File(path_name));
                if(path_name != null) {
                    imageSelect.setText(path_name);
                    Log.d("Debug: ", "Path Name = " + path_name);
                }
                else
                    Toast.makeText(this, "Failed selecting image from file system", Toast.LENGTH_SHORT).show();
            }
        }

    }
    void handleDecode(Intent intent){
        imageUri = Uri.fromFile(new File(Utilities.getRealPathFromURI(this, intent.getData())));
        DecodeSelectPasswordFragment f = new DecodeSelectPasswordFragment();
        f.imageUri = imageUri;
        f.show(getFragmentManager(), "Decode");
    }
}
