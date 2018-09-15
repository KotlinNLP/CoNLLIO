/* Copyright 2016-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

import com.kotlinnlp.conllio.Token
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * TokenSpec Tests Class.
 */
object TokenSpec : Spek({

  describe("Token") {

    on("Constructor") {

      it("should raise an exception in case the id is 0") {
        assertFailsWith<Token.InvalidTokenId> {
          Token(
            id = 0,
            form = Token.emptyFiller,
            lemma = Token.emptyFiller,
            pos = Token.emptyFiller,
            pos2 = Token.emptyFiller,
            feats = mapOf(),
            head = 0,
            deprel = Token.emptyFiller,
            lineNumber = 0)
        }
      }

      it("should raise an exception in case the head < 0") {
        assertFailsWith<Token.InvalidTokenHead> {
          Token(
            id = 1,
            form = Token.emptyFiller,
            lemma = Token.emptyFiller,
            pos = Token.emptyFiller,
            pos2 = Token.emptyFiller,
            feats = mapOf(),
            head = -1,
            deprel = Token.emptyFiller,
            lineNumber = 0)
        }
      }

      it("should raise an exception in case the head == id") {
        assertFailsWith<Token.InvalidTokenHead> {
          Token(
            id = 1,
            form = Token.emptyFiller,
            lemma = Token.emptyFiller,
            pos = Token.emptyFiller,
            pos2 = Token.emptyFiller,
            feats = mapOf(),
            head = 1,
            deprel = Token.emptyFiller,
            lineNumber = 0)
        }
      }

      it("should raise an exception in case the form is empty") {
        assertFailsWith<Token.InvalidTokenForm> {
          Token(
            id = 1,
            form = "",
            lemma = Token.emptyFiller,
            pos = Token.emptyFiller,
            pos2 = Token.emptyFiller,
            feats = mapOf(),
            head = 0,
            deprel = Token.emptyFiller,
            lineNumber = 0)
        }
      }

      it("should raise an exception in case the pos is empty") {
        assertFailsWith<Token.InvalidTokenPOS> {
          Token(
            id = 1,
            form = Token.emptyFiller,
            lemma = Token.emptyFiller,
            pos = "",
            pos2 = Token.emptyFiller,
            feats = mapOf(),
            head = 0,
            deprel = Token.emptyFiller,
            lineNumber = 0)
        }
      }
    }

    on("toCoNLLString()") {

      it("should return the expected CoNLL line") {
        assertEquals(
          "1\tdogs\tdog\tnoun\t_\t_\t0\troot\t_\t_",
          Token(
            id = 1,
            form = "dogs",
            lemma = "dog",
            pos = "noun",
            pos2 = Token.emptyFiller,
            feats = mapOf(),
            head = 0,
            deprel = "root",
            lineNumber = 0).toCoNLLString())
      }
    }
  }
})
