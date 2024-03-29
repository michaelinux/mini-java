package mini.java.syntaxtree;

import mini.java.semantics.TypeVisitor;
import mini.java.semantics.Visitor;

public class ErrorType extends Type {
	public int errno;
	public String msg;
	
	// TODO error types to be defined!!!
//	public static final int NO_ERROR = 0;
	public static final int ERROR = 1;	// need to define in a more specific way
	
	public static final ErrorType NO_ERROR = new ErrorType(0, ""); 
//	public static final int
//		WHILE_STMT_NO_COND = 0,
//		IF_STMT_DIFF_TYPE = 1,
//		IF_STMT_NO_COND = 2,
//		PLUS_NO_INT = 3,
//		MINUS_NO_INT = 4,
//		TIMES_NO_INT = 5,
//		ARRAY_LOOKUP_NO_INT = 6,
//		ARRAY_LENGTH_NO_INT = 7;
	public ErrorType(int errno, String msg) {
		this.errno = errno;
		this.msg = msg;
	}
	
	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub

	}

	@Override
	public Type accept(TypeVisitor v) {
		// TODO Auto-generated method stub
		return null;
	}

}
