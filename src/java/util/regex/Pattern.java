/*
 * @(#)Pattern.java	1.95 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.regex;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.CharacterIterator;
import sun.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A compiled representation of a regular expression.
 *
 * <p> A regular expression, specified as a string, must first be compiled into
 * an instance of this class.  The resulting pattern can then be used to create
 * a {@link Matcher} object that can match arbitrary {@link
 * java.lang.CharSequence </code>character sequences<code>} against the regular
 * expression.  All of the state involved in performing a match resides in the
 * matcher, so many matchers can share the same pattern.
 *
 * <p> A typical invocation sequence is thus
 *
 * <blockquote><pre>
 * Pattern p = Pattern.{@link #compile compile}("a*b");
 * Matcher m = p.{@link #matcher matcher}("aaaaab");
 * boolean b = m.{@link Matcher#matches matches}();</pre></blockquote>
 *
 * <p> A {@link #matches matches} method is defined by this class as a
 * convenience for when a regular expression is used just once.  This method
 * compiles an expression and matches an input sequence against it in a single
 * invocation.  The statement
 *
 * <blockquote><pre>
 * boolean b = Pattern.matches("a*b", "aaaaab");</pre></blockquote>
 *
 * is equivalent to the three statements above, though for repeated matches it
 * is less efficient since it does not allow the compiled pattern to be reused.
 *
 * <p> Instances of this class are immutable and are safe for use by multiple
 * concurrent threads.  Instances of the {@link Matcher} class are not safe for
 * such use.
 *
 *
 * <a name="sum">
 * <h4> Summary of regular-expression constructs </h4>
 *
 * <table border="0" cellpadding="1" cellspacing="0" 
 *  summary="Regular expression constructs, and what they match">
 *
 * <tr align="left">
 * <th bgcolor="#CCCCFF" align="left" id="construct">Construct</th>
 * <th bgcolor="#CCCCFF" align="left" id="matches">Matches</th>
 * </tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="2" id="characters">Characters</th></tr>
 *
 * <tr><td valign="top" headers="construct characters"><i>x</i></td>
 *     <td headers="matches">The character <i>x</i></td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\\</tt></td>
 *     <td headers="matches">The backslash character</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\0</tt><i>n</i></td>
 *     <td headers="matches">The character with octal value <tt>0</tt><i>n</i>
 *         (0&nbsp;<tt>&lt;=</tt>&nbsp;<i>n</i>&nbsp;<tt>&lt;=</tt>&nbsp;7)</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\0</tt><i>nn</i></td>
 *     <td headers="matches">The character with octal value <tt>0</tt><i>nn</i>
 *         (0&nbsp;<tt>&lt;=</tt>&nbsp;<i>n</i>&nbsp;<tt>&lt;=</tt>&nbsp;7)</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\0</tt><i>mnn</i></td>
 *     <td headers="matches">The character with octal value <tt>0</tt><i>mnn</i>
 *         (0&nbsp;<tt>&lt;=</tt>&nbsp;<i>m</i>&nbsp;<tt>&lt;=</tt>&nbsp;3,
 *         0&nbsp;<tt>&lt;=</tt>&nbsp;<i>n</i>&nbsp;<tt>&lt;=</tt>&nbsp;7)</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\x</tt><i>hh</i></td>
 *     <td headers="matches">The character with hexadecimal&nbsp;value&nbsp;<tt>0x</tt><i>hh</i></td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>&#92;u</tt><i>hhhh</i></td>
 *     <td headers="matches">The character with hexadecimal&nbsp;value&nbsp;<tt>0x</tt><i>hhhh</i></td></tr>
 * <tr><td valign="top" headers="matches"><tt>\t</tt></td>
 *     <td headers="matches">The tab character (<tt>'&#92;u0009'</tt>)</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\n</tt></td>
 *     <td headers="matches">The newline (line feed) character (<tt>'&#92;u000A'</tt>)</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\r</tt></td>
 *     <td headers="matches">The carriage-return character (<tt>'&#92;u000D'</tt>)</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\f</tt></td>
 *     <td headers="matches">The form-feed character (<tt>'&#92;u000C'</tt>)</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\a</tt></td>
 *     <td headers="matches">The alert (bell) character (<tt>'&#92;u0007'</tt>)</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\e</tt></td>
 *     <td headers="matches">The escape character (<tt>'&#92;u001B'</tt>)</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\c</tt><i>x</i></td>
 *     <td headers="matches">The control character corresponding to <i>x</i></td></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="2" id="classes">Character classes</th></tr>
 *
 * <tr><td valign="top" headers="construct classes"><tt>[abc]</tt></td>
 *     <td headers="matches"><tt>a</tt>, <tt>b</tt>, or <tt>c</tt> (simple class)</td></tr>
 * <tr><td valign="top" headers="construct classes"><tt>[^abc]</tt></td>
 *     <td headers="matches">Any character except <tt>a</tt>, <tt>b</tt>, or <tt>c</tt> (negation)</td></tr>
 * <tr><td valign="top" headers="construct classes"><tt>[a-zA-Z]</tt></td>
 *     <td headers="matches"><tt>a</tt> through <tt>z</tt>
 *         or <tt>A</tt> through <tt>Z</tt>, inclusive (range)</td></tr>
 * <tr><td valign="top" headers="construct classes"><tt>[a-d[m-p]]</tt></td>
 *     <td headers="matches"><tt>a</tt> through <tt>d</tt>,
 *      or <tt>m</tt> through <tt>p</tt>: <tt>[a-dm-p]</tt> (union)</td></tr>
 * <tr><td valign="top" headers="construct classes"><tt>[a-z&&[def]]</tt></td>
 *     <td headers="matches"><tt>d</tt>, <tt>e</tt>, or <tt>f</tt> (intersection)</tr>
 * <tr><td valign="top" headers="construct classes"><tt>[a-z&&[^bc]]</tt></td>
 *     <td headers="matches"><tt>a</tt> through <tt>z</tt>,
 *         except for <tt>b</tt> and <tt>c</tt>: <tt>[ad-z]</tt> (subtraction)</td></tr>
 * <tr><td valign="top" headers="construct classes"><tt>[a-z&&[^m-p]]</tt></td>
 *     <td headers="matches"><tt>a</tt> through <tt>z</tt>,
 *          and not <tt>m</tt> through <tt>p</tt>: <tt>[a-lq-z]</tt>(subtraction)</td></tr>
 * <tr><th>&nbsp;</th></tr>
 *
 * <tr align="left"><th colspan="2" id="predef">Predefined character classes</th></tr>
 *
 * <tr><td valign="top" headers="construct predef"><tt>.</tt></td>
 *     <td headers="matches">Any character (may or may not match <a href="#lt">line terminators</a>)</td></tr>
 * <tr><td valign="top" headers="construct predef"><tt>\d</tt></td>
 *     <td headers="matches">A digit: <tt>[0-9]</tt></td></tr>
 * <tr><td valign="top" headers="construct predef"><tt>\D</tt></td>
 *     <td headers="matches">A non-digit: <tt>[^0-9]</tt></td></tr>
 * <tr><td valign="top" headers="construct predef"><tt>\s</tt></td>
 *     <td headers="matches">A whitespace character: <tt>[ \t\n\x0B\f\r]</tt></td></tr>
 * <tr><td valign="top" headers="construct predef"><tt>\S</tt></td>
 *     <td headers="matches">A non-whitespace character: <tt>[^\s]</tt></td></tr>
 * <tr><td valign="top" headers="construct predef"><tt>\w</tt></td>
 *     <td headers="matches">A word character: <tt>[a-zA-Z_0-9]</tt></td></tr>
 * <tr><td valign="top" headers="construct predef"><tt>\W</tt></td>
 *     <td headers="matches">A non-word character: <tt>[^\w]</tt></td></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="2" id="posix">POSIX character classes</b> (US-ASCII only)<b></th></tr>
 *
 * <tr><td valign="top" headers="construct posix"><tt>\p{Lower}</tt></td>
 *     <td headers="matches">A lower-case alphabetic character: <tt>[a-z]</tt></td></tr>
 * <tr><td valign="top" headers="construct posix"><tt>\p{Upper}</tt></td>
 *     <td headers="matches">An upper-case alphabetic character:<tt>[A-Z]</tt></td></tr>
 * <tr><td valign="top" headers="construct posix"><tt>\p{ASCII}</tt></td>
 *     <td headers="matches">All ASCII:<tt>[\x00-\x7F]</tt></td></tr>
 * <tr><td valign="top" headers="construct posix"><tt>\p{Alpha}</tt></td>
 *     <td headers="matches">An alphabetic character:<tt>[\p{Lower}\p{Upper}]</tt></td></tr>
 * <tr><td valign="top" headers="construct posix"><tt>\p{Digit}</tt></td>
 *     <td headers="matches">A decimal digit: <tt>[0-9]</tt></td></tr>
 * <tr><td valign="top" headers="construct posix"><tt>\p{Alnum}</tt></td>
 *     <td headers="matches">An alphanumeric character:<tt>[\p{Alpha}\p{Digit}]</tt></td></tr>
 * <tr><td valign="top" headers="construct posix"><tt>\p{Punct}</tt></td>
 *     <td headers="matches">Punctuation: One of <tt>!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~</tt></td></tr>
 *     <!-- <tt>[\!"#\$%&'\(\)\*\+,\-\./:;\<=\>\?@\[\\\]\^_`\{\|\}~]</tt>
 *          <tt>[\X21-\X2F\X31-\X40\X5B-\X60\X7B-\X7E]</tt> -->
 * <tr><td valign="top" headers="construct posix"><tt>\p{Graph}</tt></td>
 *     <td headers="matches">A visible character: <tt>[\p{Alnum}\p{Punct}]</tt></td></tr>
 * <tr><td valign="top" headers="construct posix"><tt>\p{Print}</tt></td>
 *     <td headers="matches">A printable character: <tt>[\p{Graph}]</tt></td></tr>
 * <tr><td valign="top" headers="construct posix"><tt>\p{Blank}</tt></td>
 *     <td headers="matches">A space or a tab: <tt>[ \t]</tt></td></tr>
 * <tr><td valign="top" headers="construct posix"><tt>\p{Cntrl}</tt></td>
 *     <td headers="matches">A control character: <tt>[\x00-\x1F\x7F]</td></tr>
 * <tr><td valign="top" headers="construct posix"><tt>\p{XDigit}</tt></td>
 *     <td headers="matches">A hexadecimal digit: <tt>[0-9a-fA-F]</tt></td></tr>
 * <tr><td valign="top" headers="construct posix"><tt>\p{Space}</tt></td>
 *     <td headers="matches">A whitespace character: <tt>[ \t\n\x0B\f\r]</tt></td></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="2" id="unicode">Classes for Unicode blocks and categories</th></tr>
 *
 * <tr><td valign="top" headers="construct unicode"><tt>\p{InGreek}</tt></td>
 *     <td headers="matches">A character in the Greek&nbsp;block (simple <a href="#ubc">block</a>)</td></tr>
 * <tr><td valign="top" headers="construct unicode"><tt>\p{Lu}</tt></td>
 *     <td headers="matches">An uppercase letter (simple <a href="#ubc">category</a>)</td></tr>
 * <tr><td valign="top" headers="construct unicode"><tt>\p{Sc}</tt></td>
 *     <td headers="matches">A currency symbol</td></tr>
 * <tr><td valign="top" headers="construct unicode"><tt>\P{InGreek}</tt></td>
 *     <td headers="matches">Any character except one in the Greek block (negation)</td></tr>
 * <tr><td valign="top" headers="construct unicode"><tt>[\p{L}&&[^\p{Lu}]]&nbsp;</tt></td>
 *     <td headers="matches">Any letter except an uppercase letter (subtraction)</td></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="2" id="bounds">Boundary matchers</th></tr>
 *
 * <tr><td valign="top" headers="construct bounds"><tt>^</tt></td>
 *     <td headers="matches">The beginning of a line</td></tr>
 * <tr><td valign="top" headers="construct bounds"><tt>$</tt></td>
 *     <td headers="matches">The end of a line</td></tr>
 * <tr><td valign="top" headers="construct bounds"><tt>\b</tt></td>
 *     <td headers="matches">A word boundary</td></tr>
 * <tr><td valign="top" headers="construct bounds"><tt>\B</tt></td>
 *     <td headers="matches">A non-word boundary</td></tr>
 * <tr><td valign="top" headers="construct bounds"><tt>\A</tt></td>
 *     <td headers="matches">The beginning of the input</td></tr>
 * <tr><td valign="top" headers="construct bounds"><tt>\G</tt></td>
 *     <td headers="matches">The end of the previous match</td></tr>
 * <tr><td valign="top" headers="construct bounds"><tt>\Z</tt></td>
 *     <td headers="matches">The end of the input but for the final
 *         <a href="#lt">terminator</a>, if&nbsp;any</td></tr>
 * <tr><td valign="top" headers="construct bounds"><tt>\z</tt></td>
 *     <td headers="matches">The end of the input</td></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="2" id="greedy">Greedy quantifiers</th></tr>
 *
 * <tr><td valign="top" headers="construct greedy"><i>X</i><tt>?</tt></td>
 *     <td headers="matches"><i>X</i>, once or not at all</td></tr>
 * <tr><td valign="top" headers="construct greedy"><i>X</i><tt>*</tt></td>
 *     <td headers="matches"><i>X</i>, zero or more times</td></tr>
 * <tr><td valign="top" headers="construct greedy"><i>X</i><tt>+</tt></td>
 *     <td headers="matches"><i>X</i>, one or more times</td></tr>
 * <tr><td valign="top" headers="construct greedy"><i>X</i><tt>{</tt><i>n</i><tt>}</tt></td>
 *     <td headers="matches"><i>X</i>, exactly <i>n</i> times</td></tr>
 * <tr><td valign="top" headers="construct greedy"><i>X</i><tt>{</tt><i>n</i><tt>,}</tt></td>
 *     <td headers="matches"><i>X</i>, at least <i>n</i> times</td></tr>
 * <tr><td valign="top" headers="construct greedy"><i>X</i><tt>{</tt><i>n</i><tt>,</tt><i>m</i><tt>}</tt></td>
 *     <td headers="matches"><i>X</i>, at least <i>n</i> but not more than <i>m</i> times</td></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="2" id="reluc">Reluctant quantifiers</th></tr>
 *
 * <tr><td valign="top" headers="construct reluc"><i>X</i><tt>??</tt></td>
 *     <td headers="matches"><i>X</i>, once or not at all</td></tr>
 * <tr><td valign="top" headers="construct reluc"><i>X</i><tt>*?</tt></td>
 *     <td headers="matches"><i>X</i>, zero or more times</td></tr>
 * <tr><td valign="top" headers="construct reluc"><i>X</i><tt>+?</tt></td>
 *     <td headers="matches"><i>X</i>, one or more times</td></tr>
 * <tr><td valign="top" headers="construct reluc"><i>X</i><tt>{</tt><i>n</i><tt>}?</tt></td>
 *     <td headers="matches"><i>X</i>, exactly <i>n</i> times</td></tr>
 * <tr><td valign="top" headers="construct reluc"><i>X</i><tt>{</tt><i>n</i><tt>,}?</tt></td>
 *     <td headers="matches"><i>X</i>, at least <i>n</i> times</td></tr>
 * <tr><td valign="top" headers="construct reluc"><i>X</i><tt>{</tt><i>n</i><tt>,</tt><i>m</i><tt>}?</tt></td>
 *     <td headers="matches"><i>X</i>, at least <i>n</i> but not more than <i>m</i> times</td></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="2" id="poss">Possessive quantifiers</th></tr>
 *
 * <tr><td valign="top" headers="construct poss"><i>X</i><tt>?+</tt></td>
 *     <td headers="matches"><i>X</i>, once or not at all</td></tr>
 * <tr><td valign="top" headers="construct poss"><i>X</i><tt>*+</tt></td>
 *     <td headers="matches"><i>X</i>, zero or more times</td></tr>
 * <tr><td valign="top" headers="construct poss"><i>X</i><tt>++</tt></td>
 *     <td headers="matches"><i>X</i>, one or more times</td></tr>
 * <tr><td valign="top" headers="construct poss"><i>X</i><tt>{</tt><i>n</i><tt>}+</tt></td>
 *     <td headers="matches"><i>X</i>, exactly <i>n</i> times</td></tr>
 * <tr><td valign="top" headers="construct poss"><i>X</i><tt>{</tt><i>n</i><tt>,}+</tt></td>
 *     <td headers="matches"><i>X</i>, at least <i>n</i> times</td></tr>
 * <tr><td valign="top" headers="construct poss"><i>X</i><tt>{</tt><i>n</i><tt>,</tt><i>m</i><tt>}+</tt></td>
 *     <td headers="matches"><i>X</i>, at least <i>n</i> but not more than <i>m</i> times</td></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="2" id="logical">Logical operators</th></tr>
 *
 * <tr><td valign="top" headers="construct logical"><i>XY</i></td>
 *     <td headers="matches"><i>X</i> followed by <i>Y</i></td></tr>
 * <tr><td valign="top" headers="construct logical"><i>X</i><tt>|</tt><i>Y</i></td>
 *     <td headers="matches">Either <i>X</i> or <i>Y</i></td></tr>
 * <tr><td valign="top" headers="construct logical"><tt>(</tt><i>X</i><tt>)</tt></td>
 *     <td headers="matches">X, as a <a href="#cg">capturing group</a></td></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="2" id="backref">Back references</th></tr>
 *
 * <tr><td valign="bottom" headers="construct backref"><tt>\</tt><i>n</i></td>
 *     <td valign="bottom" headers="matches">Whatever the <i>n</i><sup>th</sup>
 *     <a href="#cg">capturing group</a> matched</td></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="2" id="quot">Quotation</th></tr>
 *
 * <tr><td valign="top" headers="construct quot"><tt>\</tt></td>
 *     <td headers="matches">Nothing, but quotes the following character</tt></td></tr>
 * <tr><td valign="top" headers="construct quot"><tt>\Q</tt></td>
 *     <td headers="matches">Nothing, but quotes all characters until <tt>\E</tt></td></tr>
 * <tr><td valign="top" headers="construct quot"><tt>\E</tt></td>
 *     <td headers="matches">Nothing, but ends quoting started by <tt>\Q</tt></td></tr>
 *     <!-- Metachars: !$()*+.<>?[\]^{|} -->
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="2" id="special">Special constructs (non-capturing)</th></tr>
 *
 * <tr><td valign="top" headers="construct special"><tt>(?:</tt><i>X</i><tt>)</tt></td>
 *     <td headers="matches"><i>X</i>, as a non-capturing group</td></tr>
 * <tr><td valign="top" headers="construct special"><tt>(?idmsux-idmsux)&nbsp;</tt></td>
 *     <td headers="matches">Nothing, but turns match flags on - off</td></tr>
 * <tr><td valign="top" headers="construct special"><tt>(?idmsux-idmsux:</tt><i>X</i><tt>)</tt>&nbsp;&nbsp;</td>
 *     <td headers="matches"><i>X</i>, as a <a href="#cg">non-capturing group</a> with the
 *         given flags on - off</td></tr>
 * <tr><td valign="top" headers="construct special"><tt>(?=</tt><i>X</i><tt>)</tt></td>
 *     <td headers="matches"><i>X</i>, via zero-width positive lookahead</td></tr>
 * <tr><td valign="top" headers="construct special"><tt>(?!</tt><i>X</i><tt>)</tt></td>
 *     <td headers="matches"><i>X</i>, via zero-width negative lookahead</td></tr>
 * <tr><td valign="top" headers="construct special"><tt>(?&lt;=</tt><i>X</i><tt>)</tt></td>
 *     <td headers="matches"><i>X</i>, via zero-width positive lookbehind</td></tr>
 * <tr><td valign="top" headers="construct special"><tt>(?&lt;!</tt><i>X</i><tt>)</tt></td>
 *     <td headers="matches"><i>X</i>, via zero-width negative lookbehind</td></tr>
 * <tr><td valign="top" headers="construct special"><tt>(?&gt;</tt><i>X</i><tt>)</tt></td>
 *     <td headers="matches"><i>X</i>, as an independent, non-capturing group</td></tr>
 *
 * </table>
 *
 * <hr>
 *
 *
 * <a name="bs">
 * <h4> Backslashes, escapes, and quoting </h4>
 *
 * <p> The backslash character (<tt>'\'</tt>) serves to introduce escaped
 * constructs, as defined in the table above, as well as to quote characters
 * that otherwise would be interpreted as unescaped constructs.  Thus the
 * expression <tt>\\</tt> matches a single backslash and <tt>\{</tt> matches a
 * left brace.
 *
 * <p> It is an error to use a backslash prior to any alphabetic character that
 * does not denote an escaped construct; these are reserved for future
 * extensions to the regular-expression language.  A backslash may be used
 * prior to a non-alphabetic character regardless of whether that character is
 * part of an unescaped construct.
 *
 * <p> Backslashes within string literals in Java source code are interpreted
 * as required by the <a
 * href="http://java.sun.com/docs/books/jls/second_edition/html/">Java Language
 * Specification</a> as either <a
 * href="http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#100850">Unicode
 * escapes</a> or other <a
 * href="http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#101089">character
 * escapes</a>.  It is therefore necessary to double backslashes in string
 * literals that represent regular expressions to protect them from
 * interpretation by the Java bytecode compiler.  The string literal
 * <tt>"&#92;b"</tt>, for example, matches a single backspace character when
 * interpreted as a regular expression, while <tt>"&#92;&#92;b"</tt> matches a
 * word boundary.  The string literal <tt>"&#92;(hello&#92;)"</tt> is illegal
 * and leads to a compile-time error; in order to match the string
 * <tt>(hello)</tt> the string literal <tt>"&#92;&#92;(hello&#92;&#92;)"</tt>
 * must be used.
 *
 * <a name="cc">
 * <h4> Character Classes </h4>
 *
 *    <p> Character classes may appear within other character classes, and
 *    may be composed by the union operator (implicit) and the intersection
 *    operator (<tt>&amp;&amp;</tt>).
 *    The union operator denotes a class that contains every character that is
 *    in at least one of its operand classes.  The intersection operator
 *    denotes a class that contains every character that is in both of its
 *    operand classes.
 *
 *    <p> The precedence of character-class operators is as follows, from
 *    highest to lowest:
 *
 *    <blockquote><table border="0" cellpadding="1" cellspacing="0" 
 *                 summary="Precedence of character class operators.">
 *      <tr><th>1&nbsp;&nbsp;&nbsp;&nbsp;</th>
 *	  <td>Literal escape&nbsp;&nbsp;&nbsp;&nbsp;</td>
 *	  <td><tt>\x</tt></td></tr>
 *     <tr><th>2&nbsp;&nbsp;&nbsp;&nbsp;</th>
 *	  <td>Grouping</td>
 *	  <td><tt>[...]</tt></td></tr>
 *     <tr><th>3&nbsp;&nbsp;&nbsp;&nbsp;</th>
 *	  <td>Range</td>
 *	  <td><tt>a-z</tt></td></tr>
 *      <tr><th>4&nbsp;&nbsp;&nbsp;&nbsp;</th>
 *	  <td>Union</td>
 *	  <td><tt>[a-e][i-u]<tt></td></tr>
 *      <tr><th>5&nbsp;&nbsp;&nbsp;&nbsp;</th>
 *	  <td>Intersection</td>
 *	  <td><tt>[a-z&&[aeiou]]</tt></td></tr>
 *    </table></blockquote>
 *
 *    <p> Note that a different set of metacharacters are in effect inside
 *    a character class than outside a character class. For instance, the
 *    regular expression <tt>.</tt> loses its special meaning inside a
 *    character class, while the expression <tt>-</tt> becomes a range
 *    forming metacharacter.
 *
 * <a name="lt">
 * <h4> Line terminators </h4>
 *
 * <p> A <i>line terminator</i> is a one- or two-character sequence that marks
 * the end of a line of the input character sequence.  The following are
 * recognized as line terminators:
 *
 * <ul>
 *
 *   <li> A newline (line feed) character&nbsp;(<tt>'\n'</tt>),
 *
 *   <li> A carriage-return character followed immediately by a newline
 *   character&nbsp;(<tt>"\r\n"</tt>),
 *
 *   <li> A standalone carriage-return character&nbsp;(<tt>'\r'</tt>),
 *
 *   <li> A next-line character&nbsp;(<tt>'&#92;u0085'</tt>),
 *
 *   <li> A line-separator character&nbsp;(<tt>'&#92;u2028'</tt>), or
 *
 *   <li> A paragraph-separator character&nbsp;(<tt>'&#92;u2029</tt>).
 *
 * </ul>
 * <p>If {@link #UNIX_LINES} mode is activated, then the only line terminators
 * recognized are newline characters.
 *
 * <p> The regular expression <tt>.</tt> matches any character except a line
 * terminator unless the {@link #DOTALL} flag is specified.
 *
 * <p> By default, the regular expressions <tt>^</tt> and <tt>$</tt> ignore
 * line terminators and only match at the beginning and the end, respectively,
 * of the entire input sequence. If {@link #MULTILINE} mode is activated then
 * <tt>^</tt> matches at the beginning of input and after any line terminator
 * except at the end of input. When in {@link #MULTILINE} mode <tt>$</tt>
 * matches just before a line terminator or the end of the input sequence.
 *
 * <a name="cg">
 * <h4> Groups and capturing </h4>
 *
 * <p> Capturing groups are numbered by counting their opening parentheses from
 * left to right.  In the expression <tt>((A)(B(C)))</tt>, for example, there
 * are four such groups: </p>
 *
 * <blockquote><table cellpadding=1 cellspacing=0 summary="Capturing group numberings">
 * <tr><th>1&nbsp;&nbsp;&nbsp;&nbsp;</th>
 *     <td><tt>((A)(B(C)))</tt></td></tr>
 * <tr><th>2&nbsp;&nbsp;&nbsp;&nbsp;</th>
 *     <td><tt>(A)</tt></td></tr>
 * <tr><th>3&nbsp;&nbsp;&nbsp;&nbsp;</th>
 *     <td><tt>(B(C))</tt></td></tr>
 * <tr><th>4&nbsp;&nbsp;&nbsp;&nbsp;</th>
 *     <td><tt>(C)</tt></td></tr>
 * </table></blockquote>
 *
 * <p> Group zero always stands for the entire expression.
 *
 * <p> Capturing groups are so named because, during a match, each subsequence
 * of the input sequence that matches such a group is saved.  The captured
 * subsequence may be used later in the expression, via a back reference, and
 * may also be retrieved from the matcher once the match operation is complete.
 *
 * <p> The captured input associated with a group is always the subsequence
 * that the group most recently matched.  If a group is evaluated a second time
 * because of quantification then its previously-captured value, if any, will
 * be retained if the second evaluation fails.  Matching the string
 * <tt>"aba"</tt> against the expression <tt>(a(b)?)+</tt>, for example, leaves
 * group two set to <tt>"b"</tt>.  All captured input is discarded at the
 * beginning of each match.
 *
 * <p> Groups beginning with <tt>(?</tt> are pure, <i>non-capturing</i> groups
 * that do not capture text and do not count towards the group total.
 *
 *
 * <h4> Unicode support </h4>
 *
 * <p> This class follows <a
 * href="http://www.unicode.org/unicode/reports/tr18/"><i>Unicode Technical
 * Report #18: Unicode Regular Expression Guidelines</i></a>, implementing its
 * second level of support though with a slightly different concrete syntax.
 *
 * <p> Unicode escape sequences such as <tt>&#92;u2014</tt> in Java source code
 * are processed as described in <a
 * href="http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#100850">\u00A73.3</a>
 * of the Java Language Specification.  Such escape sequences are also
 * implemented directly by the regular-expression parser so that Unicode
 * escapes can be used in expressions that are read from files or from the
 * keyboard.  Thus the strings <tt>"&#92;u2014"</tt> and <tt>"\\u2014"</tt>,
 * while not equal, compile into the same pattern, which matches the character
 * with hexadecimal value <tt>0x2014</tt>.
 *
 * <a name="ubc"> <p>Unicode blocks and categories are written with the
 * <tt>\p</tt> and <tt>\P</tt> constructs as in
 * Perl. <tt>\p{</tt><i>prop</i><tt>}</tt> matches if the input has the
 * property <i>prop</i>, while \P{</tt><i>prop</i><tt>}</tt> does not match if
 * the input has that property.  Blocks are specified with the prefix
 * <tt>In</tt>, as in <tt>InMongolian</tt>.  Categories may be specified with
 * the optional prefix <tt>Is</tt>: Both <tt>\p{L}</tt> and <tt>\p{IsL}</tt>
 * denote the category of Unicode letters.  Blocks and categories can be used
 * both inside and outside of a character class.
 *
 * <p> The supported blocks and categories are those of <a
 * href="http://www.unicode.org/unicode/standard/standard.html"><i>The Unicode
 * Standard, Version&nbsp;3.0</i></a>.  The block names are those defined in
 * Chapter&nbsp;14 and in the file <a
 * href="http://www.unicode.org/Public/3.0-Update/Blocks-3.txt">Blocks-3.txt
 * </a> of the <a
 * href="http://www.unicode.org/Public/3.0-Update/UnicodeCharacterDatabase-3.0.0.html">Unicode
 * Character Database</a> except that the spaces are removed; <tt>"Basic
 * Latin"</tt>, for example, becomes <tt>"BasicLatin"</tt>.  The category names
 * are those defined in table 4-5 of the Standard (p.&nbsp;88), both normative
 * and informative.
 *
 *
 * <h4> Comparison to Perl 5 </h4>
 *
 * <p> Perl constructs not supported by this class: </p>
 *
 * <ul>
 *
 *    <li><p> The conditional constructs <tt>(?{</tt><i>X</i><tt>})</tt> and
 *    <tt>(?(</tt><i>condition</i><tt>)</tt><i>X</i><tt>|</tt><i>Y</i><tt>)</tt>,
 *    </p></li>
 *
 *    <li><p> The embedded code constructs <tt>(?{</tt><i>code</i><tt>})</tt>
 *    and <tt>(??{</tt><i>code</i><tt>})</tt>,</p></li>
 *
 *    <li><p> The embedded comment syntax <tt>(?#comment)</tt>, and </p></li>
 *
 *    <li><p> The preprocessing operations <tt>\l</tt> <tt>&#92;u</tt>,
 *    <tt>\L</tt>, and <tt>\U</tt>.  </p></li>
 *
 * </ul>
 *
 * <p> Constructs supported by this class but not by Perl: </p>
 *
 * <ul>
 *
 *    <li><p> Possessive quantifiers, which greedily match as much as they can
 *    and do not back off, even when doing so would allow the overall match to
 *    succeed.  </p></li>
 *
 *    <li><p> Character-class union and intersection as described
 *    <a href="#cc">above</a>.</p></li>
 *
 * </ul>
 *
 * <p> Notable differences from Perl: </p>
 *
 * <ul>
 *
 *    <li><p> In Perl, <tt>\1</tt> through <tt>\9</tt> are always interpreted
 *    as back references; a backslash-escaped number greater than <tt>9</tt> is
 *    treated as a back reference if at least that many subexpressions exist,
 *    otherwise it is interpreted, if possible, as an octal escape.  In this
 *    class octal escapes must always begin with a zero. In this class,
 *    <tt>\1</tt> through <tt>\9</tt> are always interpreted as back
 *    references, and a larger number is accepted as a back reference if at
 *    least that many subexpressions exist at that point in the regular
 *    expression, otherwise the parser will drop digits until the number is
 *    smaller or equal to the existing number of groups or it is one digit.
 *    </p></li>
 *
 *    <li><p> Perl uses the <tt>g</tt> flag to request a match that resumes
 *    where the last match left off.  This functionality is provided implicitly
 *    by the {@link Matcher} class: Repeated invocations of the {@link
 *    Matcher#find find} method will resume where the last match left off,
 *    unless the matcher is reset.  </p></li>
 *
 *    <li><p> In Perl, embedded flags at the top level of an expression affect
 *    the whole expression.  In this class, embedded flags always take effect
 *    at the point at which they appear, whether they are at the top level or
 *    within a group; in the latter case, flags are restored at the end of the
 *    group just as in Perl.  </p></li>
 *
 *    <li><p> Perl is forgiving about malformed matching constructs, as in the
 *    expression <tt>*a</tt>, as well as dangling brackets, as in the
 *    expression <tt>abc]</tt>, and treats them as literals.  This
 *    class also accepts dangling brackets but is strict about dangling
 *    metacharacters like +, ? and *, and will throw a
 *    {@link PatternSyntaxException} if it encounters them. </p></li>
 *
 * </ul>
 *
 *
 * <p> For a more precise description of the behavior of regular expression
 * constructs, please see <a href="http://www.oreilly.com/catalog/regex2/">
 * <i>Mastering Regular Expressions, 2nd Edition</i>, Jeffrey E. F. Friedl,
 * O'Reilly and Associates, 2002.</a>
 * </p>
 *
 * @see java.lang.String#split(String, int)
 * @see java.lang.String#split(String)
 *
 * @author      Mike McCloskey
 * @author      Mark Reinhold
 * @author	JSR-51 Expert Group
 * @version 	1.95, 03/01/23
 * @since       1.4
 * @spec	JSR-51
 */

public final class Pattern
    implements java.io.Serializable
{

    /**
     * Regular expression modifier values.  Instead of being passed as
     * arguments, they can also be passed as inline modifiers.
     * For example, the following statements have the same effect.
     * <pre>
     * RegExp r1 = RegExp.compile("abc", Pattern.I|Pattern.M);
     * RegExp r2 = RegExp.compile("(?im)abc", 0);
     * </pre>
     *
     * The flags are duplicated so that the familiar Perl match flag
     * names are available.
     */

    /**
     * Enables Unix lines mode.
     *
     * <p> In this mode, only the <tt>'\n'</tt> line terminator is recognized
     * in the behavior of <tt>.</tt>, <tt>^</tt>, and <tt>$</tt>.
     *
     * <p> Unix lines mode can also be enabled via the embedded flag
     * expression&nbsp;<tt>(?d)</tt>.
     */
    public static final int UNIX_LINES = 0x01;

    /**
     * Enables case-insensitive matching.
     *
     * <p> By default, case-insensitive matching assumes that only characters
     * in the US-ASCII charset are being matched.  Unicode-aware
     * case-insensitive matching can be enabled by specifying the {@link
     * #UNICODE_CASE} flag in conjunction with this flag.
     *
     * <p> Case-insensitive matching can also be enabled via the embedded flag
     * expression&nbsp;<tt>(?i)</tt>.
     *
     * <p> Specifying this flag may impose a slight performance penalty.  </p>
     */
    public static final int CASE_INSENSITIVE = 0x02;

    /**
     * Permits whitespace and comments in pattern.
     *
     * <p> In this mode, whitespace is ignored, and embedded comments starting
     * with <tt>#</tt> are ignored until the end of a line.
     *
     * <p> Comments mode can also be enabled via the embedded flag
     * expression&nbsp;<tt>(?x)</tt>.
     */
    public static final int COMMENTS = 0x04;

    /**
     * Enables multiline mode.
     *
     * <p> In multiline mode the expressions <tt>^</tt> and <tt>$</tt> match
     * just after or just before, respectively, a line terminator or the end of
     * the input sequence.  By default these expressions only match at the
     * beginning and the end of the entire input sequence.
     *
     * <p> Multiline mode can also be enabled via the embedded flag
     * expression&nbsp;<tt>(?m)</tt>.  </p>
     */
    public static final int MULTILINE = 0x08;

    /**
     * Enables dotall mode.
     *
     * <p> In dotall mode, the expression <tt>.</tt> matches any character,
     * including a line terminator.  By default this expression does not match
     * line terminators.
     *
     * <p> Dotall mode can also be enabled via the embedded flag
     * expression&nbsp;<tt>(?s)</tt>.  (The <tt>s</tt> is a mnemonic for
     * "single-line" mode, which is what this is called in Perl.)  </p>
     */
    public static final int DOTALL = 0x20;

    /**
     * Enables Unicode-aware case folding.
     *
     * <p> When this flag is specified then case-insensitive matching, when
     * enabled by the {@link #CASE_INSENSITIVE} flag, is done in a manner
     * consistent with the Unicode Standard.  By default, case-insensitive
     * matching assumes that only characters in the US-ASCII charset are being
     * matched.
     *
     * <p> Unicode-aware case folding can also be enabled via the embedded flag
     * expression&nbsp;<tt>(?u)</tt>.
     *
     * <p> Specifying this flag may impose a performance penalty.  </p>
     */
    public static final int UNICODE_CASE = 0x40;

    /**
     * Enables canonical equivalence.
     *
     * <p> When this flag is specified then two characters will be considered
     * to match if, and only if, their full canonical decompositions match.
     * The expression <tt>"a&#92;u030A"</tt>, for example, will match the
     * string <tt>"\u00E5"</tt> when this flag is specified.  By default,
     * matching does not take canonical equivalence into account.
     *
     * <p> There is no embedded flag character for enabling canonical
     * equivalence.
     *
     * <p> Specifying this flag may impose a performance penalty.  </p>
     */
    public static final int CANON_EQ = 0x80;

    /* Pattern has only two serialized components: The pattern string
     * and the flags, which are all that is needed to recompile the pattern
     * when it is deserialized.
     */

    /** use serialVersionUID from Merlin b59 for interoperability */
    private static final long serialVersionUID = 5073258162644648461L;

    /**
     * The original regular-expression pattern string.
     *
     * @serial
     */
    private String pattern;

    /**
     * The original pattern flags.
     *
     * @serial
     */
    private int flags;

    /**
     * The normalized pattern string.
     */
    private transient String normalizedPattern;

    /**
     * The starting point of state machine for the find operation.  This allows
     * a match to start anywhere in the input.
     */
    transient Node root;

    /**
     * The root of object tree for a match operation.  The pattern is matched
     * at the beginning.  This may include a find that uses BnM or a First
     * node.
     */
    transient Node matchRoot;

    /**
     * Temporary storage used by parsing pattern slice.
     */
    transient char[] buffer;

    /**
     * Temporary storage used while parsing group references.
     */
    transient GroupHead[] groupNodes;

    /**
     * Temporary null terminating char array used by pattern compiling.
     */
    private transient char[] temp;

    /**
     * The group count of this Pattern. Used by matchers to allocate storage
     * needed to perform a match.
     */
    transient int groupCount;

    /**
     * The local variable count used by parsing tree. Used by matchers to
     * allocate storage needed to perform a match.
     */
    transient int localCount;

    /**
     * Index into the pattern string that keeps track of how much has been
     * parsed.
     */
    private transient int cursor;

    /**
     * Holds the length of the pattern string.
     */
    private transient int patternLength;

    /**
     * Compiles the given regular expression into a pattern.  </p>
     *
     * @param  regex
     *         The expression to be compiled
     *
     * @throws  PatternSyntaxException
     *          If the expression's syntax is invalid
     */
    public static Pattern compile(String regex) {
        return new Pattern(regex, 0);
    }

    /**
     * Compiles the given regular expression into a pattern with the given
     * flags.  </p>
     *
     * @param  regex
     *         The expression to be compiled
     *
     * @param  flags
     *         Match flags, a bit mask that may include
     *         {@link #CASE_INSENSITIVE}, {@link #MULTILINE}, {@link #DOTALL},
     *         {@link #UNICODE_CASE}, and {@link #CANON_EQ}
     *
     * @throws  IllegalArgumentException
     *          If bit values other than those corresponding to the defined
     *          match flags are set in <tt>flags</tt>
     *
     * @throws  PatternSyntaxException
     *          If the expression's syntax is invalid
     */
    public static Pattern compile(String regex, int flags) {
        return new Pattern(regex, flags);
    }

    /**
     * Returns the regular expression from which this pattern was compiled.
     * </p>
     *
     * @return  The source of this pattern
     */
    public String pattern() {
        return pattern;
    }

    /**
     * Creates a matcher that will match the given input against this pattern.
     * </p>
     *
     * @param  input
     *         The character sequence to be matched
     *
     * @return  A new matcher for this pattern
     */
    public Matcher matcher(CharSequence input) {
        Matcher m = new Matcher(this, input);
        return m;
    }

    /**
     * Returns this pattern's match flags.  </p>
     *
     * @return  The match flags specified when this pattern was compiled
     */
    public int flags() {
        return flags;
    }

    /**
     * Compiles the given regular expression and attempts to match the given
     * input against it.
     *
     * <p> An invocation of this convenience method of the form
     *
     * <blockquote><pre>
     * Pattern.matches(regex, input);</pre></blockquote>
     *
     * behaves in exactly the same way as the expression
     *
     * <blockquote><pre>
     * Pattern.compile(regex).matcher(input).matches()</pre></blockquote>
     *
     * <p> If a pattern is to be used multiple times, compiling it once and reusing
     * it will be more efficient than invoking this method each time.  </p>
     *
     * @param  regex
     *         The expression to be compiled
     *
     * @param  input
     *         The character sequence to be matched
     *
     * @throws  PatternSyntaxException
     *          If the expression's syntax is invalid
     */
    public static boolean matches(String regex, CharSequence input) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        return m.matches();
    }

    /**
     * Splits the given input sequence around matches of this pattern.
     *
     * <p> The array returned by this method contains each substring of the
     * input sequence that is terminated by another subsequence that matches
     * this pattern or is terminated by the end of the input sequence.  The
     * substrings in the array are in the order in which they occur in the
     * input.  If this pattern does not match any subsequence of the input then
     * the resulting array has just one element, namely the input sequence in
     * string form.
     *
     * <p> The <tt>limit</tt> parameter controls the number of times the
     * pattern is applied and therefore affects the length of the resulting
     * array.  If the limit <i>n</i> is greater than zero then the pattern
     * will be applied at most <i>n</i>&nbsp;-&nbsp;1 times, the array's
     * length will be no greater than <i>n</i>, and the array's last entry
     * will contain all input beyond the last matched delimiter.  If <i>n</i>
     * is non-positive then the pattern will be applied as many times as
     * possible and the array can have any length.  If <i>n</i> is zero then
     * the pattern will be applied as many times as possible, the array can
     * have any length, and trailing empty strings will be discarded.
     *
     * <p> The input <tt>"boo:and:foo"</tt>, for example, yields the following
     * results with these parameters:
     *
     * <blockquote><table cellpadding=1 cellspacing=0 
     *              summary="Split examples showing regex, limit, and result">
     * <tr><th><P align="left"><i>Regex&nbsp;&nbsp;&nbsp;&nbsp;</i></th>
     *     <th><P align="left"><i>Limit&nbsp;&nbsp;&nbsp;&nbsp;</i></th>
     *     <th><P align="left"><i>Result&nbsp;&nbsp;&nbsp;&nbsp;</i></th></tr>
     * <tr><td align=center>:</td>
     *     <td align=center>2</td>
     *     <td><tt>{ "boo", "and:foo" }</tt></td></tr>
     * <tr><td align=center>:</td>
     *     <td align=center>5</td>
     *     <td><tt>{ "boo", "and", "foo" }</tt></td></tr>
     * <tr><td align=center>:</td>
     *     <td align=center>-2</td>
     *     <td><tt>{ "boo", "and", "foo" }</tt></td></tr>
     * <tr><td align=center>o</td>
     *     <td align=center>5</td>
     *     <td><tt>{ "b", "", ":and:f", "", "" }</tt></td></tr>
     * <tr><td align=center>o</td>
     *     <td align=center>-2</td>
     *     <td><tt>{ "b", "", ":and:f", "", "" }</tt></td></tr>
     * <tr><td align=center>o</td>
     *     <td align=center>0</td>
     *     <td><tt>{ "b", "", ":and:f" }</tt></td></tr>
     * </table></blockquote>
     *
     *
     * @param  input
     *         The character sequence to be split
     *
     * @param  limit
     *         The result threshold, as described above
     *
     * @return  The array of strings computed by splitting the input
     *          around matches of this pattern
     */
    public String[] split(CharSequence input, int limit) {
        int index = 0;
        boolean matchLimited = limit > 0;
        ArrayList matchList = new ArrayList();
        Matcher m = matcher(input);

        // Add segments before each match found
        while(m.find()) {
            if (!matchLimited || matchList.size() < limit - 1) {
                String match = input.subSequence(index, m.start()).toString();
                matchList.add(match);
                index = m.end();
            } else if (matchList.size() == limit - 1) { // last one
                String match = input.subSequence(index,
                                                 input.length()).toString();
                matchList.add(match);
                index = m.end();
            }
        }

        // If no match was found, return this
        if (index == 0)
            return new String[] {input.toString()};

        // Add remaining segment
        if (!matchLimited || matchList.size() < limit)
            matchList.add(input.subSequence(index, input.length()).toString());

        // Construct result
        int resultSize = matchList.size();
        if (limit == 0)
            while (resultSize > 0 && matchList.get(resultSize-1).equals(""))
                resultSize--;
        String[] result = new String[resultSize];
        return (String[])matchList.subList(0, resultSize).toArray(result);
    }

    /**
     * Splits the given input sequence around matches of this pattern.
     *
     * <p> This method works as if by invoking the two-argument {@link
     * #split(java.lang.CharSequence, int) split} method with the given input
     * sequence and a limit argument of zero.  Trailing empty strings are
     * therefore not included in the resulting array. </p>
     *
     * <p> The input <tt>"boo:and:foo"</tt>, for example, yields the following
     * results with these expressions:
     *
     * <blockquote><table cellpadding=1 cellspacing=0 
     *              summary="Split examples showing regex and result">
     * <tr><th><P align="left"><i>Regex&nbsp;&nbsp;&nbsp;&nbsp;</i></th>
     *     <th><P align="left"><i>Result</i></th></tr>
     * <tr><td align=center>:</td>
     *     <td><tt>{ "boo", "and", "foo" }</tt></td></tr>
     * <tr><td align=center>o</td>
     *     <td><tt>{ "b", "", ":and:f" }</tt></td></tr>
     * </table></blockquote>
     *
     *
     * @param  input
     *         The character sequence to be split
     *
     * @return  The array of strings computed by splitting the input
     *          around matches of this pattern
     */
    public String[] split(CharSequence input) {
        return split(input, 0);
    }

    /**
     * Recompile the Pattern instance from a stream.  The original pattern
     * string is read in and the object tree is recompiled from it.
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {

        // Read in all fields
	s.defaultReadObject();

        // Initialize counts
        groupCount = 1;
        localCount = 0;

        // Recompile object tree
        if (pattern.length() > 0)
            compile();
        else
            root = new Start(lastAccept);
    }

    /**
     * This private constructor is used to create all Patterns. The pattern
     * string and match flags are all that is needed to completely describe
     * a Pattern. An empty pattern string results in an object tree with
     * only a Start node and a LastNode node.
     */
    private Pattern(String p, int f) {
        pattern = p;
        flags = f;

        // Reset group index count
        groupCount = 1;
        localCount = 0;

        if (pattern.length() > 0) {
            compile();
        } else {
            root = new Start(lastAccept);
            matchRoot = lastAccept;
        }
    }

    /**
     * The pattern is converted to normalizedD form and then a pure group
     * is constructed to match canonical equivalences of the characters.
     */
    private void normalize() {
        boolean inCharClass = false;
        char lastChar = 0xffff;

        // Convert pattern into normalizedD form
        normalizedPattern = Normalizer.decompose(pattern, false, 0);
        patternLength = normalizedPattern.length();

        // Modify pattern to match canonical equivalences
        StringBuffer newPattern = new StringBuffer(patternLength);
        for(int i=0; i<patternLength; i++) {
            char c = normalizedPattern.charAt(i);
            StringBuffer sequenceBuffer;
            if ((Character.getType(c) == Character.NON_SPACING_MARK)
                && (lastChar != 0xffff)) {
                sequenceBuffer = new StringBuffer();
                sequenceBuffer.append(lastChar);
                sequenceBuffer.append(c);
                while(Character.getType(c) == Character.NON_SPACING_MARK) {
                    i++;
                    if (i >= patternLength)
                        break;
                    c = normalizedPattern.charAt(i);
                    sequenceBuffer.append(c);
                }
                String ea = produceEquivalentAlternation(
                                               sequenceBuffer.toString());
                newPattern.setLength(newPattern.length()-1);
                newPattern.append("(?:").append(ea).append(")");
            } else if (c == '[' && lastChar != '\\') {
                i = normalizeCharClass(newPattern, i);
            } else {
                newPattern.append(c);
            }
            lastChar = c;
        }
        normalizedPattern = newPattern.toString();
    }

    /**
     * Complete the character class being parsed and add a set
     * of alternations to it that will match the canonical equivalences
     * of the characters within the class.
     */
    private int normalizeCharClass(StringBuffer newPattern, int i) {
        StringBuffer charClass = new StringBuffer();
        StringBuffer eq = null;
        char lastChar = 0xffff;
        String result;

        i++;
        charClass.append("[");
        while(true) {
            char c = normalizedPattern.charAt(i);
            StringBuffer sequenceBuffer;

            if (c == ']' && lastChar != '\\') {
                charClass.append(c);
                break;
            } else if (Character.getType(c) == Character.NON_SPACING_MARK) {
                sequenceBuffer = new StringBuffer();
                sequenceBuffer.append(lastChar);
                while(Character.getType(c) == Character.NON_SPACING_MARK) {
                    sequenceBuffer.append(c);
                    i++;
                    if (i >= normalizedPattern.length())
                        break;
                    c = normalizedPattern.charAt(i);
                }
                String ea = produceEquivalentAlternation(
                                                  sequenceBuffer.toString());

                charClass.setLength(charClass.length()-1);
                if (eq == null)
                    eq = new StringBuffer();
                eq.append('|');
                eq.append(ea);
            } else {
                charClass.append(c);
                i++;
            }
            if (i == normalizedPattern.length())
                error("Unclosed character class");
            lastChar = c;
        }

        if (eq != null) {
            result = new String("(?:"+charClass.toString()+
                                eq.toString()+")");
        } else {
            result = charClass.toString();
        }

        newPattern.append(result);
        return i;
    }

    /**
     * Given a specific sequence composed of a regular character and
     * combining marks that follow it, produce the alternation that will
     * match all canonical equivalences of that sequence.
     */
    private String produceEquivalentAlternation(String source) {
        if (source.length() == 1)
            return new String(source);

        String base = source.substring(0,1);
        String combiningMarks = source.substring(1);

        String[] perms = producePermutations(combiningMarks);
        StringBuffer result = new StringBuffer(source);

        // Add combined permutations
        for(int x=0; x<perms.length; x++) {
            String next = base + perms[x];
            if (x>0)
                result.append("|"+next);
            next = composeOneStep(next);
            if (next != null)
                result.append("|"+produceEquivalentAlternation(next));
        }
        return result.toString();
    }

    /**
     * Returns an array of strings that have all the possible
     * permutations of the characters in the input string.
     * This is used to get a list of all possible orderings
     * of a set of combining marks. Note that some of the permutations
     * are invalid because of combining class collisions, and these
     * possibilities must be removed because they are not canonically
     * equivalent.
     */
    private String[] producePermutations(String input) {
        if (input.length() == 1)
            return new String[] {input};

        if (input.length() == 2) {
            if (getClass(input.charAt(1)) ==
                getClass(input.charAt(0))) {
                return new String[] {input};
            }
            String[] result = new String[2];
            result[0] = input;
            StringBuffer sb = new StringBuffer(2);
            sb.append(input.charAt(1));
            sb.append(input.charAt(0));
            result[1] = sb.toString();
            return result;
        }

        int length = 1;
        for(int x=1; x<input.length(); x++)
            length = length * (x+1);

        String[] temp = new String[length];

        int combClass[] = new int[input.length()];
        for(int x=0; x<input.length(); x++)
            combClass[x] = getClass(input.charAt(x));

        // For each char, take it out and add the permutations
        // of the remaining chars
        int index = 0;
loop:   for(int x=0; x<input.length(); x++) {
            boolean skip = false;
            for(int y=x-1; y>=0; y--) {
                if (combClass[y] == combClass[x]) {
                    continue loop;
                }
            }
            StringBuffer sb = new StringBuffer(input);
            String otherChars = sb.delete(x, x+1).toString();
            String[] subResult = producePermutations(otherChars);

            String prefix = input.substring(x, x+1);
            for(int y=0; y<subResult.length; y++)
                temp[index++] =  prefix + subResult[y];
        }
        String[] result = new String[index];
        for (int x=0; x<index; x++)
            result[x] = temp[x];
        return result;
    }

    private int getClass(char c) {
        return Normalizer.getClass(c);
    }

    /**
     * Attempts to compose input by combining the first character
     * with the first combining mark following it. Returns a String
     * that is the composition of the leading character with its first
     * combining mark followed by the remaining combining marks. Returns
     * null if the first two chars cannot be further composed.
     */
    private String composeOneStep(String input) {
        String firstTwoChars = input.substring(0,2);
        String result = Normalizer.compose(firstTwoChars, false, 0);

        if (result.equals(firstTwoChars))
            return null;
        else {
            String remainder = input.substring(2);
            return result + remainder;
        }
    }

    /**
     * Copies regular expression to a char array and inovkes the parsing
     * of the expression which will create the object tree.
     */
    private void compile() {
        // Handle canonical equivalences
        if (has(CANON_EQ)) {
            normalize();
        } else {
            normalizedPattern = pattern;
        }

        // Copy pattern to char array for convenience
        patternLength = normalizedPattern.length();
        temp = new char[patternLength + 2];

        // Use double null characters to terminate pattern
        normalizedPattern.getChars(0, patternLength, temp, 0);
        temp[patternLength] = 0;
        temp[patternLength + 1] = 0;

        // Allocate all temporary objects here.
        buffer = new char[32];
        groupNodes = new GroupHead[10];
        // Start recursive decedent parsing
        matchRoot = expr(lastAccept);

        // Check extra pattern characters
        if (patternLength != cursor) {
            if (peek() == ')') {
                error("Unmatched closing ')'");
            } else {
                error("Unexpected internal error");
            }
        }

        // Peephole optimization
        if (matchRoot instanceof Slice) {
            root = BnM.optimize(matchRoot);
            if (root == matchRoot) {
                root = new Start(matchRoot);
            }
        } else if (matchRoot instanceof Begin
            || matchRoot instanceof First) {
            root = matchRoot;
        } else {
            root = new Start(matchRoot);
        }

        // Release temporary storage
        temp = null;
        buffer = null;
        groupNodes = null;
        patternLength = 0;
    }

    /**
     * Used to print out a subtree of the Pattern to help with debugging.
     */
    private static void printObjectTree(Node node) {
        while(node != null) {
            if (node instanceof Prolog) {
                System.out.println(node);
                printObjectTree(((Prolog)node).loop);
                System.out.println("**** end contents prolog loop");
            } else if (node instanceof Loop) {
                System.out.println(node);
                printObjectTree(((Loop)node).body);
                System.out.println("**** end contents Loop body");
            } else if (node instanceof Curly) {
                System.out.println(node);
                printObjectTree(((Curly)node).atom);
                System.out.println("**** end contents Curly body");
            } else if (node instanceof GroupTail) {
                System.out.println(node);
                System.out.println("Tail next is "+node.next);
                return;
            } else {
                System.out.println(node);
            }      
            node = node.next;
            if (node != null)
                System.out.println("->next:");
            if (node == Pattern.accept) {
                System.out.println("Accept Node");
                node = null;
            }
       }
    }

    /**
     * Used to accumulate information about a subtree of the object graph
     * so that optimizations can be applied to the subtree.
     */
    static final class TreeInfo {
        int minLength;
        int maxLength;
        boolean maxValid;
        boolean deterministic;

        TreeInfo() {
            reset();
        }
        void reset() {
            minLength = 0;
            maxLength = 0;
            maxValid = true;
            deterministic = true;
        }
    }

    /**
     * The following private methods are mainly used to improve the
     * readability of the code. In order to let the Java compiler easily
     * inline them, we should not put many assertions or error checks in them.
     */

    /**
     * Indicates whether a particular flag is set or not.
     */
    private boolean has(int f) {
        return (flags & f) > 0;
    }

    /**
     * Match next character, signal error if failed.
     */
    private void accept(int ch, String s) {
        int testChar = temp[cursor++];
        if (has(COMMENTS))
            testChar = parsePastWhitespace(testChar);
        if (ch != testChar) {
           error(s);
        }
    }

    /**
     * Mark the end of pattern with a specific character.
     */
    private void mark(char c) {
        temp[patternLength] = c;
    }

    /**
     * Peek the next character, and do not advance the cursor.
     */
    private int peek() {
        int ch = temp[cursor];
        if (has(COMMENTS))
            ch = peekPastWhitespace(ch);
        return ch;
    }

    /**
     * Read the next character, and advance the cursor by one.
     */
    private int read() {
        int ch = temp[cursor++];
        if (has(COMMENTS))
            ch = parsePastWhitespace(ch);
        return ch;
    }

    /**
     * Read the next character, and advance the cursor by one,
     * ignoring the COMMENTS setting
     */
    private int readEscaped() {
        int ch = temp[cursor++];
        return ch;
    }

    /**
     * Advance the cursor by one, and peek the next character.
     */
    private int next() {
        int ch = temp[++cursor];
        if (has(COMMENTS))
            ch = peekPastWhitespace(ch);
        return ch;
    }

    /**
     * Advance the cursor by one, and peek the next character,
     * ignoring the COMMENTS setting
     */
    private int nextEscaped() {
        int ch = temp[++cursor];
        return ch;
    }

    /**
     * If in xmode peek past whitespace and comments.
     */
    private int peekPastWhitespace(int ch) {
        while (ASCII.isSpace(ch) || ch == '#') {
            while (ASCII.isSpace(ch))
                ch = temp[++cursor];
            if (ch == '#') {
                ch = peekPastLine();
            }
        }
        return ch;
    }

    /**
     * If in xmode parse past whitespace and comments.
     */
    private int parsePastWhitespace(int ch) {
        while (ASCII.isSpace(ch) || ch == '#') {
            while (ASCII.isSpace(ch))
                ch = temp[cursor++];
            if (ch == '#')
                ch = parsePastLine();
        }
        return ch;
    }

    /**
     * xmode parse past comment to end of line.
     */
    private int parsePastLine() {
        int ch = temp[cursor++];
        while (ch != 0 && !isLineSeparator(ch))
            ch = temp[cursor++];
        return ch;
    }

    /**
     * xmode peek past comment to end of line.
     */
    private int peekPastLine() {
        int ch = temp[++cursor];
        while (ch != 0 && !isLineSeparator(ch))
            ch = temp[++cursor];
        return ch;
    }

    /**
     * Determines if character is a line separator in the current mode
     */
    private boolean isLineSeparator(int ch) {
        if (has(UNIX_LINES)) {
            return ch == '\n';
        } else {
            return (ch == '\n' ||
                    ch == '\r' ||
                    (ch|1) == '\u2029' ||
                    ch == '\u0085');
        }
    }

    /**
     * Read the character after the next one, and advance the cursor by two.
     */
    private int skip() {
        int i = cursor;
        int ch = temp[i+1];
        cursor = i + 2;
        return ch;
    }

    /**
     * Unread one next character, and retreat cursor by one.
     */
    private void unread() {
        cursor--;
    }

    /**
     * Internal method used for handling all syntax errors. The pattern is
     * displayed with a pointer to aid in locating the syntax error.
     */
    private Node error(String s) {
	throw new PatternSyntaxException(s, normalizedPattern,
					 cursor - 1);
    }

    /**
     *  The following methods handle the main parsing. They are sorted
     *  according to their precedence order, the lowest one first.
     */

    /**
     * The expression is parsed with branch nodes added for alternations.
     * This may be called recursively to parse sub expressions that may
     * contain alternations.
     */
    private Node expr(Node end) {
        Node prev = null;
        for (;;) {
            Node node = sequence(end);
            if (prev == null) {
                prev = node;
            } else {
                prev = new Branch(prev, node);
            }
            if (peek() != '|') {
                return prev;
            }
            next();
        }
    }

    /**
     * Parsing of sequences between alternations.
     */
    private Node sequence(Node end) {
        Node head = null;
        Node tail = null;
        Node node = null;
        int i, j, ch;
    LOOP:
        for (;;) {
            ch = peek();
            switch (ch) {
            case '(':
                // Because group handles its own closure,
                // we need to treat it differently
                node = group0();
                // Check for comment or flag group
                if (node == null)
                    continue;
                if (head == null)
                    head = node;
                else
                    tail.next = node;
                // Double return: Tail was returned in root
                tail = root;
                continue;
            case '[':
                node = clazz(true);
                break;
            case '\\':
                ch = nextEscaped();
                if (ch == 'p' || ch == 'P') {
                    boolean comp = (ch == 'P');
                    boolean oneLetter = true;
                    ch = next(); // Consume { if present
                    if (ch != '{') {
                        unread();
                    } else {
                        oneLetter = false;
                    }
                    node = family(comp, oneLetter);
                } else {
                    unread();
                    node = atom();
                }
                break;
            case '^':
                next();
                if (has(MULTILINE)) {
                    if (has(UNIX_LINES))
                        node = new UnixCaret();
                    else
                        node = new Caret();
                } else {
                    node = new Begin();
                }
                break;
            case '$':
                next();
                if (has(UNIX_LINES))
                    node = new UnixDollar(has(MULTILINE));
                else
                    node = new Dollar(has(MULTILINE));
                break;
            case '.':
                next();
                if (has(DOTALL)) {
                    node = new All();
                } else {
                    if (has(UNIX_LINES))
                        node = new UnixDot();
                    else {
                        node = new Dot();
                    }
                }
                break;
            case '|':
            case ')':
                break LOOP;
            case ']': // Now interpreting dangling ] and } as literals
            case '}':
                node = atom();
                break;
            case '?':
            case '*':
            case '+':
                next();
                return error("Dangling meta character '" + ((char)ch) + "'");
            case 0:
                if (cursor >= patternLength) {
                    break LOOP;
                }
                // Fall through
            default:
                node = atom();
                break;
            }

            node = closure(node);

            if (head == null) {
                head = tail = node;
            } else {
                tail.next = node;
                tail = node;
            }
        }
        if (head == null) {
            return end;
        }
        tail.next = end;
        return head;
    }

    /**
     * Parse and add a new Single or Slice.
     */
    private Node atom() {
        int first = 0;
        int prev = -1;
        int ch = peek();
        for (;;) {
            switch (ch) {
            case '*':
            case '+':
            case '?':
            case '{':
                if (first > 1) {
                    cursor = prev;    // Unwind one character
                    first--;
                }
                break;
            case '$':
            case '.':
            case '^':
            case '(':
            case '[':
            case '|':
            case ')':
                break;
            case '\\':
                ch = nextEscaped();
                if (ch == 'p' || ch == 'P') { // Property
                    if (first > 0) { // Slice is waiting; handle it first
                        unread();
                        break;
                    } else { // No slice; just return the family node
                        if (ch == 'p' || ch == 'P') {
                            boolean comp = (ch == 'P');
                            boolean oneLetter = true;
                            ch = next(); // Consume { if present
                            if (ch != '{')
                                unread();
                            else
                                oneLetter = false;
                            return family(comp, oneLetter);
                        }
                    }
                    break;
                }
                unread();
                prev = cursor;
                ch = escape(false, first == 0);
                if (ch >= 0) {
                    append(ch, first);
                    first++;
                    ch = peek();
                    continue;
                } else if (first == 0) {
                    return root;
                }
                // Unwind meta escape sequence
                cursor = prev;
                break;
            case 0:
                if (cursor >= patternLength) {
                    break;
                }
                // Fall through
            default:
                prev = cursor;
                append(ch, first);
                first++;
                ch = next();
                continue;
            }
            break;
        }
        if (first == 1) {
            return newSingle(buffer[0]);
        } else {
            return newSlice(buffer, first);
        }
    }

    private void append(int ch, int len) {
        if (len >= buffer.length) {
            char[] tmp = new char[len+len];
            System.arraycopy(buffer, 0, tmp, 0, len);
            buffer = tmp;
        }
        buffer[len] = (char) ch;
    }

    /**
     * Parses a backref greedily, taking as many numbers as it
     * can. The first digit is always treated as a backref, but
     * multi digit numbers are only treated as a backref if at
     * least that many backrefs exist at this point in the regex.
     */
    private Node ref(int refNum) {
        boolean done = false;
        while(!done) {
            int ch = peek();
            switch(ch) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    int newRefNum = (refNum * 10) + (ch - '0');
                    // Add another number if it doesn't make a group
                    // that doesn't exist
                    if (groupCount - 1 < newRefNum) {
                        done = true;
                        break;
                    }
                    refNum = newRefNum;
                    read();
                    break;
                default:
                    done = true;
                    break;
            }
        }
        if (has(CASE_INSENSITIVE))
            return new CIBackRef(refNum);
        else
            return new BackRef(refNum);
    }

    /**
     * Parses an escape sequence to determine the actual value that needs
     * to be matched.
     * If -1 is returned and create was true a new object was added to the tree
     * to handle the escape sequence.
     * If the returned value is greater than zero, it is the value that
     * matches the escape sequence.
     */
    private int escape(boolean inclass, boolean create) {
        int ch = skip();
        switch (ch) {
            case '0':
                return o();
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                if (inclass) break;
                if (groupCount < (ch - '0'))
                    error("No such group yet exists at this point in the pattern");
                if (create) {
                    root = ref((ch - '0'));
                }
                return -1;
            case 'A':
                if (inclass) break;
                if (create) root = new Begin();
                return -1;
            case 'B':
                if (inclass) break;
                if (create) root = new Bound(Bound.NONE);
                return -1;
            case 'C':
                break;
            case 'D':
                if (create) root = new NotCtype(ASCII.DIGIT);
                return -1;
            case 'E':
            case 'F':
                break;
            case 'G':
                if (inclass) break;
                if (create) root = new LastMatch();
                return -1;
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
                break;
            case 'Q':
                if (create) {
                    // Disable metacharacters. We will return a slice
                    // up to the next \E
                    int i = cursor;
                    int c;
                    while ((c = readEscaped()) != 0) {
                        if (c == '\\') {
                            c = readEscaped();
                            if (c == 'E' || c == 0)
                                break;
                        }
                    }
                    int j = cursor-1;
                    if (c == 'E')
                        j--;
                    else
                        unread();
                    for (int x = i; x<j; x++)
                        append(temp[x], x-i);
                    root = newSlice(buffer, j-i);
                }
                return -1;
            case 'R':
                break;
            case 'S':
                if (create) root = new NotCtype(ASCII.SPACE);
                return -1;
            case 'T':
            case 'U':
            case 'V':
                break;
            case 'W':
                if (create) root = new NotCtype(ASCII.WORD);
                return -1;
            case 'X':
            case 'Y':
                break;
            case 'Z':
                if (inclass) break;
                if (create) {
                    if (has(UNIX_LINES))
                        root = new UnixDollar(false);
                    else
                        root = new Dollar(false);
                }
                return -1;
            case 'a':
                return '\007';
            case 'b':
                if (inclass) break;
                if (create) root = new Bound(Bound.BOTH);
                return -1;
            case 'c':
                return c();
            case 'd':
                if (create) root = new Ctype(ASCII.DIGIT);
                return -1;
            case 'e':
                return '\033';
            case 'f':
                return '\f';
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
                break;
            case 'n':
                return '\n';
            case 'o':
            case 'p':
            case 'q':
                break;
            case 'r':
                return '\r';
            case 's':
                if (create) root = new Ctype(ASCII.SPACE);
                return -1;
            case 't':
                return '\t';
            case 'u':
                return u();
            case 'v':
                return '\013';
            case 'w':
                if (create) root = new Ctype(ASCII.WORD);
                return -1;
            case 'x':
                return x();
            case 'y':
                break;
            case 'z':
                if (inclass) break;
                if (create) root = new End();
                return -1;
            default:
                return ch;
        }
        error("Illegal/unsupported escape squence");
        return -2;
    }

    /**
     * Parse a character class, and return the node that matches it.
     *
     * Consumes a ] on the way out if consume is true. Usually consume
     * is true except for the case of [abc&&def] where def is a separate
     * right hand node with "understood" brackets.
     */
    private Node clazz(boolean consume) {
        Node prev = null;
        Node node = null;
        BitClass bits = new BitClass(false);
        boolean include = true;
        boolean firstInClass = true;
        int ch = next();
        for (;;) {
            switch (ch) {
                case '^':
                    // Negates if first char in a class, otherwise literal
                    if (firstInClass) {
                        if (temp[cursor-1] != '[')
                            break;
                        ch = next();
                        include = !include;
                        continue;
                    } else {
                        // ^ not first in class, treat as literal
                        break;
                    }
                case '[':
                    firstInClass = false;
                    node = clazz(true);
                    if (prev == null)
                        prev = node;
                    else
                        prev = new Add(prev, node);
                    ch = peek();
                    continue;
                case '&':
                    firstInClass = false;
                    ch = next();
                    if (ch == '&') {
                        ch = next();
                        Node rightNode = null;
                        while (ch != ']' && ch != '&') {
                            if (ch == '[') {
                                if (rightNode == null)
                                    rightNode = clazz(true);
                                else
                                    rightNode = new Add(rightNode, clazz(true));
                            } else { // abc&&def
                                unread();
                                rightNode = clazz(false);
                            }
                            ch = peek();
                        }
                        if (rightNode != null)
                            node = rightNode;
                        if (prev == null) {
                            if (rightNode == null)
                                return error("Bad class syntax");
                            else
                                prev = rightNode;
                        } else {
                            prev = new Both(prev, node);
                        }
                    } else {
                        // treat as a literal &
                        unread();
                        break;
                    }
                    continue;
                case 0:
                    firstInClass = false;
                    if (cursor >= patternLength)
                        return error("Unclosed character class");
                    break;
                case ']':
                    firstInClass = false;
                    if (prev != null) {
                        if (consume)
                            next();
                        return prev;
                    }
                    break;
                default:
                    firstInClass = false;
                    break;
            }
            node = range(bits);
            if (include) {
                if (prev == null) {
                    prev = node;
                } else {
                    if (prev != node)
                        prev = new Add(prev, node);
                }
            } else {
                if (prev == null) {
                    prev = node.dup(true);  // Complement
                } else {
                    if (prev != node)
                        prev = new Sub(prev, node);
                }
            }
            ch = peek();
        }
    }

    /**
     * Parse a single character or a character range in a character class
     * and return its representative node.
     */
    private Node range(BitClass bits) {
        int ch = peek();
        if (ch == '\\') {
            ch = nextEscaped();
            if (ch == 'p' || ch == 'P') { // A property
                boolean comp = (ch == 'P');
                boolean oneLetter = true;
                // Consume { if present
                ch = next();
                if (ch != '{')
                    unread();
                else
                    oneLetter = false;
                return family(comp, oneLetter);
            } else { // ordinary escape
                unread();
                ch = escape(true, true);
                if (ch == -1)
                    return root;
            }
        } else {
            ch = single();
        }
        if (ch >= 0) {
            if (peek() == '-') {
                char endRange = temp[cursor+1];
                if (endRange == '[') {
                    if (ch < 256)
                        return bits.add(ch, flags());
                    return newSingle(ch);
                }
                if (endRange != ']') {
                    next();
                    int m = single();
                    if (m < ch)
                        return error("Illegal character range");
                    if (has(CASE_INSENSITIVE))
                        return new CIRange((ch<<16)+m);
                    else
                        return new Range((ch<<16)+m);
                }
            }
            if (ch < 256)
                return bits.add(ch, flags());
            return newSingle(ch);
        }
        return error("Unexpected character '"+((char)ch)+"'");
    }

    private int single() {
        int ch = peek();
        switch (ch) {
        case '\\':
            return escape(true, false);
        default:
            next();
            return ch;
        }
    }

    /**
     * Parses a Unicode character family and returns its representative node.
     * Reference to an unknown character family results in a list of supported
     * families in the error.
     */
    private Node family(boolean not, boolean singleLetter) {
        next();
        String name;

        if (singleLetter) {
            name = new String(temp, cursor, 1).intern();
            read();
        } else {
            int i = cursor;
            mark('}');
            while(read() != '}') {
            }
            mark('\000');
            int j = cursor;
            if (j > patternLength)
                return error("Unclosed character family");
            if (i + 1 >= j)
                return error("Empty character family");
            name = new String(temp, i, j-i-1).intern();
        }

        if (name.startsWith("In")) {
            name = name.substring(2, name.length()).intern();
            return retrieveFamilyNode(name).dup(not);
        }
        if (name.startsWith("Is"))
            name = name.substring(2, name.length()).intern();
        return retrieveCategoryNode(name).dup(not);
    }

    private Node retrieveFamilyNode(String name) {
        if (families == null) {
            int fns = familyNodes.length;
            families = new HashMap((int)(fns/.75) + 1);
            for (int x=0; x<fns; x++)
                families.put(familyNames[x], familyNodes[x]);
        }
        Node n = (Node)families.get(name);
        if (n != null)
            return n;

        return familyError(name, "Unknown character family {");
    }

    private Node retrieveCategoryNode(String name) {
        if (categories == null) {
            int cns = categoryNodes.length;
            categories = new HashMap((int)(cns/.75) + 1);
            for (int x=0; x<cns; x++)
                categories.put(categoryNames[x], categoryNodes[x]);
        }
        Node n = (Node)categories.get(name);
        if (n != null)
            return n;

        return familyError(name, "Unknown character category {");
    }

    private Node familyError(String name, String type) {
        StringBuffer sb = new StringBuffer();
        sb.append(type);
        sb.append(name);
        sb.append("}");
        name = sb.toString();
        return error(name);
    }

    /**
     * Parses a group and returns the head node of a set of nodes that process
     * the group. Sometimes a double return system is used where the tail is
     * returned in root.
     */
    private Node group0() {
        Node head = null;
        Node tail = null;
        int save = flags;
        root = null;
        int ch = next();
        if (ch == '?') {
            ch = skip();
            switch (ch) {
            case ':':   //  (?:xxx) pure group
                head = createGroup(true);
                tail = root;
                head.next = expr(tail);
                break;
            case '=':   // (?=xxx) and (?!xxx) lookahead
            case '!':
                head = createGroup(true);
                tail = root;
                head.next = expr(tail);
                if (ch == '=') {
                    head = tail = new Pos(head);
                } else {
                    head = tail = new Neg(head);
                }
                break;
            case '>':   // (?>xxx)  independent group
                head = createGroup(true);
                tail = root;
                head.next = expr(tail);
                head = tail = new Ques(head, INDEPENDENT);
                break;
            case '<':   // (?<xxx)  look behind
                ch = read();
                head = createGroup(true);
                tail = root;
                head.next = expr(tail);
                TreeInfo info = new TreeInfo();
                head.study(info);
                if (info.maxValid == false) {
                    return error("Look-behind group does not have "
                                 + "an obvious maximum length");
                }
                if (ch == '=') {
                    head = tail = new Behind(head, info.maxLength,
                                             info.minLength);
                } else if (ch == '!') {
                    head = tail = new NotBehind(head, info.maxLength,
                                                info.minLength);
                } else {
                    error("Unknown look-behind group");
                }
                break;
            case '1': case '2': case '3': case '4': case '5':
            case '6': case '7': case '8': case '9':
                if (groupNodes[ch-'0'] != null) {
                    head = tail = new GroupRef(groupNodes[ch-'0']);
                    break;
                }
                return error("Unknown group reference");
            case '$':
            case '@':
		return error("Unknown group type");
            default:    // (?xxx:) inlined match flags
                unread();
                addFlag();
                ch = read();
                if (ch == ')') {
                    return null;    // Inline modifier only
                }
                if (ch != ':') {
                    return error("Unknown inline modifier");
                }
                head = createGroup(true);
                tail = root;
                head.next = expr(tail);
                break;
            }
        } else { // (xxx) a regular group
            head = createGroup(false);
            tail = root;
            head.next = expr(tail);
        }

        accept(')', "Unclosed group");
        flags = save;

        // Check for quantifiers
        Node node = closure(head);
        if (node == head) { // No closure
            root = tail;
            return node;    // Dual return
        }
        if (head == tail) { // Zero length assertion
            root = node;
            return node;    // Dual return
        }

        if (node instanceof Ques) {
            Ques ques = (Ques) node;
            if (ques.type == POSSESSIVE) {
                root = node;
                return node;
            }
            // Dummy node to connect branch
            tail.next = new Dummy();
            tail = tail.next;
            if (ques.type == GREEDY) {
                head = new Branch(head, tail);
            } else { // Reluctant quantifier
                head = new Branch(tail, head);
            }
            root = tail;
            return head;
        } else if (node instanceof Curly) {
            Curly curly = (Curly) node;
            if (curly.type == POSSESSIVE) {
                root = node;
                return node;
            }
            // Discover if the group is deterministic
            TreeInfo info = new TreeInfo();
            if (head.study(info)) { // Deterministic
                GroupTail temp = (GroupTail) tail;
                head = root = new GroupCurly(head.next, curly.cmin,
                                   curly.cmax, curly.type,
                                   ((GroupTail)tail).localIndex,
                                   ((GroupTail)tail).groupIndex);
                return head;
            } else { // Non-deterministic
                int temp = ((GroupHead) head).localIndex;
                Loop loop;
                if (curly.type == GREEDY)
                    loop = new Loop(this.localCount, temp);
                else  // Reluctant Curly
                    loop = new LazyLoop(this.localCount, temp);
                Prolog prolog = new Prolog(loop);
                this.localCount += 1;
                loop.cmin = curly.cmin;
                loop.cmax = curly.cmax;
                loop.body = head;
                tail.next = loop;
                root = loop;
                return prolog; // Dual return
            }
        } else if (node instanceof First) {
            root = node;
            return node;
        }
        return error("Internal logic error");
    }

    /**
     * Create group head and tail nodes using double return. If the group is
     * created with anonymous true then it is a pure group and should not
     * affect group counting.
     */
    private Node createGroup(boolean anonymous) {
        int localIndex = localCount++;
        int groupIndex = 0;
        if (!anonymous)
            groupIndex = groupCount++;
        GroupHead head = new GroupHead(localIndex);
        root = new GroupTail(localIndex, groupIndex);
        if (!anonymous && groupIndex < 10)
            groupNodes[groupIndex] = head;
        return head;
    }

    /**
     * Parses inlined match flags and set them appropriately.
     */
    private void addFlag() {
        int ch = peek();
        for (;;) {
            switch (ch) {
            case 'i':
                flags |= CASE_INSENSITIVE;
                break;
            case 'm':
                flags |= MULTILINE;
                break;
            case 's':
                flags |= DOTALL;
                break;
            case 'd':
                flags |= UNIX_LINES;
                break;
            case 'u':
                flags |= UNICODE_CASE;
                break;
            case 'c':
                flags |= CANON_EQ;
                break;
            case 'x':
                flags |= COMMENTS;
                break;
            case '-': // subFlag then fall through
                ch = next();
                subFlag();
            default:
                return;
            }
            ch = next();
        }
    }

    /**
     * Parses the second part of inlined match flags and turns off
     * flags appropriately.
     */
    private void subFlag() {
        int ch = peek();
        for (;;) {
            switch (ch) {
            case 'i':
                flags &= ~CASE_INSENSITIVE;
                break;
            case 'm':
                flags &= ~MULTILINE;
                break;
            case 's':
                flags &= ~DOTALL;
                break;
            case 'd':
                flags &= ~UNIX_LINES;
                break;
            case 'u':
                flags &= ~UNICODE_CASE;
                break;
            case 'c':
                flags &= ~CANON_EQ;
                break;
            case 'x':
                flags &= ~COMMENTS;
                break;
            default:
                return;
            }
            ch = next();
        }
    }

    static final int MAX_REPS   = 0x7FFFFFFF;

    static final int GREEDY     = 0;

    static final int LAZY       = 1;

    static final int POSSESSIVE = 2;

    static final int INDEPENDENT = 3;

    /**
     * Processes repetition. If the next character peeked is a quantifier
     * then new nodes must be appended to handle the repetition.
     * Prev could be a single or a group, so it could be a chain of nodes.
     */
    private Node closure(Node prev) {
        Node atom;
        int ch = peek();
        switch (ch) {
        case '?':
            ch = next();
            if (ch == '?') {
                next();
                return new Ques(prev, LAZY);
            } else if (ch == '+') {
                next();
                return new Ques(prev, POSSESSIVE);
            }
            return new Ques(prev, GREEDY);
        case '*':
            ch = next();
            if (ch == '?') {
                next();
                return new Curly(prev, 0, MAX_REPS, LAZY);
            } else if (ch == '+') {
                next();
                return new Curly(prev, 0, MAX_REPS, POSSESSIVE);
            }
            return new Curly(prev, 0, MAX_REPS, GREEDY);
        case '+':
            ch = next();
            if (ch == '?') {
                next();
                return new Curly(prev, 1, MAX_REPS, LAZY);
            } else if (ch == '+') {
                next();
                return new Curly(prev, 1, MAX_REPS, POSSESSIVE);
            }
            return new Curly(prev, 1, MAX_REPS, GREEDY);
        case '{':
            ch = temp[cursor+1];
            if (ASCII.isDigit(ch)) {
                skip();
                int cmin = 0;
                do {
                    cmin = cmin * 10 + (ch - '0');
                } while (ASCII.isDigit(ch = read()));
                int cmax = cmin;
                if (ch == ',') {
                    ch = read();
                    cmax = MAX_REPS;
                    if (ch != '}') {
                        cmax = 0;
                        while (ASCII.isDigit(ch)) {
                            cmax = cmax * 10 + (ch - '0');
                            ch = read();
                        }
                    }
                }
                if (ch != '}')
                    return error("Unclosed counted closure");
                if (((cmin) | (cmax) | (cmax - cmin)) < 0)
                    return error("Illegal repetition range");
                Curly curly;
                ch = peek();
                if (ch == '?') {
                    next();
                    curly = new Curly(prev, cmin, cmax, LAZY);
                } else if (ch == '+') {
                    next();
                    curly = new Curly(prev, cmin, cmax, POSSESSIVE);
                } else {
                    curly = new Curly(prev, cmin, cmax, GREEDY);
                }
                return curly;
            } else {
                error("Illegal repetition");
            }
            return prev;
        default:
            return prev;
        }
    }

    /**
     *  Utility method for parsing control escape sequences.
     */
    private int c() {
        if (cursor < patternLength) {
            return read() ^ 64;
        }
        error("Illegal control escape sequence");
        return -1;
    }

    /**
     *  Utility method for parsing octal escape sequences.
     */
    private int o() {
        int n = read();
        if (((n-'0')|('7'-n)) >= 0) {
            int m = read();
            if (((m-'0')|('7'-m)) >= 0) {
                int o = read();
                if ((((o-'0')|('7'-o)) >= 0) && (((n-'0')|('3'-n)) >= 0)) {
                    return (n - '0') * 64 + (m - '0') * 8 + (o - '0');
                }
                unread();
                return (n - '0') * 8 + (m - '0');
            }
            unread();
            return (n - '0');
        }
        error("Illegal octal escape sequence");
        return -1;
    }

    /**
     *  Utility method for parsing hexadecimal escape sequences.
     */
    private int x() {
        int n = read();
        if (ASCII.isHexDigit(n)) {
            int m = read();
            if (ASCII.isHexDigit(m)) {
                return ASCII.toDigit(n) * 16 + ASCII.toDigit(m);
            }
        }
        error("Illegal hexadecimal escape sequence");
        return -1;
    }

    /**
     *  Utility method for parsing unicode escape sequences.
     */
    private int u() {
        int n = 0;
        for (int i = 0; i < 4; i++) {
            int ch = read();
            if (!ASCII.isHexDigit(ch)) {
                error("Illegal Unicode escape sequence");
            }
            n = n * 16 + ASCII.toDigit(ch);
        }
        return n;
    }

    /**
     *  Creates a bit vector for matching ASCII values.
     */
    static final class BitClass extends Node {
        boolean[] bits = new boolean[256];
        boolean complementMe = false;
        BitClass(boolean not) {
            complementMe = not;
        }
        BitClass(boolean[] newBits, boolean not) {
            complementMe = not;
            bits = newBits;
        }
        Node add(int c, int f) {
            if ((f & CASE_INSENSITIVE) == 0) {
                bits[c] = true;
                return this;
            }
            if (c < 128) {
                bits[c] = true;
                if (ASCII.isUpper(c)) {
                    c += 0x20;
                    bits[c] = true;
                } else if (ASCII.isLower(c)) {
                    c -= 0x20;
                    bits[c] = true;
                }
                return this;
            }
            c = Character.toLowerCase((char)c);
            bits[c] = true;
            c = Character.toUpperCase((char)c);
            bits[c] = true;
            return this;
        }
        Node dup(boolean not) {
            return new BitClass(bits, not);
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to) {
                int c = seq.charAt(i);
                if (c > 255)
                    return false;
                if (complementMe)
                    return ((!bits[c]) &&
                            next.match(matcher, i+1, seq));
                if (bits[c])
                    return next.match(matcher, i+1, seq);
            }
            return false;
        }
        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    /**
     *  Utility method for creating a single character matcher.
     */
    private Node newSingle(int ch) {
        int f = flags;
        if ((f & CASE_INSENSITIVE) == 0) {
            return new Single(ch);
        }
        if ((f & UNICODE_CASE) == 0) {
            return new SingleA(ch);
        }
        return new SingleU(ch);
    }

    /**
     *  Utility method for creating a string slice matcher.
     */
    private Node newSlice(char[] buf, int count) {
        char[] tmp = new char[count];
        int i = flags;
        if ((i & CASE_INSENSITIVE) == 0) {
            for (i = 0; i < count; i++) {
                tmp[i] = buf[i];
            }
            return new Slice(tmp);
        } else if ((i & UNICODE_CASE) == 0) {
            for (i = 0; i < count; i++) {
                tmp[i] = (char)ASCII.toLower(buf[i]);
            }
            return new SliceA(tmp);
        } else {
            for (i = 0; i < count; i++) {
                char c = buf[i];
                c = Character.toUpperCase(c);
                c = Character.toLowerCase(c);
                tmp[i] = c;
            }
            return new SliceU(tmp);
        }
    }

    /**
     * The following classes are the building components of the object
     * tree that represents a compiled regular expression. The object tree
     * is made of individual elements that handle constructs in the Pattern.
     * Each type of object knows how to match its equivalent construct with
     * the match() method.
     */

    /**
     * Base class for all node classes. Subclasses should override the match()
     * method as appropriate. This class is an accepting node, so its match()
     * always returns true.
     */
    static class Node extends Object {
        Node next;
        Node() {
            next = Pattern.accept;
        }
        Node dup(boolean not) {
            if (not) {
                return new Not(this);
            } else {
                throw new RuntimeException("internal error in Node dup()");
            }
        }
        /**
         * This method implements the classic accept node.
         */
        boolean match(Matcher matcher, int i, CharSequence seq) {
            matcher.last = i;
            matcher.groups[0] = matcher.first;
            matcher.groups[1] = matcher.last;
            return true;
        }
        /**
         * This method is good for all zero length assertions.
         */
        boolean study(TreeInfo info) {
            if (next != null) {
                return next.study(info);
            } else {
                return info.deterministic;
            }
        }
    }

    static class LastNode extends Node {
        /**
         * This method implements the classic accept node with
         * the addition of a check to see if the match occured
         * using all of the input.
         */
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (matcher.acceptMode == Matcher.ENDANCHOR && i != matcher.to)
                return false;
            matcher.last = i;
            matcher.groups[0] = matcher.first;
            matcher.groups[1] = matcher.last;
            return true;
        }
    }

    /**
     * Dummy node to assist in connecting branches.
     */
    static class Dummy extends Node {
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return next.match(matcher, i, seq);
        }
    }

    /**
     * Used for REs that can start anywhere within the input string.
     * This basically tries to match repeatedly at each spot in the
     * input string, moving forward after each try. An anchored search
     * or a BnM will bypass this node completely.
     */
    static final class Start extends Node {
        int minLength;
        Start(Node node) {
            this.next = node;
            TreeInfo info = new TreeInfo();
            next.study(info);
            minLength = info.minLength;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i > matcher.to - minLength)
                return false;
            boolean ret = false;
            int guard = matcher.to - minLength;
            for (; i <= guard; i++) {
                if (ret = next.match(matcher, i, seq))
                    break;
            }
            if (ret) {
                matcher.first = i;
                matcher.groups[0] = matcher.first;
                matcher.groups[1] = matcher.last;
            }
            return ret;
        }
        boolean study(TreeInfo info) {
            next.study(info);
            info.maxValid = false;
            info.deterministic = false;
            return false;
        }
    }

    /**
     * Node to anchor at the beginning of input. This object implements the
     * match for a \A sequence, and the caret anchor will use this if not in
     * multiline mode.
     */
    static final class Begin extends Node {
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i == matcher.from && next.match(matcher, i, seq)) {
                matcher.first = i;
                matcher.groups[0] = i;
                matcher.groups[1] = matcher.last;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Node to anchor at the end of input. This is the absolute end, so this
     * should not match at the last newline before the end as $ will.
     */
    static final class End extends Node {
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return (i == matcher.to && next.match(matcher, i, seq));
        }
    }

    /**
     * Node to anchor at the beginning of a line. This is essentially the
     * object to match for the multiline ^.
     */
    static final class Caret extends Node {
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i > matcher.from) {
                char ch = seq.charAt(i-1);
                if (ch != '\n' && ch != '\r'
                    && (ch|1) != '\u2029'
                    && ch != '\u0085' ) {
                    return false;
                }
                // Should treat /r/n as one newline
                if (ch == '\r' && seq.charAt(i) == '\n')
                    return false;
            }
            // Perl does not match ^ at end of input even after newline
            if (i == matcher.to)
                return false;
            return next.match(matcher, i, seq);
        }
    }

    /**
     * Node to anchor at the beginning of a line when in unixdot mode.
     */
    static final class UnixCaret extends Node {
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i > matcher.from) {
                char ch = seq.charAt(i-1);
                if (ch != '\n') {
                    return false;
                }
            }
            // Perl does not match ^ at end of input even after newline
            if (i == matcher.to)
                return false;
            return next.match(matcher, i, seq);
        }
    }

    /**
     * Node to match the location where the last match ended.
     * This is used for the \G construct.
     */
    static final class LastMatch extends Node {
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i != matcher.oldLast)
                return false;
            return next.match(matcher, i, seq);
        }
    }

    /**
     * Node to anchor at the end of a line or the end of input based on the
     * multiline mode.
     *
     * When not in multiline mode, the $ can only match at the very end
     * of the input, unless the input ends in a line terminator in which
     * it matches right before the last line terminator.
     *
     * Note that \r\n is considered an atomic line terminator.
     * 
     * Like ^ the $ operator matches at a position, it does not match the
     * line terminators themselves.
     */
    static final class Dollar extends Node {
        boolean multiline;
        Dollar(boolean mul) {
            multiline = mul;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (!multiline) {
                if (i < matcher.to - 2)
                    return false;
                if (i == matcher.to - 2) {
                    char ch = seq.charAt(i);
                    if (ch != '\r')
                        return false;
                    ch = seq.charAt(i + 1);
                    if (ch != '\n')
                        return false;
                }
            }
            // Matches before any line terminator; also matches at the
            // end of input
            if (i < matcher.to) {
                char ch = seq.charAt(i);
                 if (ch == '\n') {
                     // No match between \r\n
                     if (i > 0 && seq.charAt(i-1) == '\r')
                         return false;
                 } else if (ch == '\r' || ch == '\u0085' ||
                            (ch|1) == '\u2029') {
                     // line terminator; match
                 } else { // No line terminator, no match
                     return false;
                 }
            }
            return next.match(matcher, i, seq);
        }
        boolean study(TreeInfo info) {
            next.study(info);
            return info.deterministic;
        }
    }

    /**
     * Node to anchor at the end of a line or the end of input based on the
     * multiline mode when in unix lines mode.
     */
    static final class UnixDollar extends Node {
        boolean multiline;
        UnixDollar(boolean mul) {
            multiline = mul;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to) {
                char ch = seq.charAt(i);
                if (ch == '\n') {
                    // If not multiline, then only possible to
                    // match at very end or one before end
                    if (multiline == false && i != matcher.to - 1)
                        return false;
                } else {
                    return false;
                }
            }
            return next.match(matcher, i, seq);
        }
        boolean study(TreeInfo info) {
            next.study(info);
            return info.deterministic;
        }
    }

    /**
     * Node class for a single character value.
     */
    static final class Single extends Node {
        int ch;
        Single(int n) {
            ch = n;
        }
        Node dup(boolean not) {
            if (not)
                return new NotSingle(ch);
            else
                return new Single(ch);
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return (i < matcher.to
                && seq.charAt(i) == ch
                && next.match(matcher, i+1, seq));
        }
        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    /**
     * Node class to match any character except a single char value.
     */
    static final class NotSingle extends Node {
        int ch;
        NotSingle(int n) {
            ch = n;
        }
        Node dup(boolean not) {
            if (not)
                return new Single(ch);
            else
                return new NotSingle(ch);
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return (i < matcher.to
                && seq.charAt(i) != ch
                && next.match(matcher, i+1, seq));
        }
        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    /**
     * Case independent ASCII value.
     */
    static final class SingleA extends Node {
        int ch;
        SingleA(int n) {
            ch = ASCII.toLower(n);
        }
        Node dup(boolean not) {
            if (not)
                return new NotSingleA(ch);
            else
                return new SingleA(ch);
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to) {
                int c = seq.charAt(i);
                if (c == ch || ASCII.toLower(c) == ch) {
                    return next.match(matcher, i+1, seq);
                }
            }
            return false;
        }

        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    static final class NotSingleA extends Node {
        int ch;
        NotSingleA(int n) {
            ch = ASCII.toLower(n);
        }
        Node dup(boolean not) {
            if (not)
                return new SingleA(ch);
            else
                return new NotSingleA(ch);
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to) {
                int c = seq.charAt(i);
                if (c != ch && ASCII.toLower(c) != ch) {
                    return next.match(matcher, i+1, seq);
                }
            }
            return false;
        }

        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    /**
     * Case independent unicode value.
     */
    static final class SingleU extends Node {
        int ch;
        SingleU(int c) {
            ch = Character.toLowerCase(Character.toUpperCase((char)c));
        }
        Node dup(boolean not) {
            if (not)
                return new NotSingleU(ch);
            else
                return new SingleU(ch);
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to) {
                char c = seq.charAt(i);
                if (c == ch)
                    return next.match(matcher, i+1, seq);
                c = Character.toUpperCase(c);
                c = Character.toLowerCase(c);
                if (c == ch)
                    return next.match(matcher, i+1, seq);
            }
            return false;
        }
        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    /**
     * Case independent unicode value.
     */
    static final class NotSingleU extends Node {
        int ch;
        NotSingleU(int c) {
            ch = Character.toLowerCase(Character.toUpperCase((char)c));
        }
        Node dup(boolean not) {
            if (not)
                return new SingleU(ch);
            else
                return new NotSingleU(ch);
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to) {
                char c = seq.charAt(i);
                if (c == ch)
                    return false;
                c = Character.toUpperCase(c);
                c = Character.toLowerCase(c);
                if (c != ch)
                    return next.match(matcher, i+1, seq);
            }
            return false;
        }
        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    /**
     * Node class that matches a Unicode category.
     */
    static final class Category extends Node {
        int atype;
        Category(int type) {
            atype = type;
        }
        Node dup(boolean not) {
            return new Category(not ? ~atype : atype);
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return i < matcher.to
                && (atype & (1 << Character.getType(seq.charAt(i)))) != 0
                && next.match(matcher, i+1, seq);
        }
        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    /**
     * Node class that matches a POSIX type.
     */
    static final class Ctype extends Node {
        int ctype;
        Ctype(int type) {
            ctype = type;
        }
        Node dup(boolean not) {
            if (not) {
                return new NotCtype(ctype);
            } else {
                return new Ctype(ctype);
            }
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return (i < matcher.to
                && ASCII.isType(seq.charAt(i), ctype)
                && next.match(matcher, i+1, seq));
        }
        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    static final class NotCtype extends Node {
        int ctype;
        NotCtype(int type) {
            ctype = type;
        }
        Node dup(boolean not) {
            if (not) {
                return new Ctype(ctype);
            } else {
                return new NotCtype(ctype);
            }
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return (i < matcher.to
                && !ASCII.isType(seq.charAt(i), ctype)
                && next.match(matcher, i+1, seq));
        }
        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    static final class Specials extends Node {
        Specials() {
        }
        Node dup(boolean not) {
            if (not)
                return new Not(this);
            else
                return new Specials();
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to) {
                int ch = seq.charAt(i);
                return (((ch-0xFFF0) | (0xFFFD-ch)) >= 0 || ch == 0xFEFF)
                    && next.match(matcher, i+1, seq);
            }
            return false;
        }
        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    static final class Not extends Node {
        Node atom;
        Not(Node atom) {
            this.atom = atom;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return !atom.match(matcher, i, seq) && next.match(matcher, i+1, seq);
        }
        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    /**
     * Node class for a case sensitive sequence of literal characters.
     */
    static final class Slice extends Node {
        char[] buffer;
        Slice(char[] buf) {
            buffer = buf;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            char[] buf = buffer;
            int len = buf.length;
            if (i + len > matcher.to)
                return false;

            for (int j = 0; j < len; j++)
                if (buf[j] != seq.charAt(i+j))
                    return false;

            return next.match(matcher, i+len, seq);
        }
        boolean study(TreeInfo info) {
            info.minLength += buffer.length;
            info.maxLength += buffer.length;
            return next.study(info);
        }
    }

    /**
     * Node class for a case insensitive sequence of literal characters.
     */
    static final class SliceA extends Node {
        char[] buffer;
        SliceA(char[] buf) {
            buffer = buf;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            char[] buf = buffer;
            int len = buf.length;
            if (i + len > matcher.to) {
                return false;
            }
            for (int j = 0; j < len; j++) {
                int c = ASCII.toLower(seq.charAt(i+j));
                if (buf[j] != c) {
                    return false;
                }
            }
            return next.match(matcher, i+len, seq);
        }
        boolean study(TreeInfo info) {
            info.minLength += buffer.length;
            info.maxLength += buffer.length;
            return next.study(info);
        }
    }

    /**
     * Node class for a case insensitive sequence of literal characters.
     * Uses unicode case folding.
     */
    static final class SliceU extends Node {
        char[] buffer;
        SliceU(char[] buf) {
            buffer = buf;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            char[] buf = buffer;
            int len = buf.length;
            if (i + len > matcher.to) {
                return false;
            }
            for (int j = 0; j < len; j++) {
                char c = seq.charAt(i+j);
                c = Character.toUpperCase(c);
                c = Character.toLowerCase(c);
                if (buf[j] != c) {
                    return false;
                }
            }
            return next.match(matcher, i+len, seq);
        }
        boolean study(TreeInfo info) {
            info.minLength += buffer.length;
            info.maxLength += buffer.length;
            return next.study(info);
        }
    }

    /**
     * Node class for matching characters within an explicit value range.
     */
    static class Range extends Node {
        int lower, upper;
        Range() {
        }
        Range(int n) {
            lower = n >>> 16;
            upper = n & 0xFFFF;
        }
        Node dup(boolean not) {
            if (not)
                return new NotRange((lower << 16) + upper);
            else
                return new Range((lower << 16) + upper);
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to) {
                char ch = seq.charAt(i);
                return ((ch-lower)|(upper-ch)) >= 0
                    && next.match(matcher, i+1, seq);
            }
            return false;
        }
        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    /**
     * Node class for matching characters within an explicit value range
     * in a case insensitive manner.
     */
    static final class CIRange extends Range {
        CIRange(int n) {
            lower = n >>> 16;
            upper = n & 0xFFFF;
        }
        Node dup(boolean not) {
            if (not)
                return new CINotRange((lower << 16) + upper);
            else
                return new CIRange((lower << 16) + upper);
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to) {
                char ch = seq.charAt(i);
                boolean m = (((ch-lower)|(upper-ch)) >= 0);
                if (!m) {
                    ch = Character.toUpperCase(ch);
                    m = (((ch-lower)|(upper-ch)) >= 0);
                    if (!m) {
                        ch = Character.toLowerCase(ch);
                        m = (((ch-lower)|(upper-ch)) >= 0);
                    }
                }
                return (m && next.match(matcher, i+1, seq));
            }
            return false;
        }
    }

    static class NotRange extends Node {
        int lower, upper;
        NotRange() {
        }
        NotRange(int n) {
            lower = n >>> 16;
            upper = n & 0xFFFF;
        }
        Node dup(boolean not) {
            if (not) {
                return new Range((lower << 16) + upper);
            } else {
                return new NotRange((lower << 16) + upper);
            }
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to) {
                char ch = seq.charAt(i);
                return ((ch-lower)|(upper-ch)) < 0
                    && next.match(matcher, i+1, seq);
            }
            return false;
        }
        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    static class CINotRange extends NotRange {
        int lower, upper;
        CINotRange(int n) {
            lower = n >>> 16;
            upper = n & 0xFFFF;
        }
        Node dup(boolean not) {
            if (not) {
                return new CIRange((lower << 16) + upper);
            } else {
                return new CINotRange((lower << 16) + upper);
            }
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to) {
                char ch = seq.charAt(i);
                boolean m = (((ch-lower)|(upper-ch)) < 0);
                if (m) {
                    ch = Character.toUpperCase(ch);
                    m = (((ch-lower)|(upper-ch)) < 0);
                    if (m) {
                        ch = Character.toLowerCase(ch);
                        m = (((ch-lower)|(upper-ch)) < 0);
                    }
                }

                return (m && next.match(matcher, i+1, seq));
            }
            return false;
        }
    }

    /**
     * Implements the Unicode category ALL and the dot metacharacter when
     * in dotall mode.
     */
    static final class All extends Node {
        All() {
            super();
        }
        Node dup(boolean not) {
            if (not) {
                return new Single(-1);
            } else {
                return new All();
            }
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return (i < matcher.to && next.match(matcher, i+1, seq));
        }
        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    /**
     * Node class for the dot metacharacter when dotall is not enabled.
     */
    static final class Dot extends Node {
        Dot() {
            super();
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to) {
                char ch = seq.charAt(i);
                return (ch != '\n' && ch != '\r'
                    && (ch|1) != '\u2029'
                    && ch != '\u0085'
                    && next.match(matcher, i+1, seq));
            }
            return false;
        }
        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    /**
     * Node class for the dot metacharacter when dotall is not enabled
     * but UNIX_LINES is enabled.
     */
    static final class UnixDot extends Node {
        UnixDot() {
            super();
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to) {
                char ch = seq.charAt(i);
                return (ch != '\n' && next.match(matcher, i+1, seq));
            }
            return false;
        }
        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    /**
     * The 0 or 1 quantifier. This one class implements all three types.
     */
    static final class Ques extends Node {
        Node atom;
        int type;
        Ques(Node node, int type) {
            this.atom = node;
            this.type = type;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            switch (type) {
            case GREEDY:
                return (atom.match(matcher, i, seq) && next.match(matcher, matcher.last, seq))
                    || next.match(matcher, i, seq);
            case LAZY:
                return next.match(matcher, i, seq)
                    || (atom.match(matcher, i, seq) && next.match(matcher, matcher.last, seq));
            case POSSESSIVE:
                if (atom.match(matcher, i, seq)) i = matcher.last;
                return next.match(matcher, i, seq);
            default:
                return atom.match(matcher, i, seq) && next.match(matcher, matcher.last, seq);
            }
        }
        boolean study(TreeInfo info) {
            if (type != INDEPENDENT) {
                int minL = info.minLength;
                atom.study(info);
                info.minLength = minL;
                info.deterministic = false;
                return next.study(info);
            } else {
                atom.study(info);
                return next.study(info);
            }
        }
    }

    /**
     * Handles the curly-brace style repetition with a specified minimum and
     * maximum occurrences. The * quantifier is handled as a special case.
     * This class handles the three types.
     */
    static final class Curly extends Node {
        Node atom;
        int type;
        int cmin;
        int cmax;

        Curly(Node node, int cmin, int cmax, int type) {
            this.atom = node;
            this.type = type;
            this.cmin = cmin;
            this.cmax = cmax;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            int j;
            for (j = 0; j < cmin; j++) {
                if (atom.match(matcher, i, seq)) {
                    i = matcher.last;
                    continue;
                }
                return false;
            }
            if (type == GREEDY)
                return match0(matcher, i, j, seq);
            else if (type == LAZY)
                return match1(matcher, i, j, seq);
            else
                return match2(matcher, i, j, seq);
        }
        // Greedy match.
        // i is the index to start matching at
        // j is the number of atoms that have matched
        boolean match0(Matcher matcher, int i, int j, CharSequence seq) {
            if (j >= cmax) {
                // We have matched the maximum... continue with the rest of
                // the regular expression
                return next.match(matcher, i, seq);
            }
            int backLimit = j;
            while (atom.match(matcher, i, seq)) {
                // k is the length of this match
                int k = matcher.last - i;
                if (k == 0) // Zero length match
                    break;
                // Move up index and number matched
                i = matcher.last;
                j++;
                // We are greedy so match as many as we can
                while (j < cmax) {
                    if (!atom.match(matcher, i, seq))
                        break;
                    if (i + k != matcher.last) {
                        if (match0(matcher, matcher.last, j+1, seq))
                            return true;
                        break;
                    }
                    i += k;
                    j++;
                }
                // Handle backing off if match fails
                while (j >= backLimit) {
                   if (next.match(matcher, i, seq))
                        return true;
                    i -= k;
                    j--;
                }
                return false;
            }
            return next.match(matcher, i, seq);
        }
        // Reluctant match. At this point, the minimum has been satisfied.
        // i is the index to start matching at
        // j is the number of atoms that have matched
        boolean match1(Matcher matcher, int i, int j, CharSequence seq) {
            for (;;) {
                // Try finishing match without consuming any more
                if (next.match(matcher, i, seq))
                    return true;
                // At the maximum, no match found
                if (j >= cmax)
                    return false;
                // Okay, must try one more atom
                if (!atom.match(matcher, i, seq))
                    return false;
                // If we haven't moved forward then must break out
                if (i == matcher.last)
                    return false;
                // Move up index and number matched
                i = matcher.last;
                j++;
            }
        }
        boolean match2(Matcher matcher, int i, int j, CharSequence seq) {
            for (; j < cmax; j++) {
                if (!atom.match(matcher, i, seq))
                    break;
                if (i == matcher.last)
                    break;
                i = matcher.last;
            }
            return next.match(matcher, i, seq);
        }
        boolean study(TreeInfo info) {
            // Save original info
            int minL = info.minLength;
            int maxL = info.maxLength;
            boolean maxV = info.maxValid;
            boolean detm = info.deterministic;
            info.reset();

            atom.study(info);

            int temp = info.minLength * cmin + minL;
            if (temp < minL) {
                temp = 0xFFFFFFF; // arbitrary large number
            }
            info.minLength = temp;

            if (maxV & info.maxValid) {
                temp = info.maxLength * cmax + maxL;
                info.maxLength = temp;
                if (temp < maxL) {
                    info.maxValid = false;
                }
            } else {
                info.maxValid = false;
            }

            if (info.deterministic && cmin == cmax)
                info.deterministic = detm;
            else
                info.deterministic = false;

            return next.study(info);
        }
    }

    /**
     * Handles the curly-brace style repetition with a specified minimum and
     * maximum occurrences in deterministic cases. This is an iterative
     * optimization over the Prolog and Loop system which would handle this
     * in a recursive way. The * quantifier is handled as a special case.
     * This class saves group settings so that the groups are unset when
     * backing off of a group match.
     */
    static final class GroupCurly extends Node {
        Node atom;
        int type;
        int cmin;
        int cmax;
        int localIndex;
        int groupIndex;

        GroupCurly(Node node, int cmin, int cmax, int type, int local,
                   int group) {
            this.atom = node;
            this.type = type;
            this.cmin = cmin;
            this.cmax = cmax;
            this.localIndex = local;
            this.groupIndex = group;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            int[] groups = matcher.groups;
            int[] locals = matcher.locals;
            int save0 = locals[localIndex];
            int save1 = groups[groupIndex];
            int save2 = groups[groupIndex+1];

            // Notify GroupTail there is no need to setup group info
            // because it will be set here
            locals[localIndex] = -1;

            boolean ret = true;
            for (int j = 0; j < cmin; j++) {
                if (atom.match(matcher, i, seq)) {
                    groups[groupIndex] = i;
                    groups[groupIndex+1] = i = matcher.last;
                } else {
                    ret = false;
                    break;
                }
            }
            if (!ret) {
                ;
            } else if (type == GREEDY) {
                ret = match0(matcher, i, cmin, seq);
            } else if (type == LAZY) {
                ret = match1(matcher, i, cmin, seq);
            } else {
                ret = match2(matcher, i, cmin, seq);
            }
            if (!ret) {
                locals[localIndex] = save0;
                groups[groupIndex] = save1;
                groups[groupIndex+1] = save2;
            }
            return ret;
        }
        // Aggressive group match
        boolean match0(Matcher matcher, int i, int j, CharSequence seq) {
            int[] groups = matcher.groups;
            int save0 = groups[groupIndex];
            int save1 = groups[groupIndex+1];
            for (;;) {
                if (j >= cmax)
                    break;
                if (!atom.match(matcher, i, seq))
                    break;
                int k = matcher.last - i;
                if (k <= 0) {
                    groups[groupIndex] = i;
                    groups[groupIndex+1] = i = i + k;
                    break;
                }
                for (;;) {
                    groups[groupIndex] = i;
                    groups[groupIndex+1] = i = i + k;
                    if (++j >= cmax)
                        break;
                    if (!atom.match(matcher, i, seq))
                        break;
                    if (i + k != matcher.last) {
                        if (match0(matcher, i, j, seq))
                            return true;
                        break;
                    }
                }
                while (j > cmin) {
                    if (next.match(matcher, i, seq)) {
                        groups[groupIndex+1] = i;
                        groups[groupIndex] = i = i - k;
                        return true;
                    }
                    // backing off
                    groups[groupIndex+1] = i;
                    groups[groupIndex] = i = i - k;
                    j--;
                }
                break;
            }
            groups[groupIndex] = save0;
            groups[groupIndex+1] = save1;
            return next.match(matcher, i, seq);
        }
        // Reluctant matching
        boolean match1(Matcher matcher, int i, int j, CharSequence seq) {
            for (;;) {
                if (next.match(matcher, i, seq))
                    return true;
                if (j >= cmax)
                    return false;
                if (!atom.match(matcher, i, seq))
                    return false;
                if (i == matcher.last)
                    return false;

                matcher.groups[groupIndex] = i;
                matcher.groups[groupIndex+1] = i = matcher.last;
                j++;
            }
        }
        // Possessive matching
        boolean match2(Matcher matcher, int i, int j, CharSequence seq) {
            for (; j < cmax; j++) {
                if (!atom.match(matcher, i, seq)) {
                    break;
                }
                matcher.groups[groupIndex] = i;
                matcher.groups[groupIndex+1] = matcher.last;
                if (i == matcher.last) {
                    break;
                }
                i = matcher.last;
            }
            return next.match(matcher, i, seq);
        }
        boolean study(TreeInfo info) {
            // Save original info
            int minL = info.minLength;
            int maxL = info.maxLength;
            boolean maxV = info.maxValid;
            boolean detm = info.deterministic;
            info.reset();

            atom.study(info);

            int temp = info.minLength * cmin + minL;
            if (temp < minL) {
                temp = 0xFFFFFFF; // Arbitrary large number
            }
            info.minLength = temp;

            if (maxV & info.maxValid) {
                temp = info.maxLength * cmax + maxL;
                info.maxLength = temp;
                if (temp < maxL) {
                    info.maxValid = false;
                }
            } else {
                info.maxValid = false;
            }

            if (info.deterministic && cmin == cmax) {
                info.deterministic = detm;
            } else {
                info.deterministic = false;
            }

            return next.study(info);
        }
    }

    /**
     * Handles the branching of alternations. Note this is also used for
     * the ? quantifier to branch between the case where it matches once
     * and where it does not occur.
     */
    static final class Branch extends Node {
        Node prev;
        Branch(Node lhs, Node rhs) {
            this.prev = lhs;
            this.next = rhs;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return (prev.match(matcher, i, seq) || next.match(matcher, i, seq));
        }
        boolean study(TreeInfo info) {
            int minL = info.minLength;
            int maxL = info.maxLength;
            boolean maxV = info.maxValid;
            info.reset();
            prev.study(info);

            int minL2 = info.minLength;
            int maxL2 = info.maxLength;
            boolean maxV2 = info.maxValid;
            info.reset();
            next.study(info);

            info.minLength = minL + Math.min(minL2, info.minLength);
            info.maxLength = maxL + Math.max(maxL2, info.maxLength);
            info.maxValid = (maxV & maxV2 & info.maxValid);
            info.deterministic = false;
            return false;
        }
    }

    /**
     * The GroupHead saves the location where the group begins in the locals
     * and restores them when the match is done.
     *
     * The matchRef is used when a reference to this group is accessed later
     * in the expression. The locals will have a negative value in them to
     * indicate that we do not want to unset the group if the reference
     * doesn't match.
     */
    static final class GroupHead extends Node {
        int localIndex;
        GroupHead(int localCount) {
            localIndex = localCount;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            int save = matcher.locals[localIndex];
            matcher.locals[localIndex] = i;
            boolean ret = next.match(matcher, i, seq);
            matcher.locals[localIndex] = save;
            return ret;
        }
        boolean matchRef(Matcher matcher, int i, CharSequence seq) {
            int save = matcher.locals[localIndex];
            matcher.locals[localIndex] = ~i; // HACK
            boolean ret = next.match(matcher, i, seq);
            matcher.locals[localIndex] = save;
            return ret;
        }
    }

    /**
     * Recursive reference to a group in the regular expression. It calls
     * matchRef because if the reference fails to match we would not unset
     * the group.
     */
    static final class GroupRef extends Node {
        GroupHead head;
        GroupRef(GroupHead head) {
            this.head = head;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return head.matchRef(matcher, i, seq)
                && next.match(matcher, matcher.last, seq);
        }
        boolean study(TreeInfo info) {
            info.maxValid = false;
            info.deterministic = false;
            return next.study(info);
        }
    }

    /**
     * The GroupTail handles the setting of group beginning and ending
     * locations when groups are successfully matched. It must also be able to
     * unset groups that have to be backed off of.
     *
     * The GroupTail node is also used when a previous group is referenced,
     * and in that case no group information needs to be set.
     */
    static final class GroupTail extends Node {
        int localIndex;
        int groupIndex;
        GroupTail(int localCount, int groupCount) {
            localIndex = localCount;
            groupIndex = groupCount + groupCount;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            int tmp = matcher.locals[localIndex];
            if (tmp >= 0) { // This is the normal group case.
                // Save the group so we can unset it if it
                // backs off of a match.
                int groupStart = matcher.groups[groupIndex];
                int groupEnd = matcher.groups[groupIndex+1];

                matcher.groups[groupIndex] = tmp;
                matcher.groups[groupIndex+1] = i;
                if (next.match(matcher, i, seq)) {
                    return true;
                }
                matcher.groups[groupIndex] = groupStart;
                matcher.groups[groupIndex+1] = groupEnd;
                return false;
            } else {
                // This is a group reference case. We don't need to save any
                // group info because it isn't really a group.
                matcher.last = i;
                return true;
            }
        }
    }

    /**
     * This sets up a loop to handle a recursive quantifier structure.
     */
    static final class Prolog extends Node {
        Loop loop;
        Prolog(Loop loop) {
            this.loop = loop;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return loop.matchInit(matcher, i, seq);
        }
        boolean study(TreeInfo info) {
            return loop.study(info);
        }
    }

    /**
     * Handles the repetition count for a greedy Curly. The matchInit
     * is called from the Prolog to save the index of where the group
     * beginning is stored. A zero length group check occurs in the
     * normal match but is skipped in the matchInit.
     */
    static class Loop extends Node {
        Node body;
        int countIndex; // local count index in matcher locals
        int beginIndex; // group begining index
        int cmin, cmax;
        Loop(int countIndex, int beginIndex) {
            this.countIndex = countIndex;
            this.beginIndex = beginIndex;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            // Avoid infinite loop in zero-length case.
            if (i > matcher.locals[beginIndex]) {
                int count = matcher.locals[countIndex];

                // This block is for before we reach the minimum
                // iterations required for the loop to match
                if (count < cmin) {
                    matcher.locals[countIndex] = count + 1;
                    boolean b = body.match(matcher, i, seq);
                    // If match failed we must backtrack, so
                    // the loop count should NOT be incremented
                    if (!b)
                        matcher.locals[countIndex] = count;
                    // Return success or failure since we are under
                    // minimum
                    return b;
                }
                // This block is for after we have the minimum
                // iterations required for the loop to match
                if (count < cmax) {
                    matcher.locals[countIndex] = count + 1;
                    boolean b = body.match(matcher, i, seq);
                    // If match failed we must backtrack, so
                    // the loop count should NOT be incremented
                    if (!b)
                        matcher.locals[countIndex] = count;
                    else
                        return true;
                }
            }
            return next.match(matcher, i, seq);
        }
        boolean matchInit(Matcher matcher, int i, CharSequence seq) {
            int save = matcher.locals[countIndex];
            boolean ret = false;
            if (0 < cmin) {
                matcher.locals[countIndex] = 1;
                ret = body.match(matcher, i, seq);
            } else if (0 < cmax) {
                matcher.locals[countIndex] = 1;
                ret = body.match(matcher, i, seq);
                if (ret == false)
                    ret = next.match(matcher, i, seq);
            } else {
                ret = next.match(matcher, i, seq);
            }
            matcher.locals[countIndex] = save;
            return ret;
        }
        boolean study(TreeInfo info) {
            info.maxValid = false;
            info.deterministic = false;
            return false;
        }
    }

    /**
     * Handles the repetition count for a reluctant Curly. The matchInit
     * is called from the Prolog to save the index of where the group
     * beginning is stored. A zero length group check occurs in the
     * normal match but is skipped in the matchInit.
     */
    static final class LazyLoop extends Loop {
        LazyLoop(int countIndex, int beginIndex) {
            super(countIndex, beginIndex);
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            // Check for zero length group
            if (i > matcher.locals[beginIndex]) {
                int count = matcher.locals[countIndex];
                if (count < cmin) {
                    matcher.locals[countIndex] = count + 1;
                    boolean result = body.match(matcher, i, seq);
                    // If match failed we must backtrack, so
                    // the loop count should NOT be incremented
                    if (!result)
                        matcher.locals[countIndex] = count;
                    return result;
                }
                if (next.match(matcher, i, seq))
                    return true;
                if (count < cmax) {
                    matcher.locals[countIndex] = count + 1;
                    boolean result = body.match(matcher, i, seq);
                    // If match failed we must backtrack, so
                    // the loop count should NOT be incremented
                    if (!result)
                        matcher.locals[countIndex] = count;
                    return result;
                }
                return false;
            }
            return next.match(matcher, i, seq);
        }
        boolean matchInit(Matcher matcher, int i, CharSequence seq) {
            int save = matcher.locals[countIndex];
            boolean ret = false;
            if (0 < cmin) {
                matcher.locals[countIndex] = 1;
                ret = body.match(matcher, i, seq);
            } else if (next.match(matcher, i, seq)) {
                ret = true;
            } else if (0 < cmax) {
                matcher.locals[countIndex] = 1;
                ret = body.match(matcher, i, seq);
            }
            matcher.locals[countIndex] = save;
            return ret;
        }
        boolean study(TreeInfo info) {
            info.maxValid = false;
            info.deterministic = false;
            return false;
        }
    }

    /**
     * Refers to a group in the regular expression. Attempts to match
     * whatever the group referred to last matched.
     */
    static class BackRef extends Node {
        int groupIndex;
        BackRef(int groupCount) {
            super();
            groupIndex = groupCount + groupCount;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            int j = matcher.groups[groupIndex];
            int k = matcher.groups[groupIndex+1];

            int groupSize = k - j;

            // If the referenced group didn't match, neither can this
            if (j < 0)
                return false;

            // If there isn't enough input left no match
            if (i + groupSize > matcher.to)
                return false;

            // Check each new char to make sure it matches what the group
            // referenced matched last time around
            for (int index=0; index<groupSize; index++)
                if (seq.charAt(i+index) != seq.charAt(j+index))
                    return false;

            return next.match(matcher, i+groupSize, seq);
        }
        boolean study(TreeInfo info) {
            info.maxValid = false;
            return next.study(info);
        }
    }

    static class CIBackRef extends Node {
        int groupIndex;
        CIBackRef(int groupCount) {
            super();
            groupIndex = groupCount + groupCount;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            int j = matcher.groups[groupIndex];
            int k = matcher.groups[groupIndex+1];

            int groupSize = k - j;

            // If the referenced group didn't match, neither can this
            if (j < 0)
                return false;

            // If there isn't enough input left no match
            if (i + groupSize > matcher.to)
                return false;

            // Check each new char to make sure it matches what the group
            // referenced matched last time around
            for (int index=0; index<groupSize; index++) {
                char c1 = seq.charAt(i+index);
                char c2 = seq.charAt(j+index);
                if (c1 != c2) {
                    c1 = Character.toUpperCase(c1);
                    c2 = Character.toUpperCase(c2);
                    if (c1 != c2) {
                        c1 = Character.toLowerCase(c1);
                        c2 = Character.toLowerCase(c2);
                        if (c1 != c2)
                            return false;
                    }
                }
            }

            return next.match(matcher, i+groupSize, seq);
        }
        boolean study(TreeInfo info) {
            info.maxValid = false;
            return next.study(info);
        }
    }

    /**
     * Searches until the next instance of its atom. This is useful for
     * finding the atom efficiently without passing an instance of it
     * (greedy problem) and without a lot of wasted search time (reluctant
     * problem).
     */
    static final class First extends Node {
        Node atom;
        First(Node node) {
            this.atom = BnM.optimize(node);
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (atom instanceof BnM) {
                return atom.match(matcher, i, seq)
                    && next.match(matcher, matcher.last, seq);
            }
            for (;;) {
                if (i > matcher.to) {
                    return false;
                }
                if (atom.match(matcher, i, seq)) {
                    return next.match(matcher, matcher.last, seq);
                }
                i++;
                matcher.first++;
            }
        }
        boolean study(TreeInfo info) {
            atom.study(info);
            info.maxValid = false;
            info.deterministic = false;
            return next.study(info);
        }
    }

    static final class Conditional extends Node {
        Node cond, yes, not;
        Conditional(Node cond, Node yes, Node not) {
            this.cond = cond;
            this.yes = yes;
            this.not = not;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (cond.match(matcher, i, seq)) {
                return yes.match(matcher, i, seq);
            } else {
                return not.match(matcher, i, seq);
            }
        }
        boolean study(TreeInfo info) {
            int minL = info.minLength;
            int maxL = info.maxLength;
            boolean maxV = info.maxValid;
            info.reset();
            yes.study(info);

            int minL2 = info.minLength;
            int maxL2 = info.maxLength;
            boolean maxV2 = info.maxValid;
            info.reset();
            not.study(info);

            info.minLength = minL + Math.min(minL2, info.minLength);
            info.maxLength = maxL + Math.max(maxL2, info.maxLength);
            info.maxValid = (maxV & maxV2 & info.maxValid);
            info.deterministic = false;
            return next.study(info);
        }
    }

    /**
     * Zero width positive lookahead.
     */
    static final class Pos extends Node {
        Node cond;
        Pos(Node cond) {
            this.cond = cond;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return cond.match(matcher, i, seq) && next.match(matcher, i, seq);
        }
    }

    /**
     * Zero width negative lookahead.
     */
    static final class Neg extends Node {
        Node cond;
        Neg(Node cond) {
            this.cond = cond;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return !cond.match(matcher, i, seq) && next.match(matcher, i, seq);
        }
    }

    /**
     * Zero width positive lookbehind.
     */
    static final class Behind extends Node {
        Node cond;
        int rmax, rmin;
        Behind(Node cond, int rmax, int rmin) {
            this.cond = cond;
            this.rmax = rmax;
            this.rmin = rmin;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            int from = Math.max(i - rmax, matcher.from);
            for (int j = i - rmin; j >= from; j--) {
                if (cond.match(matcher, j, seq) && matcher.last == i) {
                    return next.match(matcher, i, seq);
                }
            }
            return false;
        }
    }

    /**
     * Zero width negative lookbehind.
     */
    static final class NotBehind extends Node {
        Node cond;
        int rmax, rmin;
        NotBehind(Node cond, int rmax, int rmin) {
            this.cond = cond;
            this.rmax = rmax;
            this.rmin = rmin;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            int from = Math.max(i - rmax, matcher.from);
            for (int j = i - rmin; j >= from; j--) {
                if (cond.match(matcher, j, seq) && matcher.last == i) {
                    return false;
                }
            }
            return next.match(matcher, i, seq);
        }
    }

    /**
     * An object added to the tree when a character class has an additional
     * range added to it.
     */
    static class Add extends Node {
        Node lhs, rhs;
        Add(Node lhs, Node rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to)
            return ((lhs.match(matcher, i, seq) || rhs.match(matcher, i, seq))
                && next.match(matcher, matcher.last, seq));
            return false;
        }
        boolean study(TreeInfo info) {
            boolean maxV = info.maxValid;
            boolean detm = info.deterministic;

            int minL = info.minLength;
            int maxL = info.maxLength;
            lhs.study(info);

            int minL2 = info.minLength;
            int maxL2 = info.maxLength;

            info.minLength = minL;
            info.maxLength = maxL;
            rhs.study(info);

            info.minLength = Math.min(minL2, info.minLength);
            info.maxLength = Math.max(maxL2, info.maxLength);
            info.maxValid = maxV;
            info.deterministic = detm;

            return next.study(info);
        }
    }

    /**
     * An object added to the tree when a character class has another
     * nested class in it.
     */
    static class Both extends Node {
        Node lhs, rhs;
        Both(Node lhs, Node rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to)
            return ((lhs.match(matcher, i, seq) && rhs.match(matcher, i, seq))
                && next.match(matcher, matcher.last, seq));
            return false;
        }
        boolean study(TreeInfo info) {
            boolean maxV = info.maxValid;
            boolean detm = info.deterministic;

            int minL = info.minLength;
            int maxL = info.maxLength;
            lhs.study(info);

            int minL2 = info.minLength;
            int maxL2 = info.maxLength;

            info.minLength = minL;
            info.maxLength = maxL;
            rhs.study(info);

            info.minLength = Math.min(minL2, info.minLength);
            info.maxLength = Math.max(maxL2, info.maxLength);
            info.maxValid = maxV;
            info.deterministic = detm;

            return next.study(info);
        }
    }

    /**
     * An object added to the tree when a character class has a range
     * or single subtracted from it.
     */
    static final class Sub extends Add  {
        Sub(Node lhs, Node rhs) {
            super(lhs, rhs);
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to)
                return !rhs.match(matcher, i, seq)
                    && lhs.match(matcher, i, seq)
                    && next.match(matcher, matcher.last, seq);
            return false;
        }
        boolean study(TreeInfo info) {
            lhs.study(info);
            return next.study(info);
        }
    }

    /**
     * Handles word boundaries. Includes a field to allow this one class to
     * deal with the different types of word boundaries we can match. The word
     * characters include underscores, letters, and digits.
     */
    static final class Bound extends Node {
        static int LEFT = 0x1;
        static int RIGHT= 0x2;
        static int BOTH = 0x3;
        static int NONE = 0x4;
        int type;
        Bound(int n) {
            type = n;
        }
        int check(Matcher matcher, int i, CharSequence seq) {
            char ch;
            boolean left = false;
            if (i > matcher.from) {
                ch = seq.charAt(i-1);
                left = (ch == '_' || Character.isLetterOrDigit(ch));
            }
            boolean right = false;
            if (i < matcher.to) {
                ch = seq.charAt(i);
                right = (ch == '_' || Character.isLetterOrDigit(ch));
            }
            return ((left ^ right) ? (right ? LEFT : RIGHT) : NONE);
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return (check(matcher, i, seq) & type) > 0
                && next.match(matcher, i, seq);
        }
    }

    /**
     * Attempts to match a slice in the input using the Boyer-Moore string
     * matching algorithm. The algorithm is based on the idea that the
     * pattern can be shifted farther ahead in the search text if it is
     * matched right to left.
     * <p>
     * The pattern is compared to the input one character at a time, from
     * the rightmost character in the pattern to the left. If the characters
     * all match the pattern has been found. If a character does not match,
     * the pattern is shifted right a distance that is the maximum of two
     * functions, the bad character shift and the good suffix shift. This
     * shift moves the attempted match position through the input more
     * quickly than a naive one postion at a time check.
     * <p>
     * The bad character shift is based on the character from the text that
     * did not match. If the character does not appear in the pattern, the
     * pattern can be shifted completely beyond the bad character. If the
     * character does occur in the pattern, the pattern can be shifted to
     * line the pattern up with the next occurrence of that character.
     * <p>
     * The good suffix shift is based on the idea that some subset on the right
     * side of the pattern has matched. When a bad character is found, the
     * pattern can be shifted right by the pattern length if the subset does
     * not occur again in pattern, or by the amount of distance to the
     * next occurrence of the subset in the pattern.
     *
     * Boyer-Moore search methods adapted from code by Amy Yu.
     */
    static final class BnM extends Node {
        char[] buffer;
        int[] lastOcc;
        int[] optoSft;

        /**
         * Pre calculates arrays needed to generate the bad character
         * shift and the good suffix shift. Only the last seven bits
         * are used to see if chars match; This keeps the tables small
         * and covers the heavily used ASII range, but occasionally
         * results in an aliased match for the bad character shift.
         */
        static Node optimize(Node node) {
            if (!(node instanceof Slice)) {
                return node;
            }
            char[] src = ((Slice) node).buffer;
            int patternLength = src.length;
            // The BM algorithm requires a bit of overhead;
            // If the pattern is short don't use it, since
            // a shift larger than the pattern length cannot
            // be used anyway.
            if (patternLength < 4) {
                return node;
            }
            int i, j, k;
            int[] lastOcc = new int[128];
            int[] optoSft = new int[patternLength];
            // Precalculate part of the bad character shift
            // It is a table for where in the pattern each
            // lower 7-bit value occurs
            for (i = 0; i < patternLength; i++) {
                lastOcc[src[i]&0x7F] = i + 1;
            }
            // Precalculate the good suffix shift
            // i is the shift amount being considered
NEXT:       for (i = patternLength; i > 0; i--) {
                // j is the beginning index of suffix being considered
                for (j = patternLength - 1; j >= i; j--) {
                    // Testing for good suffix
                    if (src[j] == src[j-i]) {
                        // src[j..len] is a good suffix
                        optoSft[j-1] = i;
                    } else {
                        // No match. The array has already been
                        // filled up with correct values before.
                        continue NEXT;
                    }
                }
                // This fills up the remaining of optoSft
                // any suffix can not have larger shift amount
                // then its sub-suffix. Why???
                while (j > 0) {
                    optoSft[--j] = i;
                }
            }
            // Set the guard value because of unicode compression
            optoSft[patternLength-1] = 1;
            return new BnM(src, lastOcc, optoSft, node.next);
        }
        BnM(char[] src, int[] lastOcc, int[] optoSft, Node next) {
            this.buffer = src;
            this.lastOcc = lastOcc;
            this.optoSft = optoSft;
            this.next = next;
        }
        boolean match(Matcher matcher, int i, CharSequence seq) {
            char[] src = buffer;
            int patternLength = src.length;
            int last = matcher.to - patternLength;

            // Loop over all possible match positions in text
NEXT:       while (i <= last) {
                // Loop over pattern from right to left
                for (int j = patternLength - 1; j >= 0; j--) {
                    char ch = seq.charAt(i+j);
                    if (ch != src[j]) {
                        // Shift search to the right by the maximum of the
                        // bad character shift and the good suffix shift
                        i += Math.max(j + 1 - lastOcc[ch&0x7F], optoSft[j]);
                        continue NEXT;
                    }
                }
                // Entire pattern matched starting at i
                matcher.first = i;
                boolean ret = next.match(matcher, i + patternLength, seq);
                if (ret) {
                    matcher.first = i;
                    matcher.groups[0] = matcher.first;
                    matcher.groups[1] = matcher.last;
                    return true;
                }
                i++;
            }
            return false;
        }
        boolean study(TreeInfo info) {
            info.minLength += buffer.length;
            info.maxValid = false;
            return next.study(info);
        }
    }

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

    /**
     *  This must be the very first initializer.
     */
    static Node accept = new Node();

    static Node lastAccept = new LastNode();

    static HashMap families = null;

    static HashMap categories = null;

    /**
     * Static template for all character families.
     * This information should be obtained programmatically in the future.
     */
    private static final String[] familyNames = new String[] {
        "BasicLatin",
        "Latin-1Supplement",
        "LatinExtended-A",
        "LatinExtended-Bound",
        "IPAExtensions",
        "SpacingModifierLetters",
        "CombiningDiacriticalMarks",
        "Greek",
        "Cyrillic",
        "Armenian",
        "Hebrew",
        "Arabic",
        "Syriac",
        "Thaana",
        "Devanagari",
        "Bengali",
        "Gurmukhi",
        "Gujarati",
        "Oriya",
        "Tamil",
        "Telugu",
        "Kannada",
        "Malayalam",
        "Sinhala",
        "Thai",
        "Lao",
        "Tibetan",
        "Myanmar",
        "Georgian",
        "HangulJamo",
        "Ethiopic",
        "Cherokee",
        "UnifiedCanadianAboriginalSyllabics",
        "Ogham",
        "Runic",
        "Khmer",
        "Mongolian",
        "LatinExtendedAdditional",
        "GreekExtended",
        "GeneralPunctuation",
        "SuperscriptsandSubscripts",
        "CurrencySymbols",
        "CombiningMarksforSymbols",
        "LetterlikeSymbols",
        "NumberForms",
        "Arrows",
        "MathematicalOperators",
        "MiscellaneousTechnical",
        "ControlPictures",
        "OpticalCharacterRecognition",
        "EnclosedAlphanumerics",
        "BoxDrawing",
        "BlockElements",
        "GeometricShapes",
        "MiscellaneousSymbols",
        "Dingbats",
        "BraillePatterns",
        "CJKRadicalsSupplement",
        "KangxiRadicals",
        "IdeographicDescriptionCharacters",
        "CJKSymbolsandPunctuation",
        "Hiragana",
        "Katakana",
        "Bopomofo",
        "HangulCompatibilityJamo",
        "Kanbun",
        "BopomofoExtended",
        "EnclosedCJKLettersandMonths",
        "CJKCompatibility",
        "CJKUnifiedIdeographsExtensionA",
        "CJKUnifiedIdeographs",
        "YiSyllables",
        "YiRadicals",
        "HangulSyllables",
        "HighSurrogates",
        "HighPrivateUseSurrogates",
        "LowSurrogates",
        "PrivateUse",
        "CJKCompatibilityIdeographs",
        "AlphabeticPresentationForms",
        "ArabicPresentationForms-A",
        "CombiningHalfMarks",
        "CJKCompatibilityForms",
        "SmallFormVariants",
        "ArabicPresentationForms-Bound",
        "Specials",
        "HalfwidthandFullwidthForms",
    };

    private static final String[] categoryNames = new String[] {
	"Cn",                   // UNASSIGNED		    = 0,
	"Lu",                   // UPPERCASE_LETTER	    = 1,
	"Ll",                   // LOWERCASE_LETTER	    = 2,
	"Lt",                   // TITLECASE_LETTER	    = 3,
	"Lm",                   // MODIFIER_LETTER	    = 4,
	"Lo",                   // OTHER_LETTER		    = 5,
	"Mn",                   // NON_SPACING_MARK	    = 6,
	"Me",                   // ENCLOSING_MARK	    = 7,
	"Mc",                   // COMBINING_SPACING_MARK   = 8,
	"Nd",                   // DECIMAL_DIGIT_NUMBER	    = 9,
	"Nl",                   // LETTER_NUMBER	    = 10,
	"No",                   // OTHER_NUMBER		    = 11,
	"Zs",                   // SPACE_SEPARATOR	    = 12,
	"Zl",                   // LINE_SEPARATOR	    = 13,
	"Zp",                   // PARAGRAPH_SEPARATOR	    = 14,
	"Cc",                   // CNTRL		    = 15,
	"Cf",                   // FORMAT		    = 16,
	"Co",                   // PRIVATE_USE		    = 18,
	"Cs",                   // SURROGATE		    = 19,
	"Pd",                   // DASH_PUNCTUATION	    = 20,
	"Ps",                   // START_PUNCTUATION	    = 21,
	"Pe",                   // END_PUNCTUATION	    = 22,
	"Pc",                   // CONNECTOR_PUNCTUATION    = 23,
	"Po",                   // OTHER_PUNCTUATION	    = 24,
	"Sm",                   // MATH_SYMBOL		    = 25,
	"Sc",                   // CURRENCY_SYMBOL	    = 26,
	"Sk",                   // MODIFIER_SYMBOL	    = 27,
	"So",                   // OTHER_SYMBOL		    = 28;

        "L",                    // LETTER
        "M",                    // MARK
        "N",                    // NUMBER
        "Z",                    // SEPARATOR
        "C",                    // CONTROL
        "P",                    // PUNCTUATION
        "S",                    // SYMBOL

        "LD",                   // LETTER_OR_DIGIT
        "L1",                   // Latin-1

        "all",                  // ALL
        "ASCII",                // ASCII

        "Alnum",                // Alphanumeric characters.
        "Alpha",                // Alphabetic characters.
        "Blank",                // Space and tab characters.
        "Cntrl",                // Control characters.
        "Digit",                // Numeric characters.
        "Graph",                // Characters that are printable and are also visible.
                                // (A space is printable, but "not visible, while an `a' is both.)
        "Lower",                // Lower-case alphabetic characters.
        "Print",                // Printable characters (characters that are not control characters.)
        "Punct",                // Punctuation characters (characters that are not letter,
                                // digits, control charact ers, or space characters).
        "Space",                // Space characters (such as space, tab, and formfeed, to name a few).
        "Upper",                // Upper-case alphabetic characters.
        "XDigit",               // Characters that are hexadecimal digits.
    };

    private static final Node[] familyNodes = new Node[] {
        new Range(0x0000007F),      // Basic Latin
        new Range(0x008000FF),      // Latin-1 Supplement
        new Range(0x0100017F),      // Latin Extended-A
        new Range(0x0180024F),      // Latin Extended-Bound
        new Range(0x025002AF),      // IPA Extensions
        new Range(0x02B002FF),      // Spacing Modifier Letters
        new Range(0x0300036F),      // Combining Diacritical Marks
        new Range(0x037003FF),      // Greek
        new Range(0x040004FF),      // Cyrillic
        new Range(0x0530058F),      // Armenian
        new Range(0x059005FF),      // Hebrew
        new Range(0x060006FF),      // Arabic
        new Range(0x0700074F),      // Syriac
        new Range(0x078007BF),      // Thaana
        new Range(0x0900097F),      // Devanagari
        new Range(0x098009FF),      // Bengali
        new Range(0x0A000A7F),      // Gurmukhi
        new Range(0x0A800AFF),      // Gujarati
        new Range(0x0B000B7F),      // Oriya
        new Range(0x0B800BFF),      // Tamil
        new Range(0x0C000C7F),      // Telugu
        new Range(0x0C800CFF),      // Kannada
        new Range(0x0D000D7F),      // Malayalam
        new Range(0x0D800DFF),      // Sinhala
        new Range(0x0E000E7F),      // Thai
        new Range(0x0E800EFF),      // Lao
        new Range(0x0F000FFF),      // Tibetan
        new Range(0x1000109F),      // Myanmar
        new Range(0x10A010FF),      // Georgian
        new Range(0x110011FF),      // Hangul Jamo
        new Range(0x1200137F),      // Ethiopic
        new Range(0x13A013FF),      // Cherokee
        new Range(0x1400167F),      // Unified Canadian Aboriginal Syllabics
        new Range(0x1680169F),      // Ogham
        new Range(0x16A016FF),      // Runic
        new Range(0x178017FF),      // Khmer
        new Range(0x180018AF),      // Mongolian
        new Range(0x1E001EFF),      // Latin Extended Additional
        new Range(0x1F001FFF),      // Greek Extended
        new Range(0x2000206F),      // General Punctuation
        new Range(0x2070209F),      // Superscripts and Subscripts
        new Range(0x20A020CF),      // Currency Symbols
        new Range(0x20D020FF),      // Combining Marks for Symbols
        new Range(0x2100214F),      // Letterlike Symbols
        new Range(0x2150218F),      // Number Forms
        new Range(0x219021FF),      // Arrows
        new Range(0x220022FF),      // Mathematical Operators
        new Range(0x230023FF),      // Miscellaneous Technical
        new Range(0x2400243F),      // Control Pictures
        new Range(0x2440245F),      // Optical Character Recognition
        new Range(0x246024FF),      // Enclosed Alphanumerics
        new Range(0x2500257F),      // Box Drawing
        new Range(0x2580259F),      // Block Elements
        new Range(0x25A025FF),      // Geometric Shapes
        new Range(0x260026FF),      // Miscellaneous Symbols
        new Range(0x270027BF),      // Dingbats
        new Range(0x280028FF),      // Braille Patterns
        new Range(0x2E802EFF),      // CJK Radicals Supplement
        new Range(0x2F002FDF),      // Kangxi Radicals
        new Range(0x2FF02FFF),      // Ideographic Description Characters
        new Range(0x3000303F),      // CJK Symbols and Punctuation
        new Range(0x3040309F),      // Hiragana
        new Range(0x30A030FF),      // Katakana
        new Range(0x3100312F),      // Bopomofo
        new Range(0x3130318F),      // Hangul Compatibility Jamo
        new Range(0x3190319F),      // Kanbun
        new Range(0x31A031BF),      // Bopomofo Extended
        new Range(0x320032FF),      // Enclosed CJK Letters and Months
        new Range(0x330033FF),      // CJK Compatibility
        new Range(0x34004DB5),      // CJK Unified Ideographs Extension A
        new Range(0x4E009FFF),      // CJK Unified Ideographs
        new Range(0xA000A48F),      // Yi Syllables
        new Range(0xA490A4CF),      // Yi Radicals
        new Range(0xAC00D7A3),      // Hangul Syllables
        new Range(0xD800DB7F),      // High Surrogates
        new Range(0xDB80DBFF),      // High Private Use Surrogates
        new Range(0xDC00DFFF),      // Low Surrogates
        new Range(0xE000F8FF),      // Private Use
        new Range(0xF900FAFF),      // CJK Compatibility Ideographs
        new Range(0xFB00FB4F),      // Alphabetic Presentation Forms
        new Range(0xFB50FDFF),      // Arabic Presentation Forms-A
        new Range(0xFE20FE2F),      // Combining Half Marks
        new Range(0xFE30FE4F),      // CJK Compatibility Forms
        new Range(0xFE50FE6F),      // Small Form Variants
        new Range(0xFE70FEFE),      // Arabic Presentation Forms-Bound
        new Specials(),             // Specials
        new Range(0xFF00FFEF),      // Halfwidth and Fullwidth Forms
    };

    private static final Node[] categoryNodes = new Node[] {
	new Category(1<<0),         // UNASSIGNED           = 0,
	new Category(1<<1),         // UPPERCASE_LETTER	    = 1,
	new Category(1<<2),         // LOWERCASE_LETTER	    = 2,
	new Category(1<<3),         // TITLECASE_LETTER	    = 3,
	new Category(1<<4),         // MODIFIER_LETTER      = 4,
	new Category(1<<5),         // OTHER_LETTER         = 5,
	new Category(1<<6),         // NON_SPACING_MARK	    = 6,
	new Category(1<<7),         // ENCLOSING_MARK	    = 7,
	new Category(1<<8),         // COMBINING_SPACING_MARK=8,
	new Category(1<<9),         // DECIMAL_DIGIT_NUMBER = 9,
	new Category(1<<10),        // LETTER_NUMBER	    = 10,
	new Category(1<<11),        // OTHER_NUMBER         = 11,
	new Category(1<<12),        // SPACE_SEPARATOR	    = 12,
	new Category(1<<13),        // LINE_SEPARATOR	    = 13,
	new Category(1<<14),        // PARAGRAPH_SEPARATOR  = 14,
	new Category(1<<15),        // CNTRL		    = 15,
	new Category(1<<16),        // FORMAT		    = 16,
	new Category(1<<18),        // PRIVATE_USE          = 18,
	new Category(1<<19),        // SURROGATE            = 19,
	new Category(1<<20),        // DASH_PUNCTUATION	    = 20,
	new Category(1<<21),        // START_PUNCTUATION    = 21,
	new Category(1<<22),        // END_PUNCTUATION	    = 22,
	new Category(1<<23),        // CONNECTOR_PUNCTUATION= 23,
	new Category(1<<24),        // OTHER_PUNCTUATION    = 24,
	new Category(1<<25),        // MATH_SYMBOL          = 25,
	new Category(1<<26),        // CURRENCY_SYMBOL	    = 26,
	new Category(1<<27),        // MODIFIER_SYMBOL	    = 27,
	new Category(1<<28),        // OTHER_SYMBOL         = 28;

        new Category(0x0000003E),   // LETTER
        new Category(0x000001C0),   // MARK
        new Category(0x00000E00),   // NUMBER
        new Category(0x00007000),   // SEPARATOR
        new Category(0x000D8000),   // CONTROL
        new Category(0x01F00000),   // PUNCTUATION
        new Category(0x1E000000),   // SYMBOL

        new Category(0x0000023E),   // LETTER_OR_DIGIT
        new Range(0x000000FF),      // Latin-1

        new All(),                  // ALL
        new Range(0x0000007F),      // ASCII

        new Ctype(ASCII.ALNUM),     // Alphanumeric characters.
        new Ctype(ASCII.ALPHA),     // Alphabetic characters.
        new Ctype(ASCII.BLANK),     // Space and tab characters.
        new Ctype(ASCII.CNTRL),     // Control characters.
        new Range(('0'<<16)|'9'),   // Numeric characters.
        new Ctype(ASCII.GRAPH),     // Characters that are printable and are also visible.
                                    // (A space is printable, but "not visible, while an `a' is both.)
        new Range(('a'<<16)|'z'),   // Lower-case alphabetic characters.
        new Range(0x0020007E),      // Printable characters (characters that are not control characters.)
        new Ctype(ASCII.PUNCT),     // Punctuation characters (characters that are not letter,
                                    // digits, control charact ers, or space characters).
        new Ctype(ASCII.SPACE),     // Space characters (such as space, tab, and formfeed, to name a few).
        new Range(('A'<<16)|'Z'),   // Upper-case alphabetic characters.
        new Ctype(ASCII.XDIGIT),    // Characters that are hexadecimal digits.
    };

}
