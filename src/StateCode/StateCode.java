/**
 * @author Zhuhan Qin, 988039
 */
package StateCode;

//import org.omg.CORBA.PUBLIC_MEMBER;

public class StateCode {
    // Command
    public final static int QUERY = 0;
    public final static int ADD = 1;
    public final static int REMOVE = 2;
    public final static int ADD_MEANING = 3;
    public final static int UPDATE = 4;


    //Command state
    public final static int SUCCESS = 5;
    public final static int FAIL = 6;
    public final static int MEANING_EXIST = 7;


    //Net Error state
    public final static int UNKNOWN_HOST = 403;
    public final static int COLLECTIONG_REFUSED = 403;
    public final static int IO_ERROR = 403;
    public final static int TIMEOUT = 400;
}
