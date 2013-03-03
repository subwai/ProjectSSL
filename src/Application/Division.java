package Application;

import java.io.Serializable;

public class Division implements Serializable{
	private static final long serialVersionUID = 6688084196354243618L;
	private String name;

	public Division(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
