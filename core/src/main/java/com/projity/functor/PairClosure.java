package com.projity.functor;

import org.apache.commons.collections.Closure;


public class PairClosure implements Closure {

	private Closure first;
	private Closure second;

	public PairClosure(Closure first, Closure second) {
		this.first = first;
		this.second = second;
	}

	public void execute(Object arg0) {
		first.execute(arg0);
		second.execute(arg0);
	}

}
