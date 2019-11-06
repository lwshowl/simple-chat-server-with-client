package chatinServer;
import java.net.*;
import java.util.HashMap;

public class StringSocketMap {
	public static HashMap<String,Socket> map = new HashMap<String,Socket>();
	
	public static void AddtoMap(String name,Socket socket)
	{
		map.put(name, socket);
	}
	
	public static void DeletefromMap(String name,Socket socket)
	{
		map.remove(name, socket);
	}
}
