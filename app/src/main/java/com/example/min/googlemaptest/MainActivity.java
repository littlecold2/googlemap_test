package com.example.min.googlemaptest;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

//import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;



public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap map;
    ArrayList<LatLng> MarkerPoints; // 나중에 쓰기
    //로마
//    LatLng M2 = new LatLng(41.86, 12.97); // 경도, 위도
//    LatLng M1 = new LatLng(41.85, 12.46);
    //서울
    LatLng M1 = new LatLng(37.56, 126.97);
    LatLng M2 = new LatLng(37.55, 126.47);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MarkerPoints = new ArrayList<>();
        MarkerPoints.clear();
//        String url = getUrl(M1,M2);
//        fetchUrl fUrl = new fetchUrl();
//        fUrl.execute(url);
//

    }
    private void pickMark(final GoogleMap map, final LatLng LL)
    {

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(LL);
            markerOptions.title(Double.toString(LL.latitude)+","+Double.toString(LL.longitude));
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
        map.addMarker(markerOptions).setDraggable(true);
        MarkerPoints.add(LL);
    } // pickMark
    @Override
    public void onMapReady(final GoogleMap gMap) {
        map = gMap;
        //pickMark(map,M2);
/*        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        markerOptions.draggable(true);
        map.addMarker(markerOptions).setDraggable(true);
        // 함수나 클래스로 만드려면 이렇게 하는게 나을 듯
        */
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
                if(MarkerPoints.size()>1){
                    MarkerPoints.clear();
                    map.clear();
                }
                pickMark(map,latLng);
                if (MarkerPoints.size() >= 2) {
                    LatLng origin = MarkerPoints.get(0);
                    LatLng dest = MarkerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getUrl(origin, dest);
                    Log.d("onMapClick", url.toString());
                    fetchUrl FetchUrl = new fetchUrl();

                    // Start downloading json data from Google Directions API
                    FetchUrl.execute(url);
                    //move map camera
                    map.moveCamera(CameraUpdateFactory.newLatLng(origin));
                   // map.animateCamera(CameraUpdateFactory.zoomTo(11));
                }
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
        map.animateCamera(CameraUpdateFactory.zoomTo(9));

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
       String url = "https://maps.googleapis.com/maps/api/directions/json?" +  str_origin +"&"+str_dest+"&mode=transit"+ "&key=AIzaSyDIPfmJXw78A2tKbCtGZekNxAQcli7eoLM";
//        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=75+9th+Ave+New+York,+NY&destination=MetLife+Stadium+1+MetLife+Stadium+Dr+East+Rutherford,+NJ+07073&key=AIzaSyDIPfmJXw78A2tKbCtGZekNxAQcli7eoLM";
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
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

            Intent MyIntent = new Intent(getApplicationContext(),Urltextview.class);
            Log.d("Ftext", result);
            MyIntent.putExtra("url",result);
            startActivity(MyIntent);

//            fetchedtext = result;
        }
    }// fetchUrl

    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>>
    {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String,String >>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
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
                lineOptions.color(Color.RED);

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



    }

}

