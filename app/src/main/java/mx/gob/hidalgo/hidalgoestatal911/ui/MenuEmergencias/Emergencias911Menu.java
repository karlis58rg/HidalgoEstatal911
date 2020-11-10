package mx.gob.hidalgo.hidalgoestatal911.ui.MenuEmergencias;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import mx.gob.hidalgo.hidalgoestatal911.MedicoMenu;
import mx.gob.hidalgo.hidalgoestatal911.MensajeSalida;
import mx.gob.hidalgo.hidalgoestatal911.MenuEmergencias;
import mx.gob.hidalgo.hidalgoestatal911.ProteccionMenu;
import mx.gob.hidalgo.hidalgoestatal911.R;
import mx.gob.hidalgo.hidalgoestatal911.SeguridadMenu;

public class Emergencias911Menu extends Fragment {

    private ImageButton medico,protecCivil,seguridad;


    private Emergencias911MenuViewModel mViewModel;

    public static Emergencias911Menu newInstance() {
        return new Emergencias911Menu();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(Emergencias911MenuViewModel.class);
        View root = inflater.inflate(R.layout.emergencias911_menu_fragment, container, false);

        //return inflater.inflate(R.layout.emergencias911_menu_fragment, container, false);
        //****************************************************************************************************//
        medico = root.findViewById(R.id.imgMedica);
        seguridad = root.findViewById(R.id.imgSeguridad);
        protecCivil = root.findViewById(R.id.imgProCi);



        medico.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent( getActivity(), MedicoMenu.class );
                startActivity(i);
            }
        } );

        seguridad.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( getActivity(), SeguridadMenu.class );
                startActivity(i);

            }
        } );

        protecCivil.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( getActivity(), ProteccionMenu.class );
                startActivity(i);
            }
        } );

        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(Emergencias911MenuViewModel.class);
        // TODO: Use the ViewModel
    }

}
