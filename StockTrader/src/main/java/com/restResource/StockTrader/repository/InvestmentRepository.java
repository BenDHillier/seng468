package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.Investment;
import com.restResource.StockTrader.entity.InvestmentId;
import org.springframework.data.repository.CrudRepository;

public interface InvestmentRepository extends CrudRepository<Investment, InvestmentId> {
}
