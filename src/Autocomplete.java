import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class Autocomplete implements DocumentListener {

  private static ArrayList<String> list = new ArrayList<String>(5);
  private static enum Mode {
    INSERT,
    COMPLETION
  };
  private JTextField textField;
  private Mode mode = Mode.INSERT;
  
  public static void clearData(){
	  list.clear();
	  PrintWriter pw = null;
		try {
		if(Commands.getWindowsOS())
				pw = new PrintWriter(new File("LoggerAssets\\autofill"));
		else 
   		  pw = new PrintWriter(new File("./LoggerAssets/autofill"));
		pw.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
  }
  
  public static void loadList() throws IOException{
	  Scanner s = null;
      try {
    	  if(Commands.getWindowsOS())
    		  s = new Scanner(new File("LoggerAssets\\autofill"));
    	  else 
    		  s = new Scanner(new File("./LoggerAssets/autofill"));

          for(int i = 0; i < 5; i++) {
        	  if(s.hasNextLine())
        		  list.add(s.nextLine());
        	  else{
        		  s.close();
        		  break;
        	  }
          }
      } finally {
          if (s != null) {
              s.close();
          }
      }
  }
  
  public static void saveList() throws IOException{
	  PrintWriter pw = null;
	  try {
    	  if(Commands.getWindowsOS())
    		  pw = new PrintWriter(new File("LoggerAssets\\autofill"));
    	  else 
    		  pw = new PrintWriter(new File("./LoggerAssets/autofill"));

          for(int i = 0; i < list.size() && i < 5; i++) {
        	  if(list.get(i) != null)
        		  pw.println(list.get(i));
        	  else{
        		  pw.close();
        		  break;
        	  }
          }
      } finally {
          if (pw != null) {
              pw.close();
          }
      }
  }
  
  public static void addToList(String s){
	  s = s.replaceAll(" ", "");
	  if(list.contains(s))
		  list.remove(s);
	  list.add(0,s);
	  if(list.size() > 5)
		  list.remove(5);
  }

  public Autocomplete(JTextField textField) {
    this.textField = textField;
  }

  @Override
  public void changedUpdate(DocumentEvent ev) { }

  @Override
  public void removeUpdate(DocumentEvent ev) { }

  @Override
  public void insertUpdate(DocumentEvent ev) {
    if (ev.getLength() != 1)
      return;

    int pos = ev.getOffset();
    String content = null;
    try {
      content = textField.getText(0, pos + 1);
    } catch (BadLocationException e) {
      e.printStackTrace();
    }

    // Find where the word starts
    int w;
    for (w = pos; w >= 0; w--) {
      if (!Character.isLetter(content.charAt(w)) && !Character.isDigit(content.charAt(w)) && content.charAt(w) != '.') {
        break;
      }
    }

    // Too few chars
    if (pos - w < 2)
      return;

    String prefix = content.substring(w + 1).toLowerCase();
    int n = Collections.binarySearch(list, prefix);
    if (n < 0 && -n <= list.size()) {
      String match = list.get(-n - 1);
      if (match.startsWith(prefix)) {
        // A completion is found
        String completion = match.substring(pos - w);
        // We cannot modify Document from within notification,
        // so we submit a task that does the change later
        SwingUtilities.invokeLater(new CompletionTask(completion, pos + 1));
      }
    } else {
      // Nothing found
      mode = Mode.INSERT;
    }
  }

  public class CommitAction extends AbstractAction {
    /**
     * 
     */
    private static final long serialVersionUID = 5794543109646743416L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      if (mode == Mode.COMPLETION) {
        int pos = textField.getSelectionEnd();
        StringBuffer sb = new StringBuffer(textField.getText());
        sb.insert(pos, " ");
        textField.setText(sb.toString());
        textField.setCaretPosition(pos + 1);
        mode = Mode.INSERT;
      } else {
        textField.replaceSelection("\t");
      }
    }
  }

  private class CompletionTask implements Runnable {
    private String completion;
    private int position;

    CompletionTask(String completion, int position) {
      this.completion = completion;
      this.position = position;
    }

    public void run() {
      StringBuffer sb = new StringBuffer(textField.getText());
      sb.insert(position, completion);
      textField.setText(sb.toString());
      textField.setCaretPosition(position + completion.length());
      textField.moveCaretPosition(position);
      mode = Mode.COMPLETION;
    }
  }


}