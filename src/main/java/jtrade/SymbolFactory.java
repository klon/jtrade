package jtrade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jtrade.util.DateTimeRange;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDateTime;

public class SymbolFactory {
	public static List<Symbol> forex = new ArrayList<Symbol>();
	public static List<Symbol> futures = new ArrayList<Symbol>();
	public static List<Symbol> omxs30 = new ArrayList<Symbol>();
	public static List<Symbol> omxLargeCap = new ArrayList<Symbol>();
	public static List<Symbol> sp500 = new ArrayList<Symbol>();
	public static Map<String, Symbol> symbols = new TreeMap<String, Symbol>();
	static {
		symbols.put("24H-SFB-SEK-STOCK", new Symbol("24H-SFB-SEK-STOCK"));
		symbols.put("AAK-SFB-SEK-STOCK", new Symbol("AAK-SFB-SEK-STOCK"));
		symbols.put("ABB-SFB-SEK-STOCK", new Symbol("ABB-SFB-SEK-STOCK"));
		symbols.put("ABE-SFB-SEK-STOCK", new Symbol("ABE-SFB-SEK-STOCK"));
		symbols.put("ACAP.A-SFB-SEK-STOCK", new Symbol("ACAP.A-SFB-SEK-STOCK"));
		symbols.put("ACAP.B-SFB-SEK-STOCK", new Symbol("ACAP.B-SFB-SEK-STOCK"));
		symbols.put("ACOM-SFB-SEK-STOCK", new Symbol("ACOM-SFB-SEK-STOCK"));
		symbols.put("ACTI-SFB-SEK-STOCK", new Symbol("ACTI-SFB-SEK-STOCK"));
		symbols.put("ADDT.B-SFB-SEK-STOCK", new Symbol("ADDT.B-SFB-SEK-STOCK"));
		symbols.put("ADER.B-SFB-SEK-STOCK", new Symbol("ADER.B-SFB-SEK-STOCK"));
		symbols.put("AFAB.B-SFB-SEK-STOCK", new Symbol("AFAB.B-SFB-SEK-STOCK"));
		symbols.put("ALCA-SFB-SEK-STOCK", new Symbol("ALCA-SFB-SEK-STOCK"));
		symbols.put("ALFA-SFB-SEK-STOCK", new Symbol("ALFA-SFB-SEK-STOCK"));
		symbols.put("ALIV.SDB-SFB-SEK-STOCK", new Symbol("ALIV.SDB-SFB-SEK-STOCK"));
		symbols.put("ANGPB-SFB-SEK-STOCK", new Symbol("ANGPB-SFB-SEK-STOCK"));
		symbols.put("ANOT-SFB-SEK-STOCK", new Symbol("ANOT-SFB-SEK-STOCK"));
		symbols.put("AOI-SFB-SEK-STOCK", new Symbol("AOI-SFB-SEK-STOCK"));
		symbols.put("AQ-SFB-SEK-STOCK", new Symbol("AQ-SFB-SEK-STOCK"));
		symbols.put("AROC-SFB-SEK-STOCK", new Symbol("AROC-SFB-SEK-STOCK"));
		symbols.put("ARTI.B-SFB-SEK-STOCK", new Symbol("ARTI.B-SFB-SEK-STOCK"));
		symbols.put("ASP-SFB-SEK-STOCK", new Symbol("ASP-SFB-SEK-STOCK"));
		symbols.put("ASSA.B-SFB-SEK-STOCK", new Symbol("ASSA.B-SFB-SEK-STOCK"));
		symbols.put("ATCO.A-SFB-SEK-STOCK", new Symbol("ATCO.A-SFB-SEK-STOCK"));
		symbols.put("ATCO.B-SFB-SEK-STOCK", new Symbol("ATCO.B-SFB-SEK-STOCK"));
		symbols.put("ATI-SFB-SEK-STOCK", new Symbol("ATI-SFB-SEK-STOCK"));
		symbols.put("AWP-SFB-SEK-STOCK", new Symbol("AWP-SFB-SEK-STOCK"));
		symbols.put("AXFO-SFB-SEK-STOCK", new Symbol("AXFO-SFB-SEK-STOCK"));
		symbols.put("AXIS-SFB-SEK-STOCK", new Symbol("AXIS-SFB-SEK-STOCK"));
		symbols.put("AZA-SFB-SEK-STOCK", new Symbol("AZA-SFB-SEK-STOCK"));
		symbols.put("AZN-SFB-SEK-STOCK", new Symbol("AZN-SFB-SEK-STOCK"));
		symbols.put("BAHN B-SFB-SEK-STOCK", new Symbol("BAHN B-SFB-SEK-STOCK"));
		symbols.put("BCS-SFB-SEK-STOCK", new Symbol("BCS-SFB-SEK-STOCK"));
		symbols.put("BEF.SDB-SFB-SEK-STOCK", new Symbol("BEF.SDB-SFB-SEK-STOCK"));
		symbols.put("BEIA.B-SFB-SEK-STOCK", new Symbol("BEIA.B-SFB-SEK-STOCK"));
		symbols.put("BEIJ.B-SFB-SEK-STOCK", new Symbol("BEIJ.B-SFB-SEK-STOCK"));
		symbols.put("BELE-SFB-SEK-STOCK", new Symbol("BELE-SFB-SEK-STOCK"));
		symbols.put("BEO SDB-SFB-SEK-STOCK", new Symbol("BEO SDB-SFB-SEK-STOCK"));
		symbols.put("BERG.B-SFB-SEK-STOCK", new Symbol("BERG.B-SFB-SEK-STOCK"));
		symbols.put("BILI.A-SFB-SEK-STOCK", new Symbol("BILI.A-SFB-SEK-STOCK"));
		symbols.put("BILL-SFB-SEK-STOCK", new Symbol("BILL-SFB-SEK-STOCK"));
		symbols.put("BINV-SFB-SEK-STOCK", new Symbol("BINV-SFB-SEK-STOCK"));
		symbols.put("BIOG.B-SFB-SEK-STOCK", new Symbol("BIOG.B-SFB-SEK-STOCK"));
		symbols.put("BIOT-SFB-SEK-STOCK", new Symbol("BIOT-SFB-SEK-STOCK"));
		symbols.put("BMAX-SFB-SEK-STOCK", new Symbol("BMAX-SFB-SEK-STOCK"));
		symbols.put("BN.B-SFB-SEK-STOCK", new Symbol("BN.B-SFB-SEK-STOCK"));
		symbols.put("BOL-SFB-SEK-STOCK", new Symbol("BOL-SFB-SEK-STOCK"));
		symbols.put("BORG-SFB-SEK-STOCK", new Symbol("BORG-SFB-SEK-STOCK"));
		symbols.put("BOUL-SFB-SEK-STOCK", new Symbol("BOUL-SFB-SEK-STOCK"));
		symbols.put("BTS.B-SFB-SEK-STOCK", new Symbol("BTS.B-SFB-SEK-STOCK"));
		symbols.put("BURE-SFB-SEK-STOCK", new Symbol("BURE-SFB-SEK-STOCK"));
		symbols.put("BVTR-SFB-SEK-STOCK", new Symbol("BVTR-SFB-SEK-STOCK"));
		symbols.put("BWL-SFB-SEK-STOCK", new Symbol("BWL-SFB-SEK-STOCK"));
		symbols.put("CAST-SFB-SEK-STOCK", new Symbol("CAST-SFB-SEK-STOCK"));
		symbols.put("CCOR.B-SFB-SEK-STOCK", new Symbol("CCOR.B-SFB-SEK-STOCK"));
		symbols.put("CDON-SFB-SEK-STOCK", new Symbol("CDON-SFB-SEK-STOCK"));
		symbols.put("CHER.B-SFB-SEK-STOCK", new Symbol("CHER.B-SFB-SEK-STOCK"));
		symbols.put("CLA B-SFB-SEK-STOCK", new Symbol("CLA B-SFB-SEK-STOCK"));
		symbols.put("CLAS.B-SFB-SEK-STOCK", new Symbol("CLAS.B-SFB-SEK-STOCK"));
		symbols.put("CLS B-SFB-SEK-STOCK", new Symbol("CLS B-SFB-SEK-STOCK"));
		symbols.put("CNTA-SFB-SEK-STOCK", new Symbol("CNTA-SFB-SEK-STOCK"));
		symbols.put("COA-SFB-SEK-STOCK", new Symbol("COA-SFB-SEK-STOCK"));
		symbols.put("COIC-SFB-SEK-STOCK", new Symbol("COIC-SFB-SEK-STOCK"));
		symbols.put("CONS.B-SFB-SEK-STOCK", new Symbol("CONS.B-SFB-SEK-STOCK"));
		symbols.put("CSN-SFB-SEK-STOCK", new Symbol("CSN-SFB-SEK-STOCK"));
		symbols.put("CTT-SFB-SEK-STOCK", new Symbol("CTT-SFB-SEK-STOCK"));
		symbols.put("CYBE-SFB-SEK-STOCK", new Symbol("CYBE-SFB-SEK-STOCK"));
		symbols.put("CZON B-SFB-SEK-STOCK", new Symbol("CZON B-SFB-SEK-STOCK"));
		symbols.put("DEDI-SFB-SEK-STOCK", new Symbol("DEDI-SFB-SEK-STOCK"));
		symbols.put("DELT-SFB-SEK-STOCK", new Symbol("DELT-SFB-SEK-STOCK"));
		symbols.put("DIAM.B-SFB-SEK-STOCK", new Symbol("DIAM.B-SFB-SEK-STOCK"));
		symbols.put("DORO-SFB-SEK-STOCK", new Symbol("DORO-SFB-SEK-STOCK"));
		symbols.put("DV-SFB-SEK-STOCK", new Symbol("DV-SFB-SEK-STOCK"));
		symbols.put("ECEX-SFB-SEK-STOCK", new Symbol("ECEX-SFB-SEK-STOCK"));
		symbols.put("EFFN-SFB-SEK-STOCK", new Symbol("EFFN-SFB-SEK-STOCK"));
		symbols.put("EKOM-SFB-SEK-STOCK", new Symbol("EKOM-SFB-SEK-STOCK"));
		symbols.put("EKTA.B-SFB-SEK-STOCK", new Symbol("EKTA.B-SFB-SEK-STOCK"));
		symbols.put("ELAN.B-SFB-SEK-STOCK", new Symbol("ELAN.B-SFB-SEK-STOCK"));
		symbols.put("ELGR.B-SFB-SEK-STOCK", new Symbol("ELGR.B-SFB-SEK-STOCK"));
		symbols.put("ELUX.A-SFB-SEK-STOCK", new Symbol("ELUX.A-SFB-SEK-STOCK"));
		symbols.put("ELUX.B-SFB-SEK-STOCK", new Symbol("ELUX.B-SFB-SEK-STOCK"));
		symbols.put("ENDO-SFB-SEK-STOCK", new Symbol("ENDO-SFB-SEK-STOCK"));
		symbols.put("ENEA-SFB-SEK-STOCK", new Symbol("ENEA-SFB-SEK-STOCK"));
		symbols.put("ENLI.B-SFB-SEK-STOCK", new Symbol("ENLI.B-SFB-SEK-STOCK"));
		symbols.put("ENQ-SFB-SEK-STOCK", new Symbol("ENQ-SFB-SEK-STOCK"));
		symbols.put("ENRO-SFB-SEK-STOCK", new Symbol("ENRO-SFB-SEK-STOCK"));
		symbols.put("ENZY-SFB-SEK-STOCK", new Symbol("ENZY-SFB-SEK-STOCK"));
		symbols.put("EOLU B-SFB-SEK-STOCK", new Symbol("EOLU B-SFB-SEK-STOCK"));
		symbols.put("EOS-SFB-SEK-STOCK", new Symbol("EOS-SFB-SEK-STOCK"));
		symbols.put("EPCT-SFB-SEK-STOCK", new Symbol("EPCT-SFB-SEK-STOCK"));
		symbols.put("EPIS B-SFB-SEK-STOCK", new Symbol("EPIS B-SFB-SEK-STOCK"));
		symbols.put("ERIC.A-SFB-SEK-STOCK", new Symbol("ERIC.A-SFB-SEK-STOCK"));
		symbols.put("ERIC.B-SFB-SEK-STOCK", new Symbol("ERIC.B-SFB-SEK-STOCK"));
		symbols.put("ETX-SFB-SEK-STOCK", new Symbol("ETX-SFB-SEK-STOCK"));
		symbols.put("EUCI-SFB-SEK-STOCK", new Symbol("EUCI-SFB-SEK-STOCK"));
		symbols.put("EXIN.A-SFB-SEK-STOCK", new Symbol("EXIN A-SFB-SEK-STOCK"));
		symbols.put("EXPA.B-SFB-SEK-STOCK", new Symbol("EXPA.B-SFB-SEK-STOCK"));
		symbols.put("FABG-SFB-SEK-STOCK", new Symbol("FABG-SFB-SEK-STOCK"));
		symbols.put("FACLB-SFB-SEK-STOCK", new Symbol("FACLB-SFB-SEK-STOCK"));
		symbols.put("FBAB-SFB-SEK-STOCK", new Symbol("FBAB-SFB-SEK-STOCK"));
		symbols.put("FEEL-SFB-SEK-STOCK", new Symbol("FEEL-SFB-SEK-STOCK"));
		symbols.put("FIND-SFB-SEK-STOCK", new Symbol("FIND-SFB-SEK-STOCK"));
		symbols.put("FING.B-SFB-SEK-STOCK", new Symbol("FING.B-SFB-SEK-STOCK"));
		symbols.put("FIX.B-SFB-SEK-STOCK", new Symbol("FIX.B-SFB-SEK-STOCK"));
		symbols.put("FPAR-SFB-SEK-STOCK", new Symbol("FPAR-SFB-SEK-STOCK"));
		symbols.put("FPIP-SFB-SEK-STOCK", new Symbol("FPIP-SFB-SEK-STOCK"));
		symbols.put("FXI-SFB-SEK-STOCK", new Symbol("FXI-SFB-SEK-STOCK"));
		symbols.put("GETI.B-SFB-SEK-STOCK", new Symbol("GETI.B-SFB-SEK-STOCK"));
		symbols.put("GHP-SFB-SEK-STOCK", new Symbol("GHP-SFB-SEK-STOCK"));
		symbols.put("GIM-SFB-SEK-STOCK", new Symbol("GIM-SFB-SEK-STOCK"));
		symbols.put("GOLD-SFB-SEK-STOCK", new Symbol("GOLD-SFB-SEK-STOCK"));
		symbols.put("GULD-SFB-SEK-STOCK", new Symbol("GULD-SFB-SEK-STOCK"));
		symbols.put("GUNN-SFB-SEK-STOCK", new Symbol("GUNN-SFB-SEK-STOCK"));
		symbols.put("GVKO.B-SFB-SEK-STOCK", new Symbol("GVKO.B-SFB-SEK-STOCK"));
		symbols.put("HAGQ-SFB-SEK-STOCK", new Symbol("HAGQ-SFB-SEK-STOCK"));
		symbols.put("HAKN-SFB-SEK-STOCK", new Symbol("HAKN-SFB-SEK-STOCK"));
		symbols.put("HCH-SFB-SEK-STOCK", new Symbol("HCH-SFB-SEK-STOCK"));
		symbols.put("HEBA.B-SFB-SEK-STOCK", new Symbol("HEBA.B-SFB-SEK-STOCK"));
		symbols.put("HEMX-SFB-SEK-STOCK", new Symbol("HEMX-SFB-SEK-STOCK"));
		symbols.put("HEXA.B-SFB-SEK-STOCK", new Symbol("HEXA.B-SFB-SEK-STOCK"));
		symbols.put("HIQ-SFB-SEK-STOCK", new Symbol("HIQ-SFB-SEK-STOCK"));
		symbols.put("HLDX-SFB-SEK-STOCK", new Symbol("HLDX-SFB-SEK-STOCK"));
		symbols.put("HM.B-SFB-SEK-STOCK", new Symbol("HM.B-SFB-SEK-STOCK"));
		symbols.put("HOGA.B-SFB-SEK-STOCK", new Symbol("HOGA.B-SFB-SEK-STOCK"));
		symbols.put("HOLM.A-SFB-SEK-STOCK", new Symbol("HOLM.A-SFB-SEK-STOCK"));
		symbols.put("HOLM.B-SFB-SEK-STOCK", new Symbol("HOLM.B-SFB-SEK-STOCK"));
		symbols.put("HPOL.B-SFB-SEK-STOCK", new Symbol("HPOL.B-SFB-SEK-STOCK"));
		symbols.put("HUFV.A-SFB-SEK-STOCK", new Symbol("HUFV.A-SFB-SEK-STOCK"));
		symbols.put("HUFV.C-SFB-SEK-STOCK", new Symbol("HUFV.C-SFB-SEK-STOCK"));
		symbols.put("HUS-SFB-SEK-STOCK", new Symbol("HUS-SFB-SEK-STOCK"));
		symbols.put("HUSQA-SFB-SEK-STOCK", new Symbol("HUSQA-SFB-SEK-STOCK"));
		symbols.put("IAR B-SFB-SEK-STOCK", new Symbol("IAR B-SFB-SEK-STOCK"));
		symbols.put("ICTA.B-SFB-SEK-STOCK", new Symbol("ICTA.B-SFB-SEK-STOCK"));
		symbols.put("IFS.A-SFB-SEK-STOCK", new Symbol("IFS.A-SFB-SEK-STOCK"));
		symbols.put("IFS.B-SFB-SEK-STOCK", new Symbol("IFS.B-SFB-SEK-STOCK"));
		symbols.put("IJ-SFB-SEK-STOCK", new Symbol("IJ-SFB-SEK-STOCK"));
		symbols.put("INDU.A-SFB-SEK-STOCK", new Symbol("INDU.A-SFB-SEK-STOCK"));
		symbols.put("INDU.C-SFB-SEK-STOCK", new Symbol("INDU.C-SFB-SEK-STOCK"));
		symbols.put("INVE.A-SFB-SEK-STOCK", new Symbol("INVE.A-SFB-SEK-STOCK"));
		symbols.put("INVE.B-SFB-SEK-STOCK", new Symbol("INVE.B-SFB-SEK-STOCK"));
		symbols.put("INVK.A-SFB-SEK-STOCK", new Symbol("INVK.A-SFB-SEK-STOCK"));
		symbols.put("IRON-SFB-SEK-STOCK", new Symbol("IRON-SFB-SEK-STOCK"));
		symbols.put("ISCO-SFB-SEK-STOCK", new Symbol("ISCO-SFB-SEK-STOCK"));
		symbols.put("ITAB.B-SFB-SEK-STOCK", new Symbol("ITAB.B-SFB-SEK-STOCK"));
		symbols.put("IVSO-SFB-SEK-STOCK", new Symbol("IVSO-SFB-SEK-STOCK"));
		symbols.put("JAYS-SFB-SEK-STOCK", new Symbol("JAYS-SFB-SEK-STOCK"));
		symbols.put("JM-SFB-SEK-STOCK", new Symbol("JM-SFB-SEK-STOCK"));
		symbols.put("KAHL-SFB-SEK-STOCK", new Symbol("KAHL-SFB-SEK-STOCK"));
		symbols.put("KAN-SFB-SEK-STOCK", new Symbol("KAN-SFB-SEK-STOCK"));
		symbols.put("KARO-SFB-SEK-STOCK", new Symbol("KARO-SFB-SEK-STOCK"));
		symbols.put("KDEV-SFB-SEK-STOCK", new Symbol("KDEV-SFB-SEK-STOCK"));
		symbols.put("KINV.A-SFB-SEK-STOCK", new Symbol("KINV.A-SFB-SEK-STOCK"));
		symbols.put("KINV.B-SFB-SEK-STOCK", new Symbol("KINV.B-SFB-SEK-STOCK"));
		symbols.put("KLED-SFB-SEK-STOCK", new Symbol("KLED-SFB-SEK-STOCK"));
		symbols.put("KLOV-SFB-SEK-STOCK", new Symbol("KLOV-SFB-SEK-STOCK"));
		symbols.put("KNOW-SFB-SEK-STOCK", new Symbol("KNOW-SFB-SEK-STOCK"));
		symbols.put("KOPYB-SFB-SEK-STOCK", new Symbol("KOPYB-SFB-SEK-STOCK"));
		symbols.put("LAGR.B-SFB-SEK-STOCK", new Symbol("LAGR.B-SFB-SEK-STOCK"));
		symbols.put("LATO.B-SFB-SEK-STOCK", new Symbol("LATO.B-SFB-SEK-STOCK"));
		symbols.put("LAY-SFB-SEK-STOCK", new Symbol("LAY-SFB-SEK-STOCK"));
		symbols.put("LIAB-SFB-SEK-STOCK", new Symbol("LIAB-SFB-SEK-STOCK"));
		symbols.put("LJGR.B-SFB-SEK-STOCK", new Symbol("LJGR.B-SFB-SEK-STOCK"));
		symbols.put("LLSW B-SFB-SEK-STOCK", new Symbol("LLSW B-SFB-SEK-STOCK"));
		symbols.put("LOOMB-SFB-SEK-STOCK", new Symbol("LOOMB-SFB-SEK-STOCK"));
		symbols.put("LUMI-SFB-SEK-STOCK", new Symbol("LUMI-SFB-SEK-STOCK"));
		symbols.put("LUND.B-SFB-SEK-STOCK", new Symbol("LUND.B-SFB-SEK-STOCK"));
		symbols.put("LUPE-SFB-SEK-STOCK", new Symbol("LUPE-SFB-SEK-STOCK"));
		symbols.put("LUXO.SDB-SFB-SEK-STOCK", new Symbol("LUXO.SDB-SFB-SEK-STOCK"));
		symbols.put("MABI-SFB-SEK-STOCK", new Symbol("MABI-SFB-SEK-STOCK"));
		symbols.put("MEAB-SFB-SEK-STOCK", new Symbol("MEAB-SFB-SEK-STOCK"));
		symbols.put("MEDA.A-SFB-SEK-STOCK", new Symbol("MEDA.A-SFB-SEK-STOCK"));
		symbols.put("MEDR B-SFB-SEK-STOCK", new Symbol("MEDR B-SFB-SEK-STOCK"));
		symbols.put("MEK-SFB-SEK-STOCK", new Symbol("MEK-SFB-SEK-STOCK"));
		symbols.put("MELK-SFB-SEK-STOCK", new Symbol("MELK-SFB-SEK-STOCK"));
		symbols.put("MIC.SDB-SFB-SEK-STOCK", new Symbol("MIC.SDB-SFB-SEK-STOCK"));
		symbols.put("MICR-SFB-SEK-STOCK", new Symbol("MICR-SFB-SEK-STOCK"));
		symbols.put("MIDW.B-SFB-SEK-STOCK", new Symbol("MIDW.B-SFB-SEK-STOCK"));
		symbols.put("MII-SFB-SEK-STOCK", new Symbol("MII-SFB-SEK-STOCK"));
		symbols.put("MOB-SFB-SEK-STOCK", new Symbol("MOB-SFB-SEK-STOCK"));
		symbols.put("MOBY-SFB-SEK-STOCK", new Symbol("MOBY-SFB-SEK-STOCK"));
		symbols.put("MORPB-SFB-SEK-STOCK", new Symbol("MORPB-SFB-SEK-STOCK"));
		symbols.put("MPOS-SFB-SEK-STOCK", new Symbol("MPOS-SFB-SEK-STOCK"));
		symbols.put("MQ-SFB-SEK-STOCK", new Symbol("MQ-SFB-SEK-STOCK"));
		symbols.put("MTG.A-SFB-SEK-STOCK", new Symbol("MTG.A-SFB-SEK-STOCK"));
		symbols.put("MTG.B-SFB-SEK-STOCK", new Symbol("MTG.B-SFB-SEK-STOCK"));
		symbols.put("MTRO.A-SFB-SEK-STOCK", new Symbol("MTRO.A-SFB-SEK-STOCK"));
		symbols.put("MTRO.B-SFB-SEK-STOCK", new Symbol("MTRO.B-SFB-SEK-STOCK"));
		symbols.put("MVIR.B-SFB-SEK-STOCK", new Symbol("MVIR.B-SFB-SEK-STOCK"));
		symbols.put("NCC.A-SFB-SEK-STOCK", new Symbol("NCC.A-SFB-SEK-STOCK"));
		symbols.put("NCC.B-SFB-SEK-STOCK", new Symbol("NCC.B-SFB-SEK-STOCK"));
		symbols.put("NDA-SFB-SEK-STOCK", new Symbol("NDA-SFB-SEK-STOCK"));
		symbols.put("NETE-SFB-SEK-STOCK", new Symbol("NETE-SFB-SEK-STOCK"));
		symbols.put("NETI.B-SFB-SEK-STOCK", new Symbol("NETI.B-SFB-SEK-STOCK"));
		symbols.put("NETR-SFB-SEK-STOCK", new Symbol("NETR-SFB-SEK-STOCK"));
		symbols.put("NEWA.B-SFB-SEK-STOCK", new Symbol("NEWA.B-SFB-SEK-STOCK"));
		symbols.put("NEWS-SFB-SEK-STOCK", new Symbol("NEWS-SFB-SEK-STOCK"));
		symbols.put("NIBE.B-SFB-SEK-STOCK", new Symbol("NIBE.B-SFB-SEK-STOCK"));
		symbols.put("NN.B-SFB-SEK-STOCK", new Symbol("NN.B-SFB-SEK-STOCK"));
		symbols.put("NOBI-SFB-SEK-STOCK", new Symbol("NOBI-SFB-SEK-STOCK"));
		symbols.put("NOKIA-SFB-SEK-STOCK", new Symbol("NOKIA-SFB-SEK-STOCK"));
		symbols.put("NOLA.B-SFB-SEK-STOCK", new Symbol("NOLA.B-SFB-SEK-STOCK"));
		symbols.put("NOMI-SFB-SEK-STOCK", new Symbol("NOMI-SFB-SEK-STOCK"));
		symbols.put("NOTE-SFB-SEK-STOCK", new Symbol("NOTE-SFB-SEK-STOCK"));
		symbols.put("NOVE-SFB-SEK-STOCK", new Symbol("NOVE-SFB-SEK-STOCK"));
		symbols.put("NTEK.B-SFB-SEK-STOCK", new Symbol("NTEK.B-SFB-SEK-STOCK"));
		symbols.put("NVP-SFB-SEK-STOCK", new Symbol("NVP-SFB-SEK-STOCK"));
		symbols.put("OASM-SFB-SEK-STOCK", new Symbol("OASM-SFB-SEK-STOCK"));
		symbols.put("OEM.B-SFB-SEK-STOCK", new Symbol("OEM.B-SFB-SEK-STOCK"));
		symbols.put("OLDM-SFB-SEK-STOCK", new Symbol("OLDM-SFB-SEK-STOCK"));
		symbols.put("OPCO-SFB-SEK-STOCK", new Symbol("OPCO-SFB-SEK-STOCK"));
		symbols.put("OPVE.B-SFB-SEK-STOCK", new Symbol("OPVE.B-SFB-SEK-STOCK"));
		symbols.put("ORC-SFB-SEK-STOCK", new Symbol("ORC-SFB-SEK-STOCK"));
		symbols.put("ORES-SFB-SEK-STOCK", new Symbol("ORES-SFB-SEK-STOCK"));
		symbols.put("ORI.SDB-SFB-SEK-STOCK", new Symbol("ORI.SDB-SFB-SEK-STOCK"));
		symbols.put("ORTI.B-SFB-SEK-STOCK", new Symbol("ORTI.B-SFB-SEK-STOCK"));
		symbols.put("ORX-SFB-SEK-STOCK", new Symbol("ORX-SFB-SEK-STOCK"));
		symbols.put("PACT-SFB-SEK-STOCK", new Symbol("PACT-SFB-SEK-STOCK"));
		symbols.put("PALS B-SFB-SEK-STOCK", new Symbol("PALS B-SFB-SEK-STOCK"));
		symbols.put("PARE-SFB-SEK-STOCK", new Symbol("PARE-SFB-SEK-STOCK"));
		symbols.put("PART-SFB-SEK-STOCK", new Symbol("PART-SFB-SEK-STOCK"));
		symbols.put("PEAB.B-SFB-SEK-STOCK", new Symbol("PEAB.B-SFB-SEK-STOCK"));
		symbols.put("PFE-SFB-SEK-STOCK", new Symbol("PFE-SFB-SEK-STOCK"));
		symbols.put("PLED-SFB-SEK-STOCK", new Symbol("PLED-SFB-SEK-STOCK"));
		symbols.put("POOL-SFB-SEK-STOCK", new Symbol("POOL-SFB-SEK-STOCK"));
		symbols.put("PREC.A-SFB-SEK-STOCK", new Symbol("PREC.A-SFB-SEK-STOCK"));
		symbols.put("PREV.B-SFB-SEK-STOCK", new Symbol("PREV.B-SFB-SEK-STOCK"));
		symbols.put("PRIC.B-SFB-SEK-STOCK", new Symbol("PRIC.B-SFB-SEK-STOCK"));
		symbols.put("PROE.B-SFB-SEK-STOCK", new Symbol("PROE.B-SFB-SEK-STOCK"));
		symbols.put("PROF.B-SFB-SEK-STOCK", new Symbol("PROF.B-SFB-SEK-STOCK"));
		symbols.put("PSI-SFB-SEK-STOCK", new Symbol("PSI-SFB-SEK-STOCK"));
		symbols.put("PXXSSDB-SFB-SEK-STOCK", new Symbol("PXXSSDB-SFB-SEK-STOCK"));
		symbols.put("RATO.A-SFB-SEK-STOCK", new Symbol("RATO.A-SFB-SEK-STOCK"));
		symbols.put("RATO.B-SFB-SEK-STOCK", new Symbol("RATO.B-SFB-SEK-STOCK"));
		symbols.put("RAY.B-SFB-SEK-STOCK", new Symbol("RAY.B-SFB-SEK-STOCK"));
		symbols.put("REZT-SFB-SEK-STOCK", new Symbol("REZT-SFB-SEK-STOCK"));
		symbols.put("RNBS-SFB-SEK-STOCK", new Symbol("RNBS-SFB-SEK-STOCK"));
		symbols.put("RROS-SFB-SEK-STOCK", new Symbol("RROS-SFB-SEK-STOCK"));
		symbols.put("RSOF.B-SFB-SEK-STOCK", new Symbol("RSOF.B-SFB-SEK-STOCK"));
		symbols.put("RTIM.B-SFB-SEK-STOCK", new Symbol("RTIM.B-SFB-SEK-STOCK"));
		symbols.put("RUN-SFB-SEK-STOCK", new Symbol("RUN-SFB-SEK-STOCK"));
		symbols.put("RUSF-SFB-SEK-STOCK", new Symbol("RUSF-SFB-SEK-STOCK"));
		symbols.put("SAAB.B-SFB-SEK-STOCK", new Symbol("SAAB.B-SFB-SEK-STOCK"));
		symbols.put("SAFE-SFB-SEK-STOCK", new Symbol("SAFE-SFB-SEK-STOCK"));
		symbols.put("SAGA PREF-SFB-SEK-STOCK", new Symbol("SAGA PREF-SFB-SEK-STOCK"));
		symbols.put("SAND-SFB-SEK-STOCK", new Symbol("SAND-SFB-SEK-STOCK"));
		symbols.put("SAS-SFB-SEK-STOCK", new Symbol("SAS-SFB-SEK-STOCK"));
		symbols.put("SBOK-SFB-SEK-STOCK", new Symbol("SBOK-SFB-SEK-STOCK"));
		symbols.put("SCA.A-SFB-SEK-STOCK", new Symbol("SCA.A-SFB-SEK-STOCK"));
		symbols.put("SCA.B-SFB-SEK-STOCK", new Symbol("SCA.B-SFB-SEK-STOCK"));
		symbols.put("SCOR-SFB-SEK-STOCK", new Symbol("SCOR-SFB-SEK-STOCK"));
		symbols.put("SCOR B-SFB-SEK-STOCK", new Symbol("SCOR B-SFB-SEK-STOCK"));
		symbols.put("SCRI.A-SFB-SEK-STOCK", new Symbol("SCRI.A-SFB-SEK-STOCK"));
		symbols.put("SCRI.B-SFB-SEK-STOCK", new Symbol("SCRI.B-SFB-SEK-STOCK"));
		symbols.put("SCV.A-SFB-SEK-STOCK", new Symbol("SCV.A-SFB-SEK-STOCK"));
		symbols.put("SCV.B-SFB-SEK-STOCK", new Symbol("SCV.B-SFB-SEK-STOCK"));
		symbols.put("SEB.A-SFB-SEK-STOCK", new Symbol("SEB.A-SFB-SEK-STOCK"));
		symbols.put("SEB.C-SFB-SEK-STOCK", new Symbol("SEB.C-SFB-SEK-STOCK"));
		symbols.put("SECT-SFB-SEK-STOCK", new Symbol("SECT-SFB-SEK-STOCK"));
		symbols.put("SECU.B-SFB-SEK-STOCK", new Symbol("SECU.B-SFB-SEK-STOCK"));
		symbols.put("SEMC-SFB-SEK-STOCK", new Symbol("SEMC-SFB-SEK-STOCK"));
		symbols.put("SENS-SFB-SEK-STOCK", new Symbol("SENS-SFB-SEK-STOCK"));
		symbols.put("SERS B-SFB-SEK-STOCK", new Symbol("SERS B-SFB-SEK-STOCK"));
		symbols.put("SHB.A-SFB-SEK-STOCK", new Symbol("SHB.A-SFB-SEK-STOCK"));
		symbols.put("SHB.B-SFB-SEK-STOCK", new Symbol("SHB.B-SFB-SEK-STOCK"));
		symbols.put("SIGM.B-SFB-SEK-STOCK", new Symbol("SIGM.B-SFB-SEK-STOCK"));
		symbols.put("SINT-SFB-SEK-STOCK", new Symbol("SINT-SFB-SEK-STOCK"));
		symbols.put("SION-SFB-SEK-STOCK", new Symbol("SION-SFB-SEK-STOCK"));
		symbols.put("SKA.B-SFB-SEK-STOCK", new Symbol("SKA.B-SFB-SEK-STOCK"));
		symbols.put("SKF.A-SFB-SEK-STOCK", new Symbol("SKF.A-SFB-SEK-STOCK"));
		symbols.put("SKF.B-SFB-SEK-STOCK", new Symbol("SKF.B-SFB-SEK-STOCK"));
		symbols.put("SKIS.B-SFB-SEK-STOCK", new Symbol("SKIS.B-SFB-SEK-STOCK"));
		symbols.put("SMF-SFB-SEK-STOCK", new Symbol("SMF-SFB-SEK-STOCK"));
		symbols.put("SNM-SFB-SEK-STOCK", new Symbol("SNM-SFB-SEK-STOCK"));
		symbols.put("SOF.B-SFB-SEK-STOCK", new Symbol("SOF.B-SFB-SEK-STOCK"));
		symbols.put("SPOTR OMX-SFB-SEK-STOCK", new Symbol("SPOTR OMX-SFB-SEK-STOCK"));
		symbols.put("SPOTRBEAR-SFB-SEK-STOCK", new Symbol("SPOTRBEAR-SFB-SEK-STOCK"));
		symbols.put("SPOTRBULL-SFB-SEK-STOCK", new Symbol("SPOTRBULL-SFB-SEK-STOCK"));
		symbols.put("SSAB.A-SFB-SEK-STOCK", new Symbol("SSAB.A-SFB-SEK-STOCK"));
		symbols.put("SSAB.B-SFB-SEK-STOCK", new Symbol("SSAB.B-SFB-SEK-STOCK"));
		symbols.put("STE.A-SFB-SEK-STOCK", new Symbol("STE.A-SFB-SEK-STOCK"));
		symbols.put("STE.R-SFB-SEK-STOCK", new Symbol("STE.R-SFB-SEK-STOCK"));
		symbols.put("SVED.B-SFB-SEK-STOCK", new Symbol("SVED.B-SFB-SEK-STOCK"));
		symbols.put("SVIK-SFB-SEK-STOCK", new Symbol("SVIK-SFB-SEK-STOCK"));
		symbols.put("SVOL.B-SFB-SEK-STOCK", new Symbol("SVOL.B-SFB-SEK-STOCK"));
		symbols.put("SWEC.A-SFB-SEK-STOCK", new Symbol("SWEC.A-SFB-SEK-STOCK"));
		symbols.put("SWEC.B-SFB-SEK-STOCK", new Symbol("SWEC.B-SFB-SEK-STOCK"));
		symbols.put("SWEDA-SFB-SEK-STOCK", new Symbol("SWEDA-SFB-SEK-STOCK"));
		symbols.put("SWEDPREF-SFB-SEK-STOCK", new Symbol("SWEDPREF-SFB-SEK-STOCK"));
		symbols.put("SWMA-SFB-SEK-STOCK", new Symbol("SWMA-SFB-SEK-STOCK"));
		symbols.put("SXPX-SFB-SEK-STOCK", new Symbol("SXPX-SFB-SEK-STOCK"));
		symbols.put("SYSI.B-SFB-SEK-STOCK", new Symbol("SYSI.B-SFB-SEK-STOCK"));
		symbols.put("TAGR-SFB-SEK-STOCK", new Symbol("TAGR-SFB-SEK-STOCK"));
		symbols.put("TEL2.A-SFB-SEK-STOCK", new Symbol("TEL2.A-SFB-SEK-STOCK"));
		symbols.put("TEL2.B-SFB-SEK-STOCK", new Symbol("TEL2.B-SFB-SEK-STOCK"));
		symbols.put("TETY-SFB-SEK-STOCK", new Symbol("TETY-SFB-SEK-STOCK"));
		symbols.put("TIGR-SFB-SEK-STOCK", new Symbol("TIGR-SFB-SEK-STOCK"));
		symbols.put("TLSN-SFB-SEK-STOCK", new Symbol("TLSN-SFB-SEK-STOCK"));
		symbols.put("TRAD-SFB-SEK-STOCK", new Symbol("TRAD-SFB-SEK-STOCK"));
		symbols.put("TREL.B-SFB-SEK-STOCK", new Symbol("TREL.B-SFB-SEK-STOCK"));
		symbols.put("TRMO-SFB-SEK-STOCK", new Symbol("TRMO-SFB-SEK-STOCK"));
		symbols.put("TTEB-SFB-SEK-STOCK", new Symbol("TTEB-SFB-SEK-STOCK"));
		symbols.put("TWW.A.SDB-SFB-SEK-STOCK", new Symbol("TWW.A.SDB-SFB-SEK-STOCK"));
		symbols.put("TWW.B.SDB-SFB-SEK-STOCK", new Symbol("TWW.B.SDB-SFB-SEK-STOCK"));
		symbols.put("UFLX B-SFB-SEK-STOCK", new Symbol("UFLX B-SFB-SEK-STOCK"));
		symbols.put("UNIB.SDB-SFB-SEK-STOCK", new Symbol("UNIB.SDB-SFB-SEK-STOCK"));
		symbols.put("VALU-SFB-SEK-STOCK", new Symbol("VALU-SFB-SEK-STOCK"));
		symbols.put("VBG.B-SFB-SEK-STOCK", new Symbol("VBG.B-SFB-SEK-STOCK"));
		symbols.put("VFS-SFB-SEK-STOCK", new Symbol("VFS-SFB-SEK-STOCK"));
		symbols.put("VIKT-SFB-SEK-STOCK", new Symbol("VIKT-SFB-SEK-STOCK"));
		symbols.put("VITR-SFB-SEK-STOCK", new Symbol("VITR-SFB-SEK-STOCK"));
		symbols.put("VNIL-SFB-SEK-STOCK", new Symbol("VNIL-SFB-SEK-STOCK"));
		symbols.put("VOLV.A-SFB-SEK-STOCK", new Symbol("VOLV.A-SFB-SEK-STOCK"));
		symbols.put("VOLV.B-SFB-SEK-STOCK", new Symbol("VOLV.B-SFB-SEK-STOCK"));
		symbols.put("WALL.B-SFB-SEK-STOCK", new Symbol("WALL.B-SFB-SEK-STOCK"));
		symbols.put("WED.B-SFB-SEK-STOCK", new Symbol("WED.B-SFB-SEK-STOCK"));
		symbols.put("WEST.B-SFB-SEK-STOCK", new Symbol("WEST.B-SFB-SEK-STOCK"));
		symbols.put("WIHL-SFB-SEK-STOCK", new Symbol("WIHL-SFB-SEK-STOCK"));
		symbols.put("WNT-SFB-SEK-STOCK", new Symbol("WNT-SFB-SEK-STOCK"));
		symbols.put("WSIB-SFB-SEK-STOCK", new Symbol("WSIB-SFB-SEK-STOCK"));
		symbols.put("WSON.B-SFB-SEK-STOCK", new Symbol("WSON.B-SFB-SEK-STOCK"));
		symbols.put("XACT LAKE-SFB-SEK-STOCK", new Symbol("XACT LAKE-SFB-SEK-STOCK"));
		symbols.put("XACT OBLI-SFB-SEK-STOCK", new Symbol("XACT OBLI-SFB-SEK-STOCK"));
		symbols.put("XACT REPO-SFB-SEK-STOCK", new Symbol("XACT REPO-SFB-SEK-STOCK"));
		symbols.put("XACT VERK-SFB-SEK-STOCK", new Symbol("XACT VERK-SFB-SEK-STOCK"));
		symbols.put("XACT.BEAR-SFB-SEK-STOCK", new Symbol("XACT.BEAR-SFB-SEK-STOCK"));
		symbols.put("XACT.BULL-SFB-SEK-STOCK", new Symbol("XACT.BULL-SFB-SEK-STOCK"));
		symbols.put("XACT.OMX-SFB-SEK-STOCK", new Symbol("XACT.OMX-SFB-SEK-STOCK"));
		symbols.put("XACT.SBX-SFB-SEK-STOCK", new Symbol("XACT.SBX-SFB-SEK-STOCK"));
		symbols.put("XAXJ-SFB-SEK-STOCK", new Symbol("XAXJ-SFB-SEK-STOCK"));
		symbols.put("XEDS-SFB-SEK-STOCK", new Symbol("XEDS-SFB-SEK-STOCK"));
		symbols.put("XSL2-SFB-SEK-STOCK", new Symbol("XSL2-SFB-SEK-STOCK"));
		symbols.put("XX25-SFB-SEK-STOCK", new Symbol("XX25-SFB-SEK-STOCK"));
		symbols.put("ZETADISP-SFB-SEK-STOCK", new Symbol("ZETADISP-SFB-SEK-STOCK"));

		sp500.add(new Symbol("MMM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ACE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AES", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AFL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("GAS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("T", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ABBV", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ABT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ANF", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ACN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ACT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ADBE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AMD", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AET", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("A", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("APD", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ARG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AKAM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AA", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ALXN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ATI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AGN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ALL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ALTR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AMZN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AEE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AEP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AXP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AIG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AMT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AMP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ABC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AMGN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("APH", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("APC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ADI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AON", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("APA", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AIV", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("APOL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AAPL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AMAT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ADM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AIZ", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AZO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ADSK", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ADP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AVB", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AVY", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("AVP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BBT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BMC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BHI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BLL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BAC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BCR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BAX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BEAM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BDX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BBBY", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BMS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BRK.B", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BBY", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BIIB", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BLK", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HRB", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BA", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BWA", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BXP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BSX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BMY", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BRCM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BF.B", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CA", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CBG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CBS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CF", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CHRW", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CME", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CMS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CNX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CSX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CVS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CVC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("COG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CAM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CPB", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("COF", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CAH", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CFN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("KMX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CCL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CAT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CELG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CNP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CTL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CERN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CHK", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CVX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CMG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CB", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CINF", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CTAS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CSCO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("C", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CTXS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CLF", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CLX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("COH", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("KO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CCE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CTSH", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CMCSA", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CMA", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CSC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CAG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("COP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ED", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("STZ", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("GLW", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("COST", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CVH", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("COV", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CCI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CMI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DTV", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DTE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DVA", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DHR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DRI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DF", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DELL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DLPH", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DNR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("XRAY", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DVN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DFS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DISCA", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DLTR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("D", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DOV", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DOW", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DPS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DUK", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DNB", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ETFC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DD", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("EMC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("EOG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("EQT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("EMN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ETN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ECL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("EIX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("EW", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("EA", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("EMR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ESV", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ETR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("EFX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("EQR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("EL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("EXC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("EXPE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("EXPD", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ESRX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("XOM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FFIV", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FLIR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FMC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FTI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FDO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FAST", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FDX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FIS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FITB", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FHN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FSLR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FISV", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FLS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FLR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("F", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FRX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FOSL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BEN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FCX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("FTR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("GME", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("GCI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("GPS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("GRMN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("GD", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("GE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("GIS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("GPC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("GNW", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("GILD", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("GS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("GT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("GOOG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("GWW", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HCP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HAL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HOG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HAR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HRS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HIG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HAS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HCN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HNZ", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HSY", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HES", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HPQ", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HD", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HON", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HRL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DHI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HSP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HST", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HCBK", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HUM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HBAN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ITW", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("IR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TEG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("INTC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ICE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("IPG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("IBM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("IFF", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("IGT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("IP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("INTU", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ISRG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("IVZ", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("IRM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("JDSU", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("JPM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("JBL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("JEC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("JNJ", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("JCI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("JOY", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("JNPR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("KLAC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("K", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("KEY", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("KMB", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("KIM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("KMI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("KSS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("KRFT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("KR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LLL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LSI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LH", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LRCX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LEG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LEN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LUK", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LIFE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LLY", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LTD", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LNC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LLTC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LMT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("L", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LOW", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LYB", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MTB", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("M", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MRO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MPC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MAR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MMC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MAS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MA", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MAT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MKC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MCD", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MHP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MCK", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MJN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MWV", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MDT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MRK", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MET", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PCS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MCHP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MU", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MSFT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MOLX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TAP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MDLZ", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MON", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MNST", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MCO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MOS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MSI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MUR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("MYL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NKE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NRG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NYX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NBR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NDAQ", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NOV", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NTAP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NFLX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NWL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NFX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NEM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NWSA", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NEE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NBL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("JWN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NSC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NU", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NTRS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NOC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NUE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("NVDA", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ORLY", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("OKE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("OXY", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("OMC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ORCL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("OI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PCAR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PETM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PCG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PNC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PPG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PPL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PVH", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PLL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PH", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PDCO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PAYX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BTU", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("JCP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PNR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PBCT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("POM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PEP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PKI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PRGO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PFE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PSX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PNW", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PXD", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PBI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PCL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PCP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PCLN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PFG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PLD", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PGR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PRU", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PEG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PSA", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PHM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("QEP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("QCOM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("PWR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DGX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("RL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("RRC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("RTN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("RHT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("RF", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("RSG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("RAI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("RHI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ROK", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("COL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ROP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ROST", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("RDC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("R", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SAI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SCG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SLM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SWY", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("CRM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SNDK", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SLB", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SCHW", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SNI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("STX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SEE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SRE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SHW", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SIAL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SPG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SJM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SNA", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("LUV", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SWN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("S", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("STJ", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SWK", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SPLS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SBUX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("HOT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("STT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SRCL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SYK", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("STI", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SYMC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("SYY", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TROW", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TEL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TE", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TJX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TGT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("THC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TDC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TER", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TSO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TXN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TXT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ADT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("BK", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WMB", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TMO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TIF", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TWC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TWX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TMK", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TSS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TRV", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TRIP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TYC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("TSN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("USB", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("UNP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("UPS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("X", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("UTX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("UNH", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("UNM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("URBN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("VFC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("VLO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("VAR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("VTR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("VRSN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("VZ", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("VIAB", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("V", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("VNO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("VMC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WPX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WMT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WAG", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("DIS", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WPO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WAT", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WLP", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WFC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WDC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WU", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WY", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WHR", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WFM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WIN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WEC", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WYN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("WYNN", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("XL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("XEL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("XRX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("XLNX", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("XYL", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("YHOO", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("YUM", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ZMH", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("ZION", "SMART", "USD", "STOCK", 1, 0.05));
		sp500.add(new Symbol("EBAY", "SMART", "USD", "STOCK", 1, 0.05));
		for (Symbol f : sp500) {
			symbols.put(f.getFullCode(), f);
		}

		symbols.put("OMXS30-OMS-SEK-INDEX", new Symbol("OMXS30", "OMS", "SEK", "INDEX", 100, 0.25));
		symbols.put("SPX-CBOE-USD-INDEX", new Symbol("SPX-CBOE-USD-INDEX"));
		symbols.put("INDU-NYSE-USD-INDEX", new Symbol("INDU-NYSE-USD-INDEX"));
		symbols.put("COMP-NASDAQ-USD-INDEX", new Symbol("COMP-NASDAQ-USD-INDEX"));
		symbols.put("Z-LIFFE-GBP-INDEX", new Symbol("Z-LIFFE-GBP-INDEX"));
		symbols.put("DAX-DTB-EUR-INDEX", new Symbol("DAX-DTB-EUR-INDEX"));
		symbols.put("ESTX50-DTB-EUR-INDEX", new Symbol("ESTX50-DTB-EUR-INDEX"));
		symbols.put("CAC40-MONEP-EUR-INDEX", new Symbol("CAC40-MONEP-EUR-INDEX"));
		symbols.put("N225-OSE.JPN-JPY-INDEX", new Symbol("N225-OSE.JPN-JPY-INDEX"));
		symbols.put("VIX-CBOE-USD-INDEX", new Symbol("VIX-CBOE-USD-INDEX"));
		symbols.put("HSI-HKFE-HKD-INDEX", new Symbol("HSI-HKFE-HKD-INDEX"));

		futures.add(new Symbol("OMXS30", "OMS", "SEK", "FUTURE", 100, 0.25));
		futures.add(new Symbol("ES", "GLOBEX", "USD", "FUTURE", 50, 0.25));
		futures.add(new Symbol("Z", "LIFFE", "GBP", "FUTURE", 10, 0.5));
		futures.add(new Symbol("DAX", "DTB", "EUR", "FUTURE", 25, 0.5));
		futures.add(new Symbol("ESTX50", "DTB", "EUR", "FUTURE", 10, 1.0));
		futures.add(new Symbol("CAC40", "MONEP", "EUR", "FUTURE", 10, 0.5));
		futures.add(new Symbol("WTI", "IPE", "USD", "FUTURE", 1000, 0.01));
		futures.add(new Symbol("COIL", "IPE", "USD", "FUTURE", 100, 0.25));
		futures.add(new Symbol("GC", "NYMEX", "USD", "FUTURE", 100, 0.1));
		futures.add(new Symbol("CL", "NYMEX", "USD", "FUTURE", 1000, 0.01));
		for (Symbol f : futures) {
			symbols.put(f.getFullCode(), f);
		}
		for (Iterator<DateTime> dates = new DateTimeRange(new DateTime().withDayOfYear(1).minusYears(2), new DateTime().withDayOfYear(1).plusYears(2))
				.iterator(Months.ONE); dates.hasNext();) {
			DateTime dt = dates.next();
			for (Symbol f : futures) {
				Symbol s = getMostLiquidFutureSymbol(f, dt);
				symbols.put(s.getFullCode(), s);
			}
		}

		forex.add(new Symbol("EUR", "IDEALPRO", "USD", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "SEK", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "CHF", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "CAD", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "HKD", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "SGD", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "RUB", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "CNH", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "CZK", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "HUF", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "ILS", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "DKK", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "NOK", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "PLN", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "MXN", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "JPY", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "USD", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "GBP", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "SEK", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "CHF", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "CAD", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "AUD", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "HKD", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "SGD", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "RUB", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "CNH", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "CZK", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "HUF", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "ILS", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "DKK", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "NOK", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "PLN", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "MXN", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "JPY", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "USD", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "NZD", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "SEK", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "CHF", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "CAD", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "AUD", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "HKD", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "CNH", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "DKK", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "NOK", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "MXN", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "JPY", "CASH", 1, 0.0001));
		forex.add(new Symbol("CAD", "IDEALPRO", "CHF", "CASH", 1, 0.0001));
		forex.add(new Symbol("CAD", "IDEALPRO", "HKD", "CASH", 1, 0.0001));
		forex.add(new Symbol("CAD", "IDEALPRO", "JPY", "CASH", 1, 0.0001));
		forex.add(new Symbol("CAD", "IDEALPRO", "CNH", "CASH", 1, 0.0001));
		forex.add(new Symbol("AUD", "IDEALPRO", "USD", "CASH", 1, 0.0001));
		forex.add(new Symbol("AUD", "IDEALPRO", "CHF", "CASH", 1, 0.0001));
		forex.add(new Symbol("AUD", "IDEALPRO", "HKD", "CASH", 1, 0.0001));
		forex.add(new Symbol("AUD", "IDEALPRO", "JPY", "CASH", 1, 0.0001));
		forex.add(new Symbol("AUD", "IDEALPRO", "CAD", "CASH", 1, 0.0001));
		forex.add(new Symbol("AUD", "IDEALPRO", "CNH", "CASH", 1, 0.0001));
		forex.add(new Symbol("AUD", "IDEALPRO", "NZD", "CASH", 1, 0.0001));
		forex.add(new Symbol("AUD", "IDEALPRO", "SGD", "CASH", 1, 0.0001));
		forex.add(new Symbol("CHF", "IDEALPRO", "JPY", "CASH", 1, 0.0001));
		forex.add(new Symbol("CHF", "IDEALPRO", "CNH", "CASH", 1, 0.0001));
		forex.add(new Symbol("CHF", "IDEALPRO", "DKK", "CASH", 1, 0.0001));
		forex.add(new Symbol("CHF", "IDEALPRO", "NOK", "CASH", 1, 0.0001));
		forex.add(new Symbol("CHF", "IDEALPRO", "SEK", "CASH", 1, 0.0001));
		for (Symbol f : forex) {
			symbols.put(f.getFullCode(), f);
		}

		omxs30.add(symbols.get("ABB-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("ALFA-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("ASSA.B-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("AZN-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("ATCO.A-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("ATCO.B-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("BOL-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("ELUX.B-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("ERIC.B-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("GETI.B-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("HM.B-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("INVE.B-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("LUPE-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("MTG.B-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("NOKI.SEK-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("NDA.SEK-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("SAND-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("SCA.B-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("SCV.B-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("SEB.A-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("SECU.B-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("SKA.B-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("SKF.B-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("SSAB.A-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("SHB.A-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("SWED.A-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("SWMA-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("TEL2.B-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("TLSN-SFB-SEK-STOCK"));
		omxs30.add(symbols.get("VOLV.B-SFB-SEK-STOCK"));

		omxLargeCap.add(symbols.get("ABB-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("ALFA-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("ALIV.SDB-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("ASSA.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("ATCO.A-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("ATCO.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("AXFO-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("AZN-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("BOL-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("CAST-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("EKTA.B-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("ELUX.A-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("ELUX.B-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("ERIC.A-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("ERIC.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("FABG-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("GETI.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("HAKN-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("HEXA.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("HM.B-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("HOLM.A-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("HOLM.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("HUFV.A-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("HUFV.C-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("HUSQA-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("HUS-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("INDU.A-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("INDU.C-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("INVE.A-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("INVE.B-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("KINV.A-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("KINV.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("LATO.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("LJGR.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("LUMI-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("LUND.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("LUPE-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("MEDA.A-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("MELK-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("MIC.SDB-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("MTG.A-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("MTG.B-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("NCC.A-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("NCC.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("NDA-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("ORI.SDB-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("PEAB.B-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("RATO.A-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("RATO.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("SAAB.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("SAND-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("SCA.A-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("SCA.B-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("SCV.A-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("SCV.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("SEB.A-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("SEB.C-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("SECU.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("SMF-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("SHB.A-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("SHB.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("SKA.B-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("SKF.A-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("SKF.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("SSAB.A-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("SSAB.B-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("STE.A-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("STE.R-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("SWEDA-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("SWEDPREF-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("SWMA-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("TEL2.A-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("TEL2.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("TTEB-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("TLSN-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("TREL.B-SFB-SEK-STOCK"));
		// omxLargeCap.add(symbols.get("VOLV.A-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("VOLV.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("WALL.B-SFB-SEK-STOCK"));
		omxLargeCap.add(symbols.get("WSIB-SFB-SEK-STOCK"));
	}

	public static List<Symbol> getFutures() {
		return new ArrayList<Symbol>(futures);
	}

	public static List<Symbol> getSP500() {
		return new ArrayList<Symbol>(sp500);
	}

	public static List<Symbol> getOMXS30Stocks() {
		return new ArrayList<Symbol>(omxs30);
	}

	public static List<Symbol> getOMXLargeCap() {
		return new ArrayList<Symbol>(omxLargeCap);
	}

	public static List<Symbol> getSFBStocks(String... code) {
		List<Symbol> result = new ArrayList<Symbol>(code.length);
		for (String s : code) {
			result.add(getSFBStock(s));
		}
		return result;
	}

	public static List<Symbol> getSymbols(List<String> symbols) {
		return getSymbols(symbols.toArray(new String[symbols.size()]));
	}

	public static List<Symbol> getSymbols(String... symbol) {
		List<Symbol> result = new ArrayList<Symbol>(symbol.length);
		for (String s : symbol) {
			result.add(getSymbol(s));
		}
		return result;
	}

	public static List<Symbol> getExchangeSymbols(String exchange) {
		List<Symbol> result = new ArrayList<Symbol>();
		for (Symbol s : symbols.values()) {
			if (s.getExchange().equals(exchange)) {
				result.add(s);
			}
		}
		return result;
	}

	public static Symbol getSymbol(String fullCode) {
		Symbol s = symbols.get(fullCode);
		if (s == null) {
			s = new Symbol(fullCode);
			symbols.put(fullCode, s);
		}
		return s;
	}

	public static Symbol getCash(String currency, String baseCurrency) {
		return getSymbol(new StringBuilder().append(currency).append("-IDEALPRO-").append(baseCurrency).append("-CASH").toString());
	}

	public static Symbol getSFBStock(String code) {
		Symbol s = symbols.get(code.concat("-SFB-SEK-STOCK"));
		if (s == null) {
			throw new IllegalArgumentException("Invalid SFB stock: " + code);
		}
		return s;
	}

	public static Symbol getSP500Index() {
		return symbols.get("SPX-CBOE-USD-INDEX");
	}

	public static Symbol getDowIndex() {
		return symbols.get("INDU-NYSE-USD-INDEX");
	}

	public static Symbol getNasdaqIndex() {
		return symbols.get("COMP-NASDAQ-USD-INDEX");
	}

	public static Symbol getNikkeiIndex() {
		return symbols.get("N225-OSE.JPN-JPY-INDEX");
	}

	public static Symbol getHangSengIndex() {
		return symbols.get("HSI-HKFE-HKD-INDEX");
	}

	public static Symbol getVixIndex() {
		return symbols.get("VIX-CBOE-USD-INDEX");
	}

	public static Symbol getDAXIndex() {
		return symbols.get("DAX-DTB-EUR-INDEX");
	}

	public static Symbol getESTX50Index() {
		return symbols.get("ESTX50-DTB-EUR-INDEX");
	}

	public static Symbol getFTSEIndex() {
		return symbols.get("Z-LIFFE-GBP-INDEX");
	}

	public static Symbol getOMXS30Index() {
		return symbols.get("OMXS30-OMS-SEK-INDEX");
	}

	public static Symbol getCAC40Index() {
		return symbols.get("CAC40-MONEP-EUR-INDEX");
	}

	public static Symbol getMostLiquidFutureSymbol(Symbol s, DateTime date) {
		if (!s.isFuture()) {
			return s;
		}
		if (s.getCode().equals("ES")) {
			return getESFutureSymbol(s, date);
		}
		if (s.getCode().equals("OMXS30")) {
			return getOMXFutureSymbol(s, date);
		}
		if (s.getCode().equals("DAX")) {
			return getDAXFutureSymbol(s, date);
		}
		if (s.getCode().equals("ESTX50")) {
			return getESTX50FutureSymbol(s, date);
		}
		if (s.getCode().equals("Z")) {
			return getZFutureSymbol(s, date);
		}
		if (s.getCode().equals("CAC40")) {
			return getCAC40FutureSymbol(s, date);
		}
		if (s.getCode().equals("VIX")) {
			return getVIXFutureSymbol(s, date);
		}
		if (s.getCode().equals("COIL")) {
			return getCOILFutureSymbol(s, date);
		}
		if (s.getCode().equals("WTI")) {
			return getWTIFutureSymbol(s, date);
		}
		if (s.getCode().equals("GC")) {
			return getGCFutureSymbol(s, date);
		}
		if (s.getCode().equals("CL")) {
			return getCLFutureSymbol(s, date);
		}
		if (s.getExchange().equals("OMX")) {
			return getOMXFutureSymbol(s, date);
		}
		if (s.getExchange().equals("ONE")) {
			return getONEFutureSymbol(s, date);
		}
		throw new IllegalArgumentException("Unsupported future: " + s.toString());
	}

	public static Symbol getOMXS30FutureSymbol(DateTime date) {
		return getOMXFutureSymbol(null, date);
	}

	public static Symbol getOMXS30FutureSymbol(Symbol s, DateTime date) {
		return getOMXFutureSymbol(s, date);
	}

	public static Symbol getOMXFutureSymbol(DateTime date) {
		return getOMXFutureSymbol(null, date);
	}

	public static Symbol getOMXFutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = null;
		if (date.isBefore(1208383200000L)) { // 2008-04-17
			expiryDate = getMostLiquidMonthlyExpiry(date, 2, 4);
			if (expiryDate.equals(new DateTime(2007, 6, 22, 0, 0, 0, 0))) {
				expiryDate = expiryDate.minusDays(1);
			}
		} else if (date.isBefore(1293836400000L)) { // 2011-01-01
			expiryDate = getMostLiquidMonthlyExpiry(date, 2, 3);
			// Strange hack for these dates
			if (expiryDate.equals(new DateTime(2008, 6, 20, 0, 0, 0, 0))) {
				expiryDate = expiryDate.minusDays(1);
			} else if (expiryDate.equals(new DateTime(2009, 1, 16, 0, 0, 0, 0))) {
				expiryDate = expiryDate.plusDays(7);
			} else if (expiryDate.equals(new DateTime(2009, 6, 19, 0, 0, 0, 0))) {
				expiryDate = expiryDate.minusDays(1);
			} else if (expiryDate.equals(new DateTime(2010, 1, 15, 0, 0, 0, 0))) {
				expiryDate = expiryDate.plusDays(7);
			}
		} else if (date.isBefore(1371765600000L)) { // 2013-06-21
			expiryDate = getMostLiquidMonthlyExpiry(date, 2, 3);
			// Strange hack for these dates
			if (expiryDate.getMillis() == 1371765600000L) {
				expiryDate = expiryDate.minusDays(1);
			}
		} else {
			expiryDate = getMostLiquidMonthlyExpiry(date, 2, 3);
		}
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		// String code = "OMXS30" + (expiryDate.getYearOfCentury() % 10) +
		// (char)
		// (64 + expiryDate.getMonthOfYear());
		// return new Symbol(code, "OMS", "SEK", expiryDate);
		return new Symbol(s != null ? s.getCode() : "OMXS30", "OMS", "SEK", expiryDate, 100, 0.25);
	}

	public static Symbol getESFutureSymbol(DateTime date) {
		return getESFutureSymbol(null, date);
	}

	public static Symbol getESFutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = getMostLiquidQuarterlyExpiry(date, 8);
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("ES", "GLOBEX", "USD", expiryDate, 50, 0.25);
	}

	public static Symbol getDAXFutureSymbol(DateTime date) {
		return getDAXFutureSymbol(null, date);
	}

	public static Symbol getDAXFutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = getMostLiquidQuarterlyExpiry(date, 8);
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("DAX", "DTB", "EUR", getMostLiquidQuarterlyExpiry(date, 8), 25, 0.5);
	}

	public static Symbol getESTX50FutureSymbol(DateTime date) {
		return getESTX50FutureSymbol(null, date);
	}

	public static Symbol getESTX50FutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = getMostLiquidQuarterlyExpiry(date, 8);
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("ESTX50", "DTB", "EUR", getMostLiquidQuarterlyExpiry(date, 8), 10, 1.0);
	}

	public static Symbol getZFutureSymbol(DateTime date) {
		return getZFutureSymbol(null, date);
	}

	public static Symbol getZFutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = getMostLiquidQuarterlyExpiry(date, 8);
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("Z", "LIFFE", "GBP", expiryDate, 10, 0.5);
	}

	public static Symbol getCAC40FutureSymbol(DateTime date) {
		return getCAC40FutureSymbol(null, date);
	}

	public static Symbol getCAC40FutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = getMostLiquidMonthlyExpiry(date, 7, 3);
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("CAC40", "MONEP", "EUR", expiryDate, 10, 0.5);
	}

	public static Symbol getVIXFutureSymbol(DateTime date) {
		return getVIXFutureSymbol(null, date);
	}

	public static Symbol getVIXFutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = getNthFriday(date.plusMonths(1), 3).minusDays(31);
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("VIX", "CFE", "USD", expiryDate, 1000, 0.05);
	}

	public static Symbol getCOILFutureSymbol(DateTime date) {
		return getCOILFutureSymbol(null, date);
	}

	public static Symbol getCOILFutureSymbol(Symbol s, DateTime date) {
		// Get 15th day preceding the first of the contract month
		MutableDateTime expiryDate = new MutableDateTime(date);
		expiryDate.setMillisOfDay(0);
		expiryDate.setMonthOfYear(expiryDate.getMonthOfYear() + (expiryDate.getMonthOfYear() < 12 ? 1 : 0));
		expiryDate.setDayOfMonth(1);
		expiryDate.addDays(-15);
		if (expiryDate.getDayOfWeek() == DateTimeConstants.SATURDAY) {
			expiryDate.addDays(-2);
		} else if (expiryDate.getDayOfWeek() == DateTimeConstants.SUNDAY) {
			expiryDate.addDays(-3);
		} else if (expiryDate.getDayOfWeek() == DateTimeConstants.MONDAY) {
			expiryDate.addDays(-3);
		} else {
			expiryDate.addDays(-1);
		}

		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("COIL", "IPE", "USD", expiryDate.toDateTime(), 1000, 0.01);
	}

	public static Symbol getWTIFutureSymbol(DateTime date) {
		return getWTIFutureSymbol(null, date);
	}

	public static Symbol getWTIFutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = getMostLiquidMonthlyExpiry(date, 7, 3);
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		s = symbols.get(s.getFullCode());
		return new Symbol("WTI", "IPE", "USD", expiryDate, 1000, 0.01);
	}

	public static Symbol getGCFutureSymbol(DateTime date) {
		return getGCFutureSymbol(null, date);
	}

	public static Symbol getGCFutureSymbol(Symbol s, DateTime date) {
		// Get the third last business day of the delivery month.
		MutableDateTime expiryDate = new MutableDateTime(date.dayOfMonth().withMaximumValue());
		expiryDate.setMillisOfDay(0);
		int d = 3;
		while (d > 1) {
			if (isBusinessDay(expiryDate)) {
				d--;
			}
			expiryDate.addDays(-1);
		}
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("GC", "NYMEX", "USD", expiryDate.toDateTime(), 100, 0.1);
	}

	public static Symbol getCLFutureSymbol(DateTime date) {
		return getCLFutureSymbol(null, date);
	}

	public static Symbol getCLFutureSymbol(Symbol s, DateTime date) {
		// Trading in the current delivery month shall cease on the third
		// business
		// day prior to the twenty-fifth calendar day of the
		// month preceding the delivery month.
		// If the twenty-fifth calendar day of the month is a non-business day,
		// trading shall cease on the third business day prior
		// to the last business day preceding the twenty-fifth calendar day.
		// In the event that the official Exchange holiday schedule changes
		// subsequent to the listing of a Crude Oil futures,
		// the originally listed expiration date shall remain in effect. In the
		// event that the originally listed expiration day is declared a
		// holiday,
		// expiration will move to the business day immediately prior.
		MutableDateTime expiryDate = new MutableDateTime(date);
		expiryDate.setMillisOfDay(0);
		if (expiryDate.getDayOfMonth() > 25) {
			expiryDate.addMonths(1);
		}
		expiryDate.setDayOfMonth(25);
		while (!isBusinessDay(expiryDate)) {
			expiryDate.addDays(-1);
		}
		int d = 3;
		while (d > 0) {
			if (isBusinessDay(expiryDate)) {
				d--;
			}
			expiryDate.addDays(-1);
		}
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("CL", "NYMEX", "USD", expiryDate.toDateTime(), 1000, 0.01);
	}

	public static Symbol getONEFutureSymbol(DateTime date) {
		return getONEFutureSymbol(null, date);
	}

	public static Symbol getONEFutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = getMostLiquidQuarterlyExpiry(date, 8);
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol(s.getCode(), "ONE", "USD", expiryDate, 100, 0.01);
	}

	public static DateTime getMostLiquidQuarterlyExpiry() {
		return getMostLiquidQuarterlyExpiry(new DateTime(), 8);
	}

	/**
	 * Certain futures contracts (such as ES) expire on the 3rd Friday of the
	 * contract month, but the volume shifts to the next month contract on the
	 * business day preceding the 2nd Friday of the expiration month. For example,
	 * a 200606 contract had more volume than a 200609 contract on Wednesday June
	 * 7, 2006, but on Thursday, June 8th (the day preceding the 2nd Friday of the
	 * expiration), the 200609 contract had more volume than the 200606 contract.
	 * <p/>
	 * This function calculates the most liquid contract traded as of given date.
	 */
	public static DateTime getMostLiquidQuarterlyExpiry(DateTime date, int daysBeforeExpiry) {
		DateTime expiryDate = getQuarterlyExpiryDate(date);
		// Volume shifts to next month about x days before expiry
		DateTime volumeShiftDate = expiryDate.minusDays(daysBeforeExpiry);

		if (date.isBefore(volumeShiftDate)) {
			return expiryDate;
		}
		return getQuarterlyExpiryDate(new DateTime(date.getMonthOfYear() == DateTimeConstants.DECEMBER ? date.getYear() + 1 : date.getYear(),
				(date.getMonthOfYear() + 1) % 12, 1, 0, 0, 0, 0));
	}

	public static DateTime getMostLiquidOMXMonthlyExpiry(DateTime date) {
		if (date == null) {
			date = new DateTime();
		} else if (date.isBefore(new DateTime(2008, 4, 17, 0, 0, 0, 0))) {
			return getMostLiquidMonthlyExpiry(date, 8, 4);
		}
		return getMostLiquidMonthlyExpiry(date, 8, 3);
	}

	public static DateTime getMostLiquidMonthlyExpiry() {
		return getMostLiquidMonthlyExpiry(new DateTime(), 7, 3);
	}

	public static DateTime getMostLiquidMonthlyExpiry(DateTime date, int daysBeforeExpiry, int nthFriday) {
		DateTime expiryDate = getNthFriday(date, nthFriday);
		// Volume shifts to next month about x days before expiry
		DateTime volumeShiftDate = expiryDate.minusDays(daysBeforeExpiry);

		if (date.isBefore(volumeShiftDate)) {
			return expiryDate;
		}
		return getNthFriday(date.plusMonths(1), nthFriday);
	}

	public static DateTime getQuarterlyExpiryDate(DateTime date) {
		// Get third Friday
		MutableDateTime expiryDate = new MutableDateTime(date);
		expiryDate.setMillisOfDay(0);
		switch (expiryDate.getMonthOfYear()) {
		case DateTimeConstants.JANUARY:
		case DateTimeConstants.FEBRUARY:
		case DateTimeConstants.MARCH:
			expiryDate.setMonthOfYear(DateTimeConstants.MARCH);
			break;
		case DateTimeConstants.APRIL:
		case DateTimeConstants.MAY:
		case DateTimeConstants.JUNE:
			expiryDate.setMonthOfYear(DateTimeConstants.JUNE);
			break;
		case DateTimeConstants.JULY:
		case DateTimeConstants.AUGUST:
		case DateTimeConstants.SEPTEMBER:
			expiryDate.setMonthOfYear(DateTimeConstants.SEPTEMBER);
			break;
		case DateTimeConstants.OCTOBER:
		case DateTimeConstants.NOVEMBER:
		case DateTimeConstants.DECEMBER:
			expiryDate.setMonthOfYear(DateTimeConstants.DECEMBER);
			break;
		}
		expiryDate.setDayOfMonth(1);
		if (expiryDate.getDayOfWeek() > DateTimeConstants.FRIDAY) {
			expiryDate.addWeeks(3);
			expiryDate.setDayOfWeek(DateTimeConstants.FRIDAY);
		} else {
			expiryDate.addWeeks(2);
			expiryDate.setDayOfWeek(DateTimeConstants.FRIDAY);
		}
		return expiryDate.toDateTime();
	}

	private static DateTime getNthFriday(DateTime date, int nthFriday) {
		MutableDateTime expiryDate = new MutableDateTime(date);
		expiryDate.setMillisOfDay(0);
		expiryDate.setDayOfMonth(1);
		if (expiryDate.getDayOfWeek() > DateTimeConstants.FRIDAY) {
			expiryDate.addWeeks(nthFriday);
			expiryDate.setDayOfWeek(DateTimeConstants.FRIDAY);
		} else {
			expiryDate.addWeeks(nthFriday - 1);
			expiryDate.setDayOfWeek(DateTimeConstants.FRIDAY);
		}
		return expiryDate.toDateTime();
	}

	private static boolean isBusinessDay(ReadableDateTime date) {
		int dayOfWeek = date.getDayOfWeek();
		if (dayOfWeek == DateTimeConstants.SATURDAY || dayOfWeek == DateTimeConstants.SUNDAY) {
			return false;
		}
		return true;
	}
}
