package alice.tucson.parsing;

import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;

/**
 * 
 * @author ste
 * 
 */
public class TupleCentreIdParser {

    private final String defPort;
    private final String input;
    private final String node;

    /**
     * 
     * @param in
     * @param port
     */
    public TupleCentreIdParser(final String in, final String n,
            final String port) {
        this.input = in;
        this.node = n;
        this.defPort = port;
    }

    /**
     * 
     * @return
     * @throws TucsonInvalidTupleCentreIdException
     */
    public TucsonTupleCentreId parse()
            throws TucsonInvalidTupleCentreIdException {
        String tcName = this.input.trim();
        String hostName = this.node;
        String portName = this.defPort;
        final int iAt = this.input.indexOf("@");
        final int iCol = this.input.indexOf(":");
        if (iAt == 0) {
            tcName = "default";
            if (iCol != -1) {
                hostName = this.input.substring(iAt + 1, iCol).trim();
                portName =
                        this.input.substring(iCol + 1, this.input.length())
                                .trim();
            } else {
                hostName =
                        this.input.substring(iAt + 1, this.input.length())
                                .trim();
            }
        } else if (iAt != -1) {
            tcName = this.input.substring(0, iAt).trim();
            if (iCol != -1) {
                hostName = this.input.substring(iAt + 1, iCol).trim();
                portName =
                        this.input.substring(iCol + 1, this.input.length())
                                .trim();
            } else {
                hostName =
                        this.input.substring(iAt + 1, this.input.length())
                                .trim();
            }
        } else {
            if (iCol == 0) {
                tcName = "default";
                portName =
                        this.input.substring(iCol + 1, this.input.length())
                                .trim();
            } else if (iCol != -1) {
                tcName = this.input.substring(0, iCol).trim();
                portName =
                        this.input.substring(iCol + 1, this.input.length())
                                .trim();
            }
        }
        return new TucsonTupleCentreId(tcName, hostName, portName);
    }

}
