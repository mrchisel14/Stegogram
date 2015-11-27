package com.team.redacted.stegogram;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button send_btn = (Button) findViewById(R.id.SenderButton);
        Button receive_btn = (Button) findViewById(R.id.ReceiverButton);
        Button test_btn = (Button)findViewById(R.id.test);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call SenderActivity
                CryptoEngine.decryptMessage(CryptoEngine.encryptMessage("hello", "hi"), "hi");
                senderButtonClick();
            }
        });
        receive_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call ReceiverActivity
                receiverButtonClick();
            }
        });
        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });


    }
    void test(){
        Intent intent = new Intent(this, TestCryptoActivity.class);
        startActivity(intent);
    }
    void senderButtonClick(){
        Intent sIntent = new Intent(this, SenderActivity.class);
        startActivity(sIntent);
    }
    void receiverButtonClick(){
        Intent rIntent = new Intent(this, ReceiverActivity.class);
        startActivity(rIntent);
    }
}
