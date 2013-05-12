/*
 * Copyright 2012-2013 the original author or authors.
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.plugins.xmlrpc

import groovy.net.xmlrpc.XMLRPCServerProxy

import java.util.concurrent.ConcurrentHashMap

/**
 * @author Andres Almiray
 */
class XmlrpcClientHolder {
    private static final XmlrpcClientHolder INSTANCE

    static {
        INSTANCE = new XmlrpcClientHolder()
    }

    static XmlrpcClientHolder getInstance() {
        INSTANCE
    }

    private XmlrpcClientHolder() {}

    private final Map<String, XMLRPCServerProxy> PROXIES = new ConcurrentHashMap<String, XMLRPCServerProxy>()

    String[] getXmlrpcProxyIds() {
        List<String> ids = []
        ids.addAll(PROXIES.keySet())
        ids.toArray(new String[ids.size()])
    }

    XMLRPCServerProxy getXmlrpcProxy(String id) {
        PROXIES[id]
    }

    void setXmlrpcProxy(String id, XMLRPCServerProxy client) {
        PROXIES[id] = client
    }

    // ======================================================

    XMLRPCServerProxy fetchXmlrpcProxy(Map<String, Object> params) {
        XMLRPCServerProxy client = PROXIES[(params.id).toString()]
        if (client == null) {
            String id = params.id ? params.remove('id').toString() : '<EMPTY>'
            client = XmlrpcConnector.instance.createClient(params)
            if (id != '<EMPTY>') PROXIES[id] = client
        }
        client
    }
}
