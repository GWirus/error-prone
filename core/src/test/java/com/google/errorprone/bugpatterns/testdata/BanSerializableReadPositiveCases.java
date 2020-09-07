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

package com.google.errorprone.bugpatterns.testdata;

import com.google.errorprone.bugpatterns.BanSerializableReadTest;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;

/** {@link BanSerializableReadTest} @author tshadwell@google.com (Thomas Shadwell) */
public class BanSerializableReadPositiveCases implements Serializable {
  public final String hi = "hi";

  public static final void commitCodingCrime() throws IOException, ClassNotFoundException {
    PipedInputStream in = new PipedInputStream();
    PipedOutputStream out = new PipedOutputStream(in);

    ObjectOutputStream serializer = new ObjectOutputStream(out);
    ObjectInputStream deserializer = new ObjectInputStream(in);

    serializer.writeObject(new BanSerializableReadPositiveCases());
    serializer.close();

    BanSerializableReadPositiveCases crime =
        (BanSerializableReadPositiveCases) deserializer.readObject();
    System.out.println(crime.hi);
  }
}
