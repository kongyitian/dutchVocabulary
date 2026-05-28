#!/usr/bin/env python3
"""
Update existing vocabulary words with better translations using the translation dictionary.
"""

import re
from translation_dict import DUTCH_ENGLISH_DICT

def update_translations():
    """Update the wiktionary SQL file with better translations"""

    # Read the existing SQL file
    with open('wiktionary_dutch_words.sql', 'r', encoding='utf-8') as f:
        sql_content = f.read()

    # Count improvements
    improved_count = 0
    total_words = 0

    # Function to replace dutch word with translation
    def replace_translation(match):
        nonlocal improved_count, total_words
        total_words += 1

        # Extract parts
        dutch = match.group(1)
        english = match.group(2)
        category = match.group(3)
        difficulty = match.group(4)

        # Check if we have a better translation
        new_translation = DUTCH_ENGLISH_DICT.get(dutch.lower())

        if new_translation and new_translation != dutch:
            improved_count += 1
            return f"('{dutch}', '{new_translation}', '{category}', '{difficulty}')"
        else:
            # Keep original
            return match.group(0)

    # Pattern to match INSERT values
    pattern = r"\('([^']+)',\s*'([^']+)',\s*'([^']+)',\s*'([^']+)'\)"

    # Replace translations
    updated_content = re.sub(pattern, replace_translation, sql_content)

    # Write updated SQL
    with open('wiktionary_dutch_words_translated.sql', 'w', encoding='utf-8') as f:
        f.write("-- Dutch Vocabulary with IMPROVED TRANSLATIONS\n")
        f.write(f"-- Updated {improved_count} out of {total_words} words\n")
        f.write(f"-- Translation coverage: {improved_count}/{total_words} ({improved_count*100//total_words if total_words > 0 else 0}%)\n")
        f.write("\n")
        f.write(updated_content)

    print(f"✓ Updated {improved_count} out of {total_words} words")
    print(f"✓ Translation coverage: {improved_count}/{total_words} ({improved_count*100//total_words if total_words > 0 else 0}%)")
    print(f"✓ Output: wiktionary_dutch_words_translated.sql")

    return improved_count, total_words

if __name__ == "__main__":
    print("Updating translations using dictionary...")
    print(f"Dictionary size: {len(DUTCH_ENGLISH_DICT)} words\n")
    improved, total = update_translations()
    print(f"\nSuccess! {improved} words now have proper English translations.")

