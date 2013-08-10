package alice.casestudies.wanderaround;

public interface ISensorEventListener {

    String getListenerName();

    void notifyEvent(String key, int value);
}
