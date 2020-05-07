package mx.gob.hidalgo.hidalgoestatal911;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class FormAuthFirebase extends AppCompatActivity {

    //VARIABLE DEL CODIGO DE ESTADO
    public static final String TAG= "PhoneAuth";

    private EditText phoneText;
    private EditText codeText;
    private ImageButton verifyButton;
    private ImageButton sendButton;
    private Button resendButton;
    private Button signoutButton;
    private TextView statusText;
    private Button aviso;
    String number;
    Boolean aux;

    SharedPreferences share;
    SharedPreferences.Editor editor;

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_form_auth_firebase );
        solicitarPermisosCamera();

        aviso = (Button)findViewById(R.id.btnAvisoPrivacidad);
        phoneText = (EditText) findViewById(R.id.phoneText);
        codeText = (EditText) findViewById(R.id.codeText);
        verifyButton = (ImageButton) findViewById(R.id.verifyButton);
        sendButton = (ImageButton) findViewById(R.id.sendButton);
        resendButton = (Button) findViewById(R.id.resendButton);
        signoutButton = (Button) findViewById(R.id.signoutButton);
        statusText = (TextView) findViewById(R.id.statusText);
        share = getSharedPreferences("registro",MODE_PRIVATE);
        editor=share.edit();

        verifyButton.setEnabled(false);
        resendButton.setEnabled(false);
        signoutButton.setEnabled(false);
        statusText.setText("Signed Out");

        fbAuth = FirebaseAuth.getInstance();

        ImageButton downloadButton = (ImageButton)findViewById(R.id.sendButton);

        aviso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse("http://gobierno.hidalgo.gob.mx/AvisoPrivacidad");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });
    }

    public void sendCode(View view){
        int valor = 45;

        number = "+52"+phoneText.getText().toString();
        setUpVerificatonCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(number,valor,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks);

        Toast.makeText(getApplicationContext(),"ENVIANDO CODIGO DE VERIFICACIÓN, UN MOMENTO POR FAVOR",Toast.LENGTH_LONG).show();

    }

    private void setUpVerificatonCallbacks() {
        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signoutButton.setEnabled(true);
                statusText.setText("Registrado");
                resendButton.setEnabled(false);
                verifyButton.setEnabled(false);
                codeText.setText("");
                signInWithPhoneAuthCredential(credential);
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {

                /*if (e instanceof FirebaseAuthInvalidCredentialsException){
                    Log.d(TAG,"Número Invalido: " + e.getLocalizedMessage());
                } else  if (e instanceof FirebaseTooManyRequestsException){
                    Log.d(TAG,"Cuota de SMS excedida");
                }*/
            }
            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                phoneVerificationId = verificationId;
                resendToken = token;

                verifyButton.setEnabled(true);
                sendButton.setEnabled(true);
                resendButton.setEnabled(true);
            }
        };
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            signoutButton.setEnabled(true);
                            codeText.setText("");
                            statusText.setText("Signed In");
                            resendButton.setEnabled(false);
                            verifyButton.setEnabled(false);
                            FirebaseUser user = task.getResult().getUser();
                            String phoneNumber = user.getPhoneNumber();
                            editor.putString("phone",phoneNumber);
                            editor.putString("telefono",phoneText.getText().toString());
                            editor.commit();
                            guardar();
                            Intent intent = new Intent(FormAuthFirebase.this, FormFotoUser.class);
                            startActivity(intent);
                            finish();
                        } else {
                            task.getException();
                        }
                    }
                });
    }

    public void resendCode(View view){
        number = "+52";
        setUpVerificatonCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks,
                resendToken);
    }

    public void verifyCode(View view){
        String code = codeText.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // if (id == R.id.action_settings) {
        // return true;
        //}

        //return super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public void onBackPressed() {

    }
    private void guardar(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putString("telefono",phoneText.getText().toString());
        editor.commit();
        //Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }

    public void signOut(View view) {
        fbAuth.signOut();
        statusText.setText("Signed Out");
        signoutButton.setEnabled(false);
        sendButton.setEnabled(true);
    }

    //***************************** SE OPTIENEN TODOS LOS PERMISOS AL INICIAR LA APLICACIÓN *********************************//
    public void solicitarPermisosCamera(){
        if (ContextCompat.checkSelfPermission(FormAuthFirebase.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(FormAuthFirebase.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(FormAuthFirebase.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(FormAuthFirebase.this, Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(FormAuthFirebase.this, Manifest.permission.RECORD_AUDIO) !=PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(FormAuthFirebase.this,Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(FormAuthFirebase.this,Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                &&ActivityCompat.checkSelfPermission(FormAuthFirebase.this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FormAuthFirebase.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.VIBRATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.CALL_PHONE,}, 1000);

        }
    }

}

