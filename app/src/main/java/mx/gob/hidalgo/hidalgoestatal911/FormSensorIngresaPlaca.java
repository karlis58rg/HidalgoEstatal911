package mx.gob.hidalgo.hidalgoestatal911;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import mx.gob.hidalgo.hidalgoestatal911.Servicio911.ServiceShake;
import pl.droidsonroids.gif.GifImageView;

public class FormSensorIngresaPlaca extends AppCompatActivity {
    Button inicia,detiene;
    String valorPlaca;
    public static EditText txtPlaca;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    TextView coordenadas,direccion,lblPlaca,lblRan, fin, instrucciones;
    public static int versionSDK;
    public static ImageView lblagita;
    public static  TextView emergencia;
    public static GifImageView gifRojo;
    public static ImageView bandaroja;
    public static ImageView paloma;
    private Activity activity;
    private static final int CODIGO_SOLICITUD_PERMISO = 1;
    private LocationManager locationManager;
    private Context context;
    int acceso = 0;
    AlertDialog alert = null;
    ImageView home;
    Boolean bandera ;
    Boolean banderaSire;
    String cargarInfoServicio;
    String cargarInfoPlaca;
    String cargarInfoServicioShake = "creado";
    String mensaje1,mensaje2;
    String direc;
    Double lat,lon;


    //********************** SENSOR *******************************//
    Intent mServiceIntent;
    private ServiceShake mSensorService;
    Context ctx;

    public Context getCtx() {
        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_form_sensor_ingresa_placa );
        cargarServicio();
        versionSDK = android.os.Build.VERSION.SDK_INT;
        Log.i("HEY", String.valueOf( versionSDK ) );



        inicia = (Button)findViewById( R.id.btnIniciar );
        detiene = (Button)findViewById( R.id.btnDetener );
        locationStart();

        txtPlaca = (EditText)findViewById(R.id.txtIngresaPlacaSensor);
        lblPlaca = (TextView) findViewById(R.id.lblPlacaIngresadaSensor);
        paloma = findViewById( R.id.imgPaloma);
        //manita = (GifImageView) findViewById( R.id.gifMano);
        gifRojo = (GifImageView) findViewById( R.id.gifSirena);
        bandaroja = (ImageView)findViewById( R.id.lblDeten );
        lblagita = (ImageView)findViewById(R.id.lblAgita);
        emergencia = (TextView)findViewById(R.id.lblEmergencia);
        fin = (TextView)findViewById(R.id.lblFin);
        instrucciones = (TextView)findViewById(R.id.lblInstrucciones);
        coordenadas = (TextView)findViewById(R.id.lblCoordenadasSensorPlaca);
        home = findViewById(R.id.imgHeaderCyHServ);

        paloma.setVisibility( View.GONE );
        gifRojo.setVisibility( View.GONE );
        //manita.setVisibility( View.VISIBLE );
        bandaroja.setVisibility( View.INVISIBLE);
        lblPlaca.setVisibility(View.GONE);
        detiene.setVisibility(View.INVISIBLE);
        emergencia.setVisibility(View.GONE);
        fin.setVisibility(View.INVISIBLE);
        lblagita.setVisibility(View.GONE);


        //************************* SERVICIO ********************************//
        ctx = this;

        mSensorService = new ServiceShake(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());


        //************************* SERIVCIO ********************************//
        context = getApplicationContext();
        activity = this;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(acceso == 0){
            solicitarPermisoLocalizacion();
        } else {
            Toast.makeText(getApplicationContext()," **GPS** ES OBLIGATORIO PARA EL CORRECTO FUNCIONAMIENTO DEL APLICATIVO",Toast.LENGTH_LONG).show();
        }

        if (cargarInfoServicio.equals(cargarInfoServicioShake)) {
            cargarPlaca();
            //Toast.makeText(getApplicationContext(), "YA TIENES UN SERVICIO EN EJECUCION CON LA PLACA: " + cargarInfoPlaca, Toast.LENGTH_LONG).show();
            lblPlaca.setText(cargarInfoPlaca);
            instrucciones.setVisibility(View.GONE);
            inicia.setVisibility(View.GONE);
            detiene.setVisibility(View.VISIBLE);
            paloma.setVisibility(View.VISIBLE);
            txtPlaca.setVisibility(View.GONE);
            lblagita.setVisibility(View.VISIBLE);
            emergencia.setVisibility(View.VISIBLE);
            lblPlaca.setVisibility(View.VISIBLE);
            fin.setVisibility(View.VISIBLE);
        }/*else{
            //Toast.makeText(getApplicationContext(),"SIN SERVICO",Toast.LENGTH_LONG).show();
        }*/

        inicia.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtPlaca.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"EL CAMPO **PLACA** ES OBLIGATORIO",Toast.LENGTH_LONG).show();
                }else{
                    startService( new Intent( FormSensorIngresaPlaca.this, ServiceShake.class ) );
                    valorPlaca = txtPlaca.getText().toString().toUpperCase();
                    lblPlaca.setText(valorPlaca);
                    guardar();
                    paloma.setVisibility(View.VISIBLE);
                    //manita.setVisibility(View.GONE);
                    lblagita.setVisibility(View.VISIBLE);
                    txtPlaca.setVisibility(View.GONE);
                    lblPlaca.setVisibility(View.VISIBLE);
                    inicia.setVisibility(View.GONE);
                    emergencia.setVisibility(View.VISIBLE);
                    fin.setVisibility(View.VISIBLE);
                    detiene.setVisibility(View.VISIBLE);
                    instrucciones.setVisibility(View.INVISIBLE);
                }
            }
        } );

        detiene.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertaDetenerServicio();
               /* stopService( new Intent( FormSensorIngresaPlaca.this, ServiceShake.class ) );
                onDestroy();
                Intent i = new Intent( FormSensorIngresaPlaca.this,MensajeSalidaUser.class);
                startActivity( i );
                System.exit( 0 );*/
            }
        } );
        home.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( FormSensorIngresaPlaca.this,MenuEmergencias.class );
                startActivity(i);
            }
        } );
    }

    private void guardar(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putString("placa",valorPlaca);
        editor.putString( "sdk", String.valueOf( versionSDK ));
        editor.commit();
        // Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }

    //******************************** METODOS DEL SERVICIO ****************************************//
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService( Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_tipo_emergencias, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
          //  return true;
        //}

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopService(new Intent(FormSensorIngresaPlaca.this, ServiceShake.class));
        Log.i("MAINACT", "onDestroy!");


    }


//************************************ PERMISOS GPS ***********************************************//

    public void solicitarPermisoLocalizacion(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(FormSensorIngresaPlaca.this, "Permisos Activados", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, CODIGO_SOLICITUD_PERMISO);
        }
    }

    private void alertaGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema de GPS esta desactivado, ¿Desea Activarlo?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        acceso = 1;
                        startActivity(new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        acceso = 1;
                        dialogInterface.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    private void alertaDetenerServicio(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿ESTÁ USTED SEGURO EN DETENER LA ALERTA?")
                .setCancelable(false)
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isMyServiceRunning( mSensorService.getClass())) {
                            eliminarServicio();
                            stopService( mServiceIntent );
                            stopService( new Intent( FormSensorIngresaPlaca.this, ServiceShake.class ) );
                            onDestroy();
                            System.exit( 0 );
                            Log.i("HEY", "CON SERVICIO INICIADO");
                        }else{
                            eliminarServicio();
                            Intent intent = new Intent( FormSensorIngresaPlaca.this, MenuEmergencias.class );
                            startActivity( intent );
                            finish();
                            Log.i("HEY", "SIN SERVICIO");
                        }
                    }
                })
                .setNegativeButton("NO, CONTINUAR CON LA ALERTA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    public boolean checarStatusPermiso(int resultado){
        if(resultado == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CODIGO_SOLICITUD_PERMISO :
                int resultado = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

                if(checarStatusPermiso(resultado)) {
                    if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {
                        alertaGPS();
                    }
                } else {
                    Toast.makeText(activity, "No estan activos los permisos", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("SI SALE DE ESTÁ PANTALLA, DA POR TERMINADA LA ALERTA")
                .setCancelable(false)
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isMyServiceRunning( mSensorService.getClass())) {
                            eliminarServicio();
                            stopService( mServiceIntent );
                            stopService( new Intent( FormSensorIngresaPlaca.this, ServiceShake.class ) );
                            onDestroy();
                            System.exit( 0 );
                            Log.i("HEY", "CON SERVICIO INICIADO");
                        }else{
                            eliminarServicio();
                            Intent intent = new Intent( FormSensorIngresaPlaca.this, MenuEmergencias.class );
                            startActivity( intent );
                            finish();
                            Log.i("HEY", "SIN SERVICIO");
                        }
                    }
                })
                .setNegativeButton("NO, CONTINUAR CON LA ALERTA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    private void cargarServicio(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        cargarInfoServicio = share.getString("servicio","");
        //Toast.makeText(getApplicationContext(),cargarInfoServicio,Toast.LENGTH_LONG).show();
    }

    private void cargarPlaca() {
        share = getSharedPreferences("main", MODE_PRIVATE);
        cargarInfoPlaca = share.getString("placa", "");
        //Toast.makeText(getApplicationContext(),cargarInfoPlaca,Toast.LENGTH_LONG).show();
    }

    private void eliminarServicio(){
        SharedPreferences settings = context.getSharedPreferences("main", Context.MODE_PRIVATE);
        settings.edit().remove("servicio").commit();
        //Toast.makeText(getApplicationContext(),"Dato Eliminado",Toast.LENGTH_LONG).show();
    }

    /***********************************************************************************************************************/
    //Apartir de aqui empezamos a obtener la direciones y coordenadas
    public void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        FormSensorIngresaPlaca.Localizacion Local = new FormSensorIngresaPlaca.Localizacion();
        Local.setFormSensorIngresaPlaca(this);
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

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    direc = DirCalle.getAddressLine(0);
                    /*municipio = DirCalle.getLocality();
                    estado = DirCalle.getAdminArea();
                    if(municipio != null) {
                        municipio = DirCalle.getLocality();
                    }else{
                        municipio = "SIN INFORMACION";
                    }*/
                    Log.i("HERE", "dir" + direc);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        FormSensorIngresaPlaca formSensorIngresaPlaca;
        public FormSensorIngresaPlaca getFormSensorIngresaPlaca() {
            return formSensorIngresaPlaca;
        }
        public void setFormSensorIngresaPlaca(FormSensorIngresaPlaca formSensorIngresaPlaca1) {
            this.formSensorIngresaPlaca = formSensorIngresaPlaca1;
        }
        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.getLatitude();
            loc.getLongitude();
            lat = loc.getLatitude();
            lon = loc.getLongitude();
            String Text = "Lat = "+ loc.getLatitude() + "\n Long = " + loc.getLongitude();
            mensaje1 = Text;
            coordenadas.setText(direc+" "+mensaje1);
            Log.i("HERE", mensaje1);
            this.formSensorIngresaPlaca.setLocation(loc);
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
}
