import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SolucionConcurrenciaJob {

    // Semáforo con 1 permiso (actúa como un Lock de exclusión mutua)
    private static final Semaphore SEMAFORO = new Semaphore(1);

    public static void main(String[] args) {
        // Simulamos dos hilos (Jobs) que entran casi a la vez
        Runnable job = () -> enviarNotificacion("Factura_123");

        new Thread(job, "Hilo-Job-1").start();
        new Thread(job, "Hilo-Job-2").start();
    }

    public static void enviarNotificacion(String idNotificacion) {
        System.out.println(Thread.currentThread().getName() + " intentando procesar: " + idNotificacion);

        try {
            // 1. Intentamos adquirir el permiso con un timeout
            if (SEMAFORO.tryAcquire(5, TimeUnit.SECONDS)) {
                try {
                    // --- SECCIÓN CRÍTICA ---
                    // Aquí iría la consulta a Oracle: SELECT estado FROM notificaciones...
                    System.out.println(Thread.currentThread().getName() + " ha entrado en la sección crítica.");

                    // Simulamos el "delay" de red o procesamiento que causaba la Race Condition
                    Thread.sleep(2000);

                    System.out.println(Thread.currentThread().getName() + " envió la notificación con éxito.");
                    // Aquí iría el UPDATE estado = 'ENVIADO' en Oracle
                    // -----------------------
                } finally {
                    // 2. IMPORTANTE: Liberar siempre en el finally
                    System.out.println(Thread.currentThread().getName() + " libera el semáforo.");
                    SEMAFORO.release();
                }
            } else {
                // 3. Gestión de colisión
                System.err.println(Thread.currentThread().getName() + " NO pudo procesar: El recurso ya está siendo gestionado por otro hilo.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}