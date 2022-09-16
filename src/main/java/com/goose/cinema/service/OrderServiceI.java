package com.goose.cinema.service;

import com.goose.cinema.dto.OrderDto;
import com.goose.cinema.exception.CinemaException;
import com.goose.cinema.exception.ErrorType;
import com.goose.cinema.mapper.MapStructMapper;
import com.goose.cinema.persistance.Order;
import com.goose.cinema.repository.MovieRepository;
import com.goose.cinema.repository.OrderRepository;
import com.goose.cinema.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceI implements OrderService{
    private final OrderRepository ordersRepository;
    private final MovieRepository moviesRepository;
    private final MapStructMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public OrderDto findById(Long id) {
        Order byId = ordersRepository.findById(id)
                .orElseThrow(() -> new CinemaException(ErrorType.NOT_FOUND, "Order not found"));

        return mapper.orderToOrderDto(byId);
    }

    @Override
    public Page<OrderDto> searchAll(OrderDto orderDto, Pageable pageable) {
        Page<Order> ordersPage = ordersRepository.findAll(OrderSpecification.orderSpecification(orderDto), pageable);

        return new PageImpl<>(ordersPage.get().map(mapper::orderToOrderDto).toList());
    }

    @Override
    public OrderDto create(OrderDto orderInput) {
        if(Objects.nonNull(orderInput.getMovieId()) && Objects.nonNull(orderInput.getParticipants())) {
            checkExistMovie(orderInput.getMovieId());
            checkParticipants(orderInput.getParticipants());

            Order newOrder = new Order();
            newOrder.setMovieId(orderInput.getMovieId());
            newOrder.setOrderTime(LocalDate.now());
            newOrder.setParticipants(orderInput.getParticipants());

            ordersRepository.save(newOrder);
            return mapper.orderToOrderDto(newOrder);
        }
        else throw new CinemaException(ErrorType.INTERNAL_ERROR, "Missing information");
    }

    @Override
    public OrderDto update(OrderDto orderInput) {
        Order updateOrder = ordersRepository.findById(orderInput.getId())
                .orElseThrow(() -> new CinemaException(ErrorType.NOT_FOUND, "Order not found"));

        if(Objects.nonNull(orderInput.getMovieId())) {
            checkExistMovie(orderInput.getMovieId());
            updateOrder.setMovieId(orderInput.getMovieId());
        }

        if(Objects.nonNull(orderInput.getParticipants())) {
            checkParticipants(orderInput.getParticipants());
            updateOrder.setParticipants(orderInput.getParticipants());
        }

        if(Objects.nonNull(orderInput.getOrderTime())) updateOrder.setOrderTime(orderInput.getOrderTime());

        return mapper.orderToOrderDto(updateOrder);
    }

    @Override
    public void delete(Long id) {
        if (!ordersRepository.existsById(id)) {
            throw new CinemaException(ErrorType.NOT_FOUND, "Order not found");
        }

        ordersRepository.deleteById(id);
    }

    private void checkExistMovie(Long movieId) {
        if(!moviesRepository.existsById(movieId)) {
            throw new CinemaException(ErrorType.NOT_FOUND, "Movie doesn't exists");
        }
    }

    private void checkParticipants(int participants) {
        if(participants <= 0) {
            throw new CinemaException(ErrorType.INTERNAL_ERROR,
                    "The participants number should be more than 0");
        }
    }

}
