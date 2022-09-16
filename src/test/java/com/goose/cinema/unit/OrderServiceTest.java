package com.goose.cinema.unit;

import com.goose.cinema.dto.OrderDto;
import com.goose.cinema.exception.CinemaException;
import com.goose.cinema.exception.ErrorType;
import com.goose.cinema.persistance.Order;
import com.goose.cinema.repository.MovieRepository;
import com.goose.cinema.repository.OrderRepository;
import com.goose.cinema.service.OrderServiceI;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
class OrderServiceTest {
    
    @Autowired
    private OrderServiceI orderService;
    
    @MockBean
    private OrderRepository orderRepository;
    
    @MockBean
    private MovieRepository movieRepository;

    @Test
    void createTest() {
        Order order = createOrder();
        OrderDto orderDto = createOrderDto(order);

        when(orderRepository.save((any(Order.class)))).thenReturn(order);
        when(movieRepository.existsById(orderDto.getMovieId())).thenReturn(true);

        OrderDto result = orderService.create(orderDto);

        verify(orderRepository).save(any(Order.class));
        assertThat(result.getMovieId()).isEqualTo(order.getMovieId());
    }

    @Test
    void createIfFieldsNullTest() {
        OrderDto order = new OrderDto();

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(CinemaException.class)
                .matches((error) -> ((CinemaException) error).getErrorType() == ErrorType.INTERNAL_ERROR);
    }

    @Test
    void findByIdTest() {
        Order order = createOrder();

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        OrderDto result = orderService.findById(order.getId());

        verify(orderRepository).findById(order.getId());
        assertThat(result.getId()).isEqualTo(order.getId());
        assertThat(result.getMovieId()).isEqualTo(order.getMovieId());
    }

    @Test
    void searchOrdersTest() {
        Order order = createOrder();
        OrderDto orderDto = createOrderDto(order);
        PageRequest pageRequest = PageRequest.of(0, 1);

        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(order)));

        Page<OrderDto> page = orderService.searchAll(orderDto, pageRequest);

        verify(orderRepository).findAll(any(Specification.class), any(Pageable.class));

        Optional<OrderDto> first = page.get().findFirst();

        assertThat(first).isPresent();
        assertThat(first.get().getId()).isEqualTo(order.getId());
    }

    @Test
    void updateTest() {
        Order orders = createOrder();
        OrderDto orderDto = createOrderDto(orders);
        Order returnOrder = new Order();

        returnOrder.setId(orders.getId());
        returnOrder.setMovieId(1L);
        returnOrder.setOrderTime(LocalDate.now().minusDays(1));
        returnOrder.setParticipants(111);

        when(orderRepository.findById(orderDto.getId())).thenReturn(Optional.of(returnOrder));
        when(movieRepository.existsById(orderDto.getMovieId())).thenReturn(true);
        when(orderRepository.save((any(Order.class)))).thenReturn(returnOrder);

        OrderDto result = orderService.update(orderDto);

        verify(orderRepository).findById(orderDto.getId());
        assertThat(result.getId()).isEqualTo(orders.getId());
        assertThat(result.getMovieId()).isEqualTo(orders.getMovieId());
        assertThat(result.getOrderTime()).isEqualTo(orders.getOrderTime());
        assertThat(result.getParticipants()).isEqualTo(orders.getParticipants());
    }

    @Test
    void updateIfEmptyTest() {
        Order order = createOrder();
        OrderDto orderDto = createOrderDto(order);

        when(orderRepository.findById(orderDto.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.update(orderDto))
                .isInstanceOf(CinemaException.class)
                .matches((error) -> ((CinemaException) error).getErrorType() == ErrorType.NOT_FOUND);
    }

    @Test
    void updateIfMovieNotExistTest() {
        Order order = createOrder();
        OrderDto orderDto = createOrderDto(order);
        orderDto.setMovieId(1000L);

        when(orderRepository.findById(orderDto.getId())).thenReturn(Optional.of(order));
        when(movieRepository.findById(orderDto.getMovieId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.update(orderDto))
                .isInstanceOf(CinemaException.class)
                .matches((error) -> ((CinemaException) error).getErrorType() == ErrorType.NOT_FOUND);
    }

    @Test
    void updateIfParticipantsLessThanOneTest() {
        Order order = createOrder();
        OrderDto orderDto = createOrderDto(order);
        orderDto.setParticipants(0);

        when(orderRepository.findById(orderDto.getId())).thenReturn(Optional.of(order));
        when(movieRepository.existsById(orderDto.getMovieId())).thenReturn(true);
        assertThatThrownBy(() -> orderService.update(orderDto))
                .isInstanceOf(CinemaException.class)
                .matches((error) -> ((CinemaException) error).getErrorType() == ErrorType.INTERNAL_ERROR);
    }

    @Test
    void deleteTest() {
        Order order = createOrder();

        when(orderRepository.existsById(order.getId())).thenReturn(true);

        orderService.delete(order.getId());

        verify(orderRepository).deleteById(order.getId());
    }

    @Test
    void deleteIfEmptyTest() {
        Order orders = createOrder();

        when(orderRepository.findById(orders.getId())).thenReturn(Optional.empty());

        Long id = orders.getId();

        assertThatThrownBy(() -> orderService.delete(id))
                .isInstanceOf(CinemaException.class)
                .matches((error) -> ((CinemaException) error).getErrorType() == ErrorType.NOT_FOUND);
    }

    private Order createOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setMovieId(1L);
        order.setOrderTime(LocalDate.now());
        order.setParticipants(2);
        return order;
    }

    private OrderDto createOrderDto(Order order) {
        OrderDto result = new OrderDto();
        result.setId(order.getId());
        result.setMovieId((order.getMovieId()));
        result.setOrderTime(order.getOrderTime());
        result.setParticipants(order.getParticipants());
        return result;
    }

}
