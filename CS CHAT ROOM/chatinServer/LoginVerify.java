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
        result = statement.executeQuery("select password,nickname from chatuser where username="+"'"+username+"'");
        System.out.println("登陆账号:"+this.username+","+"登陆密码:"+this.password);
        
       while (result.next())
            // 当结果集不为空时
       {
    	   if(result.getString("PASSWORD").toString().equals(this.password))
    	   {
    		  //System.out.println("登录成功");
    		  return "true"+","+result.getString("nickname");
    		 }
    	   else
    	   {
    		   //System.out.println("密码错误");
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
    //System.out.println("登录成功");
	return "false";
}
}

