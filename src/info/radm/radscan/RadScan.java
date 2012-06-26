package info.radm.radscan;

import info.radm.radscan.ds.Protein;
import info.radm.radscan.utils.MutableInt;
import info.radm.radscan.utils.ProgressBar;
import info.radm.radscan.utils.RadsMessenger;
import info.radm.radscan.utils.RadsWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

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
            
            // construct a query
            QueryBuilder qBuilder = new QueryBuilder();
            
            // set to quiet mode
            if (cl.hasOption("q"))
            	qBuilder.setQuietMode(true);
            
            // set database
//            if (cl.hasOption("d"))
//            	setDatabase(cl, qBuilder);
            	            
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
            
            // add writer to list of used writers for
            // later reporting
            writers.add(writer);
            
            // print some information
            if (!qBuilder.isQuiet())
            	inform(qBuilder);
            
			RADSQuery rQuery = qBuilder.build();
			RADSRunner rads = new RADSRunner(rQuery);
			// SUBMIT //
			RADSResults results = rads.submit();
						
			// Initiate parser
			Parser resultParser = new Parser(results);
			int max;
			
			if (cl.hasOption("m")) {
				max = Integer.valueOf(cl.getOptionValue("m"));
				resultParser.setMaxHits(max);
			}
			
			if (cl.hasOption("I"))
				resultParser.setIDonlyMode(true);

			// no post-processing needed
			if (!cl.hasOption("a")) {
				resultParser.parse(writer);
			}
			else {
				ArrayList<Protein> proteins = resultParser.getProteins();
				
				ProgressBar pBar = new ProgressBar(proteins.size(), "Writing search results");
				pBar.setProgressMode(ProgressBar.PROGRESSABLE_MODE, true, true);
				
				// first write (regular) results
				int i = 1;
				for (Protein p: proteins) {
					pBar.setVal(i);
					writer.writeln(p.toString());
					i++;
				}
				// create and write architecture freq file 
				constructArchitectureFreq(proteins, cl, writers);
				
		
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
			RadsMessenger.writeMessage("Scan complete.");
			RadsMessenger.printHR();
		}
		catch (MissingOptionException e) {
			System.err.println(e.getMessage());
			f.printHelp("radscan [OPTIONS] -in <query>", 
        		"Rapid Alignment Domain Search - find proteins with similar architectures\n", opt, "");
			System.exit(-1);
		}
		catch (MissingArgumentException e) {
			System.err.println(e.getMessage());
			f.printHelp("radscan [OPTIONS] -in <query>", 
	        	"Rapid Alignment Domain Search - find proteins with similar architectures\n", opt, "");
			System.exit(-1);
		}
		catch (UnrecognizedOptionException e) {
			System.err.println(e.getMessage());
			f.printHelp("radscan [OPTIONS] -in <query>",
	        	"Rapid Alignment Domain Search - find proteins with similar architectures\n", opt, "");
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
	 * @throws MissingOptionException
	 */
	private static void setDatabase(CommandLine cl, QueryBuilder qBuilder) throws MissingOptionException{
	    	String requestedDatabase = cl.getOptionValue("d");
	    	if (! (requestedDatabase.equals("swisspfam")) ||
	    			(requestedDatabase.equals("simap")) ) {
	    	
	    		throw new MissingOptionException("Unknown DATABASE request. Use swisspfam or simap");
	    	}
	    	qBuilder.setDatabase(cl.getOptionValue("d"));
	}
	
	
	/**
	 * 
	 * @param cl
	 * @param qBuilder
	 */
	private static void setRampageOptions(CommandLine cl, QueryBuilder qBuilder) throws MissingOptionException {
		try {
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
		if (qBuilder.isFasta()) {
			RadsMessenger.writeTable("FORMAT", "FASTA");
			RadsMessenger.writeTable("SEQUENCE CHECKSUM", ""+qBuilder.getSeqChecksum());
		}
		else
			RadsMessenger.writeTable("FORMAT", "XDOM");
	}
	
	/**
	 * 
	 * @param proteins
	 * @param cl
	 * @param writers
	 */
	private static void constructArchitectureFreq(ArrayList<Protein> proteins, CommandLine cl, ArrayList<RadsWriter> writers) { 
		
		// now create Architecture Freq table
		RadsWriter archWriter = null;
		try {
			archWriter = new RadsWriter(cl.getOptionValue("a"), "Unique architecture frequencies");
		} catch (IOException ioe) {
			System.err.println("ERROR: could not create/write to "+cl.getOptionValue("a"));
			System.exit(-1);
		}
		
		HashMap<String, MutableInt> archFreq = new HashMap<String, MutableInt>();
		for (Protein p: proteins) {
			MutableInt freq = archFreq.get(p.architecture());
			if (freq == null) {
				MutableInt mint = new MutableInt();
				archFreq.put(p.architecture(), mint);
			}
			else
				freq.inc();
		}
		ProgressBar pBar = new ProgressBar(archFreq.size(), "Writing unique architectures");
		pBar.setProgressMode(ProgressBar.PROGRESSABLE_MODE, false, true);
		archWriter.writeln( "# FREQUENCY, ARCHITECTURE");
		int i = 1;
		for (Entry<String, MutableInt> e: archFreq.entrySet()) {
			pBar.setVal(i);
			MutableInt mint = e.getValue();
			archWriter.writeln( ""+mint.get()+", "+(String) e.getKey());
			i++;
		}
		pBar.finish(true);
		writers.add(archWriter);
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
	            .create("m");
		
		@SuppressWarnings("static-access")
		Option onlyIDs = OptionBuilder
	            .withDescription("Only return ID of hits (list). This option takes " +
	            		"presedence over option s")
	            .withLongOpt("ID-only")
	            .create("I");
		
		@SuppressWarnings("static-access")
		Option resultFile = OptionBuilder.withArgName( "file" )
	            .withDescription("Outfile for results (xdom)")
	            .hasArg()
	            .withLongOpt("out")
	            .create("o");
		
		@SuppressWarnings("static-access")
		Option outFile = OptionBuilder.withArgName("file")
	            .withDescription("Write results to file (DEFAULT: STDOUT)")
	            .withLongOpt("outfile")
	            .create("out");
		
		@SuppressWarnings("static-access")
		Option quiet = OptionBuilder
	            .withDescription("Quiet mode - surpress all output except for results")
	            .withLongOpt("quiet")
	            .create("q");
		
		@SuppressWarnings("static-access")
		Option database = OptionBuilder.withArgName("dbname")
	            .withDescription("RADS database to scan against " +
	            		"[simap or swisspfam, DEFAULT: swisspfam]")
	            .hasArg()
	            .withLongOpt("database")
	            .create("d");
		
		@SuppressWarnings("static-access")
		Option arch = OptionBuilder.withArgName("file")
	            .hasArg()
	            .withDescription("Post-processing: return architecture frequency table.")
	            .withLongOpt("architectures")
	            .create("arch");
		
		opt.addOption(algo);
		opt.addOption(queryProt);
		opt.addOption(resultFile);
		opt.addOption(maxNumResults);
		opt.addOption(onlyIDs);
		//opt.addOption(database);
		opt.addOption(quiet);
		opt.addOption(arch);
		opt.addOption("runtime", false, "show runtime only (for benchmarking)");
		
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
