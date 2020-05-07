package mx.gob.hidalgo.hidalgoestatal911;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import mx.gob.hidalgo.hidalgoestatal911.Shake.ShakeService;

public class FormPlacaQr extends AppCompatActivity {

    private ImageButton btnScan;
    private TextView txtResult;
    TextView coordenadas,direccion,lblRan;
    Button btnAgregarPlaca,servi,btnEndService;
    int numberRandom;
    String codigoVerifi;
    String randomCodigoVerifi;
    String valorPlacaQR;
    Toolbar toolbar;
    SharedPreferences share;
    SharedPreferences.Editor editor;

    //********************** SENSOR *******************************//
    Intent mServiceIntent;
    private ShakeService mSensorService;
    Context ctx;

    public Context getCtx() {
        return ctx;
    }

    public static TextView tvShakeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_placa_qr);

        final Activity activity = this;
        txtResult = (TextView)findViewById(R.id.lblPlacaQR);
        btnScan = (ImageButton) findViewById(R.id.btnQRScan);
        lblRan = (TextView) findViewById(R.id.lblRandom);
        btnEndService =(Button)findViewById(R.id.btnEndServiceQR);
        coordenadas = (TextView) findViewById(R.id.lblCoordenadas);
        direccion = (TextView) findViewById(R.id.lblUbicacion);

        //************************* SERIVCIO ********************************//
        ctx = this;

        mSensorService = new ShakeService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());


        tvShakeService = findViewById(R.id.tvShakeService);
        tvShakeService.setText("Prueba");

        if (!isMyServiceRunning(mSensorService.getClass())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(mServiceIntent);
            }
        }

       /* btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-----SE ABRE EL LECTOR DE QR EN UN NUEVO INTENT AUTOMATICAMENTE-----//
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });*/
        btnEndService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyServiceRunning(mSensorService.getClass())) {
                    stopService(mServiceIntent);
                    onDestroy();
                }
                Snackbar.make(v, "Servicio detenido", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null)
        {
            if (result.getContents() == null)
            {
                //----SI NO SE REALIZA EL ESCANEO DEL QR, MANDA EL SIGUIENTE  MENSAJE----//
                Toast.makeText(this, "CANCELO LA EJECUCIÓN DEL ESCANER",Toast.LENGTH_LONG).show();
            }
            else
            {
                Random();
                //----EN CASO DE REALIZAR EL ESCANEO DEL QR, SE OBTIENE LOS DATOS DEL QR----//
                //Toast.makeText(this,result.getContents(),Toast.LENGTH_LONG).show();
                //----SE ASIGNA EL VALOR DEL QR A UNA VARIABLE----//
               // String rest = result.getContents();
                //----SE CONCATENA EL CONTENIDO DEL QR EN UN TXT----//
                txtResult.setText(rest);
                valorPlacaQR = txtResult.getText().toString();
                guardar();
            }
        }else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }*/
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
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }

    //********************* GENERA EL NÚMERO ALEATORIO PARA EL FOLIO *****************************//
    public void Random()
    {
        Random random = new Random();
        numberRandom = random.nextInt(9000)*99;
        codigoVerifi = String.valueOf(numberRandom);
        randomCodigoVerifi = codigoVerifi;
        lblRan.setText("C5i2019"+randomCodigoVerifi);
    }

    private void guardar(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putString("ramdom",randomCodigoVerifi);
        editor.putString("placa",valorPlacaQR);
        editor.commit();
        // Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }
}
