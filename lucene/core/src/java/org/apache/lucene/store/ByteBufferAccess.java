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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SwitchPoint;
import java.nio.ByteBuffer;

final class ByteBufferAccess {
  final SwitchPoint switchPoint;
  final MethodHandle BYTEBUFFER_GET_BYTES_SAFE;
  
  ByteBufferAccess(String resourceDescription, boolean useUnmap) {
    if (useUnmap) {
      switchPoint = new SwitchPoint();
      BYTEBUFFER_GET_BYTES_SAFE = switchPoint.guardWithTest(BYTEBUFFER_GET_BYTES_UNSAFE, BYTEBUFFER_GET_BYTES_FALLBACK);
    } else {
      switchPoint = null;
      BYTEBUFFER_GET_BYTES_SAFE = BYTEBUFFER_GET_BYTES_UNSAFE;
    }
  }
  
  private static final MethodHandle BYTEBUFFER_GET_BYTES_UNSAFE;
  private static final MethodHandle BYTEBUFFER_GET_BYTES_FALLBACK;
  static {
    MethodHandles.Lookup lookup = MethodHandles.publicLookup();
    try {
      BYTEBUFFER_GET_BYTES_UNSAFE = lookup.findVirtual(ByteBuffer.class, "get", MethodType.methodType(ByteBuffer.class, byte[].class, int.class, int.class));
      BYTEBUFFER_GET_BYTES_FALLBACK = MethodHandles.dropArguments(MethodHandles.throwException(ByteBuffer.class, AlreadyClosedException.class), 
                                                                  1, BYTEBUFFER_GET_BYTES_UNSAFE.type().parameterArray()).bindTo(new AlreadyClosedException("already closed"));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new Error(e);
    }
  }
  
  void invalidate() {
    if (switchPoint != null) {
      // TODO: we should really batch this via deletePendingFiles() or similar, or perhaps
      // queue up and take care asynchronously from another thread.
      SwitchPoint.invalidateAll(new SwitchPoint[] { switchPoint });
    }
  }
  
  ByteBuffer get(ByteBuffer receiver, byte[] dst, int offset, int length) {
    try {
      return (ByteBuffer) BYTEBUFFER_GET_BYTES_SAFE.invokeExact(receiver, dst, offset, length);
    } catch (Throwable e) {
      rethrow(e);
      throw new AssertionError();
    }
  }
  
  /** Hack to rethrow unknown Exceptions from {@link MethodHandle#invokeExact}: */
  @SuppressWarnings("unchecked")
  static <T extends Throwable> void rethrow(Throwable t) throws T {
      throw (T) t;
  }
}
