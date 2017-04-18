/**
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

/**
 * This Java class is used as a simple container for dynamically generated
 * methods.
 */

package com.facebook.redextest;

import static org.fest.assertions.api.Assertions.*;

import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import org.junit.Test;

class A {
  public int fa = 0;
  // staticizable using this
  public int foo() {
    return 42 + this.fa;
  }
  // staticizable not using this
  public int baz() {
    return 42;
  }
}

class B extends A {
  // staticizable using this
  public int bar() {
    return super.foo();
  }
}

class C {
  static int callADotFoo() {
    A a = new A();
    return a.foo();
  }

  static int callBDotFoo() {
    B b = new B();
    return b.foo();
  }
}

public class MethodDevirtualizationTest {

  private static boolean isStatic(Method m) {
    return (m.getModifiers() & Modifier.STATIC) != 0;
  }

  @Test
  public void testCallingDevirtualizedMethods() {
    assertThat(new C().callADotFoo()).isEqualTo(42);
    assertThat(new C().callBDotFoo()).isEqualTo(42);
    assertThat(new B().bar()).isEqualTo(42);
  }

  @Test
  public void testMethodStatic() throws NoSuchMethodException {
    Method foo = A.class.getDeclaredMethod("foo", A.class);
    Method bar = B.class.getDeclaredMethod("bar", B.class);
    Method baz = A.class.getDeclaredMethod("baz");
    assertThat(isStatic(foo)).isTrue();
    assertThat(isStatic(bar)).isTrue();
    assertThat(isStatic(baz)).isTrue();
  }
}
