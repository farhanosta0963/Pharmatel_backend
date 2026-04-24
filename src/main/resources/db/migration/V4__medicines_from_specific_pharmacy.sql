insert into pharmacy_medicines (pharmacy_id, medicine_id, quantity)
select 1, gs, floor(random() * 100 + 1)::int
from generate_series(1, 6000) gs;       