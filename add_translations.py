#!/usr/bin/env python3
"""
Helper script to add more translations to the COMMON_TRANSLATIONS dictionary.
This makes it easy to manually add translations for frequently used words.

Usage:
    python3 add_translations.py

Then edit the words_to_add list below with new Dutch-English pairs.
"""

# Add your translations here
words_to_add = [
    # Format: ('dutch_word', 'english_translation')
    # Example:
    # ('lopen', 'to walk'),
    # ('spreken', 'to speak'),
]

def format_for_dict(words):
    """Format word pairs for Python dictionary"""
    lines = []
    for dutch, english in words:
        lines.append(f"    '{dutch}': '{english}',")
    return '\n'.join(lines)

def main():
    if not words_to_add:
        print("No words to add. Edit the words_to_add list in this script.")
        print("\nExample format:")
        print("words_to_add = [")
        print("    ('gaan', 'to go'),")
        print("    ('maken', 'to make'),")
        print("    ('zeggen', 'to say'),")
        print("]")
        return

    print("Add these lines to COMMON_TRANSLATIONS in wiktionary_scraper_final.py:")
    print()
    print(format_for_dict(words_to_add))
    print()
    print(f"Total words to add: {len(words_to_add)}")

if __name__ == "__main__":
    main()

