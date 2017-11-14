/*******************************************************************************
 *   Copyright (C) 2017 Peter Kolb
 *   peter.kolb@linguatools.org
 *
 *   Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *   use this file except in compliance with the License. You may obtain a copy
 *   of the License at 
 *   
 *        http://www.apache.org/licenses/LICENSE-2.0 
 *
 *   Unless required by applicable law or agreed to in writing, software 
 *   distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *   WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 *   License for the specific language governing permissions and limitations
 *   under the License.
 *
 ******************************************************************************/
package org.linguatools.stem.ar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.CharArraySet;

/**
 *
 * @author peterkolb
 */
public class Main {
    
    private static void usage(){
        System.out.println("command:");
        System.out.println("    tok         only tokenize");
        System.out.println("    norm        tokenize and normalize");
        System.out.println("    stem        tokenize, normalize, and stem");
        System.out.println("    pretok      normalize and stem pre-tokenized input");
        System.out.println("-of txt|tsv     output format");
        System.out.println("    txt: output (plain or normalized or stemmed) tokens");
        System.out.println("    tsv: output one token per line with 3 columns separated by tabs:");
        System.out.println("         plain token, isTerm, plain or normalized or stemmed token");
        System.out.println("[-ignore-xml]   do not output XML tags");
        System.out.println("-i <DIR|FILE>   input directory or file");
        System.out.println("-o <DIR|FILE>   output directory or file");
    }
    
    /**
     * Command line interface.
     * @param args 
     * @throws java.io.FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException, IOException{
        
        // parse command line options
        if( args.length < 7 ){
            usage();
            return;
        }
        Map<String,String> opts = parseOptions(args);
        if( opts == null ){
            return;
        }
        if( !opts.containsKey("command") ){
            System.out.println("ERROR: no command specified!");
            return;
        }
        if( !opts.containsKey("input") ){
            System.out.println("ERROR: no input specified!");
            return;
        }
        if( !opts.containsKey("output") ){
            System.out.println("ERROR: no output specified!");
            return;
        }
        if( !opts.containsKey("format") ){
            System.out.println("no output format specified - will use TSV as default.");
            opts.put("format", "tsv");
        }
        boolean ignoreXml = false;
        if( opts.containsKey("ignore-xml") ){
            ignoreXml = true;
        }
        
        // use analyzer depending on command
        Analyzer analyzer;
        if( opts.get("command").equals("tok") ){
            analyzer = new ArTokenAnalyzer();
        }else if( opts.get("command").equals("norm") ){
            analyzer = new ArNormalAnalyzer();
        }else if( opts.get("command").equals("stem") ){
            analyzer = new ArabicAnalyzer(CharArraySet.EMPTY_SET);
        }else if( opts.get("command").equals("pretok") ){
            analyzer = new ArStemPretokAnalyzer();
        }else{
            return;
        }
        
        File input = new File(opts.get("input"));
        if( input.isDirectory() ){
            // create output directory if it doesn't exist
            File outputDir = new File(opts.get("output"));
            outputDir.mkdirs();
            File[] files = input.listFiles();
            for( File f : files ){
                processFile(f.getAbsolutePath(), outputDir.getAbsolutePath()+
                        File.separator+f.getName(), opts.get("format"),
                        ignoreXml, analyzer);
            }
        }else{
            processFile(opts.get("input"), opts.get("output"), opts.get("format"),
                    ignoreXml, analyzer);
        }
    }
    
    public static void processFile(String inputFilePath, String outputFilePath, 
            String format, boolean ignoreXml, Analyzer analyzer) throws 
            UnsupportedEncodingException, FileNotFoundException, IOException{
        
        BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File(inputFilePath)), "UTF-8"));
        PrintWriter writer = new PrintWriter(outputFilePath, "UTF-8");
        System.out.println(inputFilePath+" -> "+outputFilePath);
        String line;
        long z = 0;
        while( (line = br.readLine()) != null ){
            z++;
            line = line.trim();
            if( line.matches("\\s*") ){
                ; // ignore empty lines
            }
            else if( line.matches("<.+?>") ){
                if( ignoreXml == false ){
                    writer.println(line);
                }
            }else{
                List<Token> tokens = analyseString(line, analyzer);
                if( format.equals("txt") ){
                    int t = 0;
                    for( Token token : tokens ){
                        if( t == 0 ){
                            writer.print(token.getStem());
                        }else{
                            writer.print(" "+token.getStem());
                        }
                        t++;
                    }
                    writer.println();
                }else{
                    for( Token token : tokens ){
                        writer.println(token.getContent()+"\t"+token.isTerm()+"\t"
                                +token.getStem());
                    }
                }
            }
            if( z % 1000 == 0 ){
                System.out.print("\r"+z);
            }
        }
        System.out.println();
        br.close();
        writer.close();
    }
    
    /**
     * Analyze the input text with the given Analyzer.
     * @param text plain text in UTF-8
     * @param analyzer Instantiate <code>analyzer</code> with one of 
     * <code>ArabicAnalyzer</code>, <code>ArTokenAnalyzer</code>,
     * <code>ArNormalAnalyzer</code>, or <code>ArStemPretokAnalyzer</code>
     * depending on your use case.
     * @return list of Tokens. Depending on the Analyzer the tokens will have
     * different stems (value of <code>Token.getStem()</code>):
     * <ul>
     * <li><a href="https://lucene.apache.org/core/5_1_0/analyzers-common/index.html?org/apache/lucene/analysis/standard/StandardTokenizer.html">
     * ArabicAnalyzer</a> and <code>ArStemPretokAnalyzer</code>: 
     * the stem will be the stem resulting from the Light10 stemmer implemented
     * by <a href="https://lucene.apache.org/core/5_1_0/analyzers-common/index.html?org/apache/lucene/analysis/standard/StandardTokenizer.html">
     * ArabicStemmer</a>.</li>
     * <li><code>ArNormalAnalyzer</code>: the stem will be the orthographic normalization
     * of the original token text, as produced by <a href="https://lucene.apache.org/core/5_1_0/analyzers-common/index.html?org/apache/lucene/analysis/standard/StandardTokenizer.html">
     * ArabicNormalizer</a>.</li>
     * <li><code>ArTokenAnalyzer</code>: the stem will be the same as the content,
     * i.e. the substring in the input spanned by the token.</li>
     * </ul>
     */
    public static List<Token> analyseString(String text, Analyzer analyzer){
        
        List<Token> tokens = new ArrayList<>();
        try {
            TokenStream ts  = analyzer.tokenStream("field", text);
            OffsetAttribute offsetAtt = ts.addAttribute(OffsetAttribute.class);
            CharTermAttribute charAtt = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            int lastEnd = 0;
            while(ts.incrementToken()) {
                // non-term characters before current term
                if( offsetAtt.startOffset() > lastEnd ){
                    Token t = new Token(text.substring(lastEnd, offsetAtt.startOffset()),
                        text.substring(lastEnd, offsetAtt.startOffset()), lastEnd, 
                            offsetAtt.startOffset(), false);
                    if( !t.getContent().matches("\\s*") ){
                        tokens.add(t);
                    }
                }
                // current term
                tokens.add(new Token(text.substring(offsetAtt.startOffset(), offsetAtt.endOffset()),
                        charAtt.toString(), lastEnd, offsetAtt.startOffset(), true));
                lastEnd = offsetAtt.endOffset();
                
            }
            // non-term characters after final term
            if( lastEnd < text.length() ){
                Token t = new Token(text.substring(lastEnd), text.substring(lastEnd),
                        lastEnd, offsetAtt.startOffset(), false);
                if( !t.getContent().matches("\\s*") ){
                    tokens.add(t);
                }
            }
            ts.end();
            ts.close();
        }
        catch(IOException e) {
            // never thrown b/c we're using a string reader
        }
        return tokens;
    }
    
    private static Map<String,String> parseOptions(String[] args){
        
        Map<String,String> map = new HashMap<>();
        for( int i = 0; i < args.length; i++ ){
            switch (args[i]) {
                case "tok":
                case "norm":
                case "stem":
                case "pretok":
                    map.put("command", args[i]);
                    break;
                case "-of":
                    if( args[i+1].equalsIgnoreCase("txt") ){
                        map.put("format", "txt");
                    }else if( args[i+1].equalsIgnoreCase("tsv") ){
                        map.put("format", "tsv");
                    }else{
                        System.out.println("output format (-of) must be \"txt\" or \"tsv\"!");
                        return null;
                    }   i++;
                    break;
                case "-ignore-xml":
                    map.put("ignore-xml", "");
                    break;
                case "-i":
                    map.put("input", args[i+1]);
                    i++;
                    break;
                case "-o":
                    map.put("output", args[i+1]);
                    i++;
                    break;
                default:
                    System.out.println("WARNING: unrecognized option \""+args[i]+"\"!");
                    break;
            }
        }
        return map;
    }
}
