/***********************************************************************************************************************
 *
 * Copyright (C) 2010 by the Stratosphere project (http://stratosphere.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/

package eu.stratosphere.pact.runtime.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import eu.stratosphere.pact.common.stub.Collector;
import eu.stratosphere.pact.common.stub.CrossStub;
import eu.stratosphere.pact.common.type.KeyValuePair;
import eu.stratosphere.pact.common.type.base.PactInteger;
import eu.stratosphere.pact.runtime.task.util.TaskConfig.LocalStrategy;
import eu.stratosphere.pact.runtime.test.util.DelayingInfinitiveInputIterator;
import eu.stratosphere.pact.runtime.test.util.RegularlyGeneratedInputGenerator;
import eu.stratosphere.pact.runtime.test.util.TaskCancelThread;
import eu.stratosphere.pact.runtime.test.util.TaskTestBase;

@SuppressWarnings("javadoc")
public class CrossTaskTest extends TaskTestBase {

	private static final Log LOG = LogFactory.getLog(CrossTaskTest.class);
	
	List<KeyValuePair<PactInteger,PactInteger>> outList = new ArrayList<KeyValuePair<PactInteger,PactInteger>>();

	@Test
	public void testBlock1CrossTask() {

		int keyCnt1 = 10;
		int valCnt1 = 1;
		
		int keyCnt2 = 100;
		int valCnt2 = 4;
		
		super.initEnvironment(1*1024*1024);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt1, valCnt1, false), 1);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt2, valCnt2, false), 2);
		super.addOutput(this.outList);
		
		CrossTask testTask = new CrossTask();
		super.getTaskConfig().setLocalStrategy(LocalStrategy.NESTEDLOOP_BLOCKED_OUTER_FIRST);
		super.getTaskConfig().setMemorySize(1 * 1024 * 1024);
		
		super.registerTask(testTask, MockCrossStub.class);
		
		try {
			testTask.invoke();
		} catch (Exception e) {
			LOG.debug(e);
			Assert.fail("Invoke method caused exception.");
		}
		
		int expCnt = keyCnt1*valCnt1*keyCnt2*valCnt2;
		
		Assert.assertTrue("Resultset size was "+this.outList.size()+". Expected was "+expCnt, this.outList.size() == expCnt);
		
		this.outList.clear();
				
	}
	
	@Test
	public void testBlock2CrossTask() {

		int keyCnt1 = 10;
		int valCnt1 = 1;
		
		int keyCnt2 = 100;
		int valCnt2 = 4;
		
		super.initEnvironment(1*1024*1024);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt1, valCnt1, false), 1);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt2, valCnt2, false), 2);
		super.addOutput(this.outList);
		
		CrossTask testTask = new CrossTask();
		super.getTaskConfig().setLocalStrategy(LocalStrategy.NESTEDLOOP_BLOCKED_OUTER_SECOND);
		super.getTaskConfig().setMemorySize(1 * 1024 * 1024);
		
		super.registerTask(testTask, MockCrossStub.class);
		
		try {
			testTask.invoke();
		} catch (Exception e) {
			LOG.debug(e);
			Assert.fail("Invoke method caused exception.");
		}
		
		int expCnt = keyCnt1*valCnt1*keyCnt2*valCnt2;
		
		Assert.assertTrue("Resultset size was "+this.outList.size()+". Expected was "+expCnt, this.outList.size() == expCnt);
		
		this.outList.clear();
		
	}
	
	@Test
	public void testFailingBlockCrossTask() {

		int keyCnt1 = 10;
		int valCnt1 = 1;
		
		int keyCnt2 = 100;
		int valCnt2 = 4;
		
		super.initEnvironment(1*1024*1024);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt1, valCnt1, false), 1);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt2, valCnt2, false), 2);
		super.addOutput(this.outList);
		
		CrossTask testTask = new CrossTask();
		super.getTaskConfig().setLocalStrategy(LocalStrategy.NESTEDLOOP_BLOCKED_OUTER_FIRST);
		super.getTaskConfig().setMemorySize(1 * 1024 * 1024);
		
		super.registerTask(testTask, MockFailingCrossStub.class);
		
		boolean stubFailed = false;
		
		try {
			testTask.invoke();
		} catch (Exception e) {
			stubFailed = true;
		}
		
		Assert.assertTrue("Stub exception was not forwarded.", stubFailed);
		
		this.outList.clear();
				
	}
	
	@Test
	public void testStream1CrossTask() {

		int keyCnt1 = 10;
		int valCnt1 = 1;
		
		int keyCnt2 = 100;
		int valCnt2 = 4;
		
		super.initEnvironment(1*1024*1024);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt1, valCnt1, false), 1);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt2, valCnt2, false), 2);
		super.addOutput(this.outList);
		
		CrossTask testTask = new CrossTask();
		super.getTaskConfig().setLocalStrategy(LocalStrategy.NESTEDLOOP_STREAMED_OUTER_FIRST);
		super.getTaskConfig().setMemorySize(1 * 1024 * 1024);
		
		super.registerTask(testTask, MockCrossStub.class);
		
		try {
			testTask.invoke();
		} catch (Exception e) {
			LOG.debug(e);
			Assert.fail("Invoke method caused exception.");
		}
		
		int expCnt = keyCnt1*valCnt1*keyCnt2*valCnt2;
		
		Assert.assertTrue("Resultset size was "+this.outList.size()+". Expected was "+expCnt, this.outList.size() == expCnt);
		
		this.outList.clear();
		
	}
	
	@Test
	public void testStream2CrossTask() {

		int keyCnt1 = 10;
		int valCnt1 = 1;
		
		int keyCnt2 = 100;
		int valCnt2 = 4;
		
		super.initEnvironment(1*1024*1024);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt1, valCnt1, false), 1);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt2, valCnt2, false), 2);
		super.addOutput(this.outList);
		
		CrossTask testTask = new CrossTask();
		super.getTaskConfig().setLocalStrategy(LocalStrategy.NESTEDLOOP_STREAMED_OUTER_SECOND);
		super.getTaskConfig().setMemorySize(1 * 1024 * 1024);
		
		super.registerTask(testTask, MockCrossStub.class);
		
		try {
			testTask.invoke();
		} catch (Exception e) {
			LOG.debug(e);
			Assert.fail("Invoke method caused exception.");
		}
		
		int expCnt = keyCnt1*valCnt1*keyCnt2*valCnt2;
		
		Assert.assertTrue("Resultset size was "+this.outList.size()+". Expected was "+expCnt, this.outList.size() == expCnt);
		
		this.outList.clear();
		
	}
	
	@Test
	public void testEmptyCrossTask() {

		int keyCnt1 = 10;
		int valCnt1 = 1;
		
		int keyCnt2 = 0;
		int valCnt2 = 0;
		
		super.initEnvironment(1*1024*1024);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt1, valCnt1, false), 1);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt2, valCnt2, false), 2);
		super.addOutput(this.outList);
		
		CrossTask testTask = new CrossTask();
		super.getTaskConfig().setLocalStrategy(LocalStrategy.NESTEDLOOP_STREAMED_OUTER_FIRST);
		super.getTaskConfig().setMemorySize(1 * 1024 * 1024);
		
		super.registerTask(testTask, MockCrossStub.class);
		
		try {
			testTask.invoke();
		} catch (Exception e) {
			LOG.debug(e);
			Assert.fail("Invoke method caused exception.");
		}
		
		int expCnt = keyCnt1*valCnt1*keyCnt2*valCnt2;
		
		Assert.assertTrue("Resultset size was "+this.outList.size()+". Expected was "+expCnt, this.outList.size() == expCnt);
		
		this.outList.clear();
		
	}
	
	
	
	@Test
	public void testFailingStreamCrossTask() {

		int keyCnt1 = 10;
		int valCnt1 = 1;
		
		int keyCnt2 = 100;
		int valCnt2 = 4;
		
		super.initEnvironment(1*1024*1024);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt1, valCnt1, false), 1);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt2, valCnt2, false), 2);
		super.addOutput(this.outList);
		
		CrossTask testTask = new CrossTask();
		super.getTaskConfig().setLocalStrategy(LocalStrategy.NESTEDLOOP_STREAMED_OUTER_FIRST);
		super.getTaskConfig().setMemorySize(1 * 1024 * 1024);
		
		super.registerTask(testTask, MockFailingCrossStub.class);
		
		boolean stubFailed = false;
		
		try {
			testTask.invoke();
		} catch (Exception e) {
			stubFailed = true;
		}
		
		Assert.assertTrue("Stub exception was not forwarded.", stubFailed);
		
		this.outList.clear();
		
	}

	@Test
	public void testCancelBlockCrossTaskInit() {
		
		int keyCnt = 10;
		int valCnt = 1;
		
		super.initEnvironment(1*1024*1024);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt, valCnt, false), 1);
		super.addInput(new DelayingInfinitiveInputIterator(100), 2);
		super.addOutput(this.outList);
		
		final CrossTask testTask = new CrossTask();
		super.getTaskConfig().setLocalStrategy(LocalStrategy.NESTEDLOOP_BLOCKED_OUTER_FIRST);
		super.getTaskConfig().setMemorySize(1 * 1024 * 1024);
		
		super.registerTask(testTask, MockCrossStub.class);
		
		Thread taskRunner = new Thread() {
			@Override
			public void run() {
				try {
					testTask.invoke();
				} catch (Exception ie) {
					ie.printStackTrace();
					Assert.fail("Task threw exception although it was properly canceled");
				}
			}
		};
		taskRunner.start();
		
		TaskCancelThread tct = new TaskCancelThread(1, taskRunner, testTask);
		tct.start();
		
		try {
			tct.join();
			taskRunner.join();		
		} catch(InterruptedException ie) {
			Assert.fail("Joining threads failed");
		}
		
	}
	
	@Test
	public void testCancelBlockCrossTaskCrossing() {
		
		int keyCnt = 10;
		int valCnt = 1;
		
		super.initEnvironment(1*1024*1024);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt, valCnt, false), 1);
		super.addInput(new DelayingInfinitiveInputIterator(100), 2);
		super.addOutput(this.outList);
		
		final CrossTask testTask = new CrossTask();
		super.getTaskConfig().setLocalStrategy(LocalStrategy.NESTEDLOOP_BLOCKED_OUTER_SECOND);
		super.getTaskConfig().setMemorySize(1 * 1024 * 1024);
		
		super.registerTask(testTask, MockCrossStub.class);
		
		Thread taskRunner = new Thread() {
			@Override
			public void run() {
				try {
					testTask.invoke();
				} catch (Exception ie) {
					ie.printStackTrace();
					Assert.fail("Task threw exception although it was properly canceled");
				}
			}
		};
		taskRunner.start();
		
		TaskCancelThread tct = new TaskCancelThread(1, taskRunner, testTask);
		tct.start();
		
		try {
			tct.join();
			taskRunner.join();		
		} catch(InterruptedException ie) {
			Assert.fail("Joining threads failed");
		}
		
	}
	
	@Test
	public void testCancelStreamCrossTaskInit() {
		
		int keyCnt = 10;
		int valCnt = 1;
		
		super.initEnvironment(1*1024*1024);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt, valCnt, false), 1);
		super.addInput(new DelayingInfinitiveInputIterator(100), 2);
		super.addOutput(this.outList);
		
		final CrossTask testTask = new CrossTask();
		super.getTaskConfig().setLocalStrategy(LocalStrategy.NESTEDLOOP_STREAMED_OUTER_FIRST);
		super.getTaskConfig().setMemorySize(1 * 1024 * 1024);
		
		super.registerTask(testTask, MockCrossStub.class);
		
		Thread taskRunner = new Thread() {
			@Override
			public void run() {
				try {
					testTask.invoke();
				} catch (Exception ie) {
					ie.printStackTrace();
					Assert.fail("Task threw exception although it was properly canceled");
				}
			}
		};
		taskRunner.start();
		
		TaskCancelThread tct = new TaskCancelThread(1, taskRunner, testTask);
		tct.start();
		
		try {
			tct.join();
			taskRunner.join();		
		} catch(InterruptedException ie) {
			Assert.fail("Joining threads failed");
		}
		
	}
	
	@Test
	public void testCancelStreamCrossTaskCrossing() {
		
		int keyCnt = 10;
		int valCnt = 1;
		
		super.initEnvironment(1*1024*1024);
		super.addInput(new RegularlyGeneratedInputGenerator(keyCnt, valCnt, false), 1);
		super.addInput(new DelayingInfinitiveInputIterator(100), 2);
		super.addOutput(this.outList);
		
		final CrossTask testTask = new CrossTask();
		super.getTaskConfig().setLocalStrategy(LocalStrategy.NESTEDLOOP_STREAMED_OUTER_SECOND);
		super.getTaskConfig().setMemorySize(1 * 1024 * 1024);
		
		super.registerTask(testTask, MockCrossStub.class);
		
		Thread taskRunner = new Thread() {
			@Override
			public void run() {
				try {
					testTask.invoke();
				} catch (Exception ie) {
					ie.printStackTrace();
					Assert.fail("Task threw exception although it was properly canceled");
				}
			}
		};
		taskRunner.start();
		
		TaskCancelThread tct = new TaskCancelThread(1, taskRunner, testTask);
		tct.start();
		
		try {
			tct.join();
			taskRunner.join();		
		} catch(InterruptedException ie) {
			Assert.fail("Joining threads failed");
		}
		
	}
	
	public static class MockCrossStub extends CrossStub<PactInteger, PactInteger, PactInteger, PactInteger, PactInteger, PactInteger> {

		HashSet<Integer> hashSet = new HashSet<Integer>(1000);
		
		@Override
		public void cross(PactInteger key1, PactInteger value1, PactInteger key2, PactInteger value2,
				Collector<PactInteger, PactInteger> out) {
			
			Assert.assertTrue("Key was given multiple times into user code",!this.hashSet.contains(System.identityHashCode(key1)));
			Assert.assertTrue("Key was given multiple times into user code",!this.hashSet.contains(System.identityHashCode(key2)));
			Assert.assertTrue("Value was given multiple times into user code",!this.hashSet.contains(System.identityHashCode(value1)));
			Assert.assertTrue("Value was given multiple times into user code",!this.hashSet.contains(System.identityHashCode(value2)));
			
			this.hashSet.add(System.identityHashCode(key1));
			this.hashSet.add(System.identityHashCode(key2));
			this.hashSet.add(System.identityHashCode(value1));
			this.hashSet.add(System.identityHashCode(value2));
			
			out.collect(key1, value1);
		}
	}
	
	public static class MockFailingCrossStub extends CrossStub<PactInteger, PactInteger, PactInteger, PactInteger, PactInteger, PactInteger> {

		int cnt = 0;
		
		@Override
		public void cross(PactInteger key1, PactInteger value1, PactInteger key2, PactInteger value2,
				Collector<PactInteger, PactInteger> out) {
			
			if(++this.cnt>=10) {
				throw new RuntimeException("Expected Test Exception");
			}
						
			out.collect(key1, value1);
		}
	}
	
	
	
}
