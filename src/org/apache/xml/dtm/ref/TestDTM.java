package org.apache.xml.dtm.ref;

import org.apache.xml.dtm.ref.DTMDocumentImpl;
import org.apache.xml.dtm.ref.TestDTMNodes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Tests the DTM by creating
 *
 * REWRITTEN to use SAX2 ContentHandler APIs -- original draft used
 * an incomplete/incorrect version of SAX1 DocumentHandler, which is
 * being phased out as quickly as we can possibly manage it.
 *
 * %TBD% I _think_ the SAX convention is that "no namespace" is expressed
 * as "" rather than as null (which is the DOM's convention). What should 
 * DTM expect? What should it do with the other?
 */
public class TestDTM {

  public static void main(String argv[]) {
    String text;

    /*  <?xml version="1.0"?>
     *  <top>
     *   <A>
     *    <B hat="new" car="Honda" dog="Boxer">Life is good</B>
     *   </A>
     *   <C>My Anaconda<D/>Words</C>
     *  </top> */

    DTMDocumentImpl doc = new DTMDocumentImpl(null, 0, null, 
                    org.apache.xpath.objects.XMLStringFactoryImpl.getFactory());

    try
      {
        doc.startDocument();

        doc.startElement("", "top", "top", null);

        doc.startElement("", "A", "A", null);

        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute("", "", "hat", "CDATA", "new");
        atts.addAttribute("", "", "car", "CDATA", "Honda");
        atts.addAttribute("", "", "dog", "CDATA", "Boxer");
        doc.startElement("","B","B", atts);
        text="Life is good";
        doc.characters(text.toCharArray(),0,text.length());
        doc.endElement("","B","B");

        doc.endElement("","A","A");
        doc.startElement("","C","C", null);

        text="My Anaconda";
        doc.characters(text.toCharArray(),0,text.length());
        doc.startElement("","D","D",null);
        doc.endElement("","D","D");
        text="Words";
        doc.characters(text.toCharArray(),0,text.length());

        doc.endElement("", "C", "C");

        boolean BUILDPURCHASEORDER=false;
        if(BUILDPURCHASEORDER)
          {
            int root, h, c1, c2, c3, c4, c1_text, c2_text, c3_text, c4_text;

            doc.startElement(null,"PurchaseOrderList","PurchaseOrderList", null);

            for (int i = 0; i < 10; i++) {

              doc.startElement("","PurchaseOrder","PurchaseOrder", null);

              doc.startElement("","Item","Item", null);
              text="Basketball" + " - " + i;
              doc.characters(text.toCharArray(),0,text.length());
                      
              doc.endElement("", "Item", "Item");

              doc.startElement("","Description","Description", null);
              // c2.createAttribute();
              text="Professional Leather Michael Jordan Signatured Basketball";
              doc.characters(text.toCharArray(),0,text.length());
                      
              doc.endElement("", "Description", "Description");

              doc.startElement("","UnitPrice","UnitPrice", null);
              text="$12.99";
              doc.characters(text.toCharArray(),0,text.length());
                      
              doc.endElement("", "UnitPrice", "UnitPrice");

              doc.startElement("","Quantity","Quantity", null);
              text="50";
              doc.characters(text.toCharArray(),0,text.length());
                      
              doc.endElement("", "Quantity", "Quantity");

              doc.endElement("", "PurchaseOrder", "PurchaseOrder");
            }

            doc.endElement("", "PurchaseOrderList", "PurchaseOrderList");
          } // if(BUILDPURCHASEORDER)

        doc.endElement("", "top", "top");
        doc.endDocument();
      }
    catch(org.xml.sax.SAXException e)
      {
        e.printStackTrace();
      }
                

    TestDTMNodes.printNodeTable(doc);
  }
}
