/*
 * @(#)Query.java	1.26 04/02/10
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management;


/**
 * <p>Constructs query object constraints. The static methods provided
 * return query expressions that may be used in listing and
 * enumerating MBeans. Individual constraint construction methods
 * allow only appropriate types as arguments. Composition of calls can
 * construct arbitrary nestings of constraints, as the following
 * example illustrates:</p>
 *
 * <pre>
 * QueryExp exp = Query.and(Query.gt(Query.attr("age"),Query.value(5)),
 *                          Query.match(Query.attr("name"),
 *                                      Query.value("Smith")));
 * </pre>
 *
 * @since 1.5
 */
 public class Query extends Object   { 
     
     
     /**
      * A code representing the {@link Query#gt} query.  This is chiefly
      * of interest for the serialized form of queries.
      */
     public static final int GT	 = 0;

     /**
      * A code representing the {@link Query#lt} query.  This is chiefly
      * of interest for the serialized form of queries.
      */
     public static final int LT	 = 1;

     /**
      * A code representing the {@link Query#geq} query.  This is chiefly
      * of interest for the serialized form of queries.
      */
     public static final int GE	 = 2;

     /**
      * A code representing the {@link Query#leq} query.  This is chiefly
      * of interest for the serialized form of queries.
      */
     public static final int LE	 = 3;

     /**
      * A code representing the {@link Query#eq} query.  This is chiefly
      * of interest for the serialized form of queries.
      */
     public static final int EQ	 = 4;
     

     /**
      * A code representing the {@link Query#plus} expression.  This
      * is chiefly of interest for the serialized form of queries.
      */
     public static final int PLUS  = 0;

     /**
      * A code representing the {@link Query#minus} expression.  This
      * is chiefly of interest for the serialized form of queries.
      */
     public static final int MINUS = 1;

     /**
      * A code representing the {@link Query#times} expression.  This
      * is chiefly of interest for the serialized form of queries.
      */
     public static final int TIMES = 2;

     /**
      * A code representing the {@link Query#div} expression.  This is
      * chiefly of interest for the serialized form of queries.
      */
     public static final int DIV   = 3;
     

     /**
      * Basic constructor.
      */
     public Query() { 
     } 


     /**
      * Returns a query expression that is the conjunction of two other query
      * expressions.
      *
      * @param q1 A query expression.
      * @param q2 Another query expression.
      *
      * @return  The conjunction of the two arguments.
      */
     public static QueryExp and(QueryExp q1, QueryExp q2)  { 
	 return new AndQueryExp(q1, q2);
     } 
     
     /**
      * Returns a query expression that is the disjunction of two other query
      * expressions.
      *
      * @param q1 A query expression.
      * @param q2 Another query expression.
      *
      * @return  The disjunction of the two arguments.
      */
     public static QueryExp or(QueryExp q1, QueryExp q2)  { 
	 return new OrQueryExp(q1, q2);
     } 
     
     /**
      * Returns a query expression that represents a "greater than" constraint on
      * two values.
      *
      * @param v1 A value expression.
      * @param v2 Another value expression.
      *
      * @return  A "greater than" constraint on the arguments.
      */
     public static QueryExp gt(ValueExp v1, ValueExp v2)  { 
	 return new BinaryRelQueryExp(GT, v1, v2);
     } 

     /**
      * Returns a query expression that represents a "greater than or equal
      * to" constraint on two values.
      *
      * @param v1 A value expression.
      * @param v2 Another value expression.
      *
      * @return  A "greater than or equal to" constraint on the arguments.
      */
     public static QueryExp geq(ValueExp v1, ValueExp v2)  { 
	 return new BinaryRelQueryExp(GE, v1, v2);
     } 

     /**
      * Returns a query expression that represents a "less than or equal to"
      * constraint on two values.
      *
      * @param v1 A value expression.
      * @param v2 Another value expression.
      *
      * @return  A "less than or equal to" constraint on the arguments.
      */
     public static QueryExp leq(ValueExp v1, ValueExp v2)  { 
	 return new BinaryRelQueryExp(LE, v1, v2);
     } 

     /**
      * Returns a query expression that represents a "less than" constraint on
      * two values.
      *
      * @param v1 A value expression.
      * @param v2 Another value expression.
      *
      * @return  A "less than" constraint on the arguments.
      */
     public static QueryExp lt(ValueExp v1, ValueExp v2)  { 
	 return new BinaryRelQueryExp(LT, v1, v2);
     } 

     /**
      * Returns a query expression that represents an equality constraint on
      * two values.
      *
      * @param v1 A value expression.
      * @param v2 Another value expression.
      *
      * @return  A "equal to" constraint on the arguments.
      */
     public static QueryExp eq(ValueExp v1, ValueExp v2)  { 
	 return new BinaryRelQueryExp(EQ, v1, v2);
     } 

     /**
      * Returns a query expression that represents the constraint that one
      * value is between two other values.
      *
      * @param v1 A value expression that is "between" v2 and v3.
      * @param v2 Value expression that represents a boundary of the constraint.     
      * @param v3 Value expression that represents a boundary of the constraint.
      *
      * @return  The constraint that v1 lies between v2 and v3.
      */
     public static QueryExp between(ValueExp v1, ValueExp v2, ValueExp v3) {
	 return new BetweenQueryExp(v1, v2, v3); 
     } 

     /**
      * Returns a query expression that represents a matching constraint on
      * a string argument. The matching syntax is consistent with file globbing:
      * Supports "<code>?</code>", "<code>*</code>", "<code>[</code>",
      * each of which may be escaped with "<code>\</code>";
      * Character classes may use "<code>!</code>" for negation and
      * "<code>-</code>" for range.
      * (<code>*</code> for any character sequence,
      * <code>?</code> for a single arbitrary character,
      * <code>[...]</code> for a character sequence).
      * For example: <code>a*b?c</code> would match a string starting
      * with the character <code>a</code>, followed
      * by any number of characters, followed by a <code>b</code>,
      * any single character, and a <code>c</code>.
      *
      * @param a An attribute expression
      * @param s A string value expression representing a matching constraint
      *
      * @return  A query expression that represents the matching constraint on the
      * string argument.
      */
     public static QueryExp match(AttributeValueExp a, StringValueExp s)  { 
	 return new MatchQueryExp(a, s);
     } 

     /**
      * <p>Returns a new attribute expression.</p>
      *
      * <p>Evaluating this expression for a given
      * <code>objectName</code> includes performing {@link
      * MBeanServer#getAttribute MBeanServer.getAttribute(objectName,
      * name)}.</p>
      *
      * @param name The name of the attribute.
      *
      * @return  An attribute expression for the attribute named name.
      */    
     public static AttributeValueExp attr(String name)  { 
	 return new AttributeValueExp(name);
     }     

     /**
      * <p>Returns a new qualified attribute expression.</p>
      *
      * <p>Evaluating this expression for a given
      * <code>objectName</code> includes performing {@link
      * MBeanServer#getObjectInstance
      * MBeanServer.getObjectInstance(objectName)} and {@link
      * MBeanServer#getAttribute MBeanServer.getAttribute(objectName,
      * name)}.</p>
      *
      * @param className The name of the class possessing the attribute.
      * @param name The name of the attribute.
      *
      * @return  An attribute expression for the attribute named name.
      */     
     public static AttributeValueExp attr(String className, String name)  { 
	 return new QualifiedAttributeValueExp(className, name);
     } 
     
     /**
      * <p>Returns a new class attribute expression which can be used in any
      * Query call that expects a ValueExp.</p>
      *
      * <p>Evaluating this expression for a given
      * <code>objectName</code> includes performing {@link
      * MBeanServer#getObjectInstance
      * MBeanServer.getObjectInstance(objectName)}.</p>
      *
      * @return  A class attribute expression.
      */
     public static AttributeValueExp classattr()  { 
	 return new ClassAttributeValueExp();
     } 
     
     /**
      * Returns a constraint that is the negation of its argument.
      *
      * @param queryExp The constraint to negate.
      *
      * @return  A negated constraint.
      */
     public static QueryExp not(QueryExp queryExp)  { 
	 return new NotQueryExp(queryExp);
     } 
    
     /**
      * Returns an expression constraining a value to be one of an explicit list.
      *      
      * @param val A value to be constrained.
      * @param valueList An array of ValueExps.
      *
      * @return  A QueryExp that represents the constraint.
      */
     public static QueryExp in(ValueExp val, ValueExp valueList[])  { 
	 return new InQueryExp(val, valueList);
     } 
     
     /**
      * Returns a new string expression.
      *
      * @param val The string value.
      *
      * @return  A ValueExp object containing the string argument.
      */    
     public static StringValueExp value(String val)  { 
	 return new StringValueExp(val);
     } 
     
     /**
      * Returns a numeric value expression that can be used in any Query call
      * that expects a ValueExp.
      *
      * @param val An instance of Number.
      *
      * @return  A ValueExp object containing the argument.
      */
     public static ValueExp value(Number val)  { 
	 return new NumericValueExp(val);
     } 

     /**
      * Returns a numeric value expression that can be used in any Query call
      * that expects a ValueExp.
      *
      * @param val An int value.
      *
      * @return  A ValueExp object containing the argument.
      */
     public static ValueExp value(int val)  { 
	 return new NumericValueExp(new Long(val));
     } 

     /**
      * Returns a numeric value expression that can be used in any Query call
      * that expects a ValueExp.
      *
      * @param val A long value.
      *
      * @return  A ValueExp object containing the argument.
      */
     public static ValueExp value(long val)  { 
	 return new NumericValueExp(new Long(val));
     } 
     
     /**
      * Returns a numeric value expression that can be used in any Query call
      * that expects a ValueExp.
      *
      * @param val A float value.
      *
      * @return  A ValueExp object containing the argument.
      */
     public static ValueExp value(float val)  { 
	 return new NumericValueExp(new Double(val));
     } 
     
     /**
      * Returns a numeric value expression that can be used in any Query call
      * that expects a ValueExp.
      *
      * @param val A double value.
      *
      * @return  A ValueExp object containing the argument.
      */
     public static ValueExp value(double val)  { 
	 return new NumericValueExp(new Double(val));
     } 

     /**
      * Returns a boolean value expression that can be used in any Query call
      * that expects a ValueExp.
      *
      * @param val A boolean value.
      *
      * @return  A ValueExp object containing the argument.
      */
     public static ValueExp value(boolean val)  { 
	 return new BooleanValueExp(val);
     } 

     /**
      * Returns a binary expression representing the sum of two numeric values,
      * or the concatenation of two string values.
      *
      * @param value1 The first '+' operand.
      * @param value2 The second '+' operand.
      *
      * @return  A ValueExp representing the sum or concatenation of the two
      * arguments.
      */
     public static ValueExp plus(ValueExp value1, ValueExp value2) {
	 return new BinaryOpValueExp(PLUS, value1, value2); 
     } 
     
     /**
      * Returns a binary expression representing the product of two numeric values.
      *
      *
      * @param value1 The first '*' operand.      
      * @param value2 The second '*' operand.
      *
      * @return  A ValueExp representing the product.
      */
     public static ValueExp times(ValueExp value1,ValueExp value2) {
	 return new BinaryOpValueExp(TIMES, value1, value2); 
     }
     
     /**
      * Returns a binary expression representing the difference between two numeric
      * values.
      *
      * @param value1 The first '-' operand.      
      * @param value2 The second '-' operand.
      *
      * @return  A ValueExp representing the difference between two arguments.
      */
     public static ValueExp minus(ValueExp value1, ValueExp value2) {
	 return new BinaryOpValueExp(MINUS, value1, value2);
     } 
     
     /**
      * Returns a binary expression representing the quotient of two numeric
      * values.
      *
      * @param value1 The first '/' operand.
      * @param value2 The second '/' operand.
      *
      * @return  A ValueExp representing the quotient of two arguments.
      */     
     public static ValueExp div(ValueExp value1, ValueExp value2) {
	 return new BinaryOpValueExp(DIV, value1, value2); 
     } 
     
     /**
      * Returns a query expression that represents a matching constraint on
      * a string argument. The value must start with the given string value.
      *      
      * @param a An attribute expression.
      * @param s A string value expression representing the beginning of the string value.      
      *
      * @return  The constraint that a matches s.
      */
     public static QueryExp initialSubString(AttributeValueExp a, StringValueExp s)  { 
	 return new MatchQueryExp(a, new StringValueExp(s.getValue()+"*"));
     } 

     /**
      * Returns a query expression that represents a matching constraint on
      * a string argument. The value must contain the given string value.
      *
      * @param a An attribute expression.
      * @param s A string value expression representing the substring.
      *
      * @return  The constraint that a matches s.
      */
     public static QueryExp anySubString(AttributeValueExp a, StringValueExp s) {
	 return new MatchQueryExp(a, new StringValueExp("*"+s.getValue()+"*")); 
     } 

     /**
      * Returns a query expression that represents a matching constraint on
      * a string argument. The value must contain the given string value.
      *
      * @param a An attribute expression.
      * @param s A string value expression representing the end of the string value.
      *
      *@return  The constraint that a matches s.
      */
     public static QueryExp finalSubString(AttributeValueExp a, StringValueExp s) {
	 return new MatchQueryExp(a, new StringValueExp("*"+ s.getValue()));
     } 
 }
