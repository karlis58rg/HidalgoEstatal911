package mx.gob.hidalgo.hidalgoestatal911.ui.MenuAvisoPrivacidad;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import mx.gob.hidalgo.hidalgoestatal911.R;

public class AvisoPrivacidadMenu extends Fragment {

    private AvisoPrivacidadMenuViewModel mViewModel;
    static WebView wvAvisoPrivacidad;

    public static AvisoPrivacidadMenu newInstance() {
        return new AvisoPrivacidadMenu();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:{
                    webViewGoBack();
                }break;
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.aviso_privacidad_menu_fragment, container, false);

        //************************************************************************//

        wvAvisoPrivacidad = root.findViewById(R.id.wvAvisoPrivacidad);

        wvAvisoPrivacidad.setWebViewClient(new WebViewClient());
        wvAvisoPrivacidad.loadUrl("http://gobierno.hidalgo.gob.mx/AvisoPrivacidad");

        WebSettings webSettings = wvAvisoPrivacidad.getSettings();
        webSettings.setJavaScriptEnabled(true);


        wvAvisoPrivacidad.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && wvAvisoPrivacidad.canGoBack()) {
                    handler.sendEmptyMessage(1);
                    return true;
                }

                return false;
            }
        });
        //************************************************************************//
        return root;
    }

    private void webViewGoBack(){
        wvAvisoPrivacidad.goBack();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AvisoPrivacidadMenuViewModel.class);
        // TODO: Use the ViewModel
    }

}


