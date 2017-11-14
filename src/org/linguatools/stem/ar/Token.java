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

/**
 *
 * @author peterkolb
 */
public class Token {
    
    private final String content;
    private final String stem;
    private final int start;
    private final int end;
    private final boolean isTerm;
    
    public Token(String content, String stem, int start, int end, boolean isTerm){
        this.content = content;
        this.stem = stem;
        this.start = start;
        this.end = end;
        this.isTerm = isTerm;
    }
    
    /**
     * 
     * @return the substring of the input text that is spanned by the Token. 
     */
    public String getContent(){
        return content;
    }
    
    /**
     * 
     * @return the stem for the Token. If no stemming was performed, then this is
     * the normalized version of the Token. If no normalization was performed, then
     * this is equal to the original content of the Token as returned by getString().
     */
    public String getStem(){
        return stem;
    }
    
    /**
     * 
     * @return the start offset in characters of the Token in the input string. 
     */
    public int getStart(){
        return start;
    }
    
    /**
     * 
     * @return the end offset in characters of the Token in the input string.
     */
    public int getEnd(){
       return end; 
    }
    
    /**
     * 
     * @return <code>true</code> if the Token is a term according to the Lucene
     * tokenizer. 
     */
    public boolean isTerm(){
        return isTerm;
    }
}
