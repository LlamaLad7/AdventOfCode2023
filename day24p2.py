import z3

with open("src/main/resources/inputs/day24.txt") as f:
    lines = f.readlines()

solver = z3.Solver()
x, y, z, u, v, w = z3.Ints("x y z u v w")
t = [z3.Int(f"t{i}") for i in range(len(lines))]
for i, line in enumerate(lines):
    l, r = [[*map(int, it.split(", "))] for it in line.split(" @ ")]
    xi, yi, zi = l
    ui, vi, wi = r
    solver.add(x + t[i] * u == xi + t[i] * ui)
    solver.add(y + t[i] * v == yi + t[i] * vi)
    solver.add(z + t[i] * w == zi + t[i] * wi)
print(solver.check())
model = solver.model()
print(sum(model[it].as_long() for it in [x, y, z]))
