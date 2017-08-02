/* Copyright 2016-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.conllio

import com.kotlinnlp.conllio.treeutils.isNonProjectiveTree
import com.kotlinnlp.conllio.treeutils.isSingleRoot
import com.kotlinnlp.conllio.treeutils.isTree

/**
 * The Sentence.
 *
 * @property sentenceId A tree-bank-wide unique sentence id (can be "")
 * @property text the original (pre-tokenization) text (can be "")
 * @property tokens an array of [Token] (cannot be empty)
 */
class Sentence(
  val sentenceId: String,
  val text: String,
  val tokens: Array<Token>){

  init { require(this.tokens.isNotEmpty()) { "A Sentence requires at least one Token." } }

  /**
   * Exception in case of invalid tree.
   */
  class InvalidTree(message: String?) : Throwable(message)

  /**
   * A range defined by the first token line and the last token line in the CoNLL corpus.
   */
  val corpusLinesRange = this.tokens.first().lineNumber .. this.tokens.last().lineNumber

  /**
   * The list of heads for each token.
   *
   * The head are decreased by one (null if the value 0) to fit the format required by the tree utils package.
   */
  private val heads by lazy { require(this.hasAnnotatedHeads()) { "Require annotated heads"}
    Array(size = this.tokens.size, init = { i ->
      if (this.tokens[i].head == 0) null else this.tokens[i].head!! - 1
    })
  }

  /**
   * @return True if all the [tokens] have not null heads.
   */
  fun hasAnnotatedHeads(): Boolean = this.tokens.none { it.head == null }

  /**
   * @return True if the tree represented by the [heads] is non-projective.
   */
  fun isNonProjective(): Boolean = this.heads.isNonProjectiveTree()

  /**
   * @return True if the tree represented by the [heads] constitute a valid tree.
   */
  fun isTree(): Boolean = this.heads.isTree()

  /**
   * @return True if the tree represented by the [heads] is single root
   */
  fun isSingleRoot(): Boolean = this.heads.isSingleRoot()

  /**
   * Check if the sentence has a valid tree structure and raises an exception otherwise.
   *
   * @param requireSingleRoot check if the tree has one single root.
   */
  fun assertValidCoNLLTree(requireSingleRoot: Boolean = true) {
    if (!this.heads.isTree()) {
      throw InvalidTree("Invalid CoNLL: Not a Tree: Lines ${corpusLinesRange.start} .. ${corpusLinesRange.endInclusive}")
    }
    if (requireSingleRoot && !this.heads.isSingleRoot()) {
      throw InvalidTree("Invalid CoNLL: Multiple Roots: Lines ${corpusLinesRange.start} .. ${corpusLinesRange.endInclusive}")
    }
  }

  /**
   * @param writeComments whether to write the sentence comments.
   *
   * @return the CoNLL string representation of this sentence.
   */
  fun toCoNLL(writeComments: Boolean): String {
    val comments: String = if (writeComments) {
      "# sent_id = ${this.sentenceId}\n# text = ${this.text}\n"
    } else {
      ""
    }

    return comments + this.tokens.map { it.toCoNLL() }.joinToString("\n")
  }
}