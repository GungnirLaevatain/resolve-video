package task;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class CacheQueue {

    public static BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1024);

}
