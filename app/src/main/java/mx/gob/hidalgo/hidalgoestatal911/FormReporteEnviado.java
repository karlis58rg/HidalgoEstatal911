package mx.gob.hidalgo.hidalgoestatal911;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class FormReporteEnviado extends AppCompatActivity {
    ImageButton home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_form_reporte_enviado );
        home = (ImageButton)findViewById( R.id.imgHomeReporte );

        home.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( FormReporteEnviado.this,MenuEmergencias.class );
                startActivity( i );
            }
        } );
    }
}
