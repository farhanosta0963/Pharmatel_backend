insert into pharmacy_medicines (pharmacy_id, medicine_id, quantity, available, price)
select 1, gs, floor(random() * 100 + 1)::int, true, random() * 100 
from generate_series(1, 3000) gs;       

insert into pharmacy_medicines (pharmacy_id, medicine_id, quantity, available, price)
select 2, gs, floor(random() * 100 + 1)::int, true, random() * 100 
from generate_series(3000, 6000) gs;       