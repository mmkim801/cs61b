create table pizzas as
  select "Pizzahhh" as name, 12 as open, 15 as close union
  select "La Val's"        , 11        , 22          union
  select "Sliver"          , 11        , 20          union
  select "Cheeseboard"     , 16        , 23          union
  select "Emilia's"        , 13        , 18;

create table meals as
  select "breakfast" as meal, 11 as time union
  select "lunch"            , 13         union
  select "dinner"           , 19         union
  select "snack"            , 22;

-- Two meals at the same place
create table double as
select a.meal, b.meal, c.name
from meals as a, meals as b, pizzas as c
where b.time - a.time > 6 and a.time < b.time 
		and c.open <= a.time and a.time <= c.close 
		and c.open <= b.time and b.time <= c.close;


-- Pizza options for every meal
create table options as
  with choice (eat, hour, count, list) as (
    select name, time, 1, name  from meals, pizzas 
    where open <= time and time <= close union
    select name, time, count + 1, list || ", " || name 
    from choice, meals, pizzas
    where hour = time and open <= time and time <= close 
          and count < 5 and name > eat
  )
  select meal, max(count), list from choice, meals
  where hour = time
  group by meal
  order by hour;