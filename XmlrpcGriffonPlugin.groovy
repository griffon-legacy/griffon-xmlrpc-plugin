/*
 * Copyright 2009-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing pexmlrpcssions and
 * limitations under the License.
 */

/**
 * @author Andres Almiray
 */
class XmlrpcGriffonPlugin {
    // the plugin version
    String version = '0.7'
    // the version or versions of Griffon the plugin is designed for
    String griffonVersion = '0.9.5 > *'
    // the other plugins this plugin depends on
    Map dependsOn = [:]
    // resources that are included in plugin packaging
    List pluginIncludes = []
    // the plugin license
    String license = 'Apache Software License 2.0'
    // Toolkit compatibility. No value means compatible with all
    // Valid values are: swing, javafx, swt, pivot, gtk
    List toolkits = []
    // Platform compatibility. No value means compatible with all
    // Valid values are:
    // linux, linux64, windows, windows64, macosx, macosx64, solaris
    List platforms = []
    // URL where documentation can be found
    String documentation = ''
    // URL where source can be found
    String source = 'https://github.com/griffon/griffon-xmlrpc-plugin'

    List authors = [
        [
            name: 'Andres Almiray',
            email: 'aalmiray@yahoo.com'
        ]
    ]
    String title = 'XML-RPC client & libraries'

    String description = '''
The Xmlrpc plugin adds a remoting client capable of communicating via XML-RPC.
It is compatible with Grails' [Xmlrpc plugin 0.1][1].

Usage
-----
The plugin will inject the following dynamic methods:

* `withXmlrpc(Map params, Closure stmts)` - executes stmts using xml-rpc

Where params may contain

| Property | Type     | Required |
| -------- | -------- | ---------|
| url      | String   | yes      |

All dynamic methods will create a new client when invoked unless you define an `id:` attribute.
When this attribute is supplied the client will be stored in a cache managed by the `XmlrpcProvider` that
handled the call.

These methods are also accessible to any component through the singleton `griffon.plugins.xmlrpc.XmlrpcEnhancer`.
You can inject these methods to non-artifacts via metaclasses. Simply grab hold of a particular metaclass and call
`XmlrpcEnhancer.enhance(metaClassInstance)`.

Configuration
-------------
### Dynamic method injection

Dynamic methods will be added to controllers by default. You can
change this setting by adding a configuration flag in `griffon-app/conf/Config.groovy`

    griffon.xmlrpc.injectInto = ['controller', 'service']

### Example

This example relies on [Grails][2] as the service provider. Follow these steps to configure the service on the Grails side:

1. Download a copy of [Grails][3] and install it.
2. Create a new Grails application. We'll pick 'exporter' as the application name.

        grails create-app exporter

3. Change into the application's directory. Install the xmlrpc plugin.

        grails install-plugin xmlrpc

4. Create a `Calculator` service

        grails create-service calculator
    
5. Paste the following code in `grails-app/services/exporter/CalculatorService.groovy`. Mind the fact that we're using the
default package as the `Xmlrpc` class provided by the xmlrpc plugin is defined with no package

        class CalculatorService extends Xmlrpc {
            boolean transactional = false
 
            def add(params) {
                println "add(${params[0]}, ${params[1]})" // good old println() for quick debugging
                return params[0].toDouble() + params[1].toDouble()
            }
        }

6. Run the application

        grails run-app
    
Now we're ready to build the Griffon application

1. Create a new Griffon application. We'll pick `calculator` as the application name

        griffon create-app calculator
    
2. Install the xmlrpc plugin

        griffon install-plugin xmlrpc

3. Fix the view script to look like this

        package calculator
        application(title: 'Xmlrpc Plugin Example',
          pack: true,
          locationByPlatform: true,
          iconImage: imageIcon('/griffon-icon-48x48.png').image,
          iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                       imageIcon('/griffon-icon-32x32.png').image,
                       imageIcon('/griffon-icon-16x16.png').image]) {
            gridLayout(cols: 2, rows: 4)
            label('Num1:')
            textField(columns: 20, text: bind(target: model, targetProperty: 'num1'))
            label('Num2:')
            textField(columns: 20, text: bind(target: model, targetProperty: 'num2'))
            label('Result:')
            label(text: bind{model.result})
            button('Calculate', enabled: bind{model.enabled}, actionPerformed: controller.calculate)
        }

4. Let's add required properties to the model

        package calculator
        @Bindable
        class CalculatorModel {
           String num1
           String num2
           String result
           boolean enabled = true
        }

5. Now for the controller code. Notice that there is minimal error handling in place. If the user
types something that is not a number the client will surely break, but the code is sufficient for now.

        package calculator
        class CalculatorController {
            def model
 
            def calculate = { evt = null ->
                double a = model.num1.toDouble()
                double b = model.num2.toDouble()
                execSync { model.enabled = false }
                try {
                    def result = withXmlrpc(url: 'http://localhost:8080/exporter/xmlrpc/') {
                        add(a, b)
                    }
                    execInsideUIAsync { model.result = result.toString() }
                } finally {
                    execInsideUIAsync { model.enabled = true }
                }
            }
        }
    
6. Run the application

    griffon run-app

### Java API

Here's how the above service call may be written in Java

        import static griffon.util.CollectionUtils.map;
        import griffon.plugins.xmlrpc.XmlrpcConnector;
        import groovy.net.xmlrpc.XMLRPCServerProxy;
        import griffon.util.CallableWithArgs;
        import java.util.Map;
 
        final double a = Double.parseDouble(model.getNum1());
        final double b = Double.parseDouble(model.getNum2());
        Map params = map().e("url", "http://localhost:8080/exporter/xmlrpc/");
        Double result = XmlrpcConnector.getInstance().withXmlrpc(params, new CallableWithArgs<Double>() {
            public Double call(Object[] args) {
                XMLRPCServerProxy proxy = (XMLRPCServerProxy) args[0];
                return (Double) proxy.invokeMethod("add", new Object[]{a, b});
            }
        });

Testing
-------
Dynamic methods will not be automatically injected during unit testing, because addons are simply not initialized
for this kind of tests. However you can use `XmlrpcEnhancer.enhance(metaClassInstance, xmlrpcProviderInstance)` where 
`xmlrpcProviderInstance` is of type `griffon.plugins.xmlrpc.XmlrpcProvider`. The contract for this interface looks like this

    public interface XmlrpcProvider {
        Object withXmlrpc(Map params, Closure closure);
        <T> T withXmlrpc(Map params, CallableWithArgs<T> callable);
    }

It's up to you define how these methods need to be implemented for your tests. For example, here's an implementation that never
fails regardless of the arguments it receives

    class MyXmlrpcProvider implements XmlrpcProvider {
        Object withXmlrpc(Map params, Closure closure) { null }
        public <T> T withXmlrpc(Map params, CallableWithArgs<T> callable) { null }
    }
    
This implementation may be used in the following way

    class MyServiceTests extends GriffonUnitTestCase {
        void testSmokeAndMirrors() {
            MyService service = new MyService()
            XmlrpcEnhancer.enhance(service.metaClass, new MyXmlrpcProvider())
            // exercise service methods
        }
    }


[1]: http://grails.org/plugin/xmlrpc
[2]: http://grails.org
[3]: http://grails.org/Download
'''
}
