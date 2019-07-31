package com.abcar.abride;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DeliverActivity extends AppCompatActivity {
    Button btnSend;
    EditText deliverWeightInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver);
        btnSend = (Button)findViewById(R.id.deliveryBtnSend);
        deliverWeightInput = (EditText)findViewById(R.id.deliverWeightInput);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ErrorActivity.class);
                startActivity(i);
            }
        });
        deliverWeightInput.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "20")});
    }
}
