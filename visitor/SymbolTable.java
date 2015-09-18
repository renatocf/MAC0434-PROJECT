package visitor;

import minijava.node.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

public class SymbolTable {
   private Hashtable hashtable;

   public SymbolTable() {
	    hashtable = new Hashtable();
    }

    public boolean addClass(String id, String parent) {
      id = id.replaceAll("\\s+","");
      if (parent != null)
        parent = parent.replaceAll("\\s+","");
	    if(containsClass(id))
                    return false;
	    else
		hashtable.put(id, new Class(id, parent));
	    return true;
    }

    public Class getClass(String id) {
      id = id.replaceAll("\\s+","");
	    if(containsClass(id))
		return (Class)hashtable.get(id);
	    else
		return null;
    }

    public boolean containsClass(String id) {
      id = id.replaceAll("\\s+","");
	    return hashtable.containsKey(id);
    }



    public PType getVarType(Method m, Class c, String id) {
      id = id.replaceAll("\\s+","");
      if(m != null) {
	  if(m.getVar(id) != null) {
	      return m.getVar(id).type();
	  }
	  if(m.getParam(id) != null){
	     return m.getParam(id).type();
	  }
      }

      while(c != null) {
	  if(c.getVar(id) != null) {
	      return c.getVar(id).type();
	  }
	  else {
	      if(c.parent() == null) {
		  c = null;
	      }
	      else {
		  c = getClass(c.parent());
	      }
	  }
      }


      System.out.println("Variable " + id
			 + " not defined in current scope");
      System.exit(0);
      return null;
  }

  public Method getMethod(String id, String classScope) {
    id = id.replaceAll("\\s+","");
    classScope = classScope.replaceAll("\\s+","");
	if(getClass(classScope)==null) {
	    System.out.println("Class " + classScope
			       + " not defined");
	    System.exit(0);
	}

	Class c = getClass(classScope);
	while(c != null) {
	    if(c.getMethod(id) != null) {
		return c.getMethod(id);
	    }
	    else {
		if(c.parent() == null) {
		    c = null;
		}
		else {
		    c = getClass(c.parent());
		}
	    }
	}


	System.out.println("Method " + id + " not defined in class " + classScope);

	System.exit(0);
	return null;
    }

    public PType getMethodType(String id, String classScope) {
      id = id.replaceAll("\\s+","");
      classScope = classScope.replaceAll("\\s+","");
	if(getClass(classScope)==null) {
	    System.out.println("Class " + classScope
			       + " not defined");
	    System.exit(0);
	}

	Class c = getClass(classScope);
	while(c != null) {
	    if(c.getMethod(id) != null) {
		return c.getMethod(id).type();
	    }
	    else {
		if(c.parent() == null) {
		    c = null;
		}
		else {
		    c = getClass(c.parent());
		}
	    }
	}

	System.out.println("Method " + id + " not defined in class " + classScope);
	System.exit(0);
	return null;
    }

    public boolean compareTypes(PType t1, PType t2) {
      if (t1 == null || t2 == null) return false;

      if (t1 instanceof AIntType && t2 instanceof  AIntType)
	    return true;
	if (t1 instanceof ABooleanType && t2 instanceof  ABooleanType)
	    return true;
	if (t1 instanceof AIntArrayType && t2 instanceof AIntArrayType)
	    return true;
	if (t1 instanceof AIdentifierType && t2 instanceof AIdentifierType){
	    AIdentifierType i1 = (AIdentifierType)t1;
	    AIdentifierType i2 = (AIdentifierType)t2;

	    Class c = getClass(i2.toString());
	    while(c != null) {
		if (i1.toString().replaceAll("\\s+","").equals(c.getId())) return true;
		else {
		    if(c.parent() == null) return false;
		    c = getClass(c.parent());
		}
	    }
	}
	return false;
    }

}//SymbolTable

class Class {

    String id;
    Hashtable methods;
    Hashtable globals;
    String parent;
    PType type;

    public Class(String id, String p) {
      id = id.replaceAll("\\s+","");
      if (p != null)
        p = p.replaceAll("\\s+","");
	this.id = id;
	parent = p;
	type = new AIdentifierType(new AIdentifier(new TId(id)));
	methods = new Hashtable();
	globals = new Hashtable();
    }

    public Class() {}

    public String getId(){ return id; }

    public PType type(){ return type; }

    public boolean addMethod(String id, PType type) {
      id = id.replaceAll("\\s+","");
	if(containsMethod(id))
	    return false;
	else {
	    methods.put(id, new Method(id, type));
	    return true;
	}
    }

    public Enumeration getMethods(){
	return methods.keys();
    }

    public Method getMethod(String id) {
      id = id.replaceAll("\\s+","");
	if(containsMethod(id))
	    return (Method)methods.get(id);
	else
	    return null;
    }

    public boolean addVar(String id, PType type) {
      id = id.replaceAll("\\s+","");
	if(globals.containsKey(id))
	    return false;
	else{
	    globals.put(id, new Variable(id, type));
	    return true;
	}
    }

    public Variable getVar(String id) {
      id = id.replaceAll("\\s+","");
	if(containsVar(id))
	    return (Variable)globals.get(id);
	else
	    return null;
    }

    public boolean containsVar(String id) {
      id = id.replaceAll("\\s+","");
	return globals.containsKey(id);
    }

    public boolean containsMethod(String id) {
      id = id.replaceAll("\\s+","");
	return methods.containsKey(id);
    }

    public String parent() {
	return parent;
    }
} // Class

class Variable {

    String id;
    PType type;

    public Variable(String id, PType type) {
      id = id.replaceAll("\\s+","");
	this.id = id;
	this.type = type;
    }

    public String id() { return id; }

    public PType type() { return type; }

} // Variable

class Method {

    String id;
    PType type;
    Vector params;
    Hashtable vars;

    public Method(String id, PType type) {
      id = id.replaceAll("\\s+","");
	this.id = id;
	this.type = type;
	vars = new Hashtable();
	params = new Vector();
    }

    public String getId() { return id; }

    public PType type() { return type; }


    public boolean addParam(String id, PType type) {
      id = id.replaceAll("\\s+","");
	if(containsParam(id))
	    return false;
	else {
	    params.addElement(new Variable(id, type));
	    return true;
	}
    }

    public Enumeration getParams(){
	return params.elements();
    }

    public Variable getParamAt(int i){
	if (i<params.size())
	    return (Variable)params.elementAt(i);
	else
	    return null;
    }

    public boolean addVar(String id, PType type) {
      id = id.replaceAll("\\s+","");
	if(vars.containsKey(id))
	    return false;
	else{
	    vars.put(id, new Variable(id, type));
	    return true;
	}
    }

    public boolean containsVar(String id) {
      id = id.replaceAll("\\s+","");
	return vars.containsKey(id);
    }

    public boolean containsParam(String id) {
      id = id.replaceAll("\\s+","");
	for (int i = 0; i< params.size(); i++)
	    if (((Variable)params.elementAt(i)).id.equals(id))
		return true;
	return false;
    }

    public Variable getVar(String id) {
      id = id.replaceAll("\\s+","");
	if(containsVar(id))
	    return (Variable)vars.get(id);
	else
	    return null;
    }

    public Variable getParam(String id) {
      id = id.replaceAll("\\s+","");

	for (int i = 0; i< params.size(); i++)
	    if (((Variable)params.elementAt(i)).id.equals(id))
		return (Variable)(params.elementAt(i));

	return null;
    }

} // Method


