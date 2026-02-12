package com.canteen.OrderingComponent;

import java.util.Optional;


public interface CustomerRepository {
    Optional<Customer> findById(long id);
}