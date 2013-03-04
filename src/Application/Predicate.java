package Application;

public abstract class Predicate<T> {
	public abstract boolean apply(T t, Person... args);
}
