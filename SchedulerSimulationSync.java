import java.util.LinkedList;
import java.util.Queue;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Semaphore;

class Colors {
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String BLUE = "\u001B[34m";
    public static final String RED = "\u001B[31m";
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String WHITE = "\u001B[37m";
    public static final String BRIGHT_WHITE = "\u001B[97m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
}

class SharedResources {
    // Synchronization mechanisms
    public static final ReentrantLock lock = new ReentrantLock();
    public static final Semaphore cpuSemaphore = new Semaphore(1);
    
    public static int contextSwitchCount = 0;
    public static int completedProcessCount = 0;
    public static long totalWaitingTime = 0;
    public static List<String> executionLog = new ArrayList<>();
    
    public static void incrementContextSwitch() {
        lock.lock();
        try {
            contextSwitchCount++;
        } finally {
            lock.unlock();
        }
    }
    
    public static void incrementCompletedProcess() {
        lock.lock();
        try {
            completedProcessCount++;
        } finally {
            lock.unlock();
        }
    }
    
    public static void addWaitingTime(long time) {
        lock.lock();
        try {
            totalWaitingTime += time;
        } finally {
            lock.unlock();
        }
    }
    
    public static void logExecution(String message) {
        lock.lock();
        try {
            executionLog.add(message);
        } finally {
            lock.unlock();
        }
    }
}

class Process implements Runnable {
    private String name;
    private int burstTime;
    private int timeQuantum;
    private int remainingTime;
    private long creationTime;
    private long startTime;
    private long completionTime;
    private int priority;
    
    public Process(String name, int burstTime, int timeQuantum, int priority) {
        this.name = name;
        this.burstTime = burstTime;
        this.timeQuantum = timeQuantum;
        this.remainingTime = burstTime;
        this.priority = priority;
        this.creationTime = System.currentTimeMillis();
        this.startTime = -1;
    }
    
    @Override
    public void run() {
        try {
            SharedResources.cpuSemaphore.acquire();
            
            if (startTime == -1) {
                startTime = System.currentTimeMillis();
            }
            
            SharedResources.incrementContextSwitch();
            
            int runTime = Math.min(timeQuantum, remainingTime);
            String message = "  ▶ " + name + " (Priority: " + priority + ") executing quantum [" + runTime + "ms]";
            System.out.println(Colors.BRIGHT_GREEN + message + Colors.RESET);
            
            SharedResources.logExecution(name + " started quantum execution");
            
            try {
                int steps = 5;
                int stepTime = runTime / steps;
                for (int i = 1; i <= steps; i++) {
                    Thread.sleep(stepTime);
                    int progress = (i * 100) / steps;
                    System.out.print("\r  " + Colors.YELLOW + "⚡" + Colors.RESET + " Progress: " + progress + "%");
                }
                System.out.println();
            } catch (InterruptedException e) {
                System.out.println(Colors.RED + "\n  ✗ " + name + " interrupted." + Colors.RESET);
            }
            
            remainingTime -= runTime;
            
            if (remainingTime > 0) {
                SharedResources.logExecution(name + " yielded CPU");
            } else {
                completionTime = System.currentTimeMillis();
                long waitTime = (completionTime - creationTime) - burstTime;
                SharedResources.addWaitingTime(waitTime);
                SharedResources.incrementCompletedProcess();
                SharedResources.logExecution(name + " completed execution");
                System.out.println(Colors.BRIGHT_GREEN + "  ✓ " + name + " finished!" + Colors.RESET);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            SharedResources.cpuSemaphore.release();
        }
    }
    
    public void runToCompletion() {
        try {
            SharedResources.cpuSemaphore.acquire();
            System.out.println(Colors.BRIGHT_CYAN + "  ⚡ " + name + " running to completion [" + remainingTime + "ms]" + Colors.RESET);
            Thread.sleep(remainingTime);
            remainingTime = 0;
            completionTime = System.currentTimeMillis();
            
            long waitTime = (completionTime - creationTime) - burstTime;
            SharedResources.addWaitingTime(waitTime);
            SharedResources.incrementCompletedProcess();
        } catch (InterruptedException e) {
            System.out.println(Colors.RED + "  ✗ " + name + " interrupted." + Colors.RESET);
        } finally {
            SharedResources.cpuSemaphore.release();
        }
    }
    
    public String getName() { return name; }
    public int getBurstTime() { return burstTime; }
    public int getRemainingTime() { return remainingTime; }
    public int getPriority() { return priority; }
    public boolean isFinished() { return remainingTime <= 0; }
    public long getWaitingTime() { return (completionTime > 0) ? (completionTime - creationTime) - burstTime : 0; }
}

public class SchedulerSimulationSync {
    public static void main(String[] args) {
        int studentID = 442050233; 
        Random random = new Random(studentID);
        
        int timeQuantum = 2000 + random.nextInt(4) * 1000;
        int numProcesses = 10 + random.nextInt(11);
        
        Queue<Thread> processQueue = new LinkedList<>();
        Map<Thread, Process> processMap = new HashMap<>();
        List<Process> allProcesses = new ArrayList<>();
        
        System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN + "CPU SCHEDULER SYNC SIMULATION - ID: " + studentID + Colors.RESET);
        
        for (int i = 1; i <= numProcesses; i++) {
            int burstTime = timeQuantum/2 + random.nextInt(2 * timeQuantum + 1);
            int priority = 1 + random.nextInt(5);
            Process process = new Process("P" + i, burstTime, timeQuantum, priority);
            allProcesses.add(process);
            addProcessToQueue(process, processQueue, processMap);
        }
        
        while (!processQueue.isEmpty()) {
            Thread currentThread = processQueue.poll();
            currentThread.start();
            try {
                currentThread.join();
            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted.");
            }
            
            Process process = processMap.get(currentThread);
            if (!process.isFinished()) {
                if (!processQueue.isEmpty()) {
                    addProcessToQueue(process, processQueue, processMap);
                } else {
                    process.runToCompletion();
                }
            }
        }
        printStatistics(allProcesses);
    }
    
    public static void addProcessToQueue(Process process, Queue<Thread> processQueue, Map<Thread, Process> processMap) {
        Thread thread = new Thread(process);
        processQueue.add(thread);
        processMap.put(thread, process);
    }
    
    public static void printStatistics(List<Process> processes) {
        System.out.println("\n" + Colors.BOLD + "=== SIMULATION RESULTS ===" + Colors.RESET);
        System.out.println("Context Switches: " + SharedResources.contextSwitchCount);
        System.out.println("Completed Processes: " + SharedResources.completedProcessCount);
        System.out.println("Average Waiting Time: " + (SharedResources.totalWaitingTime / processes.size()) + "ms");
        System.out.println("Log Entries: " + SharedResources.executionLog.size());
    }
}
