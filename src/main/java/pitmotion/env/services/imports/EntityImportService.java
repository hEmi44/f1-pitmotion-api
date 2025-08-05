package pitmotion.env.services.imports;

import pitmotion.env.configurations.ImportProperties;
import pitmotion.env.debug.Debug;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class EntityImportService<T, W> {

    @Autowired
    protected ImportProperties importProperties;

    protected static final JaroWinklerSimilarity JW_SIM = new JaroWinklerSimilarity();
    protected static final double FUZZY_THRESHOLD = 0.95;

    protected <E> Optional<E> findFuzzyMatch(
        String keySlug,
        List<E> candidates,
        Function<E, String> toSlug
    ) {
        return candidates.stream()
            .map(e -> Map.entry(e, JW_SIM.apply(keySlug, toSlug.apply(e))))
            .filter(e -> e.getValue() >= FUZZY_THRESHOLD)
            .peek(e -> Debug.logger().dump(
                "    fuzzy candidate", toSlug.apply(e.getKey()), "score=", e.getValue()))
            .map(Map.Entry::getKey)
            .findFirst();
    }

    protected void paginatedImport(
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
            sleep(importProperties.getDelayMs());
        }
    }

    protected <R> R fetch(Supplier<R> call) {
        int attempt = 0;
        while (true) {
            try {
                return call.get();
            } catch (HttpClientErrorException e) {
                int status = e.getStatusCode().value();
                if (status == 404) return null;
                if (status == 429 || status == 499) {
                    attempt++;
                    Debug.logger().dump(
                        String.format("Erreur %d (%s) (tentative %d)… Nouvelle tentative dans 60 secondes", status,
                                      status == 429 ? "Rate Limit" : "Canceled",
                                      attempt));
                    sleep(importProperties.getRetryDelayMs());
                    continue;
                }
                throw e;
            } catch (Exception e) {
                attempt++;
                Debug.logger().dump(
                    String.format("Erreur inattendue (tentative %d): %s", attempt, e.getMessage()));
                sleep(importProperties.getRetryDelayMs());
            }
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Import interrompu", ie);
        }
    }

    protected String normalizeUrl(String url) {
        if (url == null) return null;
        return url.trim()
                  .replaceFirst("^http://", "https://")
                  .replaceAll("/+$", "");
    }

    protected String slugify(String input) {
        if (input == null) return "";
        String noAccent = Normalizer.normalize(input, Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return noAccent.toLowerCase()
                       .replaceAll("[^a-z0-9]+", "_")
                       .replaceAll("^_|_$", "");
    }
}
