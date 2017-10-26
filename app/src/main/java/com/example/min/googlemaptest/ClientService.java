package com.example.min.googlemaptest;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClientService extends Service implements Runnable {

    private Socket s;   // 소켓통신할 소켓
    private BufferedReader inMsg; // 받은 메시지 읽을 버퍼
    private PrintWriter outMsg; // 메세지 보낼 라이터

    private String a_targetIp; // 서버 ip
    private int a_targetPort; // 서버 port

    List<Userdata> message_List;
    String Usrname;
    Double lastLatitude;
    Double lastLongitude;

    public ClientService(String ip,int port ) {
        a_targetIp = ip;
        a_targetPort = port;
        Log.d("D_socket", a_targetIp+ " " +String.format(Locale.KOREA,"%d",a_targetPort));
    }
    public ClientService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void CommunicateData(Intent intent)
    {
        Bundle bundle = intent.getExtras();
        Userdata usr = (Userdata) bundle.getParcelable("userdata");
        lastLatitude = usr.getLng();
        lastLongitude = usr.getLat();
        Usrname = usr.getName();

        Intent showIntent = new Intent(getApplicationContext(),MainActivity.class);
        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
                            Intent.FLAG_ACTIVITY_SINGLE_TOP|
                            Intent.FLAG_ACTIVITY_CLEAR_TOP);


        showIntent.putExtra("usrdata_list",message_List);


    }

    // 소켓통신 부분 시작





        protected void finalize() throws Throwable
        {
            s.close(); // 끝날때 소켓 닫음
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
                sendMessage(lastLatitude,lastLongitude); // 서버에 현재 위치정보 담아서 보냄
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

}
