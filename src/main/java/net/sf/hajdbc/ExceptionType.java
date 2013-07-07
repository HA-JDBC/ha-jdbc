/*
 * HA-JDBC: High-Availability JDBC
 * Copyright (C) 2012  Paul Ferraro
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.hajdbc;

import java.io.IOException;
import java.sql.SQLException;

import javax.transaction.xa.XAException;

import net.sf.hajdbc.util.Matcher;
import net.sf.hajdbc.util.ServiceLoaders;

/**
 * @author Paul Ferraro
 */
@SuppressWarnings("rawtypes")
public enum ExceptionType implements Matcher<ExceptionFactory>
{
	SQL(SQLException.class),
	XA(XAException.class),
	IO(IOException.class)
	;
	private final Class<? extends Exception> exceptionClass;
	private volatile ExceptionFactory exceptionFactory;  
	
	private <E extends Exception> ExceptionType(final Class<E> exceptionClass)
	{
		this.exceptionClass = exceptionClass;
	}
	
	@Override
	public boolean matches(ExceptionFactory factory)
	{
		return this == factory.getType();
	}

	@SuppressWarnings("unchecked")
	public <E extends Exception> ExceptionFactory<E> getExceptionFactory()
	{
		return ServiceLoaders.findRequiredService(this, ExceptionFactory.class);
	}
	
	@SuppressWarnings( "unchecked" )
	public static <E extends Exception> ExceptionFactory<E> getExceptionFactory(Class<E> exceptionClass)
	{
		for (ExceptionType type: ExceptionType.values())
		{
			if (type.exceptionClass.equals(exceptionClass))
			{
				if ( type.exceptionFactory == null ) {
					return type.exceptionFactory = type.getExceptionFactory();
				} else {
					return type.exceptionFactory;
				}
			}
		}
		
		throw new IllegalArgumentException(exceptionClass.getName());
	}
}