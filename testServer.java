import java.io.*;
import java.net.*;

public class testServer 
{
	public static void main(String arg[])
	{
		try
		{
			ServerSocket s = new ServerSocket(3200);

			System.out.println("Waiting for a Client");
			
			Socket ss=s.accept();
			System.out.println("Talking to client");
			System.out.println(ss.getPort());

			Thread outThread = new Thread() {
				@Override
				public void run() {
					try {
						OutputStream os=ss.getOutputStream();
						BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

						while (true) {
							DataInputStream din=new DataInputStream(System.in);
							String k=din.readLine();
							bw.write(k);
							bw.newLine();
							bw.flush();
						}
						//bw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				};
			};
			outThread.start();

			Thread inThread = new Thread() {
				@Override
				public void run() {
					try {
						InputStream is=ss.getInputStream();
						BufferedReader br=new BufferedReader(new InputStreamReader(is));
						while (true) {
							String receivedMessage;

							receivedMessage=br.readLine();
							System.out.println("Received : " + receivedMessage);
							if (receivedMessage.equalsIgnoreCase("quit"))
							{
								System.out.println("Client has left !");
								break;
							}
						}
						br.close();
					} catch (Exception e) {
	//					e.printStackTrace();
					} 
				};
			};
			inThread.start();
			
		}
		catch(IOException e)
		{
			System.out.println("There're some error");
		}
	}
}


