/*
 * @(#)POAPolicyCombinationValidator.java	1.6 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.POA;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;
import org.omg.PortableServer.ServantLocatorPackage.*;

import com.sun.corba.se.internal.core.*;
import com.sun.corba.se.internal.corba.*;
import com.sun.corba.se.internal.util.*;

import java.util.*;

// NOTE: Current implementation does not check for duplicates and 
// repeated policy objects of the same type.

public final class POAPolicyCombinationValidator{

    // policy processing.

    //Always return _index <= -1 if there are no invalid policies in
    //the scope of constraint resolution.
    public static short checkForInvalidPolicyCombinations(Policy[] policies) {
        short returnIndex  = -1; 
        short currentIndex = -1;
        //Validity for this orb implementation. See the POA spec on policies.
        returnIndex  = checkValidityForThisORBImplementation(policies);
        //Consistency of the combination of policies. See the POA spec on policies.
        currentIndex = checkConsistency(policies);
        returnIndex  = findNonNegativeMinimumIfItExists(currentIndex,returnIndex);
        //Missing administrative actions. See the POA spec on policies.
        currentIndex = checkMissingAdministrativeActions(policies);
        returnIndex  = findNonNegativeMinimumIfItExists(currentIndex,returnIndex);
        //Return the minimal non-negative index of the offending policy or -1;
        //-1 indicates no policy violations detected by current algorithm.
        return returnIndex;
    }

    private static short checkValidityForThisORBImplementation(Policy[] policies){
        return -1;
    }

    private static short checkConsistency(Policy[] policies){
        short returnIndex  = -1; 
        short currentIndex = -1;
        //uaomo_req_r == USE_ACTIVE_OBJECT_MAP_ONLY requires RETAIN
        returnIndex  = uaomo_req_r(policies);
        //nR_req_uds_o_usm == NON_RETAIN requires USE_DEFAULT_SERVANT OR USE_SERVANT_MANAGER
        currentIndex = nR_req_uds_o_usm(policies);
        returnIndex  = findNonNegativeMinimumIfItExists(currentIndex,returnIndex);
        //ia_req_si_a_r == IMPLICIT_ACTIVATION requires SYSTEM_ID and RETAIN
        currentIndex = ia_req_si_a_r(policies);
        returnIndex  = findNonNegativeMinimumIfItExists(currentIndex,returnIndex);
        //life_span_excluded_middle == ! (PERSISTENT && TRANSIENT)
        currentIndex = life_span_excluded_middle(policies);
        returnIndex  = findNonNegativeMinimumIfItExists(currentIndex,returnIndex);
        //servant_retention_excluded_middle == ! (RETAIN && NON_RETAIN)
        currentIndex = servant_retention_excluded_middle(policies);
        returnIndex  = findNonNegativeMinimumIfItExists(currentIndex,returnIndex);
        //id_uniqueness_excluded_middle == ! (UNIQUE_ID && MULTIPLE_ID)
        currentIndex = id_uniqueness_excluded_middle(policies);
        returnIndex  = findNonNegativeMinimumIfItExists(currentIndex,returnIndex);
        // can add other consistency checks with the same procedure for index management.
        return returnIndex;
    }

    // Returns -1 if non-negative minimum does not exist.
    private static short findNonNegativeMinimumIfItExists(short currentIndex, short returnIndex){
        //If any of them is -1, set it to the other.
        if ( returnIndex  == -1 ) returnIndex  = currentIndex;
        if ( currentIndex == -1 ) currentIndex = returnIndex ;
        //By now, if any of them is not -1 and the other is, the other is set to it.
        //If both are -1, -1 will be returned in the last statement.
        if ( currentIndex != -1 || returnIndex != -1 ) returnIndex = (short)Math.min(currentIndex,returnIndex);
        return returnIndex;
    }

    private static short checkMissingAdministrativeActions(Policy[] policies){
        return -1;
    }

    //uaomo_req_r == USE_ACTIVE_OBJECT_MAP_ONLY requires RETAIN
    private static short uaomo_req_r(Policy[] policies){
        RequestProcessingPolicy rpp = null; short rpp_index = -1;
        ServantRetentionPolicy  srp = null; short srp_index = -1;
        for(short i=0; i<policies.length; i++){
            if ( policies[i] instanceof RequestProcessingPolicy ){
                rpp=(RequestProcessingPolicy)policies[i];
                rpp_index=i;
            }
            if ( policies[i] instanceof ServantRetentionPolicy ) {
                srp=(ServantRetentionPolicy)policies[i];
                srp_index=i;
            }
        }
        if (rpp!=null && rpp.value()==
            org.omg.PortableServer.RequestProcessingPolicyValue.USE_ACTIVE_OBJECT_MAP_ONLY){
            if (srp_index==-1) /* Default is Retain so we're OK*/ 
                return -1;
            else {
                if(srp!=null && srp.value()
                   ==org.omg.PortableServer.ServantRetentionPolicyValue.NON_RETAIN)
                    return (short)Math.min(rpp_index,srp_index);
                else return -1;
            }
        } else return -1;
    }

    //nR_req_uds_o_usm == NON_RETAIN requires USE_DEFAULT_SERVANT OR USE_SERVANT_MANAGER
    private static short nR_req_uds_o_usm(Policy[] policies){
        return -1;
    }

    //ia_req_si_a_r == IMPLICIT_ACTIVATION requires SYSTEM_ID and RETAIN
    //When combination is violated, returned index is the minimum index
    //of *the* violating combination.
    private static short ia_req_si_a_r(Policy[] policies){
        short returnIndex = -1;
        ImplicitActivationPolicy iap = null;   short iap_index = -1;
        IdAssignmentPolicy       idp = null;   short idp_index = -1;
        ServantRetentionPolicy   srp = null;   short srp_index = -1;
        //Collect policies and indeces.
        for(short i=0; i<policies.length; i++){
            if ( policies[i] instanceof ImplicitActivationPolicy ){
                iap       = (ImplicitActivationPolicy)policies[i];
                iap_index = i;
            }
            if ( policies[i] instanceof IdAssignmentPolicy ) {
                idp       = (IdAssignmentPolicy)policies[i];
                idp_index = i;
            }
            if ( policies[i] instanceof ServantRetentionPolicy ) {
                srp       = (ServantRetentionPolicy)policies[i];
                srp_index = i;
            }
        }
        if (iap == null) return -1; //The rule is for IMPLICIT_ACTIVATION which is not the default.
        else { //ImplicitActivationPolicy is set.
            if (iap.value()!=org.omg.PortableServer.ImplicitActivationPolicyValue.IMPLICIT_ACTIVATION) {
                return -1; // Default is NO_IMPLICIT_ACTIVATION, so we're OK.
            }
            else { // IMPLICIT_ACTIVATION has been set.
                if( srp == null ) { //Default: RETAIN
                    srp_index = -1;
                    if ( idp == null ) return -1; // Defaults are RETAIN and SYSTEM_ID. So we're OK.
                    else { //IdAssignmentPolicy is set by user.
                        //if IdAssignmentPolicy violates the rule, return minimal index of the set.
                        if ( idp.value()!=org.omg.PortableServer.IdAssignmentPolicyValue.SYSTEM_ID)
                            return findNonNegativeMinimumIfItExists(idp_index,iap_index);
                        else //IdAssignmentPolicy does not violate the rule; 
                            return -1;
                    }
                }
                else { // ServantRetentionPolicy is set by user.
                    //If ServantRetentionPolicy violates the rule, return minimal index of the set.
                    if(srp.value()!=org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN){
                        if ( idp == null) //Default: SYSTEM_ID
                            return findNonNegativeMinimumIfItExists(srp_index , iap_index);
                        else { //IdAssignmentPolicy is set by the user.
                            if (idp.value()!=org.omg.PortableServer.IdAssignmentPolicyValue.SYSTEM_ID)
                                //All policies are set wrong !!!
                                return findNonNegativeMinimumIfItExists
                                    (srp_index , findNonNegativeMinimumIfItExists(iap_index,idp_index));
                            else //IdAssignmentPolicy does not violate the rule. 
                                return findNonNegativeMinimumIfItExists(srp_index , iap_index);
                        }
                    }
                    else{ //The user's setting of ServantRetentionPolicy does not violate the rule
                        if ( idp == null) //Default: SYSTEM_ID
                            return -1 ;
                        else { //IdAssignmentPolicy is set by the user.
                            //If IdAssignmentPolicy violates the rule return the appropriate index.
                            if (idp.value()!=org.omg.PortableServer.IdAssignmentPolicyValue.SYSTEM_ID)
                                return findNonNegativeMinimumIfItExists(iap_index,idp_index);
                            else //IdAssignmentPolicy does not violate the rule. 
                                return -1;
                        }
                    }
                }
            }
            //Some index will be returned by one of the clauses above.
        } //ImplicitAssignmentPolicy set to IMPLICIT_ACTIVATION
    }

    //life_span_excluded_middle == ! (PERSISTENT && TRANSIENT)
    private static short life_span_excluded_middle(Policy[] policies){
        LifespanPolicy lp1 = null; short lp1_index = -1;
        LifespanPolicy lp2 = null; /* short lp2_index = -1; */
        for(short i=0; i<policies.length; i++){
            if ( policies[i] instanceof LifespanPolicy ){
                if(lp1 == null){
                    lp1=(LifespanPolicy)policies[i];
                    lp1_index=i;
                } else {
                    lp2=(LifespanPolicy)policies[i];
                    /* lp2_index=i; */
                    // LifespanPolicy can be either but not both 
                    // of the two possible values
                    if ( lp2.value() != lp1.value() ) return lp1_index;
                    else return -1;
                }
            }
        }
        return -1;
    }

    //servant_retention_excluded_middle == ! (RETAIN && NON_RETAIN)
    private static short servant_retention_excluded_middle(Policy[] policies){
        ServantRetentionPolicy rp1 = null; short rp1_index = -1;
        ServantRetentionPolicy rp2 = null; /* short rp2_index = -1; */
        for(short i=0; i<policies.length; i++){
            if ( policies[i] instanceof ServantRetentionPolicy ){
                if(rp1 == null){
                    rp1=(ServantRetentionPolicy)policies[i];
                    rp1_index=i;
                } else {
                    rp2=(ServantRetentionPolicy)policies[i];
                    /* rp2_index=i; */
                    // ServantRetentionPolicy can be either but not both 
                    // of the two possible values
                    if ( rp2.value() != rp1.value() ) return rp1_index;
                    else return -1;
                }
            }
        }
        return -1;
    }

    //id_uniqueness_excluded_middle == ! (UNIQUE_ID && MULTIPLE_ID)
    private static short id_uniqueness_excluded_middle(Policy[] policies){
        IdUniquenessPolicy id1 = null; short id1_index = -1;
        IdUniquenessPolicy id2 = null; /* short id2_index = -1; */
        for(short i=0; i<policies.length; i++){
            if ( policies[i] instanceof IdUniquenessPolicy ){
                if(id1 == null){
                    id1=(IdUniquenessPolicy)policies[i];
                    id1_index=i;
                } else {
                    id2=(IdUniquenessPolicy)policies[i];
                    /* id2_index=i; */
                    // IdUniquenessPolicy can be either but not both 
                    // of the two possible values
                    if ( id2.value() != id1.value() ) return id1_index;
                    else return -1;
                }
            }
        }
        return -1;
    }

}
