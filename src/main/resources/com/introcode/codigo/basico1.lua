-- 1- Variable duplicada
local dup = 1
local dup = 2


-- 2- Variable no declarada
no_declarada = 3

-- 3- Division entre 0
local cero = 5 / 0

-- 4- Operaciones aritmeticas no validas
local a1 = 1 + true
local a2 = 1 + "Cadena"
local a3 = 1 + nil

-- 5- Operaciones relacionales no validas
local r1 = 5 > nil
local r2 = 5 > "Cadena"
local r3 = "cadena" < true
local r4 = true <= false


local r5 = not 10


repeat 
	local x = 1
until 5 > "Cadena"


if (5 >true) then
	local abc = 9
end
