package Server;

public abstract class Predicate<T> {
	protected Object[] args;

	public Predicate(Object[] args) {
		this.args = args;
	}

	public abstract boolean apply(T t);
}
