package Server;

public class Admin extends Person{
	
	public Admin(String name){
		super(name);
	}
	
	public boolean hasReadAccess(Record r) {
		return true;
	}
	
}
