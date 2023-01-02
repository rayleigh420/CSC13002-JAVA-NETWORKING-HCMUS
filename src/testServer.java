import java.io.*;
import java.net.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class testServer extends JFrame
{
	private final String[] columns = {"Client", "Name Folder", "Change"};
	private final DefaultTableModel defaultTableModel = new DefaultTableModel(columns, 0);
	Map<String, String> mapPort = new HashMap<String, String>();
	Map<String, String> map = new HashMap<String, String>();
	int hostNumber = 0;
	JLabel[] label = {new JLabel("Host"), new JLabel("Host"), new JLabel("Host"), new JLabel("Host")};
	DefaultComboBoxModel[] host = {new DefaultComboBoxModel(), new DefaultComboBoxModel(), new DefaultComboBoxModel(), new DefaultComboBoxModel()};


	// public void addHost(String host, String[] folder){

	// 	JPanel panel = new JPanel();
	// 	topPanel.setLayout(new GridLayout(1,2));

	// 	JPanel nameHost = new JPanel();
	// 	JLabel name = new JLabel("Host");
	// 	nameHost.add(name);

	// 	JPanel chose = new JPanel();
	// 	for (int i = 0; i < folder.length; i++){
	// 		System.out.println(folder[i]);
	// 	}
	// 	JComboBox petList = new JComboBox(folder);
	// 	chose.add(petList);

	// 	panel.add(nameHost);
	// 	panel.add(chose);

	// 	topPanel.add(panel);
	// }

	public static void main(String arg[]){
		new testServer();
	}
	
	public testServer()
	{

		try
		{
			setTitle("Folder Watcher Socket");
			setSize(800, 600);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			JPanel topPanel = new JPanel();
			topPanel.setLayout(new GridLayout(1,2));
			for (int i = 0; i < 4; i++){
				JPanel panel = new JPanel();

				JPanel nameHost = new JPanel();
				JLabel name = label[i];
				nameHost.add(name);

				JPanel chose = new JPanel();
				
				JComboBox h = new JComboBox(host[i]);
				h.setActionCommand(Integer.toString(i));
				h.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JComboBox cb = (JComboBox)e.getSource();
        				String folder = (String)cb.getSelectedItem();
						String command = e.getActionCommand();
						map.put(command, folder);
					}
				});
				chose.add(h);

				panel.add(nameHost);
				panel.add(chose);
				topPanel.add(panel);
			};

			JTable table = new JTable(defaultTableModel);
			table.setEnabled(false);

			JPanel centerPanel = new JPanel();
			centerPanel.setLayout(new BorderLayout());
			centerPanel.add(new JScrollPane(table));

			add(centerPanel, BorderLayout.CENTER);	

			add(topPanel, BorderLayout.NORTH);

			setVisible(true);		

			ServerSocket s = new ServerSocket(3200);

			System.out.println("Waiting for a Client");
			
			while(true){

				Socket ss;
				if (hostNumber < 4){
					ss=s.accept();
					mapPort.put(Integer.toString(ss.getPort()), Integer.toString(hostNumber));
					hostNumber++;
					System.out.println(hostNumber);
				}
				else {
					break;
				}
				// System.out.println("Talking to client");
				// System.out.println(ss.getPort());

				Thread inThread = new Thread() {
					@Override
					public void run() {
						try {
							InputStream inputStream = ss.getInputStream();
							ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);		
							
							while(true){
								List<Message> listOfMessages = (List<Message>) objectInputStream.readObject();
								if (listOfMessages.get(0).getText().equals("list")){
									List<String> folder = new ArrayList<String>();
									listOfMessages.forEach((msg)-> folder.add(msg.getText()));

									String[] ls = folder.toArray(new String[0]);
									label[hostNumber - 1].setText(Integer.toString(ss.getPort()));
									for (int i = 2; i < ls.length; i++){
										host[hostNumber - 1].addElement(ls[i]);
									}
								}
								else {
									System.out.println("Received [" + listOfMessages.size() + "] messages from: " + ss);
									System.out.println("All messages:");
									listOfMessages.forEach((msg)-> System.out.println(msg.getText()));

									if (listOfMessages.get(0).equals("Exit")){
										break;
									}
									System.out.println("Test1");
									for (Map.Entry<String, String> entry : mapPort.entrySet()) {
										System.out.println(entry.getKey() + " " + entry.getValue());
									};
									System.out.println("Test2");
									for (Map.Entry<String, String> entry : map.entrySet()) {
										System.out.println(entry.getKey() + " " + entry.getValue());
									};
									String id = mapPort.get(Integer.toString(ss.getPort()));
									String folder = map.get(id);
									// System.out.println(id);
									// System.out.println(folder);
									// System.out.println(listOfMessages.get(0).getText());
									if (listOfMessages.get(0).getText().equals(folder)){
										defaultTableModel.insertRow(0, new Object[]{ss.getPort(), listOfMessages.get(0).getText(), listOfMessages.get(1).getText()});
									}
									
								}
							}

							inputStream.close();
							objectInputStream.close();

							ss.close();
						} catch (Exception e) {
		//					e.printStackTrace();
						} 
					};
				};
				inThread.start();
			}
			
		}
		catch(IOException e)
		{
			System.out.println("There're some error");
		}
	}
}


