import java.io.*;
import java.util.*;
import java.net.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class testClient implements Serializable
{
	public static Boolean flag = false;

	public static void main(String arg[]) throws IOException
	{
		try
		{
			folder f = new folder("Folder 1");
			System.out.println(f);
			Socket s = new Socket("localhost",3200);
			System.out.println(s.getPort());

			System.out.println("Talking to Server");

			folder[] listF = new folder[3];
			listF[0] = new folder("Folder 1");
			listF[1] = new folder("Folder 2");
			listF[2] = new folder("Folder 3");


			Thread outThread = new Thread() {
				@Override
				public void run() {
					try {
						OutputStream outputStream = s.getOutputStream();
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

						if (flag == false){
							List<Message> messages = new ArrayList<>();
							messages.add(new Message("list"));
							messages.add(new Message(Integer.toString(s.getPort())));
							for (int i = 0; i < listF.length; i++){
								messages.add(new Message(listF[i].getName()));
							}
							objectOutputStream.writeObject(messages);
							flag = true;
						}

						while(true){
							Scanner sc = new Scanner(System.in);

							String fd;
							System.out.println();
							System.out.println("Client has three folder: 1: Folder 1, 2: Folder 2, 3: Folder 3");
							System.out.print("Chose folder you want to do something with it: ");
							fd = sc.nextLine();

							String choice;
							System.out.println();
							System.out.println("0. Exit");
							System.out.println("1. Edit name folder");
							System.out.println("2. Add file");
							System.out.println("3. Delete file");
							System.out.print("Chose action you want to do with this file: ");
							choice = sc.nextLine();

							int index = Integer.parseInt(fd) - 1;

							if (choice.equals("1")){

								System.out.println();
								System.out.print("Enter new name of folder: ");
								String name = sc.nextLine();
								listF[index].editName(name);

								List<Message> messages = new ArrayList<>();
								messages.add(new Message(listF[index].getName()));
								messages.add(new Message(listF[index].getAction()));

								objectOutputStream.writeObject(messages);
								
							}
							else if (choice.equals("2")){
								listF[index].addFile();

								List<Message> messages = new ArrayList<>();
								messages.add(new Message(listF[index].getName()));
								messages.add(new Message(listF[index].getAction()));

								objectOutputStream.writeObject(messages);
							}
							else if (choice.equals("3")){
								listF[index].deleteFile();

								List<Message> messages = new ArrayList<>();
								messages.add(new Message(listF[index].getName()));
								messages.add(new Message(listF[index].getAction()));

								objectOutputStream.writeObject(messages);
							}
							else if (choice.equals("0")){
								List<Message> messages = new ArrayList<>();
								messages.add(new Message("Exit"));
								break;
							}
						}
						outputStream.close();
						objectOutputStream.close();
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

class folder{
	String name;
	String action;

	public folder(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}

	public String getAction(){
		return this.action;
	}

	public void editName(String name){
		this.name = name;
		this.action = "Edit Name Folder";
	}

	public void addFile(){
		this.action = "Add file";
	}

	public void deleteFile(){
		this.action = "Delete file";
	}

	public String toString(){
		return this.name;
	}
}
