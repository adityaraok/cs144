-- load.sql --

LOAD DATA LOCAL INFILE './User.dat' 
    INTO TABLE `User`
    FIELDS TERMINATED BY ' |*| '
    LINES TERMINATED BY "\n"
    (UserID, Rating, @location, @country) SET Location = nullif(@location, ''),
                                            Country = nullif(@country, '');

LOAD DATA LOCAL INFILE './Category.dat' 
    INTO TABLE `Category`
    FIELDS TERMINATED BY ' |*| '
    LINES TERMINATED BY "\n";
        
LOAD DATA LOCAL INFILE './Item.dat' 
    INTO TABLE `Item`
    FIELDS TERMINATED BY ' |*| '
    LINES TERMINATED BY "\n"
    (ItemID, Name, Description, @started, @ends, Currently, 
     First_Bid, @buy_price, Number_of_Bids, Seller) SET Started = STR_TO_DATE(@started, "%Y-%m-%d %H:%i:%s"), 
                                                        Ends = STR_TO_DATE(@ends, "%Y-%m-%d %H:%i:%s"),
                                                        Buy_Price = nullif(@buy_price, '');
                                            
LOAD DATA LOCAL INFILE './Item_Category.dat' 
    INTO TABLE `Item_Category`
    FIELDS TERMINATED BY ' |*| '
    LINES TERMINATED BY "\n";
        
LOAD DATA LOCAL INFILE './Bid.dat' 
    INTO TABLE `Bid`
    FIELDS TERMINATED BY ' |*| '
    LINES TERMINATED BY "\n"
    (UserID, @time, ItemID, Amount) SET Time = STR_TO_DATE(@time, "%Y-%m-%d %H:%i:%s");