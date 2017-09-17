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
package org.apache.lucene.future;

import java.util.Arrays;

// Methods from Java9's java.util.Arrays
@Deprecated
public final class FutureArrays {

  // byte[]

  public static int mismatch(byte[] a, int aFromIndex, int aToIndex, byte[] b, int bFromIndex, int bToIndex) {
    return Arrays.mismatch(a, aFromIndex, aToIndex, b, bFromIndex, bToIndex);
  }
  
  public static int compareUnsigned(byte[] a, int aFromIndex, int aToIndex, byte[] b, int bFromIndex, int bToIndex) {
    return Arrays.compareUnsigned(a, aFromIndex, aToIndex, b, bFromIndex, bToIndex);
  }
  
  public static boolean equals(byte[] a, int aFromIndex, int aToIndex, byte[] b, int bFromIndex, int bToIndex) {
    return Arrays.equals(a, aFromIndex, aToIndex, b, bFromIndex, bToIndex);
  }
  
  // char[]
  
  public static int mismatch(char[] a, int aFromIndex, int aToIndex, char[] b, int bFromIndex, int bToIndex) {
    return Arrays.mismatch(a, aFromIndex, aToIndex, b, bFromIndex, bToIndex);
  }
  
  public static int compare(char[] a, int aFromIndex, int aToIndex, char[] b, int bFromIndex, int bToIndex) {
    return Arrays.compare(a, aFromIndex, aToIndex, b, bFromIndex, bToIndex);
  }
  
  public static boolean equals(char[] a, int aFromIndex, int aToIndex, char[] b, int bFromIndex, int bToIndex) {
    return Arrays.equals(a, aFromIndex, aToIndex, b, bFromIndex, bToIndex);
  }
  
  // int[]
  
  public static int compare(int[] a, int aFromIndex, int aToIndex, int[] b, int bFromIndex, int bToIndex) {
    return Arrays.compare(a, aFromIndex, aToIndex, b, bFromIndex, bToIndex);
  }
  
  public static boolean equals(int[] a, int aFromIndex, int aToIndex, int[] b, int bFromIndex, int bToIndex) {
    return Arrays.equals(a, aFromIndex, aToIndex, b, bFromIndex, bToIndex);
  }
  
  // long[]
  
  public static int compare(long[] a, int aFromIndex, int aToIndex, long[] b, int bFromIndex, int bToIndex) {
    return Arrays.compare(a, aFromIndex, aToIndex, b, bFromIndex, bToIndex);
  }
  
  public static boolean equals(long[] a, int aFromIndex, int aToIndex, long[] b, int bFromIndex, int bToIndex) {
    return Arrays.equals(a, aFromIndex, aToIndex, b, bFromIndex, bToIndex);
  }
  
}
