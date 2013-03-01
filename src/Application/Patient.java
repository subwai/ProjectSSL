package Application;

public class Patient extends Person{
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
