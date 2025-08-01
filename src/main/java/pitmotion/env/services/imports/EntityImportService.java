package pitmotion.env.services.imports;

import pitmotion.env.debug.Debug;

import java.time.Duration;
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
            //Debug.logger().dump("Wrapper reçu (offset " + currentOffset + ") : " + wrapper);
            List<T> items = extractor.apply(wrapper);

            if (items == null || items.isEmpty()) {
                //Debug.logger().dump("Fin de pagination détectée (liste vide).");
                break;
            }

            items.forEach(processor);

            if (items.size() < limit) {
                //Debug.logger().dump("Fin de pagination détectée (dernier lot).");
                break;
            }

            offset += limit;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Import interrompu", e);
            }
        }

        //Debug.logger().dump("Import terminé");
    }

    default <R> R fetch(Supplier<R> call) {
        int attempt = 0;
    
        while (true) {
            try {
                R result = call.get();
                //Debug.logger().dump(result);
                return result;
            } catch (org.springframework.web.client.HttpClientErrorException e) {
                int status = e.getStatusCode().value();
    
                if (status == 404) {
                    Debug.logger().dump("Réponse 404 : ressource non trouvée. Skip.");
                    return null;
                }
    
                if (status == 429) {
                    String msg = String.format(
                        "Erreur 429 (Rate Limit) (tentative %d) : %s. Nouvelle tentative dans 30 secondes.",
                        attempt + 1,
                        e.getMessage()
                    );
                    Debug.logger().dump(msg);
    
                    try {
                        Thread.sleep(Duration.ofSeconds(30).toMillis());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Import interrompu", ie);
                    }
    
                    attempt++;
                    continue;
                }
    
                // autres erreurs client (400, 401, 403, etc)
                throw e;
    
            } catch (Exception e) {
                String msg = String.format(
                    "Erreur inattendue (tentative %d) : %s. Nouvelle tentative dans 30 secondes.",
                    attempt + 1,
                    e.getMessage()
                );
                Debug.logger().dump(msg);
    
                try {
                    Thread.sleep(Duration.ofSeconds(30).toMillis());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Import interrompu", ie);
                }
    
                attempt++;
            }
        }
    }
    
}