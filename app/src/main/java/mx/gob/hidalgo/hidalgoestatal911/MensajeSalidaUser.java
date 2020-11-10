package mx.gob.hidalgo.hidalgoestatal911;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MensajeSalidaUser extends AppCompatActivity {
    ImageView salidaUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_mensaje_salida_user );

        salidaUser = findViewById( R.id.imgHeaderCyHReporteSalidaUser );

        salidaUser.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( MensajeSalidaUser.this,MenuEmergencias.class );
                startActivity( i );
            }
        } );
    }
}
