/* Copyright 2016-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.conllio

import com.kotlinnlp.linguisticdescription.Deprel
import com.kotlinnlp.linguisticdescription.POSTag
import com.kotlinnlp.linguisticdescription.sentence.token.FormToken

/**
 * The Token.
 *
 * @property id the token id, an incremental integer starting from 1 within a sentence
 * @property form the token/word form or punctuation symbol
 * @property lemma the lemma or stem of word form
 * @property pos the part-of-speech tag (UPOSTAG in CoNLL-U)
 * @property pos2 a more detailed part-of-speech tag (XPOSTAG in CoNLL-U)
 * @property feats a list of morphological features (they could also be syntactic or semantic)
 * @property head the Head of the current word, which is either a value of ID or zero (0).
 *                It can be null if not annotated.
 * @property deprel the deprel
 * @property multiWord the multi-word token information in case of multi-word tokens
 * @property lineNumber the line number of the [Token] in the tree-bank
 */
data class Token(
  val id: Int,
  override val form: String,
  val lemma: String,
  val pos: POSTag,
  val pos2: POSTag,
  val feats: Map<String, String>,
  val head: Int?,
  val deprel: Deprel,
  val multiWord: MultiWord? = null,
  val lineNumber: Int = 0
) : FormToken {

  /**
   * Exception in case of invalid Token Id.
   */
  class InvalidTokenId(message: String?) : Throwable(message)

  /**
   * Exception in case of invalid Token Head.
   */
  class InvalidTokenHead(message: String?) : Throwable(message)

  /**
   * Exception in case of invalid Token Form.
   */
  class InvalidTokenForm(message: String?) : Throwable(message)

  /**
   * Exception in case of invalid POS.
   */
  class InvalidTokenPOS(message: String?) : Throwable(message)

  companion object {
    /**
     * The empty filler is used to denote unspecified values in all fields except ID.
     * In CoNLL the empty filler is the Underscore (_) character.
     *
     * Note that no format-level distinction is made for the rare cases where the FORM or LEMMA is the underscore.
     */
    const val emptyFiller = "_"
  }

  /**
   * True if the token has a not-null [multiWord].
   */
  val isMultiWordToken: Boolean get() = this.multiWord != null

  /**
   * True if the token has a not-null [multiWord] and is the first of the range.
   */
  private val isFirstMultiWordToken: Boolean
    get() = this.multiWord != null && this.id == this.multiWord.range.first

  /**
   * @return a multi-word token line in CoNLL format (Id-range, FORM value and underscore in all the remaining fields).
   */
  private fun buildMultiWordHeadline(): String? {

    return if (this.multiWord != null && this.isFirstMultiWordToken) {

      listOf(
        this.multiWord.getCoNLLRange(),
        this.multiWord.form,
        (emptyFiller + "\t").repeat(8).trim('\t') // remove last tab
      ).joinToString("\t")

    } else {
      null
    }
  }

  /**
   * Perform basic properties check (positive id, head > 0 if not null, form and pos not empty).
   */
  init {

    if (this.id < 1)
      throw InvalidTokenId("[Line ${this.lineNumber}] Negative ID")

    if (this.head != null) {
      if (this.head < 0) throw InvalidTokenHead("[Line ${this.lineNumber}] Negative head")
      if (this.head == this.id) throw InvalidTokenHead("[Line ${this.lineNumber}] Head equal to the token ID")
    }

    if (this.form.trim().isEmpty())
      throw InvalidTokenForm("[Line ${this.lineNumber}] Empty form")

    if (this.pos.labels.any { it.trim().isEmpty() })
      throw InvalidTokenPOS("[Line ${this.lineNumber}] Empty POS")
  }

  /**
   * @return the CoNLL string representation of this token (with the addition of a possible head line in the case that
   * this token is the first of a sequence of multi-words tokens)
   */
  fun toCoNLLString(): String {

    val headline: String = this.buildMultiWordHeadline()?.plus("\n") ?: ""

    return headline + listOf(
      this.id.toString(),
      this.form,
      this.lemma,
      this.pos,
      this.pos2,
      this.featsToCoNLL(),
      this.head?.toString() ?: emptyFiller,
      this.deprel,
      emptyFiller,
      emptyFiller).joinToString("\t")
  }

  /**
   * @return the CoNLL string representation of the features of this token
   */
  private fun featsToCoNLL() = if (this.feats.isEmpty()) {
    emptyFiller
  } else {
    this.feats.map { "${it.key}=${it.value}" }.joinToString("|")
  }
}
