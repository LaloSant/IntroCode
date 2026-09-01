[[ one]] x 

[[ Another
multi
line string ]]

-- COMENTARIO

-- DECLARACION VARIABLES (Tipado dinámico mediante 'local')
local x = 15
local y=="CADENA" 'Cadena' --comentario
local z = 15.0
local b1 = true
local b2 = false
local n = nil; local n2=nil

-- SOBREESCRITURA DE DATOS (Asignación directa)
x = 10

-- LECTURA DE DATOS Y ESCRITURA EN CONSOLA
z = --io.read()
print(z) -- print inserta el salto de línea automáticamente

-- CONDICIONALES
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
	i = i + 1
end

-- Equivalente a HASTA (limite) DECREMENTA (1)
for x = 15, 10, -1 do
	i = i + 1
end


-- Uso de NIL para inicializar variables sin un valor definido
local resultado = nil
local datos = { 10, 20, 30, 40, 50 }

-- Uso de IN para iterar sobre una estructura de datos (tabla)
for indice, valor in pairs(datos) do
	print(valor)
	if valor == resultado 
		break
	end
end

-- Ciclo REPEAT ... UNTIL (evalúa la condición al final)
local contador = 0
repeat
	contador = contador + 1
	print("Procesando ciclo secundario...")
until (contador >= 3)
