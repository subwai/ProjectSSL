package Application;

public class Admin extends Person {
	private static final long serialVersionUID = -4999281412396348549L;

	public Admin(String name) {
		super(name);
	}

	@Override
	public boolean hasReadAccess(Record r) {
		return true;
	}

	@Override
	public boolean hasWriteAccess(Record r) {
		return true;
	}

	@Override
	public boolean hasDeleteAccess(Record r) {
		return true;
	}

}
