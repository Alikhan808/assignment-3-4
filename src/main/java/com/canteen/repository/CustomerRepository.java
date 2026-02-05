package com.canteen.repository;

import com.canteen.domain.Customer;
import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findById(long id);
}