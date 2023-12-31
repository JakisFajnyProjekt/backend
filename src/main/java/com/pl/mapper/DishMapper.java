package com.pl.mapper;

import com.pl.model.Dish;
import com.pl.model.Restaurant;
import com.pl.model.dto.DishCreateDTO;
import com.pl.model.dto.DishDTO;
import com.pl.repository.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DishMapper {

    private final RestaurantRepository restaurantRepository;

    public DishMapper(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public Dish mapToDish(DishCreateDTO dishDto) {
        Dish dish = new Dish();
        dish.setName(dishDto.name());
        dish.setDescription(dishDto.description());
        dish.setPrice(dishDto.price());
        Optional<Restaurant> restaurant = restaurantRepository.findById(dishDto.restaurantId());
        restaurant.ifPresent(dish::setRestaurant);
        dish.setCategory(dishDto.category());
        return dish;
    }

    public DishDTO mapToDishDto(Dish dish) {
        return new DishDTO(
                dish.getId(),
                dish.getName(),
                dish.getDescription(),
                dish.getPrice(),
                dish.getRestaurant().getId(),
                dish.getCategory());
    }

    public List<DishDTO> mapToListDto(final List<Dish> dishes) {
        return dishes.stream()
                .map(this::mapToDishDto)
                .toList();
    }
}
