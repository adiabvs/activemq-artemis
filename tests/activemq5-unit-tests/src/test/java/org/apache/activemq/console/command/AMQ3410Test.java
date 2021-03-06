/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.console.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.console.CommandContext;
import org.apache.activemq.console.formatter.CommandShellOutputFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AMQ3410Test extends TestCase {

   @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(PurgeCommandTest.class);
   private static final Collection<String> DEFAULT_OPTIONS = Arrays.asList(new String[]{"--amqurl", "tcp://localhost:61616",});

   private static final Collection<String> DEFAULT_TOKENS = Arrays.asList(new String[]{"FOO.QUEUE"});

   protected AbstractApplicationContext context;

   @Override
   protected void setUp() throws Exception {
      super.setUp();

      context = createApplicationContext();

   }

   protected AbstractApplicationContext createApplicationContext() {
      return new ClassPathXmlApplicationContext("org/apache/activemq/console/command/activemq.xml");
   }

   @Override
   protected void tearDown() throws Exception {
      BrokerService broker = (BrokerService) context.getBean("localbroker");
      broker.stop();
      broker = (BrokerService) context.getBean("default");
      broker.stop();
      super.tearDown();
   }

   public void testNoFactorySet() throws Exception {
      AmqBrowseCommand command = new AmqBrowseCommand();
      CommandContext context = new CommandContext();

      context.setFormatter(new CommandShellOutputFormatter(System.out));

      command.setCommandContext(context);

      List<String> tokens = new ArrayList<>();
      tokens.addAll(DEFAULT_OPTIONS);
      tokens.addAll(DEFAULT_TOKENS);

      command.execute(tokens);
      assertNotNull(command.getConnectionFactory());
      assertTrue(command.getConnectionFactory() instanceof ActiveMQConnectionFactory);
   }

   public void testFactorySet() throws Exception {
      AmqBrowseCommand command = new AmqBrowseCommand();
      CommandContext context = new CommandContext();

      context.setFormatter(new CommandShellOutputFormatter(System.out));

      command.setCommandContext(context);

      List<String> tokens = new ArrayList<>();
      tokens.addAll(DEFAULT_OPTIONS);
      tokens.add("--factory");
      tokens.add(DummyConnectionFactory.class.getCanonicalName());
      tokens.addAll(DEFAULT_TOKENS);

      command.execute(tokens);

      assertNotNull(command.getConnectionFactory());
      assertTrue("wrong instance returned: " + command.getConnectionFactory().getClass().getName(), command.getConnectionFactory() instanceof DummyConnectionFactory);
   }

   public void testFactorySetWrong1() throws Exception {
      AmqBrowseCommand command = new AmqBrowseCommand();
      CommandContext context = new CommandContext();

      context.setFormatter(new CommandShellOutputFormatter(System.out));

      command.setCommandContext(context);

      List<String> tokens = new ArrayList<>();
      tokens.addAll(DEFAULT_OPTIONS);
      tokens.add("--factory");
      tokens.add("org.apache.activemq.console.command.TestAMQ3410.DoesntExistFactory");
      tokens.addAll(DEFAULT_TOKENS);

      try {
         command.execute(tokens);
      }
      catch (Throwable cause) {
         while (null != cause) {
            if (cause instanceof java.lang.ClassNotFoundException)
               return;
            cause = cause.getCause();
         }
      }
      assertFalse("No exception caught", true);
   }

   public void testFactorySetWrong2() throws Exception {
      AmqBrowseCommand command = new AmqBrowseCommand();
      CommandContext context = new CommandContext();

      context.setFormatter(new CommandShellOutputFormatter(System.out));

      command.setCommandContext(context);

      List<String> tokens = new ArrayList<>();
      tokens.addAll(DEFAULT_OPTIONS);
      tokens.add("--factory");
      tokens.add(InvalidConnectionFactory.class.getCanonicalName());
      tokens.addAll(DEFAULT_TOKENS);

      try {
         command.execute(tokens);
      }
      catch (Throwable e) {
         Throwable cause = e;
         while (null != cause) {
            if (cause instanceof java.lang.NoSuchMethodException)
               return;
            cause = cause.getCause();
         }
         assertFalse(e.toString(), true);
      }
      assertFalse("No exception caught", true);
   }

   public void testFactorySetWrong3() throws Exception {
      AmqBrowseCommand command = new AmqBrowseCommand();
      CommandContext context = new CommandContext();

      context.setFormatter(new CommandShellOutputFormatter(System.out));

      command.setCommandContext(context);

      List<String> tokens = new ArrayList<>();
      tokens.addAll(DEFAULT_OPTIONS);
      tokens.add("--factory");
      tokens.add("java.lang.Object");
      tokens.addAll(DEFAULT_TOKENS);

      try {
         command.execute(tokens);
      }
      catch (Throwable cause) {
         while (null != cause) {
            if (cause instanceof java.lang.NoSuchMethodException)
               return;
            cause = cause.getCause();
         }
      }
      assertFalse(true);
   }

}
