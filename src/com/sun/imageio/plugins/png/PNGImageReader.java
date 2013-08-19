/*
 * @(#)PNGImageReader.java	1.53 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.imageio.plugins.png;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import com.sun.imageio.plugins.common.InputStreamAdapter;
import com.sun.imageio.plugins.common.SubImageInputStream;

class PNGImageDataEnumeration implements Enumeration {

    boolean firstTime = true;
    ImageInputStream stream;
    int length;
    
    public PNGImageDataEnumeration(ImageInputStream stream) 
        throws IOException {
        this.stream = stream;
        this.length = stream.readInt();
        int type = stream.readInt(); // skip chunk type
    }

    public Object nextElement() {
        try {
            firstTime = false;
            ImageInputStream iis = new SubImageInputStream(stream, length);
            return new InputStreamAdapter(iis);
        } catch (IOException e) {
            return null;
        }
    }

    public boolean hasMoreElements() {
        if (firstTime) {
            return true;
        }

        try {
            int crc = stream.readInt();
            this.length = stream.readInt();
            int type = stream.readInt();
            if (type == PNGImageReader.IDAT_TYPE) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }
}

/**
 * @version 0.5
 */
public class PNGImageReader extends ImageReader {

    // Critical chunks
    static final int IHDR_TYPE = chunkType("IHDR");
    static final int PLTE_TYPE = chunkType("PLTE");
    static final int IDAT_TYPE = chunkType("IDAT");
    static final int IEND_TYPE = chunkType("IEND");
    
    // Ancillary chunks
    static final int bKGD_TYPE = chunkType("bKGD");
    static final int cHRM_TYPE = chunkType("cHRM");
    static final int gAMA_TYPE = chunkType("gAMA");
    static final int hIST_TYPE = chunkType("hIST");
    static final int iCCP_TYPE = chunkType("iCCP");
    static final int iTXt_TYPE = chunkType("iTXt");
    static final int pHYs_TYPE = chunkType("pHYs");
    static final int sBIT_TYPE = chunkType("sBIT");
    static final int sPLT_TYPE = chunkType("sPLT");
    static final int sRGB_TYPE = chunkType("sRGB");
    static final int tEXt_TYPE = chunkType("tEXt");
    static final int tIME_TYPE = chunkType("tIME");
    static final int tRNS_TYPE = chunkType("tRNS");
    static final int zTXt_TYPE = chunkType("zTXt");

    static final int PNG_COLOR_GRAY = 0;
    static final int PNG_COLOR_RGB = 2;
    static final int PNG_COLOR_PALETTE = 3;
    static final int PNG_COLOR_GRAY_ALPHA = 4;
    static final int PNG_COLOR_RGB_ALPHA = 6;

    // The number of bands by PNG color type
    static final int[] inputBandsForColorType = {
         1, // gray
        -1, // unused
         3, // rgb
         1, // palette
         2, // gray + alpha
        -1, // unused
         4  // rgb + alpha
    };

    static final int PNG_FILTER_NONE = 0;
    static final int PNG_FILTER_SUB = 1;
    static final int PNG_FILTER_UP = 2;
    static final int PNG_FILTER_AVERAGE = 3;
    static final int PNG_FILTER_PAETH = 4;

    static final int[] adam7XOffset = { 0, 4, 0, 2, 0, 1, 0 };
    static final int[] adam7YOffset = { 0, 0, 4, 0, 2, 0, 1 };
    static final int[] adam7XSubsampling = { 8, 8, 4, 4, 2, 2, 1, 1 };
    static final int[] adam7YSubsampling = { 8, 8, 8, 4, 4, 2, 2, 1 };

    private static final boolean debug = true;

    ImageInputStream stream = null;

    boolean gotHeader = false;
    boolean gotMetadata = false;

    ImageReadParam lastParam = null;

    long imageStartPosition = -1L;

    Rectangle sourceRegion = null;
    int sourceXSubsampling = -1;
    int sourceYSubsampling = -1;
    int sourceMinProgressivePass = 0;
    int sourceMaxProgressivePass = 6;
    int[] sourceBands = null;
    int[] destinationBands = null;
    Point destinationOffset = new Point(0, 0);

    PNGMetadata metadata = new PNGMetadata();

    DataInputStream pixelStream = null;

    BufferedImage theImage = null;

    // The number of source pixels processed
    int pixelsDone = 0;

    // The total number of pixels in the source image
    int totalPixels;

    public PNGImageReader(ImageReaderSpi originatingProvider) {
        super(originatingProvider);
    } 

    public void setInput(Object input,
                         boolean seekForwardOnly,
                         boolean ignoreMetadata) {
        super.setInput(input, seekForwardOnly, ignoreMetadata);
        this.stream = (ImageInputStream)input; // Always works

        // Clear all values based on the previous stream contents
        resetStreamSettings();
    }

    // Callable from ImageWriter
    static int chunkType(String typeString) {
        char c0 = typeString.charAt(0);
        char c1 = typeString.charAt(1);
        char c2 = typeString.charAt(2);
        char c3 = typeString.charAt(3);

        int type = (c0 << 24) | (c1 << 16) | (c2 << 8) | c3;
        return type;
    }

    private String readNullTerminatedString() throws IOException {
        StringBuffer b = new StringBuffer();
        int c;

        while ((c = stream.read()) != 0) {
            b.append((char)c);
        }
        return b.toString();
    }

    private void readHeader() throws IIOException {
        if (gotHeader) {
            return;
        }
        if (stream == null) {
            throw new IllegalStateException("Input source not set!");
        }

        try {
            byte[] signature = new byte[8];
            stream.readFully(signature);
            
            if (signature[0] != (byte)137 ||
                signature[1] != (byte)80 ||
                signature[2] != (byte)78 ||
                signature[3] != (byte)71 ||
                signature[4] != (byte)13 ||
                signature[5] != (byte)10 ||
                signature[6] != (byte)26 ||
                signature[7] != (byte)10) {
                throw new IIOException("Bad PNG signature!");
            }
            
            int IHDR_length = stream.readInt();
            if (IHDR_length != 13) {
                throw new IIOException("Bad length for IHDR chunk!");
            }
            int IHDR_type = stream.readInt();
            if (IHDR_type != IHDR_TYPE) {
                throw new IIOException("Bad type for IHDR chunk!");
            }

            this.metadata = new PNGMetadata();

            int width = stream.readInt();
            int height = stream.readInt();
            int bitDepth = stream.readUnsignedByte();
            int colorType = stream.readUnsignedByte();
            int compressionMethod = stream.readUnsignedByte();
            int filterMethod = stream.readUnsignedByte();
            int interlaceMethod = stream.readUnsignedByte();
            
            int IHDR_CRC = stream.readInt();

            stream.flushBefore(stream.getStreamPosition());

            if (width == 0) {
                throw new IIOException("Image width == 0!");
            }
            if (height == 0) {
                throw new IIOException("Image height == 0!");
            }
            if (bitDepth != 1 && bitDepth != 2 && bitDepth != 4 &&
                bitDepth != 8 && bitDepth != 16) {
                throw new IIOException("Bit depth must be 1, 2, 4, 8, or 16!");
            }
            if (colorType != 0 && colorType != 2 && colorType != 3 &&
                colorType != 4 && colorType != 6) {
                throw new IIOException("Color type must be 0, 2, 3, 4, or 6!");
            }
            if (colorType == PNG_COLOR_PALETTE && bitDepth == 16) {
                throw new IIOException("Bad color type/bit depth combination!");
            }
            if ((colorType == PNG_COLOR_RGB ||
                 colorType == PNG_COLOR_RGB_ALPHA ||
                 colorType == PNG_COLOR_GRAY_ALPHA) &&
                (bitDepth != 8 && bitDepth != 16)) {
                throw new IIOException("Bad color type/bit depth combination!");
            }
            if (compressionMethod != 0) {
                throw new IIOException("Unknown compression method (not 0)!");
            }
            if (filterMethod != 0) {
                throw new IIOException("Unknown filter method (not 0)!");
            }
            if (interlaceMethod != 0 && interlaceMethod != 1) {
                throw new IIOException("Unknown interlace method (not 0 or 1)!");
            }
        
            metadata.IHDR_present = true;
            metadata.IHDR_width = width;
            metadata.IHDR_height = height;
            metadata.IHDR_bitDepth = bitDepth;
            metadata.IHDR_colorType = colorType; 
            metadata.IHDR_compressionMethod = compressionMethod;
            metadata.IHDR_filterMethod = filterMethod;
            metadata.IHDR_interlaceMethod = interlaceMethod;
            gotHeader = true;
        } catch (IOException e) {
            throw new IIOException("I/O error reading PNG header!", e);
        }
    }

    private void parse_PLTE_chunk(int chunkLength) throws IOException {
        if (metadata.PLTE_present) {
            processWarningOccurred(
"A PNG image may not contain more than one PLTE chunk.\n" +
"The chunk wil be ignored.");
            return;
        } else if (metadata.IHDR_colorType == PNG_COLOR_GRAY ||
                   metadata.IHDR_colorType == PNG_COLOR_GRAY_ALPHA) {
            processWarningOccurred(
"A PNG gray or gray alpha image cannot have a PLTE chunk.\n" +
"The chunk wil be ignored.");
            return;
        }

        byte[] palette = new byte[chunkLength];
        stream.readFully(palette);

        int numEntries = chunkLength/3;
        if (metadata.IHDR_colorType == PNG_COLOR_PALETTE) {
            int maxEntries = 1 << metadata.IHDR_bitDepth;
            if (numEntries > maxEntries) {
                processWarningOccurred(
"PLTE chunk contains too many entries for bit depth, ignoring extras.");
                numEntries = maxEntries;
            }
            numEntries = Math.min(numEntries, maxEntries);
        }

        // Round array sizes up to 2^2^n
        int paletteEntries;
        if (numEntries > 16) {
            paletteEntries = 256;
        } else if (numEntries > 8) {
            paletteEntries = 16;
        } else if (numEntries > 2) {
            paletteEntries = 4;
        } else {
            paletteEntries = 2;
        }

        metadata.PLTE_present = true;
        metadata.PLTE_red = new byte[paletteEntries];
        metadata.PLTE_green = new byte[paletteEntries];
        metadata.PLTE_blue = new byte[paletteEntries];

        int index = 0;
        for (int i = 0; i < numEntries; i++) {
            metadata.PLTE_red[i] = palette[index++];
            metadata.PLTE_green[i] = palette[index++];
            metadata.PLTE_blue[i] = palette[index++];
        }
    }

    private void parse_bKGD_chunk() throws IOException {
        if (metadata.IHDR_colorType == PNG_COLOR_PALETTE) {
            metadata.bKGD_colorType = PNG_COLOR_PALETTE;
            metadata.bKGD_index = stream.readUnsignedByte();
        } else if (metadata.IHDR_colorType == PNG_COLOR_GRAY ||
                   metadata.IHDR_colorType == PNG_COLOR_GRAY_ALPHA) {
            metadata.bKGD_colorType = PNG_COLOR_GRAY;
            metadata.bKGD_gray = stream.readUnsignedShort();
        } else { // RGB or RGB_ALPHA
            metadata.bKGD_colorType = PNG_COLOR_RGB;
            metadata.bKGD_red = stream.readUnsignedShort();
            metadata.bKGD_green = stream.readUnsignedShort();
            metadata.bKGD_blue = stream.readUnsignedShort();
        }

        metadata.bKGD_present = true;
    }

    private void parse_cHRM_chunk() throws IOException {
        metadata.cHRM_whitePointX = stream.readInt();
        metadata.cHRM_whitePointY = stream.readInt();
        metadata.cHRM_redX = stream.readInt();
        metadata.cHRM_redY = stream.readInt();
        metadata.cHRM_greenX = stream.readInt();
        metadata.cHRM_greenY = stream.readInt();
        metadata.cHRM_blueX = stream.readInt();
        metadata.cHRM_blueY = stream.readInt();

        metadata.cHRM_present = true;
    }

    private void parse_gAMA_chunk() throws IOException {
        int gamma = stream.readInt();
        metadata.gAMA_gamma = gamma;

        metadata.gAMA_present = true;
    }

    private void parse_hIST_chunk() throws IOException, IIOException {
        if (!metadata.PLTE_present) {
            throw new IIOException("hIST chunk without prior PLTE chunk!");
        }

        metadata.hIST_histogram = new char[metadata.PLTE_red.length];
        stream.readFully(metadata.hIST_histogram,
                         0, metadata.hIST_histogram.length);

        metadata.hIST_present = true;
    }

    private void parse_iCCP_chunk(int chunkLength) throws IOException {
        String keyword = readNullTerminatedString();
        metadata.iCCP_profileName = keyword;

        metadata.iCCP_compressionMethod = stream.readUnsignedByte();

        byte[] compressedProfile =
          new byte[chunkLength - keyword.length() - 2];
        stream.readFully(compressedProfile);
        metadata.iCCP_compressedProfile = compressedProfile;

        metadata.iCCP_present = true;
    }
  
    private void parse_iTXt_chunk(int chunkLength) throws IOException {
        long chunkStart = stream.getStreamPosition();

        String keyword = readNullTerminatedString();
        metadata.iTXt_keyword.add(keyword);
        
        int compressionFlag = stream.readUnsignedByte();
        metadata.iTXt_compressionFlag.add(new Integer(compressionFlag));
        
        int compressionMethod = stream.readUnsignedByte();
        metadata.iTXt_compressionMethod.add(new Integer(compressionMethod));
        
        String languageTag = readNullTerminatedString();
        metadata.iTXt_languageTag.add(languageTag);
        
        String translatedKeyword = stream.readUTF();
        metadata.iTXt_translatedKeyword.add(translatedKeyword);
        stream.skipBytes(1); // Null separator

        String text;
        if (compressionFlag == 1) { // Decompress the text
            long pos = stream.getStreamPosition();
            byte[] b = new byte[(int)(chunkStart + chunkLength - pos)];
            stream.readFully(b);
            text = inflate(b);
        } else {
            text = stream.readUTF();
        }
        metadata.iTXt_text.add(text);
    }

    private void parse_pHYs_chunk() throws IOException {
        metadata.pHYs_pixelsPerUnitXAxis = stream.readInt();
        metadata.pHYs_pixelsPerUnitYAxis = stream.readInt();
        metadata.pHYs_unitSpecifier = stream.readUnsignedByte();

        metadata.pHYs_present = true;
    }
    
    private void parse_sBIT_chunk() throws IOException {
        int colorType = metadata.IHDR_colorType;
        if (colorType == PNG_COLOR_GRAY ||
            colorType == PNG_COLOR_GRAY_ALPHA) {
            metadata.sBIT_grayBits = stream.readUnsignedByte();
        } else if (colorType == PNG_COLOR_RGB ||
                   colorType == PNG_COLOR_PALETTE ||
                   colorType == PNG_COLOR_RGB_ALPHA) {
            metadata.sBIT_redBits = stream.readUnsignedByte();
            metadata.sBIT_greenBits = stream.readUnsignedByte();
            metadata.sBIT_blueBits = stream.readUnsignedByte();
        }

        if (colorType == PNG_COLOR_GRAY_ALPHA ||
            colorType == PNG_COLOR_RGB_ALPHA) {
            metadata.sBIT_alphaBits = stream.readUnsignedByte();
        }

        metadata.sBIT_colorType = colorType;
        metadata.sBIT_present = true;
    }

    private void parse_sPLT_chunk(int chunkLength)
        throws IOException, IIOException {
        metadata.sPLT_paletteName = readNullTerminatedString();
        chunkLength -= metadata.sPLT_paletteName.length() + 1;

        int sampleDepth = stream.readUnsignedByte();
        metadata.sPLT_sampleDepth = sampleDepth;

        int numEntries = chunkLength/(4*(sampleDepth/8) + 2);
        metadata.sPLT_red = new int[numEntries];
        metadata.sPLT_green = new int[numEntries];
        metadata.sPLT_blue = new int[numEntries];
        metadata.sPLT_alpha = new int[numEntries];
        metadata.sPLT_frequency = new int[numEntries];

        if (sampleDepth == 8) {
            for (int i = 0; i < numEntries; i++) {
                metadata.sPLT_red[i] = stream.readUnsignedByte();
                metadata.sPLT_green[i] = stream.readUnsignedByte();
                metadata.sPLT_blue[i] = stream.readUnsignedByte();
                metadata.sPLT_alpha[i] = stream.readUnsignedByte();
                metadata.sPLT_frequency[i] = stream.readUnsignedShort();
            }
        } else if (sampleDepth == 16) {
            for (int i = 0; i < numEntries; i++) {
                metadata.sPLT_red[i] = stream.readUnsignedShort();
                metadata.sPLT_green[i] = stream.readUnsignedShort();
                metadata.sPLT_blue[i] = stream.readUnsignedShort();
                metadata.sPLT_alpha[i] = stream.readUnsignedShort();
                metadata.sPLT_frequency[i] = stream.readUnsignedShort();
            }
        } else {
            throw new IIOException("sPLT sample depth not 8 or 16!");
        }

        metadata.sPLT_present = true;
    }

    private void parse_sRGB_chunk() throws IOException {
        metadata.sRGB_renderingIntent = stream.readUnsignedByte();

        metadata.sRGB_present = true;
    }

    private void parse_tEXt_chunk(int chunkLength) throws IOException {
        String keyword = readNullTerminatedString();
        metadata.tEXt_keyword.add(keyword);

        byte[] b = new byte[chunkLength - keyword.length() - 1];
        stream.readFully(b);
        metadata.tEXt_text.add(new String(b));
    }

    private void parse_tIME_chunk() throws IOException {
        metadata.tIME_year = stream.readUnsignedShort();
        metadata.tIME_month = stream.readUnsignedByte();
        metadata.tIME_day = stream.readUnsignedByte();
        metadata.tIME_hour = stream.readUnsignedByte();
        metadata.tIME_minute = stream.readUnsignedByte();
        metadata.tIME_second = stream.readUnsignedByte();

        metadata.tIME_present = true;
    }

    private void parse_tRNS_chunk(int chunkLength) throws IOException {
        int colorType = metadata.IHDR_colorType;
        if (colorType == PNG_COLOR_PALETTE) {
            if (!metadata.PLTE_present) {
                processWarningOccurred(
"tRNS chunk without prior PLTE chunk, ignoring it.");
                return;
            }

            // Alpha table may have fewer entries than RGB palette
            int maxEntries = metadata.PLTE_red.length;
            int numEntries = chunkLength;
            if (numEntries > maxEntries) {
                processWarningOccurred(
"tRNS chunk has more entries than prior PLTE chunk, ignoring extras.");
                numEntries = maxEntries;
            }
            metadata.tRNS_alpha = new byte[numEntries];
            metadata.tRNS_colorType = PNG_COLOR_PALETTE;
            stream.read(metadata.tRNS_alpha, 0, numEntries);
            stream.skipBytes(chunkLength - numEntries);
        } else if (colorType == PNG_COLOR_GRAY) {
            if (chunkLength != 2) {
                processWarningOccurred(
"tRNS chunk for gray image must have length 2, ignoring chunk.");
                stream.skipBytes(chunkLength);
                return;
            }
            metadata.tRNS_gray = stream.readUnsignedShort();
            metadata.tRNS_colorType = PNG_COLOR_GRAY;
        } else if (colorType == PNG_COLOR_RGB) {
            if (chunkLength != 6) {
                processWarningOccurred(
"tRNS chunk for RGB image must have length 6, ignoring chunk.");
                stream.skipBytes(chunkLength);
                return;
            }
            metadata.tRNS_red = stream.readUnsignedShort();
            metadata.tRNS_green = stream.readUnsignedShort();
            metadata.tRNS_blue = stream.readUnsignedShort();
            metadata.tRNS_colorType = PNG_COLOR_RGB;
        } else {
            processWarningOccurred(
"Gray+Alpha and RGBS images may not have a tRNS chunk, ignoring it.");
            return;
        }

        metadata.tRNS_present = true;
    }

    private static String inflate(byte[] b) throws IOException {
        InputStream bais = new ByteArrayInputStream(b);
        InputStream iis = new InflaterInputStream(bais);
        StringBuffer sb = new StringBuffer(80);
        int c;
        while ((c = iis.read()) != -1) {
            sb.append((char)c);
        }
        return sb.toString();
    }
    
    private void parse_zTXt_chunk(int chunkLength) throws IOException {
        String keyword = readNullTerminatedString();
        metadata.zTXt_keyword.add(keyword);

        int method = stream.readUnsignedByte();
        metadata.zTXt_compressionMethod.add(new Integer(method));

        byte[] b = new byte[chunkLength - keyword.length() - 2];
        stream.readFully(b);
        metadata.zTXt_text.add(inflate(b));
    }

    private void readMetadata() throws IIOException {
        if (gotMetadata) {
            return;
        }
        
        readHeader();

        try {
            while (true) {
                int chunkLength = stream.readInt();
                int chunkType = stream.readInt();

                // If chunk type is 'IDAT', we've reached the image data.
                if (chunkType == IDAT_TYPE) {
                    stream.skipBytes(-8);
                    imageStartPosition = stream.getStreamPosition();
                    break;
                }
                
                if (chunkType == PLTE_TYPE) {
                    parse_PLTE_chunk(chunkLength);
                } else if (chunkType == bKGD_TYPE) {
                    parse_bKGD_chunk();
                } else if (chunkType == cHRM_TYPE) {
                    parse_cHRM_chunk();
                } else if (chunkType == gAMA_TYPE) {
                    parse_gAMA_chunk();
                } else if (chunkType == hIST_TYPE) {
                    parse_hIST_chunk();
                } else if (chunkType == iCCP_TYPE) {
                    parse_iCCP_chunk(chunkLength);
                } else if (chunkType == iTXt_TYPE) {
                    parse_iTXt_chunk(chunkLength);
                } else if (chunkType == pHYs_TYPE) {
                    parse_pHYs_chunk();
                } else if (chunkType == sBIT_TYPE) {
                    parse_sBIT_chunk();
                } else if (chunkType == sPLT_TYPE) {
                    parse_sPLT_chunk(chunkLength);
                } else if (chunkType == sRGB_TYPE) {
                    parse_sRGB_chunk();
                } else if (chunkType == tEXt_TYPE) {
                    parse_tEXt_chunk(chunkLength);
                } else if (chunkType == tIME_TYPE) {
                    parse_tIME_chunk();
                } else if (chunkType == tRNS_TYPE) {
                    parse_tRNS_chunk(chunkLength);
                } else if (chunkType == zTXt_TYPE) {
                    parse_zTXt_chunk(chunkLength);
                } else {
                    // Read an unknown chunk
                    byte[] b = new byte[chunkLength];
                    stream.readFully(b);

                    StringBuffer chunkName = new StringBuffer(4);
                    chunkName.append((char)(chunkType >>> 24));
                    chunkName.append((char)((chunkType >> 16) & 0xff));
                    chunkName.append((char)((chunkType >> 8) & 0xff));
                    chunkName.append((char)(chunkType & 0xff));

                    int ancillaryBit = chunkType >>> 28;
                    if (ancillaryBit == 0) {
                        processWarningOccurred(
"Encountered unknown chunk with critical bit set!");
                    }

                    metadata.unknownChunkType.add(chunkName.toString());
                    metadata.unknownChunkData.add(b);
                }

                int chunkCRC = stream.readInt();
                stream.flushBefore(stream.getStreamPosition());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IIOException("Error reading PNG metadata", e);
        }

        gotMetadata = true;
    }

    // Data filtering methods

    private static void decodeSubFilter(byte[] curr, int coff, int count,
                                        int bpp) {
        for (int i = bpp; i < count; i++) {
            int val;

            val = curr[i + coff] & 0xff;
            val += curr[i + coff - bpp] & 0xff;

            curr[i + coff] = (byte)val;
        }
    }

    private static void decodeUpFilter(byte[] curr, int coff,
                                       byte[] prev, int poff,
                                       int count) {
        for (int i = 0; i < count; i++) {
            int raw = curr[i + coff] & 0xff;
            int prior = prev[i + poff] & 0xff;

            curr[i + coff] = (byte)(raw + prior);
        }
    }

    private static void decodeAverageFilter(byte[] curr, int coff,
                                            byte[] prev, int poff,
                                            int count, int bpp) {
        int raw, priorPixel, priorRow;

        for (int i = 0; i < bpp; i++) {
            raw = curr[i + coff] & 0xff;
            priorRow = prev[i + poff] & 0xff;
            
            curr[i + coff] = (byte)(raw + priorRow/2);
        }

        for (int i = bpp; i < count; i++) {
            raw = curr[i + coff] & 0xff;
            priorPixel = curr[i + coff - bpp] & 0xff;
            priorRow = prev[i + poff] & 0xff;
            
            curr[i + coff] = (byte)(raw + (priorPixel + priorRow)/2);
        }
    }

    private static int paethPredictor(int a, int b, int c) {
        int p = a + b - c;
        int pa = Math.abs(p - a);
        int pb = Math.abs(p - b);
        int pc = Math.abs(p - c);

        if ((pa <= pb) && (pa <= pc)) {
            return a;
        } else if (pb <= pc) {
            return b;
        } else {
            return c;
        }
    } 

    private static void decodePaethFilter(byte[] curr, int coff,
                                          byte[] prev, int poff,
                                          int count, int bpp) {
        int raw, priorPixel, priorRow, priorRowPixel;

        for (int i = 0; i < bpp; i++) {
            raw = curr[i + coff] & 0xff;
            priorRow = prev[i + poff] & 0xff;

            curr[i + coff] = (byte)(raw + priorRow);
        }

        for (int i = bpp; i < count; i++) {
            raw = curr[i + coff] & 0xff;
            priorPixel = curr[i + coff - bpp] & 0xff;
            priorRow = prev[i + poff] & 0xff;
            priorRowPixel = prev[i + poff - bpp] & 0xff;

            curr[i + coff] = (byte)(raw + paethPredictor(priorPixel,
                                                         priorRow,
                                                         priorRowPixel));
        }
    }

    private static final int[][] bandOffsets = {
        null,
        { 0 }, // G
        { 0, 1 }, // GA in GA order
        { 0, 1, 2 }, // RGB in RGB order
        { 0, 1, 2, 3 } // RGBA in RGBA order
    };

    private WritableRaster createRaster(int width, int height, int bands,
                                        int scanlineStride,
                                        int bitDepth) {

        DataBuffer dataBuffer;
        WritableRaster ras = null;
        Point origin = new Point(0, 0);
        if ((bitDepth < 8) && (bands == 1)) {
            dataBuffer = new DataBufferByte(height*scanlineStride);
            ras = Raster.createPackedRaster(dataBuffer,
                                            width, height,
                                            bitDepth,
                                            origin);
        } else if (bitDepth <= 8) {
            dataBuffer = new DataBufferByte(height*scanlineStride);
            ras = Raster.createInterleavedRaster(dataBuffer,
                                                 width, height,
                                                 scanlineStride,
                                                 bands,
                                                 bandOffsets[bands],
                                                 origin);
        } else {
            dataBuffer = new DataBufferUShort(height*scanlineStride);
            ras = Raster.createInterleavedRaster(dataBuffer,
                                                 width, height,
                                                 scanlineStride,
                                                 bands,
                                                 bandOffsets[bands],
                                                 origin);
        }

        return ras;
    }

    private void skipPass(int passWidth, int passHeight)
        throws IOException, IIOException  {
        if ((passWidth == 0) || (passHeight == 0)) {
            return;
        }

        int inputBands = inputBandsForColorType[metadata.IHDR_colorType];
        int bytesPerRow = (inputBands*passWidth*metadata.IHDR_bitDepth + 7)/8;
        byte[] curr = new byte[bytesPerRow];

        // Read the image row-by-row
        for (int srcY = 0; srcY < passHeight; srcY++) {
            // Read the filter type byte and a row of data
            int filter = pixelStream.read();
            pixelStream.readFully(curr, 0, bytesPerRow);

            // If read has been aborted, just return
            // processReadAborted will be called later
            if (abortRequested()) {
                return;
            }
        }
    }

    // Helper for protected computeUpdatedPixels method
    private static void computeUpdatedPixels(int sourceOffset,
                                             int sourceExtent,
                                             int destinationOffset,
                                             int dstMin,
                                             int dstMax,
                                             int sourceSubsampling,
                                             int passStart,
                                             int passExtent,
                                             int passPeriod,
                                             int[] vals,
                                             int offset) {

        // We need to satisfy the congruences:
        // dst = destinationOffset + (src - sourceOffset)/sourceSubsampling
        //
        // src - passStart == 0 (mod passPeriod)
        // src - sourceOffset == 0 (mod sourceSubsampling)
        //
        // subject to the inequalities:
        //
        // src >= passStart
        // src < passStart + passExtent
        // src >= sourceOffset
        // src < sourceOffset + sourceExtent
        // dst >= dstMin
        // dst <= dstmax
        //
        // where
        //
        // dst = destinationOffset + (src - sourceOffset)/sourceSubsampling
        //
        // For now we use a brute-force approach although we could
        // attempt to analyze the congruences.  If passPeriod and
        // sourceSubsamling are relatively prime, the period will be
        // their product.  If they share a common factor, either the
        // period will be equal to the larger value, or the sequences
        // will be completely disjoint, depending on the relationship
        // between passStart and sourceOffset.  Since we only have to do this
        // twice per image (once each for X and Y), it seems cheap enough
        // to do it the straightforward way.

        boolean gotPixel = false;
        int firstDst = -1;
        int secondDst = -1;
        int lastDst = -1;

        for (int i = 0; i < passExtent; i++) {
            int src = passStart + i*passPeriod;
            if (src < sourceOffset) {
                continue;
            }
            if ((src - sourceOffset) % sourceSubsampling != 0) {
                continue;
            }
            if (src >= sourceOffset + sourceExtent) {
                break;
            }

            int dst = destinationOffset +
                (src - sourceOffset)/sourceSubsampling;
            if (dst < dstMin) {
                continue;
            }
            if (dst > dstMax) {
                break;
            }

            if (!gotPixel) {
                firstDst = dst; // Record smallest valid pixel
                gotPixel = true;
            } else if (secondDst == -1) {
                secondDst = dst; // Record second smallest valid pixel
            }
            lastDst = dst; // Record largest valid pixel
        }

        vals[offset] = firstDst;

        // If we never saw a valid pixel, set width to 0
        if (!gotPixel) {
            vals[offset + 2] = 0;
        } else {
            vals[offset + 2] = lastDst - firstDst + 1;
        }

        // The period is given by the difference of any two adjacent pixels
        vals[offset + 4] = Math.max(secondDst - firstDst, 1);
    }

    /**
     * A utility method that computes the exact set of destination
     * pixels that will be written during a particular decoding pass.
     * The intent is to simplify the work done by readers in combining
     * the source region, source subsampling, and destination offset
     * information obtained from the <code>ImageReadParam</code> with
     * the offsets and periods of a progressive or interlaced decoding
     * pass.
     *
     * @param sourceRegion a <code>Rectangle</code> containing the
     * source region being read, offset by the source subsampling
     * offsets, and clipped against the source bounds, as returned by
     * the <code>getSourceRegion</code> method.
     * @param destinationOffset a <code>Point</code> containing the
     * coordinates of the upper-left pixel to be written in the
     * destination.
     * @param dstMinX the smallest X coordinate (inclusive) of the
     * destination <code>Raster</code>.
     * @param dstMinY the smallest Y coordinate (inclusive) of the
     * destination <code>Raster</code>.
     * @param dstMaxX the largest X coordinate (inclusive) of the destination
     * <code>Raster</code>.
     * @param dstMaxY the largest Y coordinate (inclusive) of the destination
     * <code>Raster</code>.
     * @param sourceXSubsampling the X subsampling factor.
     * @param sourceYSubsampling the Y subsampling factor.
     * @param passXStart the smallest source X coordinate (inclusive)
     * of the current progressive pass.
     * @param passYStart the smallest source Y coordinate (inclusive)
     * of the current progressive pass.
     * @param passWidth the width in pixels of the current progressive
     * pass.
     * @param passHeight the height in pixels of the current progressive
     * pass.
     * @param passPeriodX the X period (horizontal spacing between
     * pixels) of the current progressive pass.
     * @param passPeriodY the Y period (vertical spacing between
     * pixels) of the current progressive pass.
     *
     * @return an array of 6 <code>int</code>s containing the
     * destination min X, min Y, width, height, X period and Y period
     * of the region that will be updated.
     */
    private static int[] computeUpdatedPixels(Rectangle sourceRegion,
                                              Point destinationOffset,
                                              int dstMinX,
                                              int dstMinY,
                                              int dstMaxX,
                                              int dstMaxY,
                                              int sourceXSubsampling,
                                              int sourceYSubsampling,
                                              int passXStart,
                                              int passYStart,
                                              int passWidth,
                                              int passHeight,
                                              int passPeriodX,
                                              int passPeriodY) {
        int[] vals = new int[6];
        computeUpdatedPixels(sourceRegion.x, sourceRegion.width,
                             destinationOffset.x,
                             dstMinX, dstMaxX, sourceXSubsampling,
                             passXStart, passWidth, passPeriodX,
                             vals, 0);
        computeUpdatedPixels(sourceRegion.y, sourceRegion.height,
                             destinationOffset.y,
                             dstMinY, dstMaxY, sourceYSubsampling,
                             passYStart, passHeight, passPeriodY,
                             vals, 1);
        return vals;
    }

    private void updateImageProgress(int newPixels) {
        pixelsDone += newPixels;
        processImageProgress(100.0F*pixelsDone/totalPixels);
    }

    private void decodePass(int passNum,
                            int xStart, int yStart,
                            int xStep, int yStep,
                            int passWidth, int passHeight) throws IOException {

        if ((passWidth == 0) || (passHeight == 0)) {
            return;
        }

        WritableRaster imRas = theImage.getWritableTile(0, 0);
        int dstMinX = imRas.getMinX();
        int dstMaxX = dstMinX + imRas.getWidth() - 1;
        int dstMinY = imRas.getMinY();
        int dstMaxY = dstMinY + imRas.getHeight() - 1;

        // Determine which pixels will be updated in this pass
        int[] vals = computeUpdatedPixels(sourceRegion,
                                          destinationOffset,
                                          dstMinX, dstMinY,
                                          dstMaxX, dstMaxY,
                                          sourceXSubsampling,
                                          sourceYSubsampling,
                                          xStart, yStart,
                                          passWidth, passHeight,
                                          xStep, yStep);
        int updateMinX = vals[0];
        int updateMinY = vals[1];
        int updateWidth = vals[2];
        int updateXStep = vals[4];
        int updateYStep = vals[5];

        int bitDepth = metadata.IHDR_bitDepth;
        int inputBands = inputBandsForColorType[metadata.IHDR_colorType];
        int bytesPerPixel = (bitDepth == 16) ? 2 : 1; 
        bytesPerPixel *= inputBands;
        
        int bytesPerRow = (inputBands*passWidth*bitDepth + 7)/8;
        int eltsPerRow = (bitDepth == 16) ? bytesPerRow/2 : bytesPerRow;

        // If no pixels need updating, just skip the input data 
        if (updateWidth == 0) {
            for (int srcY = 0; srcY < passHeight; srcY++) {
                // Update count of pixels read
                updateImageProgress(passWidth);
                pixelStream.skipBytes(1 + bytesPerRow);
            }
            return;
        }
        
        // Backwards map from destination pixels
        // (dstX = updateMinX + k*updateXStep)
        // to source pixels (sourceX), and then
        // to offset and skip in passRow (srcX and srcXStep)  
        int sourceX = 
            (updateMinX - destinationOffset.x)*sourceXSubsampling +
            sourceRegion.x;
        int srcX = (sourceX - xStart)/xStep;
        
        // Compute the step factor in the source 
        int srcXStep = updateXStep*sourceXSubsampling/xStep;

        byte[] byteData = null;
        short[] shortData = null;
        byte[] curr = new byte[bytesPerRow];
        byte[] prior = new byte[bytesPerRow];

        // Create a 1-row tall Raster to hold the data
        WritableRaster passRow = createRaster(passWidth, 1, inputBands,
                                              eltsPerRow,
                                              bitDepth);
        
        // Create an array suitable for holding one pixel
        int[] ps = passRow.getPixel(0, 0, (int[])null);
        
        DataBuffer dataBuffer = passRow.getDataBuffer();
        int type = dataBuffer.getDataType();
        if (type == DataBuffer.TYPE_BYTE) {
            byteData = ((DataBufferByte)dataBuffer).getData();
        } else {
            shortData = ((DataBufferUShort)dataBuffer).getData();
        }
        
        processPassStarted(theImage,
                           passNum,
                           sourceMinProgressivePass,
                           sourceMaxProgressivePass,
                           updateMinX, updateMinY,
                           updateXStep, updateYStep,
                           destinationBands);
        
        // Handle source and destination bands
        if (sourceBands != null) {
            passRow = passRow.createWritableChild(0, 0,
                                                  passRow.getWidth(), 1,
                                                  0, 0,
                                                  sourceBands);
        }
        if (destinationBands != null) {
            imRas = imRas.createWritableChild(0, 0,
                                              imRas.getWidth(),
                                              imRas.getHeight(),
                                              0, 0,
                                              destinationBands);
        }

        // Determine if all of the relevant output bands have the
        // same bit depth as the source data
        boolean adjustBitDepths = false;
        int[] outputSampleSize = imRas.getSampleModel().getSampleSize();
        int numBands = outputSampleSize.length;
        for (int b = 0; b < numBands; b++) {
            if (outputSampleSize[b] != bitDepth) {
                adjustBitDepths = true;
                break;
            }
        }

        // If the bit depths differ, create a lookup table per band to perform
        // the conversion
        int[][] scale = null;
        if (adjustBitDepths) {
            int maxInSample = (1 << bitDepth) - 1;
            int halfMaxInSample = maxInSample/2;
            scale = new int[numBands][];
            for (int b = 0; b < numBands; b++) {
                int maxOutSample = (1 << outputSampleSize[b]) - 1;
                scale[b] = new int[maxInSample + 1];
                for (int s = 0; s <= maxInSample; s++) {
                    scale[b][s] =
                        (s*maxOutSample + halfMaxInSample)/maxInSample;
                }
            }
        }

        // Limit passRow to relevant area for the case where we
        // will can setRect to copy a contiguous span
        boolean useSetRect = srcXStep == 1 &&
            updateXStep == 1 &&
            !adjustBitDepths;
        if (useSetRect) {
            passRow = passRow.createWritableChild(srcX, 0,
                                                  updateWidth, 1, 
                                                  0, 0,
                                                  null);
        }

        // Decode the (sub)image row-by-row
        for (int srcY = 0; srcY < passHeight; srcY++) {
            // Update count of pixels read
            updateImageProgress(passWidth);
            
            // Read the filter type byte and a row of data
            int filter = pixelStream.read();
            try {
                // Swap curr and prior
                byte[] tmp = prior;
                prior = curr;
                curr = tmp;

                pixelStream.readFully(curr, 0, bytesPerRow);
            } catch (java.util.zip.ZipException ze) {
                // TODO - throw a more meaningful exception
                throw ze;
            }

            switch (filter) {
            case PNG_FILTER_NONE:
                break;
            case PNG_FILTER_SUB:
                decodeSubFilter(curr, 0, bytesPerRow, bytesPerPixel);
                break;
            case PNG_FILTER_UP:
                decodeUpFilter(curr, 0, prior, 0, bytesPerRow);
                break;
            case PNG_FILTER_AVERAGE:
                decodeAverageFilter(curr, 0, prior, 0, bytesPerRow,
                                    bytesPerPixel);
                break;
            case PNG_FILTER_PAETH:
                decodePaethFilter(curr, 0, prior, 0, bytesPerRow,
                                  bytesPerPixel);
                break;
            default:
                throw new IIOException("Unknown row filter type (= " +
                                       filter + ")!");
            }

            // Copy data into passRow byte by byte
            if (bitDepth < 16) {
                System.arraycopy(curr, 0, byteData, 0, bytesPerRow);
            } else {
                int idx = 0;
                for (int j = 0; j < eltsPerRow; j++) {
                    shortData[j] =
                        (short)((curr[idx] << 8) | (curr[idx + 1] & 0xff));
                    idx += 2;
                }
            }

            // True Y position in source
            int sourceY = srcY*yStep + yStart;
            if ((sourceY >= sourceRegion.y) && 
                (sourceY < sourceRegion.y + sourceRegion.height) &&
                (((sourceY - sourceRegion.y) %
                  sourceYSubsampling) == 0)) {
                
                int dstY = destinationOffset.y +
                    (sourceY - sourceRegion.y)/sourceYSubsampling;
                if (dstY < dstMinY) {
                    continue;
                }
                if (dstY > dstMaxY) {
                    break;
                }

                if (useSetRect) {
                    imRas.setRect(updateMinX, dstY, passRow);
                } else {
                    int newSrcX = srcX;

                    for (int dstX = updateMinX;
                         dstX < updateMinX + updateWidth;
                         dstX += updateXStep) {
                        
                        passRow.getPixel(newSrcX, 0, ps);
                        if (adjustBitDepths) {
                            for (int b = 0; b < numBands; b++) {
                                ps[b] = scale[b][ps[b]];
                            }
                        }
                        imRas.setPixel(dstX, dstY, ps);
                        newSrcX += srcXStep;
                    }
                }
                
                processImageUpdate(theImage,
                                   updateMinX, dstY,
                                   updateWidth, 1,
                                   updateXStep, updateYStep,
                                   destinationBands);

                // If read has been aborted, just return
                // processReadAborted will be called later
                if (abortRequested()) {
                    return;
                }
            }
        }
        
        processPassComplete(theImage);
    }

    private void decodeImage()
        throws IOException, IIOException  {
        int width = metadata.IHDR_width;
        int height = metadata.IHDR_height;

        this.pixelsDone = 0;
        this.totalPixels = width*height;

        clearAbortRequest();

        if (metadata.IHDR_interlaceMethod == 0) {
            decodePass(0, 0, 0, 1, 1, width, height);
        } else {
            for (int i = 0; i <= sourceMaxProgressivePass; i++) {
                int XOffset = adam7XOffset[i];
                int YOffset = adam7YOffset[i];
                int XSubsampling = adam7XSubsampling[i];
                int YSubsampling = adam7YSubsampling[i];
                int xbump = adam7XSubsampling[i + 1] - 1;
                int ybump = adam7YSubsampling[i + 1] - 1;

                if (i >= sourceMinProgressivePass) {
                    decodePass(i,
                               XOffset,
                               YOffset,
                               XSubsampling,                           
                               YSubsampling,
                               (width + xbump)/XSubsampling,
                               (height + ybump)/YSubsampling);
                } else {
                    skipPass((width + xbump)/XSubsampling,
                             (height + ybump)/YSubsampling);
                }

                // If read has been aborted, just return
                // processReadAborted will be called later
                if (abortRequested()) {
                    return;
                }
            }
        }
    }

    private void readImage(ImageReadParam param) throws IIOException {
        readMetadata();

        int width = metadata.IHDR_width;
        int height = metadata.IHDR_height;

        // Init default values
        sourceRegion = getSourceRegion(param, width, height);
        sourceXSubsampling = 1;
        sourceYSubsampling = 1;
        sourceMinProgressivePass = 0;
        sourceMaxProgressivePass = 6;
        sourceBands = null;
        destinationBands = null;
        destinationOffset = new Point(0, 0);

        // If an ImageReadParam is available, get values from it
        if (param != null) {
            sourceXSubsampling = param.getSourceXSubsampling();
            sourceYSubsampling = param.getSourceYSubsampling();

            sourceMinProgressivePass =
                Math.max(param.getSourceMinProgressivePass(), 0);
            sourceMaxProgressivePass =
                Math.min(param.getSourceMaxProgressivePass(), 6);

            sourceBands = param.getSourceBands();
            destinationBands = param.getDestinationBands();
            destinationOffset = param.getDestinationOffset();
        }

        try {
            stream.seek(imageStartPosition);

            Enumeration e = new PNGImageDataEnumeration(stream);
            InputStream is = new SequenceInputStream(e);
            is = new InflaterInputStream(is, new Inflater());
            is = new BufferedInputStream(is);
            this.pixelStream = new DataInputStream(is);

            theImage = getDestination(param,
                                      getImageTypes(0),
                                      width,
                                      height);

            // At this point the header has been read and we know
            // how many bands are in the image, so perform checking
            // of the read param.
            int colorType = metadata.IHDR_colorType;
            checkReadParamBandSettings(param,
                                       inputBandsForColorType[colorType],
                                      theImage.getSampleModel().getNumBands());

            processImageStarted(0);
            decodeImage();
            if (abortRequested()) {
                processReadAborted();
            } else {
                processImageComplete();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IIOException("Error reading PNG image data", e);
        }
    }

    public int getNumImages(boolean allowSearch) throws IIOException {
        if (stream == null) {
            throw new IllegalStateException("No input source set!");
        }
        if (seekForwardOnly && allowSearch) {
            throw new IllegalStateException
                ("seekForwardOnly and allowSearch can't both be true!");
        }
        return 1;
    }

    public int getWidth(int imageIndex) throws IIOException {
        readHeader();

        return metadata.IHDR_width;
    }

    public int getHeight(int imageIndex) throws IIOException {
        readHeader();

        return metadata.IHDR_height;
    }

    public Iterator getImageTypes(int imageIndex) throws IIOException {
        if (imageIndex != 0) {
            throw new IndexOutOfBoundsException("imageIndex != 0!");
        }

        readHeader();

        ArrayList l = new ArrayList(1); // List of ImageTypeSpecifiers
        ColorSpace rgb;
        ColorSpace gray;
        int[] bandOffsets;
        
        int bitDepth = metadata.IHDR_bitDepth;
        int colorType = metadata.IHDR_colorType;

        int dataType;
        if (bitDepth <= 8) {
            dataType = DataBuffer.TYPE_BYTE;
        } else {
            dataType = DataBuffer.TYPE_USHORT;
        }

        switch (colorType) {
        case PNG_COLOR_GRAY:
            // Packed grayscale
            l.add(ImageTypeSpecifier.createGrayscale(bitDepth,
                                                     dataType,
                                                     false));
            break;

        case PNG_COLOR_RGB:
            // Component R, G, B
            rgb = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            bandOffsets = new int[3];
            bandOffsets[0] = 0;
            bandOffsets[1] = 1;
            bandOffsets[2] = 2;
            l.add(ImageTypeSpecifier.createInterleaved(rgb,
                                                       bandOffsets,
                                                       dataType,
                                                       false,
                                                       false));
            break;

        case PNG_COLOR_PALETTE:
            readMetadata(); // Need tRNS chunk

            // Alpha from tRNS chunk may have fewer entries than
            // the RGB LUTs from the PLTE chunk; if so, pad with
            // 255.
            byte[] alpha = null;
            if (metadata.tRNS_present && (metadata.tRNS_alpha != null)) {
                if (metadata.tRNS_alpha.length == metadata.PLTE_red.length) {
                    alpha = metadata.tRNS_alpha;
                } else {
                    alpha = new byte[metadata.PLTE_red.length];
                    System.arraycopy(metadata.tRNS_alpha, 0,
                                     alpha, 0,
                                     metadata.tRNS_alpha.length);
                    Arrays.fill(alpha,
                                metadata.tRNS_alpha.length,
                                metadata.PLTE_red.length,
                                (byte)255);
                }
            }

            l.add(ImageTypeSpecifier.createIndexed(metadata.PLTE_red,
                                                   metadata.PLTE_green,
                                                   metadata.PLTE_blue,
                                                   alpha,
                                                   bitDepth,
                                                   DataBuffer.TYPE_BYTE));
            break;

        case PNG_COLOR_GRAY_ALPHA:
            // Component G, A
            gray = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            bandOffsets = new int[2];
            bandOffsets[0] = 0;
            bandOffsets[1] = 1;
            l.add(ImageTypeSpecifier.createInterleaved(gray,
                                                       bandOffsets,
                                                       dataType,
                                                       true,
                                                       false));
            break;

        case PNG_COLOR_RGB_ALPHA:
            // Component R, G, B, A (non-premultiplied)
            rgb = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            bandOffsets = new int[4];
            bandOffsets[0] = 0;
            bandOffsets[1] = 1;
            bandOffsets[2] = 2;
            bandOffsets[3] = 3;

            l.add(ImageTypeSpecifier.createInterleaved(rgb,
                                                       bandOffsets,
                                                       dataType,
                                                       true,
                                                       false));
            break;

        default:
            break;
        }

        return l.iterator();
    }

    public ImageReadParam getDefaultReadParam() {
        return new ImageReadParam();
    }

    public IIOMetadata getStreamMetadata() 
        throws IIOException {
        return null;
    }

    public IIOMetadata getImageMetadata(int imageIndex) throws IIOException {
        if (imageIndex != 0) {
            throw new IndexOutOfBoundsException("imageIndex != 0!");
        }
        readMetadata();
        return metadata;
    }
    
    public BufferedImage read(int imageIndex, ImageReadParam param)
        throws IIOException {
        if (imageIndex != 0) {
            throw new IndexOutOfBoundsException("imageIndex != 0!");
        }

        readImage(param);
        return theImage;
    }
    
    public void reset() {
        super.reset();
        resetStreamSettings();
    }

    private void resetStreamSettings() {
        gotHeader = false;
        gotMetadata = false;
        metadata = null;
        pixelStream = null;
    }
}
