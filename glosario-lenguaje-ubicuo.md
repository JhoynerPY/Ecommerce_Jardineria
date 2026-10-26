# Glosario del Lenguaje Ubicuo - [E-commerce Jardinería - Modelo Intermediario Viveros]

## Conceptos Centrales

### Ejemplar
**Definición:** Unidad física individual de una planta en el inventario del vivero que puede tener atributos propios como tamaño, edad, estado, precio, etc.

**Sinónimos aceptados:** Unidad (Inventario)

**No usar:** Producto, Item, Stock

**Ejemplo de uso:**
```java
[Existen 3 ejemplares de monstera deliciosa en stock en el vivero asignado en los que: 2 estan en etapa joven y 1 en floracion]
```

---

### Especie
**Definición:** Tipo o variedad de planta que existe en el catálogo o stock del vivero, independientemente de las unidades físicas disponibles

**Sinónimos aceptados:** Variedad

**No usar:** Producto, Tipo, Articulo

**Ejemplo de uso:**
```java
El cliente filtró el catálogo por especie, con el fin de observar variedades de cactáceas disponibles en su vivero asignado
```

---

### FichaDeCuidado
**Definición:** Es un conjunto estructurado de instrucciones para el mantenimiento a una especie específica de una planta, para garantizar el mejor estado posible

**Sinónimos aceptados:** GuiaCuidado

**No usar:** Descripción, DetallesProducto

**Ejemplo de uso:**
```java
Antes de vender una planta o ejemplar, se revisa la ficha de cuidado para explicarle al cliente qué tan seguido debe regarla y si necesita sol directo o sombra
```

---

### Riesgo
**Definición:** Es el nivel y descripción del peligro en cuanto a toxicidad que presenta una especie de plantas, y se usa especialmente para mascotas o niños

**Sinónimos aceptados:** Toxicidad, NivelToxicidad

**No usar:** Advertencia, Alerta

**Ejemplo de uso:**
```java
Antes de confirmar la compra el sistema mostró un mensaje de riesgo que esa especie indica que es tóxica para mascotas, especialmente gatos
```

---

### EtapaDeCrecimiento
**Definición:** Se usa como fase del desarrollo biológico del ejemplar

**Sinónimos aceptados:** FaseDesarrollo

**No usar:** Estado

**Ejemplo de uso:**
```java
Los ejemplares en EtapaDeCrecimiento de floración, suelen venderse a mejor precio
```

---

### Vivero
**Definición:** Establecimiento local aliado que cultiva y provee las plantas. El e-commerce actúa como intermediario conectando al comprador con el vivero más cercano según su dirección.

**Sinónimos aceptados:** Nursery, ProveedorLocal

**No usar:** Tienda, Almacen, Bodega

**Ejemplo de uso:**
```java
El sistema asignó automáticamente el vivero "Jardines del Norte" al cliente por estar a 3.2km de su dirección
```

---

### RadioDeCobertura
**Definición:** Área geográfica (en kilómetros) alrededor del vivero dentro de la cual este puede realizar entregas directas.

**Sinónimos aceptados:** AreaDeEntrega, ZonaCobertura

**No usar:** Radio, Distancia

**Ejemplo de uso:**
```java
El vivero tiene un radio de cobertura de 15km, por lo que puede atender pedidos en barrios aledaños
```

---

### Direccion
**Definición:** Ubicación geográfica completa (calle, ciudad, departamento, código postal, país, coordenadas) usada para calcular el vivero más cercano y gestionar entregas.

**Sinónimos aceptados:** Ubicacion, Coordenadas

**No usar:** DireccionString, Location

**Ejemplo de uso:**
```java
La dirección del cliente se usa para encontrar el vivero más cercano mediante cálculo de distancia geodésica
```

---

### ExencionResponsabilidad
**Definición:** Acuerdo legal que define los términos bajo los cuales el vivero y la plataforma se eximen o limitan responsabilidad por daños durante el transporte o pérdidas parciales.

**Sinónimos aceptados:** AcuerdoDañosTransporte, LimitacionResponsabilidad

**No usar:** TerminaciónLegal, Disclaimer

**Ejemplo de uso:**
```java
El cliente aceptó la exención parcial: la plataforma cubre 50% del valor en caso de daño total durante el transporte
```

---

### TipoExencion
**Definición:** Clasificación del nivel de cobertura de responsabilidad: EXENCION_TOTAL (sin cobertura), ACUERDO_PARCIAL_PERDIDA (cobertura 50%), SIN_EXENCION (cobertura total).

**Sinónimos aceptados:** NivelCobertura, ModalidadResponsabilidad

**No usar:** TipoSeguro, CategoriaRiesgo

**Ejemplo de uso:**
```java
Para plantas frágiles se ofrece ACUERDO_PARCIAL_PERDIDA; para semillas se aplica SIN_EXENCION
```

---

## Anti-patrones (Términos a EVITAR en nuestro proyecto)

| No usar | Usar |
|---|---|
| Producto | Ejemplar - Especie |
| Estado | Etapa Crecimiento |
| Advertencia | Alerta - Riesgo |
| Tienda/Almacén | Vivero |
| DespachoDirecto | IntermediacionVivero |
| EnvíoPropio | EntregaViveroCercano |