package org.elasticsearch.index.analysis;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.FailedToResolveConfigException;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;

public class SkroutzGreekStemmerTokenFilterFactory extends
		AbstractTokenFilterFactory {

  private final CharArraySet stopwords;
	@Inject
	public SkroutzGreekStemmerTokenFilterFactory(Index index,
			@IndexSettings Settings indexSettings,
			Environment env, @Assisted String name,
			@Assisted Settings settings) throws IOException {
		super(index, indexSettings, name, settings);
	  this.stopwords = parseStopWords(env, settings, "stopwords_path", Version.LUCENE_44);
	}

	@Override
	public TokenStream create(TokenStream tokenStream) {
		return new SkroutzGreekStemTokenFilter(tokenStream, stopwords);
	}

  private CharArraySet parseStopWords(Environment env, Settings settings,
      String settingPrefix, Version version) throws IOException {

    List<String> stopwordList = new ArrayList<String>();
    Reader stopwordsReader = null;

    try {
      stopwordsReader = Analysis.getReaderFromFile(env, settings, settingPrefix);
    } catch (FailedToResolveConfigException e) {
      logger.info("failed to find stopwords path, using the default stopword set");
    }

    if (stopwordsReader != null) {
      try {
        stopwordList = Analysis.loadWordList(stopwordsReader, "#");
        if (stopwordList.isEmpty()) {
          return CharArraySet.EMPTY_SET;
        } else {
          return new CharArraySet(version, stopwordList, false);
        }
      } finally {
        if (stopwordsReader != null)
          stopwordsReader.close();
      }
    } else {
      return SkroutzGreekStemmer.getDefaultStopSet();
    }
  }
}