package Campbell_Work_Final;

public class Debugging {
	
	private final static boolean	DO_DEBUG = true;
	private final static boolean	DO_TRACE = false;
	
	public Debugging(){
		
	}
	
	/**
     * For debugging.  Use 'trace' for older debugging messages that you don't want to see.
     * 
     * Output is shown based on the M_DO_TRACE constant.
     */
    public void trace(String format, Object...args) {
    	if(DO_TRACE) {
    		System.out.print("TRACE: ");
    		System.out.println(String.format(format, args));
    	}
    }
    
    /**
     * For informational output.
     * @param format
     * @param args
     */
    public void info(String format, Object... args) {
    	System.out.print("INFO: ");
    	System.out.println(String.format(format, args));
    }
    
    /**
     * For error output.
     * @param format
     * @param args
     */
    public void error(String format, Object... args) {
    	System.out.print("ERROR: ");
    	System.out.println(String.format(format, args));
    }
    
    /**
     * For debugging output.  Output is controlled by the DO_DEBUG constant.
     * @param format
     * @param args
     */
    public void debug(String format, Object... args) {
    	if(DO_DEBUG) {
    		System.out.print("DEBUG: ");
    		System.out.println(String.format(format, args));
    	}
    }
}
