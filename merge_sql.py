#!/usr/bin/env python3
"""
Merge Wiktionary words with existing data.sql, avoiding duplicates
"""

import re
import sys

def extract_dutch_words_from_sql(sql_content):
    """Extract all Dutch words from SQL INSERT statements"""
    words = set()
    # Pattern to match Dutch words in INSERT statements
    # Looking for patterns like ('dutch_word', 'english', ...)
    pattern = r"\('([^']+)',\s*'[^']+',\s*(?:'[^']*',\s*)?(?:'[^']*',\s*)?'[^']+',\s*'[A-C][1-2]'"

    matches = re.findall(pattern, sql_content)
    for match in matches:
        words.add(match.lower())

    return words

def main():
    # Read existing data.sql
    with open('src/main/resources/data.sql.backup', 'r', encoding='utf-8') as f:
        existing_sql = f.read()

    # Extract existing Dutch words
    existing_words = extract_dutch_words_from_sql(existing_sql)
    print(f"Found {len(existing_words)} existing words in data.sql", file=sys.stderr)

    # Read Wiktionary words SQL
    with open('wiktionary_dutch_words.sql', 'r', encoding='utf-8') as f:
        wiktionary_sql = f.read()

    # Extract Wiktionary words and filter out duplicates
    new_inserts = []
    current_batch_header = None
    current_batch_values = []

    lines = wiktionary_sql.split('\n')

    for line in lines:
        # Keep comments and headers
        if line.strip().startswith('--'):
            if current_batch_values:
                # Write previous batch
                if current_batch_values:
                    new_inserts.append(current_batch_header)
                    new_inserts.append("INSERT INTO vocabulary_words (dutch, english, category, difficulty) VALUES")
                    new_inserts.append(',\n'.join(current_batch_values) + ';')
                    new_inserts.append("")
                current_batch_values = []

            # Check if it's a batch header
            if 'Batch' in line:
                current_batch_header = line
            else:
                new_inserts.append(line)

        elif line.strip().startswith('INSERT INTO'):
            # Skip, we'll add it ourselves
            pass

        elif line.strip().startswith('('):
            # Extract the Dutch word from this line
            match = re.search(r"\('([^']+)',", line)
            if match:
                dutch_word = match.group(1)
                # Only add if not in existing words
                if dutch_word.lower() not in existing_words:
                    current_batch_values.append(line.rstrip(',;'))

        elif line.strip().endswith(';'):
            # End of INSERT statement
            if current_batch_values:
                # Write batch
                new_inserts.append(current_batch_header)
                new_inserts.append("INSERT INTO vocabulary_words (dutch, english, category, difficulty) VALUES")
                new_inserts.append(',\n'.join(current_batch_values) + ';')
                new_inserts.append("")
                current_batch_values = []

        elif not line.strip():
            # Empty line
            if not current_batch_values:
                # Only add empty line if we're not in the middle of processing a batch
                pass

    # Write final batch if any
    if current_batch_values:
        new_inserts.append(current_batch_header)
        new_inserts.append("INSERT INTO vocabulary_words (dutch, english, category, difficulty) VALUES")
        new_inserts.append(',\n'.join(current_batch_values) + ';')
        new_inserts.append("")

    # Combine existing SQL with new filtered words
    with open('src/main/resources/data.sql', 'w', encoding='utf-8') as f:
        f.write(existing_sql)
        f.write('\n\n')
        f.write('-- ============================================================\n')
        f.write('-- WIKTIONARY FREQUENCY LIST - Additional Dutch Words\n')
        f.write('-- (Duplicates from existing data have been removed)\n')
        f.write('-- ============================================================\n')
        f.write('\n')
        f.write('\n'.join(new_inserts))

    print(f"✓ Merged data.sql created", file=sys.stderr)
    print(f"✓ Duplicate words filtered", file=sys.stderr)

if __name__ == "__main__":
    main()

