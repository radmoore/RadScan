package info.radm.radscan;

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
	
	@SuppressWarnings("static-access")
	static Option queryProt = OptionBuilder.withArgName( "file" )
            .isRequired()
            .withDescription("Query: protein in XDOM or FASTA format")
            .hasArg()
            .withLongOpt("in")
            .create("i");
	
	@SuppressWarnings("static-access")
	static Option maxNumResults = OptionBuilder.withArgName( "int" )
            .withDescription("Limit maximum number of results (top x)")
            .hasArg()
            .withLongOpt("maxHits")
            .create("m");
	
	@SuppressWarnings("static-access")
	static Option onlyIDs = OptionBuilder
            .withDescription("Only return ID of hits (list)")
            .hasArg()
            .withLongOpt("ID-only")
            .create("I");
	
	@SuppressWarnings("static-access")
	static Option resultFile = OptionBuilder.withArgName( "file" )
            .withDescription("Outfile for results (xdom) [DEFAULT: STDOUT]")
            .hasArg()
            .withLongOpt("out")
            .create("o");
	
	@SuppressWarnings("static-access")
	static Option verbose = OptionBuilder
            .withDescription("Verbose scan")
            .withLongOpt("verbose")
            .create("v");
	
	@SuppressWarnings("static-access")
	static Option quiet = OptionBuilder
            .withDescription("Quiet mode - surpress all output except for results")
            .withLongOpt("quiet")
            .create("q");
	
	
	
	static String testFasta = "/home/radmoore/Dropbox/work/rads/RADSCAN/test/data/rads_test.fa";
	//static String testXdom = "/home/radmoore/Dropbox/work/rads/RADSCAN/test/data/rads_test.xdom";
	
	public static void main (String[] args) {
		
		Options opt = new Options();
		HelpFormatter f = new HelpFormatter();
		f.setSyntaxPrefix("Usage: ");
		
		try {
			// add options
			opt.addOption(queryProt);
			opt.addOption(resultFile);
			opt.addOption(maxNumResults);
			opt.addOption(onlyIDs);
			opt.addOption(quiet);
			opt.addOption(verbose);
			
			
            PosixParser parser = new PosixParser();
            CommandLine cl = parser.parse(opt, args, false);
			
            QueryBuilder qBuilder = new QueryBuilder();
            
            if (cl.hasOption("v")) {
            	System.out.println("SCANNING IN VERBOSE MODE");
            	qBuilder.setVerboseMode(true);
            }
            
            // set query protein
            qBuilder.setQueryProtein(cl.getOptionValue("i"));

            
			RADSQuery rQuery = qBuilder.build();
			
			RADSRunner rads = new RADSRunner(rQuery);
			rads.submit();
			
		}
		catch (MissingOptionException e) {
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
			System.exit(-1);
		}
		
		
		
	}

	
	
}
