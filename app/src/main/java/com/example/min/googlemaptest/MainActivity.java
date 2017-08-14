package com.example.min.googlemaptest;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

//import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;



public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    ArrayList<LatLng> MarkerPoints; // 나중에 쓰기
    LatLng M1 = new LatLng(37.56, 126.97);
    LatLng M2 = new LatLng(37.55, 126.96);
   // Urltextview urlview;
    String fetchedtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String url = getUrl(M1,M2);
        fetchUrl fUrl = new fetchUrl();
        fUrl.execute(url);
        //fetchedtext = fetchUrl(url);

        //Log.d("iii", "뭐지");
      //  urlview = new Urltextview();
     //   urlview = (Urltextview) getApplicationContext();  // Context를 해당 Class로 캐스팅해줘야 NullPointerException이 발생하지 않습니다.
//        ((Urltextview)Urltextview.mContext).setStr("aaaaa");


    }
    private void pickMark(final GoogleMap map, final LatLng LL)
    {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(LL);
            markerOptions.title("title");
            markerOptions.snippet("snippet");
            markerOptions.draggable(true);
            map.addMarker(markerOptions).setDraggable(true);

    } // pickMark
    @Override
    public void onMapReady(final GoogleMap map) {


        pickMark(map,M2);
/*        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        markerOptions.draggable(true);
        map.addMarker(markerOptions).setDraggable(true);
        // 함수나 클래스로 만드려면 이렇게 하는게 나을 듯
        */
        // 뭐지

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
        map.moveCamera(CameraUpdateFactory.newLatLng(M1));
        map.animateCamera(CameraUpdateFactory.zoomTo(12));

        map.setPadding(300,300,300,300); // left, top, right, bottom //버튼이나 그런거 위치 한정?
        map.getUiSettings().setZoomControlsEnabled(true);
       //  map.setMapType(GoogleMap.MAP_TYPE_HYBRID); // 지도 유형 변경
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new Button.OnClickListener()
        { @Override
            public void onClick(View view)
            {
                String url = getUrl(M1,M2);
                fetchUrl fUrl = new fetchUrl();
                fUrl.execute(url);
               // fetchedtext = fUrl;
                Toast.makeText(getApplicationContext(),"url 버튼 눌림",Toast.LENGTH_LONG).show();
//                Intent MyIntent = new Intent(getApplicationContext(),Urltextview.class);
//                Log.d("Ftext", url);
//                MyIntent.putExtra("url",url);
//                startActivity(MyIntent);
                //EditText editTextName = (EditText) findViewById(R.id.editTextName) ;
                // intent.putExtra("contact_name", editTextName.getText().toString()) ;
            }
        });




    }
// https://maps.googleapis.com/maps/api/directions/outputFormat?parameters
    //https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood4&key=YOUR_API_KEY
    // origin=41.43206,-81.38992
    private String getUrl(LatLng origin, LatLng dest)
    {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
//AIzaSyDIPfmJXw78A2tKbCtGZekNxAQcli7eoLM
       //String url = "https://maps.googleapis.com/maps/api/directions/json?" +  str_origin +"&"+str_dest+"&key=AIzaSyDIPfmJXw78A2tKbCtGZekNxAQcli7eoLM";
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=75+9th+Ave+New+York,+NY&destination=MetLife+Stadium+1+MetLife+Stadium+Dr+East+Rutherford,+NJ+07073&key=AIzaSyDIPfmJXw78A2tKbCtGZekNxAQcli7eoLM";
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
    private class fetchUrl extends AsyncTask<String, Void, String>

    {
        protected String doInBackground(String... url)
        {
            String data = "";
            try {
                data = downloadUrl(url[0]);
                Log.d("dD", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;

        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            Intent MyIntent = new Intent(getApplicationContext(),Urltextview.class);
            Log.d("Ftext", result);
            MyIntent.putExtra("url",result);
            startActivity(MyIntent);
//            fetchedtext = result;
        }
    }//fetchUrl
//    void urlbutton1(View v)
//    {
//    }

}

