package alice.tucson.examples.situatedness;

public interface ISensorEventListener {

    String getListenerName();

    void notifyEvent(String key, int value);
}
