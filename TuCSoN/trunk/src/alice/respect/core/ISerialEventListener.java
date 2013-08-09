package alice.respect.core;

public interface ISerialEventListener {

    public String getListenerName();

    public void notifyEvent(String value);
}
