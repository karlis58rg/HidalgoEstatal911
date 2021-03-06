package mx.gob.hidalgo.hidalgoestatal911.Servicio911;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import mx.gob.hidalgo.hidalgoestatal911.FormSensorIngresaPlaca;
import mx.gob.hidalgo.hidalgoestatal911.MensajeSalida;
import mx.gob.hidalgo.hidalgoestatal911.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static mx.gob.hidalgo.hidalgoestatal911.Shake.app.CHANNEL_ID;

public class ServiceShake extends Service implements SensorEventListener {
    public ServiceShake(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    MyTask2 miTareaSuper;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent=0; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private int sacudidas=0;
    private long tiempoActual=0;
    private long tiempoAnterior=0;
    private long diferencia;
    private String fecha,hora;
    int bandera = 0;
    //
    private Activity activity;
    SharedPreferences shared;
    SharedPreferences.Editor editor;
    private String cargarInfoTelefono,cargarInfoNombre,cargarInfoPaterno,cargarInfoMaterno,cargarInfoRandom,cargarInfoPlaca,cargarInfoSDK;
    String cargarInfoDireccion,cargarInfoMunicipio,cargarInfoEstado;
    String cargarInfoLat;
    String cargarInfoLong;
    int numberRandom;
    String codigoVerifi;
    String randomCodigoVerifi;
    private String valorRandom;
    Double lat,lon;
    String status = "1";
    String mensaje1,mensaje2;
    String direccion,municipio,estado;
    String version;
    int comparar;
    String serbar = "sincrear";
    Boolean banderaSirena = false;

    private static final int NOTIFICATION_ID = 200;
    //private static final String CHANNEL_ID = "PANIC_ID";
    private static final String CHANNEL_NAME = "PANIC_BUTTON";


    public ServiceShake() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException( "Not yet implemented" );
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("HERE", "SERVICIO CREADO");
        miTareaSuper = new MyTask2();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        miTareaSuper.execute();
        locationStart();
        cargar();
        version = cargarInfoSDK;
        comparar = Integer.parseInt( version );

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI, new Handler());
        //startForeground();

        Intent notificationIntent = new Intent(this, FormSensorIngresaPlaca.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getApplicationContext(), CHANNEL_ID);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Servicio de Botón de Pánico")
                .setContentText("Sacudir para mandar alerta")
                .setSmallIcon(R.drawable.ic_logoapp_911)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);

        //All notifications should go through NotificationChannel on Android 26 & above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);

        }
        notificationManager.notify(NOTIFICATION_ID, notification);

        if(comparar >= 22 || comparar <= 25){
           // startForeground( 1,notification );
        }
        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("HEY", "onDestroy!");
        miTareaSuper.cancel(true);
        stopForeground(true);
        stopService(new Intent(ServiceShake.this, ServiceShake.class));
        eliminarServicio();
        super.onDestroy();
    }

    public class MyTask2 extends AsyncTask<String,String,String> {
        private boolean aux;

        @Override
        protected void onPreExecute() {
            aux = true;
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.i("HERE","Localización cada 15 segundos");
            serbar = "creado";
            Log.i("HERE",serbar);
            guardarServicio();
            if(bandera == 2){
                Log.i("HEY", "BANDERA 2!");
                locationStart();
                getDatosMapaRobos();
            }else if(bandera == 3){
                Log.i("HEY", "BANDERA 3!");
                locationStart();
                getDatosMapaRobos();
            }else if (bandera == 4) {
                bandera = 0;
                Log.i("HEY", "BANDERA 4!");
                eliminarServicio();
                FormSensorIngresaPlaca.gifRojo.setVisibility( View.GONE);
                stopSelf();
                stopForeground(true);
                miTareaSuper.cancel(true);
                stopService(new Intent(ServiceShake.this, ServiceShake.class));
                onDestroy();
                Intent i = new Intent( getApplicationContext(), MensajeSalida.class );
                i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                i.putExtra("valorRandom",valorRandom);
                startActivity( i );
                System.exit(0);

            }
            //Toast.makeText(getApplicationContext(), "EJECUTANDO SERVICIO CADA 15 SEGUNDOS", Toast.LENGTH_SHORT).show();
            Log.i("HERE","Localización cada 15 segundos");

        }

        @Override
        protected void onCancelled() {
            miTareaSuper.onCancelled();
            super.onCancelled();
            //aux = false;

        }

        @Override
        protected String doInBackground(String... strings) {
            while (aux){
                try {
                    publishProgress();
                    Thread.sleep(15000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        cargar();
        version = cargarInfoSDK;
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter

        switch (version){
            case "21":
                Log.i("HERE",version);
                if (mAccel > 7) {
                    tiempoAnterior = tiempoActual;
                    tiempoActual = System.currentTimeMillis();
                    diferencia=tiempoActual-tiempoAnterior;
                    if(diferencia < 700){
                        sacudidas++;
                    }
                    else{
                        sacudidas=0;
                    }
                    if(sacudidas == 2 ) {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(1500);
                        sacudidas=0;
                        FormSensorIngresaPlaca.gifRojo.setVisibility(View.VISIBLE);
                        FormSensorIngresaPlaca.bandaroja.setVisibility( View.VISIBLE );
                        FormSensorIngresaPlaca.lblagita.setVisibility(View.INVISIBLE);
                        FormSensorIngresaPlaca.emergencia.setVisibility(View.INVISIBLE);
                        FormSensorIngresaPlaca.paloma.setVisibility(View.INVISIBLE);
                        this.locationStart();
                        if(bandera == 2){
                            insertBdEventoTransportePublicoRobosIOS();
                        }else{
                            Random();
                            insertBdEventoTransportePublicoIOS();
                            Toast.makeText(getApplicationContext(), "EMERGENCIA ENVIADA", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;

            case "22":
                Log.i("HERE",version);
                if (mAccel > 12) {
                    tiempoAnterior = tiempoActual;
                    tiempoActual = System.currentTimeMillis();
                    diferencia=tiempoActual-tiempoAnterior;
                    if(diferencia < 700){
                        sacudidas++;
                    }
                    else{
                        sacudidas=0;
                    }
                    if(sacudidas == 2 ) {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(1500);
                        sacudidas=0;
                        FormSensorIngresaPlaca.gifRojo.setVisibility(View.VISIBLE);
                        FormSensorIngresaPlaca.bandaroja.setVisibility( View.VISIBLE );
                        FormSensorIngresaPlaca.lblagita.setVisibility(View.INVISIBLE);
                        FormSensorIngresaPlaca.emergencia.setVisibility(View.INVISIBLE);
                        FormSensorIngresaPlaca.paloma.setVisibility(View.INVISIBLE);
                        this.locationStart();
                        if(bandera == 2){
                            insertBdEventoTransportePublicoRobosIOS();
                        }else{
                            Random();
                            insertBdEventoTransportePublicoIOS();
                            Toast.makeText(getApplicationContext(), "EMERGENCIA ENVIADA", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;

            case "23":
                Log.i("HERE",version);
                if (mAccel > 8) {
                    tiempoAnterior = tiempoActual;
                    tiempoActual = System.currentTimeMillis();
                    diferencia=tiempoActual-tiempoAnterior;
                    if(diferencia < 700){
                        sacudidas++;
                    }
                    else{
                        sacudidas=0;
                    }
                    if(sacudidas == 2 ) {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(1500);
                        sacudidas=0;
                        FormSensorIngresaPlaca.gifRojo.setVisibility(View.VISIBLE);
                        FormSensorIngresaPlaca.bandaroja.setVisibility( View.VISIBLE );
                        FormSensorIngresaPlaca.lblagita.setVisibility(View.INVISIBLE);
                        FormSensorIngresaPlaca.emergencia.setVisibility(View.INVISIBLE);
                        FormSensorIngresaPlaca.paloma.setVisibility(View.INVISIBLE);
                        this.locationStart();
                        if(bandera == 2){
                            insertBdEventoTransportePublicoRobosIOS();
                        }else{
                            Random();
                            insertBdEventoTransportePublicoIOS();
                            Toast.makeText(getApplicationContext(), "EMERGENCIA ENVIADA", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case "24":
                Log.i("HERE",version);
                if (mAccel > 10) {
                    tiempoAnterior = tiempoActual;
                    tiempoActual = System.currentTimeMillis();
                    diferencia=tiempoActual-tiempoAnterior;
                    if(diferencia < 700){
                        sacudidas++;
                    }
                    else{
                        sacudidas=0;
                    }
                    if(sacudidas == 2 ) {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(1500);
                        sacudidas=0;
                        FormSensorIngresaPlaca.gifRojo.setVisibility(View.VISIBLE);
                        FormSensorIngresaPlaca.bandaroja.setVisibility( View.VISIBLE );
                        FormSensorIngresaPlaca.lblagita.setVisibility(View.INVISIBLE);
                        FormSensorIngresaPlaca.emergencia.setVisibility(View.INVISIBLE);
                        FormSensorIngresaPlaca.paloma.setVisibility(View.INVISIBLE);
                        this.locationStart();
                        if(bandera == 2){
                            insertBdEventoTransportePublicoRobosIOS();
                        }else{
                            Random();
                            insertBdEventoTransportePublicoIOS();
                            Toast.makeText(getApplicationContext(), "EMERGENCIA ENVIADA", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case "25":
                Log.i("HERE",version);
                if (mAccel > 10.5) {
                    tiempoAnterior = tiempoActual;
                    tiempoActual = System.currentTimeMillis();
                    diferencia=tiempoActual-tiempoAnterior;
                    if(diferencia < 700){
                        sacudidas++;
                    }
                    else{
                        sacudidas=0;
                    }
                    if(sacudidas == 2 ) {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(1500);
                        sacudidas=0;
                        FormSensorIngresaPlaca.gifRojo.setVisibility(View.VISIBLE);
                        FormSensorIngresaPlaca.bandaroja.setVisibility( View.VISIBLE );
                        FormSensorIngresaPlaca.lblagita.setVisibility(View.INVISIBLE);
                        FormSensorIngresaPlaca.emergencia.setVisibility(View.INVISIBLE);
                        FormSensorIngresaPlaca.paloma.setVisibility(View.INVISIBLE);
                        this.locationStart();
                        if(bandera == 2){
                            insertBdEventoTransportePublicoRobosIOS();
                        }else{
                            Random();
                            insertBdEventoTransportePublicoIOS();
                            Toast.makeText(getApplicationContext(), "EMERGENCIA ENVIADA", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case "26":
                Log.i("HERE",version);
                if (mAccel > 11.0) {
                    tiempoAnterior = tiempoActual;
                    tiempoActual = System.currentTimeMillis();
                    diferencia=tiempoActual-tiempoAnterior;
                    if(diferencia < 700){
                        sacudidas++;
                    }
                    else{
                        sacudidas=0;
                    }
                    if(sacudidas == 2 ) {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(1500);
                        sacudidas=0;
                        FormSensorIngresaPlaca.gifRojo.setVisibility(View.VISIBLE);
                        FormSensorIngresaPlaca.bandaroja.setVisibility( View.VISIBLE );
                        FormSensorIngresaPlaca.lblagita.setVisibility(View.INVISIBLE);
                        FormSensorIngresaPlaca.emergencia.setVisibility(View.INVISIBLE);
                        FormSensorIngresaPlaca.paloma.setVisibility(View.INVISIBLE);
                        this.locationStart();
                        if(bandera == 2){
                            insertBdEventoTransportePublicoRobosIOS();
                        }else{
                            Random();
                            insertBdEventoTransportePublicoIOS();
                            Toast.makeText(getApplicationContext(), "EMERGENCIA ENVIADA", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case "27":
                Log.i("HERE",version);
                if (mAccel > 11) {
                    tiempoAnterior = tiempoActual;
                    tiempoActual = System.currentTimeMillis();
                    diferencia=tiempoActual-tiempoAnterior;
                    if(diferencia < 700){
                        sacudidas++;
                    }
                    else{
                        sacudidas=0;
                    }
                    if(sacudidas == 2 ) {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(1500);
                        sacudidas=0;
                        FormSensorIngresaPlaca.gifRojo.setVisibility(View.VISIBLE);
                        FormSensorIngresaPlaca.bandaroja.setVisibility( View.VISIBLE );
                        FormSensorIngresaPlaca.lblagita.setVisibility(View.INVISIBLE);
                        FormSensorIngresaPlaca.emergencia.setVisibility(View.INVISIBLE);
                        FormSensorIngresaPlaca.paloma.setVisibility(View.INVISIBLE);
                        this.locationStart();
                        if(bandera == 2){
                            insertBdEventoTransportePublicoRobosIOS();
                        }else{
                            Random();
                            insertBdEventoTransportePublicoIOS();
                            Toast.makeText(getApplicationContext(), "EMERGENCIA ENVIADA", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;

            case "28":
                Log.i("HERE",version);
                if (mAccel > 27) {
                    tiempoAnterior = tiempoActual;
                    tiempoActual = System.currentTimeMillis();
                    diferencia=tiempoActual-tiempoAnterior;
                    if(diferencia < 700){
                        sacudidas++;
                    }
                    else{
                        sacudidas=0;
                    }
                    if(sacudidas == 2 ) {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(1500);
                        sacudidas=0;
                        FormSensorIngresaPlaca.gifRojo.setVisibility(View.VISIBLE);
                        FormSensorIngresaPlaca.bandaroja.setVisibility( View.VISIBLE );
                        FormSensorIngresaPlaca.lblagita.setVisibility(View.INVISIBLE);
                        FormSensorIngresaPlaca.emergencia.setVisibility(View.INVISIBLE);
                        FormSensorIngresaPlaca.paloma.setVisibility(View.INVISIBLE);
                        this.locationStart();
                        if(bandera == 2){
                            insertBdEventoTransportePublicoRobosIOS();
                        }else{
                            Random();
                            insertBdEventoTransportePublicoIOS();
                            Toast.makeText(getApplicationContext(), "EMERGENCIA ENVIADA", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
                default:
                    Log.i("HERE",version);
                    if (mAccel > 26) {
                        tiempoAnterior = tiempoActual;
                        tiempoActual = System.currentTimeMillis();
                        diferencia=tiempoActual-tiempoAnterior;
                        if(diferencia < 700){
                            sacudidas++;
                        }
                        else{
                            sacudidas=0;
                        }
                        if(sacudidas == 2 ) {
                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(1500);
                            sacudidas=0;
                            FormSensorIngresaPlaca.gifRojo.setVisibility(View.VISIBLE);
                            FormSensorIngresaPlaca.bandaroja.setVisibility( View.VISIBLE );
                            FormSensorIngresaPlaca.lblagita.setVisibility(View.INVISIBLE);
                            FormSensorIngresaPlaca.emergencia.setVisibility(View.INVISIBLE);
                            FormSensorIngresaPlaca.paloma.setVisibility(View.INVISIBLE);
                            this.locationStart();
                            if(bandera == 2){
                                insertBdEventoTransportePublicoRobosIOS();
                            }else{
                                Random();
                                insertBdEventoTransportePublicoIOS();
                                Toast.makeText(getApplicationContext(), "EMERGENCIA ENVIADA", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
        }

    }

    //*********************** METODO QUE INSERTA A LA BASE DE DATOS DESPUES DE INSERTAR AL CAD ***********************//
    public void insertBdEventoTransportePublicoIOS(){
        bandera = 1;
        cargar();
        valorRandom = "C5i2019"+ randomCodigoVerifi;
        //*************** FECHA **********************//
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        fecha = dateFormat.format(date);

        //*************** HORA **********************//
        Date time = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        hora = timeFormat.format(time);
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("FolioRobo",valorRandom)
                .add("Telefono", cargarInfoTelefono)
                .add("NombreUsuario", cargarInfoNombre)
                .add("ApaternoUsuario", cargarInfoPaterno)
                .add("AmaternoUsuario", cargarInfoMaterno)
                .add("Placa", cargarInfoPlaca)
                .add("Direccion", cargarInfoDireccion)
                .add("Municipio", cargarInfoMunicipio)
                .add("Estado", cargarInfoEstado)
                .add("Latitud", cargarInfoLat)
                .add("Longitud", cargarInfoLong)
                .add("Hora", hora)
                .add("Fecha", fecha)
                .add("idTipoEmergencia", "6000")
                .build();

        Request request = new Request.Builder()
                .url("http://187.174.102.131:58/api/CadWeb/")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getApplicationContext(), "ERROR AL ENVIAR SU REGISTRO", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().toString();  /********** ME REGRESA LA RESPUESTA DEL WS ****************/
                    insertBdEventoTransportePublicoRobosIOS();

                }
            }
        });
    }

    //**************** INSERTA A LA TABLA DE ROBOS CON LAS COORDENADAS EN TIEMPO REAL *****************//
    public void insertBdEventoTransportePublicoRobosIOS() {
        cargar();
        //*************** FECHA **********************//
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        fecha = dateFormat.format(date);

        //*************** HORA **********************//
        Date time = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        hora = timeFormat.format(time);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("FolioRobo",valorRandom)
                .add("Telefono", cargarInfoTelefono)
                .add("Longitud", cargarInfoLat)
                .add("Latitud", cargarInfoLong)
                .add("Status", status)
                .add("Fecha", fecha)
                .add("Hora", hora)
                .add("Placa", cargarInfoPlaca)
                .add( "Direccion",cargarInfoDireccion)
                .add( "Revisado", "1")
                .build();

        Request request = new Request.Builder()
                .url("http://187.174.102.131:58/api/EventosTransportePublicoRobos/")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getApplicationContext(), "ERROR AL ENVIAR SU REGISTRO", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().toString();
                    bandera = 2;/********** ME REGRESA LA RESPUESTA DEL WS ****************/
                    //startTimer();
                }
            }
        });
    }

    //************************ STSTUS DEL INCIDENTE ***************************************//

    public void getDatosMapaRobos(){

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://c5.hidalgo.gob.mx:58/api/EventosTransportePublicoRobos?folioRobo=C5i2019"+randomCodigoVerifi+"&statusRobo=1")
                //.url("http://c5.hidalgo.gob.mx:58/api/EventosTransportePublicoRobos?folioRobo=C5I2019891&statusRobo=1")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();
                    final String resp = myResponse;
                    final String valor = "true";

                    if(resp.equals(valor)){
                        bandera = 3;
                        Log.i("HERE", "BANDERA 3!");
                        Log.i("HERE", resp);
                        insertBdEventoTransportePublicoRobosIOS();
                    }else{
                        bandera = 4;
                        Log.i("HERE", resp);
                        Log.i("HERE", "PROCESO TERMINADO");
                        //stopSelf();
                        //onDestroy();
                        //stopForeground(true);
                        //miTarea.cancel(true);
                    }
                    Log.i("HERE", resp);

                }
            }
        });
    }

    private void runOnUiThread(Runnable runnable) {
        if(bandera == 1){
            Toast.makeText(getApplicationContext(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();
        }else if (bandera == 2){
            Toast.makeText(getApplicationContext(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();
        }else if(bandera == 3){
            Toast.makeText(getApplicationContext(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();
        }
    }

    //Apartir de aqui empezamos a obtener la direciones y coordenadas
    public void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ServiceShake.Localizacion Local = new ServiceShake.Localizacion();
        Local.setServiceShake(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
        mensaje1 = "Localizacion agregada";
        mensaje2 = "";
        Log.i("HERE", mensaje1);
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }
    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    direccion = DirCalle.getAddressLine(0);
                    municipio = DirCalle.getLocality();
                    estado = DirCalle.getAdminArea();
                    if(municipio != null) {
                        municipio = DirCalle.getLocality();
                    }else{
                        municipio = "SIN INFORMACION";
                    }
                    guardar();
                    Log.i("HERE", "dir" + direccion + "mun"+ municipio + "est"+ estado);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        ServiceShake serviceShake;
        public ServiceShake getServiceShake() {
            return serviceShake;
        }
        public void setServiceShake(ServiceShake serviceShake1) {
            this.serviceShake = serviceShake1;
        }
        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.getLatitude();
            loc.getLongitude();
            lat = loc.getLatitude();
            lon = loc.getLongitude();
            guardarCoor();
            String Text = "Lat = "+ loc.getLatitude() + "\n Long = " + loc.getLongitude();
            mensaje1 = Text;
            Log.i("HERE", mensaje1);
            this.serviceShake.setLocation(loc);
        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            mensaje1 = "GPS Desactivado";
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            mensaje1 = "GPS Activado";
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    //********************* OBTIENE LAS PREFERENCIAS DEL SISTEMA (VARIABLES DE SESSION) *****************************//
    private void cargar(){
        shared = getSharedPreferences("main",MODE_PRIVATE);
        cargarInfoTelefono = shared.getString("telefono","");
        cargarInfoNombre = shared.getString("nombre","");
        cargarInfoPaterno = shared.getString("paterno","");
        cargarInfoMaterno = shared.getString("materno","");
        cargarInfoPlaca = shared.getString("placa","");
        cargarInfoDireccion = shared.getString("direccion","");
        cargarInfoMunicipio = shared.getString("municipio","");
        cargarInfoEstado = shared.getString("estado","");
        cargarInfoLat = shared.getString("latitude","");
        cargarInfoLong = shared.getString("longitude","");
        cargarInfoSDK = shared.getString( "sdk","" );
        //Toast.makeText(getApplicationContext(),cargarInfoTelefono+cargarInfoNombre+cargarInfoPaterno+cargarInfoMaterno,Toast.LENGTH_LONG).show();
    }

    private void guardar(){
        shared = getSharedPreferences("main",MODE_PRIVATE);
        editor = shared.edit();
        editor.putString("direccion",direccion);
        editor.putString("municipio",municipio);
        editor.putString("estado",estado);
        editor.commit();
        // Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }

    private void guardarCoor(){
        shared = getSharedPreferences("main",MODE_PRIVATE);
        editor = shared.edit();
        editor.putString("latitude",lat.toString());
        editor.putString("longitude",lon.toString());
        editor.commit();
        // Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }

    private void guardarServicio(){
        shared = getSharedPreferences("main",MODE_PRIVATE);
        editor = shared.edit();
        editor.putString("servicio",serbar);
        editor.commit();
    }

    private void eliminarServicio(){
        SharedPreferences settings = getApplication().getSharedPreferences("main", Context.MODE_PRIVATE);
        settings.edit().remove("servicio").commit();
        //Toast.makeText(getApplicationContext(),"Dato Eliminado",Toast.LENGTH_LONG).show();
    }

    //********************* GENERA EL NÚMERO ALEATORIO PARA EL FOLIO *****************************//

    public void Random()
    {
        Random random = new Random();
        numberRandom = random.nextInt(9000)*99;
        codigoVerifi = String.valueOf(numberRandom);
        randomCodigoVerifi = codigoVerifi;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
