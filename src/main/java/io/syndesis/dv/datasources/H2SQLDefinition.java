/*
 * Copyright (C) 2013 Red Hat, Inc.
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
package io.syndesis.dv.datasources;

import java.util.Map;

public class H2SQLDefinition extends DataSourceDefinition {

    @Override
    public String getType() {
        return "h2";
    }

    @Override
    public String getPomDendencies() {
        return "<dependency>" +
            "  <groupId>com.h2database</groupId>" +
            "  <artifactId>h2</artifactId>" +
            "  <version>${version.h2}</version>" +
            "</dependency>\n";
    }

    @Override
    public String getTranslatorName() {
        return "h2";
    }

    @Override
    public boolean isTypeOf(Map<String, String> properties, String type) {
        if ((properties != null) && (properties.get("url") != null)
                && properties.get("url").startsWith("jdbc:h2:") && type.equals("sql")) {
            return true;
        }
        return false;
    }

}
