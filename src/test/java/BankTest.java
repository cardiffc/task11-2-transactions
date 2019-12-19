import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BankTest extends TestCase {
    Random random = new Random();
    @Override
    protected void setUp() throws Exception {
        Bank bank = new Bank();
    }

    /** Тестируем метод создания счетов на нормальную работу в многопотоке */
    //@Test
    public void testCreateAccountsForRaceCondition() {
        Bank bank = new Bank();
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 2 ; i++) {
            threads.add(new Thread(() -> bank.createAccounts(1000)));
        }
        threads.forEach(Thread::start);
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) { e.printStackTrace();}

        Assert.assertEquals(2000,bank.getAccounts().size());
    }
    /** Тестируем метод создания счетов на отсутствие стартовых отрицательных балансов */
    @Test
    public void testCreateAccountsForNegativeBalance() {
        Bank bank = new Bank();
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 2 ; i++) {
            threads.add(new Thread(() -> bank.createAccounts(1000)));
        }
        threads.forEach(Thread::start);
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) { e.printStackTrace();}
        int negativeCount = 0;

            for (Map.Entry account : bank.getAccounts().entrySet())             {
                Account testAccount = bank.getAccounts().get(account.getKey());
                negativeCount += (testAccount.getMoney() >= 0) ? 0 : 1;
            }
        Assert.assertEquals(0, negativeCount);
    }
    @Test
    public void testTransfer()
    {
        Bank bank = new Bank();

        /** Задаем количество и создаем счета */
        int accountsNum = 2000000;
        bank.createAccounts(accountsNum);
        HashMap<String , Account> accounts = bank.getAccounts();

        /** Задаем количество предполагаемых трансакций */
        int trasCount = accountsNum / 2;
        /** Задаем количество трансакций, попадающих под СБ */
        int transToCheck = (trasCount / 100) * 5;

        /** Поскольку тестировать будем в 4 потока - получим кол-во трансакций на поток */
        int transPerThread = trasCount / 4;
        int transToCheckThread = transToCheck / 4;
        /** Получим все номера счетов для отдельной обработки */
        List<String> accounNumbers = new ArrayList<>();
        for (Map.Entry account : accounts.entrySet()) {
            accounNumbers.add((String) account.getKey());
        }

        /** Получаем суммарное количество денег на счетах до запуска транзакций */

        /** Создаем 4 потока */
        ArrayList<Thread> threads = new ArrayList<>();
        AtomicInteger count = new AtomicInteger();
        for (int i = 0; i < 4; i++) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < transPerThread -  transToCheckThread; j++) {
                        String fromAccount = accounNumbers.get(random.nextInt(accounNumbers.size()));
                        String toAccount = accounNumbers.get(random.nextInt(accounNumbers.size()));
                        try {
                            bank.transfer(fromAccount, toAccount, random.nextInt(49000));
                            count.incrementAndGet();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }));
        }
       /** Запускаем 4 потока */
       threads.forEach(Thread::start);
       try {
           for (Thread thread : threads) {
               thread.join();
           }
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
       Assert.assertEquals(1000000, count);

    }
}
