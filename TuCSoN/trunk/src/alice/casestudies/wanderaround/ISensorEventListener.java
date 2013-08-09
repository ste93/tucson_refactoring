package alice.casestudies.wanderaround;

public interface ISensorEventListener {

    public String getListenerName();

    public void notifyEvent(String key, int value);
}
