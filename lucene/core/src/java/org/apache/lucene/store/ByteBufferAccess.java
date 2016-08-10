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
package org.apache.lucene.store;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SwitchPoint;
import java.nio.ByteBuffer;

final class ByteBufferAccess {
  private final String resourceDescription;
  private final BufferCleaner cleaner;
  private final SwitchPoint switchPoint;
  private final MethodHandle mhGetBytesSafe, mhGetByteSafe, mhGetShortSafe, mhGetIntSafe, mhGetLongSafe, mhGetPosByteSafe, mhGetPosShortSafe, mhGetPosIntSafe, mhGetPosLongSafe;
  
  /**
   * Pass in an implementation of this interface to cleanup ByteBuffers.
   * MMapDirectory implements this to allow unmapping of bytebuffers with private Java APIs.
   */
  @FunctionalInterface
  static interface BufferCleaner {
    void freeBuffer(String resourceDescription, ByteBuffer b) throws IOException;
  }
  
  ByteBufferAccess(String resourceDescription, BufferCleaner cleaner) {
    this.resourceDescription = resourceDescription;
    this.cleaner = cleaner;
    if (cleaner != null) {
      switchPoint = new SwitchPoint();
      mhGetBytesSafe = switchPoint.guardWithTest(BYTEBUFFER_GET_BYTES_UNSAFE, BYTEBUFFER_GET_BYTES_FALLBACK);
      mhGetByteSafe = switchPoint.guardWithTest(BYTEBUFFER_GET_BYTE_UNSAFE, BYTEBUFFER_GET_BYTE_FALLBACK);
      mhGetShortSafe = switchPoint.guardWithTest(BYTEBUFFER_GET_SHORT_UNSAFE, BYTEBUFFER_GET_SHORT_FALLBACK);
      mhGetIntSafe = switchPoint.guardWithTest(BYTEBUFFER_GET_INT_UNSAFE, BYTEBUFFER_GET_INT_FALLBACK);
      mhGetLongSafe = switchPoint.guardWithTest(BYTEBUFFER_GET_LONG_UNSAFE, BYTEBUFFER_GET_LONG_FALLBACK);
      mhGetPosByteSafe = switchPoint.guardWithTest(BYTEBUFFER_GET_POS_BYTE_UNSAFE, BYTEBUFFER_GET_POS_BYTE_FALLBACK);
      mhGetPosShortSafe = switchPoint.guardWithTest(BYTEBUFFER_GET_POS_SHORT_UNSAFE, BYTEBUFFER_GET_POS_SHORT_FALLBACK);
      mhGetPosIntSafe = switchPoint.guardWithTest(BYTEBUFFER_GET_POS_INT_UNSAFE, BYTEBUFFER_GET_POS_INT_FALLBACK);
      mhGetPosLongSafe = switchPoint.guardWithTest(BYTEBUFFER_GET_POS_LONG_UNSAFE, BYTEBUFFER_GET_POS_LONG_FALLBACK);
    } else {
      switchPoint = null;
      mhGetBytesSafe = BYTEBUFFER_GET_BYTES_UNSAFE;
      mhGetByteSafe = BYTEBUFFER_GET_BYTE_UNSAFE;
      mhGetShortSafe = BYTEBUFFER_GET_SHORT_UNSAFE;
      mhGetIntSafe = BYTEBUFFER_GET_INT_UNSAFE;
      mhGetLongSafe = BYTEBUFFER_GET_LONG_UNSAFE;
      mhGetPosByteSafe = BYTEBUFFER_GET_POS_BYTE_UNSAFE;
      mhGetPosShortSafe = BYTEBUFFER_GET_POS_SHORT_UNSAFE;
      mhGetPosIntSafe = BYTEBUFFER_GET_POS_INT_UNSAFE;
      mhGetPosLongSafe = BYTEBUFFER_GET_POS_LONG_UNSAFE;
    }
  }
  
  void invalidate(ByteBuffer bufs[]) throws IOException {
    if (cleaner != null) {
      // TODO: we should really batch this via deletePendingFiles() or similar, or perhaps
      // queue up and take care asynchronously from another thread.
      SwitchPoint.invalidateAll(new SwitchPoint[] { switchPoint });
      for (ByteBuffer b : bufs) {
        cleaner.freeBuffer(resourceDescription, b);
      }
    }
  }
  
  ByteBuffer getBytes(ByteBuffer receiver, byte[] dst, int offset, int length) {
    try {
      return (ByteBuffer) mhGetBytesSafe.invokeExact(receiver, dst, offset, length);
    } catch (Throwable e) {
      rethrow(e);
      throw new AssertionError();
    }
  }
  
  byte getByte(ByteBuffer receiver) {
    try {
      return (byte) mhGetByteSafe.invokeExact(receiver);
    } catch (Throwable e) {
      rethrow(e);
      throw new AssertionError();
    }
  }
  
  short getShort(ByteBuffer receiver) {
    try {
      return (short) mhGetShortSafe.invokeExact(receiver);
    } catch (Throwable e) {
      rethrow(e);
      throw new AssertionError();
    }
  }
  
  int getInt(ByteBuffer receiver) {
    try {
      return (int) mhGetIntSafe.invokeExact(receiver);
    } catch (Throwable e) {
      rethrow(e);
      throw new AssertionError();
    }
  }
  
  long getLong(ByteBuffer receiver) {
    try {
      return (long) mhGetLongSafe.invokeExact(receiver);
    } catch (Throwable e) {
      rethrow(e);
      throw new AssertionError();
    }
  }
  
  byte getByte(ByteBuffer receiver, int pos) {
    try {
      return (byte) mhGetPosByteSafe.invokeExact(receiver, pos);
    } catch (Throwable e) {
      rethrow(e);
      throw new AssertionError();
    }
  }
  
  short getShort(ByteBuffer receiver, int pos) {
    try {
      return (short) mhGetPosShortSafe.invokeExact(receiver, pos);
    } catch (Throwable e) {
      rethrow(e);
      throw new AssertionError();
    }
  }
  
  int getInt(ByteBuffer receiver, int pos) {
    try {
      return (int) mhGetPosIntSafe.invokeExact(receiver, pos);
    } catch (Throwable e) {
      rethrow(e);
      throw new AssertionError();
    }
  }
  
  long getLong(ByteBuffer receiver, int pos) {
    try {
      return (long) mhGetPosLongSafe.invokeExact(receiver, pos);
    } catch (Throwable e) {
      rethrow(e);
      throw new AssertionError();
    }
  }
  
  /** Hack to rethrow unknown Exceptions from {@link MethodHandle#invokeExact}: */
  @SuppressWarnings("unchecked")
  private static <T extends Throwable> void rethrow(Throwable t) throws T {
      throw (T) t;
  }

  private static MethodHandle createFallback(MethodType type) {
    MethodHandle fallback = MethodHandles.throwException(type.returnType(), NullPointerException.class).bindTo(new NullPointerException());
    return MethodHandles.dropArguments(fallback, 0, type.parameterArray());
  }
  
  private static final MethodHandle BYTEBUFFER_GET_BYTES_UNSAFE, BYTEBUFFER_GET_BYTES_FALLBACK,
    BYTEBUFFER_GET_BYTE_UNSAFE, BYTEBUFFER_GET_BYTE_FALLBACK,
    BYTEBUFFER_GET_SHORT_UNSAFE, BYTEBUFFER_GET_SHORT_FALLBACK,
    BYTEBUFFER_GET_INT_UNSAFE, BYTEBUFFER_GET_INT_FALLBACK,
    BYTEBUFFER_GET_LONG_UNSAFE, BYTEBUFFER_GET_LONG_FALLBACK,
    BYTEBUFFER_GET_POS_BYTE_UNSAFE, BYTEBUFFER_GET_POS_BYTE_FALLBACK,
    BYTEBUFFER_GET_POS_SHORT_UNSAFE, BYTEBUFFER_GET_POS_SHORT_FALLBACK,
    BYTEBUFFER_GET_POS_INT_UNSAFE, BYTEBUFFER_GET_POS_INT_FALLBACK,
    BYTEBUFFER_GET_POS_LONG_UNSAFE, BYTEBUFFER_GET_POS_LONG_FALLBACK;
  static {
    MethodHandles.Lookup lookup = MethodHandles.publicLookup();
    try {
      BYTEBUFFER_GET_BYTES_UNSAFE = lookup.findVirtual(ByteBuffer.class, "get", MethodType.methodType(ByteBuffer.class, byte[].class, int.class, int.class));
      BYTEBUFFER_GET_BYTES_FALLBACK = createFallback(BYTEBUFFER_GET_BYTES_UNSAFE.type());
      BYTEBUFFER_GET_BYTE_UNSAFE = lookup.findVirtual(ByteBuffer.class, "get", MethodType.methodType(byte.class));
      BYTEBUFFER_GET_BYTE_FALLBACK = createFallback(BYTEBUFFER_GET_BYTE_UNSAFE.type());
      BYTEBUFFER_GET_SHORT_UNSAFE = lookup.findVirtual(ByteBuffer.class, "getShort", MethodType.methodType(short.class));
      BYTEBUFFER_GET_SHORT_FALLBACK = createFallback(BYTEBUFFER_GET_SHORT_UNSAFE.type());
      BYTEBUFFER_GET_INT_UNSAFE = lookup.findVirtual(ByteBuffer.class, "getInt", MethodType.methodType(int.class));
      BYTEBUFFER_GET_INT_FALLBACK = createFallback(BYTEBUFFER_GET_INT_UNSAFE.type());
      BYTEBUFFER_GET_LONG_UNSAFE = lookup.findVirtual(ByteBuffer.class, "getLong", MethodType.methodType(long.class));
      BYTEBUFFER_GET_LONG_FALLBACK = createFallback(BYTEBUFFER_GET_LONG_UNSAFE.type());
      BYTEBUFFER_GET_POS_BYTE_UNSAFE = lookup.findVirtual(ByteBuffer.class, "get", MethodType.methodType(byte.class, int.class));
      BYTEBUFFER_GET_POS_BYTE_FALLBACK = createFallback(BYTEBUFFER_GET_POS_BYTE_UNSAFE.type());
      BYTEBUFFER_GET_POS_SHORT_UNSAFE = lookup.findVirtual(ByteBuffer.class, "getShort", MethodType.methodType(short.class, int.class));
      BYTEBUFFER_GET_POS_SHORT_FALLBACK = createFallback(BYTEBUFFER_GET_POS_SHORT_UNSAFE.type());
      BYTEBUFFER_GET_POS_INT_UNSAFE = lookup.findVirtual(ByteBuffer.class, "getInt", MethodType.methodType(int.class, int.class));
      BYTEBUFFER_GET_POS_INT_FALLBACK = createFallback(BYTEBUFFER_GET_POS_INT_UNSAFE.type());
      BYTEBUFFER_GET_POS_LONG_UNSAFE = lookup.findVirtual(ByteBuffer.class, "getLong", MethodType.methodType(long.class, int.class));
      BYTEBUFFER_GET_POS_LONG_FALLBACK = createFallback(BYTEBUFFER_GET_POS_LONG_UNSAFE.type());
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new Error(e);
    }
  }
  
}
