package org.apache.xml.dtm.ref;

import org.apache.xml.dtm.*;
import org.apache.xml.dtm.ref.ChunkedIntArray;

/** Debugging dump routine for DTMDocumentImpl. Note that it directly accesses
 * that class's internal data... which probably shouldn't be exposed.
 */
public class TestDTMNodes {

  public static void printNodeTable(DTMDocumentImpl doc) {
    int length = doc.nodes.slotsUsed(), slot[] = new int[4];
    for (int i=0; i <= length; i++) {
      doc.nodes.readSlot(i, slot);

      // Word0 is shown as its two halfwords
      short high = (short) (slot[0] >> 16);
      short low = (short) (slot[0] & 0xFFFF);

      System.out.println(i + ": (" + high + ") (" + low +
                         ") " + slot[1] + " " + slot[2] +
                         " " +slot[3] +
                         "\n\tName: " +  doc.getNodeName(i) +
                         " Value: " + doc.getNodeValue(i) +
                         " Parent: " + doc.getParent(i) +
                         " FirstAttr: " + doc.getFirstAttribute(i) +
                         " FirstChild: " + doc.getFirstChild(i) +
                         " NextSib: " + doc.getNextSibling(i)
                         ); 
    }

  }
}
