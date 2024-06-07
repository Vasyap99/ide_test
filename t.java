
import java.util.*;

import java.io.*;

import java.net.URL;

import java.util.stream.Collectors;

import javax.tools.*;

class t{

   public static void main(String[]s) throws Exception{
      new t(); 
      ///Reflections reflections = new Reflections("java.io"); 
      //Set<Class<? extends Object>> allClasses = reflections.getSubTypesOf(Object.class); 
      //if(allClasses.contains(Class.forName("InputStreamReader"))) System.out.println("true");
   }

   t() throws Exception{
      //Set<Class> s= findAllClassesUsingClassLoader("java.io");
      //ClassLoader.getSystemClassLoader().findResources("java.io.*");
      //Set<URL>s=f1("java/io");
      //System.out.println(s.size());
      t2();
   }



    public Set<URL> f1(String packageName) {
        return ClassLoader.getSystemClassLoader()
          .resources(packageName)          
          .collect(Collectors.toSet());
    }


 public Set<Class> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
          .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
          .filter(line -> line.endsWith(".class"))
          .map(line -> getClass(line, packageName))
          .collect(Collectors.toSet());
    }
 
    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
              + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }






   void t1()throws Exception{


String packageName = "java.io";
ClassLoader classLoader = ClassLoader.getSystemClassLoader();
URL packageURL;

packageURL = classLoader.getResource(packageName);

if (packageURL != null) {
    String packagePath = packageURL.getPath();
    if (packagePath != null) {
        File packageDir = new File(packagePath);
        if (packageDir.isDirectory()) {
            File[] files = packageDir.listFiles();
            for (File file : files) {
                String className = file.getName();
                if (className.endsWith(".class")) {
                    className = packageName + "." + className.substring(0, className.length() - 6);
                    Class clazz = classLoader.loadClass(className);
                    // do something with the class
                    System.out.println(className);
                }
            }
        }
    }
}else System.out.println("err:null");

   }








void t2() throws Exception{

List<Class> commands = new ArrayList<>();
JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
StandardJavaFileManager fileManager = compiler.getStandardFileManager(
        null, null, null);
StandardLocation location = StandardLocation.PLATFORM_CLASS_PATH;//.CLASS_PATH;
String packageName = "java.io";
Set<JavaFileObject.Kind> kinds = new HashSet<>();
kinds.add(JavaFileObject.Kind.CLASS);
boolean recurse = false;
Iterable<JavaFileObject> list = fileManager.list(location, packageName,
        kinds, recurse);
for (JavaFileObject classFile : list) {
    String name = classFile.getName().replaceAll(".*/|[.]class.*","");
    commands.add(Class.forName(packageName + "." + name));
    System.out.println(name);
}

}







}