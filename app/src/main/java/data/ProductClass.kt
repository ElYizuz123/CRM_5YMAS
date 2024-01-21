package data

class ProductClass {
    fun getProducts(): List<Producto>{
        var products = listOf<Producto>(
            Producto(0, "POVASA013-B", "Camiseta Polipapel Color Eco Mini", 25, 31.13, 0),
            Producto(1, "POVASA014-B", "Camiseta Polipapel Color Eco Chica", 25, 29.96, 1),
            Producto(2, "POVASA015-B", "Camiseta Polipapel Color Eco Mediana", 25, 29.96, 1),
            Producto(3, "POVASA016-B", "Camiseta Polipapel Color Eco Grande", 25, 29.96, 1),
            Producto(4, "POVASA157-B", "Camiseta Polipapel Color Eco Jumbo 4", 25, 31.13, 0),
            Producto(5, "POVASA164-B", "Camiseta Polipapel Color Eco Jumbo 6", 25, 31.13, 0),
            Producto(6, "POVASA083-B", "Camiseta Polipapel Negra Eco Mini", 25, 31.13, 0),
            Producto(7, "POVASA017-B", "Camiseta Polipapel Negra Eco Chica", 25, 29.96, 1),
            Producto(8, "POVASA018-B", "Camiseta Polipapel Negra Eco Mediana", 25, 29.96, 1),
            Producto(9, "POVASA019-B", "Camiseta Polipapel Negra Eco Grande", 25, 29.96, 1),
            Producto(10, "POVASA020-B", "Camiseta Polipapel Negra Eco Jumbo 4", 25, 31.13, 0),
            Producto(11, "POVASA145-B", "Camiseta Polipapel Negra Eco Jumbo 5", 25, 31.13, 0),
            Producto(12, "POVASA021-B", "Camiseta Polipapel Negra Eco Jumbo 6", 25, 31.13, 0),
            Producto(13, "POVASA158-B", "Camiseta Polipapel Blanca Eco Mini", 25, 31.13, 0),
            Producto(14, "POVASA159-B", "Camiseta Polipapel Blanca Eco Chica", 25, 29.96, 1),
            Producto(15, "POVASA160-B", "Camiseta Polipapel Blanca Eco Mediana", 25, 29.96, 1),
            Producto(16, "POVASA167-B", "Camiseta Polipapel Blanca Eco Grande", 25, 29.96, 1),
            Producto(17, "POVASA173-B", "Camiseta Polipapel Blanca Eco Jumbo 4", 25, 31.13, 0),
            Producto(18, "POVASA174-B", "Camiseta Polipapel Blanca Eco Jumbo 6", 25, 31.13, 0),
            Producto(19, "POVASA084-B", "Camiseta Poliseda Color Eco Mini", 25, 42.20, 2),
            Producto(20, "POVASA010-B", "Camiseta Poliseda Color Eco Chica", 25, 39.86, 3),
            Producto(21, "POVASA011-B", "Camiseta Poliseda Color Eco Mediana", 25, 39.86, 3),
            Producto(22, "POVASA012-B", "Camiseta Poliseda Color Eco Grande", 25, 39.86, 3),
            Producto(23, "POVASA001-B", "Camiseta Poliseda Natural Premium Mini", 25, 49.08, 4),
            Producto(24, "POVASA002-B", "Camiseta Poliseda Natural Premium Chica", 25, 46.72, 5),
            Producto(25, "POVASA003-B", "Camiseta Poliseda Natural Premium Mediana", 25, 46.72, 5),
            Producto(26, "POVASA004-B", "Camiseta Poliseda Natural Premium Grande", 25, 46.72, 5),
            Producto(27, "POVASA166-B", "Camiseta Poliseda Color Premium Mini", 25, 49.08, 4),
            Producto(28, "POVASA076-B", "Camiseta Poliseda Color Premium Chica", 25, 46.72, 5),
            Producto(29, "POVASA168-B", "Camiseta Poliseda Color Premium Mediana", 25, 46.72, 5),
            Producto(30, "POVASA169-B", "Camiseta Poliseda Color Premium Grande", 25, 46.72, 5),
            Producto(31, "POVASA156-B", "Camiseta Plastica Color Eco Chica", 25, 39.72, 6),
            Producto(32, "POVASA153-B", "Camiseta Plastica Color Eco Mediana", 25, 39.72, 6),
            Producto(33, "POVASA132-B", "Camiseta Plastica Color Eco Grande", 25, 39.72, 6),
            Producto(34, "POVASA077-B", "Camiseta Plastica Negra Eco Chica", 25, 39.72, 6),
            Producto(35, "POVASA026-B", "Camiseta Plastica Negra Eco Mediana", 25, 39.72, 6),
            Producto(36, "POVASA126-B", "Camiseta Plastica Negra Eco Grande", 25, 39.72, 6),
            Producto(37, "POVASA118-B", "Bolsa Plana Negra (Basura) Eco 60x90", 25, 31.73, 12),
            Producto(38, "POVASA119-B", "Bolsa Plana Negra (Basura) Eco 90x120", 25, 31.73, 12),
            Producto(39, "POVASA168-B", "Bolsa Plana Negra (Basura) Eco 70+30x120", 25, 31.73, 12),
            Producto(40, "POVASA141-B", "Bolsa Plana Negra (Basura) Eco 70x90", 25, 31.73, 12),
            Producto(41, "POVASA038-B", "Bolsa en Rollo Plastico Eco 15x25", 25, 40.80, 8),
            Producto(42, "POVASA079-B", "Bolsa en Rollo Plastico Eco 18x26", 25, 40.80, 8),
            Producto(43, "POVASA039-B", "Bolsa en Rollo Plastico Eco 20x30", 25, 40.80, 8),
            Producto(44, "POVASA040-B", "Bolsa en Rollo Plastico Eco 25x35", 25, 40.80, 8),
            Producto(45, "POVASA041-B", "Bolsa en Rollo Plastico Eco 30x40", 25, 40.80, 8),
            Producto(46, "POVASA042-B", "Bolsa en Rollo Plastico Eco 35X45", 25, 40.80, 8),
            Producto(47, "POVASA043-B", "Bolsa en Rollo Plastico Eco 40x60", 25, 40.80, 8),
            Producto(48, "POVASA044-B", "Bolsa en Rollo Plastico Eco 50x70", 25, 40.80, 8),
            Producto(49, "POVASA045-B", "Bolsa en Rollo Plastico Eco 60x90", 25, 40.80, 8),
            Producto(50, "POVASA080-B", "Bolsa en Rollo Plastico Eco 90x120", 25, 40.80, 8),
            /*Producto(51, "POVASA116-B", "Bolsa Plana Plastica Eco 8x12", 25, 48.17),
            Producto(52, "POVASA170-B", "Bolsa Plana Plastica Eco 8x15", 25, 48.17),
            Producto(53, "POVASA172-B", "Bolsa Plana Plastica Eco 8x22", 25, 48.17),
            Producto(54, "POVASA082-B", "Bolsa Plana Plastica Eco 10x15", 25, 48.17),
            Producto(55, "POVASA117-B", "Bolsa Plana Plastica Eco 10x20", 25, 48.17),
            Producto(56, "POVASA027-B", "Bolsa Plana Plastica Eco 10x25", 25, 48.17),
            Producto(57, "POVASA081-B", "Bolsa Plana Plastica Eco 12x20", 25, 48.17),*/
            Producto(58, "POVASA091-B", "Bolsa Plana Plastica Eco 15x20", 25, 40.80, 11),
            Producto(59, "POVASA092-B", "Bolsa Plana Plastica Eco 15x25", 25, 40.80, 11),
            Producto(60, "POVASA028-B", "Bolsa Plana Plastica Eco 18x26", 25, 40.80, 11),
            Producto(61, "POVASA029-B", "Bolsa Plana Plastica Eco 20x30", 25, 40.80, 11),
            Producto(62, "POVASA030-B", "Bolsa Plana Plastica Eco 25x35", 25, 40.80, 11),
            Producto(63, "POVASA031-B", "Bolsa Plana Plastica Eco 30x40", 25, 40.80, 11),
            Producto(64, "POVASA078-B", "Bolsa Plana Plastica Eco 35x45", 25, 40.80, 11),
            Producto(65, "POVASA032-B", "Bolsa Plana Plastica Eco 40x60", 25, 40.80, 11),
            Producto(66, "POVASA033-B", "Bolsa Plana Plastica Eco 50x70", 25, 40.80, 11),
            Producto(67, "POVASA034-B", "Bolsa Plana Plastica Eco 60x90", 25, 40.80, 11),
            Producto(68, "POVASA035-B", "Bolsa Plana Plastica Eco 90x120", 25, 40.80, 11),
            Producto(69, "POVASA063-B", "Bolsa en Rollo Poliseda Bio 15x25", 25, 47.12, 9),
            Producto(70, "POVASA064-B", "Bolsa en Rollo Poliseda Bio 18x26", 25, 47.12, 9),
            Producto(71, "POVASA065-B", "Bolsa en Rollo Poliseda Bio 20x30", 25, 47.12, 9),
            Producto(72, "POVASA066-B", "Bolsa en Rollo Poliseda Bio 25x35", 25, 47.12, 9),
            Producto(73, "POVASA067-B", "Bolsa en Rollo Poliseda Bio 30x40", 25, 47.12, 9),
            Producto(74, "POVASA068-B", "Bolsa en Rollo Poliseda Bio 35x45", 25, 47.12, 9),
            Producto(75, "POVASA069-B", "Bolsa en Rollo Poliseda Bio 40x60", 25, 47.12, 9),
            Producto(76, "POVASA143-B", "Bolsa en Rollo Poliseda Bio 50x70", 25, 47.12, 9),
            Producto(77, "POVASA151-B", "Bolsa en Rollo Poliseda Bio 60x90", 25, 47.12, 9),
            Producto(78, "POVASA115-B", "Bolsa en Rollo Poliseda Bio 90x120", 25, 47.12, 9),
            Producto(79, "POVASA101-B", "Bolsa en Rollo Polipapel Eco 15x25", 25, 41.81, 7),
            Producto(80, "POVASA120-B", "Bolsa en Rollo Polipapel Eco 18x26", 25, 41.81, 7),
            Producto(81, "POVASA099-B", "Bolsa en Rollo Polipapel Eco 20x30", 25, 41.81, 7),
            Producto(82, "POVASA121-B", "Bolsa en Rollo Polipapel Eco 25x35", 25, 41.81, 7),
            Producto(83, "POVASA103-B", "Bolsa en Rollo Polipapel Eco 30x40", 25, 41.81, 7),
            Producto(84, "POVASA104-B", "Bolsa en Rollo Polipapel Eco 35x45", 25, 41.81, 7),
            Producto(85, "POVASA105-B", "Bolsa en Rollo Polipapel Eco 40x60", 25, 41.81, 7),
            Producto(86, "POVASA131-B", "Bolsa en Rollo Polipapel Eco 50x70", 25, 41.81, 7),
            Producto(87, "POVASA162-B", "Bolsa en Rollo Polipapel Eco 60x90", 25, 41.81, 7),
            Producto(88, "POVASA163-B", "Bolsa en Rollo Polipapel Eco 90x120", 25, 41.81, 7),
            /*Producto(89, "POVASA096-B", "Bolsa Plana Polipapel Cortado 20x30", 25, 44.65),
            Producto(90, "POVASA097-B", "Bolsa Plana Polipapel Cortado 25x35", 25, 44.65),*/
            Producto(91, "POVASA073-C", "Lamina Polipapel Eco (Caja con 5 kgs) 25x35", 5, 194.46, 10),
            Producto(92, "POVASA074-C", "Lamina Polipapel Eco (Caja con 5 kgs) 30x40", 5, 194.46, 10),
            Producto(93, "BIO001-C", "Vaso Plastico #8 Biotermo 20/50", 0, 325.00, 14),
            Producto(94, "BIO002-C", "Envase 1 Litro Biotermo 20/25", 0, 810.00, 18),
            Producto(95, "BIO003-C", "Envase 1/2 Litro Biotermo 20/25", 0, 585.00, 17),
            Producto(96, "BIO004-C", "Vaso Plastico #5.5 Biotermo 20/50", 0, 492.20, 13),
            Producto(97, "BIO004-C", "Vaso Plastico #12 Biotermo 20/50", 0, 492.20, 15),
            Producto(98, "BIO005-C", "Vaso Plastico #14 Biotermo 20/50", 0, 503.70, 16),
            Producto(99, "PHOENIX015-C", "Tapa Phoenix Hermetica para Envase 20/25", 0, 427.36, 19),
            Producto(100, "", "Cuchara Nevera Blanca Foriba", 0, 294.00, 20),
            Producto(101, "", "Cuchara Nevera Color Foriba", 0, 294.00, 20),
            Producto(102, "", "Cuchara Chica Foriba", 0, 426.00, 21),
            Producto(103, "", "Cuchara Mediana Foriba", 0, 259.20, 22),
            Producto(104, "", "Cuchara Grande Foriba", 0, 318.00, 23),
            Producto(105, "", "Tenedor Chico Foriba", 0, 306.00, 24),
            Producto(106, "", "Tenedor Mediano Foriba", 0, 258.00, 25),
            Producto(107, "", "Tenedor Grande Foriba", 0, 318.00, 26),
            Producto(108, "", "Cuchillo Grande Foriba", 0, 366.00, 27),
            Producto(109, "", "Charola Térmica #066 Jaguar", 0, 113.40, 28),
            Producto(110, "", "Charola Térmica #855 Jaguar", 0, 180.06, 29),
            Producto(111, "", "Charola Térmica #9D Jaguar", 0, 350.98, 30),
            Producto(112, "", "Plato Térmico #PH6 Jaguar", 0, 214.45, 31),
            Producto(113, "", "Plato Térmico #PH8 Hondo", 0, 338.08, 32),
            Producto(114, "", "Plato Térmico #8 Liso Jaguar 25/20", 0, 357.43, 33),
            Producto(115, "", "Plato Térmico #10 1/4 C/DIV Jaguar 25/20", 0, 391.82, 34),
            Producto(116, "", "Contenedor Térmico Hot Dog Jaguar 4/125", 0, 499.32, 35),
            Producto(117, "", "Contenedor Térmico #6x6 Hamburguesero", 0, 325.17, 36),
            Producto(118, "", "Contenedor Térmico #8X8 (Liso/División)", 0, 311.20, 37),
            Producto(119, "", "Contenedor Térmico #9X9 (Liso/División)", 0, 348.72, 38),
            Producto(120, "", "Vaso Dart 4oz 4J6 Blanco 40/25", 0, 445.50, 39),
            Producto(121, "", "Vaso Dart 6oz 6J6 Blanco 40/25", 0, 357.50, 40),
            Producto(122, "", "Vaso Dart 8oz 8JY8 Grabado Relieve 40/25", 0, 397.10, 41),
            Producto(123, "", "Vaso Dart 10oz 10JY10 Grabado Relieve 40/25", 0, 446.60, 42),
            Producto(124, "", "Vaso Dart 12oz 12J12 Blanco 40/25", 0, 564.31, 43),
            Producto(125, "", "Vaso Dart 14oz 14J16 Blanco 50/20", 0, 699.60, 44),
            Producto(126, "", "Vaso Dart 16oz 16J165 Blanco 25/20", 0, 369.60, 45),
            Producto(127, "", "Envase Dart 16oz 16MJ32 BL T/Tazon 20/25", 0, 443.31, 46),
            Producto(128, "", "Envase Dart 32oz 32J32 Blanco 20/15", 0, 433.40, 47),
            Producto(129, "", "Tapa Dart 32SL Traslúcida Con Ranura Pop 10/100", 0, 750.20, 48),
            Producto(130, "", "Tapa Dart 16SL Translúcida Con Ranura Pop 10/100", 0, 492.80, 49),
            Producto(131, "", "Vaso Plástico Reyma #4CH 20/50", 0, 446.47, 50),
            Producto(132, "", "Vaso Plástico Reyma #8 20/50", 0, 396.17, 51),
            Producto(133, "", "Vaso Plástico #8 Jaguar 20/50", 0, 373.55, 52),
            Producto(134, "", "Vaso Plástico #16 Jaguar Largo 40/25", 0, 976.41, 53),
            Producto(135, "", "Vaso Plástico #16 Jaguar Barril 40/25", 0, 783.32, 54),
            Producto(136, "", "Servilleta Marli", 0, 383.99, 55),
            Producto(137, "", "Otros", 0, 0.0, -1)
        )
        return products
    }
}