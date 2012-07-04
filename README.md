RadScan
================

#### Introduction
RadScan is a command line utility for performing domain achitecture-based similarity searches against UniProt.
It interfaces a webapplication which runs a reference implementation of the RADS/RAMPAGE based search. As such,
RadScan does not implement either RADS or RAMPAGE - it  provides access to the webapplication of RADS running under

http://rads.uni-muenster.de

This allows users to run batch-searches etc. Furthermore, RadScan does provides some post-processing options.


##### RADS
RADS stands for __R__apid __A__lignment of __D__omain __S__trings. As the name suggests, RADS beforms a 
Needleman-Wunsch-like alignment on Domain String. Using a classical dynamic programming scheme, RADS 
returns a score representing the similarity of domain architectures (and is very swift in doing so).

##### RAMPAGE
RAMPAGE is a upplement to RADS, and perform a NW sequence alignment on similarity block obtained from the RADS
search. 

__Please note__: we are currently in the process of publishing RADS/RAMPAGE, and will keep it vague here until that
is done (besides a couple of words below). If you want to know more about RADS/RAMPAGE, get in touch with us. 

#### System requirements
RadScan is written in Java and requires Java 6.0 or higher. We have tested it on *unix-based systems. In principle,
it should run on other operating systems with a Java virtual machine. Get in touch if you encounter any problems.

##### Usage
RadScan is run from the command line. It accepts xdom, fasta and raw-sequence format as input. 
Sequence data (amino acids) is required to run RadScan in RAMPAGE mode.

###### The XDOM format
The xdom format is a simple, domain-centric, fasta-like representation of proteins.
For example, the Human ATP-binding cassette sub-family F member 2 (Q9UG63) contains three Pfam-A domains:
<pre>
>Q9UG63  623
44  257  ABC_tran
296 383  ABC_tran_2
437 545  ABC_tran   
</pre>
The first line specifies the protein ID and its length in amino acids. 
The remaining lines specify each domain (in sequence) with its repective co-ordinates and 
ID or accession number. Optionally, the ID fields can be followed by an Evalue inidcating the 
significance of the hit between the domain sequence and the defining model.

<pre>
 $ java -jar radscan.jar -h
</pre>

```java
Usage: radscan [OPTIONS] -in <query>
Rapid Alignment Domain Search - find proteins with similar architectures

 -a,--algorithm <name>               Search algorithm to run. Currently supports RADS or RAMPGAE. [Default: RADS]
 -h,--help                           Show this help
 -I,--ID-only                        Only return ID of hits (list)
 -i,--infile <file>                  Query: protein in XDOM or FASTA format
 -m,--matrix <substitution matrix>   Amino acid substitution matrix (used in RAMPAGE mode) [Default BLOSSUM62]. See
                                     ftp://ftp.ncbi.nih.gov/blast/matrices/ for a list of supported matrices
 -max,--maxHits <int>                Limit maximum number of results (top x)
 -o,--out <file>                     Outfile for results (xdom)
 -q,--quiet                          Quiet mode - surpress all output except for results (incl. score table)
    --rads_g <int>                   RADS gap penalties: Internal extension [-25]
    --rads_G <int>                   RADS gap penalties: Internal opening [-50]
    --rads_M <int>                   RADS match score [150]
    --rads_m <int>                   RADS mismatch penalty [100]
    --rads_t <int>                   RADS gap penalties: Terminal extension [-50]
    --rads_T <int>                   RADS gap penalties: Terminal opening [-100]
    --rampage_g <int>                RAMPAGE gap penalties: Internal extension [-1]
    --rampage_G <int>                RAMPAGE gap penalties: Internal opening [-10]
    --rampage_T <int>                RAMPAGE gap penalties: Terminal opening [0]
    --rampage_t <int>                RAMPAGE gap penalties: Terminal extension [0]
```

#### Misc
##### Authors
The core algorithms and original website were implemented by January Weiner. The rest of the team is:
* Erich Bornberg-Bauer (ebb@uni-muenster.de)
* Sonja Gath (s.grath@uni-muenster.de)
* Andrew D. Moore (radmoore@uni-muenster.de)
* Nicolas Terrapon (n.terrapon@uni-muenster.de)

##### How do I reference?
As previously mentioned, this is very much work in progress (we are currently working on a publication
describing RADS/RAMPAGE). Until then, please contact us before using RADS/RAMPAGE in your work.

##### Copyright
<p>
Copyright © 2011-2012
Evolutionary Bioinformatics Group 
Insitute for Evolution and Biodiversity 
University of Muenster, Germany
</p>

##### Warranty
This software is provided “as is” and without any express or implied warranties, 
including, without limitation, the implied warranties of merchantibility and fitness 
for a particular purpose.


