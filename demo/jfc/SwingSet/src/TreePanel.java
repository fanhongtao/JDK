/*
 * @(#)TreePanel.java	1.8 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.tree.*;


/**
 * Tree View!
 *
 * @version 1.8 01/11/29
 * @author Jeff Dinkins
 */
public class TreePanel extends JPanel 
{
    SwingSet swing;

    public TreePanel(SwingSet swing) {
	this.swing = swing;
	setLayout(new BorderLayout());

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Music");
        DefaultMutableTreeNode catagory;
	DefaultMutableTreeNode composer;
	DefaultMutableTreeNode style;
	DefaultMutableTreeNode album;

	// Classical
	catagory = new DefaultMutableTreeNode("Classical");
	top.add(catagory);

	// Beethoven
	catagory.add(composer = new DefaultMutableTreeNode("Beethoven"));
	composer.add(style = new DefaultMutableTreeNode("Concertos"));
	style.add(new DefaultMutableTreeNode("No. 1 - C Major"));
	style.add(new DefaultMutableTreeNode("No. 2 - B-Flat Major"));
	style.add(new DefaultMutableTreeNode("No. 3 - C Minor"));
	style.add(new DefaultMutableTreeNode("No. 4 - G Major"));
	style.add(new DefaultMutableTreeNode("No. 5 - E-Flat Major"));
	
	composer.add(style = new DefaultMutableTreeNode("Quartets"));
	style.add(new DefaultMutableTreeNode("Six String Quartets"));
	style.add(new DefaultMutableTreeNode("Three String Quartets"));
	style.add(new DefaultMutableTreeNode("Grosse Fugue for String Quartets"));
	
	composer.add(style = new DefaultMutableTreeNode("Sonatas"));
	style.add(new DefaultMutableTreeNode("Sonata in A Minor"));
	style.add(new DefaultMutableTreeNode("Sonata in F Major"));
	
	composer.add(style = new DefaultMutableTreeNode("Symphonies"));
	style.add(new DefaultMutableTreeNode("No. 1 - C Major"));
	style.add(new DefaultMutableTreeNode("No. 2 - D Major"));
	style.add(new DefaultMutableTreeNode("No. 3 - E-Flat Major"));
	style.add(new DefaultMutableTreeNode("No. 4 - B-Flat Major"));
	style.add(new DefaultMutableTreeNode("No. 5 - C Minor"));
	style.add(new DefaultMutableTreeNode("No. 6 - F Major"));
	style.add(new DefaultMutableTreeNode("No. 7 - A Major"));
	style.add(new DefaultMutableTreeNode("No. 8 - F Major"));
	style.add(new DefaultMutableTreeNode("No. 9 - D Minor"));
	
	// Brahms
	catagory.add(composer = new DefaultMutableTreeNode("Brahms"));
	composer.add(style = new DefaultMutableTreeNode("Concertos"));
	style.add(new DefaultMutableTreeNode("Violin Concerto"));
	style.add(new DefaultMutableTreeNode("Double Concerto - A Minor"));
	style.add(new DefaultMutableTreeNode("Piano Concerto No. 1 - D Minor"));
	style.add(new DefaultMutableTreeNode("Piano Concerto No. 2 - B-Flat Major"));
	
	composer.add(style = new DefaultMutableTreeNode("Quartets"));
	style.add(new DefaultMutableTreeNode("Piano Quartet No. 1 - G Minor"));
	style.add(new DefaultMutableTreeNode("Piano Quartet No. 2 - A Major"));
	style.add(new DefaultMutableTreeNode("Piano Quartet No. 3 - C Minor"));
	style.add(new DefaultMutableTreeNode("String Quartet No. 3 - B-Flat Minor"));
	
	composer.add(style = new DefaultMutableTreeNode("Sonatas"));
	style.add(new DefaultMutableTreeNode("Two Sonatas for Clarinet - F Minor"));
	style.add(new DefaultMutableTreeNode("Two Sonatas for Clarinet - E-Flat Major"));
	
	composer.add(style = new DefaultMutableTreeNode("Symphonies"));
	style.add(new DefaultMutableTreeNode("No. 1 - C Minor"));
	style.add(new DefaultMutableTreeNode("No. 2 - D Minor"));
	style.add(new DefaultMutableTreeNode("No. 3 - F Major"));
	style.add(new DefaultMutableTreeNode("No. 4 - E Minor"));
	
	// Mozart
	catagory.add(composer = new DefaultMutableTreeNode("Mozart"));
	composer.add(style = new DefaultMutableTreeNode("Concertos"));
	style.add(new DefaultMutableTreeNode("Piano Concerto No. 12"));
	style.add(new DefaultMutableTreeNode("Piano Concerto No. 17"));
	style.add(new DefaultMutableTreeNode("Clarinet Concerto"));
	style.add(new DefaultMutableTreeNode("Violin Concerto No. 5"));
	style.add(new DefaultMutableTreeNode("Violin Concerto No. 4"));

	composer.add(style = new DefaultMutableTreeNode("Sonatas"));
	style.add(new DefaultMutableTreeNode("String Quintet in G Minor"));
	style.add(new DefaultMutableTreeNode("Clarinet Quintet"));
	style.add(new DefaultMutableTreeNode("Piano Sonata No. 14"));

	composer.add(style = new DefaultMutableTreeNode("Symphonies"));
	style.add(new DefaultMutableTreeNode("No. 39"));
	style.add(new DefaultMutableTreeNode("No. 40"));
	style.add(new DefaultMutableTreeNode("No. 41"));
	
	catagory.add(composer = new DefaultMutableTreeNode("Schubert"));
	composer.add(style = new DefaultMutableTreeNode("Quartets"));
	style.add(new DefaultMutableTreeNode("No. 1 - D Major"));
	style.add(new DefaultMutableTreeNode("No. 2 - C Major"));
	style.add(new DefaultMutableTreeNode("No. 3 - B-Flat"));
	style.add(new DefaultMutableTreeNode("No. 4 - B Major,D Major"));
	style.add(new DefaultMutableTreeNode("No. 5 - B-Flat Major"));
	style.add(new DefaultMutableTreeNode("No. 6 - D Major"));
	style.add(new DefaultMutableTreeNode("No. 7 - D Major"));
	style.add(new DefaultMutableTreeNode("No. 8 - B-Flat Major"));
	style.add(new DefaultMutableTreeNode("No. 9 - G Minor,D Major"));
	style.add(new DefaultMutableTreeNode("No. 10 - E-Flat Major"));
	style.add(new DefaultMutableTreeNode("No. 11 - E,D"));
	style.add(new DefaultMutableTreeNode("No. 12 - C Minor,D"));

	composer.add(style = new DefaultMutableTreeNode("Sonatas"));
	style.add(new DefaultMutableTreeNode("No. 1 - E Major, D Major"));
	style.add(new DefaultMutableTreeNode("No. 2 - C Major, D Major"));
	style.add(new DefaultMutableTreeNode("No. 3 - E Major, D Major"));
	style.add(new DefaultMutableTreeNode("No. 4 - A Minor, D Major"));
	style.add(new DefaultMutableTreeNode("No. 6 - D Major"));
	style.add(new DefaultMutableTreeNode("No. 7 - E Minor, D Major"));
	style.add(new DefaultMutableTreeNode("No. 9 - B Major, D Major"));


	// Jazz
	top.add(catagory = new DefaultMutableTreeNode("Jazz"));

	// Ayler
	catagory.add(composer = new DefaultMutableTreeNode("Albert Ayler"));
	composer.add(album = new DefaultMutableTreeNode("My Name is Albert Ayler"));
	album.add(new DefaultMutableTreeNode("Bye Bye Blackbird"));
	album.add(new DefaultMutableTreeNode("Billie's Bounce"));
	album.add(new DefaultMutableTreeNode("Summertime"));
	album.add(new DefaultMutableTreeNode("On Green Dolphin Street"));
	album.add(new DefaultMutableTreeNode("C.T."));

	composer.add(album = new DefaultMutableTreeNode("Swing Low Seet Spiritual"));
	album.add(new DefaultMutableTreeNode("Goin' Home"));
	album.add(new DefaultMutableTreeNode("Old Man River"));
	album.add(new DefaultMutableTreeNode("When The Saints Go Marching In"));
	album.add(new DefaultMutableTreeNode("Deep River"));
	album.add(new DefaultMutableTreeNode("Down By The Riverside"));
	album.add(new DefaultMutableTreeNode("Spirits"));
	album.add(new DefaultMutableTreeNode("Witches and Devils"));
	album.add(new DefaultMutableTreeNode("Holy, Holy"));
	album.add(new DefaultMutableTreeNode("Saints"));

	composer.add(album = new DefaultMutableTreeNode("Prophesy"));
	album.add(new DefaultMutableTreeNode("Spirits"));
	album.add(new DefaultMutableTreeNode("Wizard"));
	album.add(new DefaultMutableTreeNode("Ghosts"));
	album.add(new DefaultMutableTreeNode("Prophecy"));

	composer.add(album = new DefaultMutableTreeNode("New Grass"));
	album.add(new DefaultMutableTreeNode("Free At Last"));
	album.add(new DefaultMutableTreeNode("Everybody's Movin'"));
	album.add(new DefaultMutableTreeNode("New Generation"));
	album.add(new DefaultMutableTreeNode("Heart Love"));
	album.add(new DefaultMutableTreeNode("Sun Watcher"));

	// Chet Baker
	catagory.add(composer = new DefaultMutableTreeNode("Chet Baker"));
	composer.add(album = new DefaultMutableTreeNode("Sings and Plays"));
	album.add(new DefaultMutableTreeNode("Let's Get Lost"));
	album.add(new DefaultMutableTreeNode("This Is Always"));
	album.add(new DefaultMutableTreeNode("Long Ago and Far Away"));
	album.add(new DefaultMutableTreeNode("I Wish I Knew"));
	album.add(new DefaultMutableTreeNode("Daybreak"));
	album.add(new DefaultMutableTreeNode("Grey December"));
	album.add(new DefaultMutableTreeNode("I Remember You"));

	composer.add(album = new DefaultMutableTreeNode("My Funny Valentine"));
	album.add(new DefaultMutableTreeNode("My Funny Valentine"));
	album.add(new DefaultMutableTreeNode("Someone To Watch Over Me"));
	album.add(new DefaultMutableTreeNode("Moonlight Becomes You"));
	album.add(new DefaultMutableTreeNode("I'm Glad There is You"));
	album.add(new DefaultMutableTreeNode("This is Always"));
	album.add(new DefaultMutableTreeNode("Time After Time"));
	album.add(new DefaultMutableTreeNode("Sweet Lorraine"));
	album.add(new DefaultMutableTreeNode("It's Always You"));
	album.add(new DefaultMutableTreeNode("Moon Love"));
	album.add(new DefaultMutableTreeNode("Like Someone In Love"));
	album.add(new DefaultMutableTreeNode("I've Never Been In Love Before"));
	album.add(new DefaultMutableTreeNode("Isn't it Romantic"));
	album.add(new DefaultMutableTreeNode("I Fall In Love Too Easily"));

	composer.add(album = new DefaultMutableTreeNode("Grey December"));
	album.add(new DefaultMutableTreeNode("Grey December"));
	album.add(new DefaultMutableTreeNode("I Wish I Knew"));
	album.add(new DefaultMutableTreeNode("Someone To Watch Over Me"));
	album.add(new DefaultMutableTreeNode("Headline"));
	album.add(new DefaultMutableTreeNode("Bockhanal"));
	album.add(new DefaultMutableTreeNode("A Dandy Line"));
	album.add(new DefaultMutableTreeNode("Pro Defunctus"));
	album.add(new DefaultMutableTreeNode("Little Old Lady"));
	album.add(new DefaultMutableTreeNode("Goodbye"));

	composer.add(album = new DefaultMutableTreeNode("The Route"));
	album.add(new DefaultMutableTreeNode("Tynan Time"));
	album.add(new DefaultMutableTreeNode("The Route"));
	album.add(new DefaultMutableTreeNode("Minor Yours"));
	album.add(new DefaultMutableTreeNode("Little Girl"));
	album.add(new DefaultMutableTreeNode("Ol' Croix"));
	album.add(new DefaultMutableTreeNode("The Great Lie"));
	album.add(new DefaultMutableTreeNode("Sweet Lorrain"));
	album.add(new DefaultMutableTreeNode("If I Should Lose You"));

	// Coltran
	catagory.add(composer = new DefaultMutableTreeNode("John Coltrane"));
	composer.add(album = new DefaultMutableTreeNode("Blue Train"));
	album.add(new DefaultMutableTreeNode("Blue Train"));
	album.add(new DefaultMutableTreeNode("Moment's Notice"));
	album.add(new DefaultMutableTreeNode("Locomotion"));
	album.add(new DefaultMutableTreeNode("I'm Old Fashioned"));
	album.add(new DefaultMutableTreeNode("Lazy Bird"));

	composer.add(album = new DefaultMutableTreeNode("Giant Steps"));
	album.add(new DefaultMutableTreeNode("Giant Steps"));
	album.add(new DefaultMutableTreeNode("Cousin Mary Steps"));
	album.add(new DefaultMutableTreeNode("Countdown"));
	album.add(new DefaultMutableTreeNode("Spiral"));
	album.add(new DefaultMutableTreeNode("Syeeda's Song Flute"));
	album.add(new DefaultMutableTreeNode("Naima"));
	album.add(new DefaultMutableTreeNode("Mr. P.C."));

	composer.add(album = new DefaultMutableTreeNode("My Favorite Things"));
	album.add(new DefaultMutableTreeNode("My Favorite Things"));
	album.add(new DefaultMutableTreeNode("Everytime We Say Goodbye"));
	album.add(new DefaultMutableTreeNode("Summertime"));
	album.add(new DefaultMutableTreeNode("But Not For Me"));

	composer.add(album = new DefaultMutableTreeNode("Crescent"));
	album.add(new DefaultMutableTreeNode("Crescent"));
	album.add(new DefaultMutableTreeNode("Wise One"));
	album.add(new DefaultMutableTreeNode("Bessie's Blues"));
	album.add(new DefaultMutableTreeNode("Lonnie's Lament"));
	album.add(new DefaultMutableTreeNode("The Drum Thing"));

	composer.add(album = new DefaultMutableTreeNode("Interstellar Space"));
	album.add(new DefaultMutableTreeNode("Mars"));
	album.add(new DefaultMutableTreeNode("Leo"));
	album.add(new DefaultMutableTreeNode("Venus"));
	album.add(new DefaultMutableTreeNode("Jupiter Variation"));
	album.add(new DefaultMutableTreeNode("Jupiter"));
	album.add(new DefaultMutableTreeNode("Saturn"));

	// Miles
	catagory.add(composer = new DefaultMutableTreeNode("Miles Davis"));
	composer.add(album = new DefaultMutableTreeNode("Transition"));
	album.add(new DefaultMutableTreeNode("Autumn Leaves"));
	album.add(new DefaultMutableTreeNode("Two Bass Hit"));
	album.add(new DefaultMutableTreeNode("Love, I've Found You"));
	album.add(new DefaultMutableTreeNode("I Thought About You"));
	album.add(new DefaultMutableTreeNode("All Blues"));
	album.add(new DefaultMutableTreeNode("Seven Steps To Heaven"));

	composer.add(album = new DefaultMutableTreeNode("Quiet Nights"));
	album.add(new DefaultMutableTreeNode("Once Upon a Summertime"));
	album.add(new DefaultMutableTreeNode("Aos Pes Da Cruz"));
	album.add(new DefaultMutableTreeNode("Wait Till You See Her"));
	album.add(new DefaultMutableTreeNode("Corcovado"));
	album.add(new DefaultMutableTreeNode("Summer Nights"));
	
	composer.add(album = new DefaultMutableTreeNode("My Funny Valentine"));
	album.add(new DefaultMutableTreeNode("All of You"));
	album.add(new DefaultMutableTreeNode("Stella By Starlight"));
	album.add(new DefaultMutableTreeNode("All Blues"));
	album.add(new DefaultMutableTreeNode("I Thought About You"));
	
	composer.add(album = new DefaultMutableTreeNode("Voodoo Down"));
	album.add(new DefaultMutableTreeNode("Automn Leaves"));
	album.add(new DefaultMutableTreeNode("Footprints"));
	album.add(new DefaultMutableTreeNode("Directions"));
	album.add(new DefaultMutableTreeNode("Bitches Brew"));
	album.add(new DefaultMutableTreeNode("Hush"));
	
	// Rock
	top.add(catagory = new DefaultMutableTreeNode("Rock"));

	// The Beatles
	catagory.add(composer = new DefaultMutableTreeNode("The Beatles"));
	composer.add(album = new DefaultMutableTreeNode("A Hard Day's Night"));
	album.add(new DefaultMutableTreeNode("A Hard Day's Night"));
	album.add(new DefaultMutableTreeNode("I Should Have Known Better")); 
	album.add(new DefaultMutableTreeNode("If I Fell")); 
	album.add(new DefaultMutableTreeNode("I'm Happy Just To Dance With You")); 
	album.add(new DefaultMutableTreeNode("And I Love Her")); 
	album.add(new DefaultMutableTreeNode("Tell Me Why")); 
	album.add(new DefaultMutableTreeNode("Can't Buy Me Love")); 
	album.add(new DefaultMutableTreeNode("Any Time At All")); 
	album.add(new DefaultMutableTreeNode("I'll Cry Instead")); 
	album.add(new DefaultMutableTreeNode("Things We Said Today")); 
	album.add(new DefaultMutableTreeNode("When I Get Home")); 
	album.add(new DefaultMutableTreeNode("You Can't Do That")); 
	
	composer.add(album = new DefaultMutableTreeNode("Beatles For Sale"));
	album.add(new DefaultMutableTreeNode("No Reply")); 
	album.add(new DefaultMutableTreeNode("I'm a Loser")); 
	album.add(new DefaultMutableTreeNode("Baby's In Black")); 
	album.add(new DefaultMutableTreeNode("Rock And Roll Music")); 
	album.add(new DefaultMutableTreeNode("I'll Follow the Sun")); 
	album.add(new DefaultMutableTreeNode("Mr. Moonlight")); 
	album.add(new DefaultMutableTreeNode("Kansas City/Hey Hey Hey Hey")); 
	album.add(new DefaultMutableTreeNode("Eight Days a Week")); 
	album.add(new DefaultMutableTreeNode("Words Of Love")); 
	album.add(new DefaultMutableTreeNode("Honey Don't")); 
	album.add(new DefaultMutableTreeNode("Every Little Thing")); 
	album.add(new DefaultMutableTreeNode("I Don't Want To Spoil the Party")); 
	album.add(new DefaultMutableTreeNode("What You're Doing")); 
	album.add(new DefaultMutableTreeNode("Everybody's Trying To Be My Baby")); 

	composer.add(album = new DefaultMutableTreeNode("Help"));
	album.add(new DefaultMutableTreeNode("Help!")); 
	album.add(new DefaultMutableTreeNode("The Night Before")); 
	album.add(new DefaultMutableTreeNode("You've Got To Hide Your Love Away")); 
	album.add(new DefaultMutableTreeNode("I Need You")); 
	album.add(new DefaultMutableTreeNode("Another Girl")); 
	album.add(new DefaultMutableTreeNode("You're Going To Lose That Girl")); 
	album.add(new DefaultMutableTreeNode("Ticket To Ride")); 
	album.add(new DefaultMutableTreeNode("Act Naturally")); 
	album.add(new DefaultMutableTreeNode("It's Only Love")); 
	album.add(new DefaultMutableTreeNode("You Like Me Too Much")); 
	album.add(new DefaultMutableTreeNode("Tell Me What You See")); 
	album.add(new DefaultMutableTreeNode("I've Just Seen a Face")); 
	album.add(new DefaultMutableTreeNode("Yesterday")); 
	album.add(new DefaultMutableTreeNode("Dizzy Miss Lizzie")); 
	
	composer.add(album = new DefaultMutableTreeNode("Rubber Soul"));
	album.add(new DefaultMutableTreeNode("Drive My Car")); 
      	album.add(new DefaultMutableTreeNode("Norwegian Wood"));
      	album.add(new DefaultMutableTreeNode("You Won't See Me"));
      	album.add(new DefaultMutableTreeNode("Nowhere Man"));
      	album.add(new DefaultMutableTreeNode("Think For Yourself"));
      	album.add(new DefaultMutableTreeNode("The Word"));
      	album.add(new DefaultMutableTreeNode("Michelle"));
      	album.add(new DefaultMutableTreeNode("What Goes On?"));
      	album.add(new DefaultMutableTreeNode("Girl")); 
      	album.add(new DefaultMutableTreeNode("I'm Looking Through You")); 
      	album.add(new DefaultMutableTreeNode("In My Life")); 
      	album.add(new DefaultMutableTreeNode("Wait")); 
      	album.add(new DefaultMutableTreeNode("If I Needed Someone")); 
      	album.add(new DefaultMutableTreeNode("Run For Your Life")); 

	composer.add(album = new DefaultMutableTreeNode("Revolver"));
	album.add(new DefaultMutableTreeNode("Taxman")); 
	album.add(new DefaultMutableTreeNode("Rigby")); 
	album.add(new DefaultMutableTreeNode("I'm Only Sleeping")); 
	album.add(new DefaultMutableTreeNode("For You To")); 
	album.add(new DefaultMutableTreeNode("Here There And Everywhere")); 
	album.add(new DefaultMutableTreeNode("Yellow Submarine"));
	album.add(new DefaultMutableTreeNode("She Said She Said")); 
	album.add(new DefaultMutableTreeNode("Good Day Sunshine")); 
	album.add(new DefaultMutableTreeNode("And Your Bird Can Sing")); 
	album.add(new DefaultMutableTreeNode("For No One")); 
	album.add(new DefaultMutableTreeNode("Doctor Robert")); 
	album.add(new DefaultMutableTreeNode("I Want To Tell You")); 
	album.add(new DefaultMutableTreeNode("Got To Get You Into My Life")); 
	album.add(new DefaultMutableTreeNode("Tomorrow Never Knows")); 

	composer.add(album = new DefaultMutableTreeNode("Sgt. Pepper's"));
	album.add(new DefaultMutableTreeNode("Sgt. Pepper's"));
	album.add(new DefaultMutableTreeNode("With a Little Help From My Friends"));
	album.add(new DefaultMutableTreeNode("Lucy in the Sky With Diamonds"));
	album.add(new DefaultMutableTreeNode("Getting Better"));
	album.add(new DefaultMutableTreeNode("Fixing a Hole"));
	album.add(new DefaultMutableTreeNode("She's Leaving Home"));
	album.add(new DefaultMutableTreeNode("Being For the Benefit of Mr. Kite"));
	album.add(new DefaultMutableTreeNode("Within You Without You"));
	album.add(new DefaultMutableTreeNode("When I'm Sixty Four"));
	album.add(new DefaultMutableTreeNode("Lovely Rita"));
	album.add(new DefaultMutableTreeNode("Good Morning"));
	album.add(new DefaultMutableTreeNode("Sgt. Pepper's Reprise"));
	album.add(new DefaultMutableTreeNode("A Day In The Life"));

	composer.add(album = new DefaultMutableTreeNode("Magical Mystery Tour"));
	album.add(new DefaultMutableTreeNode("Magical Mystery Tour"));
	album.add(new DefaultMutableTreeNode("Fool on the Hill"));
	album.add(new DefaultMutableTreeNode("Flying"));
	album.add(new DefaultMutableTreeNode("Blue Jay Way"));
	album.add(new DefaultMutableTreeNode("Your Mother Should Know"));
	album.add(new DefaultMutableTreeNode("I Am The Walrus"));
	album.add(new DefaultMutableTreeNode("Hello Goodbye"));
	album.add(new DefaultMutableTreeNode("Strawberry Fields Forever"));
	album.add(new DefaultMutableTreeNode("Penny Lane"));
	album.add(new DefaultMutableTreeNode("Baby You're a Rich Man"));
	album.add(new DefaultMutableTreeNode("All You Need Is Love"));

	composer.add(album = new DefaultMutableTreeNode("The White Album"));
	album.add(new DefaultMutableTreeNode("Back in the USSR"));
	album.add(new DefaultMutableTreeNode("Dear Prudence"));
	album.add(new DefaultMutableTreeNode("Glass Onion"));
	album.add(new DefaultMutableTreeNode("Wild Honey Pie"));
	album.add(new DefaultMutableTreeNode("Bungalow Bill"));
	album.add(new DefaultMutableTreeNode("While My Guitar Gently Weeps"));
	album.add(new DefaultMutableTreeNode("Martha My Dear"));
	album.add(new DefaultMutableTreeNode("I'm So Tired"));
	album.add(new DefaultMutableTreeNode("Blackbird"));
	album.add(new DefaultMutableTreeNode("Piggies"));
	album.add(new DefaultMutableTreeNode("Rocky Raccoon"));
	album.add(new DefaultMutableTreeNode("Don't Pass Me By"));
	album.add(new DefaultMutableTreeNode("Why Don't We Do It In The Road"));
	album.add(new DefaultMutableTreeNode("I Will"));
	album.add(new DefaultMutableTreeNode("Julia"));
	album.add(new DefaultMutableTreeNode("Birthday"));
	album.add(new DefaultMutableTreeNode("Yer Blues"));
	album.add(new DefaultMutableTreeNode("Mother Nature's Son"));
	album.add(new DefaultMutableTreeNode("Sexy Sadie"));
	album.add(new DefaultMutableTreeNode("Helter Skelter"));
	album.add(new DefaultMutableTreeNode("Long Long Long"));
	album.add(new DefaultMutableTreeNode("Revolution 1"));
	album.add(new DefaultMutableTreeNode("Honey Pie"));
	album.add(new DefaultMutableTreeNode("Savoy Truffle"));
	album.add(new DefaultMutableTreeNode("Cry Baby Cry"));
	album.add(new DefaultMutableTreeNode("Revolution 9"));
	album.add(new DefaultMutableTreeNode("Good Night"));

	composer.add(album = new DefaultMutableTreeNode("Abbey Road"));
	album.add(new DefaultMutableTreeNode("Come Together"));
	album.add(new DefaultMutableTreeNode("Something"));
	album.add(new DefaultMutableTreeNode("Maxwell's Silver Hammer"));
	album.add(new DefaultMutableTreeNode("Octopus's Garden"));
	album.add(new DefaultMutableTreeNode("She's So Heavy"));
	album.add(new DefaultMutableTreeNode("Here Comes The Sun"));
	album.add(new DefaultMutableTreeNode("Because"));
	album.add(new DefaultMutableTreeNode("You Never Give Me Your Money"));
	album.add(new DefaultMutableTreeNode("Sun King"));
	album.add(new DefaultMutableTreeNode("Mean Mr. Mustard"));
	album.add(new DefaultMutableTreeNode("Polythene Pam"));
	album.add(new DefaultMutableTreeNode("She Came In Through The Bathroom Window"));
	album.add(new DefaultMutableTreeNode("Golden Slumbers"));
	album.add(new DefaultMutableTreeNode("Carry That Weight"));
	album.add(new DefaultMutableTreeNode("The End"));
	album.add(new DefaultMutableTreeNode("Her Majesty"));

	composer.add(album = new DefaultMutableTreeNode("Let It Be"));
	album.add(new DefaultMutableTreeNode("Two of Us"));
	album.add(new DefaultMutableTreeNode("Dig A Pony"));
	album.add(new DefaultMutableTreeNode("Across the Universe"));
	album.add(new DefaultMutableTreeNode("I Me Mine"));
	album.add(new DefaultMutableTreeNode("Dig It"));
	album.add(new DefaultMutableTreeNode("Let It Be"));
	album.add(new DefaultMutableTreeNode("Maggie Mae"));
	album.add(new DefaultMutableTreeNode("I've Got A Feeling"));
	album.add(new DefaultMutableTreeNode("One After 909"));
	album.add(new DefaultMutableTreeNode("The Long and Winding Road"));
	album.add(new DefaultMutableTreeNode("For You Blue"));
	album.add(new DefaultMutableTreeNode("Get Back"));

	// Crowded House
	catagory.add(composer = new DefaultMutableTreeNode("Crowded House"));
	composer.add(album = new DefaultMutableTreeNode("Crowded House"));
	album.add(new DefaultMutableTreeNode("Mean To Me"));
	album.add(new DefaultMutableTreeNode("World Where You Live"));
	album.add(new DefaultMutableTreeNode("Now We're Getting Somewhere"));
	album.add(new DefaultMutableTreeNode("Don't Dream It's Over"));
	album.add(new DefaultMutableTreeNode("Love You Til The Day I Die"));
	album.add(new DefaultMutableTreeNode("Something So Strong"));
	album.add(new DefaultMutableTreeNode("Hole In The River"));
	album.add(new DefaultMutableTreeNode("Can't Carry On"));
	album.add(new DefaultMutableTreeNode("I Walk Away"));
	album.add(new DefaultMutableTreeNode("Tombstone"));
	album.add(new DefaultMutableTreeNode("That's What I Call Live"));

	composer.add(album = new DefaultMutableTreeNode("Temple of Low Men"));
	album.add(new DefaultMutableTreeNode("I Feel Possessed"));
	album.add(new DefaultMutableTreeNode("Kill Eye"));
	album.add(new DefaultMutableTreeNode("Into Temptation"));
	album.add(new DefaultMutableTreeNode("Mansion In The Slums"));
	album.add(new DefaultMutableTreeNode("When You Come"));
	album.add(new DefaultMutableTreeNode("Never Be The Same"));
	album.add(new DefaultMutableTreeNode("Love This Life"));
	album.add(new DefaultMutableTreeNode("Sister Madly"));
	album.add(new DefaultMutableTreeNode("In The Lowlands"));
	album.add(new DefaultMutableTreeNode("Better Be Home Soon"));

	composer.add(album = new DefaultMutableTreeNode("Woodface"));
	album.add(new DefaultMutableTreeNode("Chocolate Cake"));
	album.add(new DefaultMutableTreeNode("It's Only Natural"));
	album.add(new DefaultMutableTreeNode("Fall At Your Feet"));
	album.add(new DefaultMutableTreeNode("Tall Trees"));
	album.add(new DefaultMutableTreeNode("Weather With You"));
	album.add(new DefaultMutableTreeNode("Whispers and Moans"));
	album.add(new DefaultMutableTreeNode("Four Seasons in One Day"));
	album.add(new DefaultMutableTreeNode("There Goes God"));
	album.add(new DefaultMutableTreeNode("Fame Is"));
	album.add(new DefaultMutableTreeNode("All I Ask"));
	album.add(new DefaultMutableTreeNode("As Sure As I Am"));
	album.add(new DefaultMutableTreeNode("Italian Plastic"));
	album.add(new DefaultMutableTreeNode("She Goes On"));
	album.add(new DefaultMutableTreeNode("How Will You Go"));

	composer.add(album = new DefaultMutableTreeNode("Together Alone"));
	album.add(new DefaultMutableTreeNode("Kare Kare"));
	album.add(new DefaultMutableTreeNode("In My Command"));
	album.add(new DefaultMutableTreeNode("Nails In My Feet"));
	album.add(new DefaultMutableTreeNode("Black & White Boy"));
	album.add(new DefaultMutableTreeNode("Fingers of Love"));
	album.add(new DefaultMutableTreeNode("Pineapple Head"));
	album.add(new DefaultMutableTreeNode("Locked Out"));
	album.add(new DefaultMutableTreeNode("Private Universe"));
	album.add(new DefaultMutableTreeNode("Walking on the Spot"));
	album.add(new DefaultMutableTreeNode("Distant Sun"));
	album.add(new DefaultMutableTreeNode("Catherine Wheels"));
	album.add(new DefaultMutableTreeNode("Skin Feeling"));
	album.add(new DefaultMutableTreeNode("Together Alone"));

	// Harvin Garvel
	catagory.add(composer = new DefaultMutableTreeNode("Harvin Garvel"));
	composer.add(album = new DefaultMutableTreeNode("Harven Garvel I"));
	album.add(new DefaultMutableTreeNode("Body"));
	album.add(new DefaultMutableTreeNode("What You Said"));
	album.add(new DefaultMutableTreeNode("All Rights Reserved"));
	album.add(new DefaultMutableTreeNode("High Purity"));
	album.add(new DefaultMutableTreeNode("Lies"));
	album.add(new DefaultMutableTreeNode("Get Real"));
	album.add(new DefaultMutableTreeNode("Gradma Cries"));
	album.add(new DefaultMutableTreeNode("First Feel"));
	album.add(new DefaultMutableTreeNode("Somethings wrong"));
	album.add(new DefaultMutableTreeNode("Shoes"));
	album.add(new DefaultMutableTreeNode("Spice Rack"));
	album.add(new DefaultMutableTreeNode("Dark Feel"));
	album.add(new DefaultMutableTreeNode("Tug of War"));
	album.add(new DefaultMutableTreeNode("Ant Song"));

	composer.add(album = new DefaultMutableTreeNode("Harven Garvel II"));
	album.add(new DefaultMutableTreeNode("We Ain't Through"));
	album.add(new DefaultMutableTreeNode("Trash and Spend"));
	album.add(new DefaultMutableTreeNode("Kick"));
	album.add(new DefaultMutableTreeNode("The Garden"));
	album.add(new DefaultMutableTreeNode("One & Only"));
	album.add(new DefaultMutableTreeNode("Squid Frenzy"));
	album.add(new DefaultMutableTreeNode("Soul In Soul"));
	album.add(new DefaultMutableTreeNode("The Desert"));
	album.add(new DefaultMutableTreeNode("He Grew Up"));
	album.add(new DefaultMutableTreeNode("Talk"));
	album.add(new DefaultMutableTreeNode("Image"));
	album.add(new DefaultMutableTreeNode("Tomorrow"));
	album.add(new DefaultMutableTreeNode("R70"));

	composer.add(album = new DefaultMutableTreeNode("Full Grown Dog"));
	album.add(new DefaultMutableTreeNode("I Am"));
	album.add(new DefaultMutableTreeNode("Say"));
	album.add(new DefaultMutableTreeNode("Is This Real"));
	album.add(new DefaultMutableTreeNode("What She Said"));
	album.add(new DefaultMutableTreeNode("Mirror Lies"));
	album.add(new DefaultMutableTreeNode("Girls"));
	album.add(new DefaultMutableTreeNode("Your Will"));
	album.add(new DefaultMutableTreeNode("Slow Motion Sunday"));
	album.add(new DefaultMutableTreeNode("Simple Life"));
	album.add(new DefaultMutableTreeNode("The Road Song"));
	album.add(new DefaultMutableTreeNode("The Same Way"));
	album.add(new DefaultMutableTreeNode("Stop Tryin"));

	composer.add(album = new DefaultMutableTreeNode("Persia"));
	album.add(new DefaultMutableTreeNode("Exonic"));
	album.add(new DefaultMutableTreeNode("Drift"));
	album.add(new DefaultMutableTreeNode("Cruise"));
	album.add(new DefaultMutableTreeNode("MugWump"));
	album.add(new DefaultMutableTreeNode("Smear"));
	album.add(new DefaultMutableTreeNode("Everything"));
	album.add(new DefaultMutableTreeNode("Keep"));
	album.add(new DefaultMutableTreeNode("Circle"));

	composer.add(album = new DefaultMutableTreeNode("Sensative Beak"));
	album.add(new DefaultMutableTreeNode("Whatcha Gotta Do"));
	album.add(new DefaultMutableTreeNode("Somewhere In This World"));
	album.add(new DefaultMutableTreeNode("Awaken"));
	album.add(new DefaultMutableTreeNode("Just A Dog"));
	album.add(new DefaultMutableTreeNode("I Can Dance"));
	album.add(new DefaultMutableTreeNode("Tomorrow"));
	album.add(new DefaultMutableTreeNode("Love Who?"));
	album.add(new DefaultMutableTreeNode("Is There Something"));
	album.add(new DefaultMutableTreeNode("I Like It"));
	album.add(new DefaultMutableTreeNode("Easy Chair"));
	album.add(new DefaultMutableTreeNode("As We Are One"));
	album.add(new DefaultMutableTreeNode("Far Away"));
	album.add(new DefaultMutableTreeNode("Leaving Science"));
	album.add(new DefaultMutableTreeNode("What A Life"));

	// The Steve Miller Band
	catagory.add(composer = new DefaultMutableTreeNode("Steve Miller Band"));
	composer.add(album = new DefaultMutableTreeNode("Circle Of Love"));
	album.add(new DefaultMutableTreeNode("Heart Like A Wheel"));
	album.add(new DefaultMutableTreeNode("Get On Home"));
	album.add(new DefaultMutableTreeNode("Baby Wanna Dance"));
	album.add(new DefaultMutableTreeNode("Circle Of Love"));
	album.add(new DefaultMutableTreeNode("Macho City"));

	composer.add(album = new DefaultMutableTreeNode("Fly Like An Eagle"));
	album.add(new DefaultMutableTreeNode("Space Intro"));
	album.add(new DefaultMutableTreeNode("Fly Like An Eagle"));
	album.add(new DefaultMutableTreeNode("Wild Mountain Honey"));
	album.add(new DefaultMutableTreeNode("Serenade"));
	album.add(new DefaultMutableTreeNode("Dance, Dance, Dance"));
	album.add(new DefaultMutableTreeNode("Mercury Blues"));
	album.add(new DefaultMutableTreeNode("Take the Money and Run"));
	album.add(new DefaultMutableTreeNode("Rockin' Me"));
	album.add(new DefaultMutableTreeNode("You Send Me"));
	album.add(new DefaultMutableTreeNode("Blue Odyssey"));
	album.add(new DefaultMutableTreeNode("Sweet Maree"));
	album.add(new DefaultMutableTreeNode("The Window"));

	composer.add(album = new DefaultMutableTreeNode("Book Of Dreams"));
	album.add(new DefaultMutableTreeNode("Threshold"));
	album.add(new DefaultMutableTreeNode("Jet Airliner"));
	album.add(new DefaultMutableTreeNode("Winter Time"));
	album.add(new DefaultMutableTreeNode("Swingtown"));
	album.add(new DefaultMutableTreeNode("True Fine Love"));
	album.add(new DefaultMutableTreeNode("Wish Upon A Star"));
	album.add(new DefaultMutableTreeNode("Jungle Love"));
	album.add(new DefaultMutableTreeNode("Electrolux Imbroglio"));
	album.add(new DefaultMutableTreeNode("Sacrifice"));
	album.add(new DefaultMutableTreeNode("The Stake"));
	album.add(new DefaultMutableTreeNode("My Own Space"));
	album.add(new DefaultMutableTreeNode("Babes In The Wood"));

	composer.add(album = new DefaultMutableTreeNode("Joker"));
	album.add(new DefaultMutableTreeNode("Sugar, Babe"));
	album.add(new DefaultMutableTreeNode("Mary Lou"));
	album.add(new DefaultMutableTreeNode("Shu Ba Da Du Ma"));
	album.add(new DefaultMutableTreeNode("Your Cash Ain't Nothin' But Trash"));
	album.add(new DefaultMutableTreeNode("The Joker"));
	album.add(new DefaultMutableTreeNode("The Lovin' Cup"));
	album.add(new DefaultMutableTreeNode("Come On In My Kitchen"));
	album.add(new DefaultMutableTreeNode("Evil"));
	album.add(new DefaultMutableTreeNode("Something To Believe In"));

	JTree tree = new JTree(top);
	add(new JScrollPane(tree), BorderLayout.CENTER);
    }

}
