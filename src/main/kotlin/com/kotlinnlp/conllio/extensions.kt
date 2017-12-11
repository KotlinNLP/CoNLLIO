/* Copyright 2016-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.conllio

/**
 * Extract an attribute-value pair from a String, using a [separator] to separate the attribute from the value.
 *
 * @param separator the separator used to separate the attribute from the value, e.g. equals sign (=)
 *
 * @return a <attribute, value> Pair
 */
fun String.extractPair(separator: Char): Pair<String, String> {
  require(this.contains(separator))
  return Pair(this.substringBefore(separator).trim(), this.substringAfter(separator).trim())
}

/**
 * @return a <Int, Int> Pair
 */
fun Pair<String, String>.toIntPair() = Pair(this.first.toInt(), this.second.toInt())
