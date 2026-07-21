from pathlib import Path
rows = 60
cols = 60
lines = []
for r in range(1, rows + 1):
    vals = []
    for c in range(1, cols + 1):
        if r == 1 or r == rows or c == 1 or c == cols:
            if r == 2 and c == 2:
                vals.append('15')
            elif r == rows and c == 2:
                vals.append('11')
            else:
                vals.append('10')
        else:
            vals.append('0')
    lines.append('  '.join(vals))
Path('res/maps/house1.txt').write_text('\n'.join(lines) + '\n', encoding='utf-8')
print(f'wrote {rows} rows to res/maps/house1.txt')
