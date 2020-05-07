package mx.gob.hidalgo.hidalgoestatal911;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.Random;

public class FormAutenticacion extends AppCompatActivity {
    Toolbar toolbar;
    EditText txtPhone,txtCode;
    Button btnEnviar,btnVerificar,btnLeyenda;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    String numero;
    String codigoVerifi;
    int codigo;
    int number;
    private Activity activity;
    private static final int CODIGO_SOLICITUD_PERMISO = 1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_autenticacion);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.hidalgo);


        txtPhone = (EditText)findViewById(R.id.txtNumeroTelefonico);
        txtCode = (EditText)findViewById(R.id.txtOTP);
        btnEnviar = (Button)findViewById(R.id.btnEnviarNumero);
        btnVerificar = (Button)findViewById(R.id.btnVerificarCodigo);
        btnLeyenda = (Button)findViewById(R.id.btnVerAvisoPrivacidad);
        Random();
        solicitarPermisosCamera();

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtPhone.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"CAMPO OBLIGATORIO",Toast.LENGTH_LONG).show();
                }else if(txtPhone.getText().length() < 10){
                    Toast.makeText(getApplicationContext(),"LO SENTIMOS DEBE INGRESAR UN NÚMERO VALIDO",Toast.LENGTH_LONG).show();
                }else{
                    numero = txtPhone.getText().toString();
                    sendMensaje(numero,codigoVerifi);
                }
            }
        });

        btnVerificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtCode.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"CAMPO OBLIGATORIO",Toast.LENGTH_LONG).show();
                }else{
                    codigo = Integer.parseInt(txtCode.getText().toString());
                    if (codigo == number){
                        Toast.makeText(getApplicationContext(),"BIENVENIDO",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(FormAutenticacion.this,FormFotoUser.class);
                        startActivity(i);
                    }else{
                        Toast.makeText(getApplicationContext(),"LO SENTIMOS EL CODIGO INGRESADO ES INCORRECTO",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnLeyenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*URL AVISO DE PRIVACIDAD DE GOBIERNO*/
                Uri uri = Uri.parse("http://gobierno.hidalgo.gob.mx/AvisoPrivacidad");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
        });
    }

    private void sendMensaje(String numero, String mensaje)
    {
        try{
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(numero,null,mensaje,null,null);
            guardar();
            txtPhone.setText("");
            Toast.makeText(getApplicationContext(),"MENSAJE DE VERIFICACIÓN ENVIADO",Toast.LENGTH_LONG).show();

        }catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),"MENSAJE NO ENVIADO, NÚMERO INCORRECTO",Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public void Random()
    {
        Random random = new Random();
        number = random.nextInt(9000)*99;
        TextView lblRan = (TextView)findViewById(R.id.lblRandom);
        codigoVerifi = String.valueOf(number);
        lblRan.setText(codigoVerifi);
    }

    private void guardar(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putString("telefono",txtPhone.getText().toString());
        editor.commit();
        //Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }

    //***************************** SE OPTIENEN TODOS LOS PERMISOS AL INICIAR LA APLICACIÓN *********************************//
    public void solicitarPermisosCamera(){
        Toast.makeText(getApplicationContext(),"Permisos",Toast.LENGTH_LONG).show();
       /* if (ContextCompat.checkSelfPermission(FormAutenticacion.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FormAutenticacion.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(FormAutenticacion.this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FormAutenticacion.this,Manifest
                .permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FormAutenticacion.this, Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED &&ActivityCompat.checkSelfPermission(FormAutenticacion.this,Manifest.permission.VIBRATE) !=PackageManager.PERMISSION_GRANTED &&  ActivityCompat.checkSelfPermission(FormAutenticacion.this, Manifest.permission.RECORD_AUDIO) !=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FormAutenticacion.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.RECORD_AUDIO,Manifest.permission.VIBRATE}, 1000);

        }*/
    }
}
