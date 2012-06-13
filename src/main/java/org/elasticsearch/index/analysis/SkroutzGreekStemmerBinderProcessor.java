package org.elasticsearch.index.analysis;

import org.elasticsearch.index.analysis.AnalysisModule.AnalysisBinderProcessor;

public class SkroutzGreekStemmerBinderProcessor extends AnalysisBinderProcessor {
	
	@Override public void processTokenFilters(TokenFiltersBindings tokenFiltersBindings) {
		tokenFiltersBindings.processTokenFilter("skroutz_stem_greek", SkroutzGreekStemmerTokenFilterFactory.class);
	}


}
