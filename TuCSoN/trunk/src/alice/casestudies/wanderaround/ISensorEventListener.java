package alice.casestudies.wanderaround;

public interface ISensorEventListener {
	
	public void notifyEvent( String key, int value );
	
	public String getListenerName();
}
