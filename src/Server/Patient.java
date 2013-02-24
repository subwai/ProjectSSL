package Server;
import java.util.*;

public class Patient extends Person{
	private ArrayList<Record> recordlist;
	
	public Patient (String name){
		super(name);
		recordlist = new ArrayList<Record>();
	}

}
