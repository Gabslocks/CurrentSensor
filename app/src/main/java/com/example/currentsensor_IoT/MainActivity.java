package com.example.currentsensor_IoT;

import android.app.NotificationChannel;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import java.util.concurrent.TimeUnit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import org.json.JSONObject;

import helpers.MQTTHelper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;



public class MainActivity extends AppCompatActivity {

    private List<Lamps> lamps = new ArrayList();
    ListView lampList;
    Timer timer;
    int NOTIFY_ID = 1;
    MQTTHelper mqttHelper;
    int n = 1;
    private NotificationManager notificationManager;
    public static final String ACTION ="com.eugene.SHOW_OPT_ACTIVITY";
    String maxmid;

    private static final String CHANNEL_ID = "CHANNEL_ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer = new Timer();
        lampList = (ListView) findViewById(R.id.lampList);
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        setInitialData();
        startMQTT();
    }
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        maxmid = prefs.getString("login", "2500");
    }

        private void startMQTT(){
            mqttHelper = new MQTTHelper(getApplicationContext());
            mqttHelper.setCallback(new MqttCallbackExtended() {

                @Override
                public void connectComplete(boolean b, String s) {
                }

                @Override
                public void connectionLost(Throwable throwable) {
                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                    JSONObject str = new JSONObject(new String(mqttMessage.getPayload()));
                    JSONObject obj = str.getJSONObject("data");
                    int adc = obj.getInt("adc1");
                    Log.w("Debug", mqttMessage.toString());
                    for (int i=0; i<n; i++) {
                            final String name = "Элемент " + (i+1);
                            lamps.set(i, new Lamps(name, comp(adc, name)));
                   }
                    m_part();
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                }
            });
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //работа меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_reboot:
                mqttHelper.publishMessage();
            case R.id.action_settings:
                Intent intent = new Intent(this, OptionActivity.class);
                startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    public String comp(int x, String name){
        final String r = "Работает";
        final  String nr = "Не работает";
        int max = Integer.parseInt(maxmid);
        if((x>=max-5)&&(x<=max+5)){
            return r;}
        else {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this,
                    0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
           // notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            long[] vibrate = new long[] { 1000, 1000, 1000, 1000, 1000 };
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Уведомление")
                    .setContentText("Элемент работает неисправно") // Текст уведомления
                    .setContentIntent(contentIntent)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true) // автоматически закрыть уведомление после нажати
                    .setVibrate(vibrate);

            createChannelIfNeeded(notificationManager);
            notificationManager.notify(NOTIFY_ID++, builder.build());
            final String mess = name + " Вышел из строя ";
            Toast.makeText(MainActivity.this, mess, Toast.LENGTH_LONG).show();
            return nr;
        }
    }

    public static void createChannelIfNeeded(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    private void setInitialData(){
        //parsing MQTT messege
        final  String nr = "Не работает";
                for (int i=0; i<n; i++) {
                    final String name = "Элемент " + (i+1);
                    lamps.add(new Lamps(name, nr));
                }
        m_part();
    }

    private void m_part()
    {
        //  adapter for Lamps
        LampsAdapter stateAdapter = new LampsAdapter(this, R.layout.list_item, lamps);
        lampList.setAdapter(stateAdapter);
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // get the selected item
                Lamps selectedLamps = (Lamps)parent.getItemAtPosition(position);
                // Toast.makeText(getApplicationContext(), "Была выбрана " + selectedLamps.getName(),
                //        Toast.LENGTH_SHORT).show();
            }
        };
        lampList.setOnItemClickListener(itemListener);
    }

}

/*Если нужно будет парсить для нескольких ламп через массив
JSONArray arr = obj.getJSONArray("posts");
for (int i = 0; i < arr.length(); i++)
{
    String post_id = arr.getJSONObject(i).getString("post_id");
    ......
}
        */