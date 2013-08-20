/*
 * @(#)TicTacToe.java	1.10 04/07/26
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
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
 * @(#)TicTacToe.java	1.10 04/07/26
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.*;
import java.applet.*;

/**
 * A TicTacToe applet. A very simple, and mostly brain-dead
 * implementation of your favorite game! <p>
 *
 * In this game a position is represented by a white and black
 * bitmask. A bit is set if a position is ocupied. There are
 * 9 squares so there are 1<<9 possible positions for each
 * side. An array of 1<<9 booleans is created, it marks
 * all the winning positions.
 *
 * @version 	1.2, 13 Oct 1995
 * @author Arthur van Hoff
 * @modified 04/23/96 Jim Hagen : winning sounds
 * @modified 02/10/98 Mike McCloskey : added destroy()
 */
public
class TicTacToe extends Applet implements MouseListener {
    /**
     * White's current position. The computer is white.
     */
    int white;

    /**
     * Black's current position. The user is black.
     */
    int black;

    /**
     * The squares in order of importance...
     */
    final static int moves[] = {4, 0, 2, 6, 8, 1, 3, 5, 7};

    /**
     * The winning positions.
     */
    static boolean won[] = new boolean[1 << 9];
    static final int DONE = (1 << 9) - 1;
    static final int OK = 0;
    static final int WIN = 1;
    static final int LOSE = 2;
    static final int STALEMATE = 3;

    /**
     * Mark all positions with these bits set as winning.
     */
    static void isWon(int pos) {
	for (int i = 0 ; i < DONE ; i++) {
	    if ((i & pos) == pos) {
		won[i] = true;
	    }
	}
    }

    /**
     * Initialize all winning positions.
     */
    static {
	isWon((1 << 0) | (1 << 1) | (1 << 2));
	isWon((1 << 3) | (1 << 4) | (1 << 5));
	isWon((1 << 6) | (1 << 7) | (1 << 8));
	isWon((1 << 0) | (1 << 3) | (1 << 6));
	isWon((1 << 1) | (1 << 4) | (1 << 7));
	isWon((1 << 2) | (1 << 5) | (1 << 8));
	isWon((1 << 0) | (1 << 4) | (1 << 8));
	isWon((1 << 2) | (1 << 4) | (1 << 6));
    }

    /**
     * Compute the best move for white.
     * @return the square to take
     */
    int bestMove(int white, int black) {
	int bestmove = -1;

      loop:
	for (int i = 0 ; i < 9 ; i++) {
	    int mw = moves[i];
	    if (((white & (1 << mw)) == 0) && ((black & (1 << mw)) == 0)) {
		int pw = white | (1 << mw);
		if (won[pw]) {
		    // white wins, take it!
		    return mw;
		}
		for (int mb = 0 ; mb < 9 ; mb++) {
		    if (((pw & (1 << mb)) == 0) && ((black & (1 << mb)) == 0)) {
			int pb = black | (1 << mb);
			if (won[pb]) {
			    // black wins, take another
			    continue loop;
			}
		    }
		}
		// Neither white nor black can win in one move, this will do.
		if (bestmove == -1) {
		    bestmove = mw;
		}
	    }
	}
	if (bestmove != -1) {
	    return bestmove;
	}

	// No move is totally satisfactory, try the first one that is open
	for (int i = 0 ; i < 9 ; i++) {
	    int mw = moves[i];
	    if (((white & (1 << mw)) == 0) && ((black & (1 << mw)) == 0)) {
		return mw;
	    }
	}

	// No more moves
	return -1;
    }

    /**
     * User move.
     * @return true if legal
     */
    boolean yourMove(int m) {
	if ((m < 0) || (m > 8)) {
	    return false;
	}
	if (((black | white) & (1 << m)) != 0) {
	    return false;
	}
	black |= 1 << m;
	return true;
    }

    /**
     * Computer move.
     * @return true if legal
     */
    boolean myMove() {
	if ((black | white) == DONE) {
	    return false;
	}
	int best = bestMove(white, black);
	white |= 1 << best;
	return true;
    }

    /**
     * Figure what the status of the game is.
     */
    int status() {
	if (won[white]) {
	    return WIN;
	}
	if (won[black]) {
	    return LOSE;
	}
	if ((black | white) == DONE) {
	    return STALEMATE;
	}
	return OK;
    }

    /**
     * Who goes first in the next game?
     */
    boolean first = true;

    /**
     * The image for white.
     */
    Image notImage;

    /**
     * The image for black.
     */
    Image crossImage;

    /**
     * Initialize the applet. Resize and load images.
     */
    public void init() {
	notImage = getImage(getCodeBase(), "images/not.gif");
	crossImage = getImage(getCodeBase(), "images/cross.gif");

	addMouseListener(this);
    }

    public void destroy() {
        removeMouseListener(this);
    }

    /**
     * Paint it.
     */
    public void paint(Graphics g) {
	Dimension d = getSize();
	g.setColor(Color.black);
	int xoff = d.width / 3;
	int yoff = d.height / 3;
	g.drawLine(xoff, 0, xoff, d.height);
	g.drawLine(2*xoff, 0, 2*xoff, d.height);
	g.drawLine(0, yoff, d.width, yoff);
	g.drawLine(0, 2*yoff, d.width, 2*yoff);

	int i = 0;
	for (int r = 0 ; r < 3 ; r++) {
	    for (int c = 0 ; c < 3 ; c++, i++) {
		if ((white & (1 << i)) != 0) {
		    g.drawImage(notImage, c*xoff + 1, r*yoff + 1, this);
		} else if ((black & (1 << i)) != 0) {
		    g.drawImage(crossImage, c*xoff + 1, r*yoff + 1, this);
		}
	    }
	}
    }

    /**
     * The user has clicked in the applet. Figure out where
     * and see if a legal move is possible. If it is a legal
     * move, respond with a legal move (if possible).
     */
    public void mouseReleased(MouseEvent e) {
	int x = e.getX();
	int y = e.getY();

	switch (status()) {
	  case WIN:
	  case LOSE:
	  case STALEMATE:
	    play(getCodeBase(), "audio/return.au");
	    white = black = 0;
	    if (first) {
		white |= 1 << (int)(Math.random() * 9);
	    }
	    first = !first;
	    repaint();
	    return;
	}

	// Figure out the row/column
	Dimension d = getSize();
	int c = (x * 3) / d.width;
	int r = (y * 3) / d.height;
	if (yourMove(c + r * 3)) {
	    repaint();

	    switch (status()) {
	      case WIN:
		play(getCodeBase(), "audio/yahoo1.au");
		break;
	      case LOSE:
		play(getCodeBase(), "audio/yahoo2.au");
		break;
	      case STALEMATE:
		break;
	      default:
		if (myMove()) {
		    repaint();
		    switch (status()) {
		      case WIN:
			play(getCodeBase(), "audio/yahoo1.au");
			break;
		      case LOSE:
			play(getCodeBase(), "audio/yahoo2.au");
			break;
		      case STALEMATE:
			break;
		      default:
			play(getCodeBase(), "audio/ding.au");
		    }
		} else {
		    play(getCodeBase(), "audio/beep.au");
		}
	    }
	} else {
	    play(getCodeBase(), "audio/beep.au");
	}
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public String getAppletInfo() {
	return "TicTacToe by Arthur van Hoff";
    }
}
