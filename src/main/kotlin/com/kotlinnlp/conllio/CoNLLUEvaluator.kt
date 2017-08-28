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
 * The CoNLLUEvaluator runs the python script "conll17_ud_eval.py" (the script used during the CoNLL 2017 Shared Task).
 */
object CoNLLUEvaluator : CorpusEvaluator {

  /**
   * The relative path of the external script.
   */
  private val SCRIPT_RELATIVE_PATH: String = Paths.get("evaluation_scripts", "python", "conll17_ud_eval.py").toString()

  /**
   * The absolute path of the external script.
   */
  private val SCRIPT_CODE: String =
    Thread.currentThread().contextClassLoader.getResource(SCRIPT_RELATIVE_PATH).readText()

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

    File("/tmp/conll17_ud_eval.py").writeText(SCRIPT_CODE)

    val command = "python /tmp/conll17_ud_eval.py -v $goldFilePath $systemFilePath"
    val result = command.runAsCommand()

    File("/tmp/conll17_ud_eval.py").delete()

    require(result != null){ "No feedback for command $command " }

    return result
  }
}
