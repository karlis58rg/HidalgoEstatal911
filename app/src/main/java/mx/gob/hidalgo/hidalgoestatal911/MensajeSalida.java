package mx.gob.hidalgo.hidalgoestatal911;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MensajeSalida extends AppCompatActivity {
    String folio;
    ImageButton menu;
    TextView folioSalida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_mensaje_salida );

        menu = (ImageButton)findViewById( R.id.btnSalidaRegresar);
        folioSalida = (TextView)findViewById( R.id.lblFolio );

        folio = getIntent().getExtras().getString("valorRandom");
        folioSalida.setText( folio);

        menu.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( MensajeSalida.this,MenuEmergencias.class );
                startActivity(i);
            }
        } );

    }
}
