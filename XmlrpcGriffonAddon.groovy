/*
 * Copyright 2009-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing pexmlrpcssions and
 * limitations under the License.
 */

import griffon.core.GriffonClass
import griffon.plugins.xmlrpc.XmlrpcEnhancer
import griffon.plugins.xmlrpc.XmlrpcContributionHandler

/**
 * @author Andres Almiray
 */
class XmlrpcGriffonAddon {
    void addonPostInit(GriffonApplication app) {
        def types = app.config.griffon?.xmlrpc?.injectInto ?: ['controller']
        for(String type : types) {
            for(GriffonClass gc : app.artifactManager.getClassesOfType(type)) {
                if (XmlrpcContributionHandler.isAssignableFrom(gc.clazz)) continue
                XmlrpcEnhancer.enhance(gc.metaClass)
            }
        }
    }
}
