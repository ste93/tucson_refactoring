package alice.respect.core;

public interface ISerialEventListener {
	
	public void notifyEvent( String value );
	
	public String getListenerName();
}
