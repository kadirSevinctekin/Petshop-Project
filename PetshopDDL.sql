--Users tablosunu olusturma
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    user_password VARCHAR(255) NOT NULL,
    user_name VARCHAR(50),
    user_surname VARCHAR(50),
	age INT,
	address VARCHAR(255)
);

--Pets tablosunu olusturma
CREATE TABLE pets (
    pet_id SERIAL PRIMARY KEY,
	owner_id INT REFERENCES users(user_id) NOT NULL,
    pet_name VARCHAR(50) NOT NULL,
    pet_type VARCHAR(50) NOT NULL,
    age INT
);

--Advertisements tablosunu olusturma
CREATE TABLE advertisements (
    advertisement_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) NOT NULL,
    pet_id INT REFERENCES pets(pet_id) NOT NULL,
    price DECIMAL(10, 2)
);

--Applications tablosunu olusturma
CREATE TABLE applications (
    application_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) NOT NULL,
    advertisement_id INT REFERENCES advertisements(advertisement_id) NOT NULL,
    price DECIMAL(10, 2)
);



--TRIGGER AGE > 18
CREATE OR REPLACE FUNCTION check_age()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.age < 18 THEN
        RAISE EXCEPTION 'Kullanıcı yaşının 18 veya daha büyük olması gerekmektedir.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER age_check_trigger
BEFORE INSERT ON users
FOR EACH ROW
EXECUTE FUNCTION check_age();

--TRIGGER PRICE < 10000
CREATE OR REPLACE FUNCTION check_price()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.price > 10000 THEN
        RAISE EXCEPTION 'Fiyat 10000den fazla olamaz';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER price_check_trigger
BEFORE INSERT ON adversitements
FOR EACH ROW
EXECUTE FUNCTION check_price();



--Function
CREATE OR REPLACE FUNCTION getUserIdByUsername(p_username VARCHAR)
RETURNS INT AS $$
DECLARE
    user_id INT;
BEGIN
    SELECT user_id INTO user_id FROM users WHERE username = p_username;
    RETURN user_id;
END;
$$ LANGUAGE PLPGSQL;



--INSERT users
INSERT INTO users VALUES (DEFAULT, 'imparator', 'ft123', 'Fatih', 'Terim', 70, 'bodrum');
INSERT INTO users VALUES (DEFAULT, 'valiaht', 'okan123', 'Okan', 'Buruk', 50, 'istanbul');
INSERT INTO users VALUES (DEFAULT, 'kadirs', 'kadir123', 'Kadir', 'Sevinctekin', 22, 'osmanlı');
INSERT INTO users VALUES (DEFAULT, 'bakhish', 'bak123', 'Bakhish', 'Fataliyev', 20, 'bakü');
INSERT INTO users VALUES (DEFAULT, 'enez', 'enes123', 'Enes', 'Eryasan', 23, 'edirne');
INSERT INTO users VALUES (DEFAULT, 'furkanb', 'furkan123', 'Furkan', 'Bayraklı', 22, 'trabzon');
INSERT INTO users VALUES (DEFAULT, 'yusufg', 'yusuf123', 'Yusuf', 'Güney', 22, 'erzincan');
INSERT INTO users VALUES (DEFAULT, 'ergünizm', 'ergun123', 'Ergün', 'İsmailoglu', 22, 'denizli');
INSERT INTO users VALUES (DEFAULT, 'ahmete', 'ahmetenes123', 'Ahmet Eren', 'Ataş', 22, 'Mersin');
INSERT INTO users VALUES (DEFAULT, 'aby', 'aby123', 'Ahmet', 'Büyükyılmaz', 22, 'konya');

--INSERT pets
INSERT INTO pets VALUES (DEFAULT, 1, 'aslan', 'dog', 15);
INSERT INTO pets VALUES (DEFAULT, 1, 'tekir', 'cat', 2);
INSERT INTO pets VALUES (DEFAULT, 2, 'crespo', 'dog', 4);
INSERT INTO pets VALUES (DEFAULT, 8, 'hoi', 'dog', 13);
INSERT INTO pets VALUES (DEFAULT, 10, 'mavi', 'cat', 3);
INSERT INTO pets VALUES (DEFAULT, 4, 'alex', 'kanarya', 2);