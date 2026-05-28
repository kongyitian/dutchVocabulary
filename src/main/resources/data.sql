-- Dutch Vocabulary Seed Data - Organized by CEFR Levels (A1-C2)
-- A1: Beginner - Basic words and phrases for everyday survival
-- A2: Elementary - Simple everyday expressions
-- B1: Intermediate - Main points on familiar matters
-- B2: Upper Intermediate - Complex texts and abstract topics
-- C1: Advanced - Demanding texts, implicit meaning
-- C2: Proficient - Near-native mastery

-- =============================================
-- A1 LEVEL - BEGINNER (Basic survival vocabulary)
-- =============================================

-- Greetings (A1)
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty, pronunciation) VALUES
('hallo', 'hello', 'Hallo, hoe gaat het?', 'Hello, how are you?', 'greetings', 'A1', 'hah-LOH'),
('goedemorgen', 'good morning', 'Goedemorgen, sliep je lekker?', 'Good morning, did you sleep well?', 'greetings', 'A1', 'khoo-duh-MOR-khun'),
('goedemiddag', 'good afternoon', 'Goedemiddag, hoe is uw dag?', 'Good afternoon, how is your day?', 'greetings', 'A1', 'khoo-duh-MI-dakh'),
('goedenavond', 'good evening', 'Goedenavond, welkom thuis!', 'Good evening, welcome home!', 'greetings', 'A1', 'khoo-duh-NAH-vont'),
('tot ziens', 'goodbye', 'Tot ziens, tot morgen!', 'Goodbye, see you tomorrow!', 'greetings', 'A1', 'tot ZEENS'),
('dag', 'hi/bye', 'Dag, tot later!', 'Bye, see you later!', 'greetings', 'A1', 'dakh'),
('ja', 'yes', 'Ja, ik wil koffie.', 'Yes, I want coffee.', 'greetings', 'A1', 'yah'),
('nee', 'no', 'Nee, dank je.', 'No, thank you.', 'greetings', 'A1', 'nay'),
('dank je', 'thanks', 'Dank je voor de hulp.', 'Thanks for the help.', 'greetings', 'A1', 'dahnk yuh'),
('sorry', 'sorry', 'Sorry, ik ben laat.', 'Sorry, I am late.', 'greetings', 'A1', 'SOR-ee');

-- Numbers (A1)
INSERT INTO vocabulary_words (dutch, english, category, difficulty) VALUES
('een', 'one', 'numbers', 'A1'),
('twee', 'two', 'numbers', 'A1'),
('drie', 'three', 'numbers', 'A1'),
('vier', 'four', 'numbers', 'A1'),
('vijf', 'five', 'numbers', 'A1'),
('zes', 'six', 'numbers', 'A1'),
('zeven', 'seven', 'numbers', 'A1'),
('acht', 'eight', 'numbers', 'A1'),
('negen', 'nine', 'numbers', 'A1'),
('tien', 'ten', 'numbers', 'A1'),
('elf', 'eleven', 'numbers', 'A1'),
('twaalf', 'twelve', 'numbers', 'A1'),
('twintig', 'twenty', 'numbers', 'A1'),
('honderd', 'hundred', 'numbers', 'A1'),
('duizend', 'thousand', 'numbers', 'A1');

-- Basic Pronouns (A1)
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('ik', 'I', 'Ik ben student.', 'I am a student.', 'pronouns', 'A1'),
('jij', 'you (informal)', 'Jij bent mijn vriend.', 'You are my friend.', 'pronouns', 'A1'),
('hij', 'he', 'Hij is groot.', 'He is tall.', 'pronouns', 'A1'),
('zij', 'she', 'Zij is mooi.', 'She is beautiful.', 'pronouns', 'A1'),
('wij', 'we', 'Wij gaan naar huis.', 'We are going home.', 'pronouns', 'A1'),
('jullie', 'you (plural)', 'Jullie zijn welkom.', 'You are welcome.', 'pronouns', 'A1'),
('zij', 'they', 'Zij komen morgen.', 'They are coming tomorrow.', 'pronouns', 'A1'),
('u', 'you (formal)', 'Hoe gaat het met u?', 'How are you?', 'pronouns', 'A1');

-- Basic Verbs (A1)
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('zijn', 'to be', 'Ik ben blij.', 'I am happy.', 'verbs', 'A1'),
('hebben', 'to have', 'Ik heb een hond.', 'I have a dog.', 'verbs', 'A1'),
('gaan', 'to go', 'Ik ga naar huis.', 'I am going home.', 'verbs', 'A1'),
('komen', 'to come', 'Kom je morgen?', 'Are you coming tomorrow?', 'verbs', 'A1'),
('doen', 'to do', 'Wat doe je?', 'What are you doing?', 'verbs', 'A1'),
('zien', 'to see', 'Ik zie je morgen.', 'I see you tomorrow.', 'verbs', 'A1'),
('eten', 'to eat', 'Ik eet een appel.', 'I eat an apple.', 'verbs', 'A1'),
('drinken', 'to drink', 'Ik drink water.', 'I drink water.', 'verbs', 'A1'),
('slapen', 'to sleep', 'Ik slaap goed.', 'I sleep well.', 'verbs', 'A1'),
('wonen', 'to live', 'Ik woon in Amsterdam.', 'I live in Amsterdam.', 'verbs', 'A1'),
('heten', 'to be called', 'Ik heet Jan.', 'My name is Jan.', 'verbs', 'A1'),
('lopen', 'to walk', 'Ik loop naar school.', 'I walk to school.', 'verbs', 'A1');

-- Basic Nouns (A1)
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('huis', 'house', 'Mijn huis is groot.', 'My house is big.', 'nouns', 'A1'),
('water', 'water', 'Ik drink water.', 'I drink water.', 'nouns', 'A1'),
('boek', 'book', 'Ik lees een boek.', 'I read a book.', 'nouns', 'A1'),
('koffie', 'coffee', 'Wil je koffie?', 'Do you want coffee?', 'nouns', 'A1'),
('thee', 'tea', 'Ik drink thee.', 'I drink tea.', 'nouns', 'A1'),
('melk', 'milk', 'Melk is gezond.', 'Milk is healthy.', 'nouns', 'A1'),
('brood', 'bread', 'Ik eet brood.', 'I eat bread.', 'nouns', 'A1'),
('kaas', 'cheese', 'Nederlandse kaas is lekker.', 'Dutch cheese is delicious.', 'nouns', 'A1'),
('fiets', 'bicycle', 'Ik ga met de fiets.', 'I go by bicycle.', 'nouns', 'A1'),
('auto', 'car', 'Ik heb een auto.', 'I have a car.', 'nouns', 'A1'),
('school', 'school', 'De kinderen gaan naar school.', 'The children go to school.', 'nouns', 'A1'),
('man', 'man', 'De man is groot.', 'The man is tall.', 'nouns', 'A1'),
('vrouw', 'woman', 'De vrouw is mooi.', 'The woman is beautiful.', 'nouns', 'A1'),
('kind', 'child', 'Het kind speelt.', 'The child plays.', 'nouns', 'A1'),
('dag', 'day', 'Het is een mooie dag.', 'It is a beautiful day.', 'nouns', 'A1'),
('nacht', 'night', 'Goede nacht!', 'Good night!', 'nouns', 'A1');

-- Days of the Week (A1)
INSERT INTO vocabulary_words (dutch, english, category, difficulty) VALUES
('maandag', 'Monday', 'days', 'A1'),
('dinsdag', 'Tuesday', 'days', 'A1'),
('woensdag', 'Wednesday', 'days', 'A1'),
('donderdag', 'Thursday', 'days', 'A1'),
('vrijdag', 'Friday', 'days', 'A1'),
('zaterdag', 'Saturday', 'days', 'A1'),
('zondag', 'Sunday', 'days', 'A1');

-- Colors (A1)
INSERT INTO vocabulary_words (dutch, english, category, difficulty) VALUES
('rood', 'red', 'colors', 'A1'),
('blauw', 'blue', 'colors', 'A1'),
('groen', 'green', 'colors', 'A1'),
('geel', 'yellow', 'colors', 'A1'),
('oranje', 'orange', 'colors', 'A1'),
('zwart', 'black', 'colors', 'A1'),
('wit', 'white', 'colors', 'A1');

-- Basic Adjectives (A1)
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('groot', 'big', 'Het huis is groot.', 'The house is big.', 'adjectives', 'A1'),
('klein', 'small', 'De kat is klein.', 'The cat is small.', 'adjectives', 'A1'),
('goed', 'good', 'Dit is goed nieuws.', 'This is good news.', 'adjectives', 'A1'),
('slecht', 'bad', 'Het weer is slecht.', 'The weather is bad.', 'adjectives', 'A1'),
('mooi', 'beautiful', 'De bloemen zijn mooi.', 'The flowers are beautiful.', 'adjectives', 'A1'),
('oud', 'old', 'Mijn oma is oud.', 'My grandma is old.', 'adjectives', 'A1'),
('nieuw', 'new', 'Ik heb een nieuwe auto.', 'I have a new car.', 'adjectives', 'A1'),
('lekker', 'delicious', 'Het eten is lekker.', 'The food is delicious.', 'adjectives', 'A1'),
('warm', 'warm/hot', 'Het is warm vandaag.', 'It is warm today.', 'adjectives', 'A1'),
('koud', 'cold', 'Het is koud buiten.', 'It is cold outside.', 'adjectives', 'A1');

-- Question Words (A1)
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('wat', 'what', 'Wat is dit?', 'What is this?', 'questions', 'A1'),
('wie', 'who', 'Wie is dat?', 'Who is that?', 'questions', 'A1'),
('waar', 'where', 'Waar woon je?', 'Where do you live?', 'questions', 'A1'),
('wanneer', 'when', 'Wanneer kom je?', 'When are you coming?', 'questions', 'A1'),
('hoe', 'how', 'Hoe gaat het?', 'How are you?', 'questions', 'A1');

-- =============================================
-- A2 LEVEL - ELEMENTARY
-- =============================================

-- A2 Verbs
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('willen', 'to want', 'Ik wil koffie.', 'I want coffee.', 'verbs', 'A2'),
('kunnen', 'can/to be able', 'Ik kan zwemmen.', 'I can swim.', 'verbs', 'A2'),
('moeten', 'must/to have to', 'Ik moet werken.', 'I have to work.', 'verbs', 'A2'),
('mogen', 'may/to be allowed', 'Mag ik binnenkomen?', 'May I come in?', 'verbs', 'A2'),
('werken', 'to work', 'Ik werk thuis.', 'I work at home.', 'verbs', 'A2'),
('leren', 'to learn', 'Ik leer Nederlands.', 'I learn Dutch.', 'verbs', 'A2'),
('spreken', 'to speak', 'Spreekt u Engels?', 'Do you speak English?', 'verbs', 'A2'),
('lezen', 'to read', 'Ik lees de krant.', 'I read the newspaper.', 'verbs', 'A2'),
('schrijven', 'to write', 'Ik schrijf een brief.', 'I write a letter.', 'verbs', 'A2'),
('kopen', 'to buy', 'Ik koop een boek.', 'I buy a book.', 'verbs', 'A2'),
('verkopen', 'to sell', 'Hij verkoopt auto''s.', 'He sells cars.', 'verbs', 'A2'),
('betalen', 'to pay', 'Ik betaal met kaart.', 'I pay by card.', 'verbs', 'A2'),
('beginnen', 'to begin', 'De film begint om acht uur.', 'The movie starts at eight.', 'verbs', 'A2'),
('eindigen', 'to end', 'De les eindigt om vijf uur.', 'The class ends at five.', 'verbs', 'A2'),
('wachten', 'to wait', 'Ik wacht op de bus.', 'I wait for the bus.', 'verbs', 'A2'),
('zoeken', 'to search', 'Ik zoek mijn sleutels.', 'I search for my keys.', 'verbs', 'A2'),
('vinden', 'to find', 'Ik vind het leuk.', 'I find it nice.', 'verbs', 'A2'),
('denken', 'to think', 'Ik denk dat het waar is.', 'I think it is true.', 'verbs', 'A2'),
('weten', 'to know (fact)', 'Ik weet het niet.', 'I don''t know.', 'verbs', 'A2'),
('kennen', 'to know (person)', 'Ken je hem?', 'Do you know him?', 'verbs', 'A2');

-- A2 Nouns
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('trein', 'train', 'De trein is laat.', 'The train is late.', 'nouns', 'A2'),
('bus', 'bus', 'Ik neem de bus.', 'I take the bus.', 'nouns', 'A2'),
('vliegtuig', 'airplane', 'Het vliegtuig vertrekt om tien uur.', 'The plane departs at ten.', 'nouns', 'A2'),
('station', 'station', 'Ik ben op het station.', 'I am at the station.', 'nouns', 'A2'),
('ziekenhuis', 'hospital', 'Mijn moeder werkt in het ziekenhuis.', 'My mother works in the hospital.', 'nouns', 'A2'),
('apotheek', 'pharmacy', 'De apotheek is dichtbij.', 'The pharmacy is nearby.', 'nouns', 'A2'),
('bank', 'bank', 'Ik ga naar de bank.', 'I go to the bank.', 'nouns', 'A2'),
('winkel', 'shop/store', 'De winkel is open.', 'The shop is open.', 'nouns', 'A2'),
('supermarkt', 'supermarket', 'Ik koop eten in de supermarkt.', 'I buy food at the supermarket.', 'nouns', 'A2'),
('restaurant', 'restaurant', 'Wij eten in een restaurant.', 'We eat at a restaurant.', 'nouns', 'A2'),
('kantoor', 'office', 'Ik werk op kantoor.', 'I work at the office.', 'nouns', 'A2'),
('vriend', 'friend (male)', 'Hij is mijn vriend.', 'He is my friend.', 'nouns', 'A2'),
('vriendin', 'friend (female)', 'Zij is mijn vriendin.', 'She is my friend.', 'nouns', 'A2'),
('familie', 'family', 'Mijn familie is groot.', 'My family is big.', 'nouns', 'A2'),
('vader', 'father', 'Mijn vader is arts.', 'My father is a doctor.', 'nouns', 'A2'),
('moeder', 'mother', 'Mijn moeder kookt.', 'My mother cooks.', 'nouns', 'A2'),
('broer', 'brother', 'Ik heb een broer.', 'I have a brother.', 'nouns', 'A2'),
('zus', 'sister', 'Mijn zus is jonger.', 'My sister is younger.', 'nouns', 'A2'),
('werk', 'work/job', 'Ik ga naar werk.', 'I go to work.', 'nouns', 'A2'),
('vakantie', 'vacation', 'Ik ga op vakantie.', 'I go on vacation.', 'nouns', 'A2');

-- A2 Adjectives
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('duur', 'expensive', 'Amsterdam is duur.', 'Amsterdam is expensive.', 'adjectives', 'A2'),
('goedkoop', 'cheap', 'Dit is goedkoop.', 'This is cheap.', 'adjectives', 'A2'),
('lelijk', 'ugly', 'Dat gebouw is lelijk.', 'That building is ugly.', 'adjectives', 'A2'),
('jong', 'young', 'De kinderen zijn jong.', 'The children are young.', 'adjectives', 'A2'),
('snel', 'fast', 'De auto is snel.', 'The car is fast.', 'adjectives', 'A2'),
('langzaam', 'slow', 'De trein is langzaam.', 'The train is slow.', 'adjectives', 'A2'),
('makkelijk', 'easy', 'Nederlands is niet makkelijk.', 'Dutch is not easy.', 'adjectives', 'A2'),
('moeilijk', 'difficult', 'De test is moeilijk.', 'The test is difficult.', 'adjectives', 'A2'),
('druk', 'busy', 'Ik ben druk vandaag.', 'I am busy today.', 'adjectives', 'A2'),
('rustig', 'quiet/calm', 'Het is rustig hier.', 'It is quiet here.', 'adjectives', 'A2'),
('interessant', 'interesting', 'Het boek is interessant.', 'The book is interesting.', 'adjectives', 'A2'),
('saai', 'boring', 'De film is saai.', 'The movie is boring.', 'adjectives', 'A2'),
('blij', 'happy', 'Ik ben blij.', 'I am happy.', 'adjectives', 'A2'),
('verdrietig', 'sad', 'Zij is verdrietig.', 'She is sad.', 'adjectives', 'A2'),
('boos', 'angry', 'Hij is boos.', 'He is angry.', 'adjectives', 'A2');

-- A2 Time Words
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('vandaag', 'today', 'Vandaag is het maandag.', 'Today is Monday.', 'time', 'A2'),
('morgen', 'tomorrow', 'Tot morgen!', 'See you tomorrow!', 'time', 'A2'),
('gisteren', 'yesterday', 'Gisteren regende het.', 'Yesterday it rained.', 'time', 'A2'),
('nu', 'now', 'Ik kom nu.', 'I am coming now.', 'time', 'A2'),
('later', 'later', 'Tot later!', 'See you later!', 'time', 'A2'),
('vroeg', 'early', 'Ik sta vroeg op.', 'I wake up early.', 'time', 'A2'),
('laat', 'late', 'Sorry, ik ben laat.', 'Sorry, I am late.', 'time', 'A2'),
('altijd', 'always', 'Ik drink altijd koffie.', 'I always drink coffee.', 'time', 'A2'),
('nooit', 'never', 'Ik rook nooit.', 'I never smoke.', 'time', 'A2'),
('vaak', 'often', 'Ik fiets vaak.', 'I often cycle.', 'time', 'A2'),
('soms', 'sometimes', 'Soms ga ik naar de bioscoop.', 'Sometimes I go to the cinema.', 'time', 'A2');

-- A2 Phrases
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('alsjeblieft', 'please', 'Koffie, alsjeblieft.', 'Coffee, please.', 'phrases', 'A2'),
('dank je wel', 'thank you', 'Dank je wel voor je hulp!', 'Thank you for your help!', 'phrases', 'A2'),
('ik begrijp het', 'I understand', 'Ja, ik begrijp het.', 'Yes, I understand.', 'phrases', 'A2'),
('ik begrijp het niet', 'I don''t understand', 'Sorry, ik begrijp het niet.', 'Sorry, I don''t understand.', 'phrases', 'A2'),
('waar is het toilet', 'where is the toilet', 'Excuseer, waar is het toilet?', 'Excuse me, where is the toilet?', 'phrases', 'A2'),
('hoeveel kost dit', 'how much does this cost', 'Hoeveel kost dit boek?', 'How much does this book cost?', 'phrases', 'A2');

-- =============================================
-- B1 LEVEL - INTERMEDIATE
-- =============================================

-- B1 Verbs
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('vergeten', 'to forget', 'Ik vergeet soms mijn sleutels.', 'I sometimes forget my keys.', 'verbs', 'B1'),
('herinneren', 'to remember', 'Ik herinner me die dag goed.', 'I remember that day well.', 'verbs', 'B1'),
('proberen', 'to try', 'Ik probeer Nederlands te leren.', 'I try to learn Dutch.', 'verbs', 'B1'),
('beslissen', 'to decide', 'Ik moet nu beslissen.', 'I have to decide now.', 'verbs', 'B1'),
('veranderen', 'to change', 'Het weer verandert snel.', 'The weather changes quickly.', 'verbs', 'B1'),
('uitleggen', 'to explain', 'Kun je dat uitleggen?', 'Can you explain that?', 'verbs', 'B1'),
('ontwikkelen', 'to develop', 'We ontwikkelen nieuwe software.', 'We develop new software.', 'verbs', 'B1'),
('vergelijken', 'to compare', 'Je kunt prijzen vergelijken.', 'You can compare prices.', 'verbs', 'B1'),
('organiseren', 'to organize', 'Zij organiseert het feest.', 'She organizes the party.', 'verbs', 'B1'),
('communiceren', 'to communicate', 'We communiceren via e-mail.', 'We communicate via email.', 'verbs', 'B1'),
('bereiken', 'to reach/achieve', 'Je kunt je doel bereiken.', 'You can reach your goal.', 'verbs', 'B1'),
('verschijnen', 'to appear', 'Het boek verschijnt morgen.', 'The book appears tomorrow.', 'verbs', 'B1'),
('verdwijnen', 'to disappear', 'De zon verdwijnt achter de wolken.', 'The sun disappears behind the clouds.', 'verbs', 'B1'),
('bestaan', 'to exist', 'Geluk bestaat.', 'Happiness exists.', 'verbs', 'B1'),
('gebeuren', 'to happen', 'Wat is er gebeurd?', 'What happened?', 'verbs', 'B1'),
('lijken', 'to seem/appear', 'Het lijkt makkelijk.', 'It seems easy.', 'verbs', 'B1'),
('voelen', 'to feel', 'Ik voel me goed.', 'I feel good.', 'verbs', 'B1'),
('geloven', 'to believe', 'Ik geloof je.', 'I believe you.', 'verbs', 'B1'),
('hopen', 'to hope', 'Ik hoop dat het lukt.', 'I hope it works out.', 'verbs', 'B1'),
('verwachten', 'to expect', 'Ik verwacht je om vijf uur.', 'I expect you at five.', 'verbs', 'B1');

-- B1 Nouns
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('ervaring', 'experience', 'Ik heb veel ervaring.', 'I have a lot of experience.', 'nouns', 'B1'),
('mening', 'opinion', 'Wat is je mening?', 'What is your opinion?', 'nouns', 'B1'),
('probleem', 'problem', 'We hebben een probleem.', 'We have a problem.', 'nouns', 'B1'),
('oplossing', 'solution', 'Er is altijd een oplossing.', 'There is always a solution.', 'nouns', 'B1'),
('mogelijkheid', 'possibility', 'Er zijn veel mogelijkheden.', 'There are many possibilities.', 'nouns', 'B1'),
('gelegenheid', 'opportunity', 'Dit is een goede gelegenheid.', 'This is a good opportunity.', 'nouns', 'B1'),
('ontwikkeling', 'development', 'De ontwikkeling gaat snel.', 'The development is fast.', 'nouns', 'B1'),
('voorwaarde', 'condition', 'Onder één voorwaarde.', 'Under one condition.', 'nouns', 'B1'),
('situatie', 'situation', 'De situatie is ingewikkeld.', 'The situation is complicated.', 'nouns', 'B1'),
('verhouding', 'relationship/ratio', 'Onze verhouding is goed.', 'Our relationship is good.', 'nouns', 'B1'),
('samenleving', 'society', 'De Nederlandse samenleving.', 'Dutch society.', 'nouns', 'B1'),
('cultuur', 'culture', 'De Nederlandse cultuur is interessant.', 'Dutch culture is interesting.', 'nouns', 'B1'),
('traditie', 'tradition', 'Het is een oude traditie.', 'It is an old tradition.', 'nouns', 'B1'),
('toekomst', 'future', 'De toekomst ziet er goed uit.', 'The future looks good.', 'nouns', 'B1'),
('verleden', 'past', 'Het verleden is voorbij.', 'The past is over.', 'nouns', 'B1'),
('doel', 'goal/aim', 'Wat is je doel?', 'What is your goal?', 'nouns', 'B1'),
('resultaat', 'result', 'Het resultaat is positief.', 'The result is positive.', 'nouns', 'B1'),
('invloed', 'influence', 'Hij heeft veel invloed.', 'He has a lot of influence.', 'nouns', 'B1'),
('belang', 'interest/importance', 'Het is van groot belang.', 'It is of great importance.', 'nouns', 'B1'),
('voordeel', 'advantage', 'Het voordeel is duidelijk.', 'The advantage is clear.', 'nouns', 'B1'),
('nadeel', 'disadvantage', 'Er zijn ook nadelen.', 'There are also disadvantages.', 'nouns', 'B1');

-- B1 Adjectives
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('belangrijk', 'important', 'Dit is heel belangrijk.', 'This is very important.', 'adjectives', 'B1'),
('noodzakelijk', 'necessary', 'Het is noodzakelijk om te oefenen.', 'It is necessary to practice.', 'adjectives', 'B1'),
('mogelijk', 'possible', 'Is het mogelijk om te helpen?', 'Is it possible to help?', 'adjectives', 'B1'),
('onmogelijk', 'impossible', 'Dat is onmogelijk!', 'That is impossible!', 'adjectives', 'B1'),
('tevreden', 'satisfied', 'Ik ben tevreden met het resultaat.', 'I am satisfied with the result.', 'adjectives', 'B1'),
('ontevreden', 'dissatisfied', 'De klanten zijn ontevreden.', 'The customers are dissatisfied.', 'adjectives', 'B1'),
('verantwoordelijk', 'responsible', 'Wie is verantwoordelijk?', 'Who is responsible?', 'adjectives', 'B1'),
('beschikbaar', 'available', 'Ben je beschikbaar morgen?', 'Are you available tomorrow?', 'adjectives', 'B1'),
('geschikt', 'suitable', 'Dit is geschikt voor kinderen.', 'This is suitable for children.', 'adjectives', 'B1'),
('ingewikkeld', 'complicated', 'De situatie is ingewikkeld.', 'The situation is complicated.', 'adjectives', 'B1'),
('duidelijk', 'clear', 'De instructies zijn duidelijk.', 'The instructions are clear.', 'adjectives', 'B1'),
('zeker', 'certain/sure', 'Ik ben zeker.', 'I am sure.', 'adjectives', 'B1'),
('onzeker', 'uncertain', 'De toekomst is onzeker.', 'The future is uncertain.', 'adjectives', 'B1'),
('positief', 'positive', 'Het resultaat is positief.', 'The result is positive.', 'adjectives', 'B1'),
('negatief', 'negative', 'De reactie was negatief.', 'The reaction was negative.', 'adjectives', 'B1');

-- B1 Connectors
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('hoewel', 'although', 'Hoewel het regent, ga ik fietsen.', 'Although it rains, I go cycling.', 'connectors', 'B1'),
('daarom', 'therefore', 'Hij is ziek, daarom blijft hij thuis.', 'He is sick, therefore he stays home.', 'connectors', 'B1'),
('bovendien', 'moreover', 'Bovendien is het goedkoop.', 'Moreover, it is cheap.', 'connectors', 'B1'),
('echter', 'however', 'Het is mooi, echter duur.', 'It is nice, however expensive.', 'connectors', 'B1'),
('terwijl', 'while', 'Terwijl hij las, sliep zij.', 'While he read, she slept.', 'connectors', 'B1'),
('zodra', 'as soon as', 'Zodra ik klaar ben, kom ik.', 'As soon as I am ready, I come.', 'connectors', 'B1'),
('tenzij', 'unless', 'Ik kom, tenzij het regent.', 'I come, unless it rains.', 'connectors', 'B1'),
('zodat', 'so that', 'Ik werk hard zodat ik kan slagen.', 'I work hard so that I can succeed.', 'connectors', 'B1');

-- =============================================
-- B2 LEVEL - UPPER INTERMEDIATE
-- =============================================

-- B2 Verbs
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('aanpassen', 'to adjust/adapt', 'Je moet je aanpassen aan de situatie.', 'You have to adapt to the situation.', 'verbs', 'B2'),
('overtuigen', 'to convince', 'Hij probeert mij te overtuigen.', 'He tries to convince me.', 'verbs', 'B2'),
('beïnvloeden', 'to influence', 'Media kan meningen beïnvloeden.', 'Media can influence opinions.', 'verbs', 'B2'),
('beweren', 'to claim', 'Hij beweert dat het waar is.', 'He claims it is true.', 'verbs', 'B2'),
('beschouwen', 'to consider', 'Ik beschouw hem als een vriend.', 'I consider him a friend.', 'verbs', 'B2'),
('vaststellen', 'to establish/determine', 'We moeten de oorzaak vaststellen.', 'We must determine the cause.', 'verbs', 'B2'),
('onderscheiden', 'to distinguish', 'Je moet feiten van meningen onderscheiden.', 'You must distinguish facts from opinions.', 'verbs', 'B2'),
('concluderen', 'to conclude', 'Ik concludeer dat het waar is.', 'I conclude that it is true.', 'verbs', 'B2'),
('analyseren', 'to analyze', 'We analyseren de gegevens.', 'We analyze the data.', 'verbs', 'B2'),
('interpreteren', 'to interpret', 'Je kunt het verschillend interpreteren.', 'You can interpret it differently.', 'verbs', 'B2'),
('reflecteren', 'to reflect', 'Laten we reflecteren op onze acties.', 'Let us reflect on our actions.', 'verbs', 'B2'),
('realiseren', 'to realize', 'Ik realiseer me nu pas dat...', 'I only realize now that...', 'verbs', 'B2'),
('streven', 'to strive', 'Wij streven naar excellentie.', 'We strive for excellence.', 'verbs', 'B2'),
('ondernemen', 'to undertake', 'We moeten actie ondernemen.', 'We must take action.', 'verbs', 'B2'),
('handhaven', 'to maintain/enforce', 'We handhaven de regels strikt.', 'We enforce the rules strictly.', 'verbs', 'B2'),
('afwijken', 'to deviate', 'We mogen niet van het plan afwijken.', 'We may not deviate from the plan.', 'verbs', 'B2'),
('overwegen', 'to consider', 'Ik overweeg om te verhuizen.', 'I am considering moving.', 'verbs', 'B2'),
('afwegen', 'to weigh (options)', 'Je moet de voor- en nadelen afwegen.', 'You have to weigh the pros and cons.', 'verbs', 'B2');

-- B2 Abstract Nouns
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('bewustzijn', 'consciousness/awareness', 'Het milieubewustzijn groeit.', 'Environmental awareness is growing.', 'nouns', 'B2'),
('verantwoordelijkheid', 'responsibility', 'Ik neem mijn verantwoordelijkheid.', 'I take my responsibility.', 'nouns', 'B2'),
('vaardigheid', 'skill', 'Communicatievaardigheden zijn belangrijk.', 'Communication skills are important.', 'nouns', 'B2'),
('bekwaamheid', 'competence', 'Hij toont grote bekwaamheid.', 'He shows great competence.', 'nouns', 'B2'),
('overweging', 'consideration', 'Na rijp beraad en overweging.', 'After careful consideration.', 'nouns', 'B2'),
('standpunt', 'point of view', 'Wat is je standpunt hierover?', 'What is your point of view on this?', 'nouns', 'B2'),
('perspectief', 'perspective', 'Vanuit een ander perspectief.', 'From a different perspective.', 'nouns', 'B2'),
('aspect', 'aspect', 'Er zijn verschillende aspecten.', 'There are different aspects.', 'nouns', 'B2'),
('kwestie', 'issue/matter', 'Dit is een gevoelige kwestie.', 'This is a sensitive issue.', 'nouns', 'B2'),
('verschijnsel', 'phenomenon', 'Het is een bekend verschijnsel.', 'It is a well-known phenomenon.', 'nouns', 'B2'),
('tendens', 'tendency/trend', 'Er is een dalende tendens.', 'There is a declining trend.', 'nouns', 'B2'),
('strategie', 'strategy', 'We hebben een nieuwe strategie nodig.', 'We need a new strategy.', 'nouns', 'B2'),
('methodiek', 'methodology', 'De methodiek is wetenschappelijk.', 'The methodology is scientific.', 'nouns', 'B2'),
('draagvlak', 'support/backing', 'Er is breed draagvlak voor dit plan.', 'There is broad support for this plan.', 'nouns', 'B2'),
('draagkracht', 'carrying capacity', 'De draagkracht van de organisatie.', 'The carrying capacity of the organization.', 'nouns', 'B2');

-- B2 Adjectives
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('wezenlijk', 'essential', 'Er is geen wezenlijk verschil.', 'There is no essential difference.', 'adjectives', 'B2'),
('fundamenteel', 'fundamental', 'Dit is een fundamentele verandering.', 'This is a fundamental change.', 'adjectives', 'B2'),
('significant', 'significant', 'De resultaten zijn significant.', 'The results are significant.', 'adjectives', 'B2'),
('aanzienlijk', 'considerable', 'Het verschil is aanzienlijk.', 'The difference is considerable.', 'adjectives', 'B2'),
('geleidelijk', 'gradual', 'De verandering is geleidelijk.', 'The change is gradual.', 'adjectives', 'B2'),
('tijdelijk', 'temporary', 'Dit is een tijdelijke oplossing.', 'This is a temporary solution.', 'adjectives', 'B2'),
('permanent', 'permanent', 'De schade is permanent.', 'The damage is permanent.', 'adjectives', 'B2'),
('doorslaggevend', 'decisive', 'Dit was de doorslaggevende factor.', 'This was the decisive factor.', 'adjectives', 'B2'),
('controversieel', 'controversial', 'Het onderwerp is controversieel.', 'The subject is controversial.', 'adjectives', 'B2'),
('genuanceerd', 'nuanced', 'We moeten genuanceerder kijken.', 'We need to look more nuanced.', 'adjectives', 'B2'),
('omstreden', 'disputed', 'De beslissing is omstreden.', 'The decision is disputed.', 'adjectives', 'B2'),
('onvermijdelijk', 'inevitable', 'Verandering is onvermijdelijk.', 'Change is inevitable.', 'adjectives', 'B2');

-- =============================================
-- C1 LEVEL - ADVANCED
-- =============================================

-- C1 Verbs
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('belichamen', 'to embody', 'Zij belichaamt de Nederlandse waarden.', 'She embodies Dutch values.', 'verbs', 'C1'),
('bevorderen', 'to promote/foster', 'We moeten samenwerking bevorderen.', 'We must promote cooperation.', 'verbs', 'C1'),
('ondermijnen', 'to undermine', 'Dit zou onze positie kunnen ondermijnen.', 'This could undermine our position.', 'verbs', 'C1'),
('consolideren', 'to consolidate', 'We consolideren onze winst.', 'We consolidate our gains.', 'verbs', 'C1'),
('impliceren', 'to imply', 'Wat impliceert deze uitspraak?', 'What does this statement imply?', 'verbs', 'C1'),
('insinueren', 'to insinuate', 'Wat probeer je te insinueren?', 'What are you trying to insinuate?', 'verbs', 'C1'),
('nuanceren', 'to nuance/qualify', 'Ik wil mijn standpunt nuanceren.', 'I want to qualify my position.', 'verbs', 'C1'),
('relativeren', 'to put in perspective', 'Je moet het relativeren.', 'You should put it in perspective.', 'verbs', 'C1'),
('abstraheren', 'to abstract', 'We moeten van details abstraheren.', 'We must abstract from details.', 'verbs', 'C1'),
('verwezenlijken', 'to realize/achieve', 'Hij verwezenlijkte zijn droom.', 'He realized his dream.', 'verbs', 'C1'),
('veronderstellen', 'to presuppose', 'Dat veronderstelt kennis van het onderwerp.', 'That presupposes knowledge of the subject.', 'verbs', 'C1'),
('vooronderstellen', 'to assume', 'Laten we niet te veel vooronderstellen.', 'Let us not assume too much.', 'verbs', 'C1'),
('doorgronden', 'to fathom', 'Ik kan zijn motieven niet doorgronden.', 'I cannot fathom his motives.', 'verbs', 'C1'),
('aanschouwen', 'to behold', 'We aanschouwen een historische gebeurtenis.', 'We behold a historic event.', 'verbs', 'C1'),
('onthullen', 'to reveal/unveil', 'De onderzoeker onthulde nieuwe feiten.', 'The researcher revealed new facts.', 'verbs', 'C1');

-- C1 Nouns
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('nuance', 'nuance', 'De nuances zijn belangrijk.', 'The nuances are important.', 'nouns', 'C1'),
('implicatie', 'implication', 'De implicaties zijn verstrekkend.', 'The implications are far-reaching.', 'nouns', 'C1'),
('paradox', 'paradox', 'Dit is een interessante paradox.', 'This is an interesting paradox.', 'nouns', 'C1'),
('dilemma', 'dilemma', 'We staan voor een dilemma.', 'We face a dilemma.', 'nouns', 'C1'),
('hypothese', 'hypothesis', 'De hypothese moet worden getest.', 'The hypothesis must be tested.', 'nouns', 'C1'),
('premisse', 'premise', 'De premisse is onjuist.', 'The premise is incorrect.', 'nouns', 'C1'),
('paradigma', 'paradigm', 'Er vindt een paradigmaverschuiving plaats.', 'A paradigm shift is taking place.', 'nouns', 'C1'),
('discrepantie', 'discrepancy', 'Er is een discrepantie tussen woord en daad.', 'There is a discrepancy between word and deed.', 'nouns', 'C1'),
('coherentie', 'coherence', 'Er ontbreekt coherentie in het beleid.', 'There is a lack of coherence in the policy.', 'nouns', 'C1'),
('complexiteit', 'complexity', 'De complexiteit neemt toe.', 'The complexity increases.', 'nouns', 'C1'),
('ambivalentie', 'ambivalence', 'Er is ambivalentie over dit onderwerp.', 'There is ambivalence about this topic.', 'nouns', 'C1'),
('contingentie', 'contingency', 'We moeten rekening houden met contingenties.', 'We must account for contingencies.', 'nouns', 'C1'),
('hiërarchie', 'hierarchy', 'De organisatiehiërarchie is duidelijk.', 'The organizational hierarchy is clear.', 'nouns', 'C1'),
('integriteit', 'integrity', 'Integriteit is cruciaal.', 'Integrity is crucial.', 'nouns', 'C1'),
('autonomie', 'autonomy', 'De regio streeft naar meer autonomie.', 'The region strives for more autonomy.', 'nouns', 'C1');

-- C1 Adjectives
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('inherent', 'inherent', 'Dit risico is inherent aan het systeem.', 'This risk is inherent to the system.', 'adjectives', 'C1'),
('impliciet', 'implicit', 'De boodschap was impliciet.', 'The message was implicit.', 'adjectives', 'C1'),
('expliciet', 'explicit', 'De regels zijn expliciet.', 'The rules are explicit.', 'adjectives', 'C1'),
('intrinsiek', 'intrinsic', 'Dit heeft intrinsieke waarde.', 'This has intrinsic value.', 'adjectives', 'C1'),
('autonoom', 'autonomous', 'De regio wil autonoom worden.', 'The region wants to become autonomous.', 'adjectives', 'C1'),
('congruent', 'congruent', 'Dit is niet congruent met onze waarden.', 'This is not congruent with our values.', 'adjectives', 'C1'),
('ambivalent', 'ambivalent', 'Ik sta ambivalent tegenover dit idee.', 'I am ambivalent about this idea.', 'adjectives', 'C1'),
('paradoxaal', 'paradoxical', 'Het is een paradoxale situatie.', 'It is a paradoxical situation.', 'adjectives', 'C1'),
('ongrijpbaar', 'elusive', 'De waarheid blijft ongrijpbaar.', 'The truth remains elusive.', 'adjectives', 'C1'),
('verstrekkend', 'far-reaching', 'De gevolgen zijn verstrekkend.', 'The consequences are far-reaching.', 'adjectives', 'C1'),
('onomstotelijk', 'indisputable', 'Het bewijs is onomstotelijk.', 'The evidence is indisputable.', 'adjectives', 'C1'),
('ondubbelzinnig', 'unambiguous', 'De boodschap moet ondubbelzinnig zijn.', 'The message must be unambiguous.', 'adjectives', 'C1');

-- =============================================
-- C2 LEVEL - PROFICIENT
-- =============================================

-- C2 Verbs
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('doordesemen', 'to permeate', 'Zijn invloed doordesemt de hele organisatie.', 'His influence permeates the entire organization.', 'verbs', 'C2'),
('onderschrijven', 'to endorse/subscribe', 'Ik onderschrijf deze visie volledig.', 'I fully endorse this vision.', 'verbs', 'C2'),
('vergewissen', 'to ascertain', 'Je moet je ervan vergewissen dat dit klopt.', 'You must ascertain that this is correct.', 'verbs', 'C2'),
('duiden', 'to interpret/point to', 'Dit duidt op een fundamenteel probleem.', 'This points to a fundamental problem.', 'verbs', 'C2'),
('verwoorden', 'to articulate', 'Hij kan zijn gedachten goed verwoorden.', 'He can articulate his thoughts well.', 'verbs', 'C2'),
('behelzen', 'to comprise/involve', 'De taak behelst veel verantwoordelijkheid.', 'The task involves much responsibility.', 'verbs', 'C2'),
('verluiden', 'to be rumored', 'Naar verluidt komt er een reorganisatie.', 'Rumor has it there will be a reorganization.', 'verbs', 'C2'),
('ontkennen', 'to deny', 'Hij ontkent de beschuldigingen.', 'He denies the accusations.', 'verbs', 'C2'),
('verzaken', 'to forsake/neglect', 'Hij verzaakt zijn plicht.', 'He forsakes his duty.', 'verbs', 'C2'),
('getuigen', 'to testify/bear witness', 'Dit getuigt van grote moed.', 'This bears witness to great courage.', 'verbs', 'C2'),
('tenietdoen', 'to nullify', 'Dit zou al onze inspanningen tenietdoen.', 'This would nullify all our efforts.', 'verbs', 'C2'),
('ontluisteren', 'to tarnish', 'Het schandaal ontluisterde zijn reputatie.', 'The scandal tarnished his reputation.', 'verbs', 'C2');

-- C2 Idiomatic Expressions
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('met de deur in huis vallen', 'to get straight to the point', 'Ik zal met de deur in huis vallen.', 'I''ll get straight to the point.', 'idioms', 'C2'),
('de koe bij de horens vatten', 'to take the bull by the horns', 'We moeten de koe bij de horens vatten.', 'We must take the bull by the horns.', 'idioms', 'C2'),
('de knuppel in het hoenderhok gooien', 'to stir up trouble', 'Dat gaat de knuppel in het hoenderhok gooien.', 'That will stir up trouble.', 'idioms', 'C2'),
('van de regen in de drup komen', 'out of the frying pan into the fire', 'We kwamen van de regen in de drup.', 'We went from bad to worse.', 'idioms', 'C2'),
('de spijker op de kop slaan', 'to hit the nail on the head', 'Je slaat de spijker op de kop.', 'You hit the nail on the head.', 'idioms', 'C2'),
('iets door de vingers zien', 'to turn a blind eye', 'We kunnen dit niet door de vingers zien.', 'We cannot turn a blind eye to this.', 'idioms', 'C2'),
('het onderste uit de kan halen', 'to get the most out of something', 'Hij wil het onderste uit de kan halen.', 'He wants to get the most out of it.', 'idioms', 'C2'),
('op de kast jagen', 'to drive someone up the wall', 'Hij jaagt me op de kast.', 'He drives me up the wall.', 'idioms', 'C2'),
('een duit in het zakje doen', 'to put in one''s two cents', 'Ik wil ook een duit in het zakje doen.', 'I want to put in my two cents too.', 'idioms', 'C2'),
('de boel de boel laten', 'to let things slide', 'We kunnen niet gewoon de boel de boel laten.', 'We can''t just let things slide.', 'idioms', 'C2');

-- C2 Advanced Nouns
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('ontoereikendheid', 'inadequacy', 'De ontoereikendheid van het systeem is evident.', 'The inadequacy of the system is evident.', 'nouns', 'C2'),
('vergankelijkheid', 'transience', 'De vergankelijkheid van het bestaan.', 'The transience of existence.', 'nouns', 'C2'),
('duurzaamheid', 'sustainability', 'Duurzaamheid staat centraal in ons beleid.', 'Sustainability is central to our policy.', 'nouns', 'C2'),
('weerbarstigheid', 'recalcitrance', 'De weerbarstigheid van het probleem frustreert.', 'The recalcitrance of the problem frustrates.', 'nouns', 'C2'),
('onvervreemdbaar', 'inalienable', 'Onvervreemdbare rechten moeten beschermd worden.', 'Inalienable rights must be protected.', 'nouns', 'C2'),
('vooringenomenheid', 'bias/prejudice', 'Cognitieve vooringenomenheid beïnvloedt beslissingen.', 'Cognitive bias affects decisions.', 'nouns', 'C2'),
('onvoorwaardelijkheid', 'unconditionality', 'De onvoorwaardelijkheid van liefde.', 'The unconditionality of love.', 'nouns', 'C2'),
('wederzijdsheid', 'reciprocity', 'Wederzijdsheid is essentieel in relaties.', 'Reciprocity is essential in relationships.', 'nouns', 'C2'),
('gelaagdheid', 'stratification', 'De gelaagdheid van de samenleving.', 'The stratification of society.', 'nouns', 'C2'),
('veerkracht', 'resilience', 'Veerkracht is cruciaal in moeilijke tijden.', 'Resilience is crucial in difficult times.', 'nouns', 'C2');

-- C2 Advanced Adjectives
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('onloochenbaar', 'undeniable', 'De feiten zijn onloochenbaar.', 'The facts are undeniable.', 'adjectives', 'C2'),
('ongenaakbaar', 'unapproachable', 'Hij lijkt ongenaakbaar.', 'He seems unapproachable.', 'adjectives', 'C2'),
('onverbiddelijk', 'relentless', 'De tijd is onverbiddelijk.', 'Time is relentless.', 'adjectives', 'C2'),
('alomtegenwoordig', 'omnipresent', 'Technologie is alomtegenwoordig.', 'Technology is omnipresent.', 'adjectives', 'C2'),
('ondoorgrondelijk', 'unfathomable', 'Zijn motieven zijn ondoorgrondelijk.', 'His motives are unfathomable.', 'adjectives', 'C2'),
('onwrikbaar', 'unwavering', 'Hij is onwrikbaar in zijn overtuiging.', 'He is unwavering in his conviction.', 'adjectives', 'C2'),
('onomkeerbaar', 'irreversible', 'De schade is onomkeerbaar.', 'The damage is irreversible.', 'adjectives', 'C2'),
('onnavolgbaar', 'inimitable', 'Zijn stijl is onnavolgbaar.', 'His style is inimitable.', 'adjectives', 'C2'),
('allesomvattend', 'all-encompassing', 'Dit is een allesomvattende analyse.', 'This is an all-encompassing analysis.', 'adjectives', 'C2'),
('diepgravend', 'in-depth', 'We hebben een diepgravend onderzoek nodig.', 'We need an in-depth investigation.', 'adjectives', 'C2');

-- Additional Colors expanded (A2)
INSERT INTO vocabulary_words (dutch, english, category, difficulty) VALUES
('grijs', 'gray', 'colors', 'A2'),
('bruin', 'brown', 'colors', 'A2'),
('paars', 'purple', 'colors', 'A2'),
('roze', 'pink', 'colors', 'A2'),
('zilver', 'silver', 'colors', 'A2');

-- Months (A2)
INSERT INTO vocabulary_words (dutch, english, category, difficulty) VALUES
('januari', 'January', 'months', 'A2'),
('februari', 'February', 'months', 'A2'),
('maart', 'March', 'months', 'A2'),
('april', 'April', 'months', 'A2'),
('mei', 'May', 'months', 'A2'),
('juni', 'June', 'months', 'A2'),
('juli', 'July', 'months', 'A2'),
('augustus', 'August', 'months', 'A2'),
('september', 'September', 'months', 'A2'),
('oktober', 'October', 'months', 'A2'),
('november', 'November', 'months', 'A2'),
('december', 'December', 'months', 'A2');

-- Weather (A2)
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('weer', 'weather', 'Hoe is het weer vandaag?', 'How is the weather today?', 'weather', 'A2'),
('zon', 'sun', 'De zon schijnt.', 'The sun is shining.', 'weather', 'A2'),
('regen', 'rain', 'Het regent nu.', 'It is raining now.', 'weather', 'A2'),
('sneeuw', 'snow', 'Er ligt sneeuw.', 'There is snow.', 'weather', 'A2'),
('wind', 'wind', 'De wind waait hard.', 'The wind blows hard.', 'weather', 'A2'),
('wolk', 'cloud', 'Er zijn veel wolken.', 'There are many clouds.', 'weather', 'A2'),
('storm', 'storm', 'Er komt een storm.', 'A storm is coming.', 'weather', 'A2'),
('onweer', 'thunderstorm', 'Er is onweer vanavond.', 'There is a thunderstorm tonight.', 'weather', 'A2');

-- Body Parts (A2)
INSERT INTO vocabulary_words (dutch, english, category, difficulty) VALUES
('hoofd', 'head', 'body', 'A2'),
('haar', 'hair', 'body', 'A2'),
('oog', 'eye', 'body', 'A2'),
('oor', 'ear', 'body', 'A2'),
('neus', 'nose', 'body', 'A2'),
('mond', 'mouth', 'body', 'A2'),
('hand', 'hand', 'body', 'A2'),
('arm', 'arm', 'body', 'A2'),
('been', 'leg', 'body', 'A2'),
('voet', 'foot', 'body', 'A2'),
('vinger', 'finger', 'body', 'A2'),
('hart', 'heart', 'body', 'A2');

-- Food and Drink Extended (A2-B1)
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('appel', 'apple', 'Ik eet een appel.', 'I eat an apple.', 'food', 'A2'),
('sinaasappel', 'orange', 'Sinaasappelsap is gezond.', 'Orange juice is healthy.', 'food', 'A2'),
('banaan', 'banana', 'De aap eet een banaan.', 'The monkey eats a banana.', 'food', 'A2'),
('aardappel', 'potato', 'Nederlanders eten veel aardappelen.', 'Dutch people eat many potatoes.', 'food', 'A2'),
('groente', 'vegetable', 'Eet je genoeg groente?', 'Do you eat enough vegetables?', 'food', 'A2'),
('fruit', 'fruit', 'Fruit is gezond.', 'Fruit is healthy.', 'food', 'A2'),
('vlees', 'meat', 'Ik eet geen vlees.', 'I don''t eat meat.', 'food', 'A2'),
('vis', 'fish', 'Vis is gezond.', 'Fish is healthy.', 'food', 'A2'),
('kip', 'chicken', 'Ik maak kip met rijst.', 'I make chicken with rice.', 'food', 'A2'),
('rijst', 'rice', 'Rijst is lekker.', 'Rice is delicious.', 'food', 'A2'),
('pasta', 'pasta', 'Pasta is mijn favoriet.', 'Pasta is my favorite.', 'food', 'A2'),
('soep', 'soup', 'De soep is warm.', 'The soup is hot.', 'food', 'A2'),
('sap', 'juice', 'Wil je een sap?', 'Do you want a juice?', 'food', 'A2'),
('bier', 'beer', 'Een biertje, alstublieft.', 'A beer, please.', 'food', 'A2'),
('wijn', 'wine', 'Rode of witte wijn?', 'Red or white wine?', 'food', 'A2');

-- More B1 Phrases
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('ik spreek een beetje Nederlands', 'I speak a little Dutch', 'Ik spreek een beetje Nederlands.', 'I speak a little Dutch.', 'phrases', 'B1'),
('hoe zeg je dit in het Nederlands', 'how do you say this in Dutch', 'Hoe zeg je "hello" in het Nederlands?', 'How do you say "hello" in Dutch?', 'phrases', 'B1'),
('het spijt me', 'I am sorry', 'Het spijt me, maar ik kan niet komen.', 'I am sorry, but I cannot come.', 'phrases', 'B1'),
('geen probleem', 'no problem', 'Geen probleem, ik help je graag.', 'No problem, I gladly help you.', 'phrases', 'B1'),
('tot je dienst', 'at your service', 'Altijd tot je dienst.', 'Always at your service.', 'phrases', 'B1'),
('veel succes', 'good luck', 'Veel succes met je examen!', 'Good luck with your exam!', 'phrases', 'B1'),
('gefeliciteerd', 'congratulations', 'Gefeliciteerd met je verjaardag!', 'Congratulations on your birthday!', 'phrases', 'B1'),
('prettige vakantie', 'have a nice vacation', 'Prettige vakantie!', 'Have a nice vacation!', 'phrases', 'B1');

-- Business/Work Vocabulary (B2)
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('vergadering', 'meeting', 'We hebben een vergadering om drie uur.', 'We have a meeting at three.', 'business', 'B2'),
('project', 'project', 'Het project loopt goed.', 'The project is going well.', 'business', 'B2'),
('deadline', 'deadline', 'De deadline is morgen.', 'The deadline is tomorrow.', 'business', 'B2'),
('collega', 'colleague', 'Mijn collega''s zijn aardig.', 'My colleagues are nice.', 'business', 'B2'),
('manager', 'manager', 'De manager neemt de beslissing.', 'The manager makes the decision.', 'business', 'B2'),
('contract', 'contract', 'Heb je het contract ondertekend?', 'Have you signed the contract?', 'business', 'B2'),
('salaris', 'salary', 'Mijn salaris is goed.', 'My salary is good.', 'business', 'B2'),
('promotie', 'promotion', 'Ik heb promotie gekregen!', 'I got a promotion!', 'business', 'B2'),
('ontslag', 'dismissal', 'Hij kreeg ontslag.', 'He was dismissed.', 'business', 'B2'),
('sollicitatie', 'job application', 'Mijn sollicitatie is geslaagd.', 'My job application succeeded.', 'business', 'B2'),
('onderhandeling', 'negotiation', 'De onderhandelingen zijn gaande.', 'Negotiations are underway.', 'business', 'B2'),
('presentatie', 'presentation', 'Ik geef morgen een presentatie.', 'I give a presentation tomorrow.', 'business', 'B2');

-- Academic Vocabulary (C1)
INSERT INTO vocabulary_words (dutch, english, example, example_translation, category, difficulty) VALUES
('onderzoek', 'research', 'Het onderzoek is gepubliceerd.', 'The research has been published.', 'academic', 'C1'),
('thesis', 'thesis', 'Mijn thesis gaat over klimaatverandering.', 'My thesis is about climate change.', 'academic', 'C1'),
('proefschrift', 'dissertation', 'Hij verdedigt zijn proefschrift.', 'He defends his dissertation.', 'academic', 'C1'),
('methode', 'method', 'De wetenschappelijke methode.', 'The scientific method.', 'academic', 'C1'),
('resultaat', 'result', 'De resultaten zijn significant.', 'The results are significant.', 'academic', 'C1'),
('conclusie', 'conclusion', 'De conclusie is duidelijk.', 'The conclusion is clear.', 'academic', 'C1'),
('bibliografie', 'bibliography', 'Voeg een bibliografie toe.', 'Add a bibliography.', 'academic', 'C1'),
('citaat', 'citation/quote', 'Dit is een citaat uit het artikel.', 'This is a quote from the article.', 'academic', 'C1'),
('synopsis', 'synopsis', 'Geef een korte synopsis.', 'Give a short synopsis.', 'academic', 'C1'),
('peer review', 'peer review', 'Het artikel ondergaat peer review.', 'The article undergoes peer review.', 'academic', 'C1');

