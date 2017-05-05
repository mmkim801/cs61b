create table parents as
  select "abraham" as parent, "barack" as child union
  select "abraham"          , "clinton"         union
  select "delano"           , "herbert"         union
  select "fillmore"         , "abraham"         union
  select "fillmore"         , "delano"          union
  select "fillmore"         , "grover"          union
  select "eisenhower"       , "fillmore";

create table dogs as
  select "abraham" as name, "long" as fur, 26 as height union
  select "barack"         , "short"      , 52           union
  select "clinton"        , "long"       , 47           union
  select "delano"         , "long"       , 46           union
  select "eisenhower"     , "short"      , 35           union
  select "fillmore"       , "curly"      , 32           union
  select "grover"         , "short"      , 28           union
  select "herbert"        , "curly"      , 31;

create table sizes as
  select "toy" as size, 24 as min, 28 as max union
  select "mini",        28,        35        union
  select "medium",      35,        45        union
  select "standard",    45,        60;

-------------------------------------------------------------
-- PLEASE DO NOT CHANGE ANY SQL STATEMENTS ABOVE THIS LINE --
-------------------------------------------------------------

-- The size of each dog
create table size_of_dogs as
select a.name, b.size 
from dogs as a, sizes as b 
where a.height > b.min and a.height <= b.max;


-- All dogs with parents ordered by decreasing height of their parent
create table by_height as
select b.child
from dogs as a, parents as b
where a.name = b.parent
order by -a.height;


-- Sentences about siblings that are the same size
create table sentences as
  with siblings (s1, s2) as (
    select a.child, b.child
    from parents as a, parents as b
    where a.parent = b.parent and a.child < b.child
    )
select c.s1 || ' and ' || c.s2 || ' are ' || d.size ||' siblings' 
from siblings as c, size_of_dogs as d, size_of_dogs as e
where d.size = e.size and c.s1 = d.name and c.s2 = e.name; 


-- Ways to stack 4 dogs to a height of at least 170, ordered by total height
create table stacks as
  with stackz (total, num_dawg, dawgs, last_dawg) as (
    select height, 1, name, height from dogs union
    select total + height, num_dawg + 1, dawgs || ", " || name, height
    from stackz, dogs
    where num_dawg < 4 and last_dawg < height
    )
select dawgs, total from stackz 
where total >= 170
order by total;


create table tallest as
select max(height), name
from dogs
group by height/10
having count (*) > 1;


-- All non-parent relations ordered by height difference
create table non_parents as
select "REPLACE THIS LINE WITH YOUR SOLUTION";


