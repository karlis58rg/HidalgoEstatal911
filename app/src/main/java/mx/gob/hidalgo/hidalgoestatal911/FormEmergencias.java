package mx.gob.hidalgo.hidalgoestatal911;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.w3c.dom.Text;

public class FormEmergencias extends AppCompatActivity {
    Toolbar toolbar;
    ImageButton btnViewEmergencias,btnViewTransporte,btnViewLlamada;
    ImageButton btnViewEmergenciasRosa,btnViewTransporteRosa,btnViewLlamadaRosa;
    private static  final int REQUEST_CALL = 1;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    String cargarInfoSexo;
    String sexoM,sexoF;
    TextView emergencia,destino,llamada;
    TextView emergenciaRosa,destinoRosa,llamadaRosa;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_emergencias);
        cargar();
        sexoM = "MASCULINO";
        sexoF = "FEMENINO";

        btnViewEmergencias = (ImageButton)findViewById(R.id.btnEmergencias);
        btnViewTransporte = (ImageButton)findViewById(R.id.btnTransporteSeguro);
        btnViewLlamada = (ImageButton)findViewById(R.id.btn911);



        btnViewEmergenciasRosa = (ImageButton)findViewById(R.id.btnEmergenciasRosa);
        btnViewTransporteRosa = (ImageButton)findViewById(R.id.btnTransporteSeguroRosa);
        btnViewLlamadaRosa = (ImageButton)findViewById(R.id.btn911Rosa);

        emergenciaRosa = (TextView)findViewById(R.id.lblNotificarEmergenciasRosa);
        destinoRosa = (TextView)findViewById(R.id.lblTransporteSeguroRosa);
        llamadaRosa = (TextView)findViewById(R.id.lblLlamada911Rosa);


        btnViewEmergencias.setVisibility(View.GONE);
        btnViewTransporte.setVisibility(View.GONE);
        btnViewLlamada.setVisibility(View.GONE);

        emergencia.setVisibility(View.GONE);
        destino.setVisibility(View.GONE);
        llamada.setVisibility(View.GONE);

        btnViewEmergenciasRosa.setVisibility(View.GONE);
        btnViewTransporteRosa.setVisibility(View.GONE);
        btnViewLlamadaRosa.setVisibility(View.GONE);

        emergenciaRosa.setVisibility(View.GONE);
        destinoRosa.setVisibility(View.GONE);
        llamadaRosa.setVisibility(View.GONE);

        if(cargarInfoSexo.equals(sexoM)){
            btnViewEmergencias.setVisibility(View.VISIBLE);
            btnViewTransporte.setVisibility(View.VISIBLE);
            btnViewLlamada.setVisibility(View.VISIBLE);
            emergencia.setVisibility(View.VISIBLE);
            destino.setVisibility(View.VISIBLE);
            llamada.setVisibility(View.VISIBLE);
        }else if (cargarInfoSexo.equals(sexoF)){
            btnViewEmergenciasRosa.setVisibility(View.VISIBLE);
            btnViewTransporteRosa.setVisibility(View.VISIBLE);
            btnViewLlamadaRosa.setVisibility(View.VISIBLE);
            emergenciaRosa.setVisibility(View.VISIBLE);
            destinoRosa.setVisibility(View.VISIBLE);
            llamadaRosa.setVisibility(View.VISIBLE);
        }

        btnViewEmergencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FormEmergencias.this,FormTipoEmergencias.class);
                startActivity(i);
            }
        });
        btnViewTransporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FormEmergencias.this,FormIngresaPlaca.class);
                startActivity(i);

               }
        });
        btnViewLlamada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        btnViewEmergenciasRosa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FormEmergencias.this,FormTipoEmergencias.class);
                startActivity(i);
            }
        });
        btnViewTransporteRosa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FormEmergencias.this,FormIngresaPlaca.class);
                startActivity(i);

            }
        });
        btnViewLlamadaRosa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

    }

    //*************************** REALIZA LLAMADA AL 911 ********************************************//

    private void makePhoneCall() {
        if(ContextCompat.checkSelfPermission(FormEmergencias.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FormEmergencias.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);

        }else{
            String dial = "tel:911";
            Intent call = new Intent(Intent.ACTION_CALL);
            call.setData(Uri.parse(dial));
            startActivity(call);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall();

            }else{
                Toast.makeText(this,"Permisos Denegados",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void cargar(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        cargarInfoSexo = share.getString("sexo","");
        //Toast.makeText(getApplicationContext(),cargarInfoTelefono,Toast.LENGTH_LONG).show();
    }
}
