package mx.gob.hidalgo.hidalgoestatal911.ui.MenuTransporte;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.gob.hidalgo.hidalgoestatal911.R;

public class TransporteMenu extends Fragment {

    private TransporteMenuViewModel mViewModel;

    public static TransporteMenu newInstance() {
        return new TransporteMenu();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.transporte_menu_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TransporteMenuViewModel.class);
        // TODO: Use the ViewModel
    }

}
