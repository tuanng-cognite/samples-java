/*
 *  Copyright (c) 2020 Temporal Technologies, Inc. All Rights Reserved
 *
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package io.temporal.samples.hello;

import static io.temporal.samples.hello.HelloActivity.TASK_QUEUE;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Hello World Temporal workflow that executes a single activity. Requires a local instance the
 * Temporal service to be running.
 */
public class HelloStarter {

  public static void main(String[] args) throws InterruptedException {

    WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    WorkflowClient client = WorkflowClient.newInstance(service);

    long start = System.currentTimeMillis();
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(30);
    CountDownLatch latch = new CountDownLatch(1000);
    for (int i = 0; i < 1000; i++) {
      executor.submit(
          () -> {
            HelloActivity.GreetingWorkflow workflow =
                client.newWorkflowStub(
                    HelloActivity.GreetingWorkflow.class,
                    WorkflowOptions.newBuilder().setTaskQueue(TASK_QUEUE).build());
            // Execute a workflow waiting for it to complete. See {@link
            // io.temporal.samples.hello.HelloSignal}
            // for an example of starting workflow without waiting synchronously for its result.
            String greeting = workflow.getGreeting("World");
            System.out.println(greeting);
            latch.countDown();
          });
    }

    latch.await();
    System.out.println("time = " + (System.currentTimeMillis() - start));
    System.exit(0);
  }
}
