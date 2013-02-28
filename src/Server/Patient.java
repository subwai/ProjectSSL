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

	@Override
	protected Person myType() {
		return this;
	}

}
