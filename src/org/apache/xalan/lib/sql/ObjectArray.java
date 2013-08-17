package org.apache.xalan.lib.sql;

import java.util.Vector;

/**
 * Provide a simple Array storage mechinsim where  native Arrays will be use as
 * the basic storage mechinism but the Arrays will be stored as blocks.
 * The size of the Array blocks is determine during object construction.
 * This is intended to be a simple storage mechinsim where the storage only
 * can grow. Array elements can not be removed, only added to.
 */
public class ObjectArray
{
  /**
   */
  private int m_minArraySize = 10;
  /**
   * The container of all the sub arrays
   */
  private Vector m_Arrays = new Vector(200);

  /**
   * An index that porvides the Vector entry for the current Array that is
   * being appended to.
   */
  private _ObjectArray m_currentArray;


  /**
   * The next offset in the current Array to append a new object
   */
  private int m_nextSlot;


  /**
   */
  public ObjectArray( )
  {
    //
    // Default constructor will work with a minimal fixed size
    //
    init(10);
  }

  /**
   * @param minArraySize The size of the Arrays stored in the Vector
   */
  public ObjectArray( final int minArraySize )
  {
    init(minArraySize);
  }

  /**
   * @param size
   * @return
   */
  private void init( int size )
  {
    m_minArraySize = size;
    m_currentArray = new _ObjectArray(m_minArraySize);
  }

  /**
   * @param idx Index of the Object in the Array
   * @return
   */
  public Object getAt( final int idx )
  {
    int arrayIndx = idx / m_minArraySize;
    int arrayOffset = idx - (arrayIndx * m_minArraySize);

    //
    // If the array has been off loaded to the Vector Storage them
    // grab it from there.
    if (arrayIndx < m_Arrays.size())
    {
      _ObjectArray a = (_ObjectArray)m_Arrays.elementAt(arrayIndx);
      return a.objects[arrayOffset];
    }
    else
    {
      // We must be in the current array, so pull it from there

      // %REVIEW% We may want to check to see if arrayIndx is only
      // one freater that the m_Arrays.size(); This code is safe but
      // will repete if the index is greater than the array size.
      return m_currentArray.objects[arrayOffset];
    }
  }

  /**
   * @param idx Index of the Object in the Array
   * @param obj , The value to set in the Array
   * @return
   */
  public void setAt( final int idx, final Object obj )
  {
    int arrayIndx = idx / m_minArraySize;
    int arrayOffset = idx - (arrayIndx * m_minArraySize);

    //
    // If the array has been off loaded to the Vector Storage them
    // grab it from there.
    if (arrayIndx < m_Arrays.size())
    {
      _ObjectArray a = (_ObjectArray)m_Arrays.elementAt(arrayIndx);
      a.objects[arrayOffset] = obj;
    }
    else
    {
      // We must be in the current array, so pull it from there

      // %REVIEW% We may want to check to see if arrayIndx is only
      // one freater that the m_Arrays.size(); This code is safe but
      // will repete if the index is greater than the array size.
      m_currentArray.objects[arrayOffset] = obj;
    }
  }



  /**
   * @param o Object to be appended to the Array
   * @return
   */
  public int append( Object o )
  {
    if (m_nextSlot >= m_minArraySize)
    {
      m_Arrays.addElement(m_currentArray);
      m_nextSlot = 0;
      m_currentArray = new _ObjectArray(m_minArraySize);
    }

    m_currentArray.objects[m_nextSlot] = o;

    int pos = (m_Arrays.size() * m_minArraySize) + m_nextSlot;

    m_nextSlot++;

    return pos;
  }


  /**
   */
  class _ObjectArray
  {
    /**
     */
    public Object[] objects;
    /**
     * @param size
     */
    public _ObjectArray( int size )
    {
      objects = new Object[size];
    }
  }

  /**
   * @param args
   * @return
   */
  public static void main( String[] args )
  {
    String[] word={
      "Zero","One","Two","Three","Four","Five",
      "Six","Seven","Eight","Nine","Ten",
      "Eleven","Twelve","Thirteen","Fourteen","Fifteen",
      "Sixteen","Seventeen","Eighteen","Nineteen","Twenty",
      "Twenty-One","Twenty-Two","Twenty-Three","Twenty-Four",
      "Twenty-Five","Twenty-Six","Twenty-Seven","Twenty-Eight",
      "Twenty-Nine","Thirty","Thirty-One","Thirty-Two",
      "Thirty-Three","Thirty-Four","Thirty-Five","Thirty-Six",
      "Thirty-Seven","Thirty-Eight","Thirty-Nine"};

    ObjectArray m_ObjectArray = new ObjectArray();
    // Add them in, using the default block size
    for (int x =0; x< word.length; x++)
    {
      System.out.print(" - " + m_ObjectArray.append(word[x]));
    }

    System.out.println("\n");
    // Now let's read them out sequentally
    for (int x =0; x< word.length; x++)
    {
      String s = (String) m_ObjectArray.getAt(x);
      System.out.println(s);
    }

    // Some Random Access
    System.out.println((String) m_ObjectArray.getAt(5));
    System.out.println((String) m_ObjectArray.getAt(10));
    System.out.println((String) m_ObjectArray.getAt(20));
    System.out.println((String) m_ObjectArray.getAt(2));
    System.out.println((String) m_ObjectArray.getAt(15));
    System.out.println((String) m_ObjectArray.getAt(30));
    System.out.println((String) m_ObjectArray.getAt(6));
    System.out.println((String) m_ObjectArray.getAt(8));

    // Out of bounds
    System.out.println((String) m_ObjectArray.getAt(40));

  }
}
