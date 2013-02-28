package Server;

public class Nurse extends Person {
	private Division division;
	
	public Nurse(String name, Division division){
		super(name);
		this.division = division;
	}
	
	public Access access(String name, Division div){
		if(this.name.equals(name)){
			return Access.READWRITE;
		}else if (div == division){
			return Access.READ;
		}else{
			return Access.NOACCESS;
		}
	}

	public boolean hasReadAccess(Record r) {
		if (r.getNurse() == this) return true;
		if (r.getDivision() == division) return true;
		return false;
	}
	
}
