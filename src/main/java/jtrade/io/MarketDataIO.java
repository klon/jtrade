package jtrade.io;

import java.io.File;
import java.io.IOException;

import jtrade.Symbol;
import jtrade.marketfeed.Bar;

import org.joda.time.DateTime;

public class MarketDataIO {

	public static File createReadFile(Symbol symbol, DateTime date, File dataDir) {
		return createReadFile(symbol, date, 0, dataDir);
	}

	public static File createReadFile(Symbol symbol, DateTime date, int barSizeSeconds, File dataDir) {
		String barSize = barSizeSeconds > 0 ? String.valueOf(barSizeSeconds) : "TICK";
		String filename = new StringBuilder(symbol.getFullCode()).append('-').append(date.toString("yyyyMM")).append('-').append(barSize).toString();
		File f = new File(dataDir, filename.concat(".bin.gz"));
		if (f.exists()) {
			return f;
		}
		f = new File(dataDir, filename.concat(".bin"));
		if (f.exists()) {
			return f;
		}
		f = new File(dataDir, filename.concat(".txt.gz"));
		if (f.exists()) {
			return f;
		}
		return new File(dataDir, filename.concat(".txt"));
	}

	public static File createWriteFile(Bar bar, File dataDir) {
		return createWriteFile(bar.getSymbol(), bar.getDateTime(), bar.getBarSize().toStandardSeconds().getSeconds(), dataDir);
	}

	public static File createWriteFile(Symbol symbol, DateTime date, File dataDir) {
		return createWriteFile(symbol, date, 0, dataDir);
	}

	public static File createWriteFile(Symbol symbol, DateTime date, int barSizeSeconds, File dataDir) {
		String barSize = barSizeSeconds > 0 ? String.valueOf(barSizeSeconds) : "TICK";
		String filename = new StringBuilder(symbol.getFullCode()).append('-').append(date.toString("yyyyMM")).append('-').append(barSize).append(".txt").toString();
		return new File(dataDir, filename);
	}

	public static BarReader createBarTickReader(File file, int barSizeSeconds) throws IOException {
		return new BarTickFileReader(file, barSizeSeconds);
	}

	public static BarReader createBarReader(File file) throws IOException {
		if (file.getPath().endsWith(".bin.gz") || file.getPath().endsWith(".bin")) {
			return new BinaryBarFileReader(file);
		}
		return new CsvBarFileReader(file);
	}

	public static TickReader createTickReader(File file) throws IOException {
		return createTickReader(file, true);
	}

	public static TickReader createTickReader(File file, boolean skipMarketDepth) throws IOException {
		if (file.getPath().endsWith(".bin.gz") || file.getPath().endsWith(".bin")) {
			return new BinaryTickFileReader(file, skipMarketDepth);
		}
		return new CsvTickFileReader(file, skipMarketDepth);
	}

	public static BarWriter createBarWriter(File file, boolean append, String source) throws IOException {
		return createBarWriter(file, append, false, source);
	}

	public static BarWriter createBarWriter(File file, boolean append, boolean compress, String source) throws IOException {
		return new CsvBarFileWriter(file, append, compress, source);
	}

	public static TickWriter createTickWriter(File file, boolean append, String source) throws IOException {
		return createTickWriter(file, append, false, source);
	}

	public static TickWriter createTickWriter(File file, boolean append, boolean compress, String source) throws IOException {
		return new CsvTickFileWriter(file, append, compress, source);
	}
}
