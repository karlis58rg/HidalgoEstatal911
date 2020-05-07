package mx.gob.hidalgo.hidalgoestatal911;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.security.PublicKey;
import java.util.Random;

import mx.gob.hidalgo.hidalgoestatal911.Servicio911.MyServicio;
import mx.gob.hidalgo.hidalgoestatal911.Shake.ShakeService;
//import pl.droidsonroids.gif.GifImageView;

public class FormIngresaPlaca extends AppCompatActivity {
    Toolbar toolbar;
    public static EditText txtPlaca;
    TextView coordenadas,direccion,lblPlaca,lblRan;
    Button placaEnviar;
   // public static GifImageView gifRojo,manita;
    String valorPlaca;
    Button servicio;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    ImageButton palomaVerde;
    private Activity activity;
    private static final int CODIGO_SOLICITUD_PERMISO = 1;
    private LocationManager locationManager;
    private Context context;
    int acceso = 0;
    AlertDialog alert = null;
    public static int versionSDK;

    //********************** SENSOR *******************************//
    Intent mServiceIntent;
    private MyServicio mSensorService;
    Context ctx;

    public Context getCtx() {
        return ctx;
    }

    public static TextView tvShakeService;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_ingresa_placa);
        versionSDK = android.os.Build.VERSION.SDK_INT;
        Log.i("HEY", String.valueOf( versionSDK ) );

        servicio = (Button)findViewById(R.id.btnEndService);
        direccion = (TextView)findViewById(R.id.lblUbicacion);
        lblRan = (TextView)findViewById(R.id.lblRandom);
        lblPlaca = (TextView) findViewById(R.id.lblPlacaIngresada);
        coordenadas = (TextView) findViewById(R.id.lblCoordenadas);
        direccion = (TextView) findViewById(R.id.lblUbicacion);
        placaEnviar = (Button)findViewById(R.id.btnInsertarPlaca);
        txtPlaca = (EditText)findViewById(R.id.txtIngresaPlaca);
        palomaVerde = (ImageButton)findViewById(R.id.impPaloma);
        //gifRojo = (GifImageView)findViewById(R.id.gifRojo);
        //manita =(GifImageView)findViewById(R.id.gifMano);

        //gifRojo.setVisibility(View.GONE);
        palomaVerde.setVisibility(View.GONE);

        //************************* SERIVCIO ********************************//
        ctx = this;

        mSensorService = new MyServicio(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());

        tvShakeService = findViewById(R.id.tvShakeService);
        tvShakeService.setText("Prueba");

        //*********************** TERMINA EL SERVICIO***************************//

        ///validacion de permisos

        context = getApplicationContext();
        activity = this;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if(acceso == 0){
            solicitarPermisoLocalizacion();
        } else {
            Toast.makeText(getApplicationContext()," **GPS** ES OBLIGATORIO PARA EL CORRECTO FUNCIONAMIENTO DEL APLICATIVO",Toast.LENGTH_LONG).show();
        }

        servicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMyServiceRunning(mSensorService.getClass())) {
                    stopService(mServiceIntent);
                    stopService(new Intent(FormIngresaPlaca.this, MyServicio.class));
                    onDestroy();
                    Intent i = new Intent( FormIngresaPlaca.this,MensajeSalidaUser.class);
                    startActivity( i );
                }
                Snackbar.make(view, "Servicio detenido", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        placaEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtPlaca.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"EL CAMPO **PLACA** ES OBLIGATORIO",Toast.LENGTH_LONG).show();
                }else if (!isMyServiceRunning(mSensorService.getClass())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //startForegroundService(mServiceIntent);
                        startService(new Intent(FormIngresaPlaca.this, MyServicio.class));
                    }
                    //manita.setVisibility(View.GONE);
                    palomaVerde.setVisibility(View.VISIBLE);
                    valorPlaca = txtPlaca.getText().toString();
                    lblPlaca.setText(valorPlaca);
                    guardar();
                    txtPlaca.setText("");
                }
            }
        });
    }

    //******************************** METODOS DEL SERVICIO ****************************************//
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        stopService(new Intent(FormIngresaPlaca.this, MyServicio.class));
        //stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }

    private void eliminar(){
        editor.remove("ramdom").commit();
        editor.remove("placa").commit();
        editor.remove("direccion").commit();
        editor.remove("municipio").commit();
        editor.remove("estado").commit();
        editor.remove("latitud").commit();
        editor.remove("longitud").commit();
        Toast.makeText(getApplicationContext(),"Dato Eliminado",Toast.LENGTH_LONG).show();
    }

    private void guardar(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putString("placa",valorPlaca);
        editor.putString( "sdk", String.valueOf( versionSDK ));
        editor.commit();
        // Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }

    public void solicitarPermisoLocalizacion(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(FormIngresaPlaca.this, "Permisos Activados", Toast.LENGTH_SHORT).show();
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
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        alertaGPS();
                    }
                } else {
                    Toast.makeText(activity, "No estan activos los permisos", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
