package alice.tucson.parsing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.service.TucsonCmd;

/**
 * 
 * @author ste
 * 
 */
public class TucsonOpParser {

    private TucsonPrimitiveParser cmdParser;
    private final String defPort;
    private final String input;
    private final String node;
    private TucsonCmd tcmd;
    private TucsonTupleCentreId tid;
    private TupleCentreIdParser tidParser;

    /**
     * 
     * @param in
     * @param port
     */
    public TucsonOpParser(final String in, final String n, final int port) {
        this.input = in;
        this.node = n;
        this.defPort = "" + port;
        this.tcmd = null;
        this.tid = null;
        this.cmdParser = null;
        this.tidParser = null;
    }

    public TucsonCmd getCmd() {
        return this.tcmd;
    }

    public TucsonTupleCentreId getTid() {
        return this.tid;
    }

    /**
     * 
     * @throws TucsonInvalidTupleCentreIdException
     */
    public void parse() throws TucsonInvalidTupleCentreIdException {
        String cmd = this.input.trim();
        String tc = "default";
        final int iOp = this.input.indexOf('?');
        final int iBra = this.input.indexOf('(');
        if (iOp != -1) {
            if (!TucsonOpParser.insideReaction(this.input.substring(0, iOp)
                    .trim())) {
                if ((iBra != -1) && (iBra < iOp)) {
                    cmd =
                            this.input.substring(iOp + 1, this.input.length())
                                    .trim();
                    tc = this.input.substring(0, iOp).trim();
                } else if ((iBra == -1) || (iOp < iBra)) {
                    cmd =
                            this.input.substring(iOp + 1, this.input.length())
                                    .trim();
                    tc = this.input.substring(0, iOp).trim();
                }
            }
        }
        this.cmdParser = new TucsonPrimitiveParser(cmd);
        this.tidParser = new TupleCentreIdParser(tc, this.node, this.defPort);
        this.tcmd = this.cmdParser.parse();
        this.tid = this.tidParser.parse();
    }

    private static boolean insideReaction(final String in) {
        final Pattern pattern =
                Pattern.compile("((out|in|rd|no|inp|rdp|nop|get|set)_s)+?");
        final Matcher matcher = pattern.matcher(in);
        return matcher.find();
    }

}
