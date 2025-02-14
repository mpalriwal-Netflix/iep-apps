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
package com.netflix.atlas.druid

import com.netflix.iep.service.AbstractService

/**
  * Used to indicate the service is not healthy until the metadata has been updated
  * at least once.
  */
class DruidMetadataService extends AbstractService {

  @volatile private var metadataAvailable: Boolean = false

  def metadataRefreshed(): Unit = {
    metadataAvailable = true
  }

  override def isHealthy: Boolean = {
    super.isHealthy && metadataAvailable
  }

  override def startImpl(): Unit = {}

  override def stopImpl(): Unit = {}
}
