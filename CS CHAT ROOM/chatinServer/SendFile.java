package chatinServer;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SendFile implements Runnable {
	
	private static String target;
	private static String filename;
	Socket targetSocket=null;

	
	SendFile(String target,String filename)
	{
		SendFile.target=target;

		SendFile.filename=filename;
	
	}
	
	
	
    public void run()
	{
	
     targetSocket = StringSocketMap.map.get(target);
   	 int length = 0;
     byte[] sendByte = null;
     BufferedOutputStream dout = null;
     FileInputStream fin = null;
     DataInputStream dis = null;
       try {
    	   ServerSocket ss = new ServerSocket(8086);
    	   System.out.println("开始监听发文件");
    	   Socket socket = ss.accept();
    	   if(socket.getInetAddress().getHostAddress().equals( targetSocket.getInetAddress().getHostAddress()))
    	   {
    		  
    	   
    	   System.out.println("收到连接");
         dout = new BufferedOutputStream(socket.getOutputStream());
         File file = new File("C:/Files/"+filename);
         System.out.println("请求文件:"+"C:/Files/"+filename);
        File tstfile = new File("C:/Files/"+filename);
      if (!tstfile.exists()) {
      System.out.println("no============");
      }else {
      System.out.println("yes============");
      }
         fin = new FileInputStream(file);
         dis=new DataInputStream(fin);
         sendByte = new byte[1024];
         
         
         byte[] buffer = new byte[8]; 
         for (int i = 0; i < 8; i++) {   
              int offset = 64 - (i + 1) * 8;    
              buffer[7-i] = (byte) ((file.length() >> offset) & 0xff); 
          }

         dout.write(buffer,0,buffer.length);
         dout.flush();
         Thread.sleep(1000);
        
         dout.write(file.getName().getBytes(),0,file.getName().getBytes().length);
         dout.flush();	
         Thread.sleep(1000);
         
         while((length = dis.read(sendByte, 0, sendByte.length))>0){
             dout.write(sendByte,0,length);
             dout.flush();
             Thread.sleep(1);
             sendByte = new byte[1024];
         }
         ss.close();
    	   }
       }
         catch (Exception e) {
        	 e.printStackTrace();
         }
       
	}
	


 }

   
