package com.abcar.abride;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView iconMotor, iconMobil, iconDeliver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iconMotor = (ImageView)findViewById(R.id.icon_motor);
        iconMobil = (ImageView)findViewById(R.id.icon_mobil);
        iconDeliver = (ImageView)findViewById(R.id.icon_deliver);
        iconMotor.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MapActivity.class);
                i.putExtra("jenis","Motor");
                i.putExtra("is_destination",false);
                startActivity(i);
            }
        });
        iconMobil.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MapActivity.class);
                i.putExtra("jenis","Mobil");
                i.putExtra("is_destination",false);
                startActivity(i);
            }
        });
        iconDeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MapActivity.class);
                i.putExtra("jenis","Deliver");
                i.putExtra("is_destination",false);
                startActivity(i);
            }
        });
    }
}
