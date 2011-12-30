package edu.cmu.smartcommunities.utilities;

import java.util.*;

public class Parser {
	/*****************************************************************
    Message string Parser utilities. 
    
         V1 - doesnt yet use Logger.
    
    Parses a Jess-like fact string (Jade ACL content) 
	string into an ArrayList.

    Provides simple matching functions
    
    To run this in standalone test mode: 
        cd to src, execute: java -cp . edu.cmu.smartcommunities.utilities.Parser

    Usage in other applications:
        import edu.cmu.smartcommunities.utilities.Parser
        ....
        
        Parser p=new Parser(<string to match>);
        boolean b=p.matches(<pattern>);
        String s=p.getToken(3); // get token #3 (0 base)
        <etc>
        
        
	*****************************************************************/


	 StringTokenizer tokens=null;
	 String a[];

	 public Parser(String s){
	    tokens=new StringTokenizer(s,"( )");
	    a = new String [tokens.countTokens()];
	    int i=0;
	    while ( tokens.hasMoreTokens() )
	     { a[i++]=tokens.nextToken();    // do we also want to "intern" ?
	     }
	  }
	 
	 public void printTokens() {
	    System.out.println("Parser: a=" + toString() + "\n");
	    for (int i=0;i<a.length;i++) {
	      System.out.println("Parser: a[" + i + "]=" + a[i] + "\n");
	    }
	 }

	 public String toString() {
	    String s="{ ";
	    for (int i=0;i<a.length;i++) {
	      s=s+ a[i] + " ";
	    }
	    return s + "}";
	 }

	 public String removeParens() {
	    String s="";
	    boolean first=true;
	    for (int i=0;i<a.length;i++) {
	      if(!first) s=s + " ";
	      s=s+ a[i];
	      first=false;
	    }
	    return s;
	 }

	 public String getArgString() {
	    String s="";
	    boolean first=true;
	    for (int i=0;i<a.length;i++) {
	      if(!first) s=s + " ";
	      s=s+ a[i];
	      first=false;
	    }
	    return s;
	 }

	 public String getRange(int i, int j) {
	    // from 0...length-1
	    if(i<0) i=0;
	    if(j>=a.length) j=a.length-1;
	    String s="";
	    boolean first=true;
	    for (int k=i;k<=j;k++) {
	      if(!first) s=s + " ";
	      s=s+ a[k];
	      first=false;
	    }
	    return s;
	 }

/* Extract tokens */
	 
	 public int countTokens() {
	   return a.length;
	 }

	 public String [] getTokens() {
	   return a;
	 }

	 public String getToken(int i) {
	  if (i <0) return "";
	  if (i>= a.length) return "";
	  return a[i];
	 }

	public String firstToken() {
	  return getToken(0);
	}

	public String secondToken() {
	  return getToken(1);
	}

	public String thirdToken() {
	  return getToken(2);
	}

	public String penultimateToken() {
	  return getToken(a.length-2);
	}

	public String lastToken() {
	  return getToken(a.length-1);
	}


	 public String nextToken(String def) {
	     if(tokens.hasMoreTokens()) return tokens.nextToken();
	      else return def;
	   } 

	 public int nextIntToken(int def) {
	     if(tokens.hasMoreTokens()) return Integer.valueOf(tokens.nextToken());
	      else return def;
	   }

	 public boolean isToken(int i, String s) {
	  return (getToken(i).equalsIgnoreCase(s));
	 }

	 public boolean matches(String s) {
	 // S is of form (Foo ? x ?) ? is a don't care, later extra parameter (see Rules and Ruleset in ruleagent)
	   Parser ps=new Parser(s);
	   if (countTokens() != ps.countTokens()) return false;
	   for (int i=0;i<countTokens(); i++) {
	    //System.out.println("i=" + i + ", p.tok=" + getToken(i) + ", ps.tok=" + ps.getToken(i) + "\n");
	    if(ps.isToken(i,"?")) continue;
	    if(isToken(i,ps.getToken(i))) continue;
	    return false;
	   }
	   return true;
	 }

	 public void test(String pattern, boolean expect) {
	   System.out.println("---- tokens =" + toString() +", pattern=" + pattern + "\n");
	   System.out.println("---- expect =" + expect + ", got " + matches(pattern) + "\n");
	 }

	public static void main(String args[]){
	 System.out.println("hi\n");
	 Parser p= new Parser("(Tivo  Tivo1   pause 3 on 4 off A1 16)");
	 p.printTokens();
	 p= new Parser("( Foo bar fum [] [3] )");
	 p.printTokens();
	 p= new Parser("( Person name:martin age:62 preferences:[a,b,c])");
	 p.printTokens();
	 System.out.println("p.isToken(0,\"Person\")=" + p.isToken(0,"Person") + "\n");
	 System.out.println("p.isToken(0,\"person\")=" + p.isToken(0,"person") + "\n");
	 System.out.println("p.isToken(0,\"fish\")=" + p.isToken(0,"fish") + "\n");
	 p=new Parser("( A B C D )");
	 System.out.println("p=" + p.toString() + " removeParens=" + p.removeParens() + "\n");
	 p.test("(A)", false);
	 p.test("( A B C D E )", false);
	 p.test("( A B C D )", true);
	 p.test("( A B x D )", false);
	 p.test("( A ? B ? )", false);
	 p.test("( A ? C ? )", true);
	 System.exit(0);
	}


}
