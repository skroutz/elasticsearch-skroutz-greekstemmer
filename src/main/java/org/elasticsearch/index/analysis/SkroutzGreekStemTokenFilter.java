package org.elasticsearch.index.analysis;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

/**
 * A {@link TokenFilter} that applies {@link SkroutzGreekStemmer} to stem Greek
 * words.
 * <p>
 * NOTE: Input is expected to be casefolded for Greek (including folding of final
 * sigma to sigma), and with diacritics removed. This can be achieved by using
 * either {@link org.apache.lucene.analysis.el.GreekLowerCaseFilter} or
 *  ICUFoldingFilter before GreekStemFilter.
 *
 * Exported from @lucene.experiment and modified in order to use the Skroutz
 * Greek stemmer.
 */
public class SkroutzGreekStemTokenFilter extends TokenFilter {
    private final SkroutzGreekStemmer stemmer = new SkroutzGreekStemmer();
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);

	public SkroutzGreekStemTokenFilter(TokenStream input) {
		super(input);
	}

	@Override
	public boolean incrementToken() throws IOException {
		if (input.incrementToken()) {
			if (!keywordAttr.isKeyword()) {
				final int newlen = stemmer.stem(termAtt.buffer(), termAtt.length());
				termAtt.setLength(newlen);
			}
			return true;
		} else {
			return false;
		}
	}
}
