package mx.gob.hidalgo.hidalgoestatal911.ui.MenuSalir;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.gob.hidalgo.hidalgoestatal911.R;

public class Salir extends Fragment {

    private SalirViewModel mViewModel;

    public static Salir newInstance() {
        return new Salir();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.salir_fragment, container, false);
        int p = android.os.Process.myPid();
        android.os.Process.killProcess(p);
        getActivity().finishAffinity();
        System.exit(0);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SalirViewModel.class);
        // TODO: Use the ViewModel
    }

}
