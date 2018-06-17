
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
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
        // Object of a class that has both produce()
        // and consume() methods
        final PC pc = new PC();
        // Create producer thread
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
        // Create consumer thread
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
        // Start both threads
        t1.start();
        t2.start();
        // t1 finishes before t2
        t1.join();
        t2.join();
    }
 
    // This class has a Non-Blocking Queue, producer (adds items to queue
    // and consumber (removes items).
    public static class PC
    {
        // Create a queue shared by producer and consumer
        // Size of queue is 2.
        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();
        int capacity = 1;
 
        // Function called by producer thread
        public void produce() throws InterruptedException
        {
            int value = 0;
            while (value <= 100)
            {
                synchronized (this)
                {
                    // producer thread waits while queue
                    // is full
                    while (queue.size()==capacity)
                        wait();
                    System.out.println("Producer produced-"+ value);
                    // to insert the jobs in the queue
                    queue.add(value++);
                    // notifies the consumer thread that
                    // now it can start consuming
                    exitNote();
                    notify();
                }
            }
        }
        // Function called by consumer thread
        public void consume() throws InterruptedException
        {
            while (true)
            {
                synchronized (this)
                {
                    // consumer thread waits while queue
                    // is empty
                    while (queue.isEmpty())
                        wait();
                    //to retrive the ifrst job in the queue
                    int val = queue.remove();
                    System.out.println("Consumer consumed-"+ val+"\n");
                    // Wake up producer thread
                    exitNote();
                    notify();
                }
            }
        }
        public void exitNote() throws InterruptedException{
            
            synchronized(this)
            {
                try {
                    Thread.sleep(5000);
                    if(System.in.available() != 0){
                        Scanner s = new Scanner(System.in);
                        if(s.nextLine().toLowerCase().equals("quit"))
                        {
                            System.out.println("Program Exiting...\nThanks for Visiting!");
                            System.exit(0);
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(NonBlockingQueue.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
