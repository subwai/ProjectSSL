package Server;

public class Nurse extends Person {
	private Division division;
	
	public Nurse(String name, Division division){
		super(name);
		this.division = division;
	}

}
