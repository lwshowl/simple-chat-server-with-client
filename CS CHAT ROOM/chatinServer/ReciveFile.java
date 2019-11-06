package chatinServer;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 
 * @author Taowd
 * TODO  Socket通讯服务端
 * 2017年9月2日 下午12:04:37
 */
public class ReciveFile implements Runnable{
    /**
     * 接收文件存储路径
     */
    private static String path;
    /**
     * 端口号，可在配置文件中配置
     */
    private static int port;
    private String fromUser;
    private String toUser;
    
    private ServerSocket ss;
    
    ReciveFile(String fromUser,String toUser)
    {
    	this.fromUser=fromUser;
    	this.toUser=toUser;	
    }
    
    
    public void setss(int port)
    {
    	try {
			ss= new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    static {
        System.out.print("读取配置文件...");
        // 资源绑定

        String portStr = "8085";
        if (path == null || path.trim().equals("")) {
            path = "C:/Files/";
        } else if (!path.endsWith("/")) {
            path = path + "/";
        }
        try {
		    port = Integer.parseInt(portStr);
		} catch (Exception ex) {
		    System.out.println("端口获取失败，已变更为默认端口60000");
		    port = 60000;
		}
        System.out.println("读取完成:" + "接收端口" + port + ",接收路径" + path);
    }

    /**
     * 
     * @author Taowd
     * TODO 接收文件操作
     * 2017年9月2日 下午12:04:57
     */
    @SuppressWarnings("resource")
  
	@Override
	public void run() {
		// TODO Auto-generated method stub
    	    Socket toSocket = null;
	        Socket s = null;
	        BufferedInputStream in = null;
	        FileOutputStream out = null;

	        // 永不停歇地运行

	            // 注意：此处需要和客户端保持一致
	            byte[] buffer = new byte[1024];
	            String fileName = null; 
	            try {
	                s = ss.accept();
	                in = new BufferedInputStream(s.getInputStream());
	                // 取得文件名
	                in.read(buffer, 0, buffer.length);
	                fileName = new String(buffer).trim();
	                // 若为测试信息
	                if (fileName.equals("test message")) {
	                    System.out.println("接收到测试消息!");
	                }
	                // 定义文件流
	                String formatname =new SimpleDateFormat("yyyymmddhhmmss").format(new Date()) + fromUser+toUser+"##"+fileName;
	                out = new FileOutputStream(
	                        new File(path + formatname));
	                out.flush();
	                buffer = new byte[1024];
	                // 写文件内容
	                int count = 0;
	                while ((count = in.read(buffer, 0, buffer.length)) > 0) {
	                    out.write(buffer, 0, count);
	                    out.flush();
	                    buffer = new byte[1024];
	                }
	                System.out.println("接收到文件" + fileName);
	                
	            	toSocket =StringSocketMap.map.get(toUser);
					
							OutputStreamWriter outSW = new OutputStreamWriter(toSocket.getOutputStream(), "UTF-8");
							BufferedWriter bw = new BufferedWriter(outSW);
							bw.write("File"+","+formatname+","+fromUser);
							bw.flush();
							
							System.out.println("消息已发送"+"File"+","+new SimpleDateFormat("yyyymmddhhmmss").format(new Date()) + fromUser+toUser+fileName+","+fromUser);
							ss.close();
    	                
	            } catch (Exception ex) {
	                System.out.println("传输异常");

	                ex.printStackTrace();
	            } finally {
	                try {
	                    if (out != null) {
	                        out.close();
	                    }
	                    if (in != null) {
	                        in.close();
	                    }
	                    if (s != null) {
	                        s.close();
	                    }
	                } catch (Exception ex) {
	                    ex.printStackTrace();
	                    System.out.println("接收文件" + fileName + "失败" + ex);
	                }
	            }
	        
	    }
		
	
}
