/* Copyright 2016-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.conllio

import java.io.File
import kotlin.coroutines.experimental.buildSequence

/**
 * The CoNLLReader is designed to read CoNLL-style data format, turning them into a list of [Sentence]s.
 *
 * It is possible to read the CoNLL-X data format used in the CoNLL 2006 Shared Task and the CoNLL-U data 
 * format used in the CoNLL 2017 Shared Task.
 *
 * In a few words, it expects data encoded in plain text files (UTF-8) with three types of lines:
 * - Word lines containing the annotation of a word/token.
 * - Blank lines marking sentence boundaries.
 * - Comment lines starting with hash (#).
 */
object CoNLLReader {

  /**
   *
   * @param filePath
   *
   * @return sequence of [Sentence]s
   */
  fun fromFile(filePath: String): Sequence<Sentence> {

    val file = File(filePath)

    require(file.exists()) { "File $file not found."}

    return this.fromFile(file)
  }

  /**
   *
   * @param file the CoNLL file to read.
   *
   * @return sequence of [Sentence]s.
   */
  fun fromFile(file: File): Sequence<Sentence> = this.fromText(file.readText(charset = Charsets.UTF_8))

  /**
   * Read a text in CoNLL-style format and return a sequence of [Sentence]s.
   *
   * @param text in CoNLL-style format.
   *
   * @return sequence of [Sentence]s.
   */
  fun fromText(text: String) = buildSequence {

    val lines = text.split('\n')
    val buffer = ArrayList<Pair<Int, String>>()

    lines.withIndex().forEach { (i, line) ->

      if (line.isSentenceBoundary() && buffer.isNotEmpty()) {

        val sentence = SentenceReader(buffer).readSentence()

        buffer.clear()

        yield(sentence)

      } else {
        buffer.add(Pair(i, line))
      }
    }
  }

  /**
   * Blank lines marking sentence boundaries.
   *
   * @return True if the string (after trim) is empty.
   */
  private fun String.isSentenceBoundary(): Boolean = this.trim().isEmpty()
}