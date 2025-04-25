package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

public class SkroutzGreekStemmerTokenFilterFactory extends AbstractTokenFilterFactory {
  public SkroutzGreekStemmerTokenFilterFactory(
    IndexSettings indexSettings, Environment environment, String name, Settings settings
  ) {
    super(name, settings);
  };


	@Override
	public TokenStream create(TokenStream tokenStream) {
		return new SkroutzGreekStemTokenFilter(tokenStream);
	}
}
