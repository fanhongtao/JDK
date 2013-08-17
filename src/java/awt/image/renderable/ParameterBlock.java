/*
 * @(#)ParameterBlock.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.awt.image.renderable;
import java.awt.image.RenderedImage;
import java.io.Serializable;
import java.util.Vector;

/**
 * A ParameterBlock encapsulates all the information about sources and
 * parameters (Objects) required by a RenderableImageOp, or other
 * classes that process images.
 *
 * <p> Although it is possible to place arbitrary objects in the
 * source Vector, users of this class may impose semantic constraints
 * such as requiring all sources to be RenderedImages or
 * RenderableImage.  ParameterBlock itself is merely a container and
 * performs no checking on source or parameter types.
 *
 * <p> All parameters in a ParameterBlock are objects; convenience
 * add and set methods are available that take arguments of base type and
 * construct the appropriate subclass of Number (such as
 * Integer or Float).  Corresponding get methods perform a
 * downward cast and have return values of base type; an exception
 * will be thrown if the stored values do not have the correct type.
 * There is no way to distinguish between the results of 
 * "short s; add(s)" and "add(new Short(s))".
 *
 * <p> Note that the get and set methods operate on references.
 * Therefore, one must be careful not to share references between
 * ParameterBlocks when this is inappropriate.  For example, to create
 * a new ParameterBlock that is equal to an old one except for an
 * added source, one might be tempted to write:
 *
 * <pre>
 * ParameterBlock addSource(ParameterBlock pb, RenderableImage im) {
 *     ParameterBlock pb1 = new ParameterBlock(pb.getSources());
 *     pb1.addSource(im);
 *     return pb1;
 * }
 * </pre>
 *
 * <p> This code will have the side effect of altering the original
 * ParameterBlock, since the getSources operation returned a reference
 * to its source Vector.  Both pb and pb1 share their source Vector,
 * and a change in either is visible to both.
 *
 * <p> A correct way to write the addSource function is to clone
 * the source Vector:
 *
 * <pre>
 * ParameterBlock addSource (ParameterBlock pb, RenderableImage im) {
 *     ParameterBlock pb1 = new ParameterBlock(pb.getSources().clone());
 *     pb1.addSource(im);
 *     return pb1;
 * }
 * </pre>
 *
 * <p> The clone method of ParameterBlock has been defined to
 * perform a clone of both the source and parameter Vectors for
 * this reason.  A standard, shallow clone is available as
 * shallowClone.
 *
 * <p> The addSource, setSource, add, and set methods are
 * defined to return 'this' after adding their argument.  This allows
 * use of syntax like:
 *
 * <pre>
 * ParameterBlock pb = new ParameterBlock();
 * op = new RenderableImageOp("operation", pb.add(arg1).add(arg2));
 * </pre>
 * */
public class ParameterBlock implements Cloneable, Serializable {
    /** A Vector of sources, stored as arbitrary Objects. */
    protected Vector sources = new Vector();
    
    /** A Vector of non-source parameters, stored as arbitrary Objects. */
    protected Vector parameters = new Vector();

    /** A dummy constructor. */
    public ParameterBlock() {}

    /** Constructs a ParameterBlock with a given Vector of sources. */
    public ParameterBlock(Vector sources) {
        setSources(sources);
    }
    
    /**
     * Constructs a ParameterBlock with a given Vector of sources and
     * Vector of parameters.
     */
    public ParameterBlock(Vector sources, Vector parameters) {
        setSources(sources);
        setParameters(parameters);
    }
    
    /**
     * Creates a shallow copy of a ParameterBlock.  The source and
     * parameter Vectors are copied by reference -- additions or
     * changes will be visible to both versions.
     *
     * @return an Object clone of the ParameterBlock.
     */
    public Object shallowClone() {
        try {
            return super.clone();
        } catch (Exception e) {
            // We can't be here since we implement Cloneable.
            return null;
        }
    }

    /**
     * Creates a copy of a ParameterBlock.  The source and parameter
     * Vectors are cloned, but the actual sources and parameters are
     * copied by reference.  This allows modifications to the order
     * and number of sources and parameters in the clone to be invisible
     * to the original ParameterBlock.  Changes to the shared sources or
     * parameters themselves will still be visible.
     *
     * @return an Object clone of the ParameterBlock.
     */
    public Object clone() {
        ParameterBlock theClone;

        try {
            theClone = (ParameterBlock) super.clone();
        } catch (Exception e) {
            // We can't be here since we implement Cloneable.
            return null;
        }

        if (sources != null) {
            theClone.setSources((Vector)sources.clone());
        }
        if (parameters != null) {
            theClone.setParameters((Vector)parameters.clone());
        }
        return (Object) theClone;
    }

    /**
     * Adds an image to end of the list of sources.  The image is
     * stored as an object in order to allow new node types in the
     * future.
     *
     * @param source an image object to be stored in the source list.
     */
    public ParameterBlock addSource(Object source) {
        sources.addElement(source);
        return this;
    }
    
    /**
     * Returns a source as a general Object.  The caller must cast it into
     * an appropriate type.
     *
     * @param index the index of the source to be returned.
     */
    public Object getSource(int index) {
        return sources.elementAt(index);
    }

    /**
     * Replaces an entry in the list of source with a new source.
     * If the index lies beyond the current source list,
     * the list is extended with nulls as needed.
     */
    public ParameterBlock setSource(Object source, int index) {
        int oldSize = sources.size();
        int newSize = index + 1;
        if (oldSize < newSize) {
            sources.setSize(newSize);
        }
        sources.setElementAt(source, index);
        return this;
    }
    
    /**
     * A convenience method to return a source as a RenderedImage.
     * An exception will be thrown if the sources is not a RenderedImage.
     *
     * @param index the index of the source to be returned.
     */
    public RenderedImage getRenderedSource(int index) {
        return (RenderedImage) sources.elementAt(index);
    }
    
    /**
     * A convenience method to return a source as a RenderableImage.
     * An exception will be thrown if the sources is not a RenderableImage.
     *
     * @param index the index of the source to be returned.
     */
    public RenderableImage getRenderableSource(int index) {
        return (RenderableImage) sources.elementAt(index);
    }

    /** Returns the number of source images. */
    public int getNumSources() {
        return sources.size();
    }
    
    /** Returns the entire Vector of sources. */
    public Vector getSources() {
        return sources;
    }
    
    /** Sets the entire Vector of sources to a given Vector. */
    public void setSources(Vector sources) {
        this.sources = sources;
    }
    
    /** Clears the list of source images. */
    public void removeSources() {
        sources = new Vector();
    }
    
    /** Returns the number of parameters (not including source images). */
    public int getNumParameters() {
        return parameters.size();
    }
    
    /** Returns the entire Vector of parameters. */
    public Vector getParameters() {
        return parameters;
    }
    
    /** Sets the entire Vector of parameters to a given Vector. */
    public void setParameters(Vector parameters) {
        this.parameters = parameters;
    }
    
    /** Clears the list of parameters. */
    public void removeParameters() {
        parameters = new Vector();
    }
    
    /** Adds an object to the list of parameters. */
    public ParameterBlock add(Object obj) {
        parameters.addElement(obj);
        return this;
    }

    /** Adds a Byte to the list of parameters. */
    public ParameterBlock add(byte b) {
        return add(new Byte(b));
    }
    
    /** Adds a Character to the list of parameters. */
    public ParameterBlock add(char c) {
        return add(new Character(c));
    }
    
    /** Adds a Short to the list of parameters. */
    public ParameterBlock add(short s) {
        return add(new Short(s));
    }
    
    /** Adds a Integer to the list of parameters. */
    public ParameterBlock add(int i) {
        return add(new Integer(i));
    }
    
    /** Adds a Long to the list of parameters. */
    public ParameterBlock add(long l) {
        return add(new Long(l));
    }

    /** Adds a Float to the list of parameters. */
    public ParameterBlock add(float f) {
        return add(new Float(f));
    }

    /** Adds a Double to the list of parameters. */
    public ParameterBlock add(double d) {
        return add(new Double(d));
    }

    /**
     * Replaces an Object in the list of parameters.
     * If the index lies beyond the current source list,
     * the list is extended with nulls as needed.
     */
    public ParameterBlock set(Object obj, int index) {
        int oldSize = parameters.size();
        int newSize = index + 1;
        if (oldSize < newSize) {
            parameters.setSize(newSize);
        }
        parameters.setElementAt(obj, index);
        return this;
    }

    /**
     * Replaces an Object in the list of parameters with a Byte.
     * If the index lies beyond the current source list,
     * the list is extended with nulls as needed.
     */
    public ParameterBlock set(byte b, int index) {
        return set(new Byte(b), index);
    }

    /**
     * Replaces an Object in the list of parameters with a Character.
     * If the index lies beyond the current source list,
     * the list is extended with nulls as needed.
     */
    public ParameterBlock set(char c, int index) {
        return set(new Character(c), index);
    }

    /**
     * Replaces an Object in the list of parameters with a Short.
     * If the index lies beyond the current source list,
     * the list is extended with nulls as needed.
     */
    public ParameterBlock set(short s, int index) {
        return set(new Short(s), index);
    }

    /**
     * Replaces an Object in the list of parameters with an Integer.
     * If the index lies beyond the current source list,
     * the list is extended with nulls as needed.
     */
    public ParameterBlock set(int i, int index) {
        return set(new Integer(i), index);
    }

    /**
     * Replaces an Object in the list of parameters with a Long.
     * If the index lies beyond the current source list,
     * the list is extended with nulls as needed.
     */
    public ParameterBlock set(long l, int index) {
        return set(new Long(l), index);
    }

    /**
     * Replaces an Object in the list of parameters with a Float.
     * If the index lies beyond the current source list,
     * the list is extended with nulls as needed.
     */
    public ParameterBlock set(float f, int index) {
        return set(new Float(f), index);
    }

    /**
     * Replaces an Object in the list of parameters with a Double.
     * If the index lies beyond the current source list,
     * the list is extended with nulls as needed.
     */
    public ParameterBlock set(double d, int index) {
        return set(new Double(d), index);
    }

    /**
     * Gets a parameter as an object.
     */
    public Object getObjectParameter(int index) {
        return parameters.elementAt(index);
    }

    /**
     * A convenience method to return a parameter as a byte.  An
     * exception will be thrown if the parameter is null or not a
     * Byte.
     *
     * @param index the index of the parameter to be returned.
     */
    public byte getByteParameter(int index) {
        return ((Byte)parameters.elementAt(index)).byteValue();
    }

    /**
     * A convenience method to return a parameter as a char.  An
     * exception will be thrown if the parameter is null or not a
     * Character.
     *
     * @param index the index of the parameter to be returned.
     */
    public char getCharParameter(int index) {
        return ((Character)parameters.elementAt(index)).charValue();
    }
    
    /**
     * A convenience method to return a parameter as a short.  An
     * exception will be thrown if the parameter is null or not a
     * Short.
     *
     * @param index the index of the parameter to be returned.
     */
    public short getShortParameter(int index) {
        return ((Short)parameters.elementAt(index)).shortValue();
    }

    /**
     * A convenience method to return a parameter as an int.  An
     * exception will be thrown if the parameter is null or not an
     * Integer.
     *
     * @param index the index of the parameter to be returned.
     */
    public int getIntParameter(int index) {
        return ((Integer)parameters.elementAt(index)).intValue();
    }

    /**
     * A convenience method to return a parameter as a long.  An
     * exception will be thrown if the parameter is null or not a
     * Long.
     *
     * @param index the index of the parameter to be returned.
     */
    public long getLongParameter(int index) {
        return ((Long)parameters.elementAt(index)).longValue();
    }

    /**
     * A convenience method to return a parameter as a float.  An
     * exception will be thrown if the parameter is null or not a
     * Float.
     *
     * @param index the index of the parameter to be returned.
     */
    public float getFloatParameter(int index) {
        return ((Float)parameters.elementAt(index)).floatValue();
    }

    /**
     * A convenience method to return a parameter as a double.  An
     * exception will be thrown if the parameter is null or not a
     * Double.
     *
     * @param index the index of the parameter to be returned.
     */
    public double getDoubleParameter(int index) {
        return ((Double)parameters.elementAt(index)).doubleValue();
    }

    /**
     * Returns an array of Class objects describing the types
     * of the parameters.
     */
    public Class [] getParamClasses() {
        int numParams = getNumParameters();
        Class [] classes = new Class[numParams];
        int i;

        for (i = 0; i < numParams; i++) {
            Object obj = getObjectParameter(i);
            if (obj instanceof Byte) {
              classes[i] = byte.class;
            } else if (obj instanceof Character) {
              classes[i] = char.class;
            } else if (obj instanceof Short) {
              classes[i] = short.class;
            } else if (obj instanceof Integer) {
              classes[i] = int.class;
            } else if (obj instanceof Long) {
              classes[i] = long.class;
            } else if (obj instanceof Float) {
              classes[i] = float.class;
            } else if (obj instanceof Double) {
              classes[i] = double.class;
            } else {
              classes[i] = obj.getClass();
            }
        }
        
        return classes;
    }
}
