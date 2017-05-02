package org.elasticsearch.index.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.lucene.analysis.WordlistLoader;

public class UpdateStemmingSamples {
  private final static SkroutzGreekStemmer stemmer = new SkroutzGreekStemmer();

  public static void main(String args[])
      throws java.io.IOException
  {
    List<String> lines = WordlistLoader.getLines(
        new FileInputStream("src/test/resources/stemming_samples.txt"),
        StandardCharsets.UTF_8);

    char[] token;
    int tokenLength, stemLength;
    String stem;
    File file = new File("src/test/resources/stemming_samples.txt");
    FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
    BufferedWriter writer = new BufferedWriter(fileWriter);

    try {
      for(String line : lines) {
        String[] sample =  line.split(",");
        token = sample[0].toCharArray();
        tokenLength = sample[0].length();
        stemLength = stemmer.stem(token, tokenLength);
        stem = new String(token, 0, stemLength);
        writer.write(sample[0] + "," + stem);
        writer.newLine();
      }
    } finally {
      writer.close();
    }
  }
}
