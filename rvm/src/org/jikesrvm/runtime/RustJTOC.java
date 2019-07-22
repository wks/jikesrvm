/*
 *  This file is part of the Jikes RVM project (http://jikesrvm.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License. You
 *  may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  See the COPYRIGHT.txt file distributed with this work for information
 *  regarding copyright ownership.
 */
package org.jikesrvm.runtime;

import org.jikesrvm.classloader.NormalMethod;
import org.jikesrvm.classloader.RVMField;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;

/**
 * Creates the the list of JTOC offsets for the
 * Rust MMTk.
 */
public class RustJTOC {
    public static void compileMMTk() {
        try {
            PrintWriter writer;
            writer = new PrintWriter("./rust_mmtk/src/vm/jikesrvm/entrypoint.rs");
            writer.println("// Do not edit this file. It is automatically generated.");
            writer.println("#![allow(dead_code)]");
            for (Field child : Entrypoints.class.getDeclaredFields()) {
                if (child.get(null) instanceof NormalMethod) {
                    writer.println("pub const " + child.getName().replaceAll("(?<=[a-z])[A-Z]|((?!^)[A-Z](?=[a-z]))", "_$0").toUpperCase() + "_OFFSET: isize = " + ((NormalMethod) child.get(null)).getOffset().toString() + ";");
                }
                if (child.get(null) instanceof RVMField) {
                    writer.println("pub const " + child.getName().replaceAll("(?<=[a-z])[A-Z]|((?!^)[A-Z](?=[a-z]))", "_$0").toUpperCase() + "_OFFSET: isize = " + ((RVMField) child.get(null)).getOffset().toString() + ";");
                }
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            PrintWriter writer;
            writer = new PrintWriter("./rust_mmtk/src/vm/jikesrvm/inc.asm");
            writer.println(".set fp_offset, " + EntrypointHelper.getField("Lorg/jikesrvm/scheduler/RVMThread;",
                    "framePointer", "Lorg/vmmagic/unboxed/Address;").getOffset().toString());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
