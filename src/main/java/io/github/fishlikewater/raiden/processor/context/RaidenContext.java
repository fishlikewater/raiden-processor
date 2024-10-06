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
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.util.Types;
import java.io.Serial;
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

    @Serial
    private static final long serialVersionUID = 5079936240211719665L;

    protected Filer filer;

    protected Messager messager;

    private Types types;

    private JavacProcessingEnvironment javaEvn;

    private Trees trees;

    private TreeMaker treeMaker;

    private Names names;
}
