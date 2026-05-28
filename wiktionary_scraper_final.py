#!/usr/bin/env python3
"""
Wiktionary Dutch Word Scraper - Simplified Version
Scrapes the Dutch frequency list and generates SQL with basic translations.
For words we can't automatically translate, we'll use the Dutch word as a placeholder.
"""

import requests
from bs4 import BeautifulSoup
import sys
from urllib.parse import urljoin

# Base URLs
FREQUENCY_LIST_URL = "https://en.wiktionary.org/wiki/Wiktionary:Frequency_lists/Dutch_wordlist"

# Common word translations (to ensure high-frequency words are properly translated)
COMMON_TRANSLATIONS = {
    'ik': 'I',
    'je': 'you',
    'het': 'it, the',
    'de': 'the',
    'dat': 'that',
    'is': 'is',
    'een': 'a, one',
    'niet': 'not',
    'en': 'and',
    'wat': 'what',
    'van': 'of, from',
    'we': 'we',
    'in': 'in',
    'ze': 'they, she',
    'hij': 'he',
    'op': 'on',
    'te': 'to, too',
    'die': 'that, those',
    'voor': 'for, before',
    'ook': 'also',
    'aan': 'to, at',
    'maar': 'but',
    'er': 'there',
    'dan': 'than, then',
    'mijn': 'my',
    'zo': 'so',
    'kan': 'can',
    'zijn': 'to be, his',
    'nu': 'now',
    'me': 'me',
    'wordt': 'becomes, is',
    'nog': 'still, yet',
    'wel': 'indeed, well',
    'heeft': 'has',
    'of': 'or, whether',
    'als': 'if, when',
    'bij': 'at, with',
    'naar': 'to, towards',
    'om': 'around, to',
    'hebben': 'to have',
    'omdat': 'because',
    'waar': 'where',
    'moet': 'must',
    'mensen': 'people',
    'jaar': 'year',
    'man': 'man',
    'weer': 'again, weather',
    'alleen': 'only, alone',
    'tijd': 'time',
    'dag': 'day',
    'ons': 'us, our',
    'nee': 'no',
    'ja': 'yes',
    'goed': 'good, well',
    'hoe': 'how',
    'zonder': 'without',
    'want': 'because, for',
    'hier': 'here',
    'veel': 'much, many',
    'zal': 'will, shall',
    'dus': 'so, thus',
    'geen': 'no, none',
    'zou': 'would, should',
    'meer': 'more',
    'kunnen': 'to be able to',
    'bent': 'are',
    'heb': 'have',
    'ben': 'am',
    'door': 'through, by',
    'heel': 'very, whole',
    'uit': 'out, from',
    'zien': 'to see',
    'jaar': 'year',
    'onder': 'under',
    'over': 'about, over',
    'zoals': 'like, such as',
    'tot': 'until, to',
    'tegen': 'against',
    'na': 'after',
    'tijdens': 'during',
    'nieuw': 'new',
    'groot': 'big, great',
    'kleine': 'small',
    'land': 'country',
    'twee': 'two',
    'drie': 'three',
    'leven': 'life, to live',
    'water': 'water',
    'wereld': 'world',
    'eerste': 'first',
    'laatste': 'last',
    'andere': 'other',
    'hele': 'whole',
    'eigen': 'own',
    'Nederlandse': 'Dutch',
    'Nederland': 'Netherlands',
    'huis': 'house',
    'stad': 'city',
    'vrouw': 'woman',
    'kind': 'child',
    'vader': 'father',
    'moeder': 'mother',
    'vriend': 'friend',
    'naam': 'name',
    'werk': 'work',
    'boek': 'book',
    'school': 'school',
    'hand': 'hand',
    'hoofd': 'head',
    'oog': 'eye',
    'deur': 'door',
    'weg': 'way, road',
    'begin': 'beginning',
    'einde': 'end',
}

def get_soup(url):
    """Fetch and parse a URL"""
    try:
        headers = {
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
        }
        response = requests.get(url, headers=headers, timeout=10)
        response.raise_for_status()
        return BeautifulSoup(response.content, 'html.parser')
    except Exception as e:
        print(f"Error fetching {url}: {e}", file=sys.stderr)
        return None

def scrape_frequency_list():
    """Scrape the Dutch frequency list"""
    print("Fetching frequency list...", file=sys.stderr)
    soup = get_soup(FREQUENCY_LIST_URL)

    if not soup:
        print("Failed to fetch frequency list", file=sys.stderr)
        return []

    words = []

    # Find the ordered list with words
    ols = soup.find_all('ol')
    if not ols:
        print("No ordered lists found", file=sys.stderr)
        return []

    # Get the first (main) ordered list
    main_list = ols[0]
    items = main_list.find_all('li', recursive=False)

    print(f"Found {len(items)} list items", file=sys.stderr)

    for rank, li in enumerate(items, 1):
        # Get the word link
        word_link = li.find('a')
        if word_link:
            dutch_word = word_link.get_text().strip()

            if dutch_word and not dutch_word.startswith('Wiktionary:'):
                words.append({
                    'rank': rank,
                    'dutch': dutch_word,
                })

    print(f"Found {len(words)} words", file=sys.stderr)
    return words

def categorize_word(dutch_word):
    """Attempt to categorize the word based on patterns"""
    word_lower = dutch_word.lower()

    # Articles
    if word_lower in ['de', 'het', 'een']:
        return 'articles'

    # Pronouns
    if word_lower in ['ik', 'jij', 'je', 'hij', 'zij', 'ze', 'wij', 'we', 'jullie', 'u', 'me', 'mij', 'hem', 'haar', 'ons']:
        return 'pronouns'

    # Conjunctions
    if word_lower in ['en', 'of', 'maar', 'want', 'dus', 'omdat', 'als', 'dan', 'hoewel', 'terwijl', 'zodra']:
        return 'conjunctions'

    # Prepositions
    if word_lower in ['in', 'op', 'aan', 'bij', 'van', 'voor', 'na', 'door', 'met', 'uit', 'over', 'onder', 'tegen', 'tussen', 'naar', 'om', 'tot']:
        return 'prepositions'

    # Question words
    if word_lower in ['wat', 'wie', 'waar', 'wanneer', 'waarom', 'hoe', 'welke', 'welk']:
        return 'questions'

    # Adverbs
    if word_lower in ['niet', 'nu', 'dan', 'zo', 'ook', 'al', 'nog', 'wel', 'hier', 'daar', 'waar', 'altijd', 'nooit', 'vaak', 'soms', 'heel', 'zeer', 'erg']:
        return 'adverbs'

    # Numbers
    if word_lower in ['een', 'twee', 'drie', 'vier', 'vijf', 'zes', 'zeven', 'acht', 'negen', 'tien', 'elf', 'twaalf', 'twintig', 'honderd', 'duizend']:
        return 'numbers'

    # Common verbs (infinitive ends in -en, or conjugated forms)
    common_verbs = ['zijn', 'hebben', 'kunnen', 'moeten', 'willen', 'mogen', 'zullen',
                    'gaan', 'komen', 'doen', 'maken', 'zien', 'krijgen', 'geven',
                    'worden', 'blijven', 'staan', 'liggen', 'zitten', 'lopen', 'denken']
    if word_lower in common_verbs or (word_lower.endswith('en') and len(word_lower) > 3):
        return 'verbs'

    # Common adjectives
    if word_lower.endswith('lijk') or word_lower.endswith('isch') or word_lower.endswith('ig'):
        return 'adjectives'

    return 'general'

def assign_difficulty(rank):
    """Assign CEFR difficulty level based on frequency rank"""
    if rank <= 200:
        return 'A1'
    elif rank <= 600:
        return 'A2'
    elif rank <= 1500:
        return 'B1'
    elif rank <= 2500:
        return 'B2'
    elif rank <= 3500:
        return 'C1'
    else:
        return 'C2'

def escape_sql(text):
    """Escape single quotes for SQL"""
    if text:
        return text.replace("'", "''")
    return text

def generate_sql_inserts(words_data):
    """Generate SQL INSERT statements"""
    sql_statements = []
    sql_statements.append("-- ===========================================================")
    sql_statements.append("-- Dutch Vocabulary from Wiktionary Frequency List")
    sql_statements.append(f"-- Total words: {len(words_data)}")
    sql_statements.append("-- Source: https://en.wiktionary.org/wiki/Wiktionary:Frequency_lists/Dutch_wordlist")
    sql_statements.append("-- Organized by frequency and CEFR difficulty levels")
    sql_statements.append("-- ===========================================================")
    sql_statements.append("")

    batch_size = 100
    for i in range(0, len(words_data), batch_size):
        batch = words_data[i:i+batch_size]

        difficulty = batch[0].get('difficulty', 'B1')
        sql_statements.append(f"-- Batch {i//batch_size + 1}: Words {i+1} to {min(i+batch_size, len(words_data))} (Difficulty: {difficulty})")
        sql_statements.append("INSERT INTO vocabulary_words (dutch, english, category, difficulty) VALUES")

        values = []
        for word in batch:
            dutch = escape_sql(word['dutch'])
            english = escape_sql(word.get('english', word['dutch']))
            category = word.get('category', 'general')
            difficulty = word.get('difficulty', 'B1')

            values.append(f"  ('{dutch}', '{english}', '{category}', '{difficulty}')")

        sql_statements.append(',\n'.join(values) + ';')
        sql_statements.append("")

    return '\n'.join(sql_statements)

def main():
    """Main function"""
    print("=" * 60, file=sys.stderr)
    print("Wiktionary Dutch Frequency List Scraper", file=sys.stderr)
    print("=" * 60, file=sys.stderr)

    # Scrape the frequency list
    words = scrape_frequency_list()

    if not words:
        print("No words found", file=sys.stderr)
        return

    # Process words
    processed_words = []
    total = len(words)

    print(f"\nProcessing {total} words...", file=sys.stderr)

    for idx, word_info in enumerate(words, 1):
        dutch_word = word_info['dutch']
        rank = word_info['rank']

        # Show progress
        if idx % 500 == 0:
            print(f"Progress: {idx}/{total} ({idx*100//total}%)", file=sys.stderr)

        # Get translation from common translations or use the Dutch word itself
        english_translation = COMMON_TRANSLATIONS.get(dutch_word.lower(), dutch_word)

        # Categorize and assign difficulty
        category = categorize_word(dutch_word)
        difficulty = assign_difficulty(rank)

        processed_words.append({
            'dutch': dutch_word,
            'english': english_translation,
            'category': category,
            'difficulty': difficulty,
            'rank': rank
        })

    print(f"\nSuccessfully processed {len(processed_words)} words", file=sys.stderr)
    print("Generating SQL...\n", file=sys.stderr)

    # Generate SQL
    sql_output = generate_sql_inserts(processed_words)

    # Write to file
    output_file = 'wiktionary_dutch_words.sql'
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(sql_output)

    print(f"✓ SQL file generated: {output_file}", file=sys.stderr)
    print(f"✓ Total words: {len(processed_words)}", file=sys.stderr)
    print(f"✓ Words with translations: {sum(1 for w in processed_words if w['english'] != w['dutch'])}", file=sys.stderr)
    print("\nYou can now import this file into your database!", file=sys.stderr)

if __name__ == "__main__":
    main()

