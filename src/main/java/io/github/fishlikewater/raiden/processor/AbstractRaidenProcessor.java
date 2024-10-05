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

import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import io.github.fishlikewater.raiden.processor.context.RaidenContext;
import lombok.Getter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * {@code AbstractRaidenProcessor}
 *
 * @author zhangxiang
 * @version 1.0.0
 * @since 2024/10/03
 */
public abstract class AbstractRaidenProcessor extends AbstractProcessor {

    @Getter
    private RaidenContext raidenContext;

    public AbstractRaidenProcessor(RaidenContext raidenContext) {
        super();
        this.raidenContext = raidenContext;
    }

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        Filer filer = processingEnv.getFiler();
        Messager messager = processingEnv.getMessager();
        Types types = processingEnv.getTypeUtils();
        Elements elements = processingEnv.getElementUtils();

        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        Trees trees = Trees.instance(processingEnv);
        TreeMaker treeMaker = TreeMaker.instance(context);
        Names names = Names.instance(context);

        raidenContext = RaidenContext.builder()
                .filer(filer)
                .messager(messager)
                .types(types)
                .elements(elements)
                .trees(trees)
                .treeMaker(treeMaker)
                .names(names)
                .build();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
