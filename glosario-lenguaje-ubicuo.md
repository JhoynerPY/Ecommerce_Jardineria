# Glosario del Lenguaje Ubicuo - [Venta de Plantas y Jardinería]

## Conceptos Centrales

### [Ejemplar]
**Definición:** [Unidad física individual de una planta en el inventario que puede tener atributos propios como tamaño, edad, estado, precio, etc.]

**Sinónimos aceptados:** [Unidad (Inventario)]

**No usar:** [Producto, Item, Stock]

**Ejemplo de uso:**
\`\`\`java
[Existen 3 ejemplares de monstera deliciosa en stock en los que: 2 estan en etapa joven y 1 en floracion]
\`\`\`

---

### [Especie]    
**Definición:** [Tipo o variedad de planta que existe en el catálogo o stock, independientemente de las unidades físicas disponibles]

**Sinónimos aceptados:** [Variedad]

**No usar:** [Producto, Tipo, Articulo]

**Ejemplo de uso:**
\`\`\`java
[El cliente filtró el catálogo por especie, con el fin de observar variedades de cactáceas]
\`\`\`

---

### [FichaDeCuidado]
**Definición:** [Es un conjunto estructurado de instrucciones para el mantenimiento a una especie específica de una planta, para garantizar el mejor estado posible]

**Sinónimos aceptados:** [GuiaCuidado]

**No usar:** [Descripción, DetallesProducto]

**Ejemplo de uso:**
\`\`\`java
[Antes de vender una planta o ejemplar, se revisa la ficha de cuidado para explicarle al cliente qué tan seguido debe regarla y si necesita sol directo o sombra]
\`\`\`
---





### [Riesgo]
**Definición:** [Es el nivel y descripción del peligro en cuanto a toxicidad que presenta una especie de plantas, y se usa especialmente para mascotas o niños]

**Sinónimos aceptados:** [Toxicidad, NivelToxicidad]

**No usar:** [Advertencia, Alerta]

**Ejemplo de uso:**
\`\`\`java
[Antes de confirmar la compra el sistema mostró un mensaje de riesgo que esa especie indica que es tóxica para mascotas, especialmente gatos]
\`\`\`
---

### [EtapaDeCrecimiento]
**Definición:** [Se usa como fase del desarrollo biológico del ejemplar]

**Sinónimos aceptados:** [FaseDesarrollo]

**No usar:** [Estado]

**Ejemplo de uso:**
\`\`\`java
[Los ejemplares en EtapaDeCrecimiento de floración, suelen venderse a mejor precio]
\`\`\`
---

## Anti-patrones (Términos a EVITAR en nuestro proyecto)

| No usar | Usar |
|---|---|
| [Producto] | [Ejemplar - Especie] |
| [Estado] | [Etapa Crecimiento] |
| [Advertencia] | [Alerta - Riesgo] |
