import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;



public class Commands {
	static boolean windowsOS;
	static String IP;
	
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
	
	/** Easy to call from ping, determines which system calls to run **/
	public static class getLogs implements Runnable{
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
				CMDcall("LoggerAssets\\pscp -i LoggerAssets\\test_putty_private.ppk -scp root@" + IP 
						+ ":/var/log/messages ModuleLogs\\Data\\messages");
				for(int i = 0; i < 10; i++){
					CMDcall("LoggerAssets\\pscp -i LoggerAssets\\test_putty_private.ppk -scp root@" + IP + ":/var/log/messages."+ i 
							+" ModuleLogs\\Data\\messages"+ i );
					}
					CMDcall("ren ModuleLogs\\Data "+name);
				}
			
			/** Get the logs from the Module in a Mac/Linux environment. **/
			else{
				sysCall("mkdir -p ModuleLogs/Data");
				sysCall("scp -i testing_rsa root@"+ IP + ":/var/log/messages ModuleLogs/Data/messages");
				for(int i = 0; i < 10; i++){
					sysCall("scp -i testing_rsa root@"+ IP + ":/var/log/messages."+ i +" ModuleLogs/Data/messages" + i);
					}
				sysCall("mv ModuleLogs/Data ModuleLogs/"+name);
				}
			GUI.progressBar.setDone();
			//GUI.progressBar.setTerminating(true);
			ping.isRunning = false;
		}
	}

	
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
				output = sysCall("ping -c 3 -W .05 " + IP);//If the host is there return true.

			//If the host is there, get the logs in a new thread!
			if(output.contains("round-trip") || output.contains("Average")){
				Thread logs = new Thread(new getLogs());
				logs.start();
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
	
	/** Use this function to make system calls and recieve the output for validation **/
	private static String sysCall(String sysCom)
	{
		String call = null;
		String returnString = null;

		
	    try {
	    
	        // using the Runtime exec method:
	        Process p = Runtime.getRuntime().exec(sysCom);
	        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

	        // read the output from the command
	        while ((call = stdInput.readLine()) != null) {
	            returnString = call;
	        }
	        
	        // read any errors from the attempted command
	        while ((call = stdError.readLine()) != null) {
	            returnString = call;
	        }
	    }
	    catch (IOException e) {
	        System.out.println("exception happened - here's what I know: ");
	        e.printStackTrace();
	        System.exit(-1);
	    }
	    return returnString;
	    }
	
		 private static String CMDcall(String sysCom) 
		  {
		String output = null;
		 try {
		    List<String> command = new ArrayList<String>();
		    command.add("cmd");
		    command.add("/C");
		    command.add(sysCom);


		    ProcessBuilder builder = new ProcessBuilder(command);
		    Map<String, String> environ = builder.environment();

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return output;
		  }
		
	
	
}
