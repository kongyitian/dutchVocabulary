# Wiktionary Dutch Frequency List Import

## Summary

Successfully imported **4,622 Dutch words** from the Wiktionary frequency list into the database.

### Source
- **URL**: https://en.wiktionary.org/wiki/Wiktionary:Frequency_lists/Dutch_wordlist
- **Total words scraped**: 4,622 words
- **Words added to database**: ~4,360 (after removing duplicates with existing data)
- **Final database size**: 4,786 total words

### Difficulty Distribution (based on frequency)

Words are automatically categorized by CEFR difficulty level based on their frequency rank:

- **A1** (1-200): Most common 200 words
- **A2** (201-600): Next 400 words  
- **B1** (601-1,500): Next 900 words
- **B2** (1,501-2,500): Next 1,000 words
- **C1** (2,501-3,500): Next 1,000 words
- **C2** (3,501-4,622): Remaining 1,122 words

### Category Distribution

Words are automatically categorized into:
- **pronouns**: ik, je, hij, zij, etc.
- **articles**: de, het, een
- **conjunctions**: en, of, maar, omdat, etc.
- **prepositions**: in, op, van, voor, etc.
- **questions**: wat, wie, waar, hoe, etc.
- **adverbs**: niet, nu, zo, ook, etc.
- **verbs**: zijn, hebben, kunnen, words ending in -en
- **adjectives**: words ending in -lijk, -isch, -ig
- **general**: all other words

### Translation Coverage

- **103 high-frequency words** have manual English translations
- **Remaining words** use the Dutch word as a placeholder
- Common words like: ik (I), je (you), het (it, the), de (the), dat (that), etc.

## Files Created

1. **wiktionary_scraper_final.py** - Main scraper script
2. **wiktionary_dutch_words.sql** - Generated SQL with all 4,622 words  
3. **merge_sql.py** - Script to merge with existing data and remove duplicates
4. **src/main/resources/data.sql** - Updated database seed file
5. **src/main/resources/data.sql.backup** - Backup of original data

## Usage

The words are now automatically loaded when the application starts. No additional configuration needed.

### Testing

```bash
# Start the application
./mvnw spring-boot:run

# Check total words
curl http://localhost:8080/api/words | jq 'length'

# Check A1 level words
curl 'http://localhost:8080/api/words?difficulty=A1' | jq 'length'

# View sample words
curl http://localhost:8080/api/words | jq '.[500:510]'
```

## Sample Data

### A1 Level (Most Common)
```
ik = I
je = you
het = it, the
de = the
dat = that
is = is
een = a, one
niet = not
en = and
```

### B1 Level (Intermediate)
```
nieuw = new
ontmoeten = to meet
verschillende = various
```

### C2 Level (Advanced)
```
basketbal = basketball
terugkeer = return
klooster = monastery
```

## Next Steps

To improve translation coverage, you could:

1. **Add more manual translations** to `COMMON_TRANSLATIONS` in the scraper
2. **Use a translation API** (Google Translate, DeepL) to bulk translate remaining words
3. **Crowdsource translations** by allowing users to suggest translations through the app
4. **Scrape individual Wiktionary pages** for translations (slower but more accurate)

## Technical Details

### Database Schema
Words are stored in the `vocabulary_words` table with columns:
- `id` (auto-increment)
- `dutch` (Dutch word)
- `english` (English translation)
- `category` (word category)
- `difficulty` (CEFR level: A1-C2)
- `example` (optional usage example)
- `example_translation` (optional example translation)
- `pronunciation` (optional pronunciation guide)
- `created_at`, `updated_at` (timestamps)

### Deduplication
The merge script automatically removes duplicates by comparing Dutch words (case-insensitive) with the existing 426 curated words in the database.

## Scripts Documentation

### wiktionary_scraper_final.py
Main scraper that:
- Fetches the frequency list from Wiktionary
- Extracts Dutch words from the ordered list
- Assigns difficulty levels based on frequency rank
- Categorizes words by type
- Adds manual translations for 103 common words
- Generates SQL INSERT statements

### merge_sql.py
Merging utility that:
- Reads existing data.sql
- Extracts all Dutch words to avoid duplicates
- Filters new Wiktionary words
- Combines both datasets into final data.sql

## References

- [Wiktionary Dutch Frequency List](https://en.wiktionary.org/wiki/Wiktionary:Frequency_lists/Dutch_wordlist)
- [CEFR Levels Explanation](https://www.coe.int/en/web/common-european-framework-reference-languages/level-descriptions)

