package java.util;

class CurrencyData {

    static final String mainTable = "\u007f\u007f\u007f\u0081C@\u0082\u007f\u0082\u007f\u007fKCF@\u007f\u0080R\u0083\u0084C\u007fF\u007f\u007fL" +
                                    "LC\u007fS\u0085\u0086MC\u0005\u0086\u007f\u007fCCA\u007f\u007fKCM\u007f\u0087O\u007f\u0011C" +
                                    "C\u007f\u0088E\u007f\u0089\u0089E\u0086\u007f\u008a\u000f\u0089XO\u007f\u007fB\u007f\u007fOD\u007f\u0088OJ" +
                                    "\u007f\u007f\u007f\u007f\u008b\u007f\u007f\u007f\u007f\u0005J\u007f\u0082\u007fO\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007fC" +
                                    "\u007f\u007f\u0083\u007fJ\u007fO\u008c\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007fM\u008dA\u007f\u007f\u007f\u007f\u007f\u007f" +
                                    "\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u008eCO\u007f\u0083\u007f\u008f\u007f\u007f\u0081\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f" +
                                    "\u0089O\u007f\u0082K\u0081\u007fBO\u007f\u007f\u008fC\u0005\u007f\u0081\u0089\u0090\u0091P\u0083\u007fO\u007fC\u007f" +
                                    "\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007fC\u007f\u0088K\u007f\u007f\u007fJ\u007fFE\u007f\u007f\u007f\u007f\u007f" +
                                    "\u007f\u007f\u007fQ\u0092\u007f\u007f\u007f\u007f\u007f\u007fR\u007fQ\u0083\u007fCQJ\u0093\u007f\u007f\u007f\u007f\u007f\u007f" +
                                    "\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007fC\u007fC\u0018\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f" +
                                    "\u007f\u007f\u007f\u007fR\u007fRQ\u0088\u007f\u007f\u007f\u0005\u0082\u007fV\u007f\u0016\u007f\u007f\u007f\u007fC\u007fCS" +
                                    "JO\u0082\u007f\u007f\u007f\u007f\u007f\u0094\u007fQ\u007f\u007f\u007f\u007f\u007f\u007fCKK\u0095K\u007f\u007fC\u007f" +
                                    "C\u007f\u0081K\u007f\u007f\u0005\u0083\u007f\u007fC\u0086JSO\u0083\u0081N\u0082KQQJMQL" +
                                    "C\u007f\u0096\u007f\u0086\u0088M\u007fN\u007f\u007f\u0097\u007f\u007fJQ\u007f\u0088\u007f\u007f\u008a\u007f\u007f\u007f\u007fC" +
                                    "\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007fQ\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f" +
                                    "A\u007f\u007f\u007fM\u0096JO\u007f\u007fQM\u0081\u008a\u007f\u007f\u007f\u0083\u0098\u0099\u007f\u007f\u0083\u007f\u0006\u007f" +
                                    "Q\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f" +
                                    "\u007f\u007f\u007f\u007f\u0081\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007fK\u007f\u007f\u007f\u007f\u007fQ\u007f\u0005\u007f\u007f\u007f" +
                                    "QCQCJ\u007fCOS\u0087JK\u0093\u0086R\u007f\u007fF\u007fC\u007fB\u007f\u007fOK" +
                                    "\u007f\u007f\u0083\u0089\u007f\u0081\u0086A\u007fR\u008a\u007fLCO\u0004\u007f\u000b\u007fC\u007f\u0088C\u007f\u007fR" +
                                    "G\u007f\u007f\u007f\u007f\u007fW\u007f\u007f\u007f\u007f\u007f\u0083\u007f\u007f\u007f\u007f\u007fC\u007f\u007f\u007f\u007f\u007fTR" +
                                    "\u0093\u007f\u0082\u007fA\u007f\u0083\u007f\u0083\u007f\u007f\u007f\u007fC\u007f\u007f\u007f\u007f\u007f\u007f\u0015\u007f\u007f\u007f\u007f\u007f" +
                                    "\u007f\u007f\u007f\u007f\u007f\u0096\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007fS\u007f\u007f\u007f\u007f\u007f\u007f\u007f" +
                                    "\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f" +
                                    "\u007f\u007f\u007f\u007fQ\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u0081L\u007f\u007f\u007f\u007f\u007f" +
                                    "Q\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007fJ\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007fC\u007f\u007f\u007f";

    static final long[] scCutOverTimes = { 1009839600000L, 9223372036854775807L, 9223372036854775807L, 1009839600000L, 1009839600000L, 9223372036854775807L, 9223372036854775807L, 9223372036854775807L, 9223372036854775807L, 9223372036854775807L, 1009839600000L, 9223372036854775807L, 1009839600000L, 1009839600000L, 9223372036854775807L, 1009836000000L, 9223372036854775807L, 1009843200000L, 1009839600000L, 9223372036854775807L, 1009839600000L, 9223372036854775807L, 1009839600000L, 9223372036854775807L, 1009839600000L, };

    static final String[] scOldCurrencies = { "FRF", "XCD", "USD", "ATS", "BEF", "XOF", "NOK", "AUD", "XAF", "NZD", "DEM", "MAD", "ESP", "FIM", "DKK", "GRD", "GBP", "IEP", "ITL", "CHF", "LUF", "XPF", "NLG", "ILS", "PTE", };

    static final String[] scNewCurrencies = { "EUR", null, null, "EUR", "EUR", null, null, null, null, null, "EUR", null, "EUR", "EUR", null, "EUR", null, "EUR", "EUR", null, "EUR", null, "EUR", null, "EUR", };

    static final int[] scOldCurrenciesDFD = { 2, 2, 2, 2, 0, 0, 2, 2, 0, 2, 2, 2, 0, 2, 2, 0, 2, 2, 0, 2, 0, 0, 2, 2, 0, };

    static final int[] scNewCurrenciesDFD = { 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 2, 0, 2, 2, 0, 2, 0, 2, 2, 0, 2, 0, 2, 0, 2, };

    static final String otherCurrencies = "ADP-ATS-BEF-BGL-BOV-BYB-CLF-DEM-ESP-EUR-FIM-FRF-GRD-IEP-ITL-LUF-MXV-NLG-PTE-RUB-USN-USS-XAF-XAG-XAU-XBA-XBB-XBC-XBD-XCD-XDR-XFO-XFU-XOF-XPD-XPF-XPT-XTS-XXX";

    static final int[] otherCurrenciesDFD = { 0, 2, 0, 2, 2, 0, 0, 2, 0, 2, 2, 2, 0, 2, 0, 0, 2, 2, 0, 2, 2, 2, 0, -1, -1, -1, -1, -1, -1, 2, -1, -1, -1, 0, -1, 0, -1, -1, -1, };

}

