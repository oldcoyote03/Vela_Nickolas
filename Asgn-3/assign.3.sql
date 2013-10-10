#1.a
SELECT model, speed, hd FROM pc WHERE price > 1000
#1.b
SELECT model, speed AS gigahertz, hd AS gigabytes FROM pc WHERE price > 1000
#1.c
SELECT DISTINCT(maker) FROM product WHERE ctype = 'printer'
#1.d
SELECT model, ram, screen FROM laptop WHERE price > 1000
#1.e
SELECT * FROM printer WHERE color
#1.f
SELECT model, hd FROM pc WHERE speed = 3.2 AND price < 2000
#2.a
SELECT maker, speed FROM laptop NATURAL JOIN product WHERE hd >= 30
#2.b
SELECT model, price FROM pc NATURAL JOIN product WHERE maker = 'B'
UNION
SELECT model, price FROM laptop NATURAL JOIN product WHERE maker = 'B'
UNION
SELECT model, price FROM printer NATURAL JOIN product WHERE maker = 'B'
#2.c
SELECT DISTINCT(maker) FROM product WHERE ctype = 'laptop' AND maker NOT IN
(SELECT DISTINCT(maker) FROM product WHERE ctype = 'pc')
#2.d
SELECT DISTINCT(a.hd) FROM pc a JOIN pc b 
WHERE a.hd = b.hd AND a.model > b.model
#2.e
SELECT a.model as First, b.model as Second FROM pc a JOIN pc b
WHERE a.speed = b.speed AND a.ram = b.ram AND a.model > b.model
#2.f
SELECT a.maker
FROM ( SELECT maker, speed FROM pc NATURAL JOIN product
WHERE speed >= 2.0
UNION SELECT maker, speed FROM laptop NATURAL JOIN product
WHERE speed >= 2.0 ) a
GROUP BY a.maker
HAVING count(a.maker) > 1
#3.a
SELECT maker FROM product WHERE 3.0 <= ALL (
SELECT speed FROM pc)
#3.b
SELECT model FROM printer exp WHERE price > ALL (
SELECT price FROM printer WHERE NOT model = exp.model)
#3.c
SELECT model FROM laptop WHERE speed < ALL (
SELECT speed FROM pc)
#3.d
SELECT model FROM (SELECT model, price FROM pc
UNION SELECT model, price FROM laptop UNION
SELECT model, price FROM printer) a
WHERE a.price >= ALL (SELECT price FROM pc
UNION SELECT price FROM laptop UNION SELECT price FROM printer)
#3.e
SELECT maker FROM product a JOIN printer b ON a.model = b.model
WHERE b.price <= ALL (SELECT price FROM printer)
#3.f
SELECT a.maker FROM (SELECT maker, speed
FROM pc NATURAL JOIN product WHERE ram <= ALL (SELECT ram FROM pc) ) a
WHERE a.speed >= ALL (SELECT speed FROM pc)
#4.a
SELECT AVG(speed) FROM pc
#4.b
SELECT AVG(speed) FROM laptop WHERE price > 1000
#4.c
SELECT AVG(price) FROM pc NATURAL JOIN product WHERE maker = 'A'
#4.d
SELECT AVG(a.price) FROM (
SELECT price FROM pc NATURAL JOIN product WHERE maker = 'B'
UNION
SELECT price FROM laptop NATURAL JOIN product WHERE maker = 'B' ) a
#4.e
SELECT speed, AVG(price) FROM pc GROUP BY speed
#4.f
SELECT b.maker, AVG(a.screen) FROM laptop a NATURAL JOIN product b
GROUP BY b.maker
#4.g
SELECT maker FROM product NATURAL JOIN pc
GROUP BY maker HAVING count(*) >= 3
#4.h
SELECT maker, MAX(price) FROM product NATURAL JOIN pc GROUP BY maker
#4.i
SELECT speed, AVG(price) FROM pc GROUP BY speed HAVING speed > 2.0
#4.j
SELECT maker, AVG(hd) FROM pc NATURAL JOIN product
WHERE maker IN (SELECT maker FROM product WHERE ctype = 'printer')
GROUP BY maker
#5.a.1
INSERT INTO product VALUES ('C',1100,'pc')
#5.a.2
INSERT INTO pc VALUES (1100,3.2,1024,180,2400)
#5.b
INSERT INTO laptop SELECT model+100, speed, ram, hd, 17, price+500 FROM pc
#5.c
DELETE FROM pc WHERE hd < 100
#5.d
DELETE FROM laptop WHERE model IN (
SELECT model FROM product WHERE maker NOT IN (
SELECT DISTINCT(maker) FROM product WHERE ctype = 'printer') )
#5.e
UPDATE product SET maker = 'A' WHERE maker = 'B'
#5.f
UPDATE pc SET ram = 2*ram, hd = hd+60
#5.g
UPDATE laptop SET screen = screen+1, price = price-100
WHERE model IN (SELECT model FROM product WHERE maker = 'B')