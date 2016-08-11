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
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

final class ByteBufferAccess {
  private static final AtomicInteger STORE_BARRIER = new AtomicInteger();
  
  private final String resourceDescription;
  private final BufferCleaner cleaner;
  
  private boolean invalidated = false;

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
  }
  
  void invalidate(ByteBuffer... bufs) throws IOException {
    if (cleaner != null) {
      invalidated = true;
      // this should trigger a happens-before - so flushes all caches
      STORE_BARRIER.lazySet(0);
      Thread.yield();
      for (ByteBuffer b : bufs) {
        cleaner.freeBuffer(resourceDescription, b);
      }
    }
  }
  
  private void ensureValid() {
    if (invalidated) {
      throw new NullPointerException();
    }
  }
  
  void getBytes(ByteBuffer receiver, byte[] dst, int offset, int length) {
    ensureValid();
    receiver.get(dst, offset, length);
  }
  
  byte getByte(ByteBuffer receiver) {
    ensureValid();
    return receiver.get();
  }
  
  short getShort(ByteBuffer receiver) {
    ensureValid();
    return receiver.getShort();
  }
  
  int getInt(ByteBuffer receiver) {
    ensureValid();
    return receiver.getInt();
  }
  
  long getLong(ByteBuffer receiver) {
    ensureValid();
    return receiver.getLong();
  }
  
  byte getByte(ByteBuffer receiver, int pos) {
    ensureValid();
    return receiver.get(pos);
  }
  
  short getShort(ByteBuffer receiver, int pos) {
    ensureValid();
    return receiver.getShort(pos);
  }
  
  int getInt(ByteBuffer receiver, int pos) {
    ensureValid();
    return receiver.getInt(pos);
  }
  
  long getLong(ByteBuffer receiver, int pos) {
    ensureValid();
    return receiver.getLong(pos);
  }
    
}
