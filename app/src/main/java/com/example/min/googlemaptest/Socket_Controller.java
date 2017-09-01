package com.example.min.googlemaptest;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// JSON으로 바꿔서 정보같은거 많이 넘기는거로 최종으론? GSON +
// 서버에서 로그인 메세지 받으면 새로 뿌다 뿌려지게 하고
// 서버 사람들어오면 쓰레드로 하나씩 생성해서 각개전투로

/**
 * Created by MIN on 2017-08-31.
 */

public class Socket_Controller {

    private Socket s;
    private BufferedReader inMsg;
    private PrintWriter outMsg;

    private Thread thread;
    private String a_targetIp;
    private int a_targetPort;

    Socket_Controller(String ip,int port )
    {
        a_targetIp = ip;
        a_targetPort = port;
    }

//    public void Sokcet_main()
//    {
//        thread = new Thread();
//        thread.start(); //run으로
//
//    }
//    public void run()
//    {
//        connectServer(a_targetIp,a_targetPort);
//    }

    public void connectServer(String targetIp,int targetPort )
    {

        try{
            // 소켓 생성
            s = new Socket(targetIp,targetPort);
            Log.d("D_socket", "[Client]Server 연결 성공!!");


            // 입출력 스트림 생성
            inMsg = new BufferedReader(new InputStreamReader(s.getInputStream()));
            outMsg = new PrintWriter(s.getOutputStream(),true);

            // 서버에 로그인 메시지 전달
            outMsg.println("login");

//            m.setId(v.id);
//            m.setType("login");

            //System.out.println(mL.getId()+"");
            //System.out.println(mL.getType()+"");
            //System.out.println(mL.getRoom()+"");

//            outMsg.println(gson.toJson(m)); // 출력 스트림으로 mL에 담은 메시지를 Json형식으로 해서 보낸다. 쓴다.

            // 메시지 수신을 위한 스레드 생성


        }catch(Exception e)
        {
            Log.d("D_socket", "Error : "+e);
            e.printStackTrace();
        }
    }// connectServer()



}
