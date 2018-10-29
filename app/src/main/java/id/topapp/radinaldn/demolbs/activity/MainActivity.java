package id.topapp.radinaldn.demolbs.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import id.topapp.radinaldn.demolbs.R;
import id.topapp.radinaldn.demolbs.response.ResponsePesanan;
import id.topapp.radinaldn.demolbs.rest.ApiClient;
import id.topapp.radinaldn.demolbs.rest.ApiInterface;
import id.topapp.radinaldn.demolbs.util.AbsRunTimePermission;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AbsRunTimePermission {

    TextInputEditText etmakanan, etporsi, etket, etlat, etlng;
    Button btkirim, btbatal;
    ApiInterface apiService;
    String makanan, porsi, ket, lat, lng;

    LocationManager lm;
    LocationListener locationListener;
    private static final int REQUEST_PERMISSION = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etmakanan = findViewById(R.id.etmakanan);
        etporsi = findViewById(R.id.etporsi);
        etket = findViewById(R.id.etket);
        etlat = findViewById(R.id.etlat);
        etlng = findViewById(R.id.etlng);
        btkirim = findViewById(R.id.btkirim);
        btbatal = findViewById(R.id.btbatal);


        apiService = ApiClient.getClient().create(ApiInterface.class);

        // do runtime permission
//        requestAppPermissions(new String[]{
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION},
//                R.string.msg
//                , REQUEST_PERMISSION);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        btkirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirimPesanan();
            }
        });

    }

    private void kirimPesanan() {
        makanan = etmakanan.getText().toString();
        porsi = etporsi.getText().toString();
        ket = etket.getText().toString();
        lat = etlat.getText().toString();
        lng = etlng.getText().toString();

        apiService.doPemesanan(makanan, porsi, ket, lat, lng).enqueue(new Callback<ResponsePesanan>() {
            @Override
            public void onResponse(Call<ResponsePesanan> call, Response<ResponsePesanan> response) {
                if (response.isSuccessful()) {
                    System.out.println("Response pesanan : " + response.body()toString());
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        Toast.makeText(getApplicationContext(), response.body().getMessage().toString(), Toast.LENGTH_LONG).show();
                        restartActivity();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponsePesanan> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void restartActivity() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onPermissionGranted(int requestcode) {
        Toast.makeText(getApplicationContext(),
                "Permission granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            requestAppPermissions(new String[]{
//                            Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_COARSE_LOCATION},
//                    R.string.msg
//                    , REQUEST_PERMISSION);
        }

        lm.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0,
                0,
                locationListener
        );
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null){
                lat = String.valueOf(location.getLatitude());
                etlat.setText(lat);

                lng = String.valueOf(location.getLongitude());
                etlng.setText(lng);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            String statusString = "";
            switch (status){
                case LocationProvider.AVAILABLE:
                    statusString = "available";
                    case LocationProvider.OUT_OF_SERVICE:
                        statusString = "out of service";
                        case LocationProvider.TEMPORARILY_UNAVAILABLE:
                            statusString = "temporarily unavailable";
            }

            Toast.makeText(getApplicationContext(),
                    provider+": "+statusString, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(),
                    provider+" enabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(),
                    provider+" disabled", Toast.LENGTH_SHORT).show();
        }
    }
}
