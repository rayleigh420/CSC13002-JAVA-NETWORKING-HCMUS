import java.io.*;
import java.net.*;

public class testClient 
{
	public static void main(String arg[])
	{
		try
		{
			Socket s = new Socket("localhost",3200);
			System.out.println(s.getPort());

			System.out.println("Talking to Server");

			Thread outThread = new Thread() {
				@Override
				public void run() {
					try {
						OutputStream os=s.getOutputStream();
						BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

						String sentMessage="";

						while (true) {
							DataInputStream din=new DataInputStream(System.in);
							sentMessage=din.readLine();
							bw.write(sentMessage);
							bw.newLine();
							bw.flush();

							if (sentMessage.equalsIgnoreCase("quit"))
								break;
							
						}
						bw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
					}
				};
			};
			outThread.start();

			Thread inThread = new Thread() {
				@Override
				public void run() {
					String receivedMessage;
					try {
						InputStream is=s.getInputStream();
						BufferedReader br=new BufferedReader(new InputStreamReader(is));
						while(true){
							receivedMessage=br.readLine();
							System.out.println("Received : " + receivedMessage);
						}
						// br.close();
					} catch (Exception e) {
	//					e.printStackTrace();
					} finally {
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
