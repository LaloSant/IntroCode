-- ==========================================
-- PRUEBA 
-- ==========================================

-- Declaraciones iniciales masivas
local v1 = 10 ;
local v2 = 20.5
local texto_1 = "cadena de prueba"
local texto_2 = 'otra cadena' ;
local bandera = true
local otraBandera = false
local nulo = nil
local tabla = {1}

-- Asignaciones y encadenamiento de expresiones aritméticas largas
v1 = v1 + v2 * 10 ^ 2 % 3 - 5 / 2
v2 = ( 100 + v1 ) * ( v2 - 10.5 ) ^ ( 2 % 1 )


-- Bloque IF profundo y redundante con múltiples ramas
if ( v1 == 10 ) and ( v2 ~= 20.5 ) or not bandera then
    print ( "rama 1", v1, v2 ) ;
    local x = 0
    
    if x < 10 then
        x = x + 1
    elseif x <= 50 then
        x = x * 2
    elseif x > 100 then
        x = x / 2 ;
    else
	x = x ^ 2
        print ( x )
    end
    
elseif bandera == true and otraBandera == false then
    print ( "rama 2" )
    v2 = - v2 
else
    -- Bucle WHILE anidado dentro de un ELSE
    while not ( v1 >= 1000 ) do
        v1 = v1 + 10 ;
        
        -- Bucle REPEAT...UNTIL anidado
        repeat
            v1 = v1 - 1
            print ( "ciclo interno", v1 )
        until v1 == 0 ;
        
    end
end

-- Bloque FOR con paso implícito
for iterador = 1, 100 do
    print ( iterador, texto_1 )
    
    if iterador == 50 then
        local temporal = nil
        temporal = 100 % 3
    end
end ;

-- Bloque FOR con paso explícito (negativo) y redundancia de código
for i = 100, 0, -2 do
    local basura = "texto"
    print ( basura, i )
    
    -- Duplicación de estructuras para probar la pila del parser
    if true then
        if true then
            if false then
                print ( "profundidad 3" )
            else
                print ( "escape" ) ;
            end
        end
    end
end

-- Operaciones lógicas puras y reasignación absurda
bandera = ( v1 == v2 ) and ( 10 > 5 ) or ( texto_1 ~= texto_2 )
nulo = not bandera or not not otraBandera ;

-- Impresión final múltiple
print ( v1, v2, texto_1, texto_2, bandera, nulo, 100, "FIN" ) ;
