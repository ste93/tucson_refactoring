package alice.tucson.service;

public class TucsonIdWrapper<I> {

    private final I id;

    public TucsonIdWrapper(final I i) {
        this.id = i;
    }

    public I getId() {
        return this.id;
    }

}
