package com.guardians.cmpe272.assignment.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import com.guardians.cmpe272.assignment.json.BookOrderJson;
import com.guardians.cmpe272.assignment.model.Book;
import com.guardians.cmpe272.assignment.model.BookOrder;
import com.guardians.cmpe272.assignment.model.Customer;
import com.guardians.cmpe272.assignment.model.Inventory;
import com.guardians.cmpe272.assignment.model.OrderLine;
import com.guardians.cmpe272.assignment.model.Pair;
import com.guardians.cmpe272.assignment.model.Status;
import com.guardians.cmpe272.assignment.model.Status.Error;

public class BookOrderRepositoryImpl implements BookOrderRepositoryCustom{

	@Autowired
	InventoryRepository inventoryRepository;
	
	@Autowired
	BookOrderRepository orderRepository;
	
	@Override
	@Transactional
	public Long createOrder(Customer customer, List<Pair> orderDetails) {
		float orderTotal = 0;
		
		BookOrder order = new BookOrder(customer, 0, new Date());
		for(Pair pair: orderDetails) {
	        Book book = pair.getBook();
	        Integer copies = pair.getCopies();
	        
	        OrderLine orderLine = new OrderLine(book, copies, book.getPrice()*copies);
	        orderTotal = orderTotal + (book.getPrice()*copies);
	        orderLine.associateToOrder(order);
		}
		order.setOrderTotal(orderTotal);
		
		orderRepository.save(order);
		
		return order.getOrderId();
	}

	@Override
	@Transactional
	public Status fulfillOrder(Long orderId) {
		Optional<BookOrder> order = orderRepository.findById(orderId);
		//orderRepository.findById(orderId);
		Status status = new Status();
		if((order.get().getIsFulfilled() == null || order.get().getIsFulfilled() == Boolean.FALSE) && validateOrder(order.get()) == 1) {
			for (OrderLine line : order.get().getOrderLines()) {
				List<Inventory> inventory = inventoryRepository.findByBookId(line.getBook().getBookId());
				inventory.get(0).setNoOfCopies(inventory.get(0).getNoOfCopies() - line.getQuantity());
				inventoryRepository.save(inventory.get(0));
			}				
			order.get().setIsFulfilled(Boolean.TRUE);
			orderRepository.save(order.get());
			status.setOrderFulfillStatus(1);
			status.setError(Error.SUCCESS);
		}
		else if(order.get().getIsFulfilled() != null && order.get().getIsFulfilled() == Boolean.TRUE) {
			status.setOrderFulfillStatus(0);
			status.setError(Error.ORDER_ALREADY_FULFILLED);
		}
		else if(validateOrder(order.get()) == 0){
			status.setOrderFulfillStatus(0);
			status.setError(Error.OUT_OF_STOCK);
		}
		else {
			status.setOrderFulfillStatus(0);
			status.setError(Error.UNKNOWN_ERROR);
		}	
		
		return status;
	}
	
	private int validateOrder(BookOrder order) {
		Set<OrderLine> orderLines = order.getOrderLines();
		for (OrderLine line : orderLines) {
			//Inventory inventory = inventoryRepository.findByBook(line.getBook());
			Inventory inventory = inventoryRepository.findByBookId(line.getBook().getBookId()).get(0);
			if(line.getQuantity() <= inventory.getNoOfCopies()) {
				continue;
			}
			else {
				return 0;
			}
		}		
		return 1;
	}

	@Override
	public List<BookOrderJson> getAllOrderIds() {
		List<BookOrderJson> orderIdList = new ArrayList<>();
		List<BookOrder> orders = orderRepository.findAll();
		for(BookOrder order: orders) {
			BookOrderJson json = new BookOrderJson(order);
			orderIdList.add(json);
		}
		return orderIdList;
	}	
}
