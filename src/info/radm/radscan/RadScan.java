package info.radm.radscan;

import info.radm.pbar.ProgressBar;
import info.radm.radscan.ds.Protein;
import info.radm.radscan.utils.RadsMessenger;
import info.radm.radscan.utils.RadsWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;

public class RadScan {
	
	protected static final String VERSION = "0.3.4";
	
	public static void main (String[] args) {
		
		Options opt = new Options();
		HelpFormatter f = new HelpFormatter();
		f.setWidth(150);
		f.setSyntaxPrefix("Usage: ");
		
		ArrayList<RadsWriter> writers = new ArrayList<RadsWriter>();
		
		try {
			// add options
			buildOptions(opt);	
            PosixParser parser = new PosixParser();
            CommandLine cl = parser.parse(opt, args, false);
            
            // TODO: complete (ensure that this does not trigger MissingOptionException)
            if (cl.hasOption("h")) {
            	f.printHelp("radscan [OPTIONS] -in <query>",
        	        	"Rapid Alignment of Domain Strings - find proteins with similar architectures\n", opt, "");
        			System.exit(0);
            }
            // TODO: complete (ensure that this does not trigger MissingOptionException)
            if (cl.hasOption("version")) {
            	System.out.println("RadScan version: "+VERSION);
            	System.out.println("Java: "+System.getProperty("java.runtime.name")+" "+System.getProperty("java.runtime.version"));
            	System.exit(0);
            }
            
            // construct a query
            QueryBuilder qBuilder = new QueryBuilder();
            
            // set to quiet mode
            if (cl.hasOption("q"))
            	qBuilder.setQuietMode(true);


			if (cl.hasOption("runtime")) {
				qBuilder.setQuietMode(true);
				qBuilder.setBenchmarkMode(true);
			}

            
            // set inputfile
            qBuilder.setQueryProtein(cl.getOptionValue("i"));

            // set algorithm
            if (cl.hasOption("a")) {
            	setAlgorithm(cl, qBuilder);
            	if (qBuilder.getAlgorithm().equals("RAMPAGE"))
            		setRampageOptions(cl, qBuilder);
            	else
            		setRadsOptions(cl, qBuilder);
            }
            
            // setup writer
            RadsWriter writer = null;
            if (cl.hasOption("o")) {			
            	String outFileName = cl.getOptionValue("o");
				try {
					writer = new RadsWriter(outFileName, "XDOM Results");
				}
				catch (IOException ioe) {
					System.err.println("ERROR: could not create outfile: "+outFileName);
					System.exit(-1);
				}	
			}
            else 
            	writer = new RadsWriter();
            
            // add writer to list of used writers for later reporting
            writers.add(writer);
            
            // print some information
            if (!qBuilder.isQuiet())
            	inform(qBuilder);
            
			RADSQuery rQuery = qBuilder.build();
			RADSRunner rads = new RADSRunner(rQuery);
			RADSResults results = rads.submit();
						
			// Initiate parser
			Parser resultParser = new Parser(results);
			
			TreeSet<Protein> proteins = resultParser.parse();
			
			boolean idMode = false; 
			if (cl.hasOption("I"))
				idMode = true;
			
			boolean arrStringMode = false;
			if (cl.hasOption("arrstr"))
				arrStringMode = true;
			
			int max = -1, current = 0;
			if (cl.hasOption("max"))
				max = Integer.valueOf(cl.getOptionValue("max"));

			
			if (cl.hasOption("c")) {
				int repNo = Integer.valueOf(cl.getOptionValue("c"));
				ProgressBar pbar = new ProgressBar(proteins.size(), "Collapsing repeats");
				int i = 0;
				for (Protein p : proteins) {
					p.collapse(repNo);
					pbar.setCurrentVal(i);
					i++;
				}
				pbar.setCurrentVal(i);
			}
			
			//TODO: nicify output
			if (cl.hasOption("u")) {
				RadsWriter archWriter;
				try {
					archWriter = new RadsWriter(cl.getOptionValue("u"), "Frequency table of unique architectures");
					writers.add(archWriter);
					List<Entry<String, Integer>> uniqArchs = Protein.getUniqueArchitectures(proteins);
					for (Entry<String, Integer> e : uniqArchs)
						archWriter.writeln(e.getKey()+"\t"+e.getValue());
				} 
				catch (IOException e1) {
					e1.printStackTrace();
				}	
			}

			for (Protein p : proteins) {
				current ++;
				if (idMode)
					writer.writeln(p.getID());
				else if (arrStringMode)
					writer.writeln(p.getArrString());
				else
					writer.writeln(p.toString());
				if (current == max)
					break;
			}

				
			//TODO: nicify output
			if (cl.hasOption("tbl")) {
				
				String tblout = cl.getOptionValue("tbl");
				try {
					RadsWriter scoreWriter = new RadsWriter(tblout, "Score table");
					writers.add(scoreWriter);
					String queryID = results.getQuery().getQueryID();
					String outLine = "QUERY\tSUBJECT\tRADS";
					if (results.getQuery().isRampageRun())
						outLine += "\tRAMPAGE";
					scoreWriter.writeln(outLine);
					for (Protein p : proteins) {
						outLine = queryID+"\t"+p.getID()+"\t"+p.getRADSScore();
						if (results.getQuery().isRampageRun())
							outLine += "\t"+p.getRAMPAGEScore();
						scoreWriter.writeln(outLine);
					}
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
				
				
			}
			
			// inform of all outputfiles created (if any)
			for (RadsWriter rw : writers) {
				
				if (rw.isToFile()) {
					RadsMessenger.writeMessage(rw.getFileDescription()+
							" written to " +
							rw.getOutFilePath());
					rw.destroy();
				}
			}
			
			// RUN COMPLETE //
			if (!qBuilder.isQuiet()) {
				RadsMessenger.writeMessage("Scan complete.");
				RadsMessenger.printHR();
			}
		}
		catch (MissingOptionException e) {
			System.err.println(e.getMessage());
			f.printHelp("radscan [OPTIONS] -in <query>", 
        		"Rapid Alignment of Domain Strings - find proteins with similar architectures\n", opt, "");
			System.exit(-1);
		}
		catch (MissingArgumentException e) {
			System.err.println(e.getMessage());
			f.printHelp("radscan [OPTIONS] -in <query>", 
	        	"Rapid Alignment of Domain Strings - find proteins with similar architectures\n", opt, "");
			System.exit(-1);
		}
		catch (UnrecognizedOptionException e) {
			System.err.println(e.getMessage());
			f.printHelp("radscan [OPTIONS] -in <query>",
	        	"Rapid Alignment of Domain Strings - find proteins with similar architectures\n", opt, "");
			System.exit(-1);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param cl
	 * @param qBuilder
	 */
	private static void setAlgorithm(CommandLine cl, QueryBuilder qBuilder) throws MissingOptionException {
    	String algo = cl.getOptionValue("algorithm");
    	if (algo.equals("RAMPAGE")) {
    		if (qBuilder.getFormat() == RADSQuery.XDOM) {
    			System.err.println("ERROR: can't run RAMPAGE mode without FASTA file.");
    			System.exit(-1);
    		}
    		qBuilder.setAlgorithm(algo);
    	}
    	else if (algo.equals("RADS")) 
    		qBuilder.setAlgorithm(algo);
    	else
    		throw new MissingOptionException("Unknown algorithm. Consider RAMPAGE (DEFAULT: RADS)");

	}
	
	/**
	 * 
	 * @param cl
	 * @param qBuilder
	 */
	private static void setRampageOptions(CommandLine cl, QueryBuilder qBuilder) throws MissingOptionException {
		try {
			
    		if (cl.hasOption("m"))
    			qBuilder.setMatrix(cl.getOptionValue("m"));
			
			if (cl.hasOption("rampage_G")) {
				int I = Integer.valueOf(cl.getOptionValue("rampage_G"));
				qBuilder.setRampage_I(I);
			}
			if (cl.hasOption("rampage_g")) {
				int i = Integer.valueOf(cl.getOptionValue("rampage_g"));
				qBuilder.setRampage_i(i);
			}
			if (cl.hasOption("rampage_E")) {
				int E = Integer.valueOf(cl.getOptionValue("rampage_E"));
				qBuilder.setRampage_E(E);
			}
			if (cl.hasOption("rampage_e")) {
				int e = Integer.valueOf(cl.getOptionValue("rampage_e"));
				qBuilder.setRampage_e(e);
			}
		}
		catch (NumberFormatException nfe) {
			throw new MissingOptionException("Error: RAMPAGE gap penalty not a valid integer");
		}
	}

	/**
	 * 
	 * @param cl
	 * @param qBuilder
	 */
	private static void setRadsOptions(CommandLine cl, QueryBuilder qBuilder) throws MissingOptionException{
		try {
			if (cl.hasOption("rads_G")) {
				int G = Integer.valueOf(cl.getOptionValue("rampage_G"));
				qBuilder.setRads_G(G);
			}
			if (cl.hasOption("rads_g")) {
				int g = Integer.valueOf(cl.getOptionValue("rampage_g"));
				qBuilder.setRads_g(g);
			}
			if (cl.hasOption("rads_T")) {
				int T = Integer.valueOf(cl.getOptionValue("rampage_T"));
				qBuilder.setRads_T(T);
			}
			if (cl.hasOption("rads_t")) {
				int t = Integer.valueOf(cl.getOptionValue("rampage_t"));
				qBuilder.setRads_t(t);
			}
		}
		catch (NullPointerException nfe) {
			throw new MissingOptionException("Error: RADS gap penalty not a valid integer");
		}
	}
	
	
	/**
	 * 
	 * @param qBuilder
	 */
	private static void inform(QueryBuilder qBuilder) {
		RadsMessenger.printBanner();
		RadsMessenger.printHR();
		RadsMessenger.writeTable("INPUT FILE", qBuilder.getFileName());
		RadsMessenger.writeTable("QUERY PROTEIN", qBuilder.getQueryID());
		RadsMessenger.writeTable("DATABASE", qBuilder.getDatabase());
		RadsMessenger.writeTable("ALGORITHM", qBuilder.getAlgorithm());
		if (qBuilder.getAlgorithm().equals("RAMPAGE")) {
			RadsMessenger.writeTable("MATRIX", qBuilder.getMatrix());	
		}
		if (qBuilder.isFasta()) {
			RadsMessenger.writeTable("FORMAT", "FASTA");
			RadsMessenger.writeTable("SEQUENCE CHECKSUM", ""+qBuilder.getSeqChecksum());
		}
		else
			RadsMessenger.writeTable("FORMAT", "XDOM");
	}
	
	/**
	 * 
	 * @param opt
	 */
	private static void buildOptions(Options opt) {
		
		@SuppressWarnings("static-access")
		Option algo = OptionBuilder.withArgName("name")
				.withLongOpt( "algorithm" )
	            .withDescription("Search algorithm to run. Currently supports " +
	            		"RADS or RAMPGAE. [Default: RADS]")
	            .hasArg()
	            .create("a");
		
		@SuppressWarnings("static-access")
		Option queryProt = OptionBuilder.withArgName( "file" )
	            .isRequired()
	            .withDescription("Query: protein in XDOM or FASTA format")
	            .hasArg()
	            .withLongOpt("infile")
	            .create("i");
		
		@SuppressWarnings("static-access")
		Option maxNumResults = OptionBuilder.withArgName( "int" )
	            .withDescription("Limit maximum number of results (top x)")
	            .hasArg()
	            .withLongOpt("maxHits")
	            .create("max");
		
		@SuppressWarnings("static-access")
		Option help = OptionBuilder
				.withDescription("Show this help")
	            .withLongOpt("help")
	            .create("h");
		
		@SuppressWarnings("static-access")
		Option onlyIDs = OptionBuilder
	            .withDescription("Only return ID of hits (list)")
	            .withLongOpt("ID-only")
	            .create("I");
		
		@SuppressWarnings("static-access")
		Option resultFile = OptionBuilder.withArgName( "file" )
	            .withDescription("Outfile for results (xdom)")
	            .hasArg()
	            .withLongOpt("out")
	            .create("o");
		
		@SuppressWarnings("static-access")
		Option arrstr = OptionBuilder.withDescription("Return hits as string of domain IDs (separated by ;)")
	            .create("arrstr");
		
		@SuppressWarnings("static-access")
		Option quiet = OptionBuilder
	            .withDescription("Quiet mode - surpress all output except for results (incl. score table)")
	            .withLongOpt("quiet")
	            .create("q");
		
		@SuppressWarnings("static-access")
		Option scoreTable = OptionBuilder.withArgName("file")
	            .withDescription("Write score table to file (will ignore max option)")
	            .hasArg()
	            .withLongOpt("score-table")
	            .create("tbl");
	
		@SuppressWarnings("static-access")
		Option unique = OptionBuilder.withArgName("file")
	            .hasArg()
	            .withDescription("Return unique architecture frequency table (ignores max)")
	            .withLongOpt("unique")
	            .create("u");

		@SuppressWarnings("static-access")
		Option collapse = OptionBuilder.withArgName("int")
	            .hasArg()
	            .withDescription("Collpase domain repeats with more than <int> units")
	            .withLongOpt("collapse")
	            .create("c");
		
		@SuppressWarnings("static-access")
		Option matrix = OptionBuilder.withArgName("substitution matrix")
	            .hasArg()
	            .withDescription("Amino acid substitution matrix (used in RAMPAGE mode) " +
	            		"[Default BLOSSUM62]. See ftp://ftp.ncbi.nih.gov/blast/matrices/ " +
	            		"for a list of supported matrices")
	            .withLongOpt("matrix")
	            .create("m");
		
		opt.addOption(algo);
		opt.addOption(queryProt);
		opt.addOption(resultFile);
		opt.addOption(maxNumResults);
		opt.addOption(onlyIDs);
		opt.addOption(help);
		opt.addOption(quiet);
		opt.addOption(unique);
		opt.addOption(matrix);
		opt.addOption(arrstr);
		opt.addOption(scoreTable);
		opt.addOption(collapse);
		opt.addOption("runtime", false, "show runtime only (for benchmarking)");
		opt.addOption("version", false, "Print RadScan version and exit");
		
		// RADS and RAMPAGE options
		
		// gp_rampage_M
		@SuppressWarnings("static-access")
		Option rads_M = OptionBuilder.withLongOpt("rads_M")
				.withArgName("int")
	            .hasArg()
	            .withDescription("RADS match score [150]")
	            .create();
		opt.addOption(rads_M);
		
		// gp_rampage_m
		@SuppressWarnings("static-access")
		Option rads_m = OptionBuilder.withLongOpt("rads_m")
				.withArgName("int")
	            .hasArg()
	            .withDescription("RADS mismatch penalty [100]")
	            .create();
		opt.addOption(rads_m);
		
		//gp_rampage_G
		@SuppressWarnings("static-access")
		Option rads_G = OptionBuilder.withLongOpt("rads_G")
				.withArgName("int")
	            .hasArg()
	            .withDescription("RADS gap penalties: Internal opening [-50]")
	            .create();
		opt.addOption(rads_G);
		
		// gp_rampage_g
		@SuppressWarnings("static-access")
		Option rads_g = OptionBuilder.withLongOpt("rads_g")
				.withArgName("int")
	            .hasArg()
	            .withDescription("RADS gap penalties: Internal extension [-25]")
	            .create();
		opt.addOption(rads_g);
		
		// gp_rampage_T
		@SuppressWarnings("static-access")
		Option rads_T = OptionBuilder.withLongOpt("rads_T")
				.withArgName("int")
	            .hasArg()
	            .withDescription("RADS gap penalties: Terminal opening [-100]")
	            .create();
		opt.addOption(rads_T);
		
		// gp_rampage_t
		@SuppressWarnings("static-access")
		Option rads_t = OptionBuilder.withLongOpt("rads_t")
				.withArgName("int")
	            .hasArg()
	            .withDescription("RADS gap penalties: Terminal extension [-50]")
	            .create();
		opt.addOption(rads_t);

		// gp_rampage_I
		@SuppressWarnings("static-access")
		Option rads_I = OptionBuilder.withLongOpt("rampage_G")
				.withArgName("int")
	            .hasArg()
	            .withDescription("RAMPAGE gap penalties: Internal opening [-10]")
	            .create();
		opt.addOption(rads_I);
		
		// gp_rampage_i
		@SuppressWarnings("static-access")
		Option rads_i = OptionBuilder.withLongOpt("rampage_g")
				.withArgName("int")
	            .hasArg()
	            .withDescription("RAMPAGE gap penalties: Internal extension [-1]")
	            .create();
		opt.addOption(rads_i);
		
		// gp_rampage_E
		@SuppressWarnings("static-access")
		Option rads_E = OptionBuilder.withLongOpt("rampage_T")
				.withArgName("int")
	            .hasArg()
	            .withDescription("RAMPAGE gap penalties: Terminal opening [0]")
	            .create();
		opt.addOption(rads_E);
		
		// gp_rampage_e
		@SuppressWarnings("static-access")
		Option rads_e = OptionBuilder.withLongOpt("rampage_t")
				.withArgName("int")
	            .hasArg()
	            .withDescription("RAMPAGE gap penalties: Terminal extension [0]")
	            .create();
		opt.addOption(rads_e);
		
	}
	
}
