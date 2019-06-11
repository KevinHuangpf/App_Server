import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class AppServer {


    // ip = 103.46.128.49
    // 端口 = 47018
    private static final int SOCKET_PORT = 80;
    private ServerSocket serverSocket = null;
    private boolean flag = true;

    private int socketId;
    private long receiveTimeDelay = 500;
    //240ms

    private ArrayList<Message> mMsgList = new ArrayList<Message>();
    private ArrayList<SocketThread> mThreadList = new ArrayList<SocketThread>();

    /**
     * @param args
     */
    public static void main(String[] args) {
        AppServer socketServer = new AppServer();
        socketServer.initSocket();
    }

    private void initSocket() {
        try {
            serverSocket = new ServerSocket(SOCKET_PORT);
            System.out.println("服务已经启动，端口号:" + SOCKET_PORT);
            startMessageThread();
            while (flag) {
                Socket clientSocket = serverSocket.accept();
                SocketThread socketThread = new SocketThread(clientSocket, socketId++);
                socketThread.start();
                mThreadList.add(socketThread);
                if(socketThread.socket.isClosed()){
                    mThreadList.remove(socketThread);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class SocketThread extends Thread {

        public Socket socket;
        public int mSocketId;
        public BufferedReader reader;
        public BufferedWriter writer;
        public InputStreamReader in;

        long lastReceiveTime = System.currentTimeMillis();
        long nowTime = System.currentTimeMillis();


        public SocketThread(Socket clientSocket, int socketId) {
            this.mSocketId = socketId;
            this.socket = clientSocket;
            System.out.println("新注册用户的id为：" + mSocketId);
            //获取输入流
            InputStream inputStream;
            try {
                inputStream = socket.getInputStream();
                //得到读取BufferedReader对象

                reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
                writer.write("用户名为：" + mSocketId + "\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            super.run();
            try {
                boolean flag1 = true;
                //循环读取客户端发过来的消息
                while (flag1) {
                    nowTime=System.currentTimeMillis();

                    if (reader.ready()) {

                        lastReceiveTime = System.currentTimeMillis();
                        String comeData = reader.readLine();
                        JSONObject msgJson = new JSONObject(comeData);
   /*                     if(msgJson.toString().equals("-1")){
                            socket.close();
                            writer.close();
                            break;
                        }*/
                        Message msg = new Message();
                        msg.setType(msgJson.getInt("type"));
                        if (msg.getType() == 01) {
                            msg.setTo(msgJson.getInt("to"));
                            msg.setMsg(msgJson.getString("msg"));
                            msg.setFrom(mSocketId);
                            msg.setTime(getTime(System.currentTimeMillis()));
                            mMsgList.add(msg);
                            System.out.println(msg.getTime() + " 用户：" + mSocketId + "发往服务器内容：" +msg.getMsg());
                        }else if(msg.getType() == 00){
                            //System.out.println("~心跳检测~");
                        }


                    }
                    if (System.currentTimeMillis() - lastReceiveTime > receiveTimeDelay) {
                        try {
                            socket.getInputStream().close();
                            socket.close();

                            flag1=false;
                            System.out.println(mSocketId + "超时关闭");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startMessageThread() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    while (flag) {
                        if (mMsgList.size() > 0) {
                            //System.out.println("sssss");
                            Message from = mMsgList.get(0);
                            for (SocketThread toThread : mThreadList) {
                                //遍历mThreadList如果to.socketID==from.to说明这个toThread与mMsgList中的这条内容是对应的
                                //这里toThread的作用通过它得到这条消息的BufferedWriter，mMsgList.get(0)得到这条消息，然后通过
                                //BufferedWriter将这条消息发送到 指定方
                                if (toThread.mSocketId == from.getFrom()) {
                                    continue;
                                }
/*                                if (toThread.socket.isClosed()) {
                                    mThreadList.remove(toThread.socket);
                                    continue;
                                }*/
                                //Socket socketTemp = toThread.socket;
                                /*                                isAlivingSocket(toThread.socket);*/
/*                                if (toThread.mSocketId == from.getFrom()) {
                                    continue;
                                }*/
                                BufferedWriter writer = toThread.writer;
                                JSONObject json = new JSONObject();

                                try{
                                    json.put("from", from.getFrom());
                                    json.put("msg", from.getMsg());
                                    json.put("time", from.getTime());
                                    writer.write(json.toString() + "\n");
                                    writer.flush();
                                    System.out.println(from.getFrom()+" -"+from.getMsg()+"-> "+toThread.mSocketId);
                                }catch (Exception e){
/*                                    writer.close();
                                    toThread.socket.close();
                                    mThreadList.remove(toThread.socket);*/
                                    continue;
                                }
/*                                if(toThread.mSocketId == from.getTo()) {
                                }*/

                            }

                            mMsgList.remove(0);
                        }
                        //很重要，处理时延5ms，不加回导致写入错误
                        Thread.sleep(10);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private String getTime(long millTime) {
        Date d = new Date(millTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }


}
