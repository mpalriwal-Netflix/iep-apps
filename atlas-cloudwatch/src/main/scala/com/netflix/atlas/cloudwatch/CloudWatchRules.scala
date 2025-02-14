/*
 * Copyright 2014-2025 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.atlas.cloudwatch

import com.typesafe.config.Config

/**
  * Compiles to configurations into a lookup map based on namespace and metric.
  *
  * @param config
  *     The non-null config to load the rules from.
  */
class CloudWatchRules(config: Config) {

  private val nsMap = {
    //                      ns          metric                       definitions to apply to metric
    var ruleMap = Map.empty[String, Map[String, List[(MetricCategory, List[MetricDefinition])]]]
    getCategories(config).foreach { category =>
      category.metrics.foreach { metricDef =>
        var inner = ruleMap.getOrElse(
          category.namespace,
          Map.empty[String, List[(MetricCategory, List[MetricDefinition])]]
        )
        // val (_, list) = inner.getOrElse(m.name, List((c, List.empty[MetricDefinition])))
        var entry =
          inner.getOrElse(metricDef.name, List.empty[(MetricCategory, List[MetricDefinition])])

        var updated = false
        for (i <- 0 until entry.size) {
          val (cat, defs) = entry(i)
          if (cat == category) {
            entry = entry.updated(i, (cat, defs :+ metricDef))
            updated = true
          }
        }

        if (!updated) {
          entry = entry :+ (category, List(metricDef))
        }
        inner += metricDef.name        -> entry
        ruleMap += (category.namespace -> inner)
      }
    }
    ruleMap
  }

  // converted to a method for unit testing.
  def rules: Map[String, Map[String, List[(MetricCategory, List[MetricDefinition])]]] = nsMap

  private[cloudwatch] def getCategories(config: Config): List[MetricCategory] = {
    import scala.jdk.CollectionConverters.*
    val categories = config.getStringList("atlas.cloudwatch.categories").asScala.map { name =>
      val cfg = config.getConfig(s"atlas.cloudwatch.$name")
      MetricCategory.fromConfig(cfg)
    }
    categories.toList
  }

}
