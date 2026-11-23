local b1 = true local contador if (true) then contador = 0 end


if (true) then
	b1 = false
end

if (false) then
	b1 = true
else
	b1 = false
end

-- Condicionales anidados (Uso de elseif para evitar anidamiento profundo)
if (false) then
	b1 = true
elseif (true) then
	b1 = true
else
	b1 = true
end

-- CICLOS
local i = 1;
while (i < 1) do
	i = i + 1
end

-- PARA: for variable = inicio, limite, paso do
-- En Lua, en lugar de una condición booleana, se establece un número límite.

-- Equivalente a HASTA (limite) INCREMENTA (1)
for x = 15, 20, 1 do
	i = i + 1
end

-- Equivalente a HASTA (limite) INCREMENTA (2)
for x = 15, 20, 2 do
	i = i - 1
end

-- Equivalente a HASTA (limite) DECREMENTA (1)
for x = 15, 10, -1 do
	i = i + 1
end

repeat
	contador = contador + 1
until (contador >= 3)
