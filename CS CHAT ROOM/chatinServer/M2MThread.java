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
	private Socket fromSocket;    //��¼�˵�socket
	private Socket toSocket;      //����Ŀ��SOCKET
	private String ip;            //������ip
	private String username;      //�����˵��˺�
	
	public M2MThread (Socket from,String username)
	{
		fromSocket = from;
		this.username=username;
	}
	
	public void run()
	{
		ip = fromSocket.getInetAddress().getHostAddress();  //��ȡ�ͻ��� ������
		
		//try
		//{
			//��ȡ��Ϣ
			BufferedReader reader;
  try {
		reader = new BufferedReader(new InputStreamReader(fromSocket.getInputStream(),"UTF-8"));
			
			
			//һֱ�ȴ���ȡ
			while(true)
			{
				
				//��������
				String info = null;
                String toUser;       //����ķ�������Ϣ
                String typeofmsg;
				if((info=reader.readLine())!=null) {}
				System.out.println("�յ���Ϣ:"+"����:"+ ip +" "+ info);
				
				/*********************������Ϣ*********************/
			      String[] msgkind = info.split(",");//��","������Ϣ�����Ͷ���
			      typeofmsg = msgkind[0];
			      
			      
				/**************************�յ���������***********************/
				
				if(typeofmsg.contentEquals("RequestForBuddy"))
				{
					   Connection con = null;// ����һ�����ݿ�����
					    Statement statement = null;
					    ResultSet result = null;// ����2һ�����������
					    try
					    {
					        Class.forName("oracle.jdbc.driver.OracleDriver");// ����Oracle��������
					        String url = "jdbc:oracle:" + "thin:@localhost:1521:ORCL";// 127.0.0.1�Ǳ�����ַ��XE�Ǿ����Oracle��Ĭ�����ݿ���
					        String user = "$$$$$";// �û���,ϵͳĬ�ϵ��˻���
					        String password = "$$$$$";// �㰲װʱѡ���õ�����
					        con = DriverManager.getConnection(url, user, password);// ��ȡ����
					        System.out.println("���ݿ����ӳɹ���");
					        statement = con.createStatement(); 
					        result = statement.executeQuery("SELECT user2,nickname FROM chatuser,buddy where buddy.user2 = chatuser.username and user1="+"'"+username+"'");
					        System.out.println("��ִ�����ݿ���Һ��Ѳ���");
					        
					        ArrayList<String> buddylist = new ArrayList<String>();
					        ArrayList<String> nicknames = new ArrayList<String>();
					        String buddyinfo=new String();
					        String nickinfo= new String();
					       while (result.next())
					            // ���������Ϊ��ʱ
					       {
					    	  buddylist.add(result.getString("user2").toString());
					    	  nicknames.add(result.getString("nickname").toString());
					       }
					       
					       for(int i=0;i<buddylist.size();i++)
					       {
					    	   buddyinfo= buddyinfo + buddylist.get(i)+"("+nicknames.get(i)+")"+",";
					    	   
					       }
					       buddyinfo =buddylist.size()+","+buddyinfo;
					       System.out.println("������Ϣ�ѷ��� (����,�����б�):"+buddyinfo);
					   
					       
					       // �Ѻ��ѷ���ȥ
					   	for(Socket ss: Server.list )
					   	{
							if(ss==fromSocket) //���ظ�������
								{
								OutputStreamWriter outSW = new OutputStreamWriter(ss.getOutputStream(), "UTF-8");
								BufferedWriter bw = new BufferedWriter(outSW);
								bw.write(buddyinfo+","+username);	//��ͻ��˷�����Ϣ�����Ϸ��з��Ա�ͻ��˽���
								bw.flush();
									
								/*System.out.println("��Ϣ�ѷ���");
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
					            // ��һ������ļ�������رգ���Ϊ���رյĻ���Ӱ�����ܡ�����ռ����Դ
					            // ע��رյ�˳�����ʹ�õ����ȹر�
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
				
				/***************�յ������ļ�������Ϣ****************/
			
				else if(typeofmsg.contentEquals("File"))
			{
					
					String msg = new String();         
					String target = new String();
					

					try {
	                String[] account = info.split(",");//��","������Ϣ�����Ͷ���
	                msg=account[1];        
	                target = account[2];             
					}  
					catch(Exception e)
					{
						System.err.println("��Ϣ����ʧ��");
						e.printStackTrace();
					
					}
	  
	                
					toSocket =StringSocketMap.map.get(target);
					
					
						for(Socket ss: Server.list )
						{
							if(ss==toSocket)
							{
								OutputStreamWriter outSW = new OutputStreamWriter(ss.getOutputStream(), "UTF-8");
								BufferedWriter bw = new BufferedWriter(outSW);
								bw.write("File"+","+msg+","+username);	//��ͻ��˷�����Ϣ�����Ϸ��з��Ա�ͻ��˽���
								bw.flush();
								
								
								System.out.println("��Ϣ�ѷ���"+"File"+","+msg+","+username);
								
								//PrintWriter pw;

							}
							
						}
			}
				
		  /***********************���͵��� �����Ĵ����ļ�����*****************/
		   else if(typeofmsg.contentEquals("PortFile"))
					
				{  
			   
				     String msg = new String();         
				     String target = new String();
					try {
	                String[] account = info.split(",");//��","������Ϣ�����Ͷ���
	                 msg=account[1];        
	                 target = account[2];             
					}  
					catch(Exception e)
					{
						System.err.println("��Ϣ����ʧ��");
						e.printStackTrace();
					
					}
					
					ReciveFile recfilerunnable = new ReciveFile(username,target);   //���������ļ��߳�
					recfilerunnable.setss(8085);
					Thread recfile = new Thread(recfilerunnable);
					recfile.start();
					boolean isalive=true;
					while(isalive)
					{
						isalive = recfile.isAlive();
					}
					recfile.stop();
					System.out.println("���ܴ������");
					
					
					
						
				}
						
				
				
				/**********************ȷ�����ļ���Ϣ*/////////////////
				else if(typeofmsg.contentEquals("ACCFile"))
				{
					String msg = new String();
					String target = new String();
					
					try {
	                String[] account = info.split(",");//��","������Ϣ�����Ͷ���
	                msg=account[1];
	                target = account[2];
					}
					catch(Exception e)
					{
						System.err.println("��Ϣ����ʧ��");
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
					System.out.println("���ʹ������");

				}
						
				
				
				

				/**************�յ��Ǻ�������(�յ���Ϣ)****************/
				
				
				else if(typeofmsg.contentEquals("Msg"))
				{
				String msg = new String();
				String target = new String();
				
				try {
                String[] account = info.split(",");//��","������Ϣ�����Ͷ���
                msg=account[1];
                target = account[2];
				}
				catch(Exception e)
				{
					System.err.println("��Ϣ����ʧ��");
					e.printStackTrace();
				}
  
                
				toSocket =StringSocketMap.map.get(target);
				
				
					for(Socket ss: Server.list )
					{
						if(ss==toSocket)
						{
							OutputStreamWriter outSW = new OutputStreamWriter(ss.getOutputStream(), "UTF-8");
							BufferedWriter bw = new BufferedWriter(outSW);
							bw.write("Msg"+","+msg+","+username);	//��ͻ��˷�����Ϣ�����Ϸ��з��Ա�ͻ��˽���
							bw.flush();

							System.out.println("��Ϣ�ѷ���");
							
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

	            System.err.println(ip + " ������ , ��ǰ��������Ϊ: " + Server.list.size() + " �� !");
			}
	}
}
			



	
	
	
	

