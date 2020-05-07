package mx.gob.hidalgo.hidalgoestatal911;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import mx.gob.hidalgo.hidalgoestatal911.Servicio911.MyServicio;

public class FormPlaca extends AppCompatActivity {
    Button iniciar,detener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_placa);

        iniciar = (Button)findViewById(R.id.btnActivar);
        detener = (Button)findViewById(R.id.btnDesactivar);

        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startService(new Intent(FormPlaca.this, MyServicio.class));
            }
        });

        detener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(FormPlaca.this, MyServicio.class));
            }
        });
    }
}
