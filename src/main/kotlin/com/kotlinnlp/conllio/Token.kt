/* Copyright 2016-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.conllio

/**
 * The Token.
 *
 * @property id the token/word index, integer starting at 1 for each new sentence.
 * @property form the token/word form or punctuation symbol.
 * @property lemma the lemma or stem of word form.
 * @property pos the part-of-speech tag (UPOSTAG in CoNLL-U)
 * @property pos2 a more detailed part-of-speech tag (XPOSTAG in CoNLL-U)
 * @property feats a list of morphological features (they could also be syntactic or semantic)
 * @property head the Head of the current word, which is either a value of ID or zero (0).
 * @property deprel the dependency relation to the HEAD
 * @property multiWordForm the original form that occurs in the sentence in case of multi-word tokens
 *                         (assigned to the first token only)
 * @property multiWordRange the id-range to which this token belongs in case of multi-word tokens.
 *                          Multi-word tokens are indexed with integer ranges like 1-2 or 3-5
 * @property lineNumber the line number of the [Token] in the tree-bank
 */
data class Token(
  val id: Int,
  val form: String,
  val lemma: String,
  val pos: String,
  val pos2: String,
  val feats: Map<String, String>,
  val head: Int?,
  val deprel: String,
  val multiWordForm: String? = null,
  val multiWordRange: IntRange? = null,
  val lineNumber: Int = 0
){

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
    val emptyFiller = "_"
  }

  /**
   * True if the token has a not-null [multiWordRange].
   */
  val isMultiWordToken: Boolean get() = this.multiWordRange != null

  /**
   * True if the token has a not-null [multiWordRange] and is the first of the range.
   */
  private val isFirstMultiWordToken: Boolean
    get() = this.multiWordRange != null && this.id == this.multiWordRange.first

  /**
   * @return a multi-word token line in CoNLL format (Id-range, FORM value and underscore in all the remaining fields).
   */
  private fun buildMultiWordHeadline(): String? = if (this.isMultiWordToken && this.isFirstMultiWordToken) {

    listOf(
      this.multiWordRange!!.toCoNLLRange(),
      this.multiWordForm!!,
      (emptyFiller + "\t").repeat(8).trim('\t') // remove last tab
    ).joinToString("\t")

  } else {
    null
  }

  /**
   * Perform basic properties check (positive id, head > 0 if not null, form and pos not empty)
   */
  init {
    if (this.id < 1) throw InvalidTokenId("Line ${this.lineNumber}")
    if (this.head != null && (this.head < 0 || this.head == this.id)) throw InvalidTokenHead("Line ${this.lineNumber}")
    if (this.form.trim().isEmpty()) throw InvalidTokenForm("Line ${this.lineNumber}")
    if (this.pos.trim().isEmpty()) throw InvalidTokenPOS("Line ${this.lineNumber}")
  }

  /**
   * @return the CoNLL string representation of this token (with the addition of a possible headline
   * in the case this token is the first of a sequence of multi-words tokens).
   */
  fun toCoNLL(): String {

    val multiWordHeadline: String? = this.buildMultiWordHeadline()

    val headline = if (multiWordHeadline != null)  multiWordHeadline + "\n" else ""

    return headline + listOf(
      this.id,
      this.form,
      this.lemma,
      this.pos,
      this.pos2,
      this.featsToCoNLL(),
      this.head ?: emptyFiller,
      this.deprel,
      emptyFiller,
      emptyFiller).joinToString("\t")
  }

  /**
   * @return the CoNLL string representation of the features of this token.
   */
  private fun featsToCoNLL() = if (this.feats.isEmpty()) {
    emptyFiller
  } else {
    this.feats.map { "${it.key}=${it.value}" }.joinToString("|")
  }

  /**
   * @return a string of the format first-last id (e.g. 1-2 or 3-5)
   */
  private fun IntRange.toCoNLLRange(): String = "${this.first}-${this.last}"
}
