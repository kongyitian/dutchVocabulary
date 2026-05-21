import React, { useState, useEffect } from 'react';
import { api } from './api';
import { VocabularyWord } from './types';

const WordList: React.FC = () => {
  const [words, setWords] = useState<VocabularyWord[]>([]);
  const [filteredWords, setFilteredWords] = useState<VocabularyWord[]>([]);
  const [categories, setCategories] = useState<string[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>('');
  const [selectedDifficulty, setSelectedDifficulty] = useState<string>('');
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  useEffect(() => {
    filterWords();
  }, [words, selectedCategory, selectedDifficulty, searchTerm]);

  const loadData = async () => {
    try {
      const [wordsData, categoriesData] = await Promise.all([
        api.getWords(),
        api.getCategories(),
      ]);
      setWords(wordsData);
      setCategories(categoriesData);
    } catch (error) {
      console.error('Failed to load data:', error);
    } finally {
      setLoading(false);
    }
  };

  const filterWords = () => {
    let filtered = [...words];

    if (selectedCategory) {
      filtered = filtered.filter(w => w.category === selectedCategory);
    }

    if (selectedDifficulty) {
      filtered = filtered.filter(w => w.difficulty === selectedDifficulty);
    }

    if (searchTerm) {
      const term = searchTerm.toLowerCase();
      filtered = filtered.filter(w =>
        w.dutch.toLowerCase().includes(term) ||
        w.english.toLowerCase().includes(term)
      );
    }

    setFilteredWords(filtered);
  };

  if (loading) {
    return (
      <div className="card">
        <div className="loading">
          <div className="loading-spinner"></div>
          <p>Loading vocabulary...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="card">
      <div className="word-list-header">
        <h2 style={{ color: '#333' }}>📚 Vocabulary ({filteredWords.length} words)</h2>

        <div className="filters">
          <input
            type="text"
            className="search-input"
            placeholder="Search..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />

          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
          >
            <option value="">All Categories</option>
            {categories.map(cat => (
              <option key={cat} value={cat}>
                {cat.charAt(0).toUpperCase() + cat.slice(1)}
              </option>
            ))}
          </select>

          <select
            value={selectedDifficulty}
            onChange={(e) => setSelectedDifficulty(e.target.value)}
          >
            <option value="">All Levels</option>
            <option value="EASY">Easy</option>
            <option value="MEDIUM">Medium</option>
            <option value="HARD">Hard</option>
          </select>
        </div>
      </div>

      <div style={{ overflowX: 'auto' }}>
        <table className="words-table">
          <thead>
            <tr>
              <th>Dutch</th>
              <th>English</th>
              <th>Category</th>
              <th>Level</th>
            </tr>
          </thead>
          <tbody>
            {filteredWords.map(word => (
              <tr key={word.id}>
                <td>
                  <strong>{word.dutch}</strong>
                  {word.pronunciation && (
                    <div style={{ fontSize: '0.8rem', color: '#888' }}>
                      {word.pronunciation}
                    </div>
                  )}
                </td>
                <td>
                  {word.english}
                  {word.example && (
                    <div style={{ fontSize: '0.8rem', color: '#888', fontStyle: 'italic' }}>
                      "{word.example}"
                    </div>
                  )}
                </td>
                <td>
                  {word.category && (
                    <span className="category-badge">{word.category}</span>
                  )}
                </td>
                <td>
                  <span className={`difficulty-badge ${word.difficulty.toLowerCase()}`}>
                    {word.difficulty}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {filteredWords.length === 0 && (
        <p style={{ textAlign: 'center', color: '#666', padding: '20px' }}>
          No words found matching your filters.
        </p>
      )}
    </div>
  );
};

export default WordList;

