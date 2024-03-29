package mini.java.syntaxtree;

import java.util.Vector;

public class VarDeclList {
	private Vector<VarDecl> list;

	public VarDeclList() {
		list = new Vector<VarDecl>();
	}

	public void addElement(VarDecl n) {
		list.addElement(n);
	}

	public VarDecl elementAt(int i) {
		return list.elementAt(i);
	}

	public int size() {
		return list.size();
	}
	
	public String toString() {
		if(list.isEmpty()) return "EMPTY";
		
		StringBuffer sb = new StringBuffer("\n");
		for(int i = 0; i < list.size(); i++) {
			sb.append(list.get(i).toString() + ";\n");
		}
		
		return sb.toString();
	}
}
