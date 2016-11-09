package org.elasticsearch.index.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.lucene.analysis.el.GreekLowerCaseFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;
import org.elasticsearch.common.lucene.Lucene;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A stemmer for Greek words, according to: <i>Development of a Stemmer for the
 * Greek Language.</i> Georgios Ntais
 * <p>
 * NOTE: Input is expected to be casefolded for Greek (including folding of final
 * sigma to sigma), and with diacritics removed. This can be achieved with
 * either {@link GreekLowerCaseFilter} or ICUFoldingFilter.
 *
 * This stemmer is based on the stemmer of the @lucene.experimental with some
 * additions.
 * <p>
 * According to: <i>Development of a Stemmer for the Greek Language.</i>, the
 * original stemmer removed 158 suffixes. Eight suffixes were not handled at
 * all by this stemmer. However, four of those eight suffixes belong to the same
 * category with one of the suffixes that is actually handle by the stemmer.
 * This suffix is "ια" which is mishandled and may be the plural type of the
 * suffix "ιο".
 * <p>
 * Furthermore, the suffix "εασ" is not stemmed correctly and is not even
 * included in the original 166 suffixes that should be removed.
 * <p>
 * This custom Greek stemmer adds to the current rule set some more cases about
 * the following suffixes:
 * <p>
 * <b>"ιο", "ιασ", "ιεσ", "ιοσ", "ιουσ", "ιοι", "εασ", "εα"</b>
 * <p>
 * Following the same strategy with the Greek stemmer of lucene, some exceptions
 * about these suffixes are added.
 */
public class SkroutzGreekStemmer {
  public final static String DEFAULT_STOPWORD_FILE = "stopwords.txt";
  private final CharArraySet stopwords;

  public SkroutzGreekStemmer(final CharArraySet stopwords) {
    this.stopwords = stopwords;
  }

  public SkroutzGreekStemmer() {
    this.stopwords = SkroutzGreekStemmer.getDefaultStopSet();
  }

  public static final CharArraySet getDefaultStopSet(){
    return DefaultSetHolder.DEFAULT_SET;
  }

  private static class DefaultSetHolder {
    private static final CharArraySet DEFAULT_SET;

    static {
      try {
        DEFAULT_SET = loadStopwordSet(
            SkroutzGreekStemmer.class.getResourceAsStream(DEFAULT_STOPWORD_FILE),
            Lucene.VERSION);
      } catch (IOException ex) {
        // default set should always be present as it is part of the
        // distribution (JAR)
        throw new RuntimeException("Unable to load default stopword set");
      }
    }
  }

  public int stem(char s[], int len) {
    // Too short or a stopword
    if (len < 3 || stopwords.contains(s, 0, len))
      return len;

    final int origLen = len;
    // "short rules": if it hits one of these, it skips the "long list"
    len = rule0(s, len);
    len = rule1(s, len);
    len = rule2(s, len);
    len = rule3(s, len);
    len = rule4(s, len);
    len = rule5a(s, len);
    len = rule5b(s, len);
    len = rule6(s, len);
    len = rule7(s, len);
    len = rule8(s, len);
    len = rule9(s, len);
    len = rule10(s, len);
    len = rule11(s, len);
    len = rule12(s, len);
    len = rule13(s, len);
    len = rule14(s, len);
    len = rule15(s, len);
    len = rule16(s, len);
    len = rule17(s, len);
    len = rule18(s, len);
    len = rule19(s, len);
    len = rule20(s, len);
    len = rule21(s, len);
    // "long list"
    if (len == origLen)
      len = rule22(s, len);

    return rule23(s, len);
  }

  private int rule0(char s[], int len) {
    if (len > 9 && (endsWith(s, len, "καθεστωτοσ")
        || endsWith(s, len, "καθεστωτων")))
      return len - 4;

    if (len > 8 && (endsWith(s, len, "γεγονοτοσ")
        || endsWith(s, len, "γεγονοτων")))
      return len - 4;

    if (len > 8 && endsWith(s, len, "καθεστωτα"))
      return len - 3;

    if (len > 7 && (endsWith(s, len, "τατογιου")
        || endsWith(s, len, "τατογιων")))
      return len - 4;

    if (len > 7 && endsWith(s, len, "γεγονοτα"))
      return len - 3;

    if (len > 7 && endsWith(s, len, "καθεστωσ"))
      return len - 2;

    if (len > 6 && (endsWith(s, len, "σκαγιου"))
        || endsWith(s, len, "σκαγιων")
        || endsWith(s, len, "κρεατοσ")
        || endsWith(s, len, "κρεατων")
        || endsWith(s, len, "περατοσ")
        || endsWith(s, len, "περατων")
        || endsWith(s, len, "τερατοσ")
        || endsWith(s, len, "τερατων"))
      return len - 4;

    if (len > 6 && endsWith(s, len, "τατογια"))
      return len - 3;

    if (len > 6 && endsWith(s, len, "γεγονοσ"))
      return len - 2;

    if (len > 5 && (endsWith(s, len, "φαγιου")
        || endsWith(s, len, "φαγιων")
        || endsWith(s, len, "σογιου")
        || endsWith(s, len, "σογιων")))
      return len - 4;

    if (len > 5 && (endsWith(s, len, "σκαγια")
        || endsWith(s, len, "κρεατα")
        || endsWith(s, len, "περατα")
        || endsWith(s, len, "τερατα")))
      return len - 3;

    if (len > 4 && (endsWith(s, len, "φαγια")
        || endsWith(s, len, "σογια")
        || endsWith(s, len, "φωτοσ")
        || endsWith(s, len, "φωτων")))
      return len - 3;

    if (len > 4 && (endsWith(s, len, "κρεασ")
        || endsWith(s, len, "περασ")
        || endsWith(s, len, "τερασ")))
      return len - 2;

    if (len > 3 && endsWith(s, len, "φωτα"))
      return len - 2;

    if (len > 2 && endsWith(s, len, "φωσ"))
      return len - 1;

    if (len > 2 && endsWith(s, len, "ευα"))
      return len - 1;

    return len;
  }

  private int rule1(char s[], int len) {
    if (len > 4 && (endsWith(s, len, "αδεσ") || endsWith(s, len, "αδων"))) {
      len -= 4;
      if (!(endsWith(s, len, "οκ") ||
          endsWith(s, len, "μαμ") ||
          endsWith(s, len, "μαν") ||
          endsWith(s, len, "μπαμπ") ||
          endsWith(s, len, "πατερ") ||
          endsWith(s, len, "γιαγι") ||
          endsWith(s, len, "νταντ") ||
          endsWith(s, len, "κυρ") ||
          endsWith(s, len, "θει") ||
          endsWith(s, len, "πεθερ") ||
          endsWith(s, len, "μουσαμ") ||
          endsWith(s, len, "παρ") ||
          endsWith(s, len, "ψαρ") ||
          endsWith(s, len, "τζουρ") ||
          endsWith(s, len, "ταμπουρ") ||
          endsWith(s, len, "καπλαμ")))
        len += 2; // add back -αδ
    }
    return len;
  }

  private int rule2(char s[], int len) {
    if (len > 4 && (endsWith(s, len, "εδεσ") || endsWith(s, len, "εδων"))) {
      len -= 4;
      if (endsWith(s, len, "οπ") ||
          endsWith(s, len, "ιπ") ||
          endsWith(s, len, "εμπ") ||
          endsWith(s, len, "υπ") ||
          endsWith(s, len, "γηπ") ||
          endsWith(s, len, "δαπ") ||
          endsWith(s, len, "κρασπ") ||
          endsWith(s, len, "μιλ"))
        len += 2; // add back -εδ
    }
    return len;
  }

  private int rule3(char s[], int len) {
    if (len > 5 && (endsWith(s, len, "ουδεσ") || endsWith(s, len, "ουδων"))) {
      len -= 5;
      if (endsWith(s, len, "αρκ") ||
          endsWith(s, len, "καλιακ") ||
          endsWith(s, len, "πεταλ") ||
          endsWith(s, len, "λιχ") ||
          endsWith(s, len, "πλεξ") ||
          endsWith(s, len, "σκ") ||
          endsWith(s, len, "σ") ||
          endsWith(s, len, "φλ") ||
          endsWith(s, len, "φρ") ||
          endsWith(s, len, "βελ") ||
          endsWith(s, len, "λουλ") ||
          endsWith(s, len, "χν") ||
          endsWith(s, len, "σπ") ||
          endsWith(s, len, "τραγ") ||
          endsWith(s, len, "φε"))
        len += 3; // add back -ουδ
    }
    return len;
  }

  /**
   * Exceptions added to the Skroutz Greek stemmer about suffixes "εασ", "εα", in
   * order to improve the quality of the query.
   * <p>
   * Exception examples:
   * <p>
   * <i>παρεα - παρεασ - παρεεσ, στερεα - στερεασ - στερεεσ</i>
   */
  private static final CharArraySet exc4 = new CharArraySet(Lucene.VERSION,
      Arrays.asList("θ", "δ", "ελ", "γαλ", "ν", "π", "ιδ", "παρ", "στερ",
          "ορφ", "ανδρ", "αντρ"),
      false);

  private int rule4(char s[], int len) {
    boolean removed = false;

    if (len > 3 && (endsWith(s, len, "εωσ") ||
                    endsWith(s, len, "εων") ||
                    endsWith(s, len, "εασ"))) {
      len -= 3;
      removed = true;
    } else if (len > 2 && endsWith(s, len, "εα")) {
      len -= 2;
      removed = true;
    }

    if (removed && exc4.contains(s, 0, len)) {
      len += 1; // add back the "ε"
    }
    return len;
  }

  /**
   * Exceptions added to the Skroutz Greek stemmer about rule 5, in order to
   * improve the quality of the query.
   * <p>
   * Exception examples:
   * <p>
   * <i>ηλιος - ηλος, αγριος - αγρος, χωρα - χωριο, αγιος - αγων, φωτο - φωτια,
   * νοτα - νοτια, μπριος - μπρος, τηλιο - τηλος, δημος - δημιος, οπαλιο - οπαλ,
   * πατριος - πατρα, ποντος - ποντιος, σκορπω - σκορπιος, σπανιος - σπανος,
   * τιμή - τιμιος</i>
   */
  private static final CharArraySet exc5 = new CharArraySet(Lucene.VERSION,
      Arrays.asList("αγ", "αγγελ", "αγρ", "αερ", "αθλ", "ακουσ", "αξ", "ασ",
          "β", "βιβλ", "βυτ", "γ", "γιαγ", "γων", "δ", "δαν", "δηλ", "δημ",
          "δοκιμ", "ελ", "ζαχαρ", "ηλ", "ηπ", "ιδ", "ισκ", "ιστ", "ιον",  "ιων",
          "κιμωλ", "κολον", "κορ", "κτηρ", "κυρ", "λαγ", "λογ", "μαγ", "μπαν",
          "μπετον", "μπρ", "ναυτ", "νοτ", "οπαλ", "οξ", "ορ", "οσ", "παναγ",
          "πατρ", "πηλ", "πην", "πλαισ", "ποντ", "ραδ", "ροδ", "σκ", "σκορπ",
          "σουν", "σπαν", "σταδ", "συρ", "τηλ", "τιμ", "τοκ", "τοπ", "τροχ",
          "χωρ", "φιλ", "φωτ", "χ", "χιλ", "χρωμ"),
          false);

  private int rule5a(char s[], int len) {
    if (len > 7 && (endsWith(s, len, "ειο") ||
                    endsWith(s, len, "εια"))) {
      len -= 3;
    } else if (len > 8 && (endsWith(s, len, "ειοσ") ||
                           endsWith(s, len, "ειοι") ||
                           endsWith(s, len, "ειασ") ||
                           endsWith(s, len, "ειεσ") ||
                           endsWith(s, len, "ειου") ||
                           endsWith(s, len, "ειων"))) {
      len -= 4;
    } else if (len > 9 && (endsWith(s, len, "ειουσ"))) {
      len -= 5;
    }

    return len;
  }

  private int rule5b(char s[], int len) {
    boolean removed = false;
    if (len > 2 && (endsWith(s, len, "ιο") ||
                    endsWith(s, len, "ια"))) {
      len -= 2;
      if(endsWith(s, len, "ρολογ") ||
         endsWith(s, len, "κατωγ")) {
        s[len-1] = 'ι'; // ρολοι, κατωι
        return len;
      }
      removed = true;
    } else if (len > 3 && (endsWith(s, len, "ιασ") ||
                           endsWith(s, len, "ιεσ") ||
                           endsWith(s, len, "ιοσ") ||
                           endsWith(s, len, "ιου") ||
                           endsWith(s, len, "ιοι") ||
                           endsWith(s, len, "ιον") ||
                           endsWith(s, len, "ιων"))) {
      len -= 3;
      removed = true;
    } else if (len > 4 && (endsWith(s, len, "ιουσ"))) {
      len -= 4;
      removed = true;
    }

    if (removed) {                                    // like γιος -> γ
      if (endsWithVowel(s, len) || exc5.contains(s, 0, len) || len < 2) {
        len++;  // add back -ι
      } else if (endsWith(s, len, "παλ")) {
        // add -αι emoved > 4 chars so its safe)
        len += 2;
        s[len - 2] = 'α';
        s[len - 1] = 'ι';
      }
    }
    return len;
  }

  private static final CharArraySet exc6 = new CharArraySet(Lucene.VERSION,
      Arrays.asList("αδ", "αλ", "αμαν", "αμερ", "αμμοχαλ", "ανηθ", "αντιδ",
          "απλ", "αττ", "αφρ", "βασ", "βρωμ","βρωμ", "γεν", "γερ", "δ", "δικαν",
          "δυτ", "ειδ", "ενδ", "εξωδ", "ηθ", "θετ", "καλλιν", "καλπ", "καταδ",
          "κουζιν", "κρ", "κωδ", "λογ",  "μ", "μερ", "μοναδ", "μουλ", "μουσ",
          "μπαγιατ", "μπαν", "μπολ", "μποσ", "μυστ", "ν", "νιτ", "ξικ", "οπτ",
          "παν", "πετσ", "πικαντ", "πιτσ", "πλαστ", "πλιατσ", "ποντ", "ποστελν",
          "πρωτοδ", "σερτ", "σημαντ", "στατ", "συναδ", "συνομηλ", "τελ", "τεχν",
          "τροπ", "τσαμ", "υποδ", "φ", "φιλον", "φυλοδ", "φυσ", "χασ"),
          false);

  private int rule6(char s[], int len) {
    boolean removed = false;
    if (len > 3 && (endsWith(s, len, "ικα") ||
                    endsWith(s, len, "ικο") ||
                    endsWith(s, len, "ικη"))) {
      len -= 3;
      removed = true;
    } else if (len > 4 && (endsWith(s, len, "ικου") ||
                           endsWith(s, len, "ικων") ||
                           endsWith(s, len, "ικωσ") ||
                           endsWith(s, len, "ικοσ") ||
                           endsWith(s, len, "ικον") ||
                           endsWith(s, len, "ικοι") ||
                           endsWith(s, len, "ικησ") ||
                           endsWith(s, len, "ικεσ"))) {
      len -= 4;
      removed = true;
    } else if (len > 5 && (endsWith(s, len, "ικουσ") ||
                           endsWith(s, len, "ικεισ"))) {
      len -= 5;
      removed = true;
    }

    if (removed) {
      if (endsWithVowel(s, len)    ||
          exc6.contains(s, 0, len) ||
          endsWith(s, len, "φοιν"))
        len += 2; // add back -ικ
    }
    return len;
  }

  private static final CharArraySet exc7 = new CharArraySet(Lucene.VERSION,
      Arrays.asList("αναπ", "αποθ", "αποκ", "αποστ", "βουβ", "ξεθ", "ουλ",
          "πεθ", "πικρ", "ποτ", "σιχ", "χ"),
      false);

  private int rule7(char s[], int len) {
    if (len == 5 && endsWith(s, len, "αγαμε"))
      return len - 1;

    if (len > 7 && endsWith(s, len, "ηθηκαμε"))
      len -= 7;
    else if (len > 6 && endsWith(s, len, "ουσαμε"))
      len -= 6;
    else if (len > 5 && (endsWith(s, len, "αγαμε") ||
             endsWith(s, len, "ησαμε") ||
             endsWith(s, len, "ηκαμε")))
      len -= 5;

    if (len > 3 && endsWith(s, len, "αμε")) {
      len -= 3;
      if (exc7.contains(s, 0, len))
        len += 2; // add back -αμ
    }

    return len;
  }

  private static final CharArraySet exc8a = new CharArraySet(Lucene.VERSION,
      Arrays.asList("τρ", "τσ"),
      false);

  private static final CharArraySet exc8b = new CharArraySet(Lucene.VERSION,
      Arrays.asList("βετερ", "βουλκ", "βραχμ", "γ", "δραδουμ", "θ", "καλπουζ",
          "καστελ", "κορμορ", "λαοπλ", "μωαμεθ", "μ", "μουσουλμ", "ν", "ουλ",
          "π", "πελεκ", "πλ", "πολισ", "πορτολ", "σαρακατσ", "σουλτ",
          "τσαρλατ", "ορφ", "τσιγγ", "τσοπ", "φωτοστεφ", "χ", "ψυχοπλ", "αγ",
          "ορφ", "γαλ", "γερ", "δεκ", "διπλ", "αμερικαν", "ουρ", "πιθ",
          "πουριτ", "σ", "ζωντ", "ικ", "καστ", "κοπ", "λιχ", "λουθηρ", "μαιντ",
          "μελ", "σιγ", "σπ", "στεγ", "τραγ", "τσαγ", "φ", "ερ", "αδαπ",
          "αθιγγ", "αμηχ", "ανικ", "ανοργ", "απηγ", "απιθ", "ατσιγγ", "βασ",
          "βασκ", "βαθυγαλ", "βιομηχ", "βραχυκ", "διατ", "διαφ", "ενοργ",
          "θυσ", "καπνοβιομηχ", "καταγαλ", "κλιβ", "κοιλαρφ", "λιβ",
          "μεγλοβιομηχ", "μικροβιομηχ", "νταβ", "ξηροκλιβ", "ολιγοδαμ",
          "ολογαλ", "πενταρφ", "περηφ", "περιτρ", "πλατ", "πολυδαπ", "πολυμηχ",
          "στεφ", "ταβ", "τετ", "υπερηφ", "υποκοπ", "χαμηλοδαπ", "ψηλοταβ"),
      false);

  private int rule8(char s[], int len) {
    boolean removed = false;

    if (len > 8 && endsWith(s, len, "ιουντανε")) {
      len -= 8;
      removed = true;
    } else if (len > 7 && (endsWith(s, len, "ιοντανε") ||
        endsWith(s, len, "ουντανε") ||
        endsWith(s, len, "ηθηκανε"))) {
      len -= 7;
      removed = true;
    } else if (len > 6 && (endsWith(s, len, "ιοτανε") ||
        endsWith(s, len, "οντανε") ||
        endsWith(s, len, "ουσανε"))) {
      len -= 6;
      removed = true;
    } else if (len > 5 && (endsWith(s, len, "αγανε") ||
        endsWith(s, len, "ησανε") ||
        endsWith(s, len, "οτανε") ||
        endsWith(s, len, "ηκανε"))) {
      len -= 5;
      removed = true;
    }

    if (removed && exc8a.contains(s, 0, len)) {
      // add -αγαν (we removed > 4 chars so its safe)
      len += 4;
      s[len - 4] = 'α';
      s[len - 3] = 'γ';
      s[len - 2] = 'α';
      s[len - 1] = 'ν';
    }

    if (len > 3 && endsWith(s, len, "ανε")) {
      len -= 3;
      if (endsWithVowelNoY(s, len) || exc8b.contains(s, 0, len)) {
        len += 2; // add back -αν
      }
    }

    return len;
  }

  private static final CharArraySet exc9 = new CharArraySet(Lucene.VERSION,
      Arrays.asList("αβαρ", "βεν", "εναρ", "αβρ", "αδ", "αθ", "αν", "απλ",
          "βαρον", "ντρ", "σκ", "κοπ", "μπορ", "νιφ", "παγ", "παρακαλ", "σερπ",
          "σκελ", "συρφ", "τοκ", "υ", "δ", "εμ", "θαρρ", "θ"),
      false);

  private int rule9(char s[], int len) {
    if (len > 5 && endsWith(s, len, "ησετε"))
      len -= 5;

    if (len > 3 && endsWith(s, len, "ετε")) {
      len -= 3;
      if (exc9.contains(s, 0, len) ||
          endsWithVowelNoY(s, len) ||
          endsWith(s, len, "οδ") ||
          endsWith(s, len, "αιρ") ||
          endsWith(s, len, "φορ") ||
          endsWith(s, len, "ταθ") ||
          endsWith(s, len, "διαθ") ||
          endsWith(s, len, "σχ") ||
          endsWith(s, len, "ενδ") ||
          endsWith(s, len, "ευρ") ||
          endsWith(s, len, "τιθ") ||
          endsWith(s, len, "υπερθ") ||
          endsWith(s, len, "ραθ") ||
          endsWith(s, len, "ενθ") ||
          endsWith(s, len, "ροθ") ||
          endsWith(s, len, "σθ") ||
          endsWith(s, len, "πυρ") ||
          endsWith(s, len, "αιν") ||
          endsWith(s, len, "συνδ") ||
          endsWith(s, len, "συν") ||
          endsWith(s, len, "συνθ") ||
          endsWith(s, len, "χωρ") ||
          endsWith(s, len, "πον") ||
          endsWith(s, len, "βρ") ||
          endsWith(s, len, "καθ") ||
          endsWith(s, len, "ευθ") ||
          endsWith(s, len, "εκθ") ||
          endsWith(s, len, "νετ") ||
          endsWith(s, len, "ρον") ||
          endsWith(s, len, "αρκ") ||
          endsWith(s, len, "βαρ") ||
          endsWith(s, len, "βολ") ||
          endsWith(s, len, "ωφελ")) {
        len += 2; // add back -ετ
      }
    }

    return len;
  }

  private int rule10(char s[], int len) {
    if (len > 5 && (endsWith(s, len, "οντασ") || endsWith(s, len, "ωντασ"))) {
      len -= 5;
      if (len == 3 && endsWith(s, len, "αρχ")) {
        len += 3; // add back *ντ
        s[len - 3] = 'ο';
      }
      if (endsWith(s, len, "κρε")) {
        len += 3; // add back *ντ
        s[len - 3] = 'ω';
      }
    }

    return len;
  }

  private int rule11(char s[], int len) {
    if (len > 6 && endsWith(s, len, "ομαστε")) {
      len -= 6;
      if (len == 2 && endsWith(s, len, "ον")) {
        len += 5; // add back -ομαστ
      }
    } else if (len > 7 && endsWith(s, len, "ιομαστε")) {
      len -= 7;
      if (len == 2 && endsWith(s, len, "ον")) {
        len += 5;
        s[len - 5] = 'ο';
        s[len - 4] = 'μ';
        s[len - 3] = 'α';
        s[len - 2] = 'σ';
        s[len - 1] = 'τ';
      }
    }
    return len;
  }

  private static final CharArraySet exc12a = new CharArraySet(Lucene.VERSION,
      Arrays.asList("π", "απ", "συμπ", "ασυμπ", "ακαταπ", "αμεταμφ"),
      false);

  private static final CharArraySet exc12b = new CharArraySet(Lucene.VERSION,
      Arrays.asList("αλ", "αρ", "εκτελ", "ζ", "μ", "ξ", "παρακαλ", "αρ", "προ", "νισ"),
      false);

  private int rule12(char s[], int len) {
    if (len > 5 && endsWith(s, len, "ιεστε")) {
      len -= 5;
      if (exc12a.contains(s, 0, len))
        len += 4; // add back -ιεστ
    }

    if (len > 4 && endsWith(s, len, "εστε")) {
      len -= 4;
      if (exc12b.contains(s, 0, len))
        len += 3; // add back -εστ
    }

    return len;
  }

  private static final CharArraySet exc13 = new CharArraySet(Lucene.VERSION,
      Arrays.asList("διαθ", "θ", "παρακαταθ", "προσθ", "συνθ"),
      false);

  private int rule13(char s[], int len) {
    if (len > 6 && endsWith(s, len, "ηθηκεσ")) {
      len -= 6;
    } else if (len > 5 && (endsWith(s, len, "ηθηκα") || endsWith(s, len, "ηθηκε"))) {
      len -= 5;
    }

    boolean removed = false;

    if (len > 4 && endsWith(s, len, "ηκεσ")) {
      len -= 4;
      removed = true;
    } else if (len > 3 && (endsWith(s, len, "ηκα") || endsWith(s, len, "ηκε"))) {
      len -= 3;
      removed = true;
    }

    if (removed && (exc13.contains(s, 0, len)
        || endsWith(s, len, "σκωλ")
        || endsWith(s, len, "σκουλ")
        || endsWith(s, len, "ναρθ")
        || endsWith(s, len, "σφ")
        || endsWith(s, len, "οθ")
        || endsWith(s, len, "πιθ"))) {
      len += 2; // add back the -ηκ
    }

    return len;
  }

  private static final CharArraySet exc14 = new CharArraySet(Lucene.VERSION,
      Arrays.asList("φαρμακ", "χαδ", "αγκ", "αναρρ", "βρομ", "εκλιπ", "λαμπιδ",
          "λεχ", "μ", "πατ", "ρ", "λ", "μεδ", "μεσαζ", "υποτειν", "αμ", "αιθ",
          "ανηκ", "δεσποζ", "ενδιαφερ", "δε", "δευτερευ", "καθαρευ", "πλε",
          "τσα"),
      false);

  private int rule14(char s[], int len) {
    boolean removed = false;

    if (len > 5 && endsWith(s, len, "ουσεσ")) {
      len -= 5;
      removed = true;
    } else if (len > 4 && (endsWith(s, len, "ουσα") || endsWith(s, len, "ουσε"))) {
      len -= 4;
      removed = true;
    }

    if (removed && (exc14.contains(s, 0, len)
        || endsWithVowel(s, len)
        || endsWith(s, len, "ποδαρ")
        || endsWith(s, len, "βλεπ")
        || endsWith(s, len, "πανταχ")
        || endsWith(s, len, "φρυδ")
        || endsWith(s, len, "μαντιλ")
        || endsWith(s, len, "μαλλ")
        || endsWith(s, len, "κυματ")
        || endsWith(s, len, "λαχ")
        || endsWith(s, len, "ληγ")
        || endsWith(s, len, "φαγ")
        || endsWith(s, len, "ομ")
        || endsWith(s, len, "πρωτ"))) {
      len += 3; // add back -ουσ
    }

   return len;
  }

  private static final CharArraySet exc15a = new CharArraySet(Lucene.VERSION,
      Arrays.asList("αβαστ", "πολυφ", "αδηφ", "παμφ", "ρ", "ασπ", "αφ", "αμαλ",
          "αμαλλι", "ανυστ", "απερ", "ασπαρ", "αχαρ", "δερβεν", "δροσοπ",
          "ξεφ", "νεοπ", "νομοτ", "ολοπ", "ομοτ", "προστ", "προσωποπ", "συμπ",
          "συντ", "τ", "υποτ", "χαρ", "αειπ", "αιμοστ", "ανυπ", "αποτ",
          "αρτιπ", "διατ", "εν", "επιτ", "κροκαλοπ", "σιδηροπ", "λ", "ναυ",
          "ουλαμ", "ουρ", "π", "τρ", "μ"),
      false);

  private static final CharArraySet exc15b = new CharArraySet(Lucene.VERSION,
      Arrays.asList("ψοφ", "ναυλοχ"),
      false);

  private int rule15(char s[], int len) {
    boolean removed = false;
    if (len > 4 && endsWith(s, len, "αγεσ")) {
      len -= 4;
      removed = true;
    } else if (len > 3 && (endsWith(s, len, "αγα") || endsWith(s, len, "αγε"))) {
      len -= 3;
      removed = true;
    }

    if (removed) {
      final boolean cond1 = exc15a.contains(s, 0, len)
        || endsWith(s, len, "οφ")
        || endsWith(s, len, "πελ")
        || endsWith(s, len, "χορτ")
        || endsWith(s, len, "λλ")
        || endsWith(s, len, "σφ")
        || endsWith(s, len, "ρπ")
        || endsWith(s, len, "φρ")
        || endsWith(s, len, "πρ")
        || endsWith(s, len, "λοχ")
        || endsWith(s, len, "σμην");

      final boolean cond2 = exc15b.contains(s, 0, len)
        || endsWith(s, len, "κολλ");

      if (cond1 && !cond2)
        len += 2; // add back -αγ
    }

    return len;
  }

  private static final CharArraySet exc16 = new CharArraySet(Lucene.VERSION,
      Arrays.asList("ν", "χερσον", "δωδεκαν", "ερημον", "μεγαλον", "επταν", "ι"),
      false);

  private int rule16(char s[], int len) {
    boolean removed = false;
    if (len > 4 && endsWith(s, len, "ησου")) {
      len -= 4;
      removed = true;
    } else if (len > 3 && (endsWith(s, len, "ησε") || endsWith(s, len, "ησα"))) {
      len -= 3;
      removed = true;
    }

    if (removed && exc16.contains(s, 0, len))
      len += 2; // add back -ησ

    return len;
  }

  private static final CharArraySet exc17 = new CharArraySet(Lucene.VERSION,
      Arrays.asList("ασβ", "σβ", "αχρ", "χρ", "απλ", "αειμν", "δυσχρ", "ευχρ", "κοινοχρ", "παλιμψ"),
      false);

  private int rule17(char s[], int len) {
    if (len > 4 && endsWith(s, len, "ηστε")) {
      len -= 4;
      if (exc17.contains(s, 0, len))
        len += 3; // add back the -ηστ
    }

    return len;
  }

  private static final CharArraySet exc18 = new CharArraySet(Lucene.VERSION,
      Arrays.asList("ν", "ρ", "σπι", "στραβομουτσ", "κακομουτσ", "εξων"),
      false);

  private int rule18(char s[], int len) {
    boolean removed = false;

    if (len > 6 && (endsWith(s, len, "ησουνε") || endsWith(s, len, "ηθουνε"))) {
      len -= 6;
      removed = true;
    } else if (len > 4 && endsWith(s, len, "ουνε")) {
      len -= 4;
      removed = true;
    }

    if (removed && exc18.contains(s, 0, len)) {
      len += 3;
      s[len - 3] = 'ο';
      s[len - 2] = 'υ';
      s[len - 1] = 'ν';
    }
    return len;
  }

  private static final CharArraySet exc19 = new CharArraySet(Lucene.VERSION,
      Arrays.asList("παρασουσ", "φ", "χ", "ωριοπλ", "αζ", "αλλοσουσ", "ασουσ"),
      false);

  private int rule19(char s[], int len) {
    boolean removed = false;

    if (len > 6 && (endsWith(s, len, "ησουμε") || endsWith(s, len, "ηθουμε"))) {
      len -= 6;
      removed = true;
    } else if (len > 4 && endsWith(s, len, "ουμε")) {
      len -= 4;
      removed = true;
    }

    if (removed && exc19.contains(s, 0, len)) {
      len += 3;
      s[len - 3] = 'ο';
      s[len - 2] = 'υ';
      s[len - 1] = 'μ';
    }
    return len;
  }

  private static final CharArraySet exc20a = new CharArraySet(Lucene.VERSION,
      Arrays.asList("γραμμ"), false);

  private static final CharArraySet exc20b = new CharArraySet(Lucene.VERSION,
      Arrays.asList("γεμ", "σταμ"), false);


  private int rule20(char s[], int len) {
    boolean removed = false;
    if (len > 6 && endsWith(s, len, "ματουσ")) {
      len -= 5;
      removed = true;
    } else if (len > 5 && (endsWith(s, len, "ματων") ||
        endsWith(s, len, "ματοσ") ||
        endsWith(s, len, "ματωσ") ||
        endsWith(s, len, "ματου") ||
        endsWith(s, len, "ματησ") ||
        endsWith(s, len, "ματεσ") ||
        endsWith(s, len, "ματοι"))) {
      len -= 4;
      removed = true;
    } else if (len > 4 && (endsWith(s, len, "ματα") ||
        endsWith(s, len, "ματο") ||
        endsWith(s, len, "ματη"))) {
      len -= 3;
      removed = true;
    }

    if (removed)
      if (exc20a.contains(s, 0, len)) {
      // add -α
      len += 1;
      s[len - 1] = 'α';
    } else if (exc20b.contains(s, 0, len)) {
      len += 2; // add -ατ
    }

    return len;
  }

  private int rule21(char s[], int len) {
    if (len > 3 && endsWith(s, len, "ουα"))
      return len - 1;

    return len;
  }

  private int rule22(char s[], int len) {
    if (len > 9 && endsWith(s, len, "ιοντουσαν"))
      return len - 9;

    if (len > 8 && (endsWith(s, len, "ιομασταν") ||
        endsWith(s, len, "ιοσασταν") ||
        endsWith(s, len, "ιουμαστε") ||
        endsWith(s, len, "οντουσαν")))
      return len - 8;

    if (len > 7 && (endsWith(s, len, "ιεμαστε") ||
        endsWith(s, len, "ιεσαστε") ||
        endsWith(s, len, "ιομουνα") ||
        endsWith(s, len, "ιοσαστε") ||
        endsWith(s, len, "ιοσουνα") ||
        endsWith(s, len, "ιουνται") ||
        endsWith(s, len, "ιουνταν") ||
        endsWith(s, len, "ηθηκατε") ||
        endsWith(s, len, "ομασταν") ||
        endsWith(s, len, "οσασταν") ||
        endsWith(s, len, "ουμαστε")))
      return len - 7;

    if (len > 6 && (endsWith(s, len, "ιομουν") ||
        endsWith(s, len, "ιονταν") ||
        endsWith(s, len, "ιοσουν") ||
        endsWith(s, len, "ηθειτε") ||
        endsWith(s, len, "ηθηκαν") ||
        endsWith(s, len, "ομουνα") ||
        endsWith(s, len, "οσαστε") ||
        endsWith(s, len, "οσουνα") ||
        endsWith(s, len, "ουνται") ||
        endsWith(s, len, "ουνταν") ||
        endsWith(s, len, "ουσατε")))
      return len - 6;

    if (len > 5 && (endsWith(s, len, "αγατε") ||
        endsWith(s, len, "ιεμαι") ||
        endsWith(s, len, "ιεται") ||
        endsWith(s, len, "ιεσαι") ||
        endsWith(s, len, "ιοταν") ||
        endsWith(s, len, "ιουμα") ||
        endsWith(s, len, "ηθεισ") ||
        endsWith(s, len, "ηθουν") ||
        endsWith(s, len, "ηκατε") ||
        endsWith(s, len, "ησατε") ||
        endsWith(s, len, "ησουν") ||
        endsWith(s, len, "ομουν") ||
        endsWith(s, len, "ονται") ||
        endsWith(s, len, "ονταν") ||
        endsWith(s, len, "οσουν") ||
        endsWith(s, len, "ουμαι") ||
        endsWith(s, len, "ουσαν")))
      return len - 5;

    if (len > 4 && (endsWith(s, len, "αγαν") ||
        endsWith(s, len, "αμαι") ||
        endsWith(s, len, "ασαι") ||
        endsWith(s, len, "αται") ||
        endsWith(s, len, "ειτε") ||
        endsWith(s, len, "εσαι") ||
        endsWith(s, len, "εται") ||
        endsWith(s, len, "ηδεσ") ||
        endsWith(s, len, "ηδων") ||
        endsWith(s, len, "ηθει") ||
        endsWith(s, len, "ηκαν") ||
        endsWith(s, len, "ησαν") ||
        endsWith(s, len, "ησει") ||
        endsWith(s, len, "ησεσ") ||
        endsWith(s, len, "ομαι") ||
        endsWith(s, len, "οταν")))
      return len - 4;

    if (len > 3 && (endsWith(s, len, "αει") ||
        endsWith(s, len, "εισ") ||
        endsWith(s, len, "ηθω") ||
        endsWith(s, len, "ησω") ||
        endsWith(s, len, "ουν") ||
        endsWith(s, len, "οισ") ||
        endsWith(s, len, "ουσ")))
      return len - 3;

    if (len > 2 && (endsWith(s, len, "αν") ||
        endsWith(s, len, "ασ") ||
        endsWith(s, len, "αω") ||
        endsWith(s, len, "ει") ||
        endsWith(s, len, "εσ") ||
        endsWith(s, len, "ησ") ||
        endsWith(s, len, "οι") ||
        endsWith(s, len, "οσ") ||
        endsWith(s, len, "ου") ||
        endsWith(s, len, "υα") ||
        endsWith(s, len, "υσ") ||
        endsWith(s, len, "ων")))
      return len - 2;

    if (len > 1 && endsWithVowel(s, len))
      return len - 1;

    return len;
  }

  private static final CharArraySet exc23a = new CharArraySet(Lucene.VERSION,
      Arrays.asList("εξ", "εσ", "κατ", "αν", "κ", "μ", "πρ"), false);

  private static final CharArraySet exc23b = new CharArraySet(Lucene.VERSION,
      Arrays.asList("κα", "μ", "λε", "ελε", "δε"), false);


  private int rule23(char s[], int len) {
    boolean removed = false;
    if (endsWith(s, len, "εστερ") ||
        endsWith(s, len, "εστατ"))
      return len - 5;

    if (endsWith(s, len, "οτερ") ||
        endsWith(s, len, "οτατ") ||
        endsWith(s, len, "υτερ") ||
        endsWith(s, len, "υτατ") ||
        endsWith(s, len, "ωτερ") ||
        endsWith(s, len, "ωτατ")) {
      len -= 4;
      removed = true;
    }

    if (removed) {
      if (exc23a.contains(s, 0, len)) {
        len += 4;
      }else if (exc23b.contains(s, 0, len)) {
        len += 2;
        s[len - 2] = 'υ';
        s[len - 1] = 'τ';
      }
      return len;
    }

    return len;
  }

  private boolean endsWith(char s[], int len, String suffix) {
    final int suffixLen = suffix.length();
    if (suffixLen > len)
      return false;
    for (int i = suffixLen - 1; i >= 0; i--)
      if (s[len -(suffixLen - i)] != suffix.charAt(i))
        return false;

    return true;
  }

  private boolean endsWithVowel(char s[], int len) {
    if (len == 0)
      return false;
    switch(s[len - 1]) {
      case 'α':
      case 'ε':
      case 'η':
      case 'ι':
      case 'ο':
      case 'υ':
      case 'ω':
        return true;
      default:
        return false;
    }
  }

  private boolean endsWithVowelNoY(char s[], int len) {
    if (len == 0)
      return false;
    switch(s[len - 1]) {
      case 'α':
      case 'ε':
      case 'η':
      case 'ι':
      case 'ο':
      case 'ω':
        return true;
      default:
        return false;
    }
  }

  /**
   * Creates a CharArraySet from a file.
   *
   * @param stopwords
   *          Input stream from the stopwords file
   *
   * @param matchVersion
   *          the Lucene version for cross version compatibility
   * @return a CharArraySet containing the distinct stopwords from the given
   *         file
   * @throws IOException
   *           if loading the stopwords throws an {@link IOException}
   */
  private static CharArraySet loadStopwordSet(InputStream stopwords,
      Version matchVersion) throws IOException {
    Reader reader = null;
    try {
      reader = IOUtils.getDecodingReader(stopwords, IOUtils.CHARSET_UTF_8);
      return WordlistLoader.getWordSet(reader, matchVersion);
    } finally {
      IOUtils.close(reader);
    }
  }
}
