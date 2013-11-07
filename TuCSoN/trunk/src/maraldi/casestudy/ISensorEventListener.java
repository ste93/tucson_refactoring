package maraldi.casestudy;

public interface ISensorEventListener {

    String getListenerName();

    void notifyEvent(String key, int value);
}
