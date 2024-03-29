-- noinspection SqlNoDataSourceInspectionForFile

-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(firstName, lastName, email, address, username, password)
VALUES (:firstName, :lastName, :email, :address, :username, :password)

-- :name update-user! :! :n
UPDATE users
SET firstName = :firstName, lastName = :lastName, email = :email, address = :address
WHERE id = :id

-- :name get-user-by-username :? :1
SELECT * FROM users
WHERE username = :username

-- :name get-user-by-email :? :1
SELECT * FROM users
WHERE email = :email

-- :name login-user :? :1
SELECT * FROM users
WHERE username = :username AND password = :password

-- :name get-user :? :1
-- :doc retrieves a user record given the id
SELECT * FROM users
WHERE id = :id

-- :name get-category :? :1
-- :doc retrieves a category record given the id
SELECT * FROM categories
WHERE id = :id

-- :name delete-user! :! :n
-- :doc deletes a user record given the id
DELETE FROM users
WHERE id = :id

-- :name get-categories :? :*
-- :doc retrieve all categories.
SELECT * FROM categories
WHERE id IN (SELECT DISTINCT categoryId FROM Products)

-- :name get-all-categories :? :*
SELECT * FROM categories

-- :name create-category! :! :n
INSERT INTO categories (name) values (:name)

-- :name update-category! :! :n
UPDATE categories
SET name = :name
WHERE id = :id

-- :name delete-category! :! :n
DELETE FROM categories
WHERE id = :id

-- :name create-product! :! :n
insert into products (name, price, description, image, categoryId, isNew, isBestSeller)
values (:name, :price, :description, :image, :categoryId, :isNew, :isBestSeller)

-- :name update-product! :! :n
update products
set name = :name, price = :price, description = :description, image = :image, categoryId = :categoryId, isBestSeller = :isBestSeller, isNew = :isNew
where id = :id

-- :name delete-product! :! :n
delete from products
where id = :id

-- :name get-products-by-category-id :? :*
SELECT p.*, c.name as categoryName from products p INNER JOIN categories c ON p.categoryId = c.Id
where c.Id = :categoryId

-- :name get-all-products :? :*
SELECT p.*, c.name as categoryName from products p INNER JOIN categories c ON p.categoryId = c.Id

-- :name get-featured-products :? :*
SELECT p.*, c.name as categoryName from products p INNER JOIN categories c ON p.categoryId = c.Id
WHERE isBestSeller = true limit 3

-- :name get-products :? :*
SELECT p.*, c.name as categoryName from products p INNER JOIN categories c ON p.categoryId = c.Id

-- :name search-products :? :*
SELECT p.*, c.name as categoryName from products p INNER JOIN categories c ON p.categoryId = c.Id
where p.name LIKE concat('%' :keyword '%')

-- :name get-product :? :1
SELECT p.*, c.name as categoryName from products p INNER JOIN categories c ON p.categoryId = c.Id
WHERE p.id = :productId

-- :name create-order :insert :raw
insert into orders (userId, address, firstName, lastName, email) values (:userId, :address, :firstName, :lastName, :email)

-- :name create-order-line :! :n
insert into orderlines (orderId, productName, quantity, price) values (:orderId, :productName, :quantity, :price)

-- :name get-last-user-order-info :? :1
select id, email
from orders where userId = :userId
order by id desc
limit 1

-- :name email-already-in-use :? :1
select COUNT(*) as isTaken from
users where email = :email AND id <> :userId

-- :name username-already-in-use :? :1
select COUNT(*) as isTaken from
users where username = :username AND id <> :userId

-- :name get-all-orders
select o.id, dateCreated, concat(firstName, ' ', lastName) as customer, sum(ol.price * ol.quantity) as totalPrice
from orders o
inner join orderlines ol ON o.id = ol.orderId
group by o.id, dateCreated, firstName, lastName

-- :name get-user-orders
select o.id, dateCreated, concat(firstName, ' ', lastName) as customer, sum(ol.price * ol.quantity) as totalPrice
from orders o
inner join orderlines ol ON o.id = ol.orderId
where o.userId = :userId
group by o.id, dateCreated, firstName, lastName