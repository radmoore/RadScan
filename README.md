RadScan
================

#### Introduction
RadScan is a command line utility for performing domain achitecture-based similarity searches against UniProt.
It interfaces a webapplication which runs a reference implementation of the RADS/RAMPAGE based search. As such,
RadScan does not implement either RADS or RAMPAGE - it  provides access to the webapplication of RADS running under

http://rads.uni-muenster.de

This allows users to run batch-searches etc. Furthermore, RadScan does provides some post-processing options.

__Please note__: we are currently in the process of publishing RADS/RAMPAGE, and will keep it vague here until that
is done (besides a couple of words below). If you want to know more about RADS/RAMPAGE, get in touch with us. 

##### System requirements
RadScan is written in Java and requires Java 6.0 or higher. We have tested it on *unix-based systems. In principle,
it should run on other operating systems with a Java virtual machine. Get in touch if you encounter any problems.

##### RADS
RADS stands for __R__apid __A__lignment of __D__omain __S__trings. As the name suggests, RADS beforms a 
Needleman-Wunsch-like alignment on Domain String. Using a classical dynamic programming scheme, RADS 
returns a score representing the similarity of domain architectures (and is very swift in doing so).

##### RAMPAGE
RAMPAGE is a supplement to RADS, and perform a NW sequence alignment on similarity block obtained from the RADS
search. 

