/*
 * The MIT License
 *
 * Copyright 2016 Samuel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.samistine.samistineutilities.utils.annotations.command.backend;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Samuel
 */
class Utils {

    public static <A extends Annotation> A getMethodParameterAnnotation(Method method, int index, Class<A> clazz) {
        Annotation[] annotations = method.getParameterAnnotations()[index];
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (clazz.isAssignableFrom(annotation.getClass())) {
                    return (A) annotation;
                }
            }
        }
        return null;
    }

    /*
	 * Author: D4rKDeagle
     */
    public static List<String> getPossibleCompletionsForGivenArgs(String[] args, String[] possibilities) {
        final String argumentToFindCompletionFor = args[args.length - 1];

        final List<String> listOfPossibleCompletions = new ArrayList<>();
        for (int i = 0; i < possibilities.length; i++) {
            try {
                if (possibilities[i] != null && possibilities[i].regionMatches(true, 0, argumentToFindCompletionFor, 0, argumentToFindCompletionFor.length())) {
                    listOfPossibleCompletions.add(possibilities[i]);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        Collections.sort(listOfPossibleCompletions);

        return listOfPossibleCompletions;
    }

    public static String joinArguments(String[] args, int start, String joiner) {
        if (start > args.length) {
            throw new IllegalArgumentException("start > length");
        }

        StringBuilder joined = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i != start) {
                joined.append(joiner);
            }
            joined.append(args[i]);
        }
        return joined.toString();
    }

    public static String[] getLeftoverArguments(String[] args, int start) {
        String[] newArray = new String[args.length - start];
        for (int i = start; i < args.length; i++) {
            newArray[i - start] = args[i];
        }
        return newArray;
    }
}
