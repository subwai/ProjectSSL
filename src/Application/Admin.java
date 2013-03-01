package Application;

public class Admin extends Person {

	public Admin(String name) {
		super(name);
	}

	@Override
	public boolean hasReadAccess(Record r) {
		return true;
	}

}
