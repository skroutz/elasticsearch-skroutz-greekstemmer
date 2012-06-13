package org.elasticsearch.index.analysis;

import static org.elasticsearch.common.settings.ImmutableSettings.Builder.EMPTY_SETTINGS;
import static org.hamcrest.Matchers.instanceOf;

import org.elasticsearch.common.inject.Injector;
import org.elasticsearch.common.inject.ModulesBuilder;
import org.elasticsearch.common.settings.SettingsModule;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.EnvironmentModule;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexNameModule;
import org.elasticsearch.index.settings.IndexSettingsModule;
import org.elasticsearch.indices.analysis.IndicesAnalysisModule;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;
import org.hamcrest.MatcherAssert;
import org.testng.annotations.Test;

public class SimpleSkroutzGreekStemmerAnalysisTest {

  @Test
  public void testSkroutzGreekStemmerAnalysis() {
      Index index = new Index("test");

      Injector parentInjector = new ModulesBuilder().add(new SettingsModule(EMPTY_SETTINGS),
              new EnvironmentModule(new Environment(EMPTY_SETTINGS)),
              new IndicesAnalysisModule()).createInjector();
      Injector injector = new ModulesBuilder().add(
              new IndexSettingsModule(index, EMPTY_SETTINGS),
              new IndexNameModule(index),
              new AnalysisModule(EMPTY_SETTINGS, parentInjector.getInstance(IndicesAnalysisService.class)).addProcessor(new SkroutzGreekStemmerBinderProcessor()))
              .createChildInjector(parentInjector);

      AnalysisService analysisService = injector.getInstance(AnalysisService.class);


      TokenFilterFactory filterFactory = analysisService.tokenFilter("skroutz_stem_greek");
      MatcherAssert.assertThat(filterFactory, instanceOf(SkroutzGreekStemmerTokenFilterFactory.class));
  }

}
