package alice.respect.api;

public class RespectOpId {

    private final long id;

    public RespectOpId(final long i) {
        this.id = i;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (this.id ^ (this.id >>> 32));
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RespectOpId)) {
            return false;
        }
        RespectOpId other = (RespectOpId) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    public long getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "" + this.id;
    }

}
