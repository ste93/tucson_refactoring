package alice.tucson.api;

public class TucsonACCWrapper<A> {

	private A acc;
	
	public TucsonACCWrapper(A acc){
		this.acc = acc;
	}
	
	public A getACC(){
		return acc;
	}
	
}
