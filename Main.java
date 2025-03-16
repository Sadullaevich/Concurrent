import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(()->{
            String summary = Warehouse.generateSummary();
            System.out.println(summary);
        }, 0,5,TimeUnit.SECONDS);

        ExecutorService fetchExecutors = Executors.newFixedThreadPool(3);

        List<Future<List<Good>>> futures = new ArrayList<>();

        int numberOfSensors = 10;

        for (int i = 1; i < numberOfSensors; i++) {

            futures.add(
                    fetchExecutors.submit(
                            new ScanTask(i)
                    )
            );
        }

        for(Future<List<Good>> future: futures){
            Thread thread = new Thread(()->{
                List<Good> items = null;
                try {
                    items = future.get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println(
                            "[Error]: Main thread interrupting while fetching future results"
                    );
                } catch (ExecutionException e) {
                    System.out.println(
                            "[Error]: A scanning task threw an exception: " + e.getCause()
                    );
                }
                Warehouse.addItems(items);
            });
            thread.setDaemon(true);
            thread.start();
        }

        fetchExecutors.shutdown();
        System.out.println("Shutdown scanning executor initiated.");
        try {
            System.out.println("Scanning executor termination await initiated");
            if(!fetchExecutors.awaitTermination(2, TimeUnit.MINUTES)){
                fetchExecutors.shutdownNow();
            }
        }catch (InterruptedException e){
            fetchExecutors.shutdownNow();
            Thread.currentThread().interrupt();
        }

        scheduler.shutdown();
        System.out.println("Shutdown reporting executor initiated.");
        try {
            System.out.println("Reporting executor termination await initiated");
            if(!scheduler.awaitTermination(30, TimeUnit.SECONDS)){
                scheduler.shutdownNow();
            }
        }catch (InterruptedException e){
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("[INFO]: " + Warehouse.generateSummary());
        System.out.println("All scanning tasks completed. Program ending.");

    }
}