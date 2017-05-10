package org.elasticsearch.index.analysis;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.plugin.analysis.skroutzgreekstemmer.SkroutzGreekStemmerPlugin;

import java.io.IOException;

import static org.hamcrest.Matchers.instanceOf;

public class SimpleSkroutzGreekStemmerAnalysisTest extends ESTestCase {
	public void testSkroutzGreekStemmerAnalysis() throws IOException {
		TestAnalysis analysis = createTestAnalysis(new Index("test", "_na_"),
				Settings.EMPTY, new SkroutzGreekStemmerPlugin());

		TokenFilterFactory filterFactory = analysis.tokenFilter.get("skroutz_stem_greek");
		assertThat(filterFactory, instanceOf(SkroutzGreekStemmerTokenFilterFactory.class));
	}
}
