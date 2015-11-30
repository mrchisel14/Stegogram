package com.team.redacted.stegogram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class SenderActivity extends Activity{
    EditText password, message_box, path;
    String MAX_COUNT = "255", password_str, message, path_name = null;
    Uri imageUri = null, fileUri = null;
    Bitmap png_bitmap;
    int PICK_IMAGE = 1, CAPTURE_IMAGE = 2, compression_length = 0;
    TextView message_count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        ImageButton select_image = (ImageButton)findViewById(R.id.select_file);
        ImageButton take_photo = (ImageButton)findViewById(R.id.take_picture_button);
        CheckBox view_password = (CheckBox)findViewById(R.id.view_password);
        Button send_button = (Button)findViewById(R.id.send_button);
        message_count = (TextView) findViewById(R.id.message_count);
        path = (EditText)findViewById(R.id.select_path);
        message_box = (EditText)findViewById(R.id.message);
        password = (EditText)findViewById(R.id.password);
        if(Intent.ACTION_VIEW.equals(action) || intent.ACTION_GET_CONTENT.equals(action)){
            if(type.startsWith("image/")){
                path_name = Utilities.getRealPathFromURI(this, intent.getData());
                imageUri = Uri.fromFile(new File(path_name));
                select_image.setVisibility(View.INVISIBLE);
                take_photo.setVisibility(View.INVISIBLE);
                path.setText(path_name);
                path.setInputType(InputType.TYPE_NULL);
            }
        }
        else if(intent.ACTION_SEND.equals(action)){
            path_name = Utilities.getRealPathFromURI(this, (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM));
            imageUri = Uri.fromFile(new File(path_name));
            select_image.setVisibility(View.INVISIBLE);
            take_photo.setVisibility(View.INVISIBLE);
            path.setText(path_name);
            path.setInputType(InputType.TYPE_NULL);
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        compression_length = Integer.parseInt(prefs.getString("compression_type", "273"));

        message_count.setText("0/"+MAX_COUNT);
        select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Search for image in file system and update path
                getFilePath();
            }
        });
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call take picture activity and update path name
                getFilePathTakePicture();
            }
        });

        final TextWatcher textWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                message_count.setText(String.valueOf(s.length()) + " / " + MAX_COUNT);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                message_count.setText(String.valueOf(s.length()) + " / " + MAX_COUNT);
            }

            public void afterTextChanged(Editable s) {
            }
        };
        message_box.addTextChangedListener(textWatcher);
        view_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendButtonClick();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Debug: ", "In On Activity Result. Result Code = " + resultCode);
        if (requestCode == PICK_IMAGE) {
            Log.d("Debug: ", "In Pick Image");
            if(resultCode == Activity.RESULT_OK) {
                Log.d("Debug: ", "Result OKay");
                path_name = Utilities.getRealPathFromURI(this, data.getData());
                imageUri = Uri.fromFile(new File(path_name));
                png_bitmap = getPngBitmap(this);
                int calculated = png_bitmap.getWidth()*png_bitmap.getHeight()-50;
                InputFilter[] fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(calculated);
                message_box.setFilters(fArray);
                MAX_COUNT = String.valueOf(calculated);
                if(Integer.parseInt(MAX_COUNT) > 1000){
                    MAX_COUNT = String.valueOf(1000);
                }
                Log.d("Debug", "Image Uri: "+ imageUri.toString());
                if(path_name != null) {
                    path.setText(path_name);
                    Log.d("Debug: ", "Path Name = " + path_name);
                }
                else
                    Toast.makeText(this, "Failed selecting image from file system", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == CAPTURE_IMAGE) {
            Log.d("Debug: ", "In Capture Image");
            if(resultCode == Activity.RESULT_OK) {
                Log.d("Debug: ", "In Capture Image Result Okay");
                try {
                    imageUri = fileUri;
                    path_name = fileUri.getPath();
                    png_bitmap = getPngBitmap(this);
                    int calculated = png_bitmap.getWidth()*png_bitmap.getHeight()-50;
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(calculated);
                    message_box.setFilters(fArray);
                    MAX_COUNT = String.valueOf(calculated);
                    if(Integer.parseInt(MAX_COUNT) > 1000){
                        MAX_COUNT = String.valueOf(1000);
                    }
                    if (path_name != null && imageUri != null) {
                        Log.d("Debug: ", "Pathname  = " + path_name);
                        path.setText(path_name);
                    } else
                        Toast.makeText(this, "Failed selecting image from Camera", Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    void onSendButtonClick() {
        message = message_box.getText().toString();
        password_str = password.getText().toString();
        if(message == null || message.matches("")){
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password_str == null || password_str.matches("")){
            Toast.makeText(this, "No password. Message will not be encrypted.", Toast.LENGTH_SHORT).show();
        }
        if(path_name == null){
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }
        message = message_box.getText().toString();
        password_str = password.getText().toString();
        Utilities.MAX_COMPRESSION_WIDTH = compression_length;
        Utilities.createStegogramRequest(this, png_bitmap, password_str, message, Utilities.ENCODE_IMAGE);
    }
    void getFilePath(){
        //Open file manager grab path of selected image ensure image is png
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }
    void getFilePathTakePicture(){
        //open camera return path for picture taken
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = Utilities.getOutputMediaFileUri(this); // create a file to save the image
        if(fileUri == null){
            Log.d("Debug", "Error creating fileuri for camera intent");
            Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("Debug", "fileUri = " + fileUri.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE);
    }
    Bitmap getPngBitmap(final Context c){
        Bitmap ret = Utilities.convertJPEGToPNG(c, BitmapFactory.decodeFile(imageUri.getPath()));
        return ret;
    }
}
