package Server;

public class Doctor extends Person {
	private Division division;
	
	
	public Doctor(String name, Division division){
		super(name);
		this.division = division;
	}

	public boolean hasReadAccess(Record r) {
		if (r.getDoctor() == this) return true;
		if (r.getDivision() == division) return true;
		return false;
	}
}
