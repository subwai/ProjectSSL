package Application;

public class Nurse extends Person {
	private Division division;
	
	public Nurse(String name, Division division) {
		super(name);
		this.division = division;
	}

	@Override
	public boolean hasReadAccess(Record r) {
		if (r.getNurse() == this) return true;
		if (r.getDivision() == division) return true;
		return false;
	}
	
}
