/*
 * @(#)DemoFonts.java	1.3	99/09/10
 *
 * Copyright (c) 1998, 1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */


import java.awt.Font;
import java.util.Hashtable;
import java.net.URL;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;


/**
 * A cache of the dynamically loaded fonts found in the fonts directory.
 */
public class DemoFonts {

    private String[] names =  { "A.ttf" };
    private static Hashtable cache;


    public DemoFonts() {
        cache = new Hashtable(names.length);
        for (int i = 0; i < names.length; i++) {
            cache.put(names[i], getFont(names[i]));
        }
    }


    public static Font getFont(String name) {
        Font font = null;
        if (cache != null) {
            if ((font = (Font) cache.get(name)) != null) {
                return font;
            }
        }
        String fName = "fonts/" + name;
        try {
            InputStream is = null;
            if (Java2DemoApplet.applet != null) {
                URL url = new URL(Java2DemoApplet.applet.getCodeBase(), fName);
                is = url.openStream();
            } else {
                is = new FileInputStream(new File(fName));
            }
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception ex) { 
            ex.printStackTrace(); 
            System.err.println(fName + " not loaded.  Using serif font.");
            font = new Font("serif", Font.PLAIN, 24);
        }
        return font;
    }
}
