package org.elasticsearch.index.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;

public class SkroutzGreekStemmerTokenFilterFactory extends
		AbstractTokenFilterFactory {

	@Inject
	public SkroutzGreekStemmerTokenFilterFactory(Index index,
			@IndexSettings Settings indexSettings,
			Environment env, @Assisted String name,
			@Assisted Settings settings) throws IOException {
		super(index, indexSettings, name, settings);
	}

	@Override
	public TokenStream create(TokenStream tokenStream) {
		return new SkroutzGreekStemTokenFilter(tokenStream);
	}
}