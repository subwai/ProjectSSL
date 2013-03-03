package Application;

import java.io.Serializable;

public abstract class Person implements Serializable{
	private static final long serialVersionUID = -5403624499257604725L;
	protected String name;

	public Person(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract boolean hasReadAccess(Record r);
	public abstract boolean hasWriteAccess(Record r);
	public abstract boolean hasDeleteAccess(Record r);
}