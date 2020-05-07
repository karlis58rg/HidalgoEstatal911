package mx.gob.hidalgo.hidalgoestatal911;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FormFotoUser extends AppCompatActivity {
    Button pickFotoAvatar;
    ImageView avatarimg;

    CircleImageView avatar2;


    static final int REQUEST_IMAGE_CAPTURE = 1;

    EditText txtTelefonoUs, txtNombre, txtPaterno, txtMaterno, txtEmail, txtDescAlergias, txtDescMedicamentos, txtNombreFamiliar, txtTelFamiliar;
    Button btnEnviar,aviso;
    String telefono, nombres, paterno, materno, mail, nombreFamiliar, telefonoFamiliar, cargarInfoTelefono, cadena,fecha,hora;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    RadioGroup radioSexo, radioAlergias, radioMedicamento;
    String varSexo = "SIN ESPECIFICAR";
    String varAlergias = "SIN ESPECIFICAR";
    String varMedicamentos = "SIN ESPECIFICAR";
    String descAlergias = "NINGUNA";
    String descMedicamentos = "NINGUNA";
    String email;
    String dispositivo = "ANDROID";
    int bandera = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_foto_user);

        txtTelefonoUs = (EditText) findViewById(R.id.txtTelefonoUser);
        txtNombre = (EditText) findViewById(R.id.txtNombres);
        txtPaterno = (EditText) findViewById(R.id.txtApaterno);
        txtMaterno = (EditText) findViewById(R.id.txtAmaterno);
        radioSexo = (RadioGroup) findViewById(R.id.radioSexo);
        txtEmail = (EditText) findViewById(R.id.txtMail);
        radioAlergias = (RadioGroup) findViewById(R.id.radioAlergias);
        txtDescAlergias = (EditText) findViewById(R.id.txtComentariosAlergias);
        radioMedicamento = (RadioGroup) findViewById(R.id.radioMedicamento);
        txtDescMedicamentos = (EditText) findViewById(R.id.txtComentariosMedicamento);
        txtNombreFamiliar = (EditText) findViewById(R.id.txtNombresFamiliar);
        txtTelFamiliar = (EditText) findViewById(R.id.txtTelefonoFamiliar);
        btnEnviar = (Button) findViewById(R.id.btnEnviarRegistro);
        aviso = (Button)findViewById(R.id.btnAvisoPrivacidad);
        solicitarPermisosCamera();
        //cargar();


        txtDescAlergias.setVisibility(View.GONE);
        txtDescMedicamentos.setVisibility(View.GONE);

        pickFotoAvatar = (Button) findViewById(R.id.pickFoto);
        avatarimg = (ImageView) findViewById(R.id.avatar);
        avatar2 = findViewById(R.id.profile_image);


        pickFotoAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bandera = 1;
                llamarItemAvatar();
            }
        });

        radioSexo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioMasculino) {
                    varSexo = "MASCULINO";
                } else if (checkedId == R.id.radioFemenino) {
                    varSexo = "FEMENINO";
                }
            }
        });

        radioAlergias.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioAlergiasSi) {
                    varAlergias = "SI";
                    txtDescAlergias.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.radioAlergiasNo) {
                    varAlergias = "NO";
                    txtDescAlergias.setVisibility(View.GONE);
                }

            }
        });

        radioMedicamento.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioMedicamentoSi) {
                    varMedicamentos = "SI";
                    txtDescMedicamentos.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.radioMedicamentoNo) {
                    varMedicamentos = "NO";
                    txtDescMedicamentos.setVisibility(View.GONE);
                }
            }
        });


        btnEnviar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                email = txtEmail.getText().toString();
                final String compruebaemail = txtEmail.getEditableText().toString().trim();
                if (!compruebaemail.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
                    Toast.makeText(FormFotoUser.this, "POR FAVOR, INTRODUZCA BIEN SU E-MAIL", Toast.LENGTH_LONG).show();
                } else if (bandera == 0) {
                    Toast.makeText(getApplicationContext(), " SU FOTO ES NECESARIA PARA VERIFICAR SU IDENTIDAD.", Toast.LENGTH_LONG).show();
                } else if (txtTelefonoUs.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), " SU NÚMERO TELEFÓNICO ES NECESARIO PARA VERIFICAR SU IDENTIDAD.", Toast.LENGTH_LONG).show();
                } else if (txtNombre.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), " SU NOMBRE ES NECESARIO PARA VERIFICAR SU IDENTIDAD.", Toast.LENGTH_LONG).show();
                } else if (txtPaterno.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), " SU APELLIDO PATERNO ES NECESARIO PARA VERIFICAR SU IDENTIDAD.", Toast.LENGTH_LONG).show();
                } else if (txtMaterno.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), " SU APELLIDO MATERNO ES NECESARIO PARA VERIFICAR SU IDENTIDAD.", Toast.LENGTH_LONG).show();
                }else if(txtNombre.getText().length() < 3 ){
                    Toast.makeText(getApplicationContext(), " SU NOMBRE NO PUEDE SER MENOR A TRES LETRAS.", Toast.LENGTH_LONG).show();
                }else if(txtPaterno.getText().length() < 3 ){
                    Toast.makeText(getApplicationContext(), " SU APELLIDO PATERNO NO PUEDE SER MENOR A TRES LETRAS.", Toast.LENGTH_LONG).show();
                }else if(txtMaterno.getText().length() < 3 ){
                    Toast.makeText(getApplicationContext(), " SU APELLIDO MATERNO NO PUEDE SER MENOR A TRES LETRAS.", Toast.LENGTH_LONG).show();
                }else if(txtTelefonoUs.getText().length() < 10 ){
                    Toast.makeText(getApplicationContext(), " SU NÚMERO TELEFÓNICO NO PUEDE SER MENOR A 10 DIGITOS.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "UN MOMENTO POR FAVOR, ESTAMOS PROCESANDO SU SOLICITUD", Toast.LENGTH_SHORT).show();
                    guardar();
                    insertRegistroUsuario();
                    insertImagen();
                }
            }
        });

        aviso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse("http://gobierno.hidalgo.gob.mx/AvisoPrivacidad");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });


    }

  /*  private boolean validateEmailAddress(String emailAddress){
        String  expression="^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = emailAddress;
        Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }*/

    private void llamarItemAvatar() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //avatarimg.setImageBitmap(imageBitmap);
            avatar2.setImageBitmap(imageBitmap);
            imagen2();
            pickFotoAvatar.setVisibility(View.GONE);


        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Tu acción ha sido cancelada", Toast.LENGTH_SHORT).show();
            avatar2.clearAnimation();
            //avatarimg.clearAnimation();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //***************** INSERTA A LA BD MEDIANTE EL WS **************************//
    private void insertRegistroUsuario() {
        telefono = txtTelefonoUs.getText().toString();
        nombres = txtNombre.getText().toString().toUpperCase();
        paterno = txtPaterno.getText().toString().toUpperCase();
        materno = txtMaterno.getText().toString().toUpperCase();
        mail = txtEmail.getText().toString();
        descAlergias = txtDescAlergias.getText().toString().toUpperCase();
        descMedicamentos = txtDescMedicamentos.getText().toString().toUpperCase();
        nombreFamiliar = txtNombreFamiliar.getText().toString().toUpperCase();
        telefonoFamiliar = txtTelFamiliar.getText().toString();

        //*************** FECHA **********************//
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        fecha = dateFormat.format(date);

        //*************** HORA **********************//
        Date time = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        hora = timeFormat.format(time);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Telefono", telefono)
                .add("Nombre", nombres)
                .add("Apaterno", paterno)
                .add("Amaterno", materno)
                .add("Sexo", varSexo)
                .add("Email", mail)
                .add("Alergias", varAlergias)
                .add("DescAlergias", descAlergias)
                .add("Medicamentos", varMedicamentos)
                .add("DescMedicamentos", descMedicamentos)
                .add("NombreFamiliar", nombreFamiliar)
                .add("TelefonoFamiliar", telefonoFamiliar)
                .add("Fecha", fecha)
                .add("Hora", hora)
                .add("Dispositivo", dispositivo)
                .build();

        Request request = new Request.Builder()
                .url("http://187.174.102.131:58/api/RegistroIOS/")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getApplicationContext(), "ERROR AL ENVIAR SU REGISTRO", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().toString();

                    FormFotoUser.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();
                            txtTelefonoUs.setText("");
                            txtNombre.setText("");
                            txtPaterno.setText("");
                            txtMaterno.setText("");
                            txtTelFamiliar.setText("");
                            radioSexo.clearCheck();
                            radioAlergias.clearCheck();
                            radioMedicamento.clearCheck();
                            txtEmail.setText("");
                            txtDescAlergias.setText("");
                            txtDescMedicamentos.setText("");
                            txtNombreFamiliar.setText("");
                            Intent i = new Intent(FormFotoUser.this, MenuEmergencias.class);
                            startActivity(i);
                            finish();
                        }
                    });
                }

            }
        });
    }


    //********************************** SE CONVIERTE A BASE64 ***********************************//
/*
    private void imagen()
    {
        avatarimg.buildDrawingCache();
        Bitmap bitmap = avatarimg.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,70,baos);
        byte[] imgBytes = baos.toByteArray();
        String imgString = android.util.Base64.encodeToString(imgBytes, android.util.Base64.NO_WRAP);
        cadena = imgString;

        imgBytes = android.util.Base64.decode(imgString, android.util.Base64.NO_WRAP);
        Bitmap decoded= BitmapFactory.decodeByteArray(imgBytes,0,imgBytes.length);
        avatarimg.setImageBitmap(decoded);
        System.out.print("IMAGEN" + avatarimg);
    }*/

    private void imagen2() {
        avatar2.buildDrawingCache();
        Bitmap bitmap = avatar2.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imgBytes = baos.toByteArray();
        String imgString = android.util.Base64.encodeToString(imgBytes, android.util.Base64.NO_WRAP);
        cadena = imgString;

        imgBytes = android.util.Base64.decode(imgString, android.util.Base64.NO_WRAP);
        Bitmap decoded = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        avatar2.setImageBitmap(decoded);
        System.out.print("IMAGEN" + avatar2);
    }


    //********************************** INSERTA IMAGEN AL SERVIDOR ***********************************//
    public void insertImagen() {

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Description", telefono + ".jpg")
                .add("ImageData", cadena)
                .build();

        Request request = new Request.Builder()
                .url("http://187.174.102.131:58/api/MultimediaUserFoto/")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getApplicationContext(), "ERROR AL ENVIAR SU REGISTRO", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().toString();  /********** ME REGRESA LA RESPUESTA DEL WS ****************/

                    FormFotoUser.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }

    private void cargar() {
        share = getSharedPreferences("main", MODE_PRIVATE);
        cargarInfoTelefono = share.getString("telefono", "");
        //Toast.makeText(getApplicationContext(),cargarInfoTelefono,Toast.LENGTH_LONG).show();
    }

    private void guardar() {
        share = getSharedPreferences("main", MODE_PRIVATE);
        editor = share.edit();
        editor.putString("telefono", txtTelefonoUs.getText().toString());
        editor.putString("nombre", txtNombre.getText().toString());
        editor.putString("paterno", txtPaterno.getText().toString());
        editor.putString("materno", txtMaterno.getText().toString());
        editor.putString("alergias", txtDescAlergias.getText().toString());
        editor.putString("medicamentos", txtDescAlergias.getText().toString());
        editor.putString("sexo", varSexo);
        editor.commit();
        //Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }

    //***************************** SE OPTIENEN TODOS LOS PERMISOS AL INICIAR LA APLICACIÓN *********************************//
    public void solicitarPermisosCamera() {
        if (ContextCompat.checkSelfPermission(FormFotoUser.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FormFotoUser.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(FormFotoUser.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FormFotoUser.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FormFotoUser.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FormFotoUser.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO,Manifest.permission.CALL_PHONE}, 1000);

        }
    }
}
