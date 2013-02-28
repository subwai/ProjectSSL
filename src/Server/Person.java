package Server;

public abstract class Person{
	protected String name;
	
	public Person(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	protected abstract Person myType();
}
