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

/** 
 * Methods available in future java versions.
 * <p>
 * Code in this package supports the minimum java version, but has a more efficient
 * implementation on newer java versions.
 * <p>
 * Currently any bytecode is patched to use the Java 9 native
 * classes through MR-JAR (Multi-Release JAR) mechanism.
 * In Java 8 it will use our own implementation.
 * Because of patching, inside the Java source files we
 * refer to the Lucene implementations, the final Java 9 JAR files
 * will use the correct class names.
 */
package org.apache.lucene.future;
// NOTE: please don't add complex classes here, just stick with 1-1 mapping of the newer
// JDK API (the current patching approach only works with static methods).
// This makes testing simpler (if you want to add tests to openjdk, please do that separate): it means
// we only need to test our fallback implementations.
// When adding other implementations, be sure to have exact same
// method signatures and add suitable tests! Also add the class names
// to the patching script!
