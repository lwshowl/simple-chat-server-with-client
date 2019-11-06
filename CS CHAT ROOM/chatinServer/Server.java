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
    	int port = 8083;   //�˿�
		int countpeople=0; //��������
        ServerSocket serverSocket=new ServerSocket(port);   //server socket
        System.out.println("��������������ȴ��ͻ�������.."+"�˿�:"+port);   
        
        while (true) {
            try {
            	
            	socket=serverSocket.accept();//���������ܵ����׽��ֵ�����,����һ��Socket����
            	countpeople ++;
            	String flag = "false";    //���صĵ�¼��֤��־
            	SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");  //ʱ��
            	ip = socket.getInetAddress().getHostAddress();  //       // �û�IP ��ַ
            	
            //��ȡ�û��ĵ�¼��Ϣ
            InputStream inputStream=socket.getInputStream();//�õ�һ�������������տͻ��˴��ݵ���Ϣ
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);//���Ч�ʣ����Լ��ֽ���תΪ�ַ���
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);//���뻺����
                /*****************��������**********************/
                String info = null;   //���ܵĻ���˺�������Ϣ
                String username="";      //������˺�
                String userpwd="";       //���������
                if((info=bufferedReader.readLine()) != null) {}
                System.out.println("�յ���Ϣ"+info);
                	
                    String[] account = info.split(",");//��","�������˺�����
                    
                    username=account[0];
                    try {
                    userpwd=account[1];
                    }
                    catch(ArrayIndexOutOfBoundsException e){
                    	e.printStackTrace();
                    	userpwd="null";
                    }
                    

                    /*****************��ʼ��¼��֤**********************/
                    System.err.println( ip + " �û����Ե�½, ʱ��:  "+df.format(new Date()));
                    LoginVerify verify = new LoginVerify(username,userpwd);  //������֤����
                    flag=verify.DBverify();
                    String[] buf = flag.split(",");  //��֤��Ϣ+������+��¼�˵��ǳ�
                    System.out.println(" ��֤��Ϣ:"+flag );
                    System.out.println("----------------------");
                    //*********************** ������֤��Ϣ***********************//
                    OutputStreamWriter outSW = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
					BufferedWriter bw = new BufferedWriter(outSW);
					bw.write(flag);	//��ͻ��˷�����Ϣ�����Ϸ��з��Ա�ͻ��˽���
					bw.flush();
                    
                          /*  PrintWriter pw;
                            pw = new PrintWriter(socket.getOutputStream());
                            //д����Ϣ
                            pw.println( flag);
                            pw.flush();*/
                    //***************************���������߳�********************//
                        if(buf[0].contentEquals("true"))
                        {
                        	M2MThread client= new M2MThread(socket,username);
                        	list.add(socket);
                        	StringSocketMap.map.put(username, socket);
                        	System.err.println( ip + " �û�:" + username + "�������˵�ǰ�����û�Ϊ: " + list.size() + "�� !" );
                        	client.start();
                        }
                    }
             catch (IOException e) 
                {
            	 System.err.println(socket.getInetAddress().getHostAddress()+ "��¼ʧ��");
            }
        }
                
    }
}