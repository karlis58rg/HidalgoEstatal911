package mx.gob.hidalgo.hidalgoestatal911;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import android.os.SystemClock;
import android.widget.Chronometer;

public class FormNotificacion911 extends Fragment implements OnMapReadyCallback {

    private OnFragmentInteractionListener mListener;
    GoogleMap map;
    Button btn_address;
    Boolean actul_posicion = true;
    Marker marker = null;
    Double lat_origen, lon_origen;
    String myCity = "";
    TextView tv_add;
    Button btn_reg;
    ImageButton detenerAudio;
    LatLng aux;
    Location aux_loc;
    AlertDialog alert = null;
    int contador1;
    Chronometer tiempo;
    Boolean correr = false;
    long detenerse;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    private ImageButton principal,photo,video,audio,recordAu,playAu;
    private ImageView imgimagen,home;
    private String cadena,cadenaVideo,cadenaAudio;
    SharedPreferences shared;
    SharedPreferences.Editor editor;
    String cargarInfoTelefono,cargarInfoNombre,cargarInfoPaterno,cargarInfoMaterno,cargarInfoAlergias,cargarInfoMedico,cargarInfoProCivil,cargarInfoSeguridad,cargarInfoSerPubli;
    VideoView videoViewImage;
    MediaPlayer mediaPlayer;
    String mediaPath;
    int bandera = 0;
    int numberRandom;
    String randomCodigoVerifi;
    String codigoVerifi;
    String fecha,hora,direccion1,municipio,estado,rutaMultimedia,descEmergencia,incidente,idIncidente;
    String opcDiscapacidad = "NO";
    private EditText rest;
    private Button btnsendEmergencia;
    private MediaRecorder miGrabacion;
    private String outputFile = null;
    ScrollView scroll;
    Switch switchDisc;
    FormNotificacionEmergencia tm =new FormNotificacionEmergencia();
    int runAudio = 0;

    public FormNotificacion911() {
        // Required empty public constructor
    }

    public static FormNotificacion911 newInstance(String param1, String param2) {
        FormNotificacion911 fragment = new FormNotificacion911();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_form_notificacion911, container, false);

        //************************************** ACCIONES DE LA VISTA **************************************//
        incidente = getActivity().getIntent().getExtras().getString("valor");
        cargar();
        Random();
        home = view.findViewById( R.id.imgHeaderCyHEmergencias);
        scroll= view.findViewById(R.id.scrollMap);

        photo = view.findViewById(R.id.btnTakeFoto);
        video = view.findViewById(R.id.btnTakeVideo);
        audio= view.findViewById(R.id.btnTakeAudio);
        switchDisc = view.findViewById(R.id.switchDiscapacidad);

        imgimagen = view.findViewById(R.id.imgPhoto);
        videoViewImage = view.findViewById(R.id.viewVideo);

        recordAu = view.findViewById(R.id.btnRecordAudio);
        playAu = view.findViewById(R.id.btnPlayAudio);
        detenerAudio = view.findViewById(R.id.btnDetener);
        tiempo = view.findViewById(R.id.timer);

        btnsendEmergencia = view.findViewById(R.id.btnEnviarEmergencia);

        rest = view.findViewById(R.id.txtComentNotifiEmergencia);

        imgimagen.setVisibility(view.GONE);
        videoViewImage.setVisibility(view.GONE);
        tiempo.setVisibility(View.GONE);
        recordAu.setVisibility(view.GONE);
        detenerAudio.setVisibility(view.GONE);
        playAu.setVisibility(view.GONE);

        home.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( getActivity(),MenuEmergencias.class );
                startActivity(i);
            }
        } );

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bandera == 2 || bandera == 3){
                    videoViewImage.clearAnimation();
                    resetChronometro();
                }
                bandera = 1;
                imgimagen.setVisibility(view.VISIBLE);
                videoViewImage.setVisibility(view.GONE);
                playAu.setVisibility(View.GONE);
                recordAu.setVisibility(View.GONE);
                detenerAudio.setVisibility(View.GONE);
                tiempo.setVisibility(View.GONE);
                llamarItem();
                Toast.makeText(getActivity(), "PROCESANDO SU SOLICITUD DE IMAGEN", Toast.LENGTH_SHORT).show();
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bandera == 1 || bandera == 3){
                    imgimagen.clearAnimation();
                    resetChronometro();
                }
                bandera = 2;
                videoViewImage.setVisibility(view.VISIBLE);
                imgimagen.setVisibility(view.GONE);
                playAu.setVisibility(View.GONE);
                recordAu.setVisibility(View.GONE);
                detenerAudio.setVisibility(View.GONE);
                tiempo.setVisibility(View.GONE);
                llamarItemVideo();
                Toast.makeText(getActivity(), "PROCESANDO SU SOLICITUD DE VIDEO", Toast.LENGTH_SHORT).show();

            }
        });
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bandera == 1 || bandera == 2){
                    imgimagen.clearAnimation();
                    videoViewImage.clearAnimation();
                }
                bandera = 3;
                recordAu.setVisibility(view.VISIBLE);
                detenerAudio.setVisibility(View.VISIBLE);
                detenerAudio.setEnabled(false);
                tiempo.setVisibility(View.VISIBLE);
                videoViewImage.setVisibility(view.GONE);
                imgimagen.setVisibility(view.GONE);
                Toast.makeText(getActivity(), "PROCESANDO SU SOLICITUD DE AUDIO", Toast.LENGTH_SHORT).show();
            }
        });

        recordAu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "MICROFONO ACTIVADO", Toast.LENGTH_SHORT).show();
                grabar();
            }
        });

        detenerAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detener();
            }
        });

        playAu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "REPRODUCIENDO AUDIO", Toast.LENGTH_SHORT).show();
                reproducir();
            }
        });

        switchDisc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheked) {
                if(isCheked == true){
                    opcDiscapacidad = "SI";
                }else{
                    opcDiscapacidad = "NO";
                }
            }
        });
        btnsendEmergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rest.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(),"EL CAMPO **DESCRIPCIÓN DE EMERGENCIA** ES OBLIGATORIO",Toast.LENGTH_SHORT).show();
                }else if (bandera == 1) {
                    Toast.makeText(getActivity(), "UN MOMENTO POR FAVOR, SU SOLICITUD ESTA SIENDO PROCESADA", Toast.LENGTH_SHORT).show();
                    insertBdEventoIOS();
                    insertImagen();
                    Intent i = new Intent( getActivity(),MensajeSalida.class );
                    i.putExtra("valorRandom",randomCodigoVerifi);
                    startActivity(i);
                } else if (bandera == 2) {
                    Toast.makeText(getActivity(), "UN MOMENTO POR FAVOR, SU SOLICITUD ESTA SIENDO PROCESADA", Toast.LENGTH_SHORT).show();
                    insertBdEventoIOS();
                    insertVideo();
                    Intent i = new Intent( getActivity(),MensajeSalida.class );
                    i.putExtra("valorRandom",randomCodigoVerifi);
                    startActivity(i);
                } else if (bandera == 3) {
                    recordAu.setVisibility(view.GONE);
                    detenerAudio.setVisibility(view.GONE);
                    playAu.setVisibility(view.GONE);
                    tiempo.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "UN MOMENTO POR FAVOR, SU SOLICITUD ESTA SIENDO PROCESADA", Toast.LENGTH_SHORT).show();
                    insertBdEventoIOS();
                    insertAudio();
                    resetChronometro();
                    Intent i = new Intent( getActivity(),MensajeSalida.class );
                    i.putExtra("valorRandom",randomCodigoVerifi);
                    startActivity(i);
                } else {
                    Toast.makeText(getActivity(), "UN MOMENTO POR FAVOR, SU SOLICITUD ESTA SIENDO PROCESADA", Toast.LENGTH_SHORT).show();
                    insertBdEventoIOS();
                    Intent i = new Intent( getActivity(),MensajeSalida.class );
                    i.putExtra("valorRandom",randomCodigoVerifi);
                    startActivity(i);
                }

            }
        });

        //*************************** SE MUESTRA EL MAPA ****************************************//
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        final String[] permisos = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        map = googleMap;
        tv_add = (TextView) getActivity().findViewById(R.id.tv_miadres);
        map.setMyLocationEnabled(true);

        //activar el boton " ubicación" de mapa y regrese la dirección actual/////
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                actul_posicion=true;
                return false;
            }
        });
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                aux_loc = location;

                if(actul_posicion) {
                    iniciar(location);
                }
            }
        });

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                scroll.requestDisallowInterceptTouchEvent(true);
                Toast.makeText(getActivity(), "Se moverá el marcador", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng neww = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                mi_ubi(neww);
            }
        });
    }

    public String mi_ubi(LatLng au){
        String address = "";

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {

            List<Address> addresses = geocoder.getFromLocation(au.latitude, au.longitude, 1);
            address = addresses.get(0).getAddressLine(0);
            address = "Dirección: \n" + address;
            tv_add.setText(address);
            Address DirCalle = addresses.get(0);
            direccion1 = DirCalle.getAddressLine(0);
            municipio = DirCalle.getLocality();
            estado = DirCalle.getAdminArea();
            if(municipio != null){
                municipio = DirCalle.getLocality();
            }else{
                municipio = "SIN INFORMACIÓN";
            }

        } catch (IOException e){

            e.printStackTrace();
        }

        return address;
    }

    public void iniciar(Location location){
        tv_add.setText("");

        if(marker != null){
            marker.remove();
        }

        lat_origen = location.getLatitude();
        lon_origen = location.getLongitude();
        actul_posicion = false;

        LatLng mi_posicion = new LatLng(lat_origen, lon_origen);

        marker = map.addMarker(new MarkerOptions().position(mi_posicion).title("Mi posición!").draggable(true));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat_origen, lon_origen)).zoom(14).bearing(15).build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        aux = new LatLng(lat_origen, lon_origen);

        mi_ubi(aux);
    }

    //********************************** IMAGEN ***********************************//
    //****************************** ABRE LA CAMARA ***********************************//
    private  void llamarItem(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        }
    }
    //********************************** VIDEO ***********************************//
    //****************************** PARA UTILIZACIÓN DEL VIDEO ***********************************//
    private  void llamarItemVideo(){
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,7);
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent,REQUEST_VIDEO_CAPTURE);
        }
    }
    //********************************** VIDEO ***********************************//
    //****************************** PARA UTILIZACIÓN DEL VIDEO ***********************************//
    private  void llamarItemAudio(){
        Intent galeryIntent = new Intent(Intent.ACTION_PICK,MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galeryIntent,1);
    }
    //********************************** ACCIÓN PARA AUDIO, VIDEO E IMAGEN ***********************************//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(bandera == 1){
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imgimagen.setImageBitmap(imageBitmap);
                imagen();

            }else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_CANCELED){
                imgimagen.setVisibility(View.GONE);
                imgimagen.clearAnimation();
            }
        }else if(bandera == 2){
            super.onActivityResult(requestCode,resultCode,data);
            try
            {
                if (requestCode == 0 && resultCode == Activity.RESULT_OK && null != data)
                {
                    Toast.makeText(getActivity(), "EL CODIGO NO ES DE VIDEO", Toast.LENGTH_LONG).show();

                }else if(requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_CANCELED){
                    videoViewImage.setVisibility(View.GONE);
                    videoViewImage.clearAnimation();
                }
                else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK )
                {
                    Uri videoUri = data.getData();
                    videoViewImage.setVideoURI(videoUri);
                    Log.i("HERE", "PRIMER PARTE DONDE TRAE LA URI");

                    // SELECCIÓN DEL VIDEO ********************

                    Uri selectedVideo = data.getData();
                    String[] filePathColum = {MediaStore.Video.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(selectedVideo,filePathColum,null,null,null);
                    assert cursor != null;
                    cursor.moveToFirst();
                    Log.i("HERE", "GRABANDO");

                    //RUTA FISICA DEL VIDEO *******************

                    int columIndex = cursor.getColumnIndex(filePathColum[0]);
                    mediaPath = cursor.getString(columIndex);
                    //txtResVideo.setText(mediaPath);
                    cursor.close();

                    // VISTA PREVIA DEL VIDEO DESDE UNA RUTA FISICA ************************

                    videoViewImage.setVideoURI(selectedVideo);
                    MediaController mediaController = new MediaController(this.getActivity());
                    videoViewImage.setMediaController(mediaController);
                    mediaController.setAnchorView(videoViewImage);
                    Log.i("HERE", "VISTA PREVIA");

                    //CONVERTIR  VIDEO A BASE64 **************************

                    InputStream inputStream = null;
                    String encodedString = null;

                    try
                    {
                        inputStream = new FileInputStream(mediaPath);

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    byte[] bytes;
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    try
                    {
                        while ((bytesRead = inputStream.read(buffer)) != -1)
                        {
                            output.write(buffer,0,bytesRead);

                        }

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    bytes = output.toByteArray();
                    encodedString = Base64.encodeToString(bytes,Base64.DEFAULT);
                    cadenaVideo = encodedString;
                    //rest.setText(encodedString);

                }else
                {
                    Toast.makeText(getActivity(),"VIDEO EN CONSTRUCCIÓN",Toast.LENGTH_SHORT);
                }

            }catch (Exception e)
            {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }
    //********************************** SE CONVIERTE A BASE64 ***********************************//
    private void imagen(){
        imgimagen.buildDrawingCache();
        Bitmap bitmap = imgimagen.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,70,baos);
        byte[] imgBytes = baos.toByteArray();
        String imgString = android.util.Base64.encodeToString(imgBytes, android.util.Base64.NO_WRAP);
        cadena = imgString;

        imgBytes = android.util.Base64.decode(imgString, android.util.Base64.NO_WRAP);
        Bitmap decoded= BitmapFactory.decodeByteArray(imgBytes,0,imgBytes.length);
        imgimagen.setImageBitmap(decoded);
        System.out.print("IMAGEN" + imgimagen);
    }
    //********************************** INSERTA REGISTRO A LA BD ***********************************//
    //*********************** METODO QUE INSERTA A LA BASE DE DATOS DESPUES DE INSERTAR AL CAD ***********************//
    public void insertBdEventoIOS() {
        idIncidente = incidente;
        //*************** FECHA **********************//
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        fecha = dateFormat.format(date);

        //*************** HORA **********************//
        Date time = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        hora = timeFormat.format(time);

        //************************************* RUTA MULTIMEDIA *************************//
        if (bandera == 1) {
            rutaMultimedia = "http://c5.hidalgo.gob.mx:58/Images/" + cargarInfoTelefono + ".jpg";

        } else if (bandera == 2) {
            rutaMultimedia = "http://c5.hidalgo.gob.mx:58/Video/" + cargarInfoTelefono + ".mp4";

        } else if (bandera == 3) {
            rutaMultimedia = "http://c5.hidalgo.gob.mx:58/Audio/" + cargarInfoTelefono + ".mp4";
        } else {
            rutaMultimedia = "Sin Archivo";
        }
        descEmergencia = rest.getText().toString().toUpperCase();

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("Folio", randomCodigoVerifi)
                .add("Telefono", cargarInfoTelefono)
                .add("NombreUsuario", cargarInfoNombre)
                .add("ApaternoUsuario", cargarInfoPaterno)
                .add("AmaternoUsuario", cargarInfoMaterno)
                .add("Municipio", municipio)
                .add("Estado", estado)
                .add("Latitud", lat_origen.toString())
                .add("Longitud", lon_origen.toString())
                .add("DescripcionEmergencia", descEmergencia)
                .add("DiscapacidadAuditiva", opcDiscapacidad)
                .add("Alergias", cargarInfoAlergias)
                .add("Fecha", fecha)
                .add("Hora", hora)
                .add("Archivo", rutaMultimedia)
                .add("idTipoEmergencia",idIncidente )
                .build();

        Request request = new Request.Builder()
                .url("http://187.174.102.131:58/api/EventosIOS/")
                //.url("http://c5.hidalgo.gob.mx/App911/api/EventosIOS/")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getContext(), "ERROR AL ENVIAR SU REGISTRO", Toast.LENGTH_LONG).show();
                Looper.loop();
                // Toast.makeText(getActivity(), "ERROR AL ENVIAR SU REGISTRO", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().toString();  /********** ME REGRESA LA RESPUESTA DEL WS ****************/

                    FormNotificacion911.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();
                            rest.setText("");
                            imgimagen.setVisibility(View.GONE);
                            videoViewImage.setVisibility(View.GONE);
                            playAu.setVisibility(View.GONE);
                            recordAu.setVisibility(View.GONE);
                            detenerAudio.setVisibility(View.GONE);
                            tiempo.setVisibility(View.GONE);
                            switchDisc.setChecked(false);

                        }
                    });
                }
            }
        });
    }
    //********************************** INSERTA IMAGEN AL SERVIDOR ***********************************//
    public void insertImagen() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Description",cargarInfoTelefono +".jpg" )
                .add("ImageData", cadena)
                .build();

        Request request = new Request.Builder()
                .url("http://187.174.102.131:58/api/Multimedia/")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getContext(), "ERROR AL ENVIAR SU REGISTRO", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().toString();  /********** ME REGRESA LA RESPUESTA DEL WS ****************/

                    FormNotificacion911.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(getActivity(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }
    //********************************** INSERTA VIDEO AL SERVIDOR ***********************************//
    public void insertVideo() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("DescriptionVideo", cargarInfoTelefono+".mp4" )
                .add("VideoData", cadenaVideo)
                .build();

        Request request = new Request.Builder()
                .url("http://187.174.102.131:58/api/MultimediaVideo/")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getContext(), "ERROR AL ENVIAR SU REGISTRO", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().toString();  /********** ME REGRESA LA RESPUESTA DEL WS ****************/

                    FormNotificacion911.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(getActivity(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }
    //********************************** INSERTA AUDIO AL SERVIDOR ***********************************//
    public void insertAudio() {

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("DescriptionAudio", cargarInfoTelefono+".mp4" )
                .add("AudioData", cadenaAudio)
                .build();

        Request request = new Request.Builder()
                .url("http://187.174.102.131:58/api/MultimediaAudio/")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getContext(), "ERROR AL ENVIAR SU REGISTRO", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().toString();  /********** ME REGRESA LA RESPUESTA DEL WS ****************/

                    FormNotificacion911.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(getActivity(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }
    //********************* INICIA EL CONTADOR **********************************************************************//
    private void resetChronometro() {
        tiempo.setBase(SystemClock.elapsedRealtime());
        detenerse = 0;
    }
    private void stopChronometro() {
        if(correr){
            tiempo.stop();
            detenerse = SystemClock.elapsedRealtime() -tiempo.getBase();
            correr = false;

        }
    }
    private void startChronometro() {
        runAudio = 1;
        if(!correr){
            tiempo.setBase(SystemClock.elapsedRealtime() -detenerse);
            tiempo.start();
            correr = true;
        }
    }
    ////*********************************************GRABAR AUDIO*********************************************//////
    public void grabar() {
        if(bandera == 4){
            resetChronometro();
            detenerAudio.setEnabled(false);
        }else if(bandera == 5){
            resetChronometro();
            detenerAudio.setEnabled(false);
        }
        startChronometro();
        detenerAudio.setEnabled(true);
        recordAu.setEnabled(false);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Grabacion.3gp";
        miGrabacion = new MediaRecorder();
        miGrabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
        miGrabacion.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        miGrabacion.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        miGrabacion.setOutputFile(outputFile);
        try {
            miGrabacion.prepare();
            miGrabacion.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //Toast.makeText(getActivity().getApplicationContext(), "La grabación comenzó", Toast.LENGTH_LONG).show();
        //detenerAudio.setVisibility(View.VISIBLE);
        //recordAu.setVisibility(View.GONE);
        //playAu.setVisibility(View.GONE);
    }
    public void detener() {
        bandera = 4;
        runAudio = 0;
        stopChronometro();
        recordAu.setEnabled(true);
        if (miGrabacion != null) {
            miGrabacion.stop();
            miGrabacion.release();
            miGrabacion = null;
            //Toast.makeText(getActivity().getApplicationContext(), "El audio fue grabado con éxito", Toast.LENGTH_LONG).show();
        }
        playAu.setVisibility(View.VISIBLE);
        playAu.setEnabled(true);
    }
    public void reproducir() {
        if(correr == true){
            Toast.makeText(getActivity(), "LO SENTIMOS, DEBE DETENER LA GRABACIÓN ", Toast.LENGTH_SHORT).show();
        }
        bandera = 5;
        MediaPlayer m = new MediaPlayer();
        try {
            m.setDataSource(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            m.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        m.start();
        //Toast.makeText(getActivity().getApplicationContext(), "reproducción de audio", Toast.LENGTH_LONG).show();
        audioBase64();
    }
    public void audioBase64(){
        //CONVERTIR AUDIO A BASE64
        InputStream inputStream = null;
        String audioEncodeString = null;
        try
        {
            inputStream = new FileInputStream(outputFile);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        byte[] bytes;
        byte[] bufferAudio = new byte[8192];
        int bytesAudioRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try
        {
            while ((bytesAudioRead = inputStream.read(bufferAudio)) != -1)
            {
                output.write(bufferAudio,0,bytesAudioRead);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        bytes = output.toByteArray();
        audioEncodeString = Base64.encodeToString(bytes,Base64.DEFAULT);
        cadenaAudio = audioEncodeString;
    }
    //********************* GENERA EL NÚMERO ALEATORIO PARA EL FOLIO *****************************//
    public void Random(){
        Random random = new Random();
        numberRandom = random.nextInt(9000)*99;
        codigoVerifi = String.valueOf(numberRandom);
        randomCodigoVerifi = "C5i2020"+codigoVerifi;
    }
    //********************* OBTIENE LAS PREFERENCIAS DEL SISTEMA (VARIABLES DE SESSION) *****************************//
    private void cargar(){
        shared = this.getActivity().getSharedPreferences("main",Context.MODE_PRIVATE);
        cargarInfoTelefono = shared.getString("telefono","");
        cargarInfoNombre = shared.getString("nombre","");
        cargarInfoPaterno = shared.getString("paterno","");
        cargarInfoMaterno = shared.getString("materno","");
        cargarInfoAlergias = shared.getString("alergias","");
        //Toast.makeText(getActivity(),cargarInfoTelefono,Toast.LENGTH_LONG).show();
    }

}