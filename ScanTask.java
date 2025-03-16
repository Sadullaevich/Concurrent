import java.util.List;
import java.util.concurrent.Callable;

public class ScanTask implements Callable<List<Good>> {
    int scannerId;

    public ScanTask(int scannerId){
        this.scannerId = scannerId;
    }

    @Override
    public List<Good> call() throws Exception {
        String threadName = Thread.currentThread().getName();
        System.out.println(
                "[INFO]: " + threadName + " started scanning (scannerID = " + scannerId + ")."
        );

        List<Good> items = ScannerClient.scan(scannerId);
        System.out.println(
                "[INFO]: " + threadName + " finished scanning (scannerID = " + scannerId + "). Retrieved: " + items.size() + " goods"
        );

        return items;
    }
}
