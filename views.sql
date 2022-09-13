DROP VIEW IF EXISTS V_Stock;
CREATE VIEW V_Stock AS
	SELECT 
		Stock.id AS id,
		Stock.recordingDate AS recordingDate,
		Stock.lastUpdateDate AS lastUpdateDate,
		Stock.product AS product,
		Stock.quantity AS quantity,
		Stock.measureUnit AS measureUnit,
		Stock."date" AS "date",
		Stock.defaultUnitPrice AS defaultUnitPrice,
		Stock.salesCurrency AS salesCurrency,
		Stock.buyingPrice AS buyingPrice,
		Stock.buyingCurrency AS buyingCurrency,
		Stock.manifacturingDate  AS manifacturingDate,
		Stock.expiryDate AS expiryDate,
		Stock.description  AS description,
		(SELECT 
			SUM(AffectedStock.quantity)
				FROM AffectedStock WHERE AffectedStock.stock = Stock.id
		) AS soldQuantity
	FROM Stock;

DROP VIEW IF EXISTS V_CommandItem;
CREATE VIEW V_CommandItem AS 
	SELECT 
		CommandItem.id AS id,
		CommandItem.recordingDate AS recordingDate,
		CommandItem.lastUpdateDate AS lastUpdateDate,
		CommandItem.quantity AS quantity,
		CommandItem.command AS command,
		CommandItem.product AS product,
		CommandItem.config AS config,
		CommandItem.unitPrice AS unitPrice,
		CommandItem.currency AS currency,
		(SELECT 
			Stock.measureUnit FROM Stock WHERE Stock.product = CommandItem.product LIMIT 1
		) AS measureUnit
	FROM CommandItem;
		