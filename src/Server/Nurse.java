package Server;

public class Nurse extends Person {
	private Division division;
	
	public Nurse(String name, Division division){
		super(name);
		this.division = division;
	}
	
	public Access access(String name, Division div){
		if(this.name.equals(name)){
			return Access.READWRITE;
		}else if (div == division){
			return Access.READ;
		}else{
			return Access.NOACCESS;
		}
	}

	@Override
	protected Person myType() {
		return this;
	}
	
}
