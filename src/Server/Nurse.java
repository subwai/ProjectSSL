package Server;

public class Nurse extends Person {
	private Division division;
	
	public Nurse(String name, Division division){
		super(name);
		this.division = division;
	}
	
	public Access access(Division div, String name){
		if(this.name.equals(name)){
			return Access.READWRITE;
		}else if (div == division){
			return Access.READ;
		}else{
			return Access.NOACCESS;
		}
	}
	
}
