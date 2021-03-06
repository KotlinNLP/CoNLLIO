/* Copyright 2016-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.conllio

import com.kotlinnlp.conllio.utils.runAsCommand
import com.kotlinnlp.utils.getResourceAsStream
import java.io.File
import java.nio.file.Paths

/**
 * The CoNLLXEvaluator runs the perl script "eval.pl" (the script used during the CoNLL 2006 Shared Task).
 */
object CoNLLXEvaluator : CorpusEvaluator {

  /**
   * The code of the evaluation script.
   */
  private val SCRIPT_CODE: String = getResourceAsStream("evaluation_scripts/perl/eval.pl").reader().readText()

  /**
   * Evaluate the system output against a gold standard.
   *
   * @param systemFilePath the file path of the system output
   * @param goldFilePath the file path of the gold standard
   *
   * @return the output of the evaluation script (can be null)
   */
  override fun evaluate(systemFilePath: String, goldFilePath: String): String? {

    require(File(systemFilePath).exists()) { "File $systemFilePath not found." }
    require(File(goldFilePath).exists()) { "File $goldFilePath not found." }

    val timestamp: Long = System.currentTimeMillis()
    val scriptFilename: String = Paths.get("/", "tmp", "conll_eval_$timestamp.pl").toString()

    File(scriptFilename).writeText(this.SCRIPT_CODE)

    val command = "perl $scriptFilename -q -g $goldFilePath -s $systemFilePath"
    val result = command.runAsCommand()

    File(scriptFilename).delete()

    require(result != null){ "No feedback for command $command " }

    return result
  }
}
