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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ar.ArabicNormalizationFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * Calls Lucene's <a href="https://lucene.apache.org/core/5_1_0/analyzers-common/index.html?org/apache/lucene/analysis/standard/StandardTokenizer.html">
 * StandardTokenizer</a> with <a href="https://lucene.apache.org/core/5_1_0/analyzers-common/org/apache/lucene/analysis/ar/ArabicNormalizationFilter.html">
 * ArabicNormalizationFilter</a> to split the input text into normalized tokens.
 * @author peterkolb
 */
public class ArNormalAnalyzer extends Analyzer{
    
    @Override
    protected TokenStreamComponents createComponents(String field) {
        Tokenizer tokenizer = new StandardTokenizer();
        TokenStream filter = new ArabicNormalizationFilter(tokenizer);
        return new TokenStreamComponents(tokenizer, filter);
    }
    
}
