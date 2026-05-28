#!/bin/bash
# Cleanup script for temporary files created during Wiktionary import

echo "Cleaning up temporary files from Wiktionary import..."

# Keep these important files:
# - wiktionary_scraper_final.py (main scraper)
# - wiktionary_dutch_words.sql (generated SQL)
# - merge_sql.py (deduplication utility)
# - add_translations.py (helper utility)

# Remove debug/test files
rm -f check_dutch_section.py
rm -f debug_page_structure.py
rm -f debug_translation.py
rm -f wiktionary_scraper.py
rm -f wiktionary_scraper_test.py
rm -f page_structure.html

echo "✓ Cleaned up temporary debug files"
echo ""
echo "Kept important files:"
echo "  - wiktionary_scraper_final.py"
echo "  - wiktionary_dutch_words.sql"
echo "  - merge_sql.py"
echo "  - add_translations.py"
echo ""
echo "Documentation:"
echo "  - WIKTIONARY_IMPORT.md"
echo "  - IMPORT_COMPLETE.md"
echo "  - README.md (updated)"

