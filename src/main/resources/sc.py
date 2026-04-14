from pathlib import Path
import re

sql_file = Path("/home/a13/front_back/PHarmatel_cursor/backend/src/main/resources/V3__medicines_from_syria.sql")

text = sql_file.read_text(encoding="utf-8")

pattern = re.compile(
    r"INSERT INTO medicine \(id, (?P<cols>.*?)\) VALUES \((?P<values>.*?)\);",
    re.DOTALL,
)

def rewrite_insert(match: re.Match) -> str:
    cols = match.group("cols")
    values = match.group("values")

    first_comma = values.find(",")
    if first_comma == -1:
        return match.group(0)

    new_values = values[first_comma + 1 :].lstrip()
    return f"INSERT INTO medicine ({cols}) VALUES ({new_values});"

updated = pattern.sub(rewrite_insert, text)
sql_file.write_text(updated, encoding="utf-8")
print("Updated SQL file")