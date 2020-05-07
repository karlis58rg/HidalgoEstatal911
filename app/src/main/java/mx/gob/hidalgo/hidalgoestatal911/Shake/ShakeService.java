package mx.gob.hidalgo.hidalgoestatal911.Shake;

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
import android.graphics.Color;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import mx.gob.hidalgo.hidalgoestatal911.FormIngresaPlaca;
import mx.gob.hidalgo.hidalgoestatal911.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import static mx.gob.hidalgo.hidalgoestatal911.Shake.app.CHANNEL_ID;


public class ShakeService extends Service implements SensorEventListener {
    public int counter=0;
    public ShakeService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }
    public ShakeService() {
    }
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
    private Activity activity;
    SharedPreferences shared;
    SharedPreferences.Editor editor;
    private String cargarInfoTelefono,cargarInfoNombre,cargarInfoPaterno,cargarInfoMaterno,cargarInfoRandom,cargarInfoPlaca;
    String cargarInfoDireccion,cargarInfoMunicipio,cargarInfoEstado;
    String cargarInfoLat;
    String cargarInfoLong;
    int numberRandom;
    String codigoVerifi;
    String randomCodigoVerifi;
    private String valorRandom;
    Double lat,lon;
    String var1,var2,var3;
    String mensaje1,mensaje2;
    String direccion,municipio,estado;

    SensorManager sensorManager;
    Sensor sensor;
    SensorEventListener sensorEventListener;

    @Override
    public void onCreate(){
        super .onCreate();

/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());*/
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI, new Handler());

        Intent notificationIntent = new Intent(this, FormIngresaPlaca.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Servicio de Botón de Pánico")
                .setContentText("Sacudir para mandar alerta")
                .setSmallIcon(R.drawable.ic_logoapp911_foreground)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(2,new Notification());
        //startForeground(1, new Notification());
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter

        if (mAccel > 20) {
            tiempoAnterior = tiempoActual;
            tiempoActual = System.currentTimeMillis();
            diferencia=tiempoActual-tiempoAnterior;
            if(diferencia<700){
                sacudidas++;
            }
            else{
                sacudidas=0;
            }
            if(sacudidas == 2 ) {
                Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                v.vibrate(1500);
                this.locationStart();
                Random();
                insertBdEventoTransportePublicoIOS();
                Toast.makeText(getApplicationContext(), "EMERGENCIA ENVIADA", Toast.LENGTH_SHORT).show();
                sacudidas=0;
            }
        }
        FormIngresaPlaca.tvShakeService.setText(String.valueOf(diferencia));
    }

    //*********************** METODO QUE INSERTA A LA BASE DE DATOS DESPUES DE INSERTAR AL CAD ***********************//
    public void insertBdEventoTransportePublicoIOS(){
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
                Toast.makeText(getApplicationContext(), "ERROR AL ENVIAR SU REGISTRO", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().toString();  /********** ME REGRESA LA RESPUESTA DEL WS ****************/

                }

            }
        });
    }
    private void runOnUiThread(Runnable runnable) {
        Toast.makeText(getApplicationContext(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //Apartir de aqui empezamos a obtener la direciones y coordenadas
    public void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ShakeService.Localizacion Local = new ShakeService.Localizacion();
        Local.setShakeService(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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
        ShakeService shakeService;
        public ShakeService getShakeService() {
            return shakeService;
        }
        public void setShakeService(ShakeService shakeService1) {
            this.shakeService = shakeService1;
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
            this.shakeService.setLocation(loc);
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

    //********************* GENERA EL NÚMERO ALEATORIO PARA EL FOLIO *****************************//

    public void Random()
    {
        Random random = new Random();
        numberRandom = random.nextInt(9000)*99;
        codigoVerifi = String.valueOf(numberRandom);
        randomCodigoVerifi = codigoVerifi;
    }

/*    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI, new Handler());

        Intent notificationIntent = new Intent(this, FormIngresaPlaca.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);


        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.app911notification)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }*/

}


