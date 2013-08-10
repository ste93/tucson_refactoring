package alice.respect.core;

public interface ISerialEventListener {

    String getListenerName();

    void notifyEvent(String value);
}
