package mx.gob.hidalgo.hidalgoestatal911;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class Splash extends AppCompatActivity {
    SharedPreferences shared;
    SharedPreferences.Editor editor;
    String cargarInfoTelefono,cargarInfoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //eliminar();
        cargar();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (cargarInfoTelefono.isEmpty() != false){
                    //Toast.makeText(getApplicationContext(),cargarInfo,Toast.LENGTH_LONG).show();
                    Intent i = new Intent(Splash.this,FormFotoUser.class);
                    startActivity(i);
                    finish();
                }else{
                    //Toast.makeText(getApplicationContext(),"EXISTE EL DATO",Toast.LENGTH_LONG).show();
                    //Intent i = new Intent(Splash.this,FormEmergencias.class);
                    Intent i = new Intent(Splash.this,MenuEmergencias.class);
                    startActivity(i);
                    finish();
                }
            }
        },4000);
    }

    private void cargar(){
        shared = getSharedPreferences("main",MODE_PRIVATE);
        cargarInfoTelefono = shared.getString("telefono","");
        cargarInfoUsuario = shared.getString("nombre","");
        //Toast.makeText(getApplicationContext(),cargarInfoTelefono,Toast.LENGTH_LONG).show();
    }

    private void eliminar(){
        editor.remove("telefono").commit();
        editor.remove("nombre").commit();
        editor.remove("sexo").commit();
        Toast.makeText(getApplicationContext(),"Dato Eliminado",Toast.LENGTH_LONG).show();
    }
}
