-- Test data for integration tests
-- Minimal dataset focused on test scenarios

-- Greetings (at least 3 for quiz tests)
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty, pronunciation) VALUES
('hallo', 'hello', 'Hallo, hoe gaat het?', 'Hello, how are you?', 'greetings', 'EASY', 'hah-LOH'),
('goedemorgen', 'good morning', 'Goedemorgen, sliep je lekker?', 'Good morning, did you sleep well?', 'greetings', 'EASY', 'khoo-duh-MOR-khun'),
('goedemiddag', 'good afternoon', 'Goedemiddag, hoe is uw dag?', 'Good afternoon, how is your day?', 'greetings', 'EASY', 'khoo-duh-MI-dakh'),
('tot ziens', 'goodbye', 'Tot ziens, tot morgen!', 'Goodbye, see you tomorrow!', 'greetings', 'EASY', 'tot ZEENS'),
('dank je wel', 'thank you', 'Dank je wel voor je hulp!', 'Thank you for your help!', 'greetings', 'EASY', 'dahnk yuh VEL');

-- Common Verbs (at least 5 for streak tests)
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('zijn', 'to be', 'Ik ben blij.', 'I am happy.', 'verbs', 'EASY'),
('hebben', 'to have', 'Ik heb een hond.', 'I have a dog.', 'verbs', 'EASY'),
('gaan', 'to go', 'Ik ga naar huis.', 'I am going home.', 'verbs', 'EASY'),
('komen', 'to come', 'Kom je morgen?', 'Are you coming tomorrow?', 'verbs', 'EASY'),
('doen', 'to do', 'Wat doe je?', 'What are you doing?', 'verbs', 'EASY'),
('willen', 'to want', 'Ik wil koffie.', 'I want coffee.', 'verbs', 'MEDIUM'),
('kunnen', 'can/to be able', 'Ik kan zwemmen.', 'I can swim.', 'verbs', 'MEDIUM'),
('moeten', 'must/to have to', 'Ik moet werken.', 'I have to work.', 'verbs', 'HARD');

-- Difficulty-based test data
INSERT INTO vocabulary_words (dutch, english, category, difficulty) VALUES
('een', 'one', 'numbers', 'EASY'),
('twee', 'two', 'numbers', 'EASY'),
('drie', 'three', 'numbers', 'MEDIUM'),
('vier', 'four', 'numbers', 'MEDIUM'),
('vijf', 'five', 'numbers', 'HARD');

