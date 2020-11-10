package mx.gob.hidalgo.hidalgoestatal911.ui.Menu;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import mx.gob.hidalgo.hidalgoestatal911.FormSensorIngresaPlaca;
import mx.gob.hidalgo.hidalgoestatal911.FormServicios;
import mx.gob.hidalgo.hidalgoestatal911.R;

public class EmergenciasMenu extends Fragment {


    private static  final int REQUEST_CALL = 1;
    ImageView btnViewEmergencias,btnViewTransporte,btnViewLlamada;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    String cargarInfoSexo;
    String sexoM,sexoF;
    TextView emergencia,destino,llamada;
    TextView emergenciaRosa,destinoRosa,llamadaRosa;


    private EmergenciasMenuViewModel mViewModel;

    public static EmergenciasMenu newInstance() {
        return new EmergenciasMenu();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.emergencias_menu_fragment, container, false);
        mViewModel = ViewModelProviders.of(this).get(EmergenciasMenuViewModel.class);
        View root = inflater.inflate(R.layout.emergencias_menu_fragment, container, false);

        //******************************* EVENTOS DE LOS BOTONES *****************************************//

        btnViewEmergencias = root.findViewById(R.id.btnEmergencias);
        btnViewTransporte = root.findViewById(R.id.btnTransporteSeguro);
        btnViewLlamada = root.findViewById(R.id.btn911);

        btnViewEmergencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();

            }
        });
        btnViewTransporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FormServicios.class);
                startActivity(i);

            }
        });
        btnViewLlamada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FormSensorIngresaPlaca.class);
                startActivity(i);

            }
        });


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(EmergenciasMenuViewModel.class);
        // TODO: Use the ViewModel
    }

    //*************************** REALIZA LLAMADA AL 911 ********************************************//

    private void makePhoneCall() {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);

        }else{
            String dial = "tel:911";
            Intent call = new Intent(Intent.ACTION_CALL);
            call.setData(Uri.parse(dial));
            startActivity(call);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall();

            }else{
                Toast.makeText(getActivity().getApplicationContext(),"Permisos Denegados",Toast.LENGTH_SHORT).show();
            }

        }
    }


}
