package mx.gob.hidalgo.hidalgoestatal911;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class Baner extends AppCompatActivity {

    CarouselView baner;
    int[] carrusel = {R.drawable.baner1,R.drawable.baner2,R.drawable.baner3,R.drawable.baner4};
    Button aceptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baner);


        baner = (CarouselView)findViewById(R.id.baner);
        baner.setPageCount(carrusel.length);
        aceptar = (Button)findViewById(R.id.btnIngresar);
        baner.setImageListener(imageListener);


    }
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(carrusel[position]);

        }
    };
}
