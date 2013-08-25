package java.util;

class CurrencyData {

    static final String version = "140";

    static final String mainTable = "\u007f\u007f\u007f\u0081CM\u0082\u007f\u0082\u007f\u007fKCF@\u007f\u0080R\u0083\u0081C\u007fF\u0081\u007f\u0084" +
                                    "LC\u007fS\u0081\u0085Mc\u0005\u0085\u007f\u0081CCA\u007f\u007fKCM\u007f\u0086O\u007f\u0011C" +
                                    "C\u007f\u0087E\u007f\u0088\u0088E\u0085\u007f\u0089\u000f\u0088XO\u007f\u007fBC\u007fOD\u007f\u0087\u008aJ" +
                                    "\u007f\u007f\u007f\u007f\u0081\u007f\u007f\u007f\u007f\u0005J\u007f\u0082\u007fO\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007fC" +
                                    "\u007f\u007f\u0083\u007fJ\u007fO\u008b\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007fM\u0081A\u007f\u007f\u007f\u007f\u007f\u007f" +
                                    "\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u0081CO\u007f\u0083\u007f\u008c\u007f\u007f\u0081\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f" +
                                    "\u0088O\u007f\u0082K\u0081\u008d\u008eO\u007f\u007f\u008cC\u0005\u007f\u0081\u0088\u0081\u008dP\u0083\u007f\u0085\u007fC\u007f" +
                                    "\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007fC\u007f\u0087K\u007f\u007f\u007fJ\u007fFE\u007f\u007f\u007f\u007f\u007f" +
                                    "\u007f\u007f\u007fQ\u0081\u007f\u007f\u007f\u007f\u007f\u007fR\u008dQ\u0083\u007fcQ\n\u0081\u007f\u007f\u007f\u007f\u007f\u007f" +
                                    "\u007f\u007f\u007f\u007f\u008d\u007f\u007f\u007f\u007f\u007f\u007f\u007fC\u007fc\u0018\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f" +
                                    "\u007f\u007f\u007f\u007fR\u007fRQ\u0087\u007f\u007f\u007f\u0005\u0082\u007fV\u007f\u0016\u007f\u007f\u007f\u007fc\u007fCS" +
                                    "JO\u0082\u007f\u007f\u007f\u007f\u007f\u008f\u007fQ\u007f\u007f\u007f\u007f\u007f\u007fCKK\u0081K\u007f\u007fc\u007f" +
                                    "C\u007f\u0081K\u0081\u0081@\u0083\u007f\u007fC\u0085JSO\u0083\u0081N\u0082\u0090QQJMQ\u0091" +
                                    "C\u007f\u0092\u007f\u0085\u0087M\u007fN\u007f\u007f\u0081\u007f\u007fJQ\u007f\u0087\u007f\u007f\u0089\u007f\u007f\u007f\u007fC" +
                                    "\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007fq\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f" +
                                    "A\u007f\u007f\u007fM\u0092JO\u007f\u007fQM\u0081\u0089\u007f\u007f\u007f\u0083\u0093\u0081\u007f\u007f\u0083\u007f\u0006\u007f" +
                                    "Q\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f" +
                                    "\u007f\u007f\u007f\u007f\u0081\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u0094\u007f\u007f\u007fC\u007fA\u007f\u0005\u007f\u007f\u007f" +
                                    "QCQFJ\u007fCO\u0081\u0086JK\u0081\u0085R\u007f\u007fC\u007fC\u007fB\u007f\u007fOK" +
                                    "\u007f\u007f\u0083\u0088\u007f\u0081\u0085A\u007fR\u0089\u0083LcO\u007f\u007f\u0095\u007fC\u007f\u0087C\u007f\u007fR" +
                                    "G\u007f\u007f\u007f\u007f\u007fW\u007f\u007f\u007f\u007f\u007f\u0083\u007f\u007f\u007f\u007f\u007fC\u007f\u007f\u007f\u007f\u007fTR" +
                                    "\u0081\u007f\u0082\u007f\u0096\u007f\u0083\u007f\u0083\u007f\u007f\u007f\u007fC\u007f\u007f\u007f\u007f\u007f\u007f\u0015\u007f\u007f\u007f\u007f\u007f" +
                                    "\u007f\u007f\u007f\u007f\u007f\u0092\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007fS\u007f\u007f\u007f\u007f\u007f\u007f\u007f" +
                                    "\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f" +
                                    "\u007f\u007f\u007f\u007fQ\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u0081\u007f\u007f\u007f\u007f\u007f\u007f" +
                                    "Q\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007fJ\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007fC\u007f\u007f\u007f";

    static final long[] scCutOverTimes = { 9223372036854775807L, 9223372036854775807L, 9223372036854775807L, 1136059200000L, 9223372036854775807L, 9223372036854775807L, 9223372036854775807L, 9223372036854775807L, 9223372036854775807L, 1199138400000L, 9223372036854775807L, 9223372036854775807L, 9223372036854775807L, 1183248000000L, 9223372036854775807L, 1199142000000L, 1151704800000L, 9223372036854775807L, 9223372036854775807L, 1120165200000L, 1104530400000L, 1199160000000L, };

    static final String[] scOldCurrencies = { "EUR", "XCD", "USD", "AZM", "XOF", "NOK", "AUD", "XAF", "NZD", "CYP", "MAD", "DKK", "GBP", "GHC", "CHF", "MTL", "MZM", "XPF", "ILS", "ROL", "TRL", "VEB", };

    static final String[] scNewCurrencies = { null, null, null, "AZN", null, null, null, null, null, "EUR", null, null, null, "GHS", null, "EUR", "MZN", null, null, "RON", "TRY", "VEF", };

    static final int[] scOldCurrenciesDFD = { 2, 2, 2, 2, 0, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 2, 2, 0, 2, };

    static final int[] scNewCurrenciesDFD = { 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 2, 2, 0, 0, 2, 2, 2, };

    static final String otherCurrencies = "ADP-AFA-ATS-AYM-AZM-AZN-BEF-BGL-BOV-BYB-CLF-CYP-DEM-ESP-EUR-FIM-FRF-GHC-GHP-GHS-GRD-GWP-IEP-ITL-LUF-MGF-MTL-MXV-MZM-MZN-NLG-PTE-ROL-RON-RUR-SDD-SIT-SRG-TPE-TRL-TRY-USN-USS-VEB-VEF-XAF-XAG-XAU-XBA-XBB-XBC-XBD-XCD-XDR-XFO-XFU-XOF-XPD-XPF-XPT-XTS-XXX-YUM-ZWN";

    static final int[] otherCurrenciesDFD = { 0, 2, 2, 2, 2, 2, 0, 2, 2, 0, 0, 2, 2, 0, 2, 2, 2, 2, 2, 2, 0, 2, 2, 0, 0, 0, 2, 2, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 2, 2, 0, -1, -1, -1, -1, -1, -1, 2, -1, -1, -1, 0, -1, 0, -1, -1, -1, 2, 2, };

}

