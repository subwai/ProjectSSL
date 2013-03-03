package Application;

public class Doctor extends Person {
	private static final long serialVersionUID = -5056242144713848328L;
	private Division division;
	
	public Doctor(String name, Division division) {
		super(name);
		this.division = division;
	}

	@Override
	public boolean hasReadAccess(Record r) {
		if (r.getDoctor() == this) return true;
		if (r.getDivision() == division) return true;
		return false;
	}
}
