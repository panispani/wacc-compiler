// Generated from ./WACCLexer.g4 by ANTLR 4.5.3
package antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class WACCLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		PLUS=1, MINUS=2, STAR=3, DIV=4, MOD=5, EQUALS=6, GREATER_THAN=7, GREATER_THAN_EQ=8, 
		LESS_THAN=9, LESS_THAN_EQ=10, EQUAL=11, NOT_EQUAL=12, BANG=13, AND=14, 
		OR=15, TRUE=16, FALSE=17, INT=18, BOOL=19, CHAR=20, STRING=21, PAIR=22, 
		NEWPAIR=23, FST=24, SND=25, NULL=26, L_BRACKET=27, R_BRACKET=28, L_SQ_BRACKET=29, 
		R_SQ_BRACKET=30, NOP=31, READ=32, FREE=33, RETURN=34, EXIT=35, PRINT=36, 
		PRINTLN=37, IF=38, THEN=39, ELSE=40, FI=41, WHILE=42, DO=43, DONE=44, 
		BEGIN=45, END=46, COMMA=47, SEMICOLON=48, HASH=49, LEN=50, ORD=51, CHR=52, 
		SINGLE_QUOTE=53, DOUBLE_QUOTE=54, UNDERSCORE=55, BACKSLASH=56, ESCAPED_CHAR=57, 
		IS=58, CALL=59, WS=60, NUMBER=61, IDENT=62, COMMENT=63, STR_LITER=64, 
		CHAR_LITER=65, CHAR_NOT_EOL=66, CHAR_NO_QUOTES_BACKSLASH=67;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"PLUS", "MINUS", "STAR", "DIV", "MOD", "EQUALS", "GREATER_THAN", "GREATER_THAN_EQ", 
		"LESS_THAN", "LESS_THAN_EQ", "EQUAL", "NOT_EQUAL", "BANG", "AND", "OR", 
		"TRUE", "FALSE", "INT", "BOOL", "CHAR", "STRING", "PAIR", "NEWPAIR", "FST", 
		"SND", "NULL", "L_BRACKET", "R_BRACKET", "L_SQ_BRACKET", "R_SQ_BRACKET", 
		"NOP", "READ", "FREE", "RETURN", "EXIT", "PRINT", "PRINTLN", "IF", "THEN", 
		"ELSE", "FI", "WHILE", "DO", "DONE", "BEGIN", "END", "COMMA", "SEMICOLON", 
		"HASH", "LEN", "ORD", "CHR", "SINGLE_QUOTE", "DOUBLE_QUOTE", "UNDERSCORE", 
		"BACKSLASH", "ESCAPED_CHAR", "IS", "CALL", "LOWERCASE", "UPPERCASE", "DIGITS", 
		"ID_START", "ID_CHAR", "STR_CHARACTER", "WS", "NUMBER", "IDENT", "COMMENT", 
		"STR_LITER", "CHAR_LITER", "CHAR_NOT_EOL", "CHAR_NO_QUOTES_BACKSLASH"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'+'", "'-'", "'*'", "'/'", "'%'", "'='", "'>'", "'>='", "'<'", 
		"'<='", "'=='", "'!='", "'!'", "'&&'", "'||'", "'true'", "'false'", "'int'", 
		"'bool'", "'char'", "'string'", "'pair'", "'newpair'", "'fst'", "'snd'", 
		"'null'", "'('", "')'", "'['", "']'", "'skip'", "'read'", "'free'", "'return'", 
		"'exit'", "'print'", "'println'", "'if'", "'then'", "'else'", "'fi'", 
		"'while'", "'do'", "'done'", "'begin'", "'end'", "','", "';'", "'#'", 
		"'len'", "'ord'", "'chr'", "'''", "'\"'", "'_'", "'\\'", null, "'is'", 
		"'call'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "PLUS", "MINUS", "STAR", "DIV", "MOD", "EQUALS", "GREATER_THAN", 
		"GREATER_THAN_EQ", "LESS_THAN", "LESS_THAN_EQ", "EQUAL", "NOT_EQUAL", 
		"BANG", "AND", "OR", "TRUE", "FALSE", "INT", "BOOL", "CHAR", "STRING", 
		"PAIR", "NEWPAIR", "FST", "SND", "NULL", "L_BRACKET", "R_BRACKET", "L_SQ_BRACKET", 
		"R_SQ_BRACKET", "NOP", "READ", "FREE", "RETURN", "EXIT", "PRINT", "PRINTLN", 
		"IF", "THEN", "ELSE", "FI", "WHILE", "DO", "DONE", "BEGIN", "END", "COMMA", 
		"SEMICOLON", "HASH", "LEN", "ORD", "CHR", "SINGLE_QUOTE", "DOUBLE_QUOTE", 
		"UNDERSCORE", "BACKSLASH", "ESCAPED_CHAR", "IS", "CALL", "WS", "NUMBER", 
		"IDENT", "COMMENT", "STR_LITER", "CHAR_LITER", "CHAR_NOT_EOL", "CHAR_NO_QUOTES_BACKSLASH"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public WACCLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "WACCLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2E\u01b5\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t"+
		"\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\17"+
		"\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25"+
		"\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27"+
		"\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\32\3\32"+
		"\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37"+
		"\3\37\3 \3 \3 \3 \3 \3!\3!\3!\3!\3!\3\"\3\"\3\"\3\"\3\"\3#\3#\3#\3#\3"+
		"#\3#\3#\3$\3$\3$\3$\3$\3%\3%\3%\3%\3%\3%\3&\3&\3&\3&\3&\3&\3&\3&\3\'\3"+
		"\'\3\'\3(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3*\3*\3*\3+\3+\3+\3+\3+\3+\3,\3,"+
		"\3,\3-\3-\3-\3-\3-\3.\3.\3.\3.\3.\3.\3/\3/\3/\3/\3\60\3\60\3\61\3\61\3"+
		"\62\3\62\3\63\3\63\3\63\3\63\3\64\3\64\3\64\3\64\3\65\3\65\3\65\3\65\3"+
		"\66\3\66\3\67\3\67\38\38\39\39\3:\3:\3:\3;\3;\3;\3<\3<\3<\3<\3<\3=\3="+
		"\3>\3>\3?\3?\3@\3@\3@\5@\u017b\n@\3A\3A\3A\3A\5A\u0181\nA\3B\3B\5B\u0185"+
		"\nB\3C\6C\u0188\nC\rC\16C\u0189\3C\3C\3D\6D\u018f\nD\rD\16D\u0190\3E\3"+
		"E\7E\u0195\nE\fE\16E\u0198\13E\3F\3F\7F\u019c\nF\fF\16F\u019f\13F\3F\3"+
		"F\3F\3F\3G\3G\7G\u01a7\nG\fG\16G\u01aa\13G\3G\3G\3H\3H\3H\3H\3I\3I\3J"+
		"\3J\3\u019d\2K\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16"+
		"\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34"+
		"\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g"+
		"\65i\66k\67m8o9q:s;u<w=y\2{\2}\2\177\2\u0081\2\u0083\2\u0085>\u0087?\u0089"+
		"@\u008bA\u008dB\u008fC\u0091D\u0093E\3\2\t\r\2\"\"$$))\62\62^^ddhhppt"+
		"tvv~~\3\2c|\3\2C\\\3\2\62;\5\2\13\f\17\17\"\"\4\2\f\f\u0080\u0080\5\2"+
		"$$))^^\u01b9\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2"+
		"\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2"+
		"\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2"+
		"\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2"+
		"\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3"+
		"\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2"+
		"\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2"+
		"S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3"+
		"\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2"+
		"\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2"+
		"\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d"+
		"\3\2\2\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093\3\2\2\2\3\u0095\3\2\2"+
		"\2\5\u0097\3\2\2\2\7\u0099\3\2\2\2\t\u009b\3\2\2\2\13\u009d\3\2\2\2\r"+
		"\u009f\3\2\2\2\17\u00a1\3\2\2\2\21\u00a3\3\2\2\2\23\u00a6\3\2\2\2\25\u00a8"+
		"\3\2\2\2\27\u00ab\3\2\2\2\31\u00ae\3\2\2\2\33\u00b1\3\2\2\2\35\u00b3\3"+
		"\2\2\2\37\u00b6\3\2\2\2!\u00b9\3\2\2\2#\u00be\3\2\2\2%\u00c4\3\2\2\2\'"+
		"\u00c8\3\2\2\2)\u00cd\3\2\2\2+\u00d2\3\2\2\2-\u00d9\3\2\2\2/\u00de\3\2"+
		"\2\2\61\u00e6\3\2\2\2\63\u00ea\3\2\2\2\65\u00ee\3\2\2\2\67\u00f3\3\2\2"+
		"\29\u00f5\3\2\2\2;\u00f7\3\2\2\2=\u00f9\3\2\2\2?\u00fb\3\2\2\2A\u0100"+
		"\3\2\2\2C\u0105\3\2\2\2E\u010a\3\2\2\2G\u0111\3\2\2\2I\u0116\3\2\2\2K"+
		"\u011c\3\2\2\2M\u0124\3\2\2\2O\u0127\3\2\2\2Q\u012c\3\2\2\2S\u0131\3\2"+
		"\2\2U\u0134\3\2\2\2W\u013a\3\2\2\2Y\u013d\3\2\2\2[\u0142\3\2\2\2]\u0148"+
		"\3\2\2\2_\u014c\3\2\2\2a\u014e\3\2\2\2c\u0150\3\2\2\2e\u0152\3\2\2\2g"+
		"\u0156\3\2\2\2i\u015a\3\2\2\2k\u015e\3\2\2\2m\u0160\3\2\2\2o\u0162\3\2"+
		"\2\2q\u0164\3\2\2\2s\u0166\3\2\2\2u\u0169\3\2\2\2w\u016c\3\2\2\2y\u0171"+
		"\3\2\2\2{\u0173\3\2\2\2}\u0175\3\2\2\2\177\u017a\3\2\2\2\u0081\u0180\3"+
		"\2\2\2\u0083\u0184\3\2\2\2\u0085\u0187\3\2\2\2\u0087\u018e\3\2\2\2\u0089"+
		"\u0192\3\2\2\2\u008b\u0199\3\2\2\2\u008d\u01a4\3\2\2\2\u008f\u01ad\3\2"+
		"\2\2\u0091\u01b1\3\2\2\2\u0093\u01b3\3\2\2\2\u0095\u0096\7-\2\2\u0096"+
		"\4\3\2\2\2\u0097\u0098\7/\2\2\u0098\6\3\2\2\2\u0099\u009a\7,\2\2\u009a"+
		"\b\3\2\2\2\u009b\u009c\7\61\2\2\u009c\n\3\2\2\2\u009d\u009e\7\'\2\2\u009e"+
		"\f\3\2\2\2\u009f\u00a0\7?\2\2\u00a0\16\3\2\2\2\u00a1\u00a2\7@\2\2\u00a2"+
		"\20\3\2\2\2\u00a3\u00a4\7@\2\2\u00a4\u00a5\7?\2\2\u00a5\22\3\2\2\2\u00a6"+
		"\u00a7\7>\2\2\u00a7\24\3\2\2\2\u00a8\u00a9\7>\2\2\u00a9\u00aa\7?\2\2\u00aa"+
		"\26\3\2\2\2\u00ab\u00ac\7?\2\2\u00ac\u00ad\7?\2\2\u00ad\30\3\2\2\2\u00ae"+
		"\u00af\7#\2\2\u00af\u00b0\7?\2\2\u00b0\32\3\2\2\2\u00b1\u00b2\7#\2\2\u00b2"+
		"\34\3\2\2\2\u00b3\u00b4\7(\2\2\u00b4\u00b5\7(\2\2\u00b5\36\3\2\2\2\u00b6"+
		"\u00b7\7~\2\2\u00b7\u00b8\7~\2\2\u00b8 \3\2\2\2\u00b9\u00ba\7v\2\2\u00ba"+
		"\u00bb\7t\2\2\u00bb\u00bc\7w\2\2\u00bc\u00bd\7g\2\2\u00bd\"\3\2\2\2\u00be"+
		"\u00bf\7h\2\2\u00bf\u00c0\7c\2\2\u00c0\u00c1\7n\2\2\u00c1\u00c2\7u\2\2"+
		"\u00c2\u00c3\7g\2\2\u00c3$\3\2\2\2\u00c4\u00c5\7k\2\2\u00c5\u00c6\7p\2"+
		"\2\u00c6\u00c7\7v\2\2\u00c7&\3\2\2\2\u00c8\u00c9\7d\2\2\u00c9\u00ca\7"+
		"q\2\2\u00ca\u00cb\7q\2\2\u00cb\u00cc\7n\2\2\u00cc(\3\2\2\2\u00cd\u00ce"+
		"\7e\2\2\u00ce\u00cf\7j\2\2\u00cf\u00d0\7c\2\2\u00d0\u00d1\7t\2\2\u00d1"+
		"*\3\2\2\2\u00d2\u00d3\7u\2\2\u00d3\u00d4\7v\2\2\u00d4\u00d5\7t\2\2\u00d5"+
		"\u00d6\7k\2\2\u00d6\u00d7\7p\2\2\u00d7\u00d8\7i\2\2\u00d8,\3\2\2\2\u00d9"+
		"\u00da\7r\2\2\u00da\u00db\7c\2\2\u00db\u00dc\7k\2\2\u00dc\u00dd\7t\2\2"+
		"\u00dd.\3\2\2\2\u00de\u00df\7p\2\2\u00df\u00e0\7g\2\2\u00e0\u00e1\7y\2"+
		"\2\u00e1\u00e2\7r\2\2\u00e2\u00e3\7c\2\2\u00e3\u00e4\7k\2\2\u00e4\u00e5"+
		"\7t\2\2\u00e5\60\3\2\2\2\u00e6\u00e7\7h\2\2\u00e7\u00e8\7u\2\2\u00e8\u00e9"+
		"\7v\2\2\u00e9\62\3\2\2\2\u00ea\u00eb\7u\2\2\u00eb\u00ec\7p\2\2\u00ec\u00ed"+
		"\7f\2\2\u00ed\64\3\2\2\2\u00ee\u00ef\7p\2\2\u00ef\u00f0\7w\2\2\u00f0\u00f1"+
		"\7n\2\2\u00f1\u00f2\7n\2\2\u00f2\66\3\2\2\2\u00f3\u00f4\7*\2\2\u00f48"+
		"\3\2\2\2\u00f5\u00f6\7+\2\2\u00f6:\3\2\2\2\u00f7\u00f8\7]\2\2\u00f8<\3"+
		"\2\2\2\u00f9\u00fa\7_\2\2\u00fa>\3\2\2\2\u00fb\u00fc\7u\2\2\u00fc\u00fd"+
		"\7m\2\2\u00fd\u00fe\7k\2\2\u00fe\u00ff\7r\2\2\u00ff@\3\2\2\2\u0100\u0101"+
		"\7t\2\2\u0101\u0102\7g\2\2\u0102\u0103\7c\2\2\u0103\u0104\7f\2\2\u0104"+
		"B\3\2\2\2\u0105\u0106\7h\2\2\u0106\u0107\7t\2\2\u0107\u0108\7g\2\2\u0108"+
		"\u0109\7g\2\2\u0109D\3\2\2\2\u010a\u010b\7t\2\2\u010b\u010c\7g\2\2\u010c"+
		"\u010d\7v\2\2\u010d\u010e\7w\2\2\u010e\u010f\7t\2\2\u010f\u0110\7p\2\2"+
		"\u0110F\3\2\2\2\u0111\u0112\7g\2\2\u0112\u0113\7z\2\2\u0113\u0114\7k\2"+
		"\2\u0114\u0115\7v\2\2\u0115H\3\2\2\2\u0116\u0117\7r\2\2\u0117\u0118\7"+
		"t\2\2\u0118\u0119\7k\2\2\u0119\u011a\7p\2\2\u011a\u011b\7v\2\2\u011bJ"+
		"\3\2\2\2\u011c\u011d\7r\2\2\u011d\u011e\7t\2\2\u011e\u011f\7k\2\2\u011f"+
		"\u0120\7p\2\2\u0120\u0121\7v\2\2\u0121\u0122\7n\2\2\u0122\u0123\7p\2\2"+
		"\u0123L\3\2\2\2\u0124\u0125\7k\2\2\u0125\u0126\7h\2\2\u0126N\3\2\2\2\u0127"+
		"\u0128\7v\2\2\u0128\u0129\7j\2\2\u0129\u012a\7g\2\2\u012a\u012b\7p\2\2"+
		"\u012bP\3\2\2\2\u012c\u012d\7g\2\2\u012d\u012e\7n\2\2\u012e\u012f\7u\2"+
		"\2\u012f\u0130\7g\2\2\u0130R\3\2\2\2\u0131\u0132\7h\2\2\u0132\u0133\7"+
		"k\2\2\u0133T\3\2\2\2\u0134\u0135\7y\2\2\u0135\u0136\7j\2\2\u0136\u0137"+
		"\7k\2\2\u0137\u0138\7n\2\2\u0138\u0139\7g\2\2\u0139V\3\2\2\2\u013a\u013b"+
		"\7f\2\2\u013b\u013c\7q\2\2\u013cX\3\2\2\2\u013d\u013e\7f\2\2\u013e\u013f"+
		"\7q\2\2\u013f\u0140\7p\2\2\u0140\u0141\7g\2\2\u0141Z\3\2\2\2\u0142\u0143"+
		"\7d\2\2\u0143\u0144\7g\2\2\u0144\u0145\7i\2\2\u0145\u0146\7k\2\2\u0146"+
		"\u0147\7p\2\2\u0147\\\3\2\2\2\u0148\u0149\7g\2\2\u0149\u014a\7p\2\2\u014a"+
		"\u014b\7f\2\2\u014b^\3\2\2\2\u014c\u014d\7.\2\2\u014d`\3\2\2\2\u014e\u014f"+
		"\7=\2\2\u014fb\3\2\2\2\u0150\u0151\7%\2\2\u0151d\3\2\2\2\u0152\u0153\7"+
		"n\2\2\u0153\u0154\7g\2\2\u0154\u0155\7p\2\2\u0155f\3\2\2\2\u0156\u0157"+
		"\7q\2\2\u0157\u0158\7t\2\2\u0158\u0159\7f\2\2\u0159h\3\2\2\2\u015a\u015b"+
		"\7e\2\2\u015b\u015c\7j\2\2\u015c\u015d\7t\2\2\u015dj\3\2\2\2\u015e\u015f"+
		"\7)\2\2\u015fl\3\2\2\2\u0160\u0161\7$\2\2\u0161n\3\2\2\2\u0162\u0163\7"+
		"a\2\2\u0163p\3\2\2\2\u0164\u0165\7^\2\2\u0165r\3\2\2\2\u0166\u0167\7^"+
		"\2\2\u0167\u0168\t\2\2\2\u0168t\3\2\2\2\u0169\u016a\7k\2\2\u016a\u016b"+
		"\7u\2\2\u016bv\3\2\2\2\u016c\u016d\7e\2\2\u016d\u016e\7c\2\2\u016e\u016f"+
		"\7n\2\2\u016f\u0170\7n\2\2\u0170x\3\2\2\2\u0171\u0172\t\3\2\2\u0172z\3"+
		"\2\2\2\u0173\u0174\t\4\2\2\u0174|\3\2\2\2\u0175\u0176\t\5\2\2\u0176~\3"+
		"\2\2\2\u0177\u017b\5o8\2\u0178\u017b\5y=\2\u0179\u017b\5{>\2\u017a\u0177"+
		"\3\2\2\2\u017a\u0178\3\2\2\2\u017a\u0179\3\2\2\2\u017b\u0080\3\2\2\2\u017c"+
		"\u0181\5o8\2\u017d\u0181\5y=\2\u017e\u0181\5{>\2\u017f\u0181\5}?\2\u0180"+
		"\u017c\3\2\2\2\u0180\u017d\3\2\2\2\u0180\u017e\3\2\2\2\u0180\u017f\3\2"+
		"\2\2\u0181\u0082\3\2\2\2\u0182\u0185\5\u0093J\2\u0183\u0185\5s:\2\u0184"+
		"\u0182\3\2\2\2\u0184\u0183\3\2\2\2\u0185\u0084\3\2\2\2\u0186\u0188\t\6"+
		"\2\2\u0187\u0186\3\2\2\2\u0188\u0189\3\2\2\2\u0189\u0187\3\2\2\2\u0189"+
		"\u018a\3\2\2\2\u018a\u018b\3\2\2\2\u018b\u018c\bC\2\2\u018c\u0086\3\2"+
		"\2\2\u018d\u018f\5}?\2\u018e\u018d\3\2\2\2\u018f\u0190\3\2\2\2\u0190\u018e"+
		"\3\2\2\2\u0190\u0191\3\2\2\2\u0191\u0088\3\2\2\2\u0192\u0196\5\177@\2"+
		"\u0193\u0195\5\u0081A\2\u0194\u0193\3\2\2\2\u0195\u0198\3\2\2\2\u0196"+
		"\u0194\3\2\2\2\u0196\u0197\3\2\2\2\u0197\u008a\3\2\2\2\u0198\u0196\3\2"+
		"\2\2\u0199\u019d\7%\2\2\u019a\u019c\13\2\2\2\u019b\u019a\3\2\2\2\u019c"+
		"\u019f\3\2\2\2\u019d\u019e\3\2\2\2\u019d\u019b\3\2\2\2\u019e\u01a0\3\2"+
		"\2\2\u019f\u019d\3\2\2\2\u01a0\u01a1\7\f\2\2\u01a1\u01a2\3\2\2\2\u01a2"+
		"\u01a3\bF\2\2\u01a3\u008c\3\2\2\2\u01a4\u01a8\5m\67\2\u01a5\u01a7\5\u0083"+
		"B\2\u01a6\u01a5\3\2\2\2\u01a7\u01aa\3\2\2\2\u01a8\u01a6\3\2\2\2\u01a8"+
		"\u01a9\3\2\2\2\u01a9\u01ab\3\2\2\2\u01aa\u01a8\3\2\2\2\u01ab\u01ac\5m"+
		"\67\2\u01ac\u008e\3\2\2\2\u01ad\u01ae\5k\66\2\u01ae\u01af\5\u0083B\2\u01af"+
		"\u01b0\5k\66\2\u01b0\u0090\3\2\2\2\u01b1\u01b2\t\7\2\2\u01b2\u0092\3\2"+
		"\2\2\u01b3\u01b4\n\b\2\2\u01b4\u0094\3\2\2\2\13\2\u017a\u0180\u0184\u0189"+
		"\u0190\u0196\u019d\u01a8\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}