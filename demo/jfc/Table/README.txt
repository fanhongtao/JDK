The four examples in this directory: TableExample1, TableExample2, 
TableExample3 and TableExample4 show how to use some of the features 
of swing's JTable component: 

1. Using the JTable to show data from an SQL query input on the command line. 
2. As 1., but with a UI for configuring the database connection and query. 
3. A minmal example showing how to plug a generic sorter into the JTable. 
4. Some examples of using specialized renderers and editors. 

The second two demonstation programs: TableExample3 and TableExample4 
do not depend on database connectivity and can be compiled and run 
run in the normal way. 

The most interesting example is probably, TableExample2, which 
has a TextArea which can be used as an editor for an SQL expression. 
When the Fetch button is pressed the expression is sent to the database 
and the results are displayed in the JTable underneath it.

To run TableExample1 and TableExample2, you'll need to find a driver for 
your database and set the environment variable, JDBCHOME, to a directory 
where it is installed. See the following URL for a list of JDBC dirvers 
provided by a number of third party vendors.  

http://java.sun.com:80/products/jdbc/jdbc.drivers.html

Having done this you can run one of the database examples by 
specifying a classpath that includes the JDBC classes, 
the JDK classes and the example classes themselves.

For example, to run TableExample2:

On jdk1.2 on Solaris:
  java -classpath $(JDBCHOME):/usr/local/java/lib/classes.zip TableExample2

On jdk1.1.x on Solaris:
  setenv SWING_HOME <path to swing release>
  setenv JAVA_HOME <path to jdk1.1.x release>
  setenv JDBCHOME <path to JDBC driver>
  runnit 


On jdk1.1.x on win32:
  set CLASSPATH=<path to jdk1.1.x release>\lib\classes.zip
  set SWING_HOME=<path to swing release>
  set JDBCHOME=<path to JDBC driver>
  runnit

