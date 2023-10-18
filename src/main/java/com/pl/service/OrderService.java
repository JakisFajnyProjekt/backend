package com.pl.service;

import com.pl.exception.InvalidValuesException;
import com.pl.exception.NotFoundException;
import com.pl.mapper.OrderMapper;
import com.pl.model.Order;
import org.slf4j.Logger;
import com.pl.model.dto.OrderDTO;
import com.pl.repository.OrderRepository;
import com.pl.repository.RestaurantRepository;
import com.pl.repository.UserRepository;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, RestaurantRepository restaurantRepository, UserRepository userRepository, Logger logger) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }


    public OrderDTO createOrder(Map<String, Object> update) {
        if (update.containsKey("price") && update.containsKey("isCompleted") && update.containsKey("restaurantId") && update.containsKey("userId")) {
            try {
                Order newOrder = new Order();
                newOrder.setPrice(new BigDecimal(update.get("price").toString()));
                newOrder.setIsCompleted(Boolean.parseBoolean(update.get("isCompleted").toString()));
                newOrder.setRestaurant(
                            restaurantRepository
                                .findById( (Long) update.get("restaurantId") )
                                .orElseThrow(() -> new NotFoundException("Restaurant not found"))
                );
                newOrder.setUser(
                        userRepository
                                .findById( (Long) update.get("userId") )
                                .orElseThrow(() -> new NotFoundException("user not found")));
                return orderMapper.mapToOrderDto(orderRepository.save(newOrder));
            } catch (Exception e) {
                throw new InvalidValuesException("Provided values does not contain name and description properties");
            }
        } else {
            throw new InvalidValuesException("Missing keys in provided object");
        }
    }

    public List<OrderDTO> listOrders() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            LOGGER.info("the users list are empty");
            return new ArrayList<>();
        }
        return orderMapper.mapToListDto(orders);
    }
}
