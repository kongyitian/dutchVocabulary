-- Initial Dutch vocabulary seed data
-- Common Dutch words with English translations

-- Greetings
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty, pronunciation) VALUES
('hallo', 'hello', 'Hallo, hoe gaat het?', 'Hello, how are you?', 'greetings', 'EASY', 'hah-LOH'),
('goedemorgen', 'good morning', 'Goedemorgen, sliep je lekker?', 'Good morning, did you sleep well?', 'greetings', 'EASY', 'khoo-duh-MOR-khun'),
('goedemiddag', 'good afternoon', 'Goedemiddag, hoe is uw dag?', 'Good afternoon, how is your day?', 'greetings', 'EASY', 'khoo-duh-MI-dakh'),
('goedenavond', 'good evening', 'Goedenavond, welkom thuis!', 'Good evening, welcome home!', 'greetings', 'EASY', 'khoo-duh-NAH-vont'),
('tot ziens', 'goodbye', 'Tot ziens, tot morgen!', 'Goodbye, see you tomorrow!', 'greetings', 'EASY', 'tot ZEENS'),
('alsjeblieft', 'please', 'Koffie, alsjeblieft.', 'Coffee, please.', 'greetings', 'MEDIUM', 'ahs-yuh-BLEEFT'),
('dank je wel', 'thank you', 'Dank je wel voor je hulp!', 'Thank you for your help!', 'greetings', 'EASY', 'dahnk yuh VEL'),
('sorry', 'sorry', 'Sorry, ik ben laat.', 'Sorry, I am late.', 'greetings', 'EASY', 'SOR-ee');

-- Numbers
INSERT INTO vocabulary_words (dutch, english, category, difficulty) VALUES
('een', 'one', 'numbers', 'EASY'),
('twee', 'two', 'numbers', 'EASY'),
('drie', 'three', 'numbers', 'EASY'),
('vier', 'four', 'numbers', 'EASY'),
('vijf', 'five', 'numbers', 'EASY'),
('zes', 'six', 'numbers', 'EASY'),
('zeven', 'seven', 'numbers', 'EASY'),
('acht', 'eight', 'numbers', 'EASY'),
('negen', 'nine', 'numbers', 'EASY'),
('tien', 'ten', 'numbers', 'EASY');

-- Common Verbs
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('zijn', 'to be', 'Ik ben blij.', 'I am happy.', 'verbs', 'EASY'),
('hebben', 'to have', 'Ik heb een hond.', 'I have a dog.', 'verbs', 'EASY'),
('gaan', 'to go', 'Ik ga naar huis.', 'I am going home.', 'verbs', 'EASY'),
('komen', 'to come', 'Kom je morgen?', 'Are you coming tomorrow?', 'verbs', 'EASY'),
('doen', 'to do', 'Wat doe je?', 'What are you doing?', 'verbs', 'EASY'),
('willen', 'to want', 'Ik wil koffie.', 'I want coffee.', 'verbs', 'MEDIUM'),
('kunnen', 'can/to be able', 'Ik kan zwemmen.', 'I can swim.', 'verbs', 'MEDIUM'),
('moeten', 'must/to have to', 'Ik moet werken.', 'I have to work.', 'verbs', 'MEDIUM'),
('zien', 'to see', 'Ik zie je morgen.', 'I see you tomorrow.', 'verbs', 'EASY'),
('eten', 'to eat', 'Ik eet een appel.', 'I eat an apple.', 'verbs', 'EASY'),
('drinken', 'to drink', 'Ik drink water.', 'I drink water.', 'verbs', 'EASY'),
('slapen', 'to sleep', 'Ik slaap goed.', 'I sleep well.', 'verbs', 'EASY'),
('werken', 'to work', 'Ik werk thuis.', 'I work at home.', 'verbs', 'EASY'),
('leren', 'to learn', 'Ik leer Nederlands.', 'I learn Dutch.', 'verbs', 'EASY'),
('spreken', 'to speak', 'Spreekt u Engels?', 'Do you speak English?', 'verbs', 'MEDIUM');

-- Common Nouns
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('huis', 'house', 'Mijn huis is groot.', 'My house is big.', 'nouns', 'EASY'),
('water', 'water', 'Ik drink water.', 'I drink water.', 'nouns', 'EASY'),
('boek', 'book', 'Ik lees een boek.', 'I read a book.', 'nouns', 'EASY'),
('koffie', 'coffee', 'Wil je koffie?', 'Do you want coffee?', 'nouns', 'EASY'),
('thee', 'tea', 'Ik drink thee.', 'I drink tea.', 'nouns', 'EASY'),
('melk', 'milk', 'Melk is gezond.', 'Milk is healthy.', 'nouns', 'EASY'),
('brood', 'bread', 'Ik eet brood.', 'I eat bread.', 'nouns', 'EASY'),
('kaas', 'cheese', 'Nederlandse kaas is lekker.', 'Dutch cheese is delicious.', 'nouns', 'EASY'),
('fiets', 'bicycle', 'Ik ga met de fiets.', 'I go by bicycle.', 'nouns', 'EASY'),
('trein', 'train', 'De trein is laat.', 'The train is late.', 'nouns', 'EASY'),
('auto', 'car', 'Ik heb een auto.', 'I have a car.', 'nouns', 'EASY'),
('werk', 'work', 'Ik ga naar werk.', 'I go to work.', 'nouns', 'EASY'),
('school', 'school', 'De kinderen gaan naar school.', 'The children go to school.', 'nouns', 'EASY'),
('vriend', 'friend', 'Hij is mijn vriend.', 'He is my friend.', 'nouns', 'EASY'),
('vriendin', 'girlfriend/female friend', 'Zij is mijn vriendin.', 'She is my girlfriend.', 'nouns', 'EASY');

-- Days of the week
INSERT INTO vocabulary_words (dutch, english, category, difficulty) VALUES
('maandag', 'Monday', 'days', 'EASY'),
('dinsdag', 'Tuesday', 'days', 'EASY'),
('woensdag', 'Wednesday', 'days', 'MEDIUM'),
('donderdag', 'Thursday', 'days', 'MEDIUM'),
('vrijdag', 'Friday', 'days', 'EASY'),
('zaterdag', 'Saturday', 'days', 'EASY'),
('zondag', 'Sunday', 'days', 'EASY');

-- Colors
INSERT INTO vocabulary_words (dutch, english, category, difficulty) VALUES
('rood', 'red', 'colors', 'EASY'),
('blauw', 'blue', 'colors', 'EASY'),
('groen', 'green', 'colors', 'EASY'),
('geel', 'yellow', 'colors', 'EASY'),
('oranje', 'orange', 'colors', 'EASY'),
('zwart', 'black', 'colors', 'EASY'),
('wit', 'white', 'colors', 'EASY'),
('grijs', 'gray', 'colors', 'EASY'),
('bruin', 'brown', 'colors', 'EASY'),
('paars', 'purple', 'colors', 'EASY');

-- Adjectives
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('groot', 'big', 'Het huis is groot.', 'The house is big.', 'adjectives', 'EASY'),
('klein', 'small', 'De kat is klein.', 'The cat is small.', 'adjectives', 'EASY'),
('goed', 'good', 'Dit is goed nieuws.', 'This is good news.', 'adjectives', 'EASY'),
('slecht', 'bad', 'Het weer is slecht.', 'The weather is bad.', 'adjectives', 'EASY'),
('mooi', 'beautiful', 'De bloemen zijn mooi.', 'The flowers are beautiful.', 'adjectives', 'EASY'),
('lelijk', 'ugly', 'Dat gebouw is lelijk.', 'That building is ugly.', 'adjectives', 'EASY'),
('oud', 'old', 'Mijn oma is oud.', 'My grandma is old.', 'adjectives', 'EASY'),
('nieuw', 'new', 'Ik heb een nieuwe auto.', 'I have a new car.', 'adjectives', 'EASY'),
('lekker', 'delicious/nice', 'Het eten is lekker.', 'The food is delicious.', 'adjectives', 'EASY'),
('duur', 'expensive', 'Amsterdam is duur.', 'Amsterdam is expensive.', 'adjectives', 'EASY'),
('goedkoop', 'cheap', 'Dit is goedkoop.', 'This is cheap.', 'adjectives', 'MEDIUM');

-- Question words
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('wat', 'what', 'Wat is dit?', 'What is this?', 'questions', 'EASY'),
('wie', 'who', 'Wie is dat?', 'Who is that?', 'questions', 'EASY'),
('waar', 'where', 'Waar woon je?', 'Where do you live?', 'questions', 'EASY'),
('wanneer', 'when', 'Wanneer kom je?', 'When are you coming?', 'questions', 'EASY'),
('waarom', 'why', 'Waarom ben je hier?', 'Why are you here?', 'questions', 'EASY'),
('hoe', 'how', 'Hoe gaat het?', 'How are you?', 'questions', 'EASY'),
('hoeveel', 'how much/many', 'Hoeveel kost dit?', 'How much does this cost?', 'questions', 'MEDIUM');

-- Common phrases
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('ik begrijp het', 'I understand', 'Ja, ik begrijp het.', 'Yes, I understand.', 'phrases', 'MEDIUM'),
('ik begrijp het niet', 'I don''t understand', 'Sorry, ik begrijp het niet.', 'Sorry, I don''t understand.', 'phrases', 'MEDIUM'),
('ik spreek een beetje Nederlands', 'I speak a little Dutch', 'Ik spreek een beetje Nederlands.', 'I speak a little Dutch.', 'phrases', 'HARD'),
('hoe zeg je dit in het Nederlands', 'how do you say this in Dutch', 'Hoe zeg je "hello" in het Nederlands?', 'How do you say "hello" in Dutch?', 'phrases', 'HARD'),
('waar is het toilet', 'where is the toilet', 'Excuseer, waar is het toilet?', 'Excuse me, where is the toilet?', 'phrases', 'EASY');

