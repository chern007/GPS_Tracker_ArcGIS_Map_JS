package com.geoape.backgroundlocationexample;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_start_tracking)
    Button btnStartTracking;

    @BindView(R.id.btn_stop_tracking)
    Button btnStopTracking;

    @BindView(R.id.txt_status)
    TextView txtStatus;

    @BindView(R.id.editText)
    EditText mText;

    public static List<String> registros;
    public static List<Double[]> coordenadasList;

    public static Context mainContext;


//    EditText mText = (EditText) findViewById(R.id.editText);

    Button btMap;

    public BackgroundService gpsService;
    public boolean mTracking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mainContext = getApplicationContext();

        //inicializamos el boton de map;
        btMap = (Button) findViewById(R.id.btMap);

        registros = new ArrayList<>();
        coordenadasList = new ArrayList<>();


        final Intent intent = new Intent(this.getApplication(), BackgroundService.class);
        this.getApplication().startService(intent);
//        this.getApplication().startForegroundService(intent);
        this.getApplication().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    //pasamos a la actividad de mapas
    public void getMap(View view) {
        Intent intent = new Intent(this, Map.class);
        startActivity(intent);
    }


    @OnClick(R.id.btn_start_tracking)
    public void startLocationButtonClick() {

        registros.clear();
        //limpiamos la caja de texto
        mText.getText().clear();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        gpsService.startTracking();
                        mTracking = true;
                        toggleButtons();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @OnClick(R.id.btn_stop_tracking)
    public void stopLocationButtonClick() {
        mTracking = false;
        gpsService.stopTracking();
        toggleButtons();

        String cadenaCompleta = "";
        for (String reg : registros) {

            cadenaCompleta += reg + "\n";

        }

        //metemos los textos
        mText.setText(cadenaCompleta, TextView.BufferType.EDITABLE);


        //MANDAMOS EL POST

        String arrayCoord = "[";

        for (Double[] coord : coordenadasList) {

            arrayCoord += Arrays.toString(coord) + ", ";

        }

        arrayCoord += "@";
        arrayCoord = arrayCoord.replaceAll(", @", "");
        arrayCoord += "]";


        HashMap<String, Object> params = new LinkedHashMap<>();
        params.put("store_data", 1); // All parameters, also easy
        params.put("event", "Android");
        params.put("uuid", 999);
        params.put("path", arrayCoord);


        StringBuilder postData = new StringBuilder();
        // POST as urlencoded is basically key-value pairs, as with GET
        // This creates key=value&key=value&... pairs
        for (HashMap.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
//                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append(param.getKey());
                postData.append('=');
                postData.append(String.valueOf(param.getValue()));
        }

        CallAPI post = new CallAPI();
        post.execute(new String[]{"http://dam.dotgiscorp.com/tests/test01.php", postData.toString()});

    }

    private void toggleButtons() {
        btnStartTracking.setEnabled(!mTracking);
        btnStopTracking.setEnabled(mTracking);
        txtStatus.setText((mTracking) ? "TRACKING" : "GPS Ready");
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();
            if (name.endsWith("BackgroundService")) {
                gpsService = ((BackgroundService.LocationServiceBinder) service).getService();
                btnStartTracking.setEnabled(true);
                txtStatus.setText("GPS Ready");
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("BackgroundService")) {
                gpsService = null;
            }
        }
    };
}
