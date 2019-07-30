package com.abcar.abride;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SummaryActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    TextView jenis,alamatTujuan,alamatJemput, jamBooking, jamBerangkat;
    String jemputLatLng, tujuanLatLng;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        jenis = (TextView)findViewById(R.id.summaryJenis);
        alamatJemput = (TextView)findViewById(R.id.summaryAlamatJemput);
        alamatTujuan = (TextView)findViewById(R.id.summaryAlamatTujuan);
        jamBooking = (TextView)findViewById(R.id.summaryBooking);
        jamBerangkat = (TextView)findViewById(R.id.summaryBerangkat);
        btnSend = (Button)findViewById(R.id.summaryBtnSend);
        jenis.setText(sharedPref.getString("jenis","motor"));
        alamatJemput.setText(sharedPref.getString("alamat_jemput", "null"));
        alamatTujuan.setText(sharedPref.getString("alamat_tujuan", "null"));
        jemputLatLng = sharedPref.getString("latitude_jemput","")+","+sharedPref.getString("longitude_jemput","");
        tujuanLatLng = sharedPref.getString("latitude_tujuan","")+","+sharedPref.getString("longitude_tujuan","");
        String directionLink = "https://www.google.com/maps/dir/"+jemputLatLng+"/"+tujuanLatLng+"/";
        Log.v("Direction Link",directionLink);
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/YYYY HH:mm");
        jamBooking.setText(sdf.format(currentTime));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.HOUR, 1);
        jamBerangkat.setText(sdf.format(calendar.getTime()));
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ThanksActivity.class);
                startActivity(i);
            }
        });
    }
}
