package Server;

public class Nurse extends Person {
	private Division division;
	
	public Nurse(String name, Division division){
		super(name);
		this.division = division;
	}

	public boolean hasReadAccess(Record r) {
		if (r.getNurse() == this) return true;
		if (r.getDivision() == division) return true;
		return false;
	}
	
}
