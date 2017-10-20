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
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Delayed;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import static android.os.StrictMode.setThreadPolicy;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class MainActivity extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{

    private GoogleMap map;
    private boolean mPermissionDenied = false;

    private Socket_Controller SC;

    private Location lastKnownLocation = null ;

    int sv_key=0;
    int L_cnt=0;
    int ccnt=0;
    private String uName;
    private Double uLat;
    private Double uLng;
    List<Userdata> message_List = new ArrayList<>();

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
    List<String> l_s_DD;
    String msg;
    private  int gps_cnt=0;

    private static final int PLACE_PICKER_REQUEST =1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.d("D_sock","destroy");
//        try {
//            SC.s.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }



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
//        SC = new Socket_Controller("13.124.63.18",9000);
//        SC.run();


        tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                s.toString().length();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        try {
//            ServerSocket ss = new ServerSocket(9000);
//            ss.getInetAddress()
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        String url = getUrl(M1,M2);
//        fetchUrl fUrl = new fetchUrl();
//        fUrl.execute(url);
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); setThreadPolicy(policy);

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

        // 맵 클릭 리스너
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//               pickMark(latLng,"a","a");
                String url = getUrl(latLng);
                fetch_RgeoUrl fUrl = new fetch_RgeoUrl();
                fUrl.execute(url,Double.toString(latLng.latitude),Double.toString(latLng.longitude));
                Log.d("R_g",Double.toString(latLng.latitude)+" " +Double.toString(latLng.longitude));
            }
        });


        // 폴리라인 클릭 리스너
        map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {

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
                SC = new Socket_Controller("13.124.63.18",9000);
                SC.start();

                for(int i=0;i<MarkerPoints.size()-1;i++) {
                    tv.setText("");
                    String url = getUrl(MarkerPoints.get(i), MarkerPoints.get(i+1));
                    fetchUrl fUrl = new fetchUrl();
                    fUrl.execute(url);
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

            if(L_cnt==0) {
                L_cnt++;
                map.setMyLocationEnabled(false);
//                map.getUiSettings().setMyLocationButtonEnabled(true);
            }
            else {
                map.setMyLocationEnabled(true);
                L_cnt=0;
            }


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

    private void pickMark(final LatLng LL,String name, String address)
    {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(LL);
//        markerOptions.title(String.format(Locale.KOREA,"%.3f",LL.latitude)+","+String.format(Locale.KOREA,"%.3f",LL.longitude));
        markerOptions.title(name);
        markerOptions.snippet(address.substring(0,40));
//        markerOptions.snippet(address);
        markerOptions.draggable(true);

        //색 다르게
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
        map.addMarker(markerOptions).showInfoWindow();
        MarkerPoints.add(LL);
    } // pickMark





///////////////////////////////////////////////////////////////////// My Location start


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            // Get the last location.
            lastKnownLocation = location;
            Log.d("loc_d",lastKnownLocation.toString());
            lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    10,
                    locationListener
            );
            if(SC.s==null)
                tv.setText(String.format(Locale.KOREA,"%.3f",lastKnownLocation.getLatitude())+ " , "+ String.format(Locale.KOREA,"%.3f",lastKnownLocation.getLongitude()));
            else {
                tv.setText("");
                for(Userdata ud:message_List) {
                    tv.append("name: " + ud.getName() + " lat: " + ud.getLat() + " lng: " + ud.getLng()+"\n");
                }
                Toast.makeText(getApplicationContext(),"메시지 받음",Toast.LENGTH_SHORT).show();
            }
           // Toast.makeText(getApplicationContext(), String.format(Locale.KOREA,"%.3f",lastKnownLocation.getLatitude())+ " , "+ String.format(Locale.KOREA,"%.3f",lastKnownLocation.getLongitude()), Toast.LENGTH_SHORT).show();
            if(sv_key==0&& lastKnownLocation.hasAltitude()) {
                SC.start();

                //msg=Jsonize(Build.USER,lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                sv_key=1;
            }


//            Toast.makeText(getApplicationContext(), SC.sendMessage(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude()),Toast.LENGTH_SHORT).show();
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

// about location button
    @Override
    public boolean onMyLocationButtonClick() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Toast.makeText(this, "GPS 켜지지않음", Toast.LENGTH_SHORT).show();

            return false;
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
        if(gps_cnt==0) {
            Toast.makeText(this, "GPS 추적 ON", Toast.LENGTH_SHORT).show();
            SC = new Socket_Controller("13.124.63.18",9000);
            gps_cnt++;
        }
        else
        {
            lm.removeUpdates(locationListener);
         //   SC.interrupt();
            Toast.makeText(this, "GPS 추적 OFF", Toast.LENGTH_SHORT).show();
     //       SC.interrupt();
            SC.disconnect();
            sv_key=0;
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

/////////////////////// about Location buttoon

/////////////////////// Google place
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

            pickMark(place.getLatLng(),name.toString(),address.toString());

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
    private String getUrl(LatLng origin, LatLng dest)
    {
        String url = "";
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
//        if()
        Log.d("l_d",Long.toString(System.currentTimeMillis()));
//        long now = System.currentTimeMillis();
//        Date date = new Date(now);
//        SimpleDateFormat a = new SimpleDateFormat("hh a, zzzz");
//        Log.d("l_d",a.format(date));

        //derection
        url = "https://maps.googleapis.com/maps/api/directions/json?" +  str_origin +"&"+str_dest +"&mode=transit"+"&alternatives=true"+  "&key=AIzaSyAKq5CUx3CsSpnWt-Ls7P_SPzCtz6FpVRE ";
        return url;
}
    private String getUrl(LatLng place)
    {
        String url = "";
        String latlng_place = "latlng=" + place.latitude + "," + place.longitude;
        Log.d("latlng",latlng_place);
        url = "https://maps.googleapis.com/maps/api/geocode/json?" + latlng_place + "&key=AIzaSyAqvBEwiML_d6OFoDf35dhxDc-V_5pMVfc";
       // http://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&key=AIzaSyCm03LatWFr4wpLYSOnVqHZdjsNPc8hvi0 - PC
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

            // 연결
            urlConnection.connect();

            // 데이터 읽기
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
//            BufferedReader br = new BufferedReader(new InputStreamReader(iStream, StandardCharsets.UTF_8));

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


    private class fetchUrl extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... url)
        {
            String data="";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;

        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
//            Intent MyIntent = new Intent(getApplicationContext(),Urltextview.class);
//
//            MyIntent.putExtra("url",result+ "\n\n\n************\n\n\n");
//            startActivity(MyIntent);


            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }
    }// fetchUrl
    private class fetch_RgeoUrl extends AsyncTask<String, String, String[]>
    {
        protected String[] doInBackground(String... URLnLL)
        {
            String[] data={"","",""};
            try {
                data[0] = downloadUrl(URLnLL[0]);
                data[1] = URLnLL[1];
                data[2] = URLnLL[2];
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;

        }
        protected void onPostExecute(String[] result){
            super.onPostExecute(result);
            JSONObject jObject;
            List<String> Rgeo;
            try {
                jObject= new JSONObject(result[0]);
                DataParser parser = new DataParser();
                LatLng LL = new LatLng(Double.parseDouble(result[1]),Double.parseDouble(result[2]));
                Rgeo = parser.R_geocoding(jObject);
                pickMark(LL,Rgeo.get(0),Rgeo.get(1));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }// fetchRgeoUrl

    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>>
    {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject_route;
            List<List<HashMap<String,String >>> routes = null;
//            List<List<String>> DD;

            try {
                jObject_route = new JSONObject(jsonData[0]);
                //jObject_DD = new JSONObject((jsonData[1]));

                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject_route);

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

            // Traversing through all the routes

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);


                Log.d("d_parsing", "path size: " + Integer.toString(path.size()));
                // Fetching all the points in i-th route

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if(point.containsKey("Distance")||point.containsKey("Duration")) {
                        String Dis = point.get("Distance");
                        String Dur = point.get("Duration");
                        tv.append(Dis + " , " + Dur + "\n");
                    }
                    else{
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
//                        Log.d("d_parsing", "lat: " + Double.toString(lat) + "  lng:" + Double.toString(lng));
                        points.add(position);
                    }

                } // for
                // Adding all the points in the route to LineOptions
//                    lineOptions.addAll(points);
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    if(ccnt==0) {
                        lineOptions.color(Color.rgb(33, 142, 233));//8EC7fF
                        ccnt++;
                    }
                    else {
                        lineOptions.color(Color.rgb(133, 142, 233));//8EC7fF
                        ccnt=0;
                    }
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

    class Socket_Controller extends Thread{

        private Socket s;
        private BufferedReader inMsg;
        private PrintWriter outMsg;

        private String a_targetIp;
        private int a_targetPort;


        protected void finalize() throws Throwable
        {
            s.close();
        }
        Socket_Controller(String ip,int port )
        {
            a_targetIp = ip;
            a_targetPort = port;
            Log.d("D_socket", a_targetIp+ " " +String.format(Locale.KOREA,"%d",a_targetPort));
//        this.connectServer(a_targetIp,a_targetPort);

        }
        Socket_Controller()
        {
        }

        public void run()
        {
            Log.d("D_socket", a_targetIp+ " " +String.format(Locale.KOREA,"%d",a_targetPort));
            connectServer(a_targetIp,a_targetPort);

            while(s!=null)
            {
                //Log.d("D_socket","sleep");
                //try {
                //    Thread.sleep(3000);
//                    Log.d("D_socket","lklt: "+lastKnownLocation);
                    sendMessage(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                  //  sendMessage(12131.13131,222.123213);
                //} catch (InterruptedException e) {
                 //   e.printStackTrace();
               // }
            }
//            try {
//                s.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


        }
        public void connectServer(String targetIp,int targetPort)
        {
            try{
                // 소켓 생성
                if((s = new Socket(targetIp,targetPort))==null)
                {
                  //  Toast.makeText(getApplicationContext(), "[Client]Server 연결 실패!!", Toast.LENGTH_SHORT).show();
                    Log.d("D_socket", "[Client]Server 연결 성공1!!");
                    return;
                }
                else {
                 //   Toast.makeText(getApplicationContext(), "[Client]Server 연결 성공!!", Toast.LENGTH_SHORT).show();
                    Log.d("D_socket", "[Client]Server 연결 성공2!!");

                }
                // 입출력 스트림 생성
                inMsg = new BufferedReader(new InputStreamReader(s.getInputStream()));
                outMsg = new PrintWriter(s.getOutputStream(),true);
               // outMsg.println(Build.USER);
                Log.d("D_socket", Build.USER);
                // 서버에 로그인 메시지 전달
                Thread.sleep(3000);
//            m.setId(v.id);
//            m.setType("login");

                //System.out.println(mL.getId()+"");
                //System.out.println(mL.getType()+"");
                //System.out.println(mL.getRoom()+"");

//            outMsg.println(gson.toJson(m)); // 출력 스트림으로 mL에 담은 메시지를 Json형식으로 해서 보낸다. 쓴다.

                // 메시지 수신을 위한 스레드 생성


            }catch(Exception e) {
                Log.d("D_socket", "Error : " + e);
                //Toast.makeText(getApplicationContext(), "[Client]Server 연결 실패!!", Toast.LENGTH_SHORT).show();
                //e.printStackTrace();
                //return;
            }
        }// connectServer()

        public String sendMessage(Double Lat, Double Lng )
        {
            String inmsg="";
            Userdata m = new Userdata();
            List<Userdata> L_m = new ArrayList<>();
           // Userdata[] get_m ;
            Gson gson = new Gson();
         //   L_m.cl
            if(!s.isConnected() )
            {
               // Toast.makeText(getApplicationContext(), "[Client]Server 연결 실패!!", Toast.LENGTH_SHORT).show();
                return "[Client]Server 연결 실패!!";
            }
//            inMsg.read(inmsg,0,512);
            try {
                //outMsg.println(String.format(Locale.KOREA,"%f",Lat)+","+String.format(Locale.KOREA,"%f",Lng));
                //Log.d("c_S","start");
                msg = Jsonize(Build.USER,Lat,Lng);

                //Log.d("c_S","start");
                outMsg.println(msg);
                inmsg = inMsg.readLine();

//                Log.d("c_S",inmsg);
             //   m = gson.fromJson(inmsg,Userdata.class);
                  L_m = gson.fromJson(inmsg, new TypeToken<ArrayList<Userdata>>() {}.getType());
//                String json = new Gson().toJson(L_m);
//                gson.fromJson(inmsg,getClass(List<Userdata>))
//                Log.d(get_m[0].get;
                message_List = L_m;
//                uName = m.getName();
//                uLat = m.getLat();
//                uLng = m.getLng();
////                tv.setText("name: " + m.getName()+" lat: "+m.getLat()+" lng: "+m.getLng());
//                Toast.makeText(getApplicationContext(),"name: " + m.getName()+"lat: "+m.getLat()+"lng: "+m.getLng(),Toast.LENGTH_SHORT).show();
             //   Log.d("c_S","name: " + m.getName()+" lat: "+m.getLat()+" lng: "+m.getLng());
                //Log.d("c_S",Integer.toString(inmsg.length()));
              //  Log.d("c_S",inmsg);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return inmsg;
        }
        public void disconnect()
        {

           // Logger.getLogger(this.getClass().getName()).;

            try {
                if(s!=null)

                    s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }
    public String Jsonize(String name, Double lat,  Double lng)
    {

        String json = new Gson().toJson(new Userdata(name,lat,lng));
        Log.d("gson",json);
       // assertNotNull(json);
       // assertEquals(json,data);

       // Userdata obj = new Gson().fromJson(data,Userdata.class);
        //Log.d("gson",obj.getName());

        //assertEquals(Build.USER,obj.getName());
        return json;

    }

}//main



