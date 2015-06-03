/*
 *  Copyright 2010, 2011, 2012 Christopher Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.usertype.moneyandcurrency.moneta.columnmapper;

import java.math.BigDecimal;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.jadira.usertype.moneyandcurrency.monetabp.util.CurrencyUnitConfigured;
import org.jadira.usertype.spi.shared.AbstractBigDecimalColumnMapper;
import org.javamoney.moneta.FastMoney;

public class BigDecimalColumnFastMoneyMapper extends AbstractBigDecimalColumnMapper<MonetaryAmount> implements CurrencyUnitConfigured {

    private static final long serialVersionUID = 4205713919952452881L;
	
    private CurrencyUnit currencyUnit;

    @Override
    public FastMoney fromNonNullValue(BigDecimal val) {
        return FastMoney.of(val, currencyUnit);
    }

    @Override
    public BigDecimal toNonNullValue(MonetaryAmount value) {
    	if (!currencyUnit.equals(value.getCurrency())) {
    		throw new IllegalStateException("Expected currency " + currencyUnit.getCurrencyCode() + " but was " + value.getCurrency());
    	}
    	return value.getNumber().numberValue(BigDecimal.class);
    }

	@Override
	public FastMoney fromNonNullString(String s) {
		
		int separator = s.indexOf(' ');
		
		String currency = s.substring(0, separator);
		String value = s.substring(separator + 1);
		
		return FastMoney.of(Long.parseLong(value), Monetary.getCurrency(currency));
	}

	@Override
	public String toNonNullString(MonetaryAmount value) {
		return value.toString();
	}
	
	@Override
    public void setCurrencyUnit(CurrencyUnit currencyUnit) {
        this.currencyUnit = currencyUnit;
    }
}
