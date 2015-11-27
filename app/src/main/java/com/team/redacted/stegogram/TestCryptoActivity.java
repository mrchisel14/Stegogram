package com.team.redacted.stegogram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Shawn on 11/27/2015.
 */
public class TestCryptoActivity extends Activity {
    int PICK_IMAGE = 0;
    String path_name, message = "Hello World";
    Uri imageUri;
    EditText path;
    Bitmap original;
    Bitmap encoded_image;
    String decoded_message;
    Context a = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_crypto);
        Button test_button = (Button)findViewById(R.id.test_button);
        Button sel_button = (Button)findViewById(R.id.test_sel);
        path = (EditText)findViewById(R.id.test_pathname);
        sel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Search for image in file system and update path
                getFilePath();
            }
        });
        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });

    }
    void getFilePath(){
        //Open file manager grab path of selected image ensure image is png
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
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
                ImageView before = (ImageView) findViewById(R.id.before_image);
                original = Utilities.convertJPEGToPNG(a, BitmapFactory.decodeFile(imageUri.getPath()));
                before.setImageBitmap(original);
                Log.d("Debug", "Image Uri: "+ imageUri.toString());
                if(path_name != null) {
                    path.setText(path_name);
                    Log.d("Debug: ", "Path Name = " + path_name);
                }
                else
                    Toast.makeText(this, "Failed selecting image from file system", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "Failed retrieving data from activity", Toast.LENGTH_SHORT).show();
        }
    }
    void test(){
        new AsyncTask<String, Void, Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(String... params) {
                // **Code**
                try{
                    encoded_image = CryptoEngine.generateStegogram(null, message, original);
                    decoded_message = CryptoEngine.receiveStegogram(encoded_image);

                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
            protected void onProgressUpdate(Void... progress) {
                super.onProgressUpdate();

            }

            protected void onPostExecute(Void result) {
                ImageView after = (ImageView)findViewById(R.id.after_image);
                TextView message_box = (TextView)findViewById(R.id.test_message);
                after.setImageBitmap(encoded_image);
                message_box.setText(decoded_message);
            }
        }.execute();
    }
}
