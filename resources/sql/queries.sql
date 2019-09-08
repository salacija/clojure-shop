-- noinspection SqlNoDataSourceInspectionForFile

-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(firstName, lastName, email, address, username, password)
VALUES (:firstName, :lastName, :email, :address, :username, :password)

-- :name get-user-by-username :? :1
SELECT * FROM users
WHERE username = :username

-- :name get-user-by-email :? :1
SELECT * FROM users
WHERE email = :email

-- :name login-user :? :1
SELECT * FROM users
WHERE username = :username AND password = :password

-- :name update-user! :! :n
-- :doc updates an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

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
insert into products (name, price, description, image, categoryId)
values (:name, :price, :description, :image, :categoryId)

-- :name update-product! :! :n
update products
set name = :name, price = :price, description = :description, image = :image, categoryId = :categoryId
where id = :id

-- :name delete-product! :! :n
delete from products
where id = :id

-- :name get-products :? :*
SELECT p.*, c.name as categoryName from products p INNER JOIN categories c ON p.categoryId = c.Id

-- :name get-product :? :1
SELECT p.*, c.name as categoryName from products p INNER JOIN categories c ON p.categoryId = c.Id
WHERE p.id = :id


