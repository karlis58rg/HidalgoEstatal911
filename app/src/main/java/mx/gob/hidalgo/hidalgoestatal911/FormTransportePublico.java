package mx.gob.hidalgo.hidalgoestatal911;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class FormTransportePublico extends AppCompatActivity {

    ImageButton btnPlacaT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_transporte_publico);

        btnPlacaT = (ImageButton)findViewById(R.id.btnPlaca);

        btnPlacaT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent i = new Intent(FormTransportePublico.this,FormIngresaPlaca.class);
                //startActivity(i);
               // Intent i = new Intent(FormTransportePublico.this,FormIngresaPlaca.class);
                //startActivity(i);


            }
        });
    }
}
