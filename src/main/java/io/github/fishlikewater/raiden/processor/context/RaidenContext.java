/*
 * Copyright (c) 2024 zhangxiang (fishlikewater@126.com)
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
package io.github.fishlikewater.raiden.processor.context;

import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.Serializable;

/**
 * {@code Context}
 *
 * @author zhangxaing
 * @version 1.0.0
 * @since 2024/10/02
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaidenContext implements Serializable {

    private static final long serialVersionUID = 5079936240211719665L;

    protected Filer filer;
    protected Messager messager;
    private Types types;
    private Elements elements;

    private Trees trees;

    private TreeMaker treeMaker;
    private Names names;

    // ----------------------------------------------------------------

    public JCTree.JCVariableDecl toVariable(Element element) {
        return (JCTree.JCVariableDecl) this.getTrees().getTree(element);
    }

    public Name getNameFromString(String name) {
        return this.names.fromString(name);
    }

    public JCTree.JCExpression literal(Object target) {
        return this.treeMaker.Literal(target);
    }

    public JCTree.JCExpression assign(Name name, JCTree.JCExpression expr) {
        return this.getTreeMaker().Assign(this.getTreeMaker().Ident(name), expr);
    }

    public JCTree.JCAnnotation makeAnnotation(String annotationName, com.sun.tools.javac.util.List<JCTree.JCExpression> args) {
        JCTree.JCExpression expression = chainDots(annotationName.split("\\."));
        return this.getTreeMaker().Annotation(expression, args);
    }

    public JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = this.getTreeMaker().Ident(getNameFromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, getNameFromString(componentArray[i]));
        }

        return expr;
    }

    public JCTree.JCVariableDecl makeVarDef(
            JCTree.JCModifiers modifiers,
            String name,
            JCTree.JCExpression varType,
            JCTree.JCExpression init) {
        return treeMaker.VarDef(
                modifiers,
                getNameFromString(name),
                varType,
                init
        );
    }

    // ----------------------------------------------------------------

    private JCTree.JCExpression chainDots(String... elems) {
        assert elems != null;
        JCTree.JCExpression e = null;
        for (String elem : elems) {
            e = e == null
                    ? this.getTreeMaker().Ident(this.getNameFromString(elem))
                    : this.getTreeMaker().Select(e, this.getNameFromString(elem));
        }
        assert e != null;

        return e;
    }
}
