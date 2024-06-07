
import java.util.StringTokenizer;
import java.nio.charset.Charset;
import java.io.*;
import java.util.Scanner;

import java.util.ArrayList;


import java.util.stream.Collectors;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import java.awt.*;
import java.applet.*;
import java.awt.event.*;


import javax.swing.*;


import javax.tools.*;
import java.util.*;

import java.util.stream.Stream;

class token{
   String t;
   int x0,y0;
   Color c;   boolean typeMenu=false;
   boolean v; 
   int clazz=0; 
   token(String s,int nl,boolean visible,int cl){t=s;n=nl;c=Color.black;v=visible; clazz=cl;}
   token(String s,int nl,Color c,boolean visible,int cl){t=s;n=nl;this.c=c;v=visible; clazz=cl;}
   public boolean equals(Object o){
      if(o instanceof String && ((String)o).equals(t) && (clazz==0 || clazz==4) )  return true;
      return false;
   }
   public String toString(){
      switch(clazz){
         case 1:		//stroka
            return '"'+t+'"';
         case 2:		//simvol
            return "'"+t+"'";
         default: 
            return t;
      }
   }
   int n; //line number
   int sn;//column number
   token addTypeMenu(){typeMenu=true;c=Color.red;return this;}
   Set<String> pkgs;  		//spisok paketov dl'a vybora, esli klassa net
   void setPkgs(Set<String> pkgs){
      this.pkgs=pkgs;
   }
   TEntry te=null;
}

class TEntry{		//tip ispol'zuemyj v programme
   ArrayList<token>ts=new ArrayList<>();
   void add(token t){
      ts.add(t);
      t.te=this;
   }
}

class SintAnException extends Exception{
   token t;
   SintAnException(token t){super();this.t=t;}
   public String toString(){
      return "(SintAnException line="+t.n+" col="+t.sn+" token="+t+")";
   }   
}


class link{
   int x,y,x1,y1;
   token t;
   link(token t,int x0,int y0,int w,int h){
      this.t=t; 
      this.x=x0;
      this.x1=x+w;
      this.y=y0;
      this.y1=y+h;
      //System.out.println("+LINK");
   }
}



class tCanvas extends JPanel implements MouseMotionListener, MouseListener{
   ShowApplet a;
   FontMetrics fm;
   tCanvas(ShowApplet a) {this.a=a;addMouseMotionListener(this);addMouseListener(this);}
   final Font f=new Font("Monospaced",0,12);
   int x,y,nl;

   boolean sizeSet=false;

   ArrayList<link> ls;
   link activeLink=null;
   public void paintComponent(Graphics g){
      int MAXX=0;
      ls=new ArrayList<>();
      super.paintComponent(g);
      x=10; y=10; nl=0;
      g.setFont(f);
      fm=g.getFontMetrics(); 
      int h=(int)(fm.getHeight()*1.0);
      for(var e:a.l) {
         for(token t:e){
            g.setColor(t.c);
            g.drawString(""+t,x,y);	if(t.typeMenu) ls.add(new link(t,x,y-fm.getHeight(),fm.stringWidth(""+t),fm.getHeight()) );
            x+=fm.stringWidth(""+t);       if(x>MAXX) MAXX=x;
            nl=t.n;
         }
         x=10; y+=h;
      }
      if(!sizeSet){setPreferredSize(new Dimension( MAXX,y+fm.getHeight()));sizeSet=true; repaint();}
   }
   @Override
   public void mouseDragged(MouseEvent e) {
      //System.out.println("Mouse dragged (" + e.getX() + ',' + e.getY() + ')');
   }

   @Override
   public void mouseMoved(MouseEvent e) {
      //System.out.println("Mouse moved (" + e.getX() + ',' + e.getY() + ')');
      link k=null;
      for(var m:ls) if(m.x<=e.getX() && m.y<=e.getY() && m.x1>e.getX() && m.y1>e.getY()){
         //System.out.println("!");
         k=m;
      }
      activeLink=k;
   }

   public void mouseClicked(MouseEvent ee){
      if(activeLink!=null){
         System.out.println(">ChoiseWin");
         JFrame f=new JFrame(new String("-".getBytes(),Charset.forName("Windows-1251")));
         f.setLayout(new BorderLayout());
         f.setSize(300,100);

         String[][]m=new String[activeLink.t.pkgs.size()][1];
         int i=0;
         for(String s:activeLink.t.pkgs){
            m[i++][0]=s;
         }

         JTable table1 = new JTable(m, new String[]{"choose:"});
         f.add(table1);

         table1.addMouseListener(new java.awt.event.MouseAdapter() {
             @Override
             public void mouseClicked(java.awt.event.MouseEvent evt) {
                 int row = table1.rowAtPoint(evt.getPoint());
                 int col = table1.columnAtPoint(evt.getPoint());
                 if (row >= 0 && col >= 0) {
                     System.out.println("Selected: "+table1.getValueAt(row, col)); //......
                     a.ie.insert_import( Arrays.asList((""+table1.getValueAt(row, col)).split("[.]")) ,"Reader");
                     f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
                     repaint();
                 }
             }
         });

         f.show();
      }
   }
   public void mousePressed(MouseEvent ee){}
   public void mouseReleased(MouseEvent ee){}
   public void mouseEntered(MouseEvent ee){}
   public void mouseExited(MouseEvent ee){}
}

class ShowApplet{  // extends Applet
  ide_edit ie;
  ArrayList<ArrayList<token>>l;
  ShowApplet(ArrayList<ArrayList<token>>l, ide_edit ie){
    this.l=l;  this.ie=ie;
  }
  public void init(){
    System.out.println("<applet init>");
    JFrame f=new JFrame(new String("Editor".getBytes(),Charset.forName("Windows-1251")));
    f.setLayout(new BorderLayout());
    f.setSize(600,400);
    JTextField d=new JTextField("");
    f.add("North",d);
    JPanel t=new tCanvas(this);
    t.setBackground(Color.white);
    ///f.add("Center",t);

    //Scrollbar hs = new Scrollbar(Scrollbar.HORIZONTAL, 50, width / 10, 0,100);
    //Scrollbar vs = new Scrollbar(Scrollbar.VERTICAL, 50, height / 2, 0,100);
    //f.add(hs);
    //f.add(vs);

    t.setPreferredSize(new Dimension( 600,400));
    JScrollPane scrollFrame = new JScrollPane(t);
    t.setAutoscrolls(true);
    //scrollFrame.setPreferredSize(new Dimension( 220,220));
    scrollFrame.setViewportView(t);
    f.add("Center",scrollFrame);

    JButton b=new JButton(new String("Close".getBytes(),Charset.forName("Windows-1251")));
    b.addActionListener(new BActionListener(f));
    f.add("South",b);
    f.show();
  }
}

class BActionListener implements ActionListener{  //button handler
	Frame f;
        BActionListener(Frame f){
          this.f=f;
        }
	public void  actionPerformed(ActionEvent e){
          f.dispose();
	}
}







class ide_edit{

   ArrayList<token>l=new ArrayList<>(); //tol'ko vidimye transl'atoru

   ArrayList<ArrayList<token>>l0=new ArrayList<>();  //vse leksemy

   ArrayList<token> e;

   int N=0;   //current token during sintax an.

   final Set<String> sw=Set.of("class","int","short","byte","static","protected","public","private","default","void","final","volatile","transient","if","while","switch","case","for","import","float","double","interface","extends","implements","goto","else","throws","return","do","try","catch","finally","throw","boolean","new","break","package","abstract");

   HashSet<TEntry> TEntries=new HashSet<>();
   HashSet<TEntry> ErrTEntries=new HashSet<>();

   HashSet<String>local_classes=new HashSet<>();

   int import_end,import_end_nl;    //konec direktiv import (dl'z avto-vstavki importov)

   void add(token t){
      e.add(t);
      if(t.v){
         l.add(t); 
      }
      //System.out.println("lexem=<"+t+'>');
   }

   void lex_anal(String fnm){

      int nl=0;  //line number
      try{
         boolean Comment=false;

         FileInputStream r=new FileInputStream(fnm);
         Scanner sc=new Scanner(r,Charset.forName("Windows-1251"));  
         while(sc.hasNext()){
            String ss=sc.nextLine();
            l0.add(e=new ArrayList<token>());
            String tk="";
            int j=0;
 
            if(Comment){   //prodolzhenie kommentaria /**/
               if(ss.contains("*/")){
                  //!!!!!!!!!!!!!!
                  int in=0;
                  for(int i=0;i<ss.length()-1;i++) if(ss.charAt(i)=='*' && ss.charAt(i+1)=='/'){
                     in=i;
                     break; 
                  }
                  add(new token(ss.substring(0,in+2),nl,Color.green,false,0)); j=in+2; Comment=false;
               }else{
                  add(new token(ss,nl,Color.green,false,0)); nl++;
                  continue;
               }
            }

            if(ss.length()==0) add(new token("",nl,false,0));
            else
            while(j<ss.length()){
               //-probely
               while(j<ss.length() && (ss.charAt(j)==' '||ss.charAt(j)=='\t')) {tk+=ss.charAt(j); j++;}
               if(tk!="")  {add(new token(tk,nl,false,0)); tk="";}
               //-slovo
               if(j<ss.length() && (ss.charAt(j)>='A' && ss.charAt(j)<='Z' || ss.charAt(j)>='a' && ss.charAt(j)<='z')){		//ID ili spec.word
                  while(j<ss.length() && (ss.charAt(j)>='A' && ss.charAt(j)<='Z' || ss.charAt(j)>='a' && ss.charAt(j)<='z' || ss.charAt(j)>='0' && ss.charAt(j)<='9' || ss.charAt(j)=='_')) {tk+=ss.charAt(j); j++;}
                  if(sw.contains(tk))
                     add(new token(tk,nl,Color.magenta,true,0)); 
                  else
                     add(new token(tk,nl,true,10)); 
                  tk="";
               }else if(j<ss.length() && (ss.charAt(j)=='"')){  		//stroka
                  j++;
                  while(j<ss.length() && (ss.charAt(j)!='"')) {if(ss.charAt(j)!='\\'){tk+=ss.charAt(j); j++;}else{tk+=ss.charAt(j); j++; tk+=ss.charAt(j); j++;}}
                  add(new token(tk,nl,Color.blue,true,1));
                  tk="";
                  j++;
               }else if(j<ss.length() && (ss.charAt(j)=='\'')){		//simvol
                  j++;
                  while(j<ss.length() && (ss.charAt(j)!='\'')) {if(ss.charAt(j)=='\\'){tk+=ss.charAt(j++); tk+=ss.charAt(j++);} else{tk+=ss.charAt(j); j++;}}
                  add(new token(tk,nl,true,2)); tk="";
                  j++;
               }else if(j<ss.length() && (ss.charAt(j)>='0' && ss.charAt(j)<='9')){  	//chislo
                  while(j<ss.length() && (ss.charAt(j)=='.' || ss.charAt(j)>='0' && ss.charAt(j)<='9')) {tk+=ss.charAt(j); j++;}
                  add(new token(tk,nl,true,3)); tk="";
               }else{  					     	//znak
                  if(j>=ss.length()) break;
                  if(ss.charAt(j)=='/' && j+1<ss.length() && ss.charAt(j+1)=='/'){add(new token(ss.substring(j,ss.length()),nl,Color.green,false,0)); tk=""; break;}
                  else
                  if(ss.charAt(j)=='/' && j+1<ss.length() && ss.charAt(j+1)=='*'){int in=j+2,m=-1; for(;in<ss.length()-1;in++) if(ss.charAt(in)=='*' && ss.charAt(in+1)=='/'){  m=in;break; }  if(m!=-1/*nashli kon kom*/){add(new token(ss.substring(j,m+2),nl,Color.green,false,0)); tk="";j=m+2; if(j>=ss.length())break;else continue;} else{  add(new token(ss.substring(j,ss.length()),nl,Color.green,false,0)); tk=""; Comment=true; break;}}

                  tk+=ss.charAt(j); j++;
                  if(j<ss.length() && (ss.charAt(j)=='=' && (tk.equals("*")||tk.equals("+")||tk.equals("-")||tk.equals("/")||tk.equals("=") ) ))  {
                     tk+=ss.charAt(j); j++;                     
                  }else if(j<ss.length() && (ss.charAt(j)=='+' && (tk.equals("+") )))  {
                     tk+=ss.charAt(j); j++;                     
                  }else if(j<ss.length() && (ss.charAt(j)=='-' && (tk.equals("-") )))  {
                     tk+=ss.charAt(j); j++;                     
                  }else if(j+1<ss.length() && (ss.charAt(j+1)=='.' && ss.charAt(j)=='.' && (tk.equals(".") )))  {    //...
                     tk="..."; j+=2;                     
                  }
                  add(new token(tk,nl,true,4)); tk="";
               }
            } 
            
            nl++;

         }

      }catch(Exception e){
         e.printStackTrace();
      }

      String lx= String.join(" ",  l.stream().map(t->""+t).collect(Collectors.toList())  );
      System.out.println(">>>"+ lx );
      try{
         FileWriter o=new FileWriter("LX.log",Charset.forName("Windows-1251"));
         o.write(lx);
         o.close();
      }catch(Exception e){}
   }

   final Set<String> basic_types=Set.of("void","short","byte","int","long","float","double","boolean","var","char");
   Set<String> typesSet=new HashSet<>();


   int readType(int n) throws Exception{
      while(true){               //type ids chain
         n++;
         if(l.get(n).equals("."))
            n++;
         else break; 
      };
      if(l.get(n).equals("<")){  //diamond
         n++; //<
         while(!l.get(n).equals(">")){
            if(l.get(n).equals("?")){
               n++; //?
               if(!l.get(n).equals(">") && !l.get(n).equals(",")){
                  if(l.get(n).equals("extends")) n++;
                  else if(l.get(n).equals("super")) n++;
                  else throw new Exception();
                  n=readType(n);
               }
            }else
               n=readType(n);
            if(l.get(n).equals(",")) n++;
         }
         n++; //>        
      }
      while(l.get(n).equals("[")){   //[][]
         n++;
         n++;
      }
      return n;
   }

   boolean isMethod() throws Exception{    //metod ili peremennaja chlen klassa
      int N1=N;
      while(l.get(N1).equals("public") || l.get(N1).equals("private") || l.get(N1).equals("protected") || l.get(N1).equals("static") || l.get(N1).equals("final") || l.get(N1).equals("volatile") || l.get(N1).equals("default")){
         N1++;  //modifiers
      }
      N1=readType(N1);
      N1++;  //name
      return l.get(N1).equals("(");
   }

   boolean isConstructor() throws Exception{    //
      int N1=N;
      while(l.get(N1).equals("public") || l.get(N1).equals("private") || l.get(N1).equals("protected") || l.get(N1).equals("static") || l.get(N1).equals("final") || l.get(N1).equals("volatile") || l.get(N1).equals("default")){
         N1++;  //modifiers
      }
      N1=readType(N1);
      //N1++;  //name
      return l.get(N1).equals("(");
   }

   boolean isClass() throws Exception{    //
      int N1=N;
      while(l.get(N1).equals("public") || l.get(N1).equals("private") || l.get(N1).equals("protected") || l.get(N1).equals("static") || l.get(N1).equals("final") || l.get(N1).equals("abstract") || l.get(N1).equals("default")){
         N1++;  //modifiers
      }
      if(l.get(N1).equals("class")||l.get(N1).equals("interface")) return true;
      else return false;
   }

   boolean isInterface() throws Exception{    //
      int N1=N;
      while(l.get(N1).equals("public") || l.get(N1).equals("private") || l.get(N1).equals("protected") || l.get(N1).equals("static") || l.get(N1).equals("final") || l.get(N1).equals("abstract") || l.get(N1).equals("default")){
         N1++;  //modifiers
      }
      if(l.get(N1).equals("interface")) return true;
      else return false;
   }


   boolean isStaticInitBlock() throws Exception{    //
      if(l.get(N).equals("static") && l.get(N+1).equals("{")) return true;
      else return false;
   }

   boolean isLocalVarDecl() throws Exception{   
      int N1=N;
      while(l.get(N1).equals("final") || l.get(N1).equals("volatile")){
         N1++;  //modifiers
      }
      N1=readType(N1);
      return l.get(N1).clazz==10;
   }

   void rule_var_member_decl() throws Exception{  //obyavlenie peremennoj chlena classa
      rule_modifiers();				System.out.println("--------- rule_var_member_decl: "+l.get(N));      //public static final
      rule_type();			
      do{
         rule_word(); //name
         while(l.get(N).equals("[")){
            rule_symbol("[");
            rule_symbol("]");
         }
         if(l.get(N).equals("=")){
            rule_symbol("="); 
            rule_expr(Set.of(",",";"));
         }
         if(l.get(N).equals(",")) rule_symbol(","); 
      }while(!l.get(N).equals(";"));
   }   

   void rule_var_decl() throws Exception{  //obyavlenie peremennoj v tele funkcii
      rule_var_modifiers();//public static final
      rule_type();
      do{
         rule_word(); //name
         while(l.get(N).equals("[")){
            rule_symbol("[");
            rule_symbol("]");
         }
         if(l.get(N).equals("=")){
            rule_symbol("="); 
            rule_expr(Set.of(",",";"));
         }
         if(l.get(N).equals(",")) rule_symbol(","); 
      }while(l.get(N).equals(","));
   }   


   void rule_symbol(String symbol) throws Exception{
      if(l.get(N).equals(symbol)) N++;
      else throw new SintAnException(l.get(N)); 
   }

   String rule_word(){
      return ""+l.get(N++); 
   }

   Map<String,HashSet<String>> packageClasses;    //rekomendacii klassov iz paketa
   String packagePath="";

   void rule_package() throws Exception{		 String b="", b0;
      if(l.get(N).equals("package")){                    int N0,N1;
         rule_symbol("package");                         N0=N;  b0=""+l.get(N);
         do{						 b+=l.get(N);
            rule_word(); //name
            if(l.get(N).equals(".")) {rule_symbol(".");  b+='/'; }
         }while(!l.get(N).equals(";"));                   N1=N;  getPackageClasses(packagePath=b,b0);
         rule_symbol(";");
      }
   }

   void rule_imports() throws Exception{
      while(l.get(N).equals("import"))
         rule_import();
   }

   void rule_classes() throws Exception{
      while(N<l.size())
         rule_decorated_class();
   }


   void rule_import() throws Exception{
      rule_symbol("import");			String b="";   String v;
      do{					b+=v=l.get(N).toString();
         rule_word();				if(l.get(N).equals(".")) b+=".";
         if(l.get(N).equals("."))
            rule_symbol(".");			
      }while(!l.get(N).equals(";"));		if(b.charAt(b.length()-1)=='*'){System.out.println("****");b=b.substring(0,b.length()-1); Set<String> allClasses = getClassNames(b); typesSet.addAll(allClasses); loadFSPackagePackageClassesList(b);}  else {System.out.println("****Single:"+b);typesSet.add(v);}
      rule_symbol(";");
   }

   void rule_class_or_interface() throws Exception{
      if(!isInterface()) rule_class();
      else rule_interface();
   }

   void rule_interface() throws Exception{
      rule_class_modifiers();
      rule_symbol("interface");					local_classes.add(l.get(N).toString()); System.out.println("--------- class: "+l.get(N));
      rule_word(); //name
      rule_class_parents();
      rule_symbol("{");
      while(!l.get(N).equals("}")){
         if(l.get(N).equals("@")){
            rule_decorator_list();
            if(l.get(N).equals("}")||l.get(N).equals(";")) throw new Exception();
         }
         if(isMethod()) rule_interface_method();   
         else if(isConstructor()) rule_interface_constructor();  
         else if(isClass()) rule_interface();
         else{
            rule_var_member_decl();
            rule_symbol(";"); 
         }      
      }
      rule_symbol("}");
   }

   void rule_class() throws Exception{
      rule_class_modifiers();
      rule_symbol("class");					local_classes.add(l.get(N).toString()); System.out.println("--------- class: "+l.get(N));
      rule_word(); //name
      rule_class_parents();
      rule_symbol("{");
      while(!l.get(N).equals("}")){
         if(l.get(N).equals("@")){
            rule_decorator_list();
            if(l.get(N).equals("}")||l.get(N).equals(";")) throw new Exception();
         }
         if(isMethod()) rule_method();   
         else if(isConstructor()) rule_constructor();  
         else if(isClass()) rule_class_or_interface();
         else if(isStaticInitBlock()) {   
            rule_symbol("static");
            rule_complex_operator();
         }else if(l.get(N).equals("{")) {   //non-static Init Block  
            rule_complex_operator();
         }else{
            rule_var_member_decl();
            rule_symbol(";"); 
         }      
      }
      rule_symbol("}");
   }

   void rule_class_parents() throws Exception{
      while(l.get(N).equals("extends") || l.get(N).equals("implements")){
         rule_class_parent();
      }
   }

   void rule_class_parent() throws Exception{
      if(l.get(N).equals("extends")) rule_symbol("extends");
      else rule_symbol("implements");
      do{
         rule_type();
         if(l.get(N).equals(",")) 
            rule_symbol(",");
         else break;
      }while(true);
   }


   void rule_method() throws Exception{				
      rule_modifiers();//public static final
      rule_type();					System.out.println("--------- method: "+l.get(N));
      rule_word(); //name
      rule_symbol("(");
      rule_pars(); 
      rule_symbol(")");
      rule_throws();
      rule_symbol("{");
      rule_operators();
      rule_symbol("}");
   }

   void rule_interface_method() throws Exception{				
      rule_modifiers();//public static final
      rule_type();					System.out.println("--------- method: "+l.get(N));
      rule_word(); //name
      rule_symbol("(");
      rule_pars(); 
      rule_symbol(")");
      rule_throws();
      rule_symbol(";");
   }


   void rule_constructor() throws Exception{
      rule_modifiers();					System.out.println("--------- construcror: "+l.get(N));//public static final
      rule_type();
      rule_symbol("(");
      rule_pars(); 
      rule_symbol(")");
      rule_throws();
      rule_symbol("{");
      rule_operators();
      rule_symbol("}");
   }

   void rule_interface_constructor() throws Exception{
      rule_modifiers();					System.out.println("--------- construcror: "+l.get(N));//public static final
      rule_type();
      rule_symbol("(");
      rule_pars(); 
      rule_symbol(")");
      rule_throws();
      rule_symbol(";");
   }

   void rule_modifiers() throws Exception{  //public static final
      while(l.get(N).equals("public") || l.get(N).equals("private") || l.get(N).equals("protected") || l.get(N).equals("static") || l.get(N).equals("final") || l.get(N).equals("volatile") || l.get(N).equals("default")){
         rule_word();
      }
   }

   void rule_class_modifiers() throws Exception{  //public static final
      while(l.get(N).equals("abstract") || l.get(N).equals("public") || l.get(N).equals("private") || l.get(N).equals("protected") || l.get(N).equals("static") || l.get(N).equals("final") || l.get(N).equals("volatile") || l.get(N).equals("default")){
         rule_word();
      }
   }

   void rule_var_modifiers() throws Exception{  //static final
      while(l.get(N).equals("final") || l.get(N).equals("volatile")){
         rule_word();
      }
   }

   void rule_type() throws Exception{  //name1.name2.name
      String b="";                                  TEntry te=new TEntry(); TEntries.add(te); int N0=N; 
      while(true){				    b+=l.get(N);  te.add(l.get(N));
         rule_word();
         if(l.get(N).equals(".")){     		    b+=".";
            rule_symbol(".");
         }else break; 
      };					    if(typesSet==null)System.out.println("(empty)");if(currDirClasses.contains(b) || typesSet!=null && typesSet.contains(b)) System.out.println("+++"+b); else {ErrTEntries.add(te); Set<String>s=null,s1=null; try{s=bcs.get(b);}catch(Exception ee2){} try{s1=packageClasses.get(b);if(s!=null)s.addAll(s1);else s=s1;}catch(Exception ee2){} for(int j=N0;j<N;j++){l.get(j).addTypeMenu();l.get(j).setPkgs(s);} System.out.println("---"+b); }
      if(l.get(N).equals("<")) rule_diamond();
      while(l.get(N).equals("[")){
         rule_symbol("[");
         rule_symbol("]");
      }
   }

   void rule_diamond() throws Exception{
      rule_symbol("<");
      while(!l.get(N).equals(">")){
         if(l.get(N).equals("?")){
            rule_symbol("?");
            if(!l.get(N).equals(">") && !l.get(N).equals(",")){
               if(l.get(N).equals("extends")) rule_symbol("extends");
               else if(l.get(N).equals("super")) rule_symbol("super");
               else throw new Exception();
               rule_type();
            }
         }else
            rule_type();
         if(l.get(N).equals(",")) rule_symbol(",");
      }
      rule_symbol(">");
   }

   void rule_pars() throws Exception{  //par,par2,.. parN
      //rule_word();//type
      while(!l.get(N).equals(")")){
         rule_decorated_par();
         if(l.get(N).equals(",")) rule_symbol(",");
      }
   }

   void rule_par() throws Exception{
      rule_var_modifiers();
      rule_type();
      if(l.get(N).equals("...")) rule_symbol("...");
      rule_word();
      while(l.get(N).equals("[")){
         rule_symbol("[");
         rule_symbol("]");
      }
   }

   void rule_throws() throws Exception{
      if(l.get(N).equals("throws")){
         rule_symbol("throws");
         do{ 
            rule_type();
            if(l.get(N).equals(",")) rule_symbol(",");
         }while(!l.get(N).equals("{"));
      }
   }


   void rule_operators() throws Exception{  //op1;op2;.. opN;
      while(!l.get(N).equals("}")){
         rule_operator();
         if(!l.get(N-1).equals("}") || l.get(N).equals(";")) {rule_symbol(";"); System.out.println("*;");}
      }
   }
   void rule_operator() throws Exception{  //op
							System.out.println("---- operator: "+l.get(N));
      if(l.get(N).equals("if")) rule_if();
      else if(l.get(N).equals("while")) rule_while();
      else if(l.get(N).equals("for")) rule_for();
      else if(l.get(N).equals("try")) rule_try();
      else if(l.get(N).equals("do")) rule_do_while();
      else if(l.get(N).equals(";")) ;//rule_symbol(";"); //empty_operator
      else if(l.get(N).equals("break")) rule_symbol("break");
      else if(l.get(N).equals("continue")) rule_symbol("continue");
      else if(l.get(N).equals("switch")) rule_switch();
      else if(l.get(N).equals("return")) {rule_symbol("return");rule_expr(";");}
      else if(l.get(N).equals("throw")) {rule_symbol("throw");rule_expr(";");}
      else if(l.get(N).equals("{")) {rule_complex_operator();}
      else{
        if(isLocalVarDecl()) {rule_var_member_decl();}
        else rule_expr(";");
      }      
   }

   void rule_complex_operator() throws Exception{
      if(l.get(N).equals("{")){
         rule_symbol("{");
         rule_operators();
         rule_symbol("}");
      }else rule_operator();
   }

   void rule_switch() throws Exception{ 
      rule_symbol("switch");
      rule_symbol("(");
      rule_expr(")");
      rule_symbol(")");
      rule_symbol("{");
      while(!l.get(N).equals("}")){
         if(l.get(N).equals("case")){
            rule_symbol("case");
            rule_expr(":");
            rule_symbol(":");
            while(!l.get(N).equals("}") && !l.get(N).equals("case") && !l.get(N).equals("default")){
               rule_operator();
               if(!l.get(N-1).equals("}")) rule_symbol(";");
            }
         }else if(l.get(N).equals("default")){
            rule_symbol("default");
            rule_symbol(":");
            while(!l.get(N).equals("}")){
               rule_operator();
               if(!l.get(N-1).equals("}")) rule_symbol(";");
            }
         }
      }
      rule_symbol("}");
   }

   void rule_if() throws Exception{ 
      rule_symbol("if");
      rule_symbol("(");
      rule_expr(")");
      rule_symbol(")");
      rule_complex_operator();      
      if(!l.get(N-1).equals("}")  &&  l.get(N+1).equals("else")) {rule_symbol(";"); }
      if(l.get(N).equals("else")) rule_else();
   }

   void rule_else() throws Exception{
      rule_symbol("else");
      rule_complex_operator();
   }

   void rule_while() throws Exception{ 
      rule_symbol("while");
      rule_symbol("(");
      rule_expr(")");
      rule_symbol(")");
      rule_complex_operator();
   }

   void rule_do_while() throws Exception{ 
      rule_symbol("do");
      rule_complex_operator();
      rule_symbol("while");
      rule_symbol("(");
      rule_expr(")");
      rule_symbol(")");
   }

   void rule_for() throws Exception{ 
      ////////////////////////////!!!!!!!!!!!!!!fix it
      rule_symbol("for");
      rule_symbol("(");
      if(l.get(N+1).equals("=")){          //obychnyj for
            rule_expr(";");
            rule_symbol(";");
            rule_expr(";");
            rule_symbol(";");
            rule_expr(Set.of(",",")"));
            rule_symbol(")");
      }else if(l.get(N).equals(";")){       //obychnyj for (s pustoj sekciej schetchika)
            rule_symbol(";");
            rule_expr(";");
            rule_symbol(";");
            rule_expr(Set.of(",",")"));
            rule_symbol(")");
      }else{
         rule_type();
         rule_word();
         if(l.get(N).equals(":")){		//for po kollekcii
            rule_expr(")");
            rule_symbol(")");
         }else{                            //obychnyj for
            rule_expr(";");
            rule_symbol(";");
            rule_expr(";");
            rule_symbol(";");
            rule_expr(Set.of(",",")"));
            rule_symbol(")");
         }
      }
      rule_complex_operator();
   }

   void rule_try() throws Exception{ 
      ////////////////////////////!!!!!!!!!!!!!!fix it  +try with resources
      rule_symbol("try");
      rule_complex_operator();
      while(l.get(N).equals("catch")){
         rule_symbol("catch");
         rule_symbol("(");
         //rule_type();
         //rule_word(); 
         rule_expr(")");			//catch(Exception1 | Exception2 e)
         rule_symbol(")");
         rule_complex_operator();
      }
      if(l.get(N).equals("finally")){
         rule_symbol("finally");
         rule_complex_operator();         
      }
   }

   void rule_decorator_list() throws Exception{
      while(l.get(N).equals("@")){
         rule_decorator();
      }
   }
   void rule_decorator() throws Exception{
      rule_symbol("@");				l.get(N-1).c=Color.yellow;  int V=N;
      rule_type();				if(l.get(V).c!=Color.red) for(int k=V;k<N;k++) l.get(k).c=Color.yellow;
      if(l.get(N).equals("(")){
         rule_symbol("(");
         while(!l.get(N).equals(")")){
            //rule_word(); //
            //rule_symbol("=");
            rule_expr(Set.of(",",")"));
            if(l.get(N).equals(",")) rule_symbol(",");
         }
         rule_symbol(")");
      } 
   }
   void rule_decorated_class() throws Exception{
      if(l.get(N).equals("@")) rule_decorator_list();
      rule_class_or_interface();
   }
   void rule_decorated_par() throws Exception{
      if(l.get(N).equals("@")) rule_decorator_list();
      rule_par();
   }

   void rule_expr(String c) throws Exception{  //s- odinochnyj token
					//System.out.println("---- expr: "+l.get(N));
      ////////////////////////////!!!!!!!!!!!!!!uproshennyj sposob
      while( !l.get(N).equals(c) ) {
         if(l.get(N).equals("(")){
            rule_symbol("(");
            rule_expr(")");
            rule_symbol(")");
         }else if(l.get(N).equals("[")){
            rule_symbol("[");
            rule_expr("]"); 
            rule_symbol("]");
         }else N++;
      }
   }

   void rule_expr(Set<String> s) throws Exception{
					//System.out.println("---- expr: "+l.get(N));
      ////////////////////////////!!!!!!!!!!!!!!uproshennyj sposob
      while( !s.contains( l.get(N).toString() ) ) {
         if(l.get(N).equals("(")){
            rule_symbol("(");
            rule_expr(")"); 
            rule_symbol(")");
         }else if(l.get(N).equals("[")){
            rule_symbol("[");
            rule_expr("]"); 
            rule_symbol("]");
         }else N++;
      }
   }

   void sint_anal() throws Exception{		currDirClasses=getDirClasses(filePath);  System.out.println(currDirClasses); packageClasses=new HashMap<>();
      rule_package(); 				//package ...
      rule_imports(); 				import_end=N; import_end_nl=l.get(N).n; //import ... (,)
      rule_classes(); 				//class ... (,)
      validateLocalClasses();
   }




   Set<String> getClassNames(String pn) throws Exception{
      Set<String>cs=new HashSet<>();
      try{
         java.util.List<Class> commands = new ArrayList<>();
         JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
         StandardJavaFileManager fileManager = compiler.getStandardFileManager(
              null, null, null);
         StandardLocation location = StandardLocation.PLATFORM_CLASS_PATH;//.CLASS_PATH;
         String packageName = pn;
         Set<JavaFileObject.Kind> kinds = new HashSet<>();
         kinds.add(JavaFileObject.Kind.CLASS);
         boolean recurse = false;
         Iterable<JavaFileObject> list = fileManager.list(location, packageName,
              kinds, recurse);
         for (JavaFileObject classFile : list) {
            String name = classFile.getName().replaceAll(".*/|[.]class.*","");
            ///commands.add(Class.forName(packageName + "." + name));
            //System.out.println(" * "+name);
            cs.add(name);
         }
         System.out.println("**"+pn);
      }catch(Exception e){e.printStackTrace();}

      return cs;
   }



   HashMap<String,HashSet<String>> bcs=new HashMap<>();    //vse pakety iz java.base:     paket -> {klassy}

   HashSet<String> javalang_classes=new HashSet<>(); 

   HashMap<String,HashSet<String>> buildClassesMap() throws Exception{

      HashMap<String,HashSet<String>> bcs=new HashMap<>();

      java.util.List<Class> commands = new ArrayList<>();
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      StandardJavaFileManager fileManager = compiler.getStandardFileManager(
           null, null, null);
      StandardLocation location = StandardLocation.PLATFORM_CLASS_PATH;//.CLASS_PATH;
      String packageName = "java";
      Set<JavaFileObject.Kind> kinds = new HashSet<>();
      kinds.add(JavaFileObject.Kind.CLASS);
      boolean recurse = true;  //false
      Iterable<JavaFileObject> list = fileManager.list(location, packageName,
           kinds, recurse);
      for (JavaFileObject classFile : list) {
         String name = classFile.getName();//.replaceAll(".*/|[.]class.*","");
         //System.out.println(name);
         java.util.List<String>l=Arrays.stream(name.split("/")).skip(3).collect(Collectors.toList());
         //System.out.println(l); 
         String pkg=String.join( ".",l.subList(0,l.size()-1) );
         //System.out.println(pkg+" "+l);
         //
         //System.out.println(Arrays.asList(l.get(l.size()-1).split("[.]")));
         String cls=l.get(l.size()-1).split("[.]")[0];
         //System.out.println(cls);
         if(pkg.indexOf("java.lang")==0){
            javalang_classes.add(cls);
         }else{
            if(!bcs.containsKey(cls)){
               bcs.put(cls,new HashSet<String>());
            }
            bcs.get(cls).add(pkg);
         }
      }
      typesSet.addAll(javalang_classes);
      return bcs;
   }


   void  getClassesMap(){
      try{
         bcs=buildClassesMap();
      }catch(Exception e){bcs=new HashMap<>();}
   }



   void insert_import(java.util.List<String> pkg,String clname){
      validateErrEntries(String.join(".",pkg));

      ArrayList<token> ll=new ArrayList<>();
      l0.add(import_end_nl, ll);
      token tt=new token("import",import_end_nl,Color.magenta,true,0);
      ll.add(tt);
      l.add(import_end++,tt);         
      //
      token t1=new token(" ",import_end_nl,false,0);
      ll.add(t1);
      l.add(import_end++,t1);         
      for(String lx:pkg){
         token t2=new token(lx,import_end_nl,true,10);  
         l.add(import_end++,t2);         
         ll.add(t2);

         token td=new token(".",import_end_nl,true,10);  
         l.add(import_end++,td);         
         ll.add(td);
      }
      token ta=new token("*",import_end_nl,Color.black,true,0);
      ll.add(ta);
      l.add(import_end++,ta);  
      token t3=new token(";",import_end_nl,Color.black,true,0);
      ll.add(t3);
      l.add(import_end++,t3);         
      //
      saveToFile();
   }

   void loadFSPackagePackageClassesList(String pkg){
         try{
            String pth=filePath.substring(0,filePath.length()-packagePath.length()-1) + _CH_ + String.join(""+_CH_,pkg.split("[.]"));
            System.out.println(pkg+" ::path of parent package==="+pth);
            Set<String>ss=getDirClasses(pth);
            typesSet.addAll(ss);
         }catch(Exception eee){}
   }
   void validateErrEntries(String pkg){
      try{
         //obnovl'aem mnozhestvo sistemnyh klasssov 
         Set<String> allClasses = getClassNames(pkg); 
         typesSet.addAll(allClasses);
         //obnovl'aem mnozhestvo klasssov iz roditel'skogo paketa
         try{
            String pth=filePath.substring(0,filePath.length()-packagePath.length()-1) + _CH_ + String.join(""+_CH_,pkg.split("[.]"));
            System.out.println(pkg+" ::path of parent package==="+pth);
            Set<String>ss=getDirClasses(pth);
            typesSet.addAll(ss);
         }catch(Exception eee){}

      for(var ete:ErrTEntries){
         String b = String.join( ".", ete.ts.stream().map(token::toString).collect(Collectors.toList()) );
         if(typesSet!=null && typesSet.contains(b)){
            for(token y:ete.ts){
               y.typeMenu=false;
               y.c=Color.BLACK;
            }
         }
      }

      }catch(Exception  e){}
   }

   void validateLocalClasses(){
      for(var ete:ErrTEntries){
         String b = String.join( ".", ete.ts.stream().map(token::toString).collect(Collectors.toList()) );
         if(typesSet!=null && local_classes.contains(b)){
            for(token y:ete.ts){
               y.typeMenu=false;
               y.c=Color.BLACK;
            }
         }
      }
   }

   String filePath;
   String fileName;
   String filename; //full name

   static char _CH_;

   ide_edit(String filePath,String fileName,String fullName){
      this.fileName=fileName;
      this.filePath=filePath;
      filename=fullName;
      typesSet.addAll(basic_types);
   }

   void saveToFile(){
      try{
         FileWriter o=new FileWriter(filename,Charset.forName("Windows-1251"));
         for(var e:l0) {
            for(token t:e){
               o.write(""+t); 
            }
            o.write("\n"); 
         }
         o.close(); 
      }catch(Exception e){}
   }

   Set<String> currDirClasses;

   Set<String> getDirClasses(String dir){
      try{

      File p = new File(dir);
      File[] l=p.listFiles();		//System.out.println(""+l);
      return Stream.of(l).map(f->f.getName())
                         .filter( n -> {
					try{
						return (n.indexOf(".java")!=-1) && n.indexOf(".java")==n.length()-5 || (n.indexOf(".class")!=-1) && n.indexOf(".class")==n.length()-6;
					}catch(Exception e){return false;} 
				 })
                         .map(n->{
                             int li=n.lastIndexOf('.');
                             return n.substring(0,li);
                          })
                         //.distinct()   //udal'aet dublikaty
                         .collect(Collectors.toSet());
      }catch(Exception e){/*e.printStackTrace();*/ return new HashSet<>();}
   }

   void getPackageClasses(String path,String first){
      try{

      System.out.println(filePath+"==="+path);
      String pth=filePath.substring(0,filePath.length()-path.length()-1) + _CH_ + first;
      System.out.println("PACKET_ROOT_PTH:"+pth);
      getPackageClasses_req(pth,first);
      System.out.println("packageClasses:"+packageClasses);

      }catch(Exception e){ }
   }

   void getPackageClasses_req(String pth1,String pkg){
      try{

      Set<String> t=getDirClasses(pth1);
      for(var i:t){
         if(!packageClasses.containsKey(i)){
            packageClasses.put(i,new HashSet<>());
         }
         packageClasses.get(i).add(pkg);
      }
      for(var j:new File(pth1).listFiles()) if(j.isDirectory()){
         getPackageClasses_req(pth1+_CH_+j.getName(),pkg+'.'+j.getName());
      }

      }catch(Exception e){ }
   }


   public static void main(String[]s){
      if(System.getProperty("os.name").indexOf("Win")!=-1)  _CH_='\\';
      else _CH_='/';

      String filePath;
      String fileName;
      String fullName;

      fullName=s[0];
      if(fullName.indexOf(_CH_)==-1){
         fileName=s[0];
         filePath=System.getProperty("user.dir");         
      }else{
         int li=s[0].lastIndexOf(_CH_);
         filePath=s[0].substring(0,li);
         fileName=s[0].substring(li+1);
      }
      fullName=filePath+_CH_+fileName;
      System.out.println(">>>>filePath:"+filePath + ">>>>fileName:"+fileName + ">>>>fullName:"+fullName);


      ide_edit p=new ide_edit(filePath,fileName,fullName);
      p.getClassesMap();

      System.out.println(">>> LEX_ANAL:");
      p.lex_anal(s[0]);

      System.out.println(">>> SINT_ANAL:");
      try{
         p.sint_anal();
      }catch(Exception e){ System.out.println(""+e); e.printStackTrace(); }

      new ShowApplet(p.l0,p).init();

   }
}
