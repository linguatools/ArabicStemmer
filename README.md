# Arabic Stemmer

Tokenizer and stemmer for Arabic based on [Lucene](https://lucene.apache.org)'s [UTF-8 tokenizer](https://lucene.apache.org/core/5_1_0/analyzers-common/org/apache/lucene/analysis/standard/StandardTokenizer.html) and [ArabicStemmer](https://lucene.apache.org/core/5_1_0/analyzers-common/org/apache/lucene/analysis/ar/ArabicStemmer.html). The ArabicStemmer is Lucene's implementation of the light stemmer (Light10) from the following paper:
> Leah S. Larkey, Lisa Ballesteros, and Margaret E. Connell. [Light Stemming for Arabic Information Retrieval](http://www.mtholyoke.edu/~lballest/Pubs/arab_stem05.pdf).

## Installation

You need a Java 8 Runtime Engine. For standalone use from the command line download the jar file [ArabicStemmer-1.0.jar](https://github.com/linguatools/ArabicStemmer/blob/master/ArabicStemmer-1.0.jar). It includes all dependencies.
Then type
```
java -jar ArabicStemmer-1.0.jar
```
to see the available command line options.

## Command line usage

All input files must be UTF-8 encoded plain text files. They may contain lines with a single XML-style tag, for instance:
```
<s>
كأس العالم لكرة القدم 2014 هي الدورة العشرون من بطولات كأس العالم لكرة القدم، أقيمت في قارة أمريكا الجنوبية بعد أن حدد الاتحاد الدولي لكرة القدم نظام التناوب بين
 القارات وحدارة أمريكا الجنوبية لتقام فيها البطولة، ولم يطلب أي بلد الاستضافة سوى البرازيل، التي تقدمت بالملف في 31 يوليو 2007.
</s>
```
These tags (in the example ```<s>``` and ```</s>```) are copied to the output, unless you specify the option ```-ignore-xml```.

You have to specify if you want to tokenize, normalize or stem the input by using one of the commands ```tok```, ```norm```, or ```stem```. Tokenization applies Lucene's [StandardTokenizer](https://lucene.apache.org/core/5_1_0/analyzers-common/org/apache/lucene/analysis/standard/StandardTokenizer.html), normalization additionally calls [ArabicNormalizer](https://lucene.apache.org/core/5_1_0/analyzers-common/org/apache/lucene/analysis/ar/ArabicNormalizer.html), and stemming uses [ArabicAnalyzer](https://lucene.apache.org/core/5_1_0/analyzers-common/org/apache/lucene/analysis/ar/ArabicAnalyzer.html) (which performs tokenization, normalization, and stemming). 
You can also normalize and stem an already tokenized input file with the command ```pretok```.

Output format can be either ```txt``` or ```tsv```:
* ```txt```: outputs only the (possibly normalized or stemmed) tokens, separated by a single white space.
* ```tsv```: outputs one token per line with three columns separated by a tab:
  1. token content as in the input (token-span substring of input)
  2. ```true``` or ```false``` specifying if the token is an indexable Term as defined by Lucene. Indexable terms are tokens that contain letters or numbers. 
  3. depending on the command: same as i. for ```tok```, normalized token for ```norm```, stemmed token for ```stem``` or ```pretok```.

If you want to build a [DISCO](http://www.linguatools.de/disco/disco_en.html) word space with [DISCOBuilder](http://www.linguatools.de/disco/disco-builder.html) use the following options to preprocess your corpus files:
```
java -jar ArabicStemmer-1.0.jar stem -of tsv -i <INPUT_FILE> -o <OUTPUT_FILE>
```
This will produce an output file in DISCOBuilder's ```LEMMATIZED``` input format (using the stems as lemmas). Then, in the ```disco.config``` configuration file for DISCOBuilder set the parameters as follows:
```
inputFileFormat=LEMMATIZED
lemma=false
lemmaFeatures=true
```

## Java API

You can also include the ArabicStemmer into your Java project. To tokenize or stem a string use the method [Main.analyseString](https://github.com/linguatools/ArabicStemmer/blob/master/src/org/linguatools/stem/ar/Main.java).
The package uses version 5.1.0 of Lucene.
See [API docs](https://linguatools.github.io/ArabicStemmer/) (javadoc).

## License
Apache License 2.0
