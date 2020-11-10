package mx.gob.hidalgo.hidalgoestatal911;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class FormServicios extends AppCompatActivity {
    ImageView home;
    private ImageButton medico,protecCivil,seguridad,serviPublicos;
    String idMedico,idProCivil,idSeguridad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_form_servicios );

        home = findViewById(R.id.imgHeaderCyHEmergencia);
        medico = (ImageButton)findViewById(R.id.btnEmergenciaMedica);
        protecCivil = (ImageButton)findViewById(R.id.btnEmergenciaProCivil);
        seguridad = (ImageButton)findViewById(R.id.btnEmergenciaSeguridad);

        medico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idMedico = "60122";
                Intent i = new Intent(FormServicios.this, FormNotificacionEmergencia.class);
                i.putExtra("valor",idMedico);
                startActivity(i);
                //Toast.makeText(getActivity(), "MEDICO", Toast.LENGTH_SHORT).show();
            }
        });

        seguridad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idSeguridad = "60121";
                Intent i = new Intent(FormServicios.this, FormNotificacionEmergencia.class);
                i.putExtra("valor",idSeguridad);
                startActivity(i);
                //Toast.makeText(getActivity(), "SEGURIDAD", Toast.LENGTH_SHORT).show();
            }
        });

        protecCivil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idProCivil = "60123";
                Intent i = new Intent(FormServicios.this, FormNotificacionEmergencia.class);
                i.putExtra("valor",idProCivil);
                startActivity(i);
                //Toast.makeText(getActivity(), "PROTECCIÓN CIVIL", Toast.LENGTH_SHORT).show();
            }
        });

        home.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( FormServicios.this,MenuEmergencias.class );
                startActivity(i);
            }
        } );

    }
}



