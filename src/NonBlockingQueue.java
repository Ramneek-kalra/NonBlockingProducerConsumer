
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ramneek Kalra
 */
public class NonBlockingQueue        
{
    public static void main(String[] args)
                        throws InterruptedException
    {
        final PC pc = new PC();
        Thread t1 = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    pc.produce();
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        Thread t2 = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    pc.consume();
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        t2.start();
    }
    
    public static class PC
    {
        
        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();
        ReentrantLock lock = new ReentrantLock();
        int capacity = 1;
 
        
        public void produce() throws InterruptedException
        {
            int value = 0;
            while (value <= 100)
            {
                synchronized (this)
                {
                    lock.lock();
                    try {
                        while (queue.size()==capacity)
                        lock.newCondition().await();
                        System.out.println("Producer produced-"+ value);
                        queue.offer(value++);
                        exitNote();
                        lock.newCondition().signalAll();
                    }finally{
                        lock.unlock();
                    }
                    
                }
            }
        }
        public void consume() throws InterruptedException
        {
            while (true)
            {
                synchronized (this)
                {
                    lock.lock();
                    try {
                        while (queue.isEmpty())
                            lock.newCondition().await();
                        int val = queue.poll();
                        System.out.println("Consumer consumed-"+ val+"\n");
                        exitNote();
                        lock.newCondition().signalAll();
                    }finally{
                        lock.unlock();
                    }
                }
            }
        }
        public void exitNote() throws InterruptedException{
            lock.tryLock();
            synchronized(this)
            {
                try {
                    Thread.sleep(500);
                    if(System.in.available() != 0){
                        Scanner s = new Scanner(System.in);
                        if(s.nextLine().toLowerCase().equals("quit"))
                        {
                            System.out.println("Program Exiting...\nThanks for Visiting!");
                            System.exit(0);
                        }
                    }
                    lock.unlock();
                } catch (IOException ex) {
                    Logger.getLogger(NonBlockingQueue.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
