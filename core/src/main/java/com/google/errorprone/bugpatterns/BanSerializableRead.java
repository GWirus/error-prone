/*
 * Copyright 2012 The Error Prone Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.errorprone.bugpatterns;

import static com.google.errorprone.matchers.Matchers.instanceMethod;

import com.google.errorprone.BugPattern;
import com.google.errorprone.BugPattern.LinkType;
import com.google.errorprone.BugPattern.SeverityLevel;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker.MethodInvocationTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;

/** A {@link BugChecker} that detects use of the unsafe Java `Serializable` API. */
@BugPattern(
    name = "BanSerializableRead",
    summary = "You used an unsafe `Serializable` API",
    explanation =
        "Java serialization is dangerous, especially so if data is from"
            + "an untrusted source. Any consumption of a serialized object that"
            + "cannot be explicitly trusted will likely result in a critical "
            + "remote code execution bug, allowing control over the application.",
    linkType = LinkType.CUSTOM,
    severity = SeverityLevel.ERROR)
public final class BanSerializableRead extends BugChecker implements MethodInvocationTreeMatcher {
  private static final Matcher<ExpressionTree> BANNED_METHODS =
      instanceMethod()
          .onExactClass("java.io.objectInputStream")
          .namedAnyOf(
              // prevent reading objects unsafely into memory
              "readObject",
              // this is the same, the default value
              "defaultReadObject",
              // this is for trusted subclasses
              "readObjectOverride",
              // ultimately, a lot of the safety worries come
              // from being able to construct arbitrary classes via
              // reading in class descriptors. I don't think anyone
              // will bother calling this directly, but I don't see
              // any reason not to block it.
              "readClassDescriptor",
              // these are basically the same as above
              "resolveClass",
              "resolveObject");

  @Override
  public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
    if (BANNED_METHODS.matches(tree, state)) {
      return describeMatch(tree);
    }

    return Description.NO_MATCH;
  }
}
