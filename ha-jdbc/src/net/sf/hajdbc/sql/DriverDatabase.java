/*
 * HA-JDBC: High-Availability JDBC
 * Copyright (c) 2004-2006 Paul Ferraro
 * 
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by the 
 * Free Software Foundation; either version 2.1 of the License, or (at your 
 * option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Contact: ferraro@users.sourceforge.net
 */
package net.sf.hajdbc.sql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Properties;

import net.sf.hajdbc.Messages;
import net.sf.hajdbc.SQLException;

/**
 * @author  Paul Ferraro
 * @version $Revision$
 * @since   1.0
 */
public class DriverDatabase extends AbstractDatabase<Driver> implements DriverDatabaseMBean
{
	private static final String USER = "user";
	private static final String PASSWORD = "password";
	
	private String url;
	private String driver;
	
	/**
	 * @see net.sf.hajdbc.sql.DriverDatabaseMBean#getUrl()
	 */
	public String getUrl()
	{
		return this.url;
	}
	
	/**
	 * Set the url for this database
	 * @param url a database url
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}
	
	/**
	 * @see net.sf.hajdbc.sql.DriverDatabaseMBean#getDriver()
	 */
	public String getDriver()
	{
		return this.driver;
	}
	
	/**
	 * Sets the driver class name for this database.
	 * @param driver a driver class name
	 */
	public void setDriver(String driver)
	{
		this.driver = driver;
	}
	
	/**
	 * @param driver a JDBC driver
	 * @return a database connection
	 * @throws java.sql.SQLException if a database connection could not be made
	 * @see net.sf.hajdbc.Database#connect(Object)
	 */
	public Connection connect(Driver driver) throws java.sql.SQLException
	{
		Properties properties = new Properties(this.getProperties());
		
		if (this.user != null)
		{
			properties.setProperty(USER, this.user);
		}

		if (this.password != null)
		{
			properties.setProperty(PASSWORD, this.password);
		}
		
		return driver.connect(this.url, properties);
	}

	/**
	 * @see net.sf.hajdbc.Database#createConnectionFactory()
	 */
	public Driver createConnectionFactory() throws java.sql.SQLException
	{
		if (this.driver != null)
		{
			try
			{
				Class driverClass = Class.forName(this.driver);
				
				if (!Driver.class.isAssignableFrom(driverClass))
				{
					throw new SQLException(Messages.getMessage(Messages.NOT_INSTANCE_OF, this.driver, Driver.class.getName()));
				}
			}
			catch (ClassNotFoundException e)
			{
				throw new SQLException(Messages.getMessage(Messages.DRIVER_NOT_FOUND, this.driver), e);
			}
		}
		
		try
		{
			return DriverManager.getDriver(this.url);
		}
		catch (java.sql.SQLException e)
		{
			throw new SQLException(Messages.getMessage(Messages.JDBC_URL_REJECTED, this.url), e);
		}
	}
}
