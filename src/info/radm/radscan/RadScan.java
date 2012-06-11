package info.radm.radscan;

import javax.swing.SwingWorker;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class RadScan {
	
	@SuppressWarnings("static-access")
	static Option queryProt = OptionBuilder.withArgName( "queryProt" )
            .withDescription("Query: protein in XDOM or FASTA format ")
            .hasArg()
            .withLongOpt("query")
            .create("q");
	
	static String testFasta = "/home/radmoore/Dropbox/work/rads/RADSCAN/test/data/rads_test.fa";
	//static String testXdom = "/home/radmoore/Dropbox/work/rads/RADSCAN/test/data/rads_test.xdom";
	
	public static void main (String[] args) {
		
		Options opt = new Options();
		HelpFormatter f = new HelpFormatter();
		f.setSyntaxPrefix("Usage: ");
		
		try {
//			opt.addOption(queryProt);
			
//            PosixParser parser = new PosixParser();
//            CommandLine cl = parser.parse(opt, args, false);
			

			//QueryBuilder qBuilder = new QueryBuilder(cl.getOptionValue("queryProt"));
			//QueryBuilder qBuilder = new QueryBuilder(testXdom);
			QueryBuilder qBuilder = new QueryBuilder(testFasta);
			RADSQuery rQuery = qBuilder.build();
		//	System.out.println("This is the query string: ");
			//System.out.println(rQuery.getQueryString());
			
			// hand over build to runner method
			RADSRunner rads = new RADSRunner(rQuery);
			rads.submit();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}

	
	
}
