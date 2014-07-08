import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



public class Commands {
	
	/** Member Variables **/
	private static boolean windowsOS; //True means the OS is windows, false means Mac/Linux
	private static String IP;
	private static int option = 0;
	public static void setOption(int i){option = i;};
	public static int getOption(){return option;};
	public static boolean getWindowsOS(){return windowsOS;};
	 /** Set the IP for the Commands **/
	 public static void setIP(String ip){IP = ip;}
	
	/** Get the runtime OS to determine which commands to run **/
	public static void getOs(){
		String OS = System.getProperty("os.name");
		OS = OS.toLowerCase();
		if(OS.contains("windows"))
			windowsOS = true;
		else
			windowsOS = false;
		
		//Use this for testing in the multiple environments
		//GUI.createWarning(OS + "\n" + windowsOS);
		}
	
	/** Make the ping class Threadable **/
	public static class ping implements Runnable{
		public static boolean isRunning = false;
		/** Ping the IP and see if it is there. If it is return true.
	 	*  Also use the windowsOS boolean to switch commands **/
		@Override
		public void run(){
			isRunning = true;
			String output;
			if(windowsOS)
				output = CMDcall("ping -n 3 " + IP);//If the host is there return true.
			else
				output = MACcall("ping -c 3 -W .05 " + IP);//If the host is there return true.

			//If the host is there, get the logs in a new thread!
			if(output.contains("round-trip") || output.contains("Average")){
				try {
					Autocomplete.addToList(IP);
					Autocomplete.saveList();
				} catch (IOException e) {
					e.printStackTrace();
				}
				switch(option){
				case(0):
					Thread logs = new Thread(new getLogs());
					logs.start();
					break;
				case(1):
				case(2):
					Thread setServer = new Thread(new setToServer());
					setServer.start();
					break;
				}
			}
			//Else let the user know.
			else{
				GUI.progressBar.setTerminating(true);
				isRunning = false;
				String message = "<html><div width = \"200\"> That IP is not responding. "
						+ "If you need help, open the help menu.</div></html>";
				GUI.createWarning(message);
			}
			
		}
	}
	
	/** Easy to call from ping, determines which system calls to run **/
	private static class getLogs implements Runnable{
		public void run(){
			String name;
			if(GUI.dirName == null){
				DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
				Date date = new Date();
				name = dateFormat.format(date);
			}
			else
				name = GUI.dirName;
			
			/** Get the logs from the module in a Windows environment using pscp to do all the work **/
			if(windowsOS){
				CMDcall("mkdir ModuleLogs\\Data");
				CMDcall("echo y | LoggerAssets\\pscp -i LoggerAssets\\test_putty_private.ppk -scp root@" + IP 
						+ ":/var/log/messages ModuleLogs\\Data\\messages");
				for(int i = 0; i < 10; i++){
					CMDcall("LoggerAssets\\pscp -i LoggerAssets\\test_putty_private.ppk -scp root@" + IP + ":/var/log/messages."+ i 
							+" ModuleLogs\\Data\\messages"+ i );
					}
					CMDcall("ren ModuleLogs\\Data "+name);
				}
			
			/** Get the logs from the Module in a Mac/Linux environment. **/
			else{
				MACcall("chmod 600 ./LoggerAssets/testing_rsa");
				MACcall("mkdir -p ModuleLogs/Data");
				MACcall("scp -o stricthostkeychecking=no -i ./LoggerAssets/testing_rsa root@"+ IP 
						+ ":/var/log/messages ModuleLogs/Data/messages");
				for(int i = 0; i < 10; i++){
					MACcall("scp -o stricthostkeychecking=no -i ./LoggerAssets/testing_rsa root@"+ IP 
							+ ":/var/log/messages."+ i +" ModuleLogs/Data/messages" + i);
					}
				MACcall("mv ModuleLogs/Data ModuleLogs/"+name);
				}
			GUI.progressBar.setDone();
			ping.isRunning = false;
		}
	}
	
	/** Easy to call from ping, determines which system calls to run **/
	private static class setToServer implements Runnable{
		public void run(){
			/** Send the setServerFile to the module in a Windows environment using pscp to do all the work **/
			if(windowsOS){
				if(option == 1){
					CMDcall("echo y | LoggerAssets\\pscp -i LoggerAssets\\test_putty_private.ppk -scp LoggerAssets\\changeServerToTest "
						+ "root@" + IP + ":/icon/bin");
					/** Use the setServerFile to change the values in utconfig **/
					CMDcall("echo y | LoggerAssets\\plink -i LoggerAssets\\test_putty_private.ppk -ssh root@" + IP
							+ " \"cd icon/bin && cat changeServerToTest | ./utconfig && rm changeServerToTest\"");
				}
				else if(option == 2){
					CMDcall("echo y | LoggerAssets\\pscp -i LoggerAssets\\test_putty_private.ppk -scp LoggerAssets\\changeServerToLive "
							+ "root@" + IP + ":/icon/bin");
					/** Use the setServerFile to change the values in utconfig **/
					CMDcall("echo y | LoggerAssets\\plink -i LoggerAssets\\test_putty_private.ppk -ssh root@" + IP
							+ " \"cd icon/bin && cat changeServerToLive | ./utconfig && rm changeServerToLive\"");
				}
				}
			
			/** Send the setServerFile to the module in a Mac environment **/
			else{
				MACcall("chmod 600 ./LoggerAssets/testing_rsa");
				if(option == 1){
					MACcall("scp -o stricthostkeychecking=no -i ./LoggerAssets/testing_rsa ./LoggerAssets/changeServerToTest"
							+ " root@" + IP + ":/icon/bin");
					/** Use the setServerFile to change the values in utconfig **/
					MACcall("ssh -o stricthostkeychecking=no -i ./LoggerAssets/testing_rsa root@" + IP
							+ " \"cd icon/bin && cat changeServerToTest | ./utconfig && rm changeServerToTest\"");
				}
				else if(option == 2){
					MACcall("scp -o stricthostkeychecking=no -i ./LoggerAssets/testing_rsa ./LoggerAssets/changeServerToLive"
							+ " root@" + IP + ":/icon/bin");
					/** Use the setServerFile to change the values in utconfig **/
					MACcall("ssh -o stricthostkeychecking=no -i ./LoggerAssets/testing_rsa root@" + IP
							+ " \"cd icon/bin && cat changeServerToLive | ./utconfig && rm changeServerToLive\"");
				}
				}
			GUI.progressBar.setDone();
			ping.isRunning = false;
		}
	}
	
	/** Use this function to make system calls and recieve the output for validation **/
	private static String sysCall(String[] command)
	 {
		String output = null;
		 try {
		    ProcessBuilder builder = new ProcessBuilder(command);
		    Process process;
			process = builder.start();
		    InputStream is = process.getInputStream();
		    InputStreamReader isr = new InputStreamReader(is);
		    BufferedReader br = new BufferedReader(isr);
		    String line;
		    while ((line = br.readLine()) != null) {
		      output = line;
		    }
		  }catch (IOException e) {
				e.printStackTrace();
			}
		 return output;
		  }
	
		/** Use the ProcessBuilder to run Windows commands **/
		 private static String CMDcall(String sysCom) 
		  {
			String string = "cmd /C " + sysCom;
		    String[] s = string.split(" ");
		    return  sysCall(s);
		  }
		 
		/** Use the ProcessBuilder to run Mac commands **/
		 private static String MACcall(String sysCom) 
		  {
		    String[] s = {"/bin/sh", "-c",sysCom};
		    return  sysCall(s);
		  }
}
