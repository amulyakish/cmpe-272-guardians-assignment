package com.guardians.cmpe272.assignment.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.guardians.cmpe272.assignment.json.BookOrderJson;
import com.guardians.cmpe272.assignment.model.BookOrder;
import com.guardians.cmpe272.assignment.model.OrderLine;
import com.guardians.cmpe272.assignment.model.Status;
import com.guardians.cmpe272.assignment.repository.BookOrderRepository;


@RestController
@CrossOrigin
@RequestMapping("/bookorder")
public class BookOrderController {
	
	@Autowired
	BookOrderRepository orderRepository;

	// Create a new Order
	@PostMapping(path="/order", consumes = "application/json")
	public String createOrder(@RequestBody BookOrder order) {	
		for(OrderLine line : order.getOrderLines()) {
			line.associateToOrder(order);
		}
		BookOrder createdOrder = orderRepository.save(order);
		String response = "{\"orderId\":" + createdOrder.getOrderId().toString() + "}";
	    return response;
	}
	
	@GetMapping("/allOrders")
	public List<BookOrderJson> getAllOrders() {
		return orderRepository.getAllOrderIds();
	}
	
	@GetMapping("/orders/{id}")
	public BookOrderJson viewOrder(@PathVariable(value = "id") Long orderId) {
		Optional<BookOrder> order = this.orderRepository.findById(orderId);
		BookOrderJson json = new BookOrderJson(order.get());
		return json;
	}
	
	// Fulfill Order
	@PutMapping(path="/fulfill/{orderId}", consumes = "application/json")
	public Status fulfillOrder(@PathVariable Long orderId) {	
		String response;
		Status status = orderRepository.fulfillOrder(orderId);
		if(status.getOrderFulfillStatus() == 1) {
			response = "{\"Success\": \"True\"}";
		}
		else {
			response = "{\"Success\": \"False\", \"Error\" :" + status.getError() + "}";
		}
		System.out.println(response);
	    return status;
	}
}
