package com.example.min.googlemaptest;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

//import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;



public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

        LatLng SEOUL = new LatLng(37.56, 126.97);
        LatLng GG = new LatLng(37.55, 126.96);
        pickMark(map,GG);
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
                .position(SEOUL)
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
        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        map.animateCamera(CameraUpdateFactory.zoomTo(12));

        map.setPadding(300,300,300,300); // left, top, right, bottom //버튼이나 그런거 위치 한정?
        map.getUiSettings().setZoomControlsEnabled(true);
       //  map.setMapType(GoogleMap.MAP_TYPE_HYBRID); // 지도 유형 변경
    }

}
