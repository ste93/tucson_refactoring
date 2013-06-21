package alice.tucson.parsing;

import alice.tucson.service.TucsonCmd;

/**
 * 
 * @author ste
 * 
 */
public class TucsonPrimitiveParser {

    private final String input;

    /**
     * 
     * @param in
     */
    public TucsonPrimitiveParser(final String in) {
        this.input = in;
    }

    /**
     * 
     * @return
     */
    public TucsonCmd parse() {
        String primitive = this.input.trim();
        String arg = "";
        final int iLeftBra = this.input.indexOf("(");
        final int iRightBra = this.input.lastIndexOf(")");
        if (iLeftBra != -1) {
            primitive = this.input.substring(0, iLeftBra).trim();
            if (iRightBra != -1) {
                arg = this.input.substring(iLeftBra + 1, iRightBra).trim();
                if ((primitive.equals("get") || primitive.equals("get_s"))
                        && !arg.equals("")) {
                    primitive = "";
                    arg = "";
                    return null;
                }
            } else {
                primitive = "";
                return null;
            }
        } else {
            if (!primitive.equals("quit") && !primitive.equals("exit")
                    && !primitive.equals("get") && !primitive.equals("get_s")
                    && !primitive.equals("help") && !primitive.equals("man")
                    && !primitive.equals("syntax") && !primitive.equals("o/")
                    && !primitive.equals("\\o") && !primitive.equals("hi")) {
                primitive = "";
                return null;
            }
        }
        return new TucsonCmd(primitive, arg);
    }

}
