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

    private GoogleMap map; // 구글맵 사용 할 때 필요
    private boolean mPermissionDenied = false; // gps 권한 체크

    private Socket_Controller SC; // 소켓 부분 컨트롫 할 쓰레드 클래스 변수

    private Location lastKnownLocation = null ; // gps에서 위치 정보 계속 받아서 저장

    int sv_key=0; // 서버 접속 한번만 하게 체크하는 변수
    int L_cnt=0; // gps 추적 버튼 나오고 사라지게 하고 체크 할때 씀 걍 체크 용이었네
    int ccnt=0; // 길찾기 라인 색 다르게 체크

    List<Userdata> message_List = new ArrayList<>(); // 사용자들 위치 리스트 수신 받을 변수

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
            new LatLng(37.56, 126.98), new LatLng(37.57, 127.02));     // 위치 정보 저장 테스트용이었음

    TextView tv; // 아래 텍스트 출력 부분 컨트롤
    String msg; // 내정보 json 으로 바꿔서 저장할 변수 (서버로 보낸다 이거)
    private  int gps_cnt=0; // gps 추적 컨트롤

    private static final int PLACE_PICKER_REQUEST =1; // 위치검색 쓸 때
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1; // 위치 권한 쓸때

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
        mapFragment.getMapAsync(this);  // 구글맵 프레그먼트 적용


        tv = (TextView) findViewById(R.id.DDtext);
        MarkerPoints = new ArrayList<>(); // 마커 저장 시켜놓을 리스트
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
        }); //텍스트뷰 와쳐
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
    @Override // 구글 맵 컨트롤 부분
    public void onMapReady(final GoogleMap gMap) {
        map = gMap;
        map.setOnMyLocationButtonClickListener(this); // gps 버튼 활성화
        enableMyLocation(); // 내 위치 활성화


        // 예제로 마커 추가해 봤던거
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
            @Override //마커 클릭시
            public boolean onMarkerClick(Marker marker) {
//                if (marker.equals(a)) // 이렇게 객체 비교 해야된다.
//                {
                Toast.makeText(getApplicationContext(),marker.getTitle(),Toast.LENGTH_SHORT).show(); // 마커 클릭 시 걔 이름 출력
//                }
                // 토스트나 알럿 메세지...
                return false;
            }
        });

        // 맵 클릭 리스너 // 맵클릭시 마커 찍고 역 지오코딩함수에 위치정보 넘겨줘서 그 위치 정보 받아와서 마커 정보에 추가하게 함
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//               pickMark(latLng,"a","a");
                String url = getUrl(latLng); // 위치정보 주고 넘겨줄 url형식으로 받아옴
                fetch_RgeoUrl fUrl = new fetch_RgeoUrl(); // url fetch 할 클래스 생성
                fUrl.execute(url,Double.toString(latLng.latitude),Double.toString(latLng.longitude)); // url fetch 할때 위치정보 넘겨줌
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
        map.getUiSettings().setZoomControlsEnabled(true); // 줌 버튼 가능하게

       //  map.setMapType(GoogleMap.MAP_TYPE_HYBRID); // 지도 유형 변경
        Button button1 = (Button) findViewById(R.id.button1); // 대중교통 길찾기 버튼
        Button button2 = (Button) findViewById(R.id.button2); // clear 버튼
        Button button3 = (Button) findViewById(R.id.button3); // 위치검색 버튼

        button1.setOnClickListener(new Button.OnClickListener()
        { @Override
            public void onClick(View view)
            { // 길찾기 버튼
          //      SC = new Socket_Controller("13.124.63.18",9000);
            //    SC.start();

                // 찍혀있는 마커 차례대로 두개씩 대중교통 길찾기 실행 (ex, 0->1,  1->2)
                for(int i=0;i<MarkerPoints.size()-1;i++) {
                    tv.setText("");
                    String url = getUrl(MarkerPoints.get(i), MarkerPoints.get(i+1)); // 마커 위치정보 넘겨줘서 맞는 url형식 만듬
                    fetchUrl fUrl = new fetchUrl(); // fetch할 클래스 생성
                    fUrl.execute(url); // url fetch
                }


            }
        });

        button2.setOnClickListener(new Button.OnClickListener()
        { @Override
        public void onClick(View view)
        { // 클리어 버튼
            MarkerPoints.clear(); // 마커 저장 해논 리스트 클리어
            map.clear(); // 맵에있는 오브젝트 클리어
            tv.setHint("Distance, Duration");


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
        { //위치검색 (PlacePicker)
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
    /////////////////////// Google place PlacePicker
    protected void onActivityResult(int requestCode, int resultCode, Intent data) // PlacePicker 끝날 때 정보 받아오기
    {
        if(requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK)
        {
            final Place place = PlacePicker.getPlace(this, data); // 정보 받아오기
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();


            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }

            pickMark(place.getLatLng(),name.toString(),address.toString()); // 받아온 정보에서 위치, 이름 , 주소 받아와서 마크 찍기

            tv.setText("");
            tv.append("name : " + name + "\n");
            tv.append("address\n" + address+"\n"); // 텍스트 뷰에 띄우기
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


    private void pickMark(final LatLng LL,String name, String address) // 위도 경도, 이름 주소 받아서 마커 찍는 함수
    {
        MarkerOptions markerOptions = new MarkerOptions(); // 옵션 설정 해놓을 변수
        markerOptions.position(LL); // 위치 적용
//        markerOptions.title(String.format(Locale.KOREA,"%.3f",LL.latitude)+","+String.format(Locale.KOREA,"%.3f",LL.longitude));
        markerOptions.title(name); // 이름
//        markerOptions.snippet(address.substring(0,20)); // 주소 넣음
        markerOptions.snippet(address); // 주소 넣음
        markerOptions.draggable(true); // 드래그 가능하도록

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
        map.addMarker(markerOptions).showInfoWindow(); // 맵에 추가
        MarkerPoints.add(LL); // 위치정보 마커 리스트에 추가
    } // pickMark





///////////////////////////////////////////////////////////////////// My Location start


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {  // 위치정보 바뀔때마다 이 함수 불러옴

            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            // Get the last location.
            lastKnownLocation = location; // 업데이트 된 주소 저장
            Log.d("loc_d",lastKnownLocation.toString());
            lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, // 네트워크+gps 이용 업데이트
                    1000, //1초마다
                    10, // 최소 거리 10미터
                    locationListener
            );
            if(SC.s==null) // 서버와 연결 안됬으면 현재 위치 텍스트뷰에
                tv.setText(String.format(Locale.KOREA,"%.3f",lastKnownLocation.getLatitude())+ " , "+ String.format(Locale.KOREA,"%.3f",lastKnownLocation.getLongitude()));
            else { // 서버 연결 됫으면 메세지 받은 걸 텍스트 뷰에 뿌림
                tv.setText("");
                for(Userdata ud:message_List) {
                    tv.append("name: " + ud.getName() + " lat: " + ud.getLat() + " lng: " + ud.getLng()+"\n");
                }
                Toast.makeText(getApplicationContext(),"메시지 받음",Toast.LENGTH_SHORT).show();
            }
           // Toast.makeText(getApplicationContext(), String.format(Locale.KOREA,"%.3f",lastKnownLocation.getLatitude())+ " , "+ String.format(Locale.KOREA,"%.3f",lastKnownLocation.getLongitude()), Toast.LENGTH_SHORT).show();
            if(sv_key==0&& lastKnownLocation.hasAltitude()) { // lastKnownLocation이 위치를 받아왔고  키가 0이면 소켓통신 스타트
                SC.start(); // 소켓통신 관련 쓰레드함수 시작

                //msg=Jsonize(Build.USER,lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                sv_key=1;
            }


//            Toast.makeText(getApplicationContext(), SC.sendMessage(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude()),Toast.LENGTH_SHORT).show();
           // lm.removeUpdates(locationListener);

        }// onLocationChanged

        @Override
        public void onProviderDisabled(String provider) { // gps꺼져잇을때

            Toast.makeText(getApplicationContext(), "GPS 꺼짐", Toast.LENGTH_SHORT).show();
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
    public boolean onMyLocationButtonClick() { // 위치 추적 버튼 누를때
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) //GPS 없을때
        {
            Toast.makeText(this, "GPS 켜지지않음, GPS를 켜주세요.", Toast.LENGTH_SHORT).show();

            return false;
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
        if(gps_cnt==0) {
            Toast.makeText(this, "GPS 추적 ON", Toast.LENGTH_SHORT).show();
            SC = new Socket_Controller("13.124.63.18",9000);
            gps_cnt++;
        }
        else // gps끌때 소켓통신 종료, 추적 종료
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

//////////////////////////  위치정보 권한 관련
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

///////////////////////




// https://maps.googleapis.com/maps/api/directions/outputFormat?parameters
    //https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood4&key=YOUR_API_KEY
    // origin=41.43206,-81.38992

    // url 보내고 받아서 파싱 하는 부분 시작
    private String getUrl(LatLng origin, LatLng dest) // 위치 두개 받아서 길찾기 URL 형식으로 바꿈  // 키 필요   Google Direction APi 이용
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
    private String getUrl(LatLng place) // 위치 하나 받아서 그 위치정보 받아오는 역지오코딩 형식으로 바꿈 // 키 필요
    {
        String url = "";
        String latlng_place = "latlng=" + place.latitude + "," + place.longitude;
        Log.d("latlng",latlng_place);
        url = "https://maps.googleapis.com/maps/api/geocode/json?" + latlng_place + "&key=AIzaSyAqvBEwiML_d6OFoDf35dhxDc-V_5pMVfc";
       // http://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&key=AIzaSyCm03LatWFr4wpLYSOnVqHZdjsNPc8hvi0 - PC
        return url;
    }


    // 길찾기 할때 패치함
    private class fetchUrl extends AsyncTask<String, Void, String> // AsyncTsk는 일종의 쓰레드 doInBackground 에서 PostExecute로 return값 넘겨줄수 있고, Post Execute는 ui컨트롤 부분 가능 Google Direction APi 이용
    {
        protected String doInBackground(String... url)
        {
            String data="";
            try {
                data = downloadUrl(url[0]); // URL 보내서 정보 받기
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
    private class fetch_RgeoUrl extends AsyncTask<String, String, String[]> // 역지오코딩 할 때 쓰는 패치 // 맵 클릭 했을때 위치정보 받아온느 역할
    {
        protected String[] doInBackground(String... URLnLL)
        {
            String[] data={"","",""};
            try {
                data[0] = downloadUrl(URLnLL[0]); //  만든 URL 이용 결과 다운로드 하는 함수
                data[1] = URLnLL[1];// 맵 클릭해서 받아온 위도 저장
                data[2] = URLnLL[2];// 맵 클릭해서 받아온 경도 저장
                //data는 onPostExecute로 넘겨줄거
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
                jObject= new JSONObject(result[0]); // JSON으로 받은내용 오브젝트에 저장
                DataParser parser = new DataParser(); // 파싱을 할 클래스 생성
                LatLng LL = new LatLng(Double.parseDouble(result[1]),Double.parseDouble(result[2])); // 넘겨받은 위치정보 (위도경도)를 저장
                Rgeo = parser.R_geocoding(jObject); // 역지오코딩으로 파싱한 결과를 저장
                pickMark(LL,Rgeo.get(0),Rgeo.get(1)); // 파싱해 얻어온 내용 (이름, 주소)를 맵에 마커찍는다


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }// fetchRgeoUrl

    private String downloadUrl(String strUrl) throws IOException // 만든 URL 보내서 관련 정보 받아오기
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
            while((line = br.readLine()) != null) // 다 읽을 때 까지 버퍼에 계속 넣기
            {
                sb.append(line);
            }

            data = sb.toString(); // 버퍼에 쌓인 내용 저장
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

    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>> // 맵에 길찾기 한 루트를 Polyline을 이용해 그려주고 소요시간, 거리 가져오는 함수 DataParser클래스를 이용해 JSON파싱한 내용을 이용한다. Google Direction APi 이용
    {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
//루트 관련 정보 저장
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

                for (int j = 0; j < path.size(); j++) { // 패스 수 많금 포문
                    HashMap<String, String> point = path.get(j);

                    if(point.containsKey("Distance")||point.containsKey("Duration")) { // 거리나 소요시간 키를 가지고 있으면
                        String Dis = point.get("Distance"); // 그 거리 정보 가져온다.
                        String Dur = point.get("Duration"); // 그 소요시간 정보 가져온다.
                        tv.append(Dis + " , " + Dur + "\n"); // 텍스트 뷰에 그 정보들 뿌려준다.
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


// 소켓통신 부분 시작
    class Socket_Controller extends Thread{

        private Socket s;   // 소켓통신할 소켓
        private BufferedReader inMsg; // 받은 메시지 읽을 버퍼
        private PrintWriter outMsg; // 메세지 보낼 라이터

        private String a_targetIp; // 서버 ip
        private int a_targetPort; // 서버 port


        protected void finalize() throws Throwable
        {
            s.close(); // 끝날때 소켓 닫음
        }
        Socket_Controller(String ip,int port ) // 생성자로 ip,port받아서 넣음
        {
            a_targetIp = ip;
            a_targetPort = port;
            Log.d("D_socket", a_targetIp+ " " +String.format(Locale.KOREA,"%d",a_targetPort));
//        this.connectServer(a_targetIp,a_targetPort);

        }
        Socket_Controller()
        {
        }

        public void run() // 쓰레드 시작부분
        {
            Log.d("D_socket", a_targetIp+ " " +String.format(Locale.KOREA,"%d",a_targetPort));
            connectServer(a_targetIp,a_targetPort); // 서버에 연결 하는 함수 받아온 ip.port 넘겨줌

            while(s!=null) // 소켓연결이 되어있을경우 무한루프
            {
                //Log.d("D_socket","sleep");
                //try {
                //    Thread.sleep(3000);
//                    Log.d("D_socket","lklt: "+lastKnownLocation);
                    sendMessage(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude()); // 서버에 현재 위치정보 담아서 보냄
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
        public void connectServer(String targetIp,int targetPort) // 서버에 연결하는부분
        {
            try{
                // 소켓 생성
                if((s = new Socket(targetIp,targetPort))==null) // 소켓연결 성공 실패시
                {
                  //  Toast.makeText(getApplicationContext(), "[Client]Server 연결 실패!!", Toast.LENGTH_SHORT).show();
                    Log.d("D_socket", "[Client]Server 연결 fail!!");
                    return;
                }
                else {
                 //   Toast.makeText(getApplicationContext(), "[Client]Server 연결 성공!!", Toast.LENGTH_SHORT).show();
                    Log.d("D_socket", "[Client]Server 연결 성공!!");

                }
                // 입출력 스트림 생성
                inMsg = new BufferedReader(new InputStreamReader(s.getInputStream())); // 수신 메시지 담을 버퍼
                outMsg = new PrintWriter(s.getOutputStream(),true); //송신 메시지 롸이터
               // outMsg.println(Build.USER);
                Log.d("D_socket", Build.USER);

                Thread.sleep(3000); // 서버연결 됬을시 3초정도 쉼
//            m.setId(v.id);
//            m.setType("login");

                //System.out.println(mL.getId()+"");
                //System.out.println(mL.getType()+"");
                //System.out.println(mL.getRoom()+"");

//            outMsg.println(gson.toJson(m)); // 출력 스트림으로 mL에 담은 메시지를 Json형식으로 해서 보낸다. 쓴다.



            }catch(Exception e) {
                Log.d("D_socket", "Error : " + e);
                //Toast.makeText(getApplicationContext(), "[Client]Server 연결 실패!!", Toast.LENGTH_SHORT).show();
                //e.printStackTrace();
                //return;
            }
        }// connectServer()

        public String sendMessage(Double Lat, Double Lng ) // 서버에 메시지 보내는 함수
        {
            String inmsg=""; // 받은 메시지 저장
            Userdata m = new Userdata(); // 메시지 형식 프로토콜 클래스 (현재 이름, 위도, 경도)
            List<Userdata> L_m = new ArrayList<>(); // 서버에서 주는 지금 접속해있는 클라이언트 위치정보 받을 메시지 리스트

            Gson gson = new Gson(); // JSon 직렬화 해서 편하게 쓰는 Gson

            if(s.isClosed() ) // 소켓 연경 안되잇으면
            {
               // Toast.makeText(getApplicationContext(), "[Client]Server 연결 실패!!", Toast.LENGTH_SHORT).show();
                return "[Client]Server 연결 실패!!";
            }
//            inMsg.read(inmsg,0,512);
            try {
                //outMsg.println(String.format(Locale.KOREA,"%f",Lat)+","+String.format(Locale.KOREA,"%f",Lng));


                msg = Jsonize(Build.USER,Lat,Lng); // 단말기 유저, 위도, 경도정보를 JSON화 함. Gson 이용

                outMsg.println(msg); // JSON화한 메시지를 서버로 보냄 (내정보, 내위치, 경도)
                inmsg = inMsg.readLine(); // 내가 메시지 보낸 이후 서버에서 보낸 메시지 수신

                 L_m = gson.fromJson(inmsg, new TypeToken<ArrayList<Userdata>>() {}.getType()); // 서버에서 받은 메시지(모든 클라이언트의 이름,위치 메시지 리스트)를 JSON->Gosn-> ArrayList<Userdata>로 해서 저장
                 message_List = L_m; // 전역변수에 그 받은 리스트 데이터 저장 (쓰레드에서는 토스트나 텍스트뷰 접근이 안되서 메인에서 쓰기위해)

//                String json = new Gson().toJson(L_m);
//                gson.fromJson(inmsg,getClass(List<Userdata>))
//                Log.d(get_m[0].get;

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
        public void disconnect() // 연결 종료 함수
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
    public String Jsonize(String name, Double lat,  Double lng) // 데이터 받아서 JSON화 하는 함수 Data -> Gson -> json
    {

        String json = new Gson().toJson(new Userdata(name,lat,lng)); //Data -> Gson -> json
        Log.d("gson",json);
       // assertNotNull(json);
       // assertEquals(json,data);

       // Userdata obj = new Gson().fromJson(data,Userdata.class);
        //Log.d("gson",obj.getName());

        //assertEquals(Build.USER,obj.getName());
        return json;

    }

}//main



