package com.guardians.cmpe272.assignment.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.EnumSet;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

import com.guardians.cmpe272.assignment.model.Book;
import com.guardians.cmpe272.assignment.model.BookOrder;
import com.guardians.cmpe272.assignment.model.Customer;
import com.guardians.cmpe272.assignment.model.Inventory;
import com.guardians.cmpe272.assignment.model.OrderLine;

public class HibernateSchemaExport {
	private HibernateSchemaExport() {

		super();
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 * @throws FileNotFoundException
	 *             file not found
	 */
	public static void main(final String[] args) throws FileNotFoundException {

		final StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
		ssrb.applySetting("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");

		final MetadataSources cfg = new MetadataSources(ssrb.build());
		
		cfg.addAnnotatedClass(Book.class);
		cfg.addAnnotatedClass(Inventory.class);
		cfg.addAnnotatedClass(Customer.class);
		cfg.addAnnotatedClass(BookOrder.class);
		cfg.addAnnotatedClass(OrderLine.class);
		
		final PrintWriter writer = new PrintWriter("src/test/resources/hibernate-schema.sql");
		writer.close();

		final MetadataBuilder metadataBuilder = cfg.getMetadataBuilder();
		metadataBuilder.applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE);

		final EnumSet<TargetType> targetTypes = EnumSet.of(TargetType.SCRIPT);
		final SchemaExport schemaExport = new SchemaExport();
		schemaExport.setDelimiter(";");
		schemaExport.setOutputFile("src/test/resources/hibernate-schema.sql").setDelimiter(";");

		schemaExport.create(targetTypes, metadataBuilder.build());
	}
}
