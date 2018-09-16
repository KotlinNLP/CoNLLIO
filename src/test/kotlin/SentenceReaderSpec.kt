/* Copyright 2016-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

import com.kotlinnlp.conllio.SentenceReader
import com.kotlinnlp.conllio.Token
import com.kotlinnlp.linguisticdescription.Deprel
import com.kotlinnlp.linguisticdescription.POSTag
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertTrue

class SentenceReaderSpec: Spek({

  describe("SentenceReader") {

    on("readSentence()") {

      val buffer = ArrayList<Pair<Int, String>>()
      buffer.add(Pair(0, "1\tthe\tthe\tdet\t_\t_\t2\tdet\t_\t_"))
      buffer.add(Pair(1, "2\tdogs\tdog\tnoun\t_\t_\t0\troot\t_\t_"))

      val sentence = SentenceReader(buffer).readSentence()

      it("should create the expected tokens at index 0") {

        assertTrue(sentence.tokens[0] ==
            Token(
              id = 1,
              form = "the",
              lemma = "the",
              pos = POSTag(labels = listOf("det")),
              pos2 = POSTag(labels = listOf("_")),
              feats = mapOf(),
              head = 2,
              deprel = Deprel(labels = listOf("det")),
              lineNumber = 0))
      }

      it("should create the expected tokens at index 1") {

        assertTrue(sentence.tokens[1] ==
          Token(
            id = 2,
            form = "dogs",
            lemma = "dog",
            pos = POSTag(labels = listOf("noun")),
            pos2 = POSTag(labels =listOf("_")),
            feats = mapOf(),
            head = 0,
            deprel = Deprel(labels = listOf("root")),
            lineNumber = 1))
      }
    }
  }
})
