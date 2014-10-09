package MAndEngine;

public class Pointer<E> {
	private volatile E object;
	public Pointer(E e){
		object = e;
	}
	
	public E getObject(){
		return object;
	}
}