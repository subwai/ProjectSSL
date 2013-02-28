package Server;
import java.util.*;

public class Patient extends Person{
	private ArrayList<Record> recordlist;
	public static int access = 1;
	
	public Patient (String name){
		super(name);
		recordlist = new ArrayList<Record>();
	}
	
	public Access access(String name){
		if(this.name.equals(name)){
			return Access.READ;
		}else{
			return Access.NOACCESS;
		}
	}

	public boolean hasReadAccess(Record r) {
		if (r.getPatient() == this) return true;
		return false;
	}

}
