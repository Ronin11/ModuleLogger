import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
 
public class GUI {
	
    /** Member Variables **/
	final static boolean shouldFill = true;
    final static boolean shouldWeightX = true;
    final static boolean RIGHT_TO_LEFT = false;
    static JFrame frame;
    static JProgressBar pBar;
    public static String dirName = null;
    //static boolean IPisValid = false;
 
    /** Create the pane with all the objects **/
    public static void addComponentsToPane(Container pane) {
        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }
 
        /** GUI Component Objects **/
        JLabel enterIP = new JLabel("Please enter the IP address of the module");
        final JTextField IP = new JTextField();
        JLabel enterDirName = new JLabel("<html><div width=\"300\">Please enter the name for the created directory. No spaces are allowed."
        		+ "<br>(If you leave it blank, the current date and time will be used.)</div></html>");
        final JTextField enterDir = new JTextField();
        JButton getLogs = new JButton("Get Logs!");
        pBar = new JProgressBar(0,1000);
        
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        if (shouldFill) {
        		//natural height, maximum width
        		c.fill = GridBagConstraints.HORIZONTAL;
        }
        //Component spacing
        c.insets = new Insets(10,10,10,10);
        
        //Place Components
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        pane.add(enterIP, c);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 2;
        pane.add(IP, c);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 3;
        pane.add(enterDirName, c);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 4;
        pane.add(enterDir, c);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 5;
        pane.add(getLogs, c);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 2;
        pane.add(pBar, c);
        
		pBar.setStringPainted(true);
        pBar.setVisible(false);
       
        /** Run the rest of the program **/
        getLogs.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		//Check and make sure the text box isn't empty
        		if(!IP.getText().isEmpty()){
        			
        			//If theres no input for the dirname, leave it null
        			if(!enterDir.getText().isEmpty())
        				dirName = enterDir.getText();
        			//Set the IP Variable in Commands.java
        			Commands.setIP(IP.getText());
        			
        			//Create the progress bar updating thread
            		Thread progressUpdate = new Thread(new progressBar());
            		progressUpdate.start();
            		
        			//Ping the IP address and see if it's up, to prevent 
        			//the program from trying to connect to a nonexistant host.
            		if(!Commands.ping.isRunning){
            			Thread ping = new Thread(new Commands.ping());
            			ping.start();
            		}
        		}else{
        			String message = "<html><div width = \"200\"> The IP address cannot be blank! </div></html>";
        			JOptionPane.showMessageDialog(frame,message);
        		}
        	} 	
        		
        });
    }
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("~Module Logger~");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(550,325));
        
        //Add Menu Bar Components
        JMenuBar menuBar = new JMenuBar();
        JMenu file;
        file = new JMenu("File");
        JMenuItem about, help;
        about = new JMenuItem("About");
        help = new JMenuItem("Help");
        file.add(about);
        menuBar.add(file);
        menuBar.add(help);
        frame.setJMenuBar(menuBar);       
        
        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        //menuBar component ActionListeners
        help.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//Create All of the Instruction Panels
				  JPanel p1 = new JPanel();
				  String address = "Finding IP Address:<br><br>"+
						    "While the module is plugged into a Module-Compatible Unit:<br>" +
						    "Push the \"Settings\" button, and push the up or down arrow until you come to an" +
						    " option that says \"Check WiFi Status\" Then hit enter/select. A test will run, " +
						    "after it's done, it will display information in the bottom of the screen, in this "+
						    "information, it will say \"IP Adress:\" followed by something like 192.168.1.21, four" +
						    " groups of numbers seperated by a period. This is the Module IP Address.<br><br>" +
						    "If the program still says \"The IP is not responding\" then make sure you are connected" +
						    " to the same network as the module.";
				  p1.add(new JLabel("<html><body><p style='width: 300px;'>"+ address + "</body></html>"));

				  JPanel p2 = new JPanel();
				  String network = "What Network is my Module Connected to?<br><br>"+
						    "Push the \"Settings\" button, and push the up or down arrow until you come to an" +
						    " option that says \"Check WiFi Status\" Then hit enter/select. A test will run, " +
						    "after it's done, it will display information in the bottom of the screen, in this "+
						    "information, it will display \"Router:\" followed by a name like ifitlive.<br><br>"
						    + "In the test lab there will be a limited"
						    + " number of networks. Here is a table of known networks:<br><br>"
						    + "eetestlab - 10.0.1.xxx<br>PlaidSpaceship - 192.168.1.xxx<br>"
						    + "FlyingSpaghettiMonster - 192.168.2.xxx<br>ifitlive - 192.168.7.xxx<br><br>"
						    + "If the prefix of the IP address matches one of these networks, then it is"
						    + " connected to that network. If the Module's IP Address does not look like "
						    + "one of the above, then it is probably connected to ifitlive.";
				  p2.add(new JLabel("<html><body><p style='width: 300px;'>"+ network + "</body></html>"));

				  JPanel p3 = new JPanel();
				  String Help = "Digital Exorcism:<br><br>"+
						    "If you read and followed the instructions on the previous two pages, and"
						    + " the program still won't work, then your computer is probably possessed"
						    + " by a demon. To fix this problem, you first must obtain Holy Water, a wooden"
						    + " stake, and petroleum oil blessed by a Pastafarian Rabbi. Then you must "
						    + "put all of the artifacts you gathered on the keyboard and "
						    + "chant the latin words 3 times:<br><br><i>"
						    + "Respice in me et rides!</i><br><br>"
						    + "If finding the objects seems to difficult, then try the chant and see what happens!"
						    + "<br><br><font size=\"1\">I am not liable for anyone's soul being lost in this process"
						    + "</font>";
				  p3.add(new JLabel("<html><body><p style='width: 300px;'>"+ Help + "</body></html>"));

				  //And add them to a tabbed pane to make it pretty
				  JTabbedPane tPane = new JTabbedPane();
				  tPane.addTab("Finding IP Address", p1);
				  tPane.addTab("What Network is my Module Connected to?",p2);
				  tPane.addTab("Digital Exorcism", p3);

				  JOptionPane.showConfirmDialog(null, tPane, "Help: How to ", JOptionPane.OK_CANCEL_OPTION);
			}
        });
        about.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = "<html><div width = \"300\"> <font size =\"14\">Version 1.0</font> <br><br>"
						+ "This awesome program is brought to you by the one "
						+ "and only Nate Ashby! <br><br>"
						+ "<font size=\"2\", font color=\"red\">We are the captains of our ships, "
						+ "masters of our destiny, and gods of the world around us.</font></div></html>";
				final ImageIcon icon = new ImageIcon("LoggerAssets/res/bender.png");
				
    			JOptionPane.showMessageDialog(frame,
    				    message,
    				    "I'm the greatest!",
    				    JOptionPane.WARNING_MESSAGE,
    				    icon);
			}
        });
    }
 
    /** The class to run the progress bar in it's own thread **/
    public static class progressBar implements Runnable{
    	static boolean isTerminating;
    	static int limit = 1;
		@Override
		public void run() {
			try{
				isTerminating = false;
				pBar.setVisible(true);
				pBar.setValue(0);
				while(!isTerminating && pBar.getValue() < 990){
					if(pBar.getValue() > 330 && pBar.getValue() <= 500)
						pBar.setString("You can do it!");
					else if(pBar.getValue() > 500 && pBar.getValue() <= 660)
						pBar.setString("Run Forrest run!");
					else if(pBar.getValue() > 660 && pBar.getValue() < 990)
						pBar.setString("I believe in you!");
					
					if(true){
						pBar.setValue(pBar.getValue()+1);
						Thread.sleep(75);
						} 
					}
				if(pBar.getValue() < 985)
					pBar.setVisible(false);
				}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//Terminate the Progress Bar thread
		public static void setTerminating(boolean b){isTerminating = b;}
		
		public static void increaseLimit(){limit++;
											pBar.setValue(limit*9);}
		public static void taskDone(){pBar.setValue(pBar.getValue()+9);}
		public static void resetLimit(){limit = 1;}
		
		//Finish the logs
		public static void setDone(){
			try {
				resetLimit();
				pBar.setString("Logs Recieved");
				pBar.setValue(1000);
				Thread.sleep(10000);
				pBar.setVisible(false);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    	
    }

    public static void setValue(int i){pBar.setValue(i);}
    public static void createWarning(String s){JOptionPane.showMessageDialog(frame,s);}
    
    public static void main(String[] args) {
    	
    	//Get the OS of the current environment
    	Commands.getOs();
    	
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}

