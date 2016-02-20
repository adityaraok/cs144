-- buildSQLIndex.sql --

USE CS144;

CREATE INDEX SellerIndex ON Item(Seller);
CREATE INDEX BuyPriceIndex ON Item(Buy_Price);
CREATE INDEX EndTimeIndex ON Item(Ends);
CREATE INDEX BidderIndex ON Bid(UserID);