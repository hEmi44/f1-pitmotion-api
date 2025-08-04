package pitmotion.env.services.imports;

import pitmotion.env.debug.Debug;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface EntityImportService<T, W> {

    default void paginatedImport(
        Function<Integer, W> fetchFunction,
        Function<W, List<T>> extractor,
        Consumer<T> processor,
        int limit
    ) {
        int offset = 0;

        while (true) {
            final int currentOffset = offset;

            W wrapper = fetch(() -> fetchFunction.apply(currentOffset));
            List<T> items = extractor.apply(wrapper);
            if (items == null || items.isEmpty()) break;

            items.forEach(processor);
            if (items.size() < limit) break;

            offset += limit;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Import interrompu", e);
            }
        }
    }

    default <R> R fetch(Supplier<R> call) {
        int attempt = 0;
    
        while (true) {
            try {
                return call.get();
            } catch (HttpClientErrorException e) {
                int status = e.getStatusCode().value();
    
                if (status == 404) {
                    return null;
                }
                if (status == 429) {
                    attempt++;
                    Debug.logger().dump(
                        String.format("Erreur 429 (Rate Limit) (tentative %d) : %s. Nouvelle tentative dans 30 s.",
                                      attempt, e.getMessage())
                    );
                    sleep(30_000);
                    continue;
                }
                throw e;
            } catch (Exception e) {
                attempt++;
                Debug.logger().dump(
                    String.format("Erreur inattendue (tentative %d) : %s. Nouvelle tentative dans 30 s.",
                                  attempt, e.getMessage())
                );
                sleep(30_000);
            }
        }
    }    

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Import interrompu", ie);
        }
    }
}
