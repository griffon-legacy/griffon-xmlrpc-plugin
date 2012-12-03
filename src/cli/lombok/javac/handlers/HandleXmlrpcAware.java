/*
 * Copyright 2012 the original author or authors.
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

package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import griffon.plugins.xmlrpc.XmlrpcAware;
import lombok.core.AnnotationValues;
import lombok.core.handlers.XmlrpcAwareConstants;
import lombok.core.handlers.XmlrpcAwareHandler;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.ast.JavacType;

import static lombok.core.util.ErrorMessages.canBeUsedOnClassAndEnumOnly;
import static lombok.javac.handlers.JavacHandlerUtil.deleteAnnotationIfNeccessary;

/**
 * @author Andres Almiray
 */
public class HandleXmlrpcAware extends JavacAnnotationHandler<XmlrpcAware> {
    private final JavacXmlrpcAwareHandler handler = new JavacXmlrpcAwareHandler();

    @Override
    public void handle(final AnnotationValues<XmlrpcAware> annotation, final JCTree.JCAnnotation source, final JavacNode annotationNode) {
        deleteAnnotationIfNeccessary(annotationNode, XmlrpcAware.class);

        JavacType type = JavacType.typeOf(annotationNode, source);
        if (type.isAnnotation() || type.isInterface()) {
            annotationNode.addError(canBeUsedOnClassAndEnumOnly(XmlrpcAware.class));
            return;
        }

        JavacUtil.addInterface(type.node(), XmlrpcAwareConstants.XMLRPC_CONTRIBUTION_HANDLER_TYPE);
        handler.addXmlrpcProviderField(type);
        handler.addXmlrpcProviderAccessors(type);
        handler.addXmlrpcContributionMethods(type);
        type.editor().rebuild();
    }

    private static class JavacXmlrpcAwareHandler extends XmlrpcAwareHandler<JavacType> {
    }
}
