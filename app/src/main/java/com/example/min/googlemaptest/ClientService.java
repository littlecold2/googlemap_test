package com.example.min.googlemaptest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClientService extends Service implements Runnable {

    private Socket s;   // 소켓통신할 소켓
    private BufferedReader inMsg; // 받은 메시지 읽을 버퍼
    private PrintWriter outMsg; // 메세지 보낼 라이터

    private String a_targetIp="13.124.63.18"; // 서버 ip
    private int a_targetPort=9000; // 서버 port

    List<Userdata> message_List;

    Double lastLatitude;
    Double lastLongitude;
    Intent myintent;
    Thread myThread;
    boolean key=false;
    boolean key2=false;
    private String j_inmsg=""; // 받은 메시지 저장
    private String j_outmsg="";

    IBinder mBinder = new Mybinder();
    public LocationManager locationManager;
    public MyLocationListener listener;


    private Location lastKnownLocation = null ; // gps에서 위치 정보 계속 받아서 저장


    class Mybinder extends Binder {
        ClientService getService(){
            return ClientService.this;
        }
    }


    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
       // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);

        myThread= new Thread(this);
        myThread.start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if(intent==null) {
            Log.d("SVC","strcmd");
            return Service.START_STICKY;
        }
        else
        {
            Log.d("SVC","strcmd");
            myintent = intent;

        }

        return super.onStartCommand(intent, flags, startId);


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
       // throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {

        Log.d("SVC","destroy");
        try {
            s.close();
            locationManager.removeUpdates(listener);

            myThread.interrupt();

        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    // 소켓통신 부분 시작





        protected void finalize() throws Throwable
        {
            s.close(); // 끝날때 소켓 닫음
        }




        public void run() // 쓰레드 시작부분
        {
            Log.d("SVC", a_targetIp+ " " +String.format(Locale.KOREA,"%d",a_targetPort));
            connectServer(a_targetIp,a_targetPort); // 서버에 연결 하는 함수 받아온 ip.port 넘겨줌

                while (s != null && !myThread.isInterrupted()) // 소켓연결이 되어있을경우 무한루프
                {
                    //Log.d("D_socket","sleep");
                    //try {
                    //    Thread.sleep(3000);
//                    Log.d("D_socket","lklt: "+lastKnownLocation);
                    Log.d("SVC", "msging");
                    if(key2)
                        sendMessage(); // 서버에 현재 위치정보 담아서 보냄
                    else
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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

              // Thread.sleep(10000); // 서버연결 됬을시 3초정도 쉼
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

        public void sendMessage() // 서버에 메시지 보내는 함수
        {
//            String j_inmsg=""; // 받은 메시지 저장
//            String j_outmsg="";

            Userdata m = new Userdata(); // 메시지 형식 프로토콜 클래스 (현재 이름, 위도, 경도)
            List<Userdata> L_m = new ArrayList<>(); // 서버에서 주는 지금 접속해있는 클라이언트 위치정보 받을 메시지 리스트

            Gson gson = new Gson(); // JSon 직렬화 해서 편하게 쓰는 Gson

            if(s.isClosed() ) // 소켓 연경 안되잇으면
            {
                return;
            }
//            inMsg.read(inmsg,0,512);
            try {
                //outMsg.println(String.format(Locale.KOREA,"%f",Lat)+","+String.format(Locale.KOREA,"%f",Lng));

     //           j_outmsg = myintent.getStringExtra("outmsg");
             //   msg = Jsonize(Build.USER,Lat,Lng); // 단말기 유저, 위도, 경도정보를 JSON화 함. Gson 이용

                outMsg.println(j_outmsg); // JSON화한 메시지를 서버로 보냄 (내정보, 내위치, 경도)
                j_inmsg = inMsg.readLine(); // 내가 메시지 보낸 이후 서버에서 보낸 메시지 수신


//                Intent showIntent = new Intent(getApplicationContext(), MainActivity.class);
//                showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
//                                    Intent.FLAG_ACTIVITY_SINGLE_TOP|
//                                    Intent.FLAG_ACTIVITY_CLEAR_TOP
//                                    );
//                //
              //  showIntent.putExtra("inmsg_list",j_inmsg);
              //  startActivity(showIntent);

                message_List= gson.fromJson(j_inmsg, new TypeToken<ArrayList<Userdata>>() {}.getType()); // 서버에서 받은 메시지(모든 클라이언트의 이름,위치 메시지 리스트)를 JSON->Gosn-> ArrayList<Userdata>로 해서 저장
                key=true;
                Log.d("SCV","j_inmsg: "+j_inmsg);
              //  message_List = L_m; // 전역변수에 그 받은 리스트 데이터 저장 (쓰레드에서는 토스트나 텍스트뷰 접근이 안되서 메인에서 쓰기위해)


            } catch (IOException e) {
                e.printStackTrace();
            }

           // return inmsg;
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
        void setOutMsg(String outmsg)
        {
            j_outmsg= outmsg;
        }
        List<Userdata> getInmsg()
        {
            return message_List;
        }
        boolean getkey()
    {
        return key;
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

    public class MyLocationListener implements LocationListener
    {

        @Override
        public void onLocationChanged(Location location) {
            String inmsg ="";
            List<Userdata> message_List = new ArrayList<>();
            Gson gson = new Gson();
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            // Get the last location.
            lastKnownLocation = location; // 업데이트 된 주소 저장
            Log.d("loc_d",lastKnownLocation.toString());
            lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, // 네트워크+gps 이용 업데이트
                    1000, //1초마다
                    10, // 최소 거리 10미터
                    listener
            );
            if(s==null) // 서버와 연결 안됬으면 현재 위치 텍스트뷰에
                Log.d("CSV","not connect");
              //  tv.setText(String.format(Locale.KOREA,"%.3f",lastKnownLocation.getLatitude())+ " , "+ String.format(Locale.KOREA,"%.3f",lastKnownLocation.getLongitude()));
            else { // 서버 연결 됫으면 메세지 받은 걸 텍스트 뷰에 뿌림
              //  message_List = cs.getInmsg();
                Log.d("ddddd",inmsg);
               // message_List = gson.fromJson(inmsg, new TypeToken<ArrayList<Userdata>>() {}.getType()); // 서버에서 받은 메시지(모든 클라이언트의 이름,위치 메시지 리스트)를 JSON->Gosn-> ArrayList<Userdata>로 해서 저장
//                tv.setText("");
                for(Userdata ud:message_List) {
                    Log.d("CSV","list: "+"name: " + ud.getName() + " lat: " + ud.getLat() + " lng: " + ud.getLng()+"\n");
  //                  tv.append("name: " + ud.getName() + " lat: " + ud.getLat() + " lng: " + ud.getLng()+"\n");
                }
                Toast.makeText(getApplicationContext(),"메시지 받음",Toast.LENGTH_SHORT).show();
            }
            // Toast.makeText(getApplicationContext(), String.format(Locale.KOREA,"%.3f",lastKnownLocation.getLatitude())+ " , "+ String.format(Locale.KOREA,"%.3f",lastKnownLocation.getLongitude()), Toast.LENGTH_SHORT).show();
            if( lastKnownLocation.hasAltitude()) { // lastKnownLocation이 위치를 받아왔고  키가 0이면 소켓통신 스타트

                j_outmsg = Jsonize(Build.USER,lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                key2=true;
                //   serviceIntent.putExtra("outmsg",msg);
                //  startService(serviceIntent);
                //cs.setOutMsg(msg);

                //  SC.start(); // 소켓통신 관련 쓰레드함수 시작

                //msg=Jsonize(Build.USER,lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                //sv_key=1;
            }


//            Toast.makeText(getApplicationContext(), SC.sendMessage(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude()),Toast.LENGTH_SHORT).show();
            // lm.removeUpdates(locationListener);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
