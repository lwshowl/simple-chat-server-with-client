package chatinServer;

import java.sql.*;

import javax.swing.JOptionPane;

public class LoginVerify {
	
	
	
	public String username;
    public String password;
    
    LoginVerify(String username,String password)
    {
    	this.username=username;
    	this.password=password;
    }
    
    public String DBverify()
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
        result = statement.executeQuery("select password,nickname from chatuser where username="+"'"+username+"'");
        System.out.println("��½�˺�:"+this.username+","+"��½����:"+this.password);
        
       while (result.next())
            // ���������Ϊ��ʱ
       {
    	   if(result.getString("PASSWORD").toString().equals(this.password))
    	   {
    		  //System.out.println("��¼�ɹ�");
    		  return "true"+","+result.getString("nickname");
    		 }
    	   else
    	   {
    		   //System.out.println("�������");
    		   return "false";
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
    //System.out.println("��¼�ɹ�");
	return "false";
}
}

