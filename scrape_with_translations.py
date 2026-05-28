#!/usr/bin/env python3
"""
Improved Wiktionary scraper with better translation extraction.
This version more aggressively scrapes translations from Wiktionary pages.
"""

import requests
from bs4 import BeautifulSoup
import time
import sys
import re
from urllib.parse import urljoin

FREQUENCY_LIST_URL = "https://en.wiktionary.org/wiki/Wiktionary:Frequency_lists/Dutch_wordlist"
WIKTIONARY_BASE = "https://en.wiktionary.org"

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

def extract_translation_from_wiktionary(soup, dutch_word):
    """Extract English translation from Wiktionary page - improved version"""
    if not soup:
        return None

    # Look for Dutch section
    dutch_header = soup.find('h2', id='Dutch')
    if not dutch_header:
        return None

    translations = []
    current = dutch_header.find_next_sibling()
    depth = 0
    max_depth = 50

    while current and depth < max_depth:
        depth += 1

        # Stop at next language section
        if current.name == 'h2':
            break

        # Look for definition lists (ol)
        if current.name == 'ol':
            for li in current.find_all('li', recursive=False):
                text = li.get_text().strip()

                # Skip usage notes and quotes
                if any(skip in text.lower() for skip in ['usage notes', 'quotations', 'derived terms', 'related terms', 'see also']):
                    continue

                # Clean the text
                text = re.sub(r'\([^)]*\)', '', text)  # Remove parentheses
                text = re.sub(r'\[[^\]]*\]', '', text)  # Remove brackets
                text = re.sub(r'\s+', ' ', text)  # Normalize whitespace

                # Get the first meaningful part
                parts = [p.strip() for p in text.split(';') if p.strip()]
                if parts:
                    clean_text = parts[0].strip()
                    # Filter out non-translation text
                    if (clean_text and
                        len(clean_text) > 1 and
                        len(clean_text) < 100 and
                        not clean_text.startswith('—') and
                        not clean_text.lower().startswith('see ')):
                        translations.append(clean_text)
                        if len(translations) >= 2:
                            break

        if translations:
            break

        current = current.find_next_sibling()

    if translations:
        return ', '.join(translations[:2])

    return None

def scrape_frequency_list():
    """Scrape the Dutch frequency list"""
    print("Fetching frequency list...", file=sys.stderr)
    soup = get_soup(FREQUENCY_LIST_URL)

    if not soup:
        return []

    words = []
    ols = soup.find_all('ol')
    if not ols:
        return []

    main_list = ols[0]
    items = main_list.find_all('li', recursive=False)

    for rank, li in enumerate(items, 1):
        word_link = li.find('a')
        if word_link:
            dutch_word = word_link.get_text().strip()
            word_url = urljoin(WIKTIONARY_BASE, word_link.get('href', ''))

            if dutch_word and not dutch_word.startswith('Wiktionary:'):
                words.append({
                    'rank': rank,
                    'dutch': dutch_word,
                    'url': word_url,
                })

    print(f"Found {len(words)} words", file=sys.stderr)
    return words

def categorize_word(dutch_word):
    """Categorize word by type"""
    word_lower = dutch_word.lower()

    if word_lower in ['de', 'het', 'een']:
        return 'articles'
    if word_lower in ['ik', 'jij', 'je', 'hij', 'zij', 'ze', 'wij', 'we', 'jullie', 'u', 'me', 'mij', 'hem', 'haar', 'ons']:
        return 'pronouns'
    if word_lower in ['en', 'of', 'maar', 'want', 'dus', 'omdat', 'als', 'dan', 'hoewel', 'terwijl', 'zodra']:
        return 'conjunctions'
    if word_lower in ['in', 'op', 'aan', 'bij', 'van', 'voor', 'na', 'door', 'met', 'uit', 'over', 'onder', 'tegen', 'tussen', 'naar', 'om', 'tot']:
        return 'prepositions'
    if word_lower in ['wat', 'wie', 'waar', 'wanneer', 'waarom', 'hoe', 'welke', 'welk']:
        return 'questions'
    if word_lower in ['niet', 'nu', 'dan', 'zo', 'ook', 'al', 'nog', 'wel', 'hier', 'daar', 'altijd', 'nooit', 'vaak', 'soms', 'heel', 'zeer', 'erg']:
        return 'adverbs'
    if word_lower.endswith('en') and len(word_lower) > 3:
        return 'verbs'
    if word_lower.endswith('lijk') or word_lower.endswith('isch') or word_lower.endswith('ig'):
        return 'adjectives'

    return 'general'

def assign_difficulty(rank):
    """Assign CEFR difficulty level"""
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
    sql = []
    sql.append("-- Dutch Vocabulary from Wiktionary - WITH TRANSLATIONS")
    sql.append(f"-- Total words: {len(words_data)}")
    sql.append("-- Source: https://en.wiktionary.org/wiki/Wiktionary:Frequency_lists/Dutch_wordlist")
    sql.append("")

    batch_size = 100
    for i in range(0, len(words_data), batch_size):
        batch = words_data[i:i+batch_size]
        difficulty = batch[0].get('difficulty', 'B1')

        sql.append(f"-- Batch {i//batch_size + 1}: Words {i+1} to {min(i+batch_size, len(words_data))} ({difficulty})")
        sql.append("INSERT INTO vocabulary_words (dutch, english, category, difficulty) VALUES")

        values = []
        for word in batch:
            dutch = escape_sql(word['dutch'])
            english = escape_sql(word.get('english', word['dutch']))
            category = word.get('category', 'general')
            difficulty = word.get('difficulty', 'B1')
            values.append(f"  ('{dutch}', '{english}', '{category}', '{difficulty}')")

        sql.append(',\n'.join(values) + ';')
        sql.append("")

    return '\n'.join(sql)

def main():
    """Main function - scrape with translations"""
    print("=" * 70, file=sys.stderr)
    print("Wiktionary Dutch Scraper - WITH FULL TRANSLATIONS", file=sys.stderr)
    print("=" * 70, file=sys.stderr)
    print("\nThis will scrape ~4,622 words with translations.", file=sys.stderr)
    print("Estimated time: ~15-20 minutes (0.2s per word)\n", file=sys.stderr)

    # Scrape word list
    words = scrape_frequency_list()
    if not words:
        print("Failed to get word list", file=sys.stderr)
        return

    # Process each word
    processed = []
    total = len(words)
    translated_count = 0

    print(f"Processing {total} words...\n", file=sys.stderr)

    for idx, word_info in enumerate(words, 1):
        dutch_word = word_info['dutch']
        url = word_info['url']
        rank = word_info['rank']

        # Progress
        if idx % 50 == 0:
            success_rate = (translated_count / idx * 100) if idx > 0 else 0
            print(f"Progress: {idx}/{total} ({idx*100//total}%) | Translated: {translated_count} ({success_rate:.1f}%)", file=sys.stderr)

        # Get translation
        soup = get_soup(url)
        translation = extract_translation_from_wiktionary(soup, dutch_word)

        if translation:
            translated_count += 1
        else:
            translation = dutch_word  # Fallback

        processed.append({
            'dutch': dutch_word,
            'english': translation,
            'category': categorize_word(dutch_word),
            'difficulty': assign_difficulty(rank),
            'rank': rank
        })

        # Rate limiting
        time.sleep(0.2)

    print(f"\n✓ Processed {len(processed)} words", file=sys.stderr)
    print(f"✓ Successfully translated: {translated_count}/{total} ({translated_count*100//total}%)", file=sys.stderr)
    print("\nGenerating SQL...\n", file=sys.stderr)

    # Generate SQL
    sql_output = generate_sql_inserts(processed)

    # Write to file
    output_file = 'wiktionary_with_translations.sql'
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(sql_output)

    print(f"✓ SQL file: {output_file}", file=sys.stderr)
    print(f"✓ Total words: {len(processed)}", file=sys.stderr)
    print(f"✓ With translations: {translated_count}", file=sys.stderr)

if __name__ == "__main__":
    main()

