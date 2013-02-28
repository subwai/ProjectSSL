package Server;

public class Admin extends Person{
	
	public Admin(String name){
		super(name);
	}

	public Access access(){
		return Access.ADMIN;
	}

	@Override
	protected Person myType() {
		// TODO Auto-generated method stub
		return this;
	}
	
}
