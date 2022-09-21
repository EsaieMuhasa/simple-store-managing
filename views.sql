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
		CommandItem.unitPrice AS unitPrice,
		CommandItem.currency AS currency,
		(SELECT 
			Stock.measureUnit FROM Stock WHERE Stock.product = CommandItem.product LIMIT 1
		) AS measureUnit
	FROM CommandItem;

DROP VIEW IF EXISTS V_PaymentPart;
CREATE VIEW V_PaymentPart AS 
	SELECT 
		CommandPayment.id AS id,
		CommandPayment.config AS config,
		CommandPayment.currency AS currency,
		CommandPayment.command AS payment,
		DistributionConfigItem.id AS itemPart,
		DistributionConfigItem.rubric AS rubric,
		CommandPayment.recordingDate AS recordingDate,
		CommandPayment.lastUpdateDate AS lastUpdateDate,
		CommandPayment.amount AS amount,
		CommandPayment.date AS date,
		DistributionConfigItem.percent AS percent,
		(SELECT (CommandPayment.amount / 100.0) * DistributionConfigItem.percent) AS part
	FROM CommandPayment LEFT JOIN DistributionConfigItem ON CommandPayment.config = DistributionConfigItem.owner;

DROP VIEW IF EXISTS V_DistributionConfigItem;
--CREATE VIEW V_DistributionConfigItem AS
--	SELECT DISTINCT  
--		DistributionConfigItem.id AS id,
--		DistributionConfigItem.recordingDate AS recordingDate,
--		DistributionConfigItem.lastUpdateDate AS lastUpdateDate,
--		DistributionConfigItem.percent AS percent,
--		DistributionConfigItem.owner AS owner,
--		DistributionConfigItem.rubric AS rubric,
--		(SELECT 
--			(SUM(CommandItem.unitPrice * CommandItem.quantity ) / 100.0) * DistributionConfigItem.percent AS percentAmount
--			FROM CommandItem WHERE CommandItem.config = DistributionConfigItem.owner
--		) AS realizedAmount
--	FROM DistributionConfigItem LEFT JOIN CommandItem ON DistributionConfigItem.owner = CommandItem.config ORDER BY id;

DROP VIEW IF EXISTS V_BudgetRubric;-- mis en commentaire de la vue car, le gestion de plusieur devise pose proble dans la requette selection
--CREATE VIEW V_BudgetRubric AS 
--	SELECT 
--		BudgetRubric.id AS id,
--		BudgetRubric.recordingDate AS recordingDate,
--		BudgetRubric.lastUpdateDate AS lastUpdateDate,
--		BudgetRubric.label AS label,
--		BudgetRubric.description AS description,
--		(SELECT 
--			SUM(V_CommandPaymentPart.part) sumAmount FROM V_CommandPaymentPart 
--			WHERE V_CommandPaymentPart.rubric = BudgetRubric.id
--		) AS totalPayment
--	FROM BudgetRubric;
		