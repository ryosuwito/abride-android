package com.abcar.abride;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class SummaryActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    TextView jenis,alamatTujuan,alamatJemput, jamBooking, jamBerangkat;
    String jemputLatLng, tujuanLatLng;
    Button btnSend;
    DataOutputStream request;

    private String vehicle_type,token,boundary,connMessage,postUrl, pickLat, pickLong, destLat, destLong, pickAddress, destAddress;
    private HttpURLConnection conn;
    private URL mUrl;

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
        vehicle_type = sharedPref.getString("jenis","motor");
        jenis.setText(vehicle_type);
        destAddress = sharedPref.getString("alamat_tujuan", "null");
        pickAddress = sharedPref.getString("alamat_jemput", "null");
        alamatJemput.setText(pickAddress);
        alamatTujuan.setText(destAddress);
        token = sharedPref.getString("token","");
        pickLat = sharedPref.getString("latitude_jemput","");
        pickLong = sharedPref.getString("longitude_jemput","");
        destLat = sharedPref.getString("latitude_tujuan","");
        destLong = sharedPref.getString("longitude_tujuan","");
        postUrl = "http://192.168.1.14:8000/api/ride_booking/";
        jemputLatLng = pickLat+","+pickLong;
        tujuanLatLng = destLat+","+destLong;
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
                if(vehicle_type.equalsIgnoreCase("motor")){
                    vehicle_type = "0";
                }else{
                    vehicle_type = "1";
                }
                boundary = UUID.randomUUID().toString();
                new SendSettingTask().execute(mUrl);
            }
        });
    }
    private class SendSettingTask extends AsyncTask<URL, Integer, Long> {

        @Override
        protected Long doInBackground(URL... urls) {
            try {
                String baseUrl;
                baseUrl = postUrl;
                Log.v("murls", baseUrl);
                mUrl = new URL(baseUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Intent i = new Intent(getApplicationContext(), ErrorActivity.class);
                startActivity(i);
            }
            try {
                conn = (HttpURLConnection) mUrl.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Authorization", "Token "+token);

                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                request = new DataOutputStream(conn.getOutputStream());
                request.writeBytes("Content-Disposition: form-data; name=\"pick_up_latitude\"\r\n\r\n");
                request.writeBytes(pickLat + "\r\n");

                request.writeBytes("--" + boundary + "\r\n");
                request.writeBytes("Content-Disposition: form-data; name=\"pick_up_longitude\"\r\n\r\n");
                request.writeBytes(pickLong + "\r\n");

                request.writeBytes("--" + boundary + "\r\n");
                request.writeBytes("Content-Disposition: form-data; name=\"pick_up_address\"\r\n\r\n");
                request.writeBytes(pickAddress + "\r\n");

                request.writeBytes("--" + boundary + "\r\n");
                request.writeBytes("Content-Disposition: form-data; name=\"destination_latitude\"\r\n\r\n");
                request.writeBytes(destLat + "\r\n");

                request.writeBytes("--" + boundary + "\r\n");
                request.writeBytes("Content-Disposition: form-data; name=\"destination_longitude\"\r\n\r\n");
                request.writeBytes(destLong + "\r\n");

                request.writeBytes("--" + boundary + "\r\n");
                request.writeBytes("Content-Disposition: form-data; name=\"destination_address\"\r\n\r\n");
                request.writeBytes(destAddress + "\r\n");

                request.writeBytes("--" + boundary + "\r\n");
                request.writeBytes("Content-Disposition: form-data; name=\"vehicle_type\"\r\n\r\n");
                request.writeBytes(vehicle_type + "\r\n");

                request.writeBytes("--" + boundary + "\r\n");
            } catch (IOException e) {
                e.printStackTrace();
                Intent i = new Intent(getApplicationContext(), ErrorActivity.class);
                startActivity(i);
            }

            try {
                int resCode = conn.getResponseCode();
                Log.v("responseCode",String.valueOf(resCode));
                connMessage = conn.getResponseMessage();
                Log.v("responseMessage", connMessage);
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                Log.v("responseBody",br.readLine());
                conn.connect();
                request.close();
                conn.disconnect();
                if(resCode/100 == 2){
                    Intent i = new Intent(getApplicationContext(), ThanksActivity.class);
                    i.putExtra("message","Booking Perjalanan Anda berhasil. " +
                            "Pengemudi Kami Akan Menjemput Anda Sesuai Waktu yang Telah Ditentukan.");
                    startActivity(i);
                }else{
                    Intent i = new Intent(getApplicationContext(), ErrorActivity.class);
                    startActivity(i);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Intent i = new Intent(getApplicationContext(), ErrorActivity.class);
                startActivity(i);
            }

            return null;
        }
    }
}
