package com.abcar.abride;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ImageView iconMotor, iconMobil, iconDeliver;
    private SharedPreferences sharedPref;
    private URL mUrl;
    String mMessage, username;
    WebSocket ws = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean logged_in =sharedPref.getBoolean("logged_in", false);
        if(logged_in != true){
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
        }
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
        WebSocketFactory factory = new WebSocketFactory().setConnectionTimeout(5000);
        try {
            String uri = "ws://192.168.1.16:8000/ws/booking/lobby/";
            ws = factory.createSocket(uri);

        } catch (Exception e) {
            e.printStackTrace();
        }
        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, final String message) throws Exception {
                runOnUiThread(new Runnable(){

                    @Override
                    public void run(){
                        try {

                            JSONObject obj = new JSONObject(message);

                            mMessage = obj.getString("message");
                            JSONObject newBooking = new JSONObject(mMessage);
                            username = obj.getString("username");
                            Log.d("My App", mMessage);
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(MainActivity.this, "lobby")
                                            .setContentTitle("Pesanan Baru")
                                            .setSmallIcon(R.drawable.icon)
                                            .setContentText("dengan nomor :" +
                                                    newBooking.getString("booking_no"));
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                NotificationChannel channel = new NotificationChannel("lobby",
                                        "lobby",
                                        NotificationManager.IMPORTANCE_DEFAULT);
                                notificationManager.createNotificationChannel(channel);
                            }
                            notificationManager.notify(1, mBuilder.build());

                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + message + "\"");
                        }
                    }
                });
                mMessage = message;
                Log.i("TAG", "onTextMessage: " + mMessage);
            }

        });

        new MainActivity.SendMessageTask().execute(mUrl);
    }
    private class SendMessageTask extends AsyncTask<URL, Integer, Long> {

        @Override
        protected Long doInBackground(URL... urls) {
            try {
                ws.connect();
                sendMessage();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (ws != null) {
            ws.disconnect();
            ws = null;
        }
    }

    public void sendMessage() {
        if (ws.isOpen()) {
            String token = sharedPref.getString("token","");
            ws.sendText("{\"message\":\"Message from Android!\"," +
                    "\"token\":\""+token+"\""+
                    "}");
            Log.i("TAG", "OKAY");
        }
    }
}
