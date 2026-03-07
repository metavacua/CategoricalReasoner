/*
 * SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * JSR 269 Annotation Processor for CattyFormula.
 * Demonstrates automated transformation from documentation to Java code.
 */
package catty.helloworld.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import java.io.IOException;
import java.util.Set;

import catty.helloworld.CattyFormula;

/**
 * Annotation processor for @CattyFormula.
 *
 * Demonstrates JSR 269 Annotation Processing per AGENTS.md:
 * "The automation of morphisms should leverage the Java Compiler API
 * (javax.tools) and Annotation Processing (JSR 269)."
 *
 * This processor reads @CattyFormula annotations from source and generates
 * corresponding Java records (proof terms) automatically.
 */
@SupportedAnnotationTypes("catty.helloworld.CattyFormula")
@SupportedSourceVersion(SourceVersion.RELEASE_25)
public class CattyFormulaProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.elementUtils = processingEnv.getElementUtils();
        this.typeUtils = processingEnv.getTypeUtils();
        this.filer = processingEnv.getFiler();

        messager.printMessage(Diagnostic.Kind.NOTE,
            "CattyFormulaProcessor initialized");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                          RoundEnvironment roundEnv) {

        for (Element element : roundEnv.getElementsAnnotatedWith(CattyFormula.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                    "@CattyFormula can only be applied to classes",
                    element);
                continue;
            }

            TypeElement typeElement = (TypeElement) element;
            CattyFormula formula = typeElement.getAnnotation(CattyFormula.class);

            messager.printMessage(Diagnostic.Kind.NOTE,
                "Processing @CattyFormula: " + formula.id());

            // Generate the record using JavaPoet
            try {
                TypeSpec recordSpec = generateRecord(typeElement, formula);
                JavaFile javaFile = JavaFile.builder(
                    elementUtils.getPackageOf(typeElement).toString(),
                    recordSpec
                ).build();

                javaFile.writeTo(filer);

                messager.printMessage(Diagnostic.Kind.NOTE,
                    "Generated: " + javaFile.typeSpec.name + ".java");
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                    "Failed to generate: " + e.getMessage());
            }
        }

        return true;
    }

    /**
     * Generates a Java record from the @CattyFormula annotation.
     * Records represent the proof term embedded during compilation.
     */
    private TypeSpec generateRecord(TypeElement typeElement, CattyFormula formula) {
        return TypeSpec.recordBuilder(formula.id() + "Proof")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addField(String.class, "id")
            .addField(String.class, "name")
            .addField(String.class, "description")
            .addField(String.class, "category")
            .addMethod(createConstructor(formula))
            .addMethod(createToString(formula))
            .build();
    }

    private com.squareup.javapoet.MethodSpec createConstructor(CattyFormula formula) {
        return com.squareup.javapoet.MethodSpec.constructorBuilder()
            .addStatement("this.id = $S", formula.id())
            .addStatement("this.name = $S", formula.name())
            .addStatement("this.description = $S", formula.description())
            .addStatement("this.category = $S", formula.category())
            .build();
    }

    private com.squareup.javapoet.MethodSpec createToString(CattyFormula formula) {
        return com.squareup.javapoet.MethodSpec.methodBuilder("toString")
            .addAnnotation(Override.class)
            .returns(String.class)
            .addStatement("return $S",
                "CattyFormula[" + formula.id() + "]: " + formula.name())
            .build();
    }
}
