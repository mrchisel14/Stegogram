package com.team.redacted.stegogram;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SenderActivity extends AppCompatActivity implements SelectImageDialogFragment.OnDialogDismissListener {
    static final int PICK_CONTACT_REQUEST = 0;
    EditText recipient_text, password, message_box;
    String phone_num = null, MAX_COUNT = "255", password_str, message, recipients;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        ImageButton recipient_button = (ImageButton) findViewById(R.id.contacts_button);
        CheckBox view_password = (CheckBox)findViewById(R.id.view_password);
        Button send_button = (Button)findViewById(R.id.send_button);
        message_box = (EditText)findViewById(R.id.message);
        final TextView message_count = (TextView) findViewById(R.id.message_count);
        password = (EditText)findViewById(R.id.password);
        recipient_text = (EditText)findViewById(R.id.recipient);

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
        recipient_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContactButton();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sender, menu);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                phone_num = cursor.getString(column);
                recipient_text.setText(recipient_text.getText() + phone_num + ";");
                cursor.close();
            }
        }
    }
    void onClickContactButton() {
        /*Do stuff when someone clicks contact button*/
        Intent it= new Intent(Intent.ACTION_PICK,  ContactsContract.Contacts.CONTENT_URI);
        it.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(it, PICK_CONTACT_REQUEST);
    }
    void onSendButtonClick() {
        recipients = recipient_text.getText().toString();
        message = message_box.getText().toString();
        password_str = password.getText().toString();
        if(recipients == null || recipients.matches("")) {
            Toast.makeText(this, "No recipients selected", Toast.LENGTH_SHORT).show();
            return;
        }
        if(message == null || message.matches("")){
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password_str == null || password_str.matches("")){
            Toast.makeText(this, "No password. Message will not be encrypted.", Toast.LENGTH_SHORT).show();
        }
        SelectImageDialogFragment fragment = new SelectImageDialogFragment();
        fragment.show(getFragmentManager(), "SelectImage");
    }
    public void onDialogDismissListener(Uri image){
        if(image != null){
            message = message_box.getText().toString();
            password_str = password.getText().toString();
            Utilities.createStegogramRequest(this, image, password_str, message, Utilities.ENCODE_IMAGE);
        }
        else
            Toast.makeText(this, "No path selected.", Toast.LENGTH_SHORT).show();
    }
}
