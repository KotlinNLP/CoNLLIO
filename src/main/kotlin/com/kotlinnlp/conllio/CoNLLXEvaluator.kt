/* Copyright 2016-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.conllio

import com.kotlinnlp.conllio.utils.runAsCommand
import java.io.File
import java.nio.file.Paths

/**
 * The CoNLLXEvaluator runs the perl script "eval.pl" (the script used during the CoNLL 2006 Shared Task).
 */
object CoNLLXEvaluator : CorpusEvaluator {

  /**
   * The relative path of the external script.
   */
  private val SCRIPT_RELATIVE_PATH: String = Paths.get("evaluation_scripts", "perl", "eval.pl").toString()

  /**
   * The absolute path of the external script.
   */
  private val SCRIPT_ABSOLUTE_PATH: String = File(
    Thread.currentThread().contextClassLoader.getResource(SCRIPT_RELATIVE_PATH).file).absolutePath

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

    return "perl $SCRIPT_ABSOLUTE_PATH -q -g $goldFilePath -s $systemFilePath".runAsCommand()
  }
}
