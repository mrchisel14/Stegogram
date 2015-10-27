package com.team.redacted.stegogram;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

public class ReceiverActivity extends AppCompatActivity {
    int PICK_IMAGE = 1;
    String path_name, password;
    EditText imageSelect, etpassword;
    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        final CheckBox checkb = (CheckBox)findViewById(R.id.view_password);
        final Button selectButton = (Button)findViewById(R.id.sel);
        Button decodeButton = (Button)findViewById(R.id.decode);
        imageSelect = (EditText)findViewById(R.id.pathname);
        etpassword = (EditText)findViewById(R.id.pwenter);

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

        checkb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    etpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else
                    etpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_receiver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void selectButtonOnClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    void decodeButtonOnClick() {
        path_name = imageSelect.getText().toString();
        password = imageSelect.getText().toString();

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
        Log.d("Debug: ", "In On Activity Result. Result Code = " + resultCode);
        if (requestCode == PICK_IMAGE) {
            Log.d("Debug: ", "In Pick Image");
            if(resultCode == Activity.RESULT_OK) {
                Log.d("Debug: ", "Result OKay");
                imageUri = data.getData();
                path_name = Utilities.getRealPathFromURI(this, data.getData());
                if(path_name != null) {
                    imageSelect.setText(path_name);
                    Log.d("Debug: ", "Path Name = " + path_name);
                }
                else
                    Toast.makeText(this, "Failed selecting image from file system", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "Failed retrieving data from activity", Toast.LENGTH_SHORT).show();
        }

    }

}
