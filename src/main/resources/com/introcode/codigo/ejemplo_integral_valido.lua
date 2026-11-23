-- EJEMPLO INTEGRAL INTRO LUA
-- Archivo de demostracion para recorrer los analizadores y el optimizador.
-- El lenguaje implementa un subconjunto educativo inspirado en Lua.
-- No se incluyen funciones definidas por el usuario ni acceso por indices.
-- Las instrucciones se separan por salto de linea o punto y coma opcional.

-- 1. DECLARACIONES, IDENTIFICADORES Y TIPOS DE LITERALES
local titulo = "Inventario escolar"
local subtitulo = 'Demostracion de IntroLua'
local activo = true
local terminado = false
local datoVacio = nil
local cantidad = 12
local precio = 18.5
local descuento = 2.5
local existencia = 40
local unidad = 1
local mensaje = "Inicio del reporte"
print(titulo, subtitulo, mensaje)
print(activo, terminado, datoVacio)
print(cantidad, precio, descuento, existencia, unidad)

-- 2. OPERADORES ARITMETICOS Y PRIORIDAD
local suma = cantidad + existencia
local resta = existencia - cantidad
local producto = cantidad * unidad
local cociente = existencia / cantidad
local residuo = existencia % cantidad
local potencia = unidad ^ 3
local combinadoUno = cantidad + existencia * unidad
local combinadoDos = (cantidad + existencia) * unidad
local combinadoTres = precio - descuento * unidad
local combinadoCuatro = (precio - descuento) * unidad
local combinadoCinco = cantidad / unidad + existencia
local combinadoSeis = cantidad + existencia / unidad
local combinadoSiete = (cantidad + existencia) / unidad
local combinadoOcho = existencia % cantidad + unidad
local combinadoNueve = existencia ^ unidad * cantidad
local combinadoDiez = - descuento
print(suma, resta, producto, cociente, residuo, potencia)
print(combinadoUno, combinadoDos, combinadoTres, combinadoCuatro)
print(combinadoCinco, combinadoSeis, combinadoSiete)
print(combinadoOcho, combinadoNueve, combinadoDiez)

-- 3. EXPRESIONES PARA NOTACION PREFIJA, POSFIJA Y CUADRUPLOS
local opA = 2
local opB = 3
local opC = 4
local opD = 5
local expr01 = opA + opB
local expr02 = opA + opB * opC
local expr03 = (opA + opB) * opC
local expr04 = opA * opB + opC / opD
local expr05 = (opA + opB) * (opC - opD)
local expr06 = opA + (opB - opC) * opD
local expr07 = (opA - opB) / opC + opD
local expr08 = opA * (opB + opC) - opD
local expr09 = (opA + opB) / (opC - opA) + opD
local expr10 = opA + opB * (opC - opD)
local expr11 = ((opA + opB) * opC) - opD
local expr12 = (opA - opB) * (opC + opD)
local expr13 = opA ^ opB % opC
local expr14 = (opA + opB) ^ (opC - opA)
local expr15 = -opA + opB * opC
print(expr01, expr02, expr03, expr04, expr05)
print(expr06, expr07, expr08, expr09, expr10)
print(expr11, expr12, expr13, expr14, expr15)

-- 4. OPERADORES RELACIONALES Y LOGICOS
local esMayor = existencia > cantidad
local esMenor = cantidad < existencia
local esIgual = unidad == 1
local esDiferente = cantidad ~= existencia
local esMayorIgual = existencia >= cantidad
local esMenorIgual = cantidad <= existencia
local noTerminado = not terminado
local ambosPositivos = activo and noTerminado
local algunValorPequeno = activo or terminado
local decisionUno = activo and noTerminado
local decisionDos = activo or terminado
local decisionTres = not (cantidad == 0)
local decisionCuatro = noTerminado and activo
local decisionCinco = activo or terminado
print(esMayor, esMenor, esIgual, esDiferente)
print(esMayorIgual, esMenorIgual, ambosPositivos)
print(algunValorPequeno, noTerminado, decisionUno)
print(decisionDos, decisionTres, decisionCuatro, decisionCinco)

-- 5. CADENAS, BOOLEANOS Y NIL
local estadoUno = true
local estadoDos = false
local estadoTres = nil
local textoUno = "Entrada recibida"
local textoDos = 'Proceso terminado'
local textoTres = "Sin datos"
local textoCuatro = 'Valor disponible'
local textoCinco = "Resumen"
print(textoUno, textoDos, textoTres, textoCuatro, textoCinco)
print(estadoUno, estadoDos, estadoTres)
estadoDos = true
estadoTres = nil
print(estadoDos, estadoTres)

-- 6. TABLAS CON CAMPOS POSICIONALES Y NOMBRADOS
local tablaVacia = {}
local calificaciones = {10, 8, 9, 7}
local productoTabla = {nombre = "Cuaderno", precio = 18.5, piezas = 12}
local mezclaTabla = {1, "lapiz", true, nil}
local medidasTabla = {ancho = 4, alto = 8, fondo = 2}
local tablaDeResultados = {suma, resta, producto}
local tablaDeEstados = {primero = activo, segundo = terminado}
local tablaDeTexto = {"inicio", "medio", "fin"}
local tablaNumerica = {2, 4, 6, 8, 10}
local tablaMixta = {id = 15, "registro", vigente = true}
print(tablaVacia)
print(calificaciones)
print(productoTabla)
print(mezclaTabla)
print(medidasTabla)
print(tablaDeResultados)
print(tablaDeEstados)
print(tablaDeTexto)
print(tablaNumerica)
print(tablaMixta)

-- 7. ASIGNACION Y ACTUALIZACION DE VALORES
local acumulador = 0
local saldo = 100
local paso = 5
local banderaProceso = false
acumulador = acumulador + cantidad
acumulador = acumulador + existencia
saldo = saldo - descuento
saldo = saldo + precio
paso = paso * unidad
banderaProceso = activo and noTerminado
print(acumulador, saldo, paso, banderaProceso)
acumulador = acumulador % existencia
saldo = saldo / unidad
paso = paso ^ 2
print(acumulador, saldo, paso)

-- 8. CONDICIONAL IF CON RAMA ELSE
local resultadoIf = 0
local etiquetaIf = "sin clasificar"
if cantidad > 10 then
    resultadoIf = cantidad + 1
    etiquetaIf = "cantidad alta"
else
    resultadoIf = cantidad - 1
    etiquetaIf = "cantidad baja"
end
print(resultadoIf, etiquetaIf)

-- 9. CONDICIONAL IF, ELSEIF Y ELSE
local puntaje = 85
local categoriaPuntaje = "pendiente"
if puntaje >= 90 then
    categoriaPuntaje = "excelente"
elseif puntaje >= 80 then
    categoriaPuntaje = "bueno"
elseif puntaje >= 70 then
    categoriaPuntaje = "regular"
else
    categoriaPuntaje = "requiere apoyo"
end
print(puntaje, categoriaPuntaje)

-- 10. CONDICIONALES ANIDADOS
local acceso = true
local pagoAlDia = true
local resultadoAcceso = "rechazado"
if acceso then
    if pagoAlDia then
        resultadoAcceso = "permitido"
    else
        resultadoAcceso = "pendiente de pago"
    end
else
    resultadoAcceso = "sin autorizacion"
end
print(resultadoAcceso)

-- 11. CONDICIONES CON OPERADORES LOGICOS
local edad = 20
local credencial = true
local acompanante = false
local esAdulto = edad >= 18
local permiteEntrada = credencial and activo
local permiteExcepcion = acompanante or credencial
local bloqueaEntrada = not permiteEntrada
if credencial and not acompanante then
    print("Puede ingresar")
else
    print("No puede ingresar")
end
if permiteExcepcion or acompanante then
    print("Revisar excepcion")
else
    print("Sin excepcion")
end
print(permiteEntrada, permiteExcepcion, bloqueaEntrada)

-- 12. CICLO WHILE CON CONTADOR Y ACTUALIZACION
local contadorWhile = 0
local sumaWhile = 0
while contadorWhile < 5 do
    sumaWhile = sumaWhile + contadorWhile
    contadorWhile = contadorWhile + 1
    print("while", contadorWhile, sumaWhile)
end
print("fin while", contadorWhile, sumaWhile)

-- 13. CICLO FOR CON PASO IMPLICITO
local indiceForUno = 1
local sumaForUno = 0
for indiceForUno = 1, 4 do
    sumaForUno = sumaForUno + indiceForUno
    print("for implicito", indiceForUno, sumaForUno)
end
print("total for uno", sumaForUno)

-- 14. CICLO FOR CON PASO POSITIVO EXPLICITO
local indiceForDos = 1
local sumaForDos = 0
for indiceForDos = 2, 10, 2 do
    sumaForDos = sumaForDos + indiceForDos
    print("for positivo", indiceForDos, sumaForDos)
end
print("total for dos", sumaForDos)

-- 15. CICLO FOR CON PASO NEGATIVO
local indiceForTres = 5
local cuentaRegresiva = 0
for indiceForTres = 5, 1, -1 do
    cuentaRegresiva = cuentaRegresiva + indiceForTres
    print("for regresivo", indiceForTres, cuentaRegresiva)
end
print("total for tres", cuentaRegresiva)

-- 16. CICLO REPEAT UNTIL
local contadorRepeat = 0
local sumaRepeat = 0
repeat
    contadorRepeat = contadorRepeat + 1
    sumaRepeat = sumaRepeat + contadorRepeat
    print("repeat", contadorRepeat, sumaRepeat)
until contadorRepeat >= 4
print("fin repeat", contadorRepeat, sumaRepeat)

-- 17. EXPRESIONES CONSTANTES PARA DOBLAMIENTO
local constanteUno = 4 + 1
local constanteDos = 5 + 3 * 2
local constanteTres = (5 + 3) * 2
local constanteCuatro = 100 / 4
local constanteCinco = 17 % 5
local constanteSeis = 2 ^ 5
local constanteSiete = (8 - 3) * (4 + 1)
local constanteOcho = 20 / 2 + 3 * 4
local constanteNueve = 9 ^ 2 - 1
local constanteDiez = (12 + 8) / 4
print(constanteUno, constanteDos, constanteTres)
print(constanteCuatro, constanteCinco, constanteSeis)
print(constanteSiete, constanteOcho, constanteNueve, constanteDiez)

-- 18. CASOS DE ASIGNACIONES REDUNDANTES PARA OBSERVAR OPTIMIZACION
local valorTemporalUno = 10
local valorTemporalDos = 20
local valorTemporalTres = valorTemporalUno + valorTemporalDos
local valorQueSeReemplaza = 1
valorQueSeReemplaza = 2
local variableNoUsadaUno = 99
local variableNoUsadaDos = 3 + 4
local variableNoUsadaTres = "sin lectura"
print(valorTemporalTres, valorQueSeReemplaza)

-- 19. RAMAS CONSTANTES PARA OBSERVAR ELIMINACION DE CODIGO INALCANZABLE
local constanteVerdadera = true
local constanteFalsa = false
local valorRama = 0
if constanteVerdadera then
    valorRama = 1
else
    valorRama = 2
end
if constanteFalsa then
    valorRama = 3
else
    valorRama = valorRama + 1
end
print(valorRama)

-- 20. VARIABLES Y RESULTADOS PARA LA TABLA DE SIMBOLOS
local simboloNumero = 321
local simboloDecimal = 45.75
local simboloCadena = "tabla de simbolos"
local simboloBooleano = true
local simboloNulo = nil
local simboloCalculado = simboloNumero + 9
local simboloLogico = simboloBooleano and activo
print(simboloNumero, simboloDecimal, simboloCadena)
print(simboloBooleano, simboloNulo, simboloCalculado, simboloLogico)

-- 21. IMPRESIONES CON VARIOS ARGUMENTOS
print("reporte", titulo, cantidad, precio)
print("operaciones", suma, resta, producto, cociente)
print("expresiones", expr01, expr05, expr10, expr15)
print("condiciones", esMayor, esIgual, decisionUno)
print("tablas", calificaciones, productoTabla, tablaMixta)
print("ciclos", sumaWhile, sumaForUno, sumaForDos, cuentaRegresiva)
print("constantes", constanteUno, constanteCinco, constanteSeis)
print("fin del ejemplo integral valido")
