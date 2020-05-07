package mx.gob.hidalgo.hidalgoestatal911;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MedicoMenu extends AppCompatActivity {
    ImageButton menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_medico_menu );
        menu = (ImageButton)findViewById( R.id.btnHomeMedico );
        menu.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( MedicoMenu.this,MenuEmergencias.class );
                startActivity(i);

            }
        } );
    }
}
