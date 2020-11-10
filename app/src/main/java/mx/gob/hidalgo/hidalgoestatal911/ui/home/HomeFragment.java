package mx.gob.hidalgo.hidalgoestatal911.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import mx.gob.hidalgo.hidalgoestatal911.FormNotificacionEmergencia;
import mx.gob.hidalgo.hidalgoestatal911.R;

public class HomeFragment extends Fragment {
    private ImageButton medico,protecCivil,seguridad,serviPublicos;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    String idMedico,idProCivil,idSeguridad,idServiPublicos;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.emergencias_menu_fragment, container, false);

        //******************************* EVENTOS DE LOS BOTONES *****************************************//

        medico = root.findViewById(R.id.btnEmergenciaMedica);
        protecCivil = root.findViewById(R.id.btnEmergenciaProCivil);
        seguridad = root.findViewById(R.id.btnEmergenciaSeguridad);
        //serviPublicos = root.findViewById(R.id.btnEmergenciaServPublicos);

        medico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idMedico = "60122";
                Intent i = new Intent(getActivity() , FormNotificacionEmergencia.class);
                i.putExtra("valor",idMedico);
                startActivity(i);
                //Toast.makeText(getActivity(), "MEDICO", Toast.LENGTH_SHORT).show();
            }
        });
        protecCivil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idProCivil = "60123";
                Intent i = new Intent(getActivity() , FormNotificacionEmergencia.class);
                i.putExtra("valor",idProCivil);
                startActivity(i);
                //Toast.makeText(getActivity(), "PROTECCIÓN CIVIL", Toast.LENGTH_SHORT).show();
            }
        });
        seguridad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idSeguridad = "60121";
                Intent i = new Intent(getActivity() , FormNotificacionEmergencia.class);
                i.putExtra("valor",idSeguridad);
                startActivity(i);
                //Toast.makeText(getActivity(), "SEGURIDAD", Toast.LENGTH_SHORT).show();
            }
        });
        /*serviPublicos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idServiPublicos = "60124";
                Intent i = new Intent(getActivity() , FormNotificacionEmergencia.class);
                i.putExtra("valor",idServiPublicos);
                startActivity(i);
                //Toast.makeText(getActivity(), "SERVICIOS PUBLICOS", Toast.LENGTH_SHORT).show();
            }
        });*/

        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }
}