package com.example.min.googlemaptest;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class MainActivity extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{

    private GoogleMap map;
    private boolean mPermissionDenied = false;



    private Location lastKnownLocation = null ;



    ArrayList<LatLng> MarkerPoints; // 마커 저장
    //로마
//    LatLng M2 = new LatLng(41.86, 12.97); // 경도, 위도
//    LatLng M1 = new LatLng(41.85, 12.46);
    //서울
    LatLng M1 = new LatLng(37.56, 126.97);
    LatLng M2 = new LatLng(37.628, 126.825); // 울집
//    private static final LatLngBounds BOUNDS_VIEW = new LatLngBounds(
//            new LatLng(37.56, 126.97), new LatLng(37.628, 126.825));
    private static final LatLngBounds BOUNDS_VIEW = new LatLngBounds(
            new LatLng(37.56, 126.98), new LatLng(37.57, 127.02));

    TextView tv;
    String s_DD=" ";
    List<String> l_s_DD;
    private int DD_cnt=0;
    private  int gps_cnt=0;

    private static final int PLACE_PICKER_REQUEST =1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        tv = (TextView) findViewById(R.id.DDtext);
        l_s_DD = new ArrayList<>();
        l_s_DD.clear();
        MarkerPoints = new ArrayList<>();
        MarkerPoints.clear();
//        String url = getUrl(M1,M2);
//        fetchUrl fUrl = new fetchUrl();
//        fUrl.execute(url);

//

    }
    @Override
    public void onMapReady(final GoogleMap gMap) {
        map = gMap;
        map.setOnMyLocationButtonClickListener(this);
        enableMyLocation();



        final Marker a = map.addMarker(new MarkerOptions()
                .position(M1)
                .draggable(true)
                .snippet("한국의 수도")
                .title("서울"));
        a.setDraggable(true);
        //  위에 말풍선 표시한거 보여주도록 호출
        a.showInfoWindow();

        // 맵 클릭 리스너
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                pickMark(map,latLng);
            }
        });

        //마커 클릭 해을떄 리스너
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                if (marker.equals(a)) // 이렇게 객체 비교 해야된다.
//                {
                    Toast.makeText(getApplicationContext(),marker.getTitle(),Toast.LENGTH_SHORT).show();
//                }
                // 토스트나 알럿 메세지...
                return false;
            }
        });



        /*
     // 맵 위치를 이동하기
		CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(37.502985, 126.799043));
    // 화면 이동
		mGoogle.moveCamera(update);
		이렇게 해도됨
         */
        //
        //맵 위치로 이동하기
        map.moveCamera(CameraUpdateFactory.newLatLng(M2));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

       // map.setPadding(300,300,300,300); // left, top, right, bottom //버튼이나 그런거 위치 한정?
        map.getUiSettings().setZoomControlsEnabled(true);
       //  map.setMapType(GoogleMap.MAP_TYPE_HYBRID); // 지도 유형 변경
        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);

        button1.setOnClickListener(new Button.OnClickListener()
        { @Override
            public void onClick(View view)
            {
                for(int i=0;i<MarkerPoints.size()-1;i++) {
                  //  tv.setText("");
                    String url[] = getUrl(MarkerPoints.get(i), MarkerPoints.get(i+1));
                    fetchUrl fUrl = new fetchUrl();
                    fUrl.execute(url[0], url[1]);
                }
            }
        });
        button2.setOnClickListener(new Button.OnClickListener()
        { @Override
        public void onClick(View view)
        {
            MarkerPoints.clear();
            map.clear();
            l_s_DD.clear();
            tv.setText("Distance, Duration");
            DD_cnt=0;



        }
        });
        button3.setOnClickListener(new Button.OnClickListener()
        { @Override
        public void onClick(View view)
        {
            try {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                intentBuilder.setLatLngBounds(BOUNDS_VIEW);
                Intent intent = intentBuilder.build(MainActivity.this);
                startActivityForResult(intent,PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
        });
    }

    private void pickMark(final GoogleMap map, final LatLng LL)
    {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(LL);
        markerOptions.title(String.format(Locale.KOREA,"%.3f",LL.latitude)+","+String.format(Locale.KOREA,"%.3f",LL.longitude));
        markerOptions.snippet("snippet");
        markerOptions.draggable(true);
        //색 다르게인가?
        if(MarkerPoints.size()==0)
        {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
        else if (MarkerPoints.size() == 1) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        else if(MarkerPoints.size()>1)
        {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        }
        map.addMarker(markerOptions).setDraggable(true);
        MarkerPoints.add(LL);
    } // pickMark


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            // Get the last location.
            lastKnownLocation = location;


            lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    10,
                    locationListener
            );
            tv.setText(String.format(Locale.KOREA,"%.3f",lastKnownLocation.getLatitude())+ " , "+ String.format(Locale.KOREA,"%.3f",lastKnownLocation.getLongitude()));
            Toast.makeText(getApplicationContext(), String.format(Locale.KOREA,"%.3f",lastKnownLocation.getLatitude())+ " , "+ String.format(Locale.KOREA,"%.3f",lastKnownLocation.getLongitude()), Toast.LENGTH_SHORT).show();
           // lm.removeUpdates(locationListener);

        }// onLocationChanged

        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

//////////////////////////// My Location start

    @Override
    public boolean onMyLocationButtonClick() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
        if(gps_cnt==0) {
            Toast.makeText(this, "GPS 추적 ON", Toast.LENGTH_SHORT).show();
            gps_cnt++;
        }
        else
        {
            lm.removeUpdates(locationListener);
            Toast.makeText(this, "GPS 추적 OFF", Toast.LENGTH_SHORT).show();
            gps_cnt=0;
        }
        return false;
    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                 != PackageManager.PERMISSION_GRANTED) {
                     // Permission to access the location is missing.
           PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                             Manifest.permission.ACCESS_FINE_LOCATION, true);
                 } else if (map != null) {
                     // Access to the location has been granted to the app.
                     map.setMyLocationEnabled(true);

        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();


        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

/////////////////////// My Location End


    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK)
        {
            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();


            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }

            pickMark(map,place.getLatLng());

            tv.setText("");
            tv.append("name : " + name + "\n");
            tv.append("address\n" + address+"\n");
            Log.d("Place_Pick","1");
            tv.append(Html.fromHtml(attributions));
            Log.d("Place_Pick","2");
            Log.d("Place_Pick",attributions);
            map.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            map.animateCamera(CameraUpdateFactory.zoomTo(13));
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    } // 구글 플레이스 정보 가져오기


// https://maps.googleapis.com/maps/api/directions/outputFormat?parameters
    //https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood4&key=YOUR_API_KEY
    // origin=41.43206,-81.38992

    // url 보내고 받아서 파싱 하는 부분 시작
    private String[] getUrl(LatLng origin, LatLng dest)
    {
        String[] url = {"",""};
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_origin2 = "origins=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String str_dest2 = "destinations=" + dest.latitude + "," + dest.longitude;
//AIzaSyDIPfmJXw78A2tKbCtGZekNxAQcli7eoLM
        //derection
        url[0] = "https://maps.googleapis.com/maps/api/directions/json?" +  str_origin +"&"+str_dest+"&mode=transit"+ "&key=AIzaSyDIPfmJXw78A2tKbCtGZekNxAQcli7eoLM";
//        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=75+9th+Ave+New+York,+NY&destination=MetLife+Stadium+1+MetLife+Stadium+Dr+East+Rutherford,+NJ+07073&key=AIzaSyDIPfmJXw78A2tKbCtGZekNxAQcli7eoLM";
        // distancematirx // origins , detanations로 해야된다.
//        String url =  "https://maps.googleapis.com/maps/api/distancematrix/json?origins=Vancouver+BC|Seattle&destinations=San+Francisco|Victoria+BC&mode=transit&transit_mode=train&key=AIzaSyBuR03U17WtCJ50jQI3WNsQOKWN9FeUu6s";
        url[1] = "https://maps.googleapis.com/maps/api/distancematrix/json?" +  str_origin2 +"&"+str_dest2+"&mode=transit"+ "&key=AIzaSyBuR03U17WtCJ50jQI3WNsQOKWN9FeUu6s";

        return url;
    }
    private String downloadUrl(String strUrl) throws IOException
    {
        String data = "";
        InputStream iStream = null;
        HttpsURLConnection urlConnection = null;
        Log.d("Url",strUrl);
        try{
            URL url = new URL(strUrl);

            // url 만들기
            urlConnection = (HttpsURLConnection) url.openConnection();
            Log.d("Url","1");

            // 연결
            urlConnection.connect();
            Log.d("Url","2"); //여기서 안되네;;

            // 데이터 읽기
            iStream = urlConnection.getInputStream();
            Log.d("Url","3");

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
//            BufferedReader br = new BufferedReader(new InputStreamReader(iStream, StandardCharsets.UTF_8));
            Log.d("Url","4");

            StringBuffer sb = new StringBuffer();

            String line = "";
            while((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        }
        catch (Exception e)
        {
            Log.d("Urlfail", "urldownloadfail");
        }
        finally
        {
            Log.d("Urlend", "end");
            iStream.close();;
            urlConnection.disconnect();
        }
        return data;
    }


    private class fetchUrl extends AsyncTask<String, Void, String[]>

    {
        protected String[] doInBackground(String... url)
        {
            String[] data = {"",""};
            try {
                data[0] = downloadUrl(url[0]);
                data[1] = downloadUrl(url[1]);

                Log.d("dD", data[0].toString());
                Log.d("dD", data[1].toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;

        }
        protected void onPostExecute(String[] result){
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

            //Intent MyIntent = new Intent(getApplicationContext(),Urltextview.class);
            //Log.d("Ftext", result[0]);
            //Log.d("Ftext", result[1]);
            //MyIntent.putExtra("url",result[0]+ "\n\n\n\n\n" + result[1]);
           // startActivity(MyIntent);

//            fetchedtext = result;
        }
    }// fetchUrl

    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>>
    {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject_route, jObject_DD;
            List<List<HashMap<String,String >>> routes = null;
            List<List<String>> DD;

            try {
                jObject_route = new JSONObject(jsonData[0]);
                jObject_DD = new JSONObject((jsonData[1]));

                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject_route);
                DD = parser.parse_DT(jObject_DD);
                String sDD ="";
                Log.d("ParserTask","DDsize: " +DD.get(0).size());
                for(int i=0;i<DD.get(0).size();i++) {
                    sDD = DD.get(0).get(i)+" "+DD.get(1).get(i);
//                    s_DD = DD.get(0).get(i) + " " + DD.get(1).get(i) + "\n";
                    // s_DD.concat(DD.get(0).get(i)+"\n"+DD.get(1).get(i));
                  //  Log.d("l_s_DD", sDD);

//                    Log.d("DisDur_main",DD.get(0).get(i)+"\n"+DD.get(1).get(i));
                }
               // Log.d("l_s_DD", "sDD: "+sDD);
                l_s_DD.add(sDD);

                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }// doinback

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            DD_cnt++;
            Log.d("l_s_DD","cnt: " + DD_cnt);
            Log.d("l_s_DD", "size :" + l_s_DD.size());
           // Log.d("l_s_DD","l_s_DD[0]: " + l_s_DD.get(0));

            if(DD_cnt == l_s_DD.size()) {
//                tv.setGravity(Gravity.LEFT);
                tv.setText("1: " + l_s_DD.get(0)+"\n");
                Log.d("l_s_DD","l_s_DD[0]: " + l_s_DD.get(0));
                for (int i = 1; i < l_s_DD.size(); i++) {
                    Log.d("l_s_DD","l_s_DD["+i+"]: " + l_s_DD.get(i));
                    tv.append(i+1+": " + l_s_DD.get(i)+"\n");
                }
            }
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.rgb(33,142,233));//8EC7fF

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                map.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }

    }// ParserTask
// url 보내고 받아서 파싱 하는 부분 끝
}

