package com.abcar.abride;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class DeliverActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Button btnSend;
    EditText deliverWeightInput, deliverNameInput, deliverRecipientPhoneInput;
    TextView deliverPickUpTime, deliverBookTime, deliverPickUpAddress,deliverDestinationAddress;

    DataOutputStream request;

    private String token,boundary,recipientPhone,recipient,connMessage,postUrl, pickLat, pickLong, destLat, destLong, pickAddress, destAddress, weight;
    private HttpURLConnection conn;
    private URL mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        btnSend = (Button)findViewById(R.id.deliveryBtnSend);
        deliverRecipientPhoneInput = (EditText)findViewById(R.id.deliverRecipientPhoneInput);
        deliverWeightInput = (EditText)findViewById(R.id.deliverWeightInput);
        deliverNameInput = (EditText)findViewById(R.id.deliverNameInput);
        deliverPickUpTime = (TextView)findViewById(R.id.deliverPickUpTime);
        deliverBookTime = (TextView)findViewById(R.id.deliverBookTime);
        deliverPickUpAddress = (TextView)findViewById(R.id.deliveryPickUpAddress);
        deliverDestinationAddress = (TextView)findViewById(R.id.deliveryDestination);
        deliverPickUpAddress.setText(sharedPref.getString("alamat_jemput", "null"));
        deliverDestinationAddress.setText(sharedPref.getString("alamat_tujuan", "null"));
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/YYYY HH:mm");
        deliverBookTime.setText(sdf.format(currentTime));
//        bookTime = deliverBookTime.getText().toString();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.HOUR, 1);
        deliverPickUpTime.setText(sdf.format(calendar.getTime()));
//        pickupTime = deliverPickUpTime.getText().toString();
        pickAddress = sharedPref.getString("alamat_jemput", "null");
        destAddress = sharedPref.getString("alamat_tujuan", "null");
        pickLat = sharedPref.getString("latitude_jemput","");
        pickLong = sharedPref.getString("longitude_jemput","");
        destLat = sharedPref.getString("latitude_tujuan","");
        destLong = sharedPref.getString("longitude_tujuan","");
        token = sharedPref.getString("token","");
        postUrl = "http://192.168.1.14:8000/api/delivery_booking/";
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    weight = deliverWeightInput.getText().toString();
                    recipient = deliverNameInput.getText().toString();
                    recipientPhone = deliverRecipientPhoneInput.getText().toString();
                }catch (Exception e){
                    e.printStackTrace();
                }

                boundary = UUID.randomUUID().toString();
                new SaveSettingTask().execute(mUrl);
            }
        });
        deliverWeightInput.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "20")});
    }
    private class SaveSettingTask extends AsyncTask<URL, Integer, Long> {

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
                request.writeBytes("Content-Disposition: form-data; name=\"recipient_name\"\r\n\r\n");
                request.writeBytes(recipient + "\r\n");

                request.writeBytes("--" + boundary + "\r\n");
                request.writeBytes("Content-Disposition: form-data; name=\"recipient_phone_number\"\r\n\r\n");
                request.writeBytes(recipientPhone + "\r\n");

                request.writeBytes("--" + boundary + "\r\n");
                request.writeBytes("Content-Disposition: form-data; name=\"total_weight\"\r\n\r\n");
                request.writeBytes(weight + "\r\n");

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
                    i.putExtra("message","Booking Pengiriman Anda berhasil. " +
                            "Pengemudi Kami Akan Mengantarkan Barang Anda Sesuai Waktu yang Telah Ditentukan.");
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
