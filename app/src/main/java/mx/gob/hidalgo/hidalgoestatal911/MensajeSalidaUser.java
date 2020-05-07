package mx.gob.hidalgo.hidalgoestatal911;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MensajeSalidaUser extends AppCompatActivity {
    ImageButton salidaUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_mensaje_salida_user );

        salidaUser = (ImageButton)findViewById( R.id.btnSalidaRegresarUser );

        salidaUser.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( MensajeSalidaUser.this,MenuEmergencias.class );
                startActivity( i );
            }
        } );
    }
}
