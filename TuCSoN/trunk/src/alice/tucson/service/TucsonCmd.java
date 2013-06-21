package alice.tucson.service;

public class TucsonCmd {

    private final String arg;
    private final String primitive;

    public TucsonCmd(final String p, final String a) {
        this.primitive = p;
        this.arg = a;
    }

    public String getArg() {
        return this.arg;
    }

    public String getPrimitive() {
        return this.primitive;
    }

}
