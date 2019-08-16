package com.abcar.abride;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ThanksActivity extends AppCompatActivity {
    private TextView thanksMessageView;
    private Button buttonOrderOk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanks);
        thanksMessageView = (TextView)findViewById(R.id.thanksMessageView);
        buttonOrderOk = (Button)findViewById(R.id.buttonOrderOk);
        String message = getIntent().getStringExtra("message");
        thanksMessageView.setText(message);
        buttonOrderOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }
}
