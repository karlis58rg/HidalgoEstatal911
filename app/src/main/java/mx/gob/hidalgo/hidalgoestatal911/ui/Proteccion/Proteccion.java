package mx.gob.hidalgo.hidalgoestatal911.ui.Proteccion;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.gob.hidalgo.hidalgoestatal911.R;

public class Proteccion extends Fragment {

    private ProteccionViewModel mViewModel;

    public static Proteccion newInstance() {
        return new Proteccion();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.proteccion_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ProteccionViewModel.class);
        // TODO: Use the ViewModel
    }

}
