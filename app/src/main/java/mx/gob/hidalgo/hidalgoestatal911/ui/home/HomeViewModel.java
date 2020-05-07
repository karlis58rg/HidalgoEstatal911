package mx.gob.hidalgo.hidalgoestatal911.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Tipo de Emergencia");
    }

    public LiveData<String> getText() {
        return mText;
    }
}