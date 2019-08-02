<?php
/*
 * Copyright 2014 Google Inc.
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

/**
 * Gets all dimensions available for the logged in user.
 *
 * Tags: metadata.dimensions.list
 */
class GetAllDimensions {
  /**
   * Gets all dimensions available for the logged in user.
   *
   * @param $service Google_Service_AdSense AdSense service object on which to
   *     run the requests.
   */
  public static function run($service) {
    $separator = str_repeat('=', 80) . "\n";
    print $separator;
    printf("Listing all dimensions for user\n");
    print $separator;

    $result = $service->metadata_dimensions->listMetadataDimensions();
    if (!empty($result['items'])) {
      $dimensions = $result['items'];
      foreach ($dimensions as $dimension) {
        printf("Dimension id \"%s\" for product(s): [%s] was found.\n",
            $dimension['id'], join(', ', $dimension['supportedProducts']));
      }
    } else {
      print "No dimensions found.\n";
    }
    print "\n";
  }
}
