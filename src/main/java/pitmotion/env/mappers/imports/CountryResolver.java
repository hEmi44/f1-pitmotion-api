package pitmotion.env.mappers.imports;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pitmotion.env.debug.Debug;
import pitmotion.env.entities.Country;
import pitmotion.env.repositories.CountryRepository;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CountryResolver {

    private final CountryRepository countryRepository;

    private static final Map<String, String> COUNTRY_ALIASES = Map.ofEntries(
        Map.entry("usa", "united states of america"),
        Map.entry("united states", "united states of america"),
        Map.entry("great britain", "united kingdom"),
        Map.entry("russia", "russian federation"),
        Map.entry("czechia", "czech republic")
    );

    private static final Map<String, String> NATIONALITY_TO_COUNTRY = Map.ofEntries(
        Map.entry("american", "united states of america"),
        Map.entry("argentinian", "argentina"),
        Map.entry("australian", "australia"),
        Map.entry("belgian", "belgium"),
        Map.entry("brazilian", "brazil"),
        Map.entry("british", "united kingdom"),
        Map.entry("canadian", "canada"),
        Map.entry("chinese", "china"),
        Map.entry("croatian", "croatia"),
        Map.entry("danish", "denmark"),
        Map.entry("dutch", "netherlands"),
        Map.entry("finnish", "finland"),
        Map.entry("french", "france"),
        Map.entry("german", "germany"),
        Map.entry("indian", "india"),
        Map.entry("indonesian", "indonesia"),
        Map.entry("irish", "ireland"),
        Map.entry("italian", "italy"),
        Map.entry("japanese", "japan"),
        Map.entry("korean", "south korea"),
        Map.entry("malaysian", "malaysia"),
        Map.entry("maltese", "malta"),
        Map.entry("mexican", "mexico"),
        Map.entry("new zealander", "new zealand"),
        Map.entry("norwegian", "norway"),
        Map.entry("philippine", "philippines"),
        Map.entry("polish", "poland"),
        Map.entry("portuguese", "portugal"),
        Map.entry("russian", "russian federation"),
        Map.entry("slovak", "slovakia"),
        Map.entry("slovenian", "slovenia"),
        Map.entry("spanish", "spain"),
        Map.entry("swedish", "sweden"),
        Map.entry("swiss", "switzerland"),
        Map.entry("thai", "thailand"),
        Map.entry("turkish", "turkey"),
        Map.entry("ukrainian", "ukraine"),
        Map.entry("vietnamese", "vietnam"),
        Map.entry("hungarian", "hungary"),
        Map.entry("monegasque", "monaco"),
        Map.entry("austrian", "austria"),
        Map.entry("belarusian", "belarus"),
        Map.entry("colombian", "colombia"),
        Map.entry("ecuadorian", "ecuador"),
        Map.entry("estonian", "estonia"),
        Map.entry("greek", "greece"),
        Map.entry("honduran", "honduras"),
        Map.entry("israeli", "israel"),
        Map.entry("kazakh", "kazakhstan"),
        Map.entry("latvian", "latvia"),
        Map.entry("lithuanian", "lithuania"),
        Map.entry("macedonian", "macedonia"),
        Map.entry("mongolian", "mongolia"),
        Map.entry("montenegrin", "montenegro"),
        Map.entry("moroccan", "morocco"),
        Map.entry("namibian", "namibia"),
        Map.entry("nigerian", "nigeria"),
        Map.entry("palestinian", "palestine"),
        Map.entry("peruvian", "peru"),
        Map.entry("qatari", "qatar"),
        Map.entry("romanian", "romania"),
        Map.entry("saint lucian", "saint lucia"),
        Map.entry("saint vincentian", "saint vincent and the grenadines"),
        Map.entry("south african", "south africa"),
        Map.entry("south korean", "south korea"),
        Map.entry("filipino", "philippines"),
        Map.entry("georgian", "georgia"),
        Map.entry("ghanaian", "ghana"),
        Map.entry("guatemalan", "guatemala"),
        Map.entry("haitian", "haiti"),
        Map.entry("icelandic", "iceland"),
        Map.entry("iranian", "iran"),
        Map.entry("jamaican", "jamaica"),
        Map.entry("kenyan", "kenya"),
        Map.entry("kurdish", "kurdish"),
        Map.entry("lao", "laos"),
        Map.entry("lebanese", "lebanon"),
        Map.entry("libyan", "libya"),
        Map.entry("uruguayan", "uruguay"),
        Map.entry("venezuelan", "venezuela"),
        Map.entry("zambian", "zambia"),
        Map.entry("zimbabwean", "zimbabwe"),
        Map.entry("east german", "germany"),
        Map.entry("west german", "germany"),
        Map.entry("rhodesian", "zimbabwe"),
        Map.entry("liechtensteiner", "liechtenstein"),
        Map.entry("chilean", "chile")
    );

    private String normalize(String input) {
        if (input == null) return "";
        String n = Normalizer.normalize(input, Normalizer.Form.NFD);
        return n.replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).trim();
    }

    public Country resolve(String input) {
        if (input == null) {
            throw new EntityNotFoundException("Nationalité nulle");
        }
    
        String normalizedInput = input;
    
        //Nationnalité composée
        if (input.contains("-")) {
            normalizedInput = input.split("-")[0].trim();
        }
    
        String normalized = normalize(normalizedInput);
    
        String intermediate = NATIONALITY_TO_COUNTRY.getOrDefault(normalized, normalized);
    
        String finalName = COUNTRY_ALIASES.getOrDefault(intermediate, intermediate);
    
        List<Country> allCountries = countryRepository.findAll();
    
        return allCountries.stream()
            .filter(c ->
                normalize(c.getNameFr()).equals(finalName) ||
                normalize(c.getNameEn()).equals(finalName) ||
                normalize(c.getNameEn()).contains(finalName)
            )
            .findFirst()
            .orElseGet(() -> createMissingCountry(input, finalName));
    }
    

    private Country createMissingCountry(String input, String resolved) {
        Debug.logger().dump("Création pays manquant pour nationalité inconnue:", input, "=>", resolved);

        Country fallback = new Country();
        fallback.setNameFr(input);
        fallback.setNameEn(resolved);
        fallback.setCodeIso2("??");
        fallback.setCodeIso3("???");

        return countryRepository.save(fallback);
    }
}
