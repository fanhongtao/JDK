/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.accessibility;

import java.util.Vector;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Class AccessibleRelationSet determines a component's relation set.  The relation
 * set of a component is a set of AccessibleRelation objects that describe the
 * component's relationships with other components.
 *
 * @see AccessibleRelation
 *
 * @version     1.4 10/12/99
 * @author      Lynn Monsanto
 */
public class AccessibleRelationSet {

    /**
     * Each entry in the Vector represents an AccessibleRelation.
     * @see #add
     * @see #addAll
     * @see #remove
     * @see #contains
     * @see #get
     * @see #size
     * @see #toArray
     * @see #clear
     */
    protected Vector relations = null;

    /**
     * Creates a new empty relation set.
     */
    public AccessibleRelationSet() {
        relations = null;
    }

    /**
     * Creates a new relation with the initial set of relations contained in 
     * the array of relations passed in.  Duplicate entries are ignored.
     * @param relation an array of AccessibleRelation describing the 
     * relation set.
     */
    public AccessibleRelationSet(AccessibleRelation[] relations) {
        if (relations.length != 0) {
            this.relations = new Vector(relations.length);
            for (int i = 0; i < relations.length; i++) {
                if (!this.relations.contains(relations[i])) {
                    this.relations.addElement(relations[i]);
                }
            }
        }
    }

    /**
     * Adds a new relation to the current relation set if it is not already
     * present.  If the relation is already in the relation set, the relation
     * set is unchanged and the return value is false.  Otherwise, 
     * the relation is added to the relation set and the return value is
     * true.
     * @param relation the relation to add to the relation set
     * @return true if relation is added to the relation set; false if the 
     * relation set is unchanged
     */
    public boolean add(AccessibleRelation relation) {
        // [[[ PENDING:  WDW - the implementation of this does not need
        // to always use a vector of relations.  It could be improved by
        // caching the relations as a bit set.]]]
        if (relations == null) {
            relations = new Vector();
        }

        if (!relations.contains(relation)) {
            relations.addElement(relation);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds all of the relations to the existing relation set.  Duplicate 
     * entries are ignored.
     * @param relation  AccessibleRelation array describing the relation set.
     */
    public void addAll(AccessibleRelation[] relations) {
        if (relations.length != 0) {
            if (this.relations == null) {
		this.relations = new Vector(relations.length);
            }
            for (int i = 0; i < relations.length; i++) {
                if (!this.relations.contains(relations[i])) {
                    this.relations.addElement(relations[i]);
                }
            }
        }
    }

    /**
     * Removes a relation from the current relation set.  If the relation 
     * is not in the set, the relation set will be unchanged and the 
     * return value will be false.  If the relation is in the relation 
     * set, it will be removed from the set and the return value will be 
     * true.
     *	
     * @param relation the relation to remove from the relation set
     * @return true if the relation is in the relation set; false if the 
     * relation set is unchanged
     */
    public boolean remove(AccessibleRelation relation) {
        if (relations == null) {
            return false;
        } else {
            return relations.removeElement(relation);
        }
    }

    /**
     * Removes all the relations from the current relation set.
     */
    public void clear() {
        if (relations != null) {
            relations.removeAllElements();
        }
    }

    /**
     * Returns the number of relations in the relation set.
     */
    public int size() {
	if (relations == null) {
	    return 0;
	} else {
	    return relations.size();
	}
    }

    /**
     * Returns whether the relation set contains a relation
     * that matches the specified key.
     * @param key the AccessibleRelation key
     * @return true if the relation is in the relation set; otherwise false
     */
    public boolean contains(String key) {
	return get(key) != null;
    }

    /**
     * Returns the relation that matches the specified key.  
     * @param key the AccessibleRelation key
     * @return the relation, if one exists, that matches the specified key.
     * Otherwise, null is returned.
     */
    public AccessibleRelation get(String key) {
        if (relations == null) {
            return null;
        } else {
	    int len = relations.size();
	    for (int i = 0; i < len; i++) {
		AccessibleRelation relation = 
		    (AccessibleRelation)relations.elementAt(i);
		if (relation != null && relation.getKey().equals(key)) {
		    return relation;
		}
	    }
            return null;
        }
    }

    /**
     * Returns the current relation set as an array of AccessibleRelation
     * @return AccessibleRelation array contacting the current relation.
     */
    public AccessibleRelation[] toArray() {
        if (relations == null) {
            return new AccessibleRelation[0];
        } else {
            AccessibleRelation[] relationArray 
		= new AccessibleRelation[relations.size()];
            for (int i = 0; i < relationArray.length; i++) {
                relationArray[i] = (AccessibleRelation) relations.elementAt(i);
            }
            return relationArray;
        }
    }

    /**
     * Creates a localized String representing all the relations in the set 
     * using the default locale.
     *
     * @return comma separated localized String
     * @see AccessibleBundle#toDisplayString
     */
    public String toString() {
        String ret = "";
        if ((relations != null) && (relations.size() > 0)) {
            ret = ((AccessibleRelation) (relations.elementAt(0))).toDisplayString();
            for (int i = 1; i < relations.size(); i++) {
                ret = ret + "," 
                        + ((AccessibleRelation) (relations.elementAt(i))).
					      toDisplayString();
            }
        }
        return ret;
    }
}
