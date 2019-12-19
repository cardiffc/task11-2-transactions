import java.util.*;

public class Main {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Bank bank = new Bank();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
            bank.createAccounts(1000000);
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
            bank.createAccounts(1000000);
            }
        });
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long finish = System.currentTimeMillis();

        System.out.println(finish - start);

        System.out.println(bank.getAccounts().size());

    }
}