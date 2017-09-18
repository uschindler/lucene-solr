/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Objects;
import java.util.Arrays;
import java.lang.reflect.Modifier;

if (!properties['build.java.runtime'].startsWith('9')) {
  throw new BuildException('You need Java 9 (exact version) to rebuild the corresponding bootclasspath stubs.');
}

File baseDir = new File(properties['java9stubs.dir']);

ant.delete(dir:baseDir, includes:'**/*.class', includeemptydirs:true);

def getInternalName(Class clazz) {
  return clazz.getName().replace('.', '/');
}

[ Objects.class, Arrays.class ].each { clazz ->
  task.log('Generating stub: ' + clazz.getName(), Project.MSG_INFO);
  String internalName = getInternalName(clazz);
  
  // generate byte code with ASM:
  ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
  writer.visit(Opcodes.V1_8, clazz.modifiers, internalName,
    clazz.getGenericSignature0(), // HACK: this is a private method, Groovy does it for us :-)
    getInternalName(clazz.superclass),
    clazz.interfaces.collect(this.&getInternalName) as String[]);
  clazz.declaredConstructors.each { c ->
    writer.visitMethod(c.modifiers, '<init>', Type.getConstructorDescriptor(c),
      c.signature, // HACK: this is a private field, Groovy does it for us :-)
      c.exceptionTypes.collect(this.&getInternalName) as String[]).visitEnd();
  }
  clazz.declaredMethods.each { m ->
    if (!Modifier.isPublic(m.modifiers) && !Modifier.isProtected(m.modifiers)) return;
    writer.visitMethod(m.modifiers, m.name, Type.getMethodDescriptor(m),
      m.signature, // HACK: this is a private field, Groovy does it for us :-)
      m.exceptionTypes.collect(this.&getInternalName) as String[]).visitEnd();
  }
  clazz.declaredFields.each { f ->
    if (!Modifier.isPublic(f.modifiers) && !Modifier.isProtected(f.modifiers)) return;
    writer.visitField(f.modifiers, f.name, Type.getDescriptor(f.type),
      f.signature, // HACK: this is a private field, Groovy does it for us :-)
      null).visitEnd();
  }
  writer.visitEnd();
  
  // write a class file:
  File f = new File(baseDir, internalName + '.class');
  f.parentFile.mkdirs();
  f.setBytes(writer.toByteArray());
}

task.log("Java 9 stubs written to: " + baseDir, Project.MSG_INFO);
