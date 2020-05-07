package mx.gob.hidalgo.hidalgoestatal911;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SeguridadMenu extends AppCompatActivity {
    ImageButton menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_seguridad_menu );
        menu = (ImageButton)findViewById( R.id.btnHomeSeguridad );

        menu.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( SeguridadMenu.this,MenuEmergencias.class );
                startActivity(i);

            }
        } );
    }
}
