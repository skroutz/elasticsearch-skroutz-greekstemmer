package org.elasticsearch.plugin.analysis.skroutzgreekstemmer;

import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import org.elasticsearch.index.analysis.SkroutzGreekStemmerTokenFilterFactory;

import java.util.Map;

import static java.util.Collections.singletonMap;

public class SkroutzGreekStemmerPlugin extends Plugin implements AnalysisPlugin {

	// Use singletonMap to register our token filter,
	// since we only have one in our plugin.
	@Override
	public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
		return singletonMap("skroutz_stem_greek", SkroutzGreekStemmerTokenFilterFactory::new);
	}
}
