package gemvietnam.com.trafficjam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gemvietnam.com.trafficjam.library.AbstractRouting;
import gemvietnam.com.trafficjam.library.GridViewAdapter;
import gemvietnam.com.trafficjam.library.MySupportMapFragment;
import gemvietnam.com.trafficjam.library.Route;
import gemvietnam.com.trafficjam.library.RouteException;
import gemvietnam.com.trafficjam.library.Routing;
import gemvietnam.com.trafficjam.library.RoutingListener;

import static gemvietnam.com.trafficjam.R.id.map;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,RoutingListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final String TAG = "TAG: ";
    private static final int LOCATION_CODE_AUTOCOMPLETE = 10;
    private static final int ORIGIN_CODE_AUTOCOMPLETE = 11;
    private static final int DESTINATION_CODE_AUTOCOMPLETE = 12;
    private static int PLACE_PICKER_REQUEST = 2;
    private GoogleMap mMap;
    private ProgressDialog mprogress;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark,R.color.primary,R.color.primary_light,R.color.accent,R.color.primary_dark_material_light};
    public static boolean mMapIsTouched = false;

    @BindView(R.id.activity_main_direction_cancel_img)
    ImageView mDirectionCancelImg;
    @BindView(R.id.activity_main_gridview)
    GridView mGridView;
    @BindView(R.id.activity_main_cancel_img)
    ImageView mCancelImg;
    @BindView(R.id.activity_main_location_search_tv)
    TextView mLocationSearchTv;
    @BindView(R.id.activity_main_direction_img)
    ImageView mDirectionImg;
    @BindView(R.id.activity_main_origin_tv)
    TextView mOriginTv;
    @BindView(R.id.activity_main_origin_cancel_img)
    ImageView mOriginCancelImg;
    @BindView(R.id.activity_main_destination_tv)
    TextView mDestinationTv;
    @BindView(R.id.activity_main_destination_cancel_img)
    ImageView mDestinationCancelImg;
    @BindView(R.id.activity_main_send_img)
    ImageView mSendImg;
    @BindView(R.id.activity_main_location_search_cv)
    CardView mLocationSearchCv;
    @BindView(R.id.activity_main_direction_search_cv)
    CardView mDirectionSearchCv;
    LatLng mOrigin;
    LatLng mDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        polylines = new ArrayList<>();

        /**
         * show map
         */
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        MySupportMapFragment fr = (MySupportMapFragment) mapFragment;
        fr.getMapAsync(this);
        mDirectionImg.setVisibility(View.VISIBLE);
        mLocationSearchCv.setVisibility(View.VISIBLE);
        mDirectionSearchCv.setVisibility(View.GONE);
        mCancelImg.setVisibility(View.GONE);
        mDirectionCancelImg.setVisibility(View.VISIBLE);
        mOriginTv.setVisibility(View.VISIBLE);
        mOriginCancelImg.setVisibility(View.VISIBLE);
        mDestinationTv.setVisibility(View.VISIBLE);
        mDestinationCancelImg.setVisibility(View.GONE);
        mSendImg.setVisibility(View.VISIBLE);
        mGridView.setAdapter(new GridViewAdapter(this));
        mGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mapFragment.getView().dispatchTouchEvent(event);
            }
        });

        /**
         * show progress when loading map
         */
        mprogress = new ProgressDialog(this);
        mprogress.setMessage("Loading...");
        mprogress.setCanceledOnTouchOutside(false);
        mprogress.show();

        /**
         * pick place
         */
        /*
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        */

        /**
         * place auto complete
         */

        mLocationSearchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAutoCompleteActivity();
            }
        });

        /**
         * on Click direction button
         */
        mDirectionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDirectionImg.setVisibility(View.GONE);
                mLocationSearchCv.setVisibility(View.GONE);
                mDirectionSearchCv.setVisibility(View.VISIBLE);
                checkDirectionSearchVisibility();
            }
        });

        /**
         * click cancel button
         */
        mCancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationSearchTv.setText("Search location here...");
                mCancelImg.setVisibility(View.GONE);
            }
        });

        /**
         * show direction search
         */
        mOrigin = getCurrentLocation();
        mOriginCancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOriginTv.setText("Choose your origin");
                mOriginCancelImg.setVisibility(View.GONE);
            }
        });
        mOriginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                originAutoCompleteFragment();
            }
        });
        mDestinationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationAutoCompleteFragment();
            }
        });
        mDestinationCancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDestinationTv.setText("Choose your destination");
                mDestinationCancelImg.setVisibility(View.GONE);
            }
        });
        mSendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gemvietnam.com.trafficjam.library.Util.Operations.isOnline(MainActivity.this))
                {
                    route();
                }
                else
                {
                    Toast.makeText(MainActivity.this,"No internet connectivity",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mDirectionCancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDirectionSearchCv.setVisibility(View.GONE);
                mLocationSearchCv.setVisibility(View.VISIBLE);
                mDirectionImg.setVisibility(View.VISIBLE);
                checkLocationSearchVisibility();
            }
        });
    }

    private void route() {
        if(mOrigin != null && mDestination != null){
            mprogress = ProgressDialog.show(this, "Please wait.",
                    "Fetching route information.", true);
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(mOrigin, mDestination)
                    .build();
            routing.execute();
        }
    }

    private void checkDirectionSearchVisibility() {
        if(mOriginTv.getText().toString().equals("Choose your origin")){
            mOriginCancelImg.setVisibility(View.GONE);
        }else {
            mOriginCancelImg.setVisibility(View.VISIBLE);
        }
        if(mDestinationTv.getText().toString().equals("Choose your destination")){
            mDestinationCancelImg.setVisibility(View.GONE);
        }else {
            mDestinationCancelImg.setVisibility(View.VISIBLE);
        }
    }

    private void checkLocationSearchVisibility() {
        if(mLocationSearchTv.getText().toString().equals("Search location here...")){
            mCancelImg.setVisibility(View.GONE);
        } else {
            mCancelImg.setVisibility(View.VISIBLE);
        }
    }

    private void destinationAutoCompleteFragment() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(MainActivity.this);
            startActivityForResult(intent, DESTINATION_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void originAutoCompleteFragment() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(MainActivity.this);
            startActivityForResult(intent, ORIGIN_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void openAutoCompleteActivity() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(MainActivity.this);
            startActivityForResult(intent, LOCATION_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                LatLng latln = place.getLatLng();
                mLocationSearchTv.setText(place.getName());
                mCancelImg.setVisibility(View.VISIBLE);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latln, 16));
                Marker marker = mMap.addMarker(new MarkerOptions().position(latln));

                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == ORIGIN_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                mOrigin = place.getLatLng();
                mOriginTv.setText(place.getName());
                mOriginCancelImg.setVisibility(View.VISIBLE);
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == DESTINATION_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                mDestination = place.getLatLng();
                mDestinationTv.setText(place.getName());
                mDestinationCancelImg.setVisibility(View.VISIBLE);
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mprogress.dismiss();

        /**
         * check permission
         */
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {


                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)) {


                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
        }

        /**
         * location chage listener
         */
        mMap.setMyLocationEnabled(true);
        mMap.setPadding(0, 300, 0, 0);
        getLocation();
        GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                if (mMap != null) {
                    mMap.clear();
                    Marker mMarker = mMap.addMarker(new MarkerOptions().position(loc));
                }
            }
        };
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);

    }

    public void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
        Location lastLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (lastLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 16));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))      // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    /**
     * get current location
     */

    public LatLng getCurrentLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
        Location lastLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        return new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        // The Routing request failed
        mprogress.dismiss();
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }


    @Override
    public void onRoutingSuccess(List<Route> route, int shortestRouteIndex)
    {
        mprogress.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(mOrigin);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        mMap.moveCamera(center);

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(mOrigin);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
        mMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(mDestination);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
        mMap.addMarker(options);

    }

    @Override
    public void onRoutingCancelled() {
        Log.i(TAG, "Routing was cancelled.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(TAG,connectionResult.toString());
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
}
