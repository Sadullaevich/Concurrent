import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Warehouse {
    static List<Good> goods = new ArrayList<>();
    static Set<String> ids = new HashSet<>();

    static Lock lock = new ReentrantLock();

    public static String generateSummary() {
        lock.lock();
        try {
            String threadName = Thread.currentThread().getName();
            return "[INFO]: " + "Total items so far: " + goods.size() + " (Thread: " + threadName + ")";
        } finally {
            lock.unlock();
        }

    }

    public static void addItems(List<Good> items) {
        lock.lock();
        try {
            for (Good good : items) {
                String goodId = good.getItemId();
                boolean goodRegistered = ids.contains(goodId);
                if (!goodRegistered) {
                    ids.add(good.getItemId());
                    goods.add(good);
                } else {
                    System.out.println("[Warning]: Duplicate goods found!" + goodId);
                }

            }

        } finally {
            lock.unlock();
        }
    }
}