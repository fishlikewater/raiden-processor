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
package io.github.fishlikewater.raiden.processor;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import io.github.fishlikewater.raiden.processor.context.RaidenContext;
import lombok.Getter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Types;

/**
 * {@code AbstractRaidenProcessor}
 *
 * @author zhangxiang
 * @version 1.0.0
 * @since 2024/10/03
 */
@Getter
public abstract class AbstractRaidenProcessor extends AbstractProcessor {

    private RaidenContext raidenContext;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        Filer filer = processingEnv.getFiler();
        Messager messager = processingEnv.getMessager();
        Types types = processingEnv.getTypeUtils();
        JavacProcessingEnvironment javaEvn = (JavacProcessingEnvironment) processingEnv;

        Context context = javaEvn.getContext();
        Trees trees = Trees.instance(processingEnv);
        TreeMaker treeMaker = TreeMaker.instance(context);
        Names names = Names.instance(context);

        this.raidenContext = RaidenContext.builder()
                .filer(filer)
                .messager(messager)
                .types(types)
                .javaEvn(javaEvn)
                .trees(trees)
                .treeMaker(treeMaker)
                .names(names)
                .build();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    public Name getNameFromString(String name) {
        return this.raidenContext.getNames().fromString(name);
    }

    public JCTree.JCExpression literal(Object target) {
        return this.raidenContext.getTreeMaker().Literal(target);
    }

    public JCTree.JCExpression assign(Name name, JCTree.JCExpression expr) {
        return this.raidenContext.getTreeMaker().Assign(this.raidenContext.getTreeMaker().Ident(name), expr);
    }

    public JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = this.raidenContext.getTreeMaker().Ident(getNameFromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = this.raidenContext.getTreeMaker().Select(expr, getNameFromString(componentArray[i]));
        }

        return expr;
    }

    protected void variableDeclareClassAddImport(VariableElement variable, String fullQualifiedName) {
        JCTree.JCClassDecl clazz = this.tryFindVariableDeclareClass(variable);
        JCTree.JCCompilationUnit unit = (JCTree.JCCompilationUnit) this.raidenContext.getTrees().getPath(clazz.sym).getCompilationUnit();

        ListBuffer<JCTree> imports = new ListBuffer<>();
        for (int i = 0; i < unit.defs.size(); i++) {
            imports.append(unit.defs.get(i));
        }

        JCTree.JCIdent packageIdent = this.raidenContext.getTreeMaker()
                .Ident(this.getNameFromString(this.determineTargetPackageName(fullQualifiedName)));
        JCTree.JCFieldAccess access = this.raidenContext.getTreeMaker()
                .Select(packageIdent, this.raidenContext.getNames().fromString(this.determineTargetSimpleName(fullQualifiedName)));
        JCTree.JCImport ipt = this.raidenContext.getTreeMaker().Import(access, false);
        imports.append(ipt);

        unit.defs = imports.toList();
    }

    protected JCTree.JCClassDecl tryFindVariableDeclareClass(VariableElement variable) {
        TreePath treePath = this.raidenContext.getTrees().getPath(variable);

        CompilationUnitTree ut = treePath.getCompilationUnit();

        Tree tree = this.raidenContext.getTrees().getTree(variable);
        if (tree instanceof JCTree.JCVariableDecl) {
            JCTree.JCVariableDecl vd = (JCTree.JCVariableDecl) tree;

            TreePath classTreePath = this.raidenContext.getTrees().getPath(ut, vd);
            while (classTreePath != null) {
                Tree classTree = classTreePath.getLeaf();
                if (classTree instanceof JCTree.JCClassDecl) {
                    return (JCTree.JCClassDecl) classTree;
                }
                classTreePath = classTreePath.getParentPath();
            }
        }

        return null;
    }

    protected String determineTargetPackageName(String fullQualifiedName) {
        return fullQualifiedName.substring(0, fullQualifiedName.lastIndexOf("."));
    }

    protected String determineTargetSimpleName(String fullQualifiedName) {
        return fullQualifiedName.substring(fullQualifiedName.lastIndexOf(".") + 1);
    }
}
