package org.elasticsearch.index.analysis;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.Map;

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
      "κανονικοσ", "κανονικεσ", "κανονικουσ", "κανονικα", "μονοφωτα",
      "μονοφωτο","ρολοι", "ρολογια"};

  /**
   * the stems that should be returned by the stemmer for the above words.
   */
  private static final String[] stems = { "προσκλητηρ", "προσκλητηρ",
      "βιντεοπροβολ", "βιντεοπροβολ", "αγρι", "αγρι", "αγρι", "αγρ", "γι",
      "γαλαζ", "γαλαζ", "κουρ", "κουρ", "αμυνοξ", "αμυνοξ", "αμυνοξ", "αγι",
      "αγ", "παπουτσ", "παπουτσ", "γραμματοκιβωτ", "γραμματοκιβωτ", "παρε",
      "παρε", "παρε", "στερε", "στερε", "στερε", "φασ", "φασ", "γραμμα", "διχτ",
      "κανονικ", "κανονικ", "κανονικ", "κανονικ", "μονοφω", "μονοφω",
      "ρολ", "ρολ"};

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
  public void testSkroutzGreekStemmerExceptionsZero()
          throws NoSuchFieldException, IllegalAccessException {

    Field field = stemmer.getClass().getDeclaredField("stepZeroExceptions");
    field.setAccessible(true);

    Map<String, char[]> stepZeroExceptions = (Map) field.get(stemmer);

   for (Map.Entry<String, char[]> entry : stepZeroExceptions.entrySet()) {
     Assert.assertTrue(entry.getKey().length() >= entry.getValue().length,
             "Make sure that the stem is shorter/equal than the original.");
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
