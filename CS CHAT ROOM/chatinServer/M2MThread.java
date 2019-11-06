package chatinServer;

import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.lang.reflect.Array;
import java.net.*;

public class M2MThread extends Thread{
	private Socket fromSocket;    //登录人的socket
	private Socket toSocket;      //发送目的SOCKET
	private String ip;            //发送人ip
	private String username;      //发送人的账号
	
	public M2MThread (Socket from,String username)
	{
		fromSocket = from;
		this.username=username;
	}
	
	public void run()
	{
		ip = fromSocket.getInetAddress().getHostAddress();  //获取客户的 主机地
		
		//try
		//{
			//读取信息
			BufferedReader reader;
  try {
		reader = new BufferedReader(new InputStreamReader(fromSocket.getInputStream(),"UTF-8"));
			
			
			//一直等待读取
			while(true)
			{
				
				//接受数据
				String info = null;
                String toUser;       //分离的发送人信息
                String typeofmsg;
				if((info=reader.readLine())!=null) {}
				System.out.println("收到消息:"+"来自:"+ ip +" "+ info);
				
				/*********************解析消息*********************/
			      String[] msgkind = info.split(",");//以","隔开消息，发送对象
			      typeofmsg = msgkind[0];
			      
			      
				/**************************收到好友请求***********************/
				
				if(typeofmsg.contentEquals("RequestForBuddy"))
				{
					   Connection con = null;// 创建一个数据库连接
					    Statement statement = null;
					    ResultSet result = null;// 创建2一个结果集对象
					    try
					    {
					        Class.forName("oracle.jdbc.driver.OracleDriver");// 加载Oracle驱动程序
					        String url = "jdbc:oracle:" + "thin:@localhost:1521:ORCL";// 127.0.0.1是本机地址，XE是精简版Oracle的默认数据库名
					        String user = "$$$$$";// 用户名,系统默认的账户名
					        String password = "$$$$$";// 你安装时选设置的密码
					        con = DriverManager.getConnection(url, user, password);// 获取连接
					        System.out.println("数据库连接成功！");
					        statement = con.createStatement(); 
					        result = statement.executeQuery("SELECT user2,nickname FROM chatuser,buddy where buddy.user2 = chatuser.username and user1="+"'"+username+"'");
					        System.out.println("已执行数据库查找好友操作");
					        
					        ArrayList<String> buddylist = new ArrayList<String>();
					        ArrayList<String> nicknames = new ArrayList<String>();
					        String buddyinfo=new String();
					        String nickinfo= new String();
					       while (result.next())
					            // 当结果集不为空时
					       {
					    	  buddylist.add(result.getString("user2").toString());
					    	  nicknames.add(result.getString("nickname").toString());
					       }
					       
					       for(int i=0;i<buddylist.size();i++)
					       {
					    	   buddyinfo= buddyinfo + buddylist.get(i)+"("+nicknames.get(i)+")"+",";
					    	   
					       }
					       buddyinfo =buddylist.size()+","+buddyinfo;
					       System.out.println("好友消息已发回 (数量,好友列表):"+buddyinfo);
					   
					       
					       // 把好友发回去
					   	for(Socket ss: Server.list )
					   	{
							if(ss==fromSocket) //发回给发送人
								{
								OutputStreamWriter outSW = new OutputStreamWriter(ss.getOutputStream(), "UTF-8");
								BufferedWriter bw = new BufferedWriter(outSW);
								bw.write(buddyinfo+","+username);	//向客户端反馈消息，加上分行符以便客户端接收
								bw.flush();
									
								/*System.out.println("消息已发送");
								PrintWriter pw;
								pw = new PrintWriter(ss.getOutputStream());
								pw.println(buddyinfo);
								pw.flush();*/
								}
							}
					   	}
					    catch (Exception f)
					    {
					        f.printStackTrace();
					    }
					    finally
					    {
					        try
					        {
					            // 逐一将上面的几个对象关闭，因为不关闭的话会影响性能、并且占用资源
					            // 注意关闭的顺序，最后使用的最先关闭
					            if (result != null)
					                result.close();
					            if (statement != null)
					                statement.close();
					            if (con != null)
					                con.close();
					        }
					        catch (Exception g)
					        {
					            g.printStackTrace();
					        }
					        
					    }
					    
				}
				
				/*************************************************/
				
				/***************收到的是文件请求消息****************/
			
				else if(typeofmsg.contentEquals("File"))
			{
					
					String msg = new String();         
					String target = new String();
					

					try {
	                String[] account = info.split(",");//以","隔开消息，发送对象
	                msg=account[1];        
	                target = account[2];             
					}  
					catch(Exception e)
					{
						System.err.println("消息解析失败");
						e.printStackTrace();
					
					}
	  
	                
					toSocket =StringSocketMap.map.get(target);
					
					
						for(Socket ss: Server.list )
						{
							if(ss==toSocket)
							{
								OutputStreamWriter outSW = new OutputStreamWriter(ss.getOutputStream(), "UTF-8");
								BufferedWriter bw = new BufferedWriter(outSW);
								bw.write("File"+","+msg+","+username);	//向客户端反馈消息，加上分行符以便客户端接收
								bw.flush();
								
								
								System.out.println("消息已发送"+"File"+","+msg+","+username);
								
								//PrintWriter pw;

							}
							
						}
			}
				
		  /***********************发送的是 真正的传送文件请求*****************/
		   else if(typeofmsg.contentEquals("PortFile"))
					
				{  
			   
				     String msg = new String();         
				     String target = new String();
					try {
	                String[] account = info.split(",");//以","隔开消息，发送对象
	                 msg=account[1];        
	                 target = account[2];             
					}  
					catch(Exception e)
					{
						System.err.println("消息解析失败");
						e.printStackTrace();
					
					}
					
					ReciveFile recfilerunnable = new ReciveFile(username,target);   //开启传送文件线程
					recfilerunnable.setss(8085);
					Thread recfile = new Thread(recfilerunnable);
					recfile.start();
					boolean isalive=true;
					while(isalive)
					{
						isalive = recfile.isAlive();
					}
					recfile.stop();
					System.out.println("接受传输完毕");
					
					
					
						
				}
						
				
				
				/**********************确认收文件信息*/////////////////
				else if(typeofmsg.contentEquals("ACCFile"))
				{
					String msg = new String();
					String target = new String();
					
					try {
	                String[] account = info.split(",");//以","隔开消息，发送对象
	                msg=account[1];
	                target = account[2];
					}
					catch(Exception e)
					{
						System.err.println("消息解析失败");
						e.printStackTrace();
					
					}
					SendFile SendFileRunnable = new SendFile(username,msg);
					Thread SendFile = new Thread(SendFileRunnable);
					SendFile.start();
					boolean isalive=true;
					while(isalive)
					{
						isalive = SendFile.isAlive();
					}
					SendFile.stop();
					System.out.println("发送传输完毕");

				}
						
				
				
				

				/**************收到非好友请求(收到消息)****************/
				
				
				else if(typeofmsg.contentEquals("Msg"))
				{
				String msg = new String();
				String target = new String();
				
				try {
                String[] account = info.split(",");//以","隔开消息，发送对象
                msg=account[1];
                target = account[2];
				}
				catch(Exception e)
				{
					System.err.println("消息解析失败");
					e.printStackTrace();
				}
  
                
				toSocket =StringSocketMap.map.get(target);
				
				
					for(Socket ss: Server.list )
					{
						if(ss==toSocket)
						{
							OutputStreamWriter outSW = new OutputStreamWriter(ss.getOutputStream(), "UTF-8");
							BufferedWriter bw = new BufferedWriter(outSW);
							bw.write("Msg"+","+msg+","+username);	//向客户端反馈消息，加上分行符以便客户端接收
							bw.flush();

							System.out.println("消息已发送");
							
							//PrintWriter pw;
							//pw = new PrintWriter(ss.getOutputStream());
							//pw.println(msg+","+username);
							//pw.flush();
						}
						
					}
				}
				}
	}
			catch (IOException e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
	            Server.list.remove(fromSocket);
	            StringSocketMap.map.remove(username);

	            System.err.println(ip + " 已下线 , 当前在线人数为: " + Server.list.size() + " 人 !");
			}
	}
}
			



	
	
	
	

