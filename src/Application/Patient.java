package Application;

public class Patient extends Person{
	private static final long serialVersionUID = 908405158418824107L;
	public static int access = 1;
	
	public Patient (String name){
		super(name);
	}

	@Override
	public boolean hasReadAccess(Record r) {
		if (r.getPatient() == this) return true;
		return false;
	}

}
