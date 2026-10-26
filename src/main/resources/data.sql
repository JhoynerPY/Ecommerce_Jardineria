-- Script de datos iniciales para Ecommerce Jardineria
-- Viveros, Clientes, Especies, Ejemplares, Artículos de Kit, Kits, Pedidos

-- ============================================
-- VIVEROS
-- ============================================
INSERT INTO viveros (id, nombre, calle, ciudad, departamento, codigo_postal, pais, latitud, longitud, telefono, email, radio_cobertura_km, activo, deleted) VALUES
('vivero-001', 'Vivero El Jardín', 'Calle 10 #5-20', 'Armenia', 'Quindío', '630001', 'Colombia', 4.5339, -75.6811, '3101234567', 'contacto@eljardin.com', 15.0, true, false),
('vivero-002', 'Vivero La Floresta', 'Carrera 15 #20-30', 'Pereira', 'Risaralda', '660001', 'Colombia', 4.8133, -75.6947, '3107654321', 'info@lafloresta.com', 20.0, true, false),
('vivero-003', 'Vivero Verde Valle', 'Avenida 30 #45-10', 'Manizales', 'Caldas', '170001', 'Colombia', 5.0703, -75.5144, '3159876543', 'ventas@verdevalle.com', 10.0, true, false),
('vivero-004', 'Vivero Los Andes', 'Calle 50 #10-15', 'Bogotá', 'Cundinamarca', '110111', 'Colombia', 4.7110, -74.0721, '3201122334', 'pedidos@losandes.com', 25.0, true, false),
('vivero-005', 'Vivero Tropical', 'Carrera 8 #12-40', 'Cali', 'Valle del Cauca', '760001', 'Colombia', 3.4516, -76.5320, '3185566778', 'hola@tropical.com', 18.0, true, false);

-- ============================================
-- CLIENTES
-- ============================================
INSERT INTO clientes (id, nombre, email_valor, telefono, calle, ciudad, departamento, codigo_postal, pais, latitud, longitud, vivero_asignado_id, deleted) VALUES
('cliente-001', 'María González', 'maria.gonzalez@email.com', '3001112233', 'Calle 20 #10-05', 'Armenia', 'Quindío', '630002', 'Colombia', 4.5350, -75.6800, 'vivero-001', false),
('cliente-002', 'Carlos Rodríguez', 'carlos.rodriguez@email.com', '3002223344', 'Carrera 10 #15-20', 'Pereira', 'Risaralda', '660002', 'Colombia', 4.8150, -75.6900, 'vivero-002', false),
('cliente-003', 'Ana Martínez', 'ana.martinez@email.com', '3003334455', 'Avenida 5 #30-40', 'Manizales', 'Caldas', '170002', 'Colombia', 5.0720, -75.5100, 'vivero-003', false),
('cliente-004', 'Luis Fernández', 'luis.fernandez@email.com', '3004445566', 'Calle 80 #12-30', 'Bogotá', 'Cundinamarca', '110112', 'Colombia', 4.7150, -74.0700, 'vivero-004', false),
('cliente-005', 'Sofía Herrera', 'sofia.herrera@email.com', '3005556677', 'Carrera 25 #8-15', 'Cali', 'Valle del Cauca', '760002', 'Colombia', 3.4550, -76.5300, 'vivero-005', false),
('cliente-006', 'Jorge López', 'jorge.lopez@email.com', '3006667788', 'Calle 12 #5-50', 'Armenia', 'Quindío', '630003', 'Colombia', 4.5300, -75.6850, 'vivero-001', false),
('cliente-007', 'Paula Jiménez', 'paula.jimenez@email.com', '3007778899', 'Carrera 30 #20-10', 'Pereira', 'Risaralda', '660003', 'Colombia', 4.8100, -75.7000, 'vivero-002', false);

-- ============================================
-- ESPECIES
-- ============================================
INSERT INTO especies (id, nombre_comun, nombre_cientifico, requerimiento_luz, frecuencia_riego, sustrato, temporada_floracion, riesgo_nivel, riesgo_descripcion, deleted) VALUES
('especie-001', 'Rosa', 'Rosa rubiginosa', 'SOL', 'Cada 2 días', 'Tierra negra con compost', 'Primavera-Verano', 'NINGUNO', 'Sin riesgo de toxicidad', false),
('especie-002', 'Orquídea', 'Phalaenopsis amabilis', 'SOMBRA_PARCIAL', 'Cada 3 días', 'Corteza de pino y musgo', 'Todo el año', 'BAJO', 'Ligeramente tóxica si se ingiere', false),
('especie-003', 'Helecho', 'Nephrolepis exaltata', 'SOMBRA_TOTAL', 'Diario', 'Turba y perlita', 'No florece', 'NINGUNO', 'Sin riesgo de toxicidad', false),
('especie-004', 'Cactús', 'Echinocactus grusonii', 'SOL', 'Cada 15 días', 'Arena y tierra arenosa', 'Verano', 'NINGUNO', 'Sin riesgo de toxicidad', false),
('especie-005', 'Lirio de la paz', 'Spathiphyllum wallisii', 'SOMBRA_PARCIAL', 'Cada 2 días', 'Tierra rica en materia orgánica', 'Primavera', 'MODERADO', 'Tóxica para mascotas', false),
('especie-006', 'Girasol', 'Helianthus annuus', 'SOL', 'Diario', 'Tierra suelta y bien drenada', 'Verano', 'NINGUNO', 'Sin riesgo de toxicidad', false),
('especie-007', 'Anturio', 'Anthurium andraeanum', 'SOMBRA_PARCIAL', 'Cada 3 días', 'Musgo sphagnum y perlita', 'Todo el año', 'ALTO', 'Muy tóxica si se ingiere', false),
('especie-008', 'Suculenta Echeveria', 'Echeveria elegans', 'SOL', 'Cada 10 días', 'Sustrato para cactus y suculentas', 'Primavera', 'NINGUNO', 'Sin riesgo de toxicidad', false);

-- ============================================
-- EJEMPLARES (Plantas individuales en inventario)
-- ============================================
INSERT INTO ejemplares (id, especie_id, etapa_crecimiento, precio_valor, precio_moneda, disponibilidad, deleted) VALUES
-- Rosas (especie-001)
('ejemplar-001', 'especie-001', 'ADULTA', 25000, 'COP', true, false),
('ejemplar-002', 'especie-001', 'FLORACION', 30000, 'COP', true, false),
('ejemplar-003', 'especie-001', 'JOVEN', 15000, 'COP', true, false),
('ejemplar-004', 'especie-001', 'ADULTA', 25000, 'COP', true, false),
('ejemplar-005', 'especie-001', 'PLANTULA', 10000, 'COP', true, false),

-- Orquídeas (especie-002)
('ejemplar-006', 'especie-002', 'ADULTA', 45000, 'COP', true, false),
('ejemplar-007', 'especie-002', 'FLORACION', 55000, 'COP', true, false),
('ejemplar-008', 'especie-002', 'JOVEN', 30000, 'COP', true, false),

-- Helechos (especie-003)
('ejemplar-009', 'especie-003', 'ADULTA', 18000, 'COP', true, false),
('ejemplar-010', 'especie-003', 'JOVEN', 12000, 'COP', true, false),
('ejemplar-011', 'especie-003', 'ADULTA', 18000, 'COP', true, false),

-- Cactus (especie-004)
('ejemplar-012', 'especie-004', 'ADULTA', 35000, 'COP', true, false),
('ejemplar-013', 'especie-004', 'JOVEN', 20000, 'COP', true, false),

-- Lirios de la paz (especie-005)
('ejemplar-014', 'especie-005', 'FLORACION', 28000, 'COP', true, false),
('ejemplar-015', 'especie-005', 'ADULTA', 22000, 'COP', true, false),

-- Girasoles (especie-006)
('ejemplar-016', 'especie-006', 'FLORACION', 15000, 'COP', true, false),
('ejemplar-017', 'especie-006', 'JOVEN', 8000, 'COP', true, false),
('ejemplar-018', 'especie-006', 'ADULTA', 12000, 'COP', true, false),

-- Anturios (especie-007)
('ejemplar-019', 'especie-007', 'ADULTA', 32000, 'COP', true, false),
('ejemplar-020', 'especie-007', 'FLORACION', 38000, 'COP', true, false),

-- Suculentas (especie-008)
('ejemplar-021', 'especie-008', 'ADULTA', 15000, 'COP', true, false),
('ejemplar-022', 'especie-008', 'JOVEN', 10000, 'COP', true, false),
('ejemplar-023', 'especie-008', 'ADULTA', 15000, 'COP', true, false),
('ejemplar-024', 'especie-008', 'PLANTULA', 8000, 'COP', true, false);

-- ============================================
-- ARTÍCULOS DE KIT (Catálogo de plantas, macetas, sustratos)
-- ============================================
INSERT INTO articulos_kit (id, nombre, precio_valor, precio_moneda, diametro, deleted) VALUES
-- Plantas para kits
('planta-001', 'Rosa miniatura', 15000, 'COP', 15.0, false),
('planta-002', 'Orquídea phalaenopsis', 35000, 'COP', 12.0, false),
('planta-003', 'Suculenta variada', 12000, 'COP', 8.0, false),
('planta-004', 'Cactus pequeño', 18000, 'COP', 10.0, false),
('planta-005', 'Helecho Boston', 14000, 'COP', 20.0, false),

-- Macetas
('maceta-001', 'Maceta cerámica 15cm', 12000, 'COP', 15.0, false),
('maceta-002', 'Maceta cerámica 20cm', 18000, 'COP', 20.0, false),
('maceta-003', 'Maceta plástico 12cm', 8000, 'COP', 12.0, false),
('maceta-004', 'Maceta plástico 18cm', 12000, 'COP', 18.0, false),
('maceta-005', 'Maceta barro 25cm', 25000, 'COP', 25.0, false),

-- Sustratos
('sustrato-001', 'Sustrato universal 5L', 10000, 'COP', 0.0, false),
('sustrato-002', 'Sustrato para cactus 5L', 12000, 'COP', 0.0, false),
('sustrato-003', 'Sustrato para orquídeas 5L', 15000, 'COP', 0.0, false),
('sustrato-004', 'Tierra para macetas 10L', 18000, 'COP', 0.0, false),
('sustrato-005', 'Compost orgánico 5kg', 14000, 'COP', 0.0, false);

-- ============================================
-- KITS DE JARDINERÍA
-- ============================================
INSERT INTO kits_jardineria (id, planta_id, maceta_id, sustrato_id, deleted) VALUES
('kit-001', 'planta-001', 'maceta-001', 'sustrato-001', false),  -- Rosa + Maceta cerámica 15 + Sustrato universal
('kit-002', 'planta-002', 'maceta-002', 'sustrato-003', false),  -- Orquídea + Maceta cerámica 20 + Sustrato orquídeas
('kit-003', 'planta-003', 'maceta-003', 'sustrato-001', false),  -- Suculenta + Maceta plástico 12 + Sustrato universal
('kit-004', 'planta-004', 'maceta-004', 'sustrato-002', false),  -- Cactus + Maceta plástico 18 + Sustrato cactus
('kit-005', 'planta-005', 'maceta-005', 'sustrato-004', false);  -- Helecho + Maceta barro 25 + Tierra macetas

-- ============================================
-- PEDIDOS (ejemplos con diferentes estados)
-- ============================================
INSERT INTO pedidos (id, id_cliente, id_vivero, estado, direccion_entrega, exencion_tipo, exencion_terminos, exencion_aceptada, exencion_version, deleted) VALUES
('pedido-001', 'cliente-001', 'vivero-001', 'ENTREGADO', 'Calle 20 #10-05, Armenia, Quindío 630002, Colombia', 'ACUERDO_PARCIAL_PERDIDA', 'El vivero no se hace responsable por daños menores en transporte (hasta 50% del valor)', true, '1.0', false),
('pedido-002', 'cliente-002', 'vivero-002', 'ENVIADO', 'Carrera 10 #15-20, Pereira, Risaralda 660002, Colombia', 'SIN_EXENCION', 'Sin exención de responsabilidad aplicada', true, '1.0', false),
('pedido-003', 'cliente-003', 'vivero-003', 'PAGADO', 'Avenida 5 #30-40, Manizales, Caldas 170002, Colombia', 'EXENCION_TOTAL', 'El cliente exime al vivero de toda responsabilidad por daños en transporte', true, '1.0', false),
('pedido-004', 'cliente-004', 'vivero-004', 'PENDIENTE_PAGO', 'Calle 80 #12-30, Bogotá, Cundinamarca 110112, Colombia', 'ACUERDO_PARCIAL_PERDIDA', 'Acuerdo parcial: cobertura del 50% por daños en transporte', true, '1.0', false),
('pedido-005', 'cliente-005', 'vivero-005', 'ENTREGADO', 'Carrera 25 #8-15, Cali, Valle del Cauca 760002, Colombia', 'SIN_EXENCION', 'Sin exención de responsabilidad aplicada', true, '1.0', false),
('pedido-006', 'cliente-006', 'vivero-001', 'ENTREGADO', 'Calle 12 #5-50, Armenia, Quindío 630003, Colombia', 'EXENCION_TOTAL', 'Exención total de responsabilidad por daños en envío', true, '1.0', false),
('pedido-007', 'cliente-007', 'vivero-002', 'RECHAZADO', 'Carrera 30 #20-10, Pereira, Risaralda 660003, Colombia', 'SIN_EXENCION', 'Sin exención de responsabilidad aplicada', true, '1.0', false);

-- ============================================
-- DETALLES DE PEDIDO
-- ============================================
INSERT INTO detalles_pedido (id, pedido_id, id_producto, cantidad, precio_unitario, deleted) VALUES
-- Pedido 001: 2 Rosas + 1 Kit de Rosa
('detalle-001', 'pedido-001', 'ejemplar-001', 1, 25000, false),
('detalle-002', 'pedido-001', 'ejemplar-002', 1, 30000, false),
('detalle-003', 'pedido-001', 'kit-001', 1, 31450, false), -- Precio con descuento 15%

-- Pedido 002: 1 Orquídea + 1 Helecho
('detalle-004', 'pedido-002', 'ejemplar-006', 1, 45000, false),
('detalle-005', 'pedido-002', 'ejemplar-009', 1, 18000, false),

-- Pedido 003: 2 Cactus + 1 Kit de Cactus
('detalle-006', 'pedido-003', 'ejemplar-012', 1, 35000, false),
('detalle-007', 'pedido-003', 'ejemplar-013', 1, 20000, false),
('detalle-008', 'pedido-003', 'kit-004', 1, 29750, false), -- Precio con descuento 15%

-- Pedido 004: 1 Lirio + 1 Kit de Orquídea
('detalle-009', 'pedido-004', 'ejemplar-014', 1, 28000, false),
('detalle-010', 'pedido-004', 'kit-002', 1, 54400, false), -- Precio con descuento 15%

-- Pedido 005: 2 Girasoles + 1 Suculenta
('detalle-011', 'pedido-005', 'ejemplar-016', 1, 15000, false),
('detalle-012', 'pedido-005', 'ejemplar-017', 1, 8000, false),
('detalle-013', 'pedido-005', 'ejemplar-021', 1, 15000, false),

-- Pedido 006: 1 Anturio + 1 Kit de Helecho
('detalle-014', 'pedido-006', 'ejemplar-019', 1, 32000, false),
('detalle-015', 'pedido-006', 'kit-005', 1, 40800, false), -- Precio con descuento 15%

-- Pedido 007: 1 Kit de Suculenta (rechazado)
('detalle-016', 'pedido-007', 'kit-003', 1, 29750, false);