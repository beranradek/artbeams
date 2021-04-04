package org.xbery.artbeams.common.text

import java.text.Normalizer
import java.nio.charset.StandardCharsets

/**
  * @author Radek Beran
  */
class NormalizationHelper {

  /**
    * Normalizuje retezec pro vyhledavani - odstrani diakritiku a nealfanumericke znaky
    *
    * @param str
    * @return
    */
  def normalize(str: String): String = {
    if (str == null || str.isEmpty) return str
    val sb = new StringBuilder
    for (c <- str.toCharArray) {
      if (Character.isLetterOrDigit(c)) sb.append(c)
    }
    toAscii(removeDiacriticalMarks(sb.toString)).toLowerCase
  }

  /**
    * Prevede retezec na slug vhodny pro pouziti v URL adrese.
    *
    * @param str
    * @return
    */
  def toSlug(str: String): String = {
    if (str == null || str.isEmpty) return str
    val sb = new StringBuilder
    for (c <- str.toCharArray) {
      if (' ' == c) sb.append('-')
      else if (Character.isLetterOrDigit(c)) sb.append(c)
    }
    toAscii(removeDiacriticalMarks(sb.toString)).toLowerCase
  }

  /**
    * Normalizuje retezec jako jmeno vhodne pro filesystem
    *
    * @param str
    * @return
    */
  def normalizeForFilesystem(str: String): String = {
    if (str == null || str.isEmpty) return str

    // Odebrani specialnich filesystem znaku
    val str2 = str.replaceAll("[<>:\"/\\\\\\|\\?\\*]", "")
    // Odebrani diakritiky
    removeDiacriticalMarks(str2)
  }

  def toAscii(str: String): String = {
    val b = str.getBytes(StandardCharsets.US_ASCII)
    new String(b, StandardCharsets.UTF_8)
  }

  def removeDiacriticalMarks(text: String): String = {
    val s = java.text.Normalizer.normalize(text, Normalizer.Form.NFD)
    s.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
  }
}
