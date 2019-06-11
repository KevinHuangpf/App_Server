
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerTest {

    /**
     * Socket服务端
     */
    public static void main(String[] args)  {
        try {
            ServerSocket serverSocket = new ServerSocket(80);
            System.out.println("服务端已启动，等待客户端连接..");
            Socket socket = serverSocket.accept();
            //侦听并接受到此套接字的连接,返回一个Socket对象

            socket.setOOBInline(false);

            socket.setKeepAlive(true);
            socket.setSoTimeout(10);
            //根据输入输出流和客户端连接
            InputStream inputStream = socket.getInputStream();
            //得到一个输入流，接收客户端传递的信息
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            //提高效率，将自己字节流转为字符流
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            //加入缓冲区
            String temp = null;
            String info = "";

            OutputStream outputStream = socket.getOutputStream();
            //获取一个输出流，向服务端发送信息
            PrintWriter printWriter = new PrintWriter(outputStream);
            //将输出流包装成打印流
            boolean isKeep = true;

            while (true) {
                try {
                    while (isKeep) {
                        System.out.println("在线");
                        socket.sendUrgentData(0);
                        Thread.sleep(1 * 1000);
                    }
                } catch (Exception e) {
                    System.out.println("断开了");
                    isKeep = false;
                }

/*                Thread.sleep(1000);
                try {
                    socket.sendUrgentData(0xFF);

                } catch (Exception e) {

                    socket.close();
                    break;
                    //进到这里，已表示该客户端已经断开，需要捕获异常进行处理了
                    //个人觉得在两端无法实现心跳包检测的情况下使用比较好
                }*/


  /*              printWriter.print("你好，服务端已接收到您的信息");
                System.out.println("返回了数据");
                printWriter.flush();
                socket.shutdownOutput();//关闭输出流*/
            }

/*            while((temp=bufferedReader.readLine())!=null){
                info=temp;
                System.out.println("已接收到客户端连接");
                System.out.println("服务端接收到客户端信息："+info+",当前客户端ip为："+socket.getInetAddress().getHostAddress());
            }*/


            //关闭相对应的资源
/*            printWriter.close();
            outputStream.close();
            bufferedReader.close();
            inputStream.close();
            socket.close();*/

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}