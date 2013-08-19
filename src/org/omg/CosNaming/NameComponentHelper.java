package org.omg.CosNaming;


/**
* org/omg/CosNaming/NameComponentHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from ../../../../src/share/classes/org/omg/CosNaming/nameservice.idl
* Friday, June 20, 2003 1:09:44 AM GMT-08:00
*/


/** 
   * Many of the operations defined on a naming context take names as
   * parameters. Names have structure. A name is an ordered sequence of 
   * components. <p>
   * 
   * A name with a single component is called a simple name; a name with
   * multiple components is called a compound name. Each component except 
   * the last is used to name a context; the last component denotes the 
   * bound object. <p>
   * 
   * A name component consists of two attributes: the identifier
   * attribute and the kind attribute. Both the identifier attribute and the 
   * kind attribute are represented as IDL strings. The kind attribute adds 
   * descriptive power to names in a syntax-independent way. Examples of the 
   * value of the kind attribute include c_source, object_code, executable, 
   * postscript, or " ". 
   */
abstract public class NameComponentHelper
{
  private static String  _id = "IDL:omg.org/CosNaming/NameComponent:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.CosNaming.NameComponent that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.CosNaming.NameComponent extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [2];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CosNaming.IstringHelper.id (), "Istring", _tcOf_members0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "id",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CosNaming.IstringHelper.id (), "Istring", _tcOf_members0);
          _members0[1] = new org.omg.CORBA.StructMember (
            "kind",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.omg.CosNaming.NameComponentHelper.id (), "NameComponent", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.omg.CosNaming.NameComponent read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.CosNaming.NameComponent value = new org.omg.CosNaming.NameComponent ();
    value.id = istream.read_string ();
    value.kind = istream.read_string ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.CosNaming.NameComponent value)
  {
    ostream.write_string (value.id);
    ostream.write_string (value.kind);
  }

}
