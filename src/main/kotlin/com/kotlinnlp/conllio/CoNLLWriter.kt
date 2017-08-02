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
 * The CoNLLWriter.
 */
object CoNLLWriter {

  /**
   * Transform a list of [Sentence]s in the CoNLL-style format.
   *
   * @param sentences sequence of [Sentence]s.
   *
   * @return sequence of sentences represented in CoNLL-U format.
   */
  fun toText(sentences: List<Sentence>, writeComments: Boolean) = buildSequence {
    sentences.forEach { yield(it.toCoNLL(writeComments = writeComments)) }
  }

  /**
   * Write a list of [Sentence]s in the CoNLL-style format to the file [outputFilePath].
   *
   * @param outputFilePath the output file path.
   *
   * @param sentences sequence of [Sentence]s to write.
   */
  fun toFile(sentences: List<Sentence>, writeComments: Boolean, outputFilePath: String) {

    File(outputFilePath).printWriter().use { out ->
      this.toText(sentences, writeComments = writeComments).forEach {
        out.write("$it\n\n")
      }
    }
  }
}
