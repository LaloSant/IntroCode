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
local mixed= x + 6;


