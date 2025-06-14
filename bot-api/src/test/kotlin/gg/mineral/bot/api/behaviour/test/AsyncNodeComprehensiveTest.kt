package gg.mineral.bot.api.behaviour.test

import gg.mineral.bot.api.behaviour.*
import gg.mineral.bot.api.behaviour.node.BTNode
import gg.mineral.bot.api.behaviour.node.ChildNode
import gg.mineral.bot.api.behaviour.node.composite.SequenceNode
import gg.mineral.bot.api.behaviour.node.leaf.AsyncNode
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.api.controls.Keyboard
import gg.mineral.bot.api.controls.Mouse
import gg.mineral.bot.api.entity.living.player.FakePlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.instance.Session
import gg.mineral.bot.api.screen.Screen
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// Mock event for async testing
class AsyncTestEvent : Event

// Simple mock executor for testing
class SimpleAsyncExecutor : ExecutorService {
    private val submittedTasks = mutableListOf<SimpleFuture<*>>()
    
    override fun submit(task: Runnable): Future<*> {
        val future = SimpleFuture<BTResult>(submittedTasks.size)
        future.setTask(task)
        submittedTasks.add(future)
        return future
    }
    
    override fun <T> submit(task: java.util.concurrent.Callable<T>): Future<T> {
        val future = SimpleFuture<T>(submittedTasks.size)
        future.setCallable(task)
        submittedTasks.add(future)
        return future
    }
    
    override fun <T> submit(task: Runnable, result: T): Future<T> {
        val future = SimpleFuture<T>(submittedTasks.size)
        future.setTask(task)
        submittedTasks.add(future)
        future.markDone(result)
        return future
    }
    
    fun completeTask(future: Future<*>, result: BTResult) {
        // Execute the task if it exists
        val simpleFuture = future as SimpleFuture<BTResult>
        simpleFuture.task?.run()
        simpleFuture.markDone(result)
    }
    
    @Suppress("UNCHECKED_CAST")
    fun getLastSubmittedFuture(): Future<BTResult>? = submittedTasks.lastOrNull() as Future<BTResult>?
    
    override fun shutdown() {}
    override fun shutdownNow(): List<Runnable> = emptyList()
    override fun isShutdown(): Boolean = false
    override fun isTerminated(): Boolean = false
    override fun awaitTermination(timeout: Long, unit: java.util.concurrent.TimeUnit): Boolean = true
    override fun <T> invokeAll(tasks: Collection<java.util.concurrent.Callable<T>>): List<Future<T>> = emptyList()
    override fun <T> invokeAll(tasks: Collection<java.util.concurrent.Callable<T>>, timeout: Long, unit: java.util.concurrent.TimeUnit): List<Future<T>> = emptyList()
    override fun <T> invokeAny(tasks: Collection<java.util.concurrent.Callable<T>>): T = throw NotImplementedError()
    override fun <T> invokeAny(tasks: Collection<java.util.concurrent.Callable<T>>, timeout: Long, unit: java.util.concurrent.TimeUnit): T = throw NotImplementedError()
    override fun execute(command: Runnable) {}
}

class SimpleFuture<T>(private val id: Int) : Future<T> {
    private var isCompleted = false
    private var result: T? = null
    internal var task: Runnable? = null
    internal var callable: java.util.concurrent.Callable<T>? = null
    
    fun setTask(runnable: Runnable) {
        this.task = runnable
    }
    
    fun setCallable(callable: java.util.concurrent.Callable<T>) {
        this.callable = callable
    }
    
    fun markDone(value: T) {
        isCompleted = true
        result = value
    }
    
    override fun cancel(mayInterruptIfRunning: Boolean): Boolean = false
    override fun isCancelled(): Boolean = false
    override fun isDone(): Boolean = isCompleted
    override fun get(): T = result ?: throw IllegalStateException("Task not completed")
    override fun get(timeout: Long, unit: java.util.concurrent.TimeUnit): T = get()
}

// Test behavior tree with simple async executor
class SimpleAsyncTestTree(private val executor: SimpleAsyncExecutor) : BehaviourTree(clientInstance = object : ClientInstance {
    override var behaviourTree: BehaviourTree? = null
    override val latency: Int = 0
    override val currentTick: Int = 0
    override val currentScreen: Screen? = null
    override val configuration: BotConfiguration
        get() = TODO("Not implemented")
    override val keyboard: Keyboard
        get() = TODO("Not implemented")
    override val mouse: Mouse
        get() = TODO("Not implemented")
    override val fakePlayer: FakePlayer
        get() = TODO("Not implemented")
    override val isRunning: Boolean = true
    override fun timeMillis(): Long = System.currentTimeMillis()
    override val gameLoopExecutor: ScheduledExecutorService
        get() = TODO("Not implemented")
    override val asyncExecutor: ExecutorService
        get() = executor
    override fun schedule(runnable: Runnable, delay: Long): Boolean = true
    override val session: Session
        get() = TODO("Not implemented")
    override fun shutdown() {}
    override fun newMouse(): Mouse = TODO("Not implemented")
    override fun newKeyboard(): Keyboard = TODO("Not implemented")
    override val displayHeight: Int = 1080
    override val displayWidth: Int = 1920
    override fun <T : Event> callEvent(event: T): Boolean = true
}) {
    override val rootNode: BTNode
        get() = object : SequenceNode(this) {
            override val children = arrayOf<ChildNode>()
        }
}

// Test async node implementation
class SimpleTestAsyncNode(
    tree: BehaviourTree,
    taskId: Int,
    waitForCompletion: Boolean = true,
    private val tickResult: BTResult = BTResult.SUCCESS
) : AsyncNode(tree, taskId, waitForCompletion) {
    
    var tickCalled = false
    var frameCalled = false
    var eventCalled = false
    
    override fun processTick(): BTResult {
        tickCalled = true
        return tickResult
    }
    
    override fun processFrame(): BTResult {
        frameCalled = true
        return BTResult.SUCCESS
    }
    
    override fun <T : Event> processEvent(event: T): BTResult {
        eventCalled = true
        return BTResult.SUCCESS
    }
}

/**
 * Basic tests for AsyncNode functionality
 */
class AsyncNodeComprehensiveTest {
    private val executor = SimpleAsyncExecutor()
    private val tree = SimpleAsyncTestTree(executor)
    
    @Test
    fun `AsyncNode with waitForCompletion true should return RUNNING until task completes`() {
        val asyncNode = SimpleTestAsyncNode(tree, taskId = 1, waitForCompletion = true)
        
        // First call should start async task and return RUNNING
        val result1 = asyncNode.tick()
        assertEquals(BTResult.RUNNING, result1)
        val future = executor.getLastSubmittedFuture()
        assertTrue(future != null)
        
        // Second call should still return RUNNING (task not complete)
        val result2 = asyncNode.tick()
        assertEquals(BTResult.RUNNING, result2)
        
        // Complete the task
        executor.completeTask(future!!, BTResult.SUCCESS)
        
        // Third call should return SUCCESS
        val result3 = asyncNode.tick()
        assertEquals(BTResult.SUCCESS, result3)
    }
    
    @Test
    fun `AsyncNode with waitForCompletion false should return SUCCESS immediately`() {
        val asyncNode = SimpleTestAsyncNode(tree, taskId = 2, waitForCompletion = false)
        
        // First call should start async task and return SUCCESS immediately
        val result1 = asyncNode.tick()
        assertEquals(BTResult.SUCCESS, result1)
        val future = executor.getLastSubmittedFuture()
        assertTrue(future != null)
        
        // Second call should also return SUCCESS immediately (task still running but we don't wait)
        val result2 = asyncNode.tick()
        assertEquals(BTResult.SUCCESS, result2)
    }
    
    @Test
    fun `AsyncNode should handle task returning FAILURE`() {
        val asyncNode = SimpleTestAsyncNode(tree, taskId = 3, waitForCompletion = true, tickResult = BTResult.FAILURE)
        
        // Start the task
        val result1 = asyncNode.tick()
        assertEquals(BTResult.RUNNING, result1)
        val future = executor.getLastSubmittedFuture()
        assertTrue(future != null)
        
        // Complete with FAILURE
        executor.completeTask(future!!, BTResult.FAILURE)
        
        // Should return FAILURE
        val result2 = asyncNode.tick()
        assertEquals(BTResult.FAILURE, result2)
    }
    
    @Test
    fun `AsyncNode frame method should work independently of tick`() {
        val asyncNode = SimpleTestAsyncNode(tree, taskId = 5, waitForCompletion = true)
        
        // Frame should start its own async task
        val result1 = asyncNode.frame()
        assertEquals(BTResult.RUNNING, result1)
        val future = executor.getLastSubmittedFuture()
        assertTrue(future != null)
        
        // Complete the frame task
        executor.completeTask(future!!, BTResult.SUCCESS)
        
        val result2 = asyncNode.frame()
        assertEquals(BTResult.SUCCESS, result2)
        assertTrue(asyncNode.frameCalled)
    }
    
    @Test
    fun `Multiple AsyncNodes with different task IDs should not interfere`() {
        val asyncNode1 = SimpleTestAsyncNode(tree, taskId = 7, waitForCompletion = true)
        val asyncNode2 = SimpleTestAsyncNode(tree, taskId = 8, waitForCompletion = true)
        
        // Start first task
        val result1a = asyncNode1.tick()
        assertEquals(BTResult.RUNNING, result1a)
        val future1 = executor.getLastSubmittedFuture()
        assertTrue(future1 != null)
        
        // Start second task  
        val result2a = asyncNode2.tick()
        assertEquals(BTResult.RUNNING, result2a)
        val future2 = executor.getLastSubmittedFuture()
        assertTrue(future2 != null)
        assertTrue(future2 != future1)
        
        // Complete only the first task
        executor.completeTask(future1!!, BTResult.SUCCESS)
        
        val result1b = asyncNode1.tick()
        val result2b = asyncNode2.tick()
        assertEquals(BTResult.SUCCESS, result1b)
        assertEquals(BTResult.RUNNING, result2b)
        
        // Complete the second task
        executor.completeTask(future2!!, BTResult.FAILURE)
        
        val result2c = asyncNode2.tick()
        assertEquals(BTResult.FAILURE, result2c)
    }
    
    @Test
    fun `AsyncNode should handle repeated execution after completion`() {
        val asyncNode = SimpleTestAsyncNode(tree, taskId = 9, waitForCompletion = true)
        
        // First execution cycle
        val result1 = asyncNode.tick()
        assertEquals(BTResult.RUNNING, result1)
        val future1 = executor.getLastSubmittedFuture()
        assertTrue(future1 != null)
        
        executor.completeTask(future1!!, BTResult.SUCCESS)
        val result2 = asyncNode.tick()
        assertEquals(BTResult.SUCCESS, result2)
        
        // Second execution cycle should start new task
        val result3 = asyncNode.tick()
        assertEquals(BTResult.RUNNING, result3)
        val future2 = executor.getLastSubmittedFuture()
        assertTrue(future2 != null)
        assertTrue(future2 != future1) // Should be a new future
    }
}