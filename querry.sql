--SELECT DISTINCT  
--		DistributionConfigItem.id AS id,
--		DistributionConfigItem.recordingDate AS recordingDate,
--		DistributionConfigItem.lastUpdateDate AS lastUpdateDate,
--		DistributionConfigItem.percent AS percent,
--		DistributionConfigItem.owner AS owner,
--		(SELECT 
--			(SUM(CommandItem.unitPrice * CommandItem.quantity ) / 100.0) * DistributionConfigItem.percent AS percent
--			FROM CommandItem WHERE CommandItem.config = DistributionConfigItem.owner
--		) AS realizedAmount
--	FROM DistributionConfigItem LEFT JOIN CommandItem ON DistributionConfigItem.owner = CommandItem.config ORDER BY id;
	
--SELECT DISTINCT  
--		BudgetRubric.id AS id,
--		BudgetRubric.recordingDate AS recordingDate,
--		BudgetRubric.lastUpdateDate AS lastUpdateDate,
--		BudgetRubric.label AS label,
--		BudgetRubric.description AS description,
--		(SELECT 
--			SUM(V_DistributionConfigItem.realizedAmount) sumAmount FROM V_DistributionConfigItem 
--			WHERE V_DistributionConfigItem.rubric = BudgetRubric.id
--		) AS totalPayment
--	FROM BudgetRubric INNER JOIN V_DistributionConfigItem ON V_DistributionConfigItem.rubric = BudgetRubric.id;


	SELECT
		CommandItem.id AS id,
		CommandItem.recordingDate AS recordingDate,
		CommandItem.lastUpdateDate AS lastUpdateDate,
		CommandItem.quantity AS quantity,
		CommandItem.product AS product,
		CommandItem.config AS config,
		CommandItem.currency AS currency,
		(SELECT 
			((CommandItem.unitPrice * CommandItem.quantity ) / 100.0) * DistributionConfigItem.percent AS part
			FROM CommandItem WHERE CommandItem.config = DistributionConfigItem.owner
		) AS amount
	FROM CommandItem INNER JOIN DistributionConfigItem ON DistributionConfigItem.owner = CommandItem.config;
		