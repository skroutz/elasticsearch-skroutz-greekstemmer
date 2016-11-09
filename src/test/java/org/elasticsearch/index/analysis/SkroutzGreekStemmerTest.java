package org.elasticsearch.index.analysis;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SkroutzGreekStemmerTest {
  private final SkroutzGreekStemmer stemmer = new SkroutzGreekStemmer();

  /**
   * a sample of greek words that should be stemmed by the Skroutz Greek stemmer.
   */
  private static final String[] words = { "προσκλητηριο", "προσκλητηρια",
      "βιντεοπροβολεασ", "βιντεοπροβολεισ", "αγριοσ", "αγριο", "αγριουσ",
      "αγροσ", "γιοσ", "γαλαζιοι", "γαλαζια", "κουρεασ", "κουρεα", "αμυνοξυ",
      "αμυνοξεα", "αμυνοξεων", "αγιασ", "αγων", "παπουτσια", "παπουτσι",
      "γραμματοκιβωτιο", "γραμματοκιβωτια", "παρεα", "παρεο", "παρεεσ",
      "στερεεσ", "στερεοσ", "στερεα", "φαση", "φασεωσ", "γραμματα", "διχτυα", 
      "ρολοι", "ρολογια"};

  /**
   * the stems that should be returned by the stemmer for the above words.
   */
  private static final String[] stems = { "προσκλητηρ", "προσκλητηρ",
      "βιντεοπροβολ", "βιντεοπροβολ", "αγρι", "αγρι", "αγρι", "αγρ", "γι",
      "γαλαζ", "γαλαζ", "κουρ", "κουρ", "αμυνοξ", "αμυνοξ", "αμυνοξ", "αγι",
      "αγ", "παπουτσ", "παπουτσ", "γραμματοκιβωτ", "γραμματοκιβωτ", "παρε",
      "παρε", "παρε", "στερε", "στερε", "στερε", "φασ", "φασ", "γραμμα", "διχτ",
      "ρολοι", "ρολοι"};

  private static final String[] stopwords = { "απο", "δυο", "ελα", "αμα",
    "ειτε", "εγω", "δεν", "δηλαδη" };

  private char[] token;
  private String stem;
  private int tokenLength, stemLength;

  @Test
  public void testSkroutzGreekStemmer() {
    for (int i = 0; i < words.length; i++) {
      token = words[i].toCharArray();
      tokenLength = words[i].length();
      stemLength = stemmer.stem(token, tokenLength);
      stem = new String(token, 0, stemLength);

      Assert.assertEquals(stem, stems[i]);
    }
  }

  @Test
  public void testSkroutzGreekStemmerStopwords() {
    for(String stopword : stopwords) {
      token = stopword.toCharArray();
      tokenLength = stopword.length();
      stemLength = stemmer.stem(token, tokenLength);
      stem = new String(token, 0, stemLength);

      Assert.assertEquals(stopword, stem);
    }
  }
}
