package org.elasticsearch.index.analysis;

import org.hamcrest.MatcherAssert;
import org.testng.annotations.Test;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.inject.Injector;
import org.elasticsearch.common.inject.ModulesBuilder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsModule;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.EnvironmentModule;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexNameModule;
import org.elasticsearch.index.settings.IndexSettingsModule;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;

import static org.elasticsearch.common.settings.Settings.settingsBuilder;
import static org.hamcrest.Matchers.instanceOf;

public class SimpleSkroutzGreekStemmerAnalysisTest {

  @Test
  public void testSkroutzGreekStemmerAnalysis() {
      Index index = new Index("test");

      Settings indexSettings = settingsBuilder()
          .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
          .put("path.home", "/")
          .build();

      Injector parentInjector = new ModulesBuilder()
          .add(new SettingsModule(indexSettings),
               new EnvironmentModule(new Environment(indexSettings)))
	  .createInjector();

      Injector injector = new ModulesBuilder()
          .add(new IndexSettingsModule(index, indexSettings),
               new IndexNameModule(index),
               new AnalysisModule(indexSettings,
                                  parentInjector.getInstance(IndicesAnalysisService.class))
               .addProcessor(new SkroutzGreekStemmerBinderProcessor()))
          .createChildInjector(parentInjector);

      AnalysisService analysisService = injector.getInstance(AnalysisService.class);

      TokenFilterFactory filterFactory = analysisService.tokenFilter("skroutz_stem_greek");
      MatcherAssert.assertThat(filterFactory, instanceOf(SkroutzGreekStemmerTokenFilterFactory.class));
  }

}
