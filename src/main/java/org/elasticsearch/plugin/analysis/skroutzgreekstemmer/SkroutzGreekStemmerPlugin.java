package org.elasticsearch.plugin.analysis.skroutzgreekstemmer;

import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.index.analysis.SkroutzGreekStemmerBinderProcessor;

public class SkroutzGreekStemmerPlugin extends Plugin {

	@Override
	public String description() {
		return "Greek stemmer customized for the needs of www.skroutz.gr";
	}

	@Override
	public String name() {
		return "skroutz-greek-stemmer";
	}

	public void onModule(AnalysisModule module) {
		module.addProcessor(new SkroutzGreekStemmerBinderProcessor());
	}

}
