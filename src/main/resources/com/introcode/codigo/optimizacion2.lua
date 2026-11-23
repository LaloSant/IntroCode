--[[ ELIMINACION DE CODIGO MUERTO
]]

-- Variables utilizadas por el programa
   	local suma = 4 + 1;
			local bandera = false;

-- ELIMINA CARACTERES EN BLANCO
	local 		 temporal = 100;
	temporal     = 		200;
local 					tambienInutil = 		
50;


-- Se elimina este bloque porque la condición siempre es falsa
if false then
    local imposible = false;
end;

-- También se elimina porque bandera vale false
if bandera then
   local banderaT = true;
else
   local banderaR = false;
end

-- Esta condición conocida como verdadera deja solo el cuerpo
local siempre = true;
local x = 0;
if siempre then
    x = 1;
else
    x = 10;
end

-- La variable se conserva porque se utiliza
local resultado = suma * 2;


-- Varias instrucciones en una sola línea también se separan al optimizar
local comprimida = 4 + 1; if (comprimida >= 5) then local comprimidaR = true end;



--[[
	DOBLAMIENTO DE CONSTANTES
]]

local desdoblada = (5 + 3 * 2 ^ 2) % 2 / 5;
temporal = 5 + 3 * 2 ^ 2;

-- EJERCICIO NOTACION INFIJA

local a = 1 local b = 1 local c = 1 local d = 1 local e = 1 local f = 1 local g = 1 local h = 1
local v1 = a + b

a = 1  b = 1  c = 1  
local v2 = a + b * c

a = 1  b = 1  c = 1  
local v3 = (a + b) * c

a = 1  b = 1  c = 1  d = 1
local v4 = a * b + c / d

a = 1  b = 1  c = 1  d = 1
local v5 = (a + b) * (c - d)

a = 1  b = 1  c = 1  d = 1
local v6 = a + (b - c) * d

a = 1  b = 1  c = 1  d = 1
local v7 = (a - b) / c + d

a = 1  b = 1  c = 1  d = 1  e = 1
local v8 = a * (b + c) - d / e

a = 1  b = 1  c = 2  d = 1  e = 1
local v9 = (a + b) / (c - d) + e

a = 1  b = 1  c = 2  d = 1  e = 1
local v10 = a + b * (c - d) / e

a = 1  b = 1  c = 1  d = 1
local v11 = ((a + b) * c) - d

a = 1  b = 1  c = 1  d = 1  e = 2  f = 1
local v12 = (a - b) * (c + d) / (e - f)

a = 1  b = 1  c = 1  d = 1  e = 1  f = 1  g = 1  h = 1
local v13 = a + b * (c ^ d - e) / (f + g) - h

a = 1  b = 1  c = 1  d = 1  e = 1
local v14 = ((a + b) / c) ^ (d - e)

a = 1  b = 1  c = 1  d = 1  e = 1  f = 1
local v15 = ((a - b) * (d + d)) ^(e / f)


