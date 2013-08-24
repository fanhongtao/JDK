/*
 * @(#)Rotator3D.java	1.18 06/08/24
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)Rotator3D.java	1.18 06/08/24
 */

package java2d.demos.Colors;

import java.awt.*;
import java2d.AnimatingSurface;

import static java.lang.Math.*;


/**
 * 3D objects with color & lighting translated, rotated and scaled.
 */
public class Rotator3D extends AnimatingSurface {

    private Objects3D objs[] = new Objects3D[3];
    private static final int[][][] polygons = 
             { 
        // Solid cube   
              {{5, 1, 15, 13, 21, 23, 15}, 
               {5, 2, 21, 13, 19, 27, 21},
               {5, 3, 23, 15, 17, 25, 23}, 
               {5, 4, 19, 13, 15, 17, 19},
               {5, 5, 27, 21, 23, 25, 27},
               {5, 6, 27, 19, 17, 25, 27}},
        // Polygonal faces cube
              {{5, 1, 21, 13, 19, 27, 21},
               {5, 5, 23, 15, 17, 25, 23}, 
               {4,0,15,14,16,15}, {7,6,16,14,13,12,18,17,16}, {4,0,12,19,18,12},
               {4,2,22,21,20,22}, {7,0,24,23,22,20,27,26,24}, {4,2,24,26,25,24},
               {4, 3, 15, 13, 23, 15}, {4, 0, 23, 13, 21, 23}, 
               {5, 0, 27, 26, 18, 19, 27}, {5, 4, 25, 17, 18, 26, 25}}, 
        // Octahedron
              {{4, 3, 18, 21, 16, 18}, {4, 1, 20, 16, 18, 20}, 
               {4, 1, 18, 21, 16, 18}, {4, 3, 20, 17, 19, 20}, 
               {4, 2, 20, 26, 27, 20}, {5, 3, 26, 18, 16, 27, 26}, 
               {5, 0, 17, 24, 25, 19, 17}, {4, 3, 21, 25, 24, 21}, 
               {4, 4, 18, 21, 22, 18}, {4, 2, 22, 21, 17, 22}, 
               {4, 5, 20, 23, 16, 20}, {4, 1, 20, 23, 19, 20}, 
               {4, 6, 21, 23, 16, 21}, {4, 4, 21, 23, 19, 21}, 
               {4, 5, 20, 18, 22, 20}, {4, 6, 20, 22, 17, 20}}
             };
    private static final double[][][] points = 
           { 
        // Points for solid cube & polygonal faces cube
            {{ 1,  0,  0}, {-1,  0,  0}, { 0,  1,  0}, { 0, -1,  0}, { 0,  0,  1}, 
             { 0,  0, -1}, { 1,  0,  0}, {-1,  0,  0}, { 0,  1,  0}, { 0, -1,  0}, 
             { 0,  0,  1}, { 0,  0, -1}, { 1,  1,  0}, { 1,  1,  1}, { 0,  1,  1}, 
             {-1,  1,  1}, {-1,  1,  0}, {-1,  1, -1}, { 0,  1, -1}, { 1,  1, -1}, 
             { 1, -1,  0}, { 1, -1,  1}, { 0, -1,  1}, {-1, -1,  1}, {-1, -1,  0}, 
             {-1, -1, -1}, { 0, -1, -1}, { 1, -1, -1}},
        // Points for octahedron
            {{0, 0, 1}, {0, 0, -1}, {-0.8165, 0.4714, 0.33333},
             {0.8165, -0.4714, -0.33333}, {0.8165, 0.4714, 0.33333},
             {-0.8165, -0.4714, -0.33333}, {0, -0.9428, 0.3333},
             {0, 0.9428, -0.33333}, {0, 0, 1}, {0, 0, -1},
             {-0.8165, 0.4714, 0.33333}, {0.8165, -0.4714, -0.33333}, 
             {0.8165, 0.4714, 0.33333}, {-0.8165, -0.4714, -0.33333},  
             {0, -0.9428, 0.33333}, {0, 0.9428, -0.33333},  
             {-1.2247, -0.7071, 1}, {1.2247, 0.7071, -1}, 
             {0, 1.4142, 1}, {0, -1.4142, -1}, {-1.2247, 0.7071, -1}, 
             {1.2247, -0.7071, 1}, {0.61237, 1.06066, 0}, 
             {-0.61237, -1.06066, 0}, {1.2247, 0, 0}, 
             {0.61237, -1.06066, 0}, {-0.61237, 1.06066, 0}, {-1.2247, 0, 0}}
           }; 
    private static final int[][][] faces = 
           { 
         // Solid cube
             {{1, 1}, {1, 2}, {1, 3}, {1, 4}, {1, 0}, {1, 5}},
         // Polygonal faces cube
             {{1, 0}, {1, 1}, {3, 2, 3, 4}, {3, 5, 6, 7}, {2,8,9}, {2,10,11}},
         // Octahedron
             {{1, 2}, {1, 3}, {2, 4, 5}, {2, 6, 7}, {2, 8, 9}, 
              {2, 10, 11}, {2, 12, 13}, {2, 14, 15}},
           };


    public Rotator3D() {
        setBackground(Color.white);
    }


    public void reset(int w, int h) {
        objs[0] = new Objects3D(polygons[0], points[0], faces[0], w, h);
        objs[1] = new Objects3D(polygons[1], points[0], faces[1], w, h);
        objs[2] = new Objects3D(polygons[2], points[1], faces[2], w, h);
    }


    public void step(int w, int h) {
        for (Objects3D obj : objs) {
            if (obj != null) {
                obj.step(w, h);
            }
        }
    }


    public void render(int w, int h, Graphics2D g2) {
        for (Objects3D obj : objs) {
            if (obj != null) {
                obj.render(g2);
            }
        }
    }


    public static void main(String argv[]) {
        createDemoFrame(new Rotator3D());
    }


/**
 * 3D Objects : Solid Cube, Cube & Octahedron with polygonal faces.
 */
public class Objects3D {

    private final int UP = 0;
    private final int DOWN = 1;
    private int[][] polygons;
    private int npoly;
    private double[][] points;
    private int npoint;
    private int[][] faces;
    private int nface;
    private int ncolour = 10;
    private Color[][] colours = new Color[ncolour][7];
    private double[] lightvec = {0, 1, 1}; 
    private double Zeye = 10;
    private double angle;
    private Matrix3D orient, tmp, tmp2, tmp3;
    private int scaleDirection;
    private double scale, scaleAmt;
    private double ix = 3.0, iy = 3.0;
    private double[][] rotPts;
    private int[][] scrPts;
    private int xx[] = new int[20];
    private int yy[] = new int[20];
    private double x, y;
    private int p, j;
    private int colour;
    private double bounce, persp;

 
    public Objects3D(int[][] polygons, 
                     double[][] points, 
                     int[][] faces, 
                     int w, 
                     int h) {

        this.polygons = polygons;
        this.points = points;
        this.faces = faces;
        npoly = polygons.length;
        npoint = points.length;
        nface = faces.length;

        x = w * random();
        y = h * random();

        ix = random() > 0.5 ? ix : -ix;
        iy = random() > 0.5 ? iy : -iy;

        rotPts = new double[npoint][3];
        scrPts = new int[npoint][2];
                    
        for (int i = 0; i < ncolour; i++) {
            int x = 255 - (ncolour - i - 1)*100/ncolour;
            Color[] c = {
                new Color(x,x,x), // white
                new Color(x,0,0), // red
                new Color(0,x,0), // green
                new Color(0,0,x), // blue
                new Color(x,x,0), // yellow
                new Color(0,x,x), // cyan
                new Color(x,0,x)  // magenta
            };
            colours[i] = c;
        }

        double len = sqrt(lightvec[0]*lightvec[0] + 
                          lightvec[1]*lightvec[1] + 
                          lightvec[2]*lightvec[2]);
        lightvec[0] = lightvec[0]/len;
        lightvec[1] = lightvec[1]/len;
        lightvec[2] = lightvec[2]/len;

        double max = 0;
        for (int i = 0; i < npoint; i++) {
            len = sqrt(points[i][0]*points[i][0] + 
                       points[i][1]*points[i][1] +
                       points[i][2]*points[i][2]);
            if (len >max) {
                max = len;
            }
        }
   
        for (int i = 0; i < nface; i++) {
            len = sqrt(points[i][0]*points[i][0] + 
                       points[i][1]*points[i][1] +
                       points[i][2]*points[i][2]);
            points[i][0] = points[i][0]/len;
            points[i][1] = points[i][1]/len;
            points[i][2] = points[i][2]/len;
        }

        orient = new Matrix3D();
        tmp    = new Matrix3D();
        tmp2   = new Matrix3D();
        tmp3   = new Matrix3D();
        tmp.Rotation(2,0,PI/50);
        CalcScrPts((double) w/3,(double) h/3, 0);

        scale = min(w/3/max/1.2, h/3/max/1.2);
        scaleAmt = scale;
        scale *= random() * 1.5;
        scaleDirection = scaleAmt < scale ? DOWN : UP;
    }

 
    private Color getColour(int f,int index) {
        colour = (int)((rotPts[f][0]*lightvec[0] +
                        rotPts[f][1]*lightvec[1] +
                        rotPts[f][2]*lightvec[2])  * ncolour); 
        if (colour < 0) {
            colour = 0;
        }
        if (colour > ncolour-1) {
            colour = ncolour-1;
        }
        return colours[colour][polygons[faces[f][index]][1]];
    }
      

    private void CalcScrPts(double x, double y, double z) {
        for (p = 0; p < npoint; p++) {

            rotPts[p][2] =   points[p][0]*orient.M[2][0]
                           + points[p][1]*orient.M[2][1]
                           + points[p][2]*orient.M[2][2];

            rotPts[p][0] =   points[p][0]*orient.M[0][0]
                           + points[p][1]*orient.M[0][1]
                           + points[p][2]*orient.M[0][2];

            rotPts[p][1] = - points[p][0]*orient.M[1][0]
                           - points[p][1]*orient.M[1][1]
                           - points[p][2]*orient.M[1][2];
        }
        for (p = nface; p < npoint; p++) {
            rotPts[p][2] += z;
            persp = (Zeye - rotPts[p][2])/(scale*Zeye);
            scrPts[p][0] = (int)(rotPts[p][0]/persp+x);
            scrPts[p][1] = (int)(rotPts[p][1]/persp+y);
        }
    }


    private boolean faceUp(int f) {
        return (rotPts[f][0] * rotPts[nface+f][0] + 
                rotPts[f][1] * rotPts[nface+f][1] + 
                rotPts[f][2] *(rotPts[nface+f][2]-Zeye) < 0);
    }
    

    public void step(int w, int h) {
        x += ix;
        y += iy;
        if (x > w-scale) {
            x = w-scale-1;
            ix = -w/100 - 1;
        }
        if (x-scale < 0) {
            x = 2+scale;
            ix = w/100 + random()*3;
        }
        if (y > h-scale) {
            y = h-scale-2;
            iy = -h/100 - 1;
        }
        if (y-scale < 0) {
            y = 2+scale;
            iy = h/100 + random()*3;
        }

        angle += random() * 0.15;
        tmp3.Rotation(1, 2, angle);
        tmp2.Rotation(1, 0, angle*sqrt(2)/2);
        tmp.Rotation(0, 2, angle*PI/4);
        orient.M = tmp3.Times(tmp2.Times(tmp.M));
        bounce = abs(cos(0.5*(angle)))*2-1;

        if (scale > scaleAmt*1.4) {
            scaleDirection = DOWN;
        }
        if (scale < scaleAmt*0.4) {
            scaleDirection = UP;
        }
        if (scaleDirection == UP) {
            scale += random();
        }
        if (scaleDirection == DOWN) {
            scale -= random();
        }

        CalcScrPts(x, y, bounce);
    }


    public void render(Graphics2D g2) {
        for (int f = 0;f < nface; f++) {
            if (faceUp(f)) {
                for (j = 1;j < faces[f][0]+1; j++) {
                    DrawPoly(g2, faces[f][j], getColour(f,j));
                }
            }
        } 
    } 


    private void DrawPoly(Graphics2D g2, int poly, Color colour) {
        for (int p = 2; p < polygons[poly][0]+2; p++) {
            xx[p-2] = scrPts[polygons[poly][p]][0];
            yy[p-2] = scrPts[polygons[poly][p]][1];
        }
        g2.setColor(colour); 
        g2.fillPolygon(xx,yy,polygons[poly][0]);
        g2.setColor(Color.black); 
        g2.drawPolygon(xx,yy,polygons[poly][0]);
    }


/** 
 * A 3D matrix object.
 */
public class Matrix3D {

    public double[][] M = { { 1, 0, 0 },
                            { 0, 1, 0 },
                            { 0, 0, 1 } };
    private double[][] tmp = new double[3][3];
    private int row, col, k;
 
    public void Rotation(int i, int j, double angle) {
        for (row = 0; row < 3; row++) {
            for (col = 0; col <3; col++) {
                if (row != col) { 
                    M[row][col] = 0.0; 
                } else { 
                    M[row][col] = 1.0; 
                }
            }
        }
        M[i][i] =  cos(angle);
        M[j][j] =  cos(angle);
        M[i][j] =  sin(angle);
        M[j][i] = -sin(angle);
    }

    public double[][] Times(double[][] N) {
        for (row = 0; row < 3; row++) {
            for (col = 0; col < 3; col++) {
                tmp[row][col] = 0.0;
                for (k = 0; k < 3; k++) {
                    tmp[row][col] += M[row][k] * N[k][col];
                }
            }
        }
        return tmp;
    }

} // End Matrix3D

} // End Objects3D

} // End Rotator3D
