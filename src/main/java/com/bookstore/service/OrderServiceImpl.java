package com.bookstore.service;

import com.bookstore.dto.order.OrderItemResponseDto;
import com.bookstore.dto.order.OrderRequestDto;
import com.bookstore.dto.order.OrderResponseDto;
import com.bookstore.dto.order.UpdateOrderStatusRequestDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.exception.OrderProcessingException;
import com.bookstore.mapper.OrderItemMapper;
import com.bookstore.mapper.OrderMapper;
import com.bookstore.model.CartItem;
import com.bookstore.model.Order;
import com.bookstore.model.OrderItem;
import com.bookstore.model.OrderStatus;
import com.bookstore.model.ShoppingCart;
import com.bookstore.model.User;
import com.bookstore.repository.OrderItemRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.ShoppingCartRepository;
import com.bookstore.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderResponseDto placeOrder(OrderRequestDto requestDto) {
        User user = getCurrentUser();
        ShoppingCart cart = getShoppingCartForUser(user);
        validateCartNotEmpty(cart);

        Order order = buildOrderFromCart(user, cart, requestDto.getShippingAddress());
        orderRepository.save(order);
        clearCart(cart);

        return orderMapper.toDto(order);
    }

    @Override
    public Page<OrderResponseDto> getOrderHistory(Pageable pageable) {
        User user = getCurrentUser();
        Page<Order> orders = orderRepository.findAllByUser(user, pageable);
        return orders.map(orderMapper::toDto);

    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId,
                                              UpdateOrderStatusRequestDto requestDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + orderId));
        order.setStatus(requestDto.getStatus());
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public List<OrderItemResponseDto> getAllByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + orderId));

        List<OrderItem> items = orderItemRepository.findAllByOrder(order);

        return items.stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderItemResponseDto getByOrderIdAndItemId(Long orderId, Long itemId) {
        OrderItem item = orderItemRepository.findByIdAndOrderId(itemId, orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "OrderItem not found with id: " + itemId
                                + " for order id: " + orderId));
        return orderItemMapper.toDto(item);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with email: " + email));
    }

    private ShoppingCart getShoppingCartForUser(User user) {
        return shoppingCartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart with user id " + user.getId() + " not found"));
    }

    private void validateCartNotEmpty(ShoppingCart cart) {
        if (cart.getCartItems().isEmpty()) {
            throw new OrderProcessingException(
                    "Shopping cart with id: " + cart.getId() + " is empty");
        }
    }

    private Order buildOrderFromCart(User user,
                                     ShoppingCart cart, String shippingAddress) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItem.setOrder(order);

            order.getOrderItems().add(orderItem);

            total = total.add(orderItem.getPrice()
                    .multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }

        order.setTotal(total);
        return order;
    }

    private void clearCart(ShoppingCart cart) {
        cart.getCartItems().clear();
    }
}
