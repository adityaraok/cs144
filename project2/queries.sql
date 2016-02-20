-- queries.sql --

-- Find the number of users in the database --
SELECT COUNT(*)
FROM User;

-- Find the number of sellers from "New York" --
SELECT COUNT(*)
FROM User
WHERE Binary Location = 'New York';

-- Find the number of auctions belonging to exactly four categories --
SELECT COUNT(*)
FROM (SELECT ItemID
      FROM Item_Category
      GROUP BY ItemID
      HAVING COUNT(CategoryID) = 4) T;

-- Find the ID(s) of current (unsold) auctions with the highest bid -- 
SELECT ItemID
FROM Item
WHERE Currently = (SELECT MAX(Currently)
                   FROM Item
                   WHERE Ends > "2001-12-20 00:00:01"
                   AND Started < "2001-12-20 00:00:00"
                   AND Number_of_Bids > 0)
AND Ends > "2001-12-20 00:00:01"
AND Started < "2001-12-20 00:00:00"
AND Number_of_Bids > 0;


-- Find the number of sellers whose rating is higher than 1000 -- 
SELECT COUNT(*)
FROM User
WHERE User.Rating > 1000
AND UserID IN (SELECT Seller FROM Item);

-- Find the number of users who are both sellers and bidders --
SELECT COUNT(*)
FROM User
WHERE UserID IN (SELECT Seller FROM Item)
AND UserID IN (SELECT UserID FROM Bid);

-- Find the number of categories that include at least one item with a bid of more than $100 --
SELECT COUNT(DISTINCT CategoryID)
FROM Item_Category
WHERE ItemID IN (SELECT ItemID
                 FROM Bid 
                 WHERE Bid.Amount > 100.00);

