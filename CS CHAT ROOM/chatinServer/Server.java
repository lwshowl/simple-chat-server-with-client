package chatinServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    public static ArrayList<Socket> list = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        Socket socket = null;
        String ip = null;
    	int port = 8083;   //端口
		int countpeople=0; //人数计数
        ServerSocket serverSocket=new ServerSocket(port);   //server socket
        System.out.println("服务端已启动，等待客户端连接.."+"端口:"+port);   
        
        while (true) {
            try {
            	
            	socket=serverSocket.accept();//侦听并接受到此套接字的连接,返回一个Socket对象
            	countpeople ++;
            	String flag = "false";    //返回的登录验证标志
            	SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");  //时间
            	ip = socket.getInetAddress().getHostAddress();  //       // 用户IP 地址
            	
            //读取用户的登录信息
            InputStream inputStream=socket.getInputStream();//得到一个输入流，接收客户端传递的信息
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);//提高效率，将自己字节流转为字符流
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);//加入缓冲区
                /*****************接受数据**********************/
                String info = null;   //接受的混合账号密码信息
                String username="";      //分离的账号
                String userpwd="";       //分离的密码
                if((info=bufferedReader.readLine()) != null) {}
                System.out.println("收到消息"+info);
                	
                    String[] account = info.split(",");//以","隔开的账号密码
                    
                    username=account[0];
                    try {
                    userpwd=account[1];
                    }
                    catch(ArrayIndexOutOfBoundsException e){
                    	e.printStackTrace();
                    	userpwd="null";
                    }
                    

                    /*****************开始登录验证**********************/
                    System.err.println( ip + " 用户尝试登陆, 时间:  "+df.format(new Date()));
                    LoginVerify verify = new LoginVerify(username,userpwd);  //建立验证对象
                    flag=verify.DBverify();
                    String[] buf = flag.split(",");  //验证信息+“，”+登录人的昵称
                    System.out.println(" 验证信息:"+flag );
                    System.out.println("----------------------");
                    //*********************** 返回验证信息***********************//
                    OutputStreamWriter outSW = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
					BufferedWriter bw = new BufferedWriter(outSW);
					bw.write(flag);	//向客户端反馈消息，加上分行符以便客户端接收
					bw.flush();
                    
                          /*  PrintWriter pw;
                            pw = new PrintWriter(socket.getOutputStream());
                            //写入信息
                            pw.println( flag);
                            pw.flush();*/
                    //***************************开启聊天线程********************//
                        if(buf[0].contentEquals("true"))
                        {
                        	M2MThread client= new M2MThread(socket,username);
                        	list.add(socket);
                        	StringSocketMap.map.put(username, socket);
                        	System.err.println( ip + " 用户:" + username + "，上线了当前在线用户为: " + list.size() + "人 !" );
                        	client.start();
                        }
                    }
             catch (IOException e) 
                {
            	 System.err.println(socket.getInetAddress().getHostAddress()+ "登录失败");
            }
        }
                
    }
}