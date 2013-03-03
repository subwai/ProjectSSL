package Application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class ListLogHandler extends Handler implements Serializable, Runnable{
	
	private static final long serialVersionUID = 3768934543061595961L;
	private transient ObjectStorage os;
	private ArrayList<String> auditLog;

	public ListLogHandler(ObjectStorage os){
		registerInstance(os);
		auditLog = new ArrayList<String>();
	}
	
	public void registerInstance(ObjectStorage os){
		this.os = os;
		setFormatter(new SimpleFormatter());
	}

	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {
	}

	@Override
	public void publish(LogRecord record) {
		auditLog.add(getFormatter().format(record));
		os.save(this);
	}
	
	public void read(int numRows){
		for(int i = auditLog.size()-numRows; i<auditLog.size(); i++){
			System.out.println(auditLog.get(i));
		}
	}

	@Override
	public void run() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Type 'logs x' to show the last x log entries or 'logs all' to show all");
		while(true){
			String command = sc.next();
			if(command.equals("logs")){
				String arg = sc.next();
				if(arg.equals("all")){
					read(auditLog.size());
				}else{
					try{
						int x = Integer.parseInt(arg);
						read(x);
					}catch(NumberFormatException ex){
						System.out.println("invalid argument '"+arg+"' given to logs (should be all or a number)");
					}
				}
			}
		}
	}

}
