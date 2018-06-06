package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.CharArrayMap;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

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
      "μονοφωτα", "μονοφωτο","ρολοι", "ρολογια", "φυτικοσ", "λαδουσα",
      "πεταλο", "τετραδιο", "τετραδιου", "πανια", "πανι", "πανιων", "ρολα",
      "σκι", "κρεμα", "κρεμασ", "σποροι", "σπορων", "σπορουσ", "βερμουδεσ",
      "ματι", "ματια", "ματιων"};

  /**
   * the stems that should be returned by the stemmer for the above words.
   */
  private static final String[] stems = { "προσκλητηρ", "προσκλητηρ",
      "βιντεοπροβολ", "βιντεοπροβολ", "αγρι", "αγρι", "αγρι", "αγρ", "γι",
      "γαλαζ", "γαλαζ", "κουρ", "κουρ", "αμυνοξ", "αμυνοξ", "αμυνοξ", "αγι",
      "αγ", "παπουτσ", "παπουτσ", "γραμματοκιβωτ", "γραμματοκιβωτ", "παρε",
      "παρε", "παρε", "στερε", "στερε", "στερε", "φασ", "φασ", "γραμμα", "διχτ",
      "μονοφω", "μονοφω", "ρολ", "ρολ", "φυτικ", "λαδουσ", "πεταλο", "τετραδι",
      "τετραδι", "πανι", "πανι", "πανι", "ρολα", "σκ", "κρεμα", "κρεμα",
      "σπορο", "σπορο", "σπορο", "βερμουδ", "ματι", "ματι", "ματι" };

  private static final String[] protectedWords = { "απο", "δυο", "ελα", "αμα",
    "ειτε", "εγω", "δεν", "δηλαδη", "κανο" };

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

    CharArrayMap stepZeroExceptions = (CharArrayMap) field.get(stemmer);
    CharArrayMap.EntryIterator it = stepZeroExceptions.entrySet().iterator();
    while(it.hasNext()) {
        char[] key = it.nextKey();
        // currentValue returns the value associated with the last key returned
        char[] value = (char[]) it.currentValue();
        Assert.assertTrue(key.length >= value.length,
                "Make sure that the stem is shorter/equal than the original.");
    }
  }

  @Test
  public void testSkroutzGreekStemmerProtectedwords() {
    for(String protectedWord : protectedWords) {
      token = protectedWord.toCharArray();
      tokenLength = protectedWord.length();
      stemLength = stemmer.stem(token, tokenLength);
      stem = new String(token, 0, stemLength);

      Assert.assertEquals(protectedWord, stem);
    }
  }
}
